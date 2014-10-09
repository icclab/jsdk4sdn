# File:   driver.py
# Author: aepp
#
# Created on 08.10.2014, 14:11
#
# MIT License 2006
#
# Copyright (c) 2014, ICCLab www.cloudcomp.ch
# All rights reserved.
# 
# Permission is hereby granted, free of charge, to any person
# obtaining a copy of this software and associated documentation
# files (SDK4SDN), to deal in the Software without
# restriction, including without limitation the rights to use,
# copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the
# Software is furnished to do so, subject to the following
# conditions:
# 
# The above copyright notice and this permission notice shall be
# included in all copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
# OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
# HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
# WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
# FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
# OTHER DEALINGS IN THE SOFTWARE.

from ryu.base import app_manager
from ryu.controller import ofp_event
from ryu.controller.handler import MAIN_DISPATCHER, CONFIG_DISPATCHER, set_ev_cls
from ryu.lib.packet import packet, ethernet
from ryu.ofproto import ofproto_v1_3
from ryu.ofproto import ofproto_v1_3_parser
import greenlet
import eventlet
# We need the version of zmq that is aware of the eventlet green threads
from eventlet.green import zmq
import simplejson
import re
import logging
import os
import traceback
from ryu.lib import hub
import pprint
import yaml

LOG = logging.getLogger('ryu.app.driver')

class Driver(app_manager.RyuApp):
    
    OFP_VERSIONS = [ofproto_v1_3.OFP_VERSION]

    def __init__(self, *args, **kwargs):      
        super(Driver, self).__init__(*args, **kwargs)
        
        self.publisherCtx = zmq.Context()
        self.publisher = self.publisherCtx.socket(zmq.PUB)
        self.publisher.bind("ipc:///tmp/controller.ipc")

        self.subscriberCtx = zmq.Context()
        self.subscriber = self.subscriberCtx.socket(zmq.SUB)
        self.subscriber.connect("ipc:///tmp/sdk4sdn.ipc")
        self.subscriber.setsockopt(zmq.SUBSCRIBE, 'sdk4sdn')
        # Start the thread to handle events from the SDK4SDN
        self.subscriberThread = hub.spawn(self.sdk4sdn_handler)
        
        self.dpstore = {}
        
    @set_ev_cls(ofp_event.EventOFPSwitchFeatures, CONFIG_DISPATCHER)
    def switch_features_handler(self, ev):
        datapath = ev.msg.datapath
        ofproto = datapath.ofproto
        parser = datapath.ofproto_parser
        self.dpstore[datapath.id] = {"dp_obj": datapath}
        
        match = parser.OFPMatch()
        actions = [parser.OFPActionOutput(ofproto.OFPP_CONTROLLER,
                                          ofproto.OFPCML_NO_BUFFER)]
        self.add_flow(datapath, 0, match, actions)

    @set_ev_cls(ofp_event.EventOFPPacketIn, MAIN_DISPATCHER)
    def handler_packetin(self, ev):
        msg = ev.msg
        datapath = msg.datapath
        ofproto = datapath.ofproto
        ofp_parser = datapath.ofproto_parser
        data = msg.data
        pack = packet.Packet(data)
        eth = pack.get_protocols(ethernet.ethernet)[0]

        dst = eth.dst
        src = eth.src
        dpid = datapath.id

        msg_json = simplejson.dumps(
                {'OFPPacketIn': {'buffer_id': msg.buffer_id, 'match':
                {'OFPMatch': {'oxm_fields': [ {'OXMTlv':
                {'field': "in_port", 'value': msg.match['in_port'] }},
                {'OXMTlv':{'field': "eth_dst", 'value': dst }},
                {'OXMTlv':{'field': "eth_src", 'value': src }},]}},
                'msg_len': msg.total_len, 'data': data.encode('hex'),
                'datapath_id': dpid, 'datapath': dpid}}, indent = 3)
        topic = "controller"
        self.publisher.send_multipart([topic, msg_json])
            
    def add_flow(self, datapath, priority, match, actions):
        ofproto = datapath.ofproto
        parser = datapath.ofproto_parser
    
        inst = [parser.OFPInstructionActions(ofproto.OFPIT_APPLY_ACTIONS,
                                                 actions)]
    
        mod = parser.OFPFlowMod(datapath=datapath, priority=priority,
                                    match=match, instructions=inst)
        datapath.send_msg(mod)


    def sdk4sdn_handler(self):
        while True:
            topic = self.subscriber.recv_string()
            message = self.subscriber.recv_string()
            obj = yaml.load(message)
            if "OFPPacketOut" in obj:
                self._send_packet_out(obj)
            if "OFPFlowMod" in obj:
                self._send_flow_mod(obj)
            
        self.logger.info("sdk4sdn handler died")
        
    def _send_flow_mod(self, obj):
        flow_mod = obj.get("OFPFlowMod")
        datapath = self.dpstore.get(flow_mod.get("datapath_id"))
        datapath = datapath.get("dp_obj")
        instructions = flow_mod.get("instructions")
        
        # FIXME: put this in a instruction parser
        for instruction in instructions:
            if "OFPInstructionActions" in instruction:
                actions = self._parse_action(instruction.get("OFPInstructionActions").get("actions"))
        
        match = flow_mod.get("match")
        match = self._pars_match(match)
        
        inst = [ofproto_v1_3_parser.OFPInstructionActions(ofproto_v1_3.OFPIT_APPLY_ACTIONS,
                                            actions)]

        mod = ofproto_v1_3_parser.OFPFlowMod(datapath=datapath, priority=int(flow_mod.get("priority")),
                                match=match, instructions=inst)

        datapath.send_msg(mod)
        
    def _send_packet_out(self, obj):
        packet_out = obj.get("OFPPacketOut")
        datapath = self.dpstore.get(packet_out.get("datapath_id"))
        datapath = datapath.get("dp_obj")
        actions = self._parse_action( packet_out.get('actions') )
        buffer_id = packet_out.get("buffer_id")
        in_port = int(packet_out.get("in_port"))
        
        data = None
        if buffer_id == ofproto_v1_3.OFP_NO_BUFFER:
            data = packet_out.get("data").decode("hex")
        
        out = ofproto_v1_3_parser.OFPPacketOut(datapath=datapath, buffer_id=buffer_id,
                                in_port=in_port, actions=actions, data=data)
        datapath.send_msg(out)
        
    def _parse_action(self, actions):
        ret = False
        for action in actions:
            if "OFPActionOutput" in action:
                out_port = action.get("OFPActionOutput").get("out_port")
                if out_port == "FLOOD":
                    out_port = ofproto_v1_3.OFPP_FLOOD
                ret = [ofproto_v1_3_parser.OFPActionOutput(int(out_port))]

        return ret
    
    def _pars_match(self, matches):
        ret = False
        fields = matches.get("OFPMatch").get("oxm_fields")
        for field in fields:
            if "OXMTlv" in field:
                field_name = field.get("OXMTlv").get("field")
                field_value = field.get("OXMTlv").get("value")
                ret = eval("ofproto_v1_3_parser.OFPMatch("+field_name+"="+"\""+field_value+"\""+")")
        
        return ret