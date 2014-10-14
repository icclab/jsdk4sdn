/*
* File:   SimpleArp.java
* Author: aepp
*
* Created on 10.10.2014
*
* MIT License 2006
*
* Copyright (c) 2014, ICCLab www.cloudcomp.ch
* All rights reserved.
*
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (SDK4SDN), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
*
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

package sdk4sdn.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ro.fortsoft.pf4j.Extension;
import sdk4sdn.Network;
import sdk4sdn.openflow13.*;

/**
 *
 * @author aepp
 */
@Extension
public class SimpleArp implements OFPEventSwitchFeatures, OFPEventPacketIn {
	
	/**
	 * This is the table, where broadcasts are processed
	 */
	public Integer broadcastTable = 5;
	
	/**
	 * This is the table, where packets are dropped
	 */
	public Integer droppingTable = 6;
	
	/**
	 * This is the broadcast store
	 */
	public HashMap<Number, HashMap<String, String>> broadcastStore = new HashMap<Number, HashMap<String, String>>();

	@Override
	public void switchFeatures(OpenFlow OFPMessage, Network network) {
		//Create a GOTO action to handle ARP broadcast
		OpenFlow message = new OpenFlow();
		
		ArrayList<oxm_fields> fieldsList = new ArrayList<>();
		oxm_fields fields = new oxm_fields();
		OXMTlv fieldEthDst = new OXMTlv();
		fieldEthDst.setField("eth_dst");
		fieldEthDst.setValue("FF:FF:FF:FF:FF:FF");
		fields.setOXMTlv(fieldEthDst);
		fieldsList.add(fields);
		
		OFPInstructionGotoTable goTo = new OFPInstructionGotoTable();
		goTo.setTable_id(this.broadcastTable);
		
		OFPFlowMod flowMod = OFPMessageFactory.CreateFlowModGoTo(goTo, fieldsList);
		flowMod.setDatapath_id(OFPMessage.getOFPSwitchFeatures().getDatapath_id());
		flowMod.setPriority(123);
		message.setOFPFlowMod(flowMod);
		network.Send(message);
		
		//Create a Table Miss for Table 5
		message = new OpenFlow();
		
		actions actions = new actions();
		OFPActionOutput action = OFPMessageFactory.CreateActionOutput("OFPP_CONTROLLER");
		actions.setOFPActionOutput(action);
		
		fieldsList = new ArrayList<>();
		fields = new oxm_fields();
		fieldEthDst = new OXMTlv();
		fieldEthDst.setField("");
		fieldEthDst.setValue("");
		fields.setOXMTlv(fieldEthDst);
		fieldsList.add(fields);

		flowMod = OFPMessageFactory.CreateFlowModAction(actions, fieldsList);
		flowMod.setDatapath_id(OFPMessage.getOFPSwitchFeatures().getDatapath_id());
		flowMod.setTable_id(this.broadcastTable);
		flowMod.setPriority(0);

		message.setOFPFlowMod(flowMod);
		network.Send(message);
	}

	@Override
	public void packetIn(OpenFlow OFPMessage, Network network) {
		String eth_src = "";
		String eth_dst = "";
		String in_port = "";
		OpenFlow message = new OpenFlow();
		ArrayList<oxm_fields> fieldsList = new ArrayList<>();
		
		for ( oxm_fields field : OFPMessage.getOFPPacketIn().getMatch().getOFPMatch().getOxm_fields()) {
			if("eth_src".equals(field.getOXMTlv().getField())) {
				eth_src = field.getOXMTlv().getValue();
			}
			if("eth_dst".equals(field.getOXMTlv().getField())) {
				eth_dst = field.getOXMTlv().getValue();
			}
			if("in_port".equals(field.getOXMTlv().getField())) {
				in_port = field.getOXMTlv().getValue();
			}
		}
		System.out.println(eth_dst);
		if("ff:ff:ff:ff:ff:ff".equals(eth_dst)){
			Number dpid = OFPMessage.getOFPPacketIn().getDatapath_id();
			if(null == this.broadcastStore.get(dpid)) {
				HashMap<String, String> emptyMap = new HashMap<>();
				this.broadcastStore.put(dpid, emptyMap);
			}
			if(this.broadcastStore.get(dpid).get(eth_src) != null) {
				return;
			}
			this.broadcastStore.get(dpid).put(eth_src, eth_src);
			
			oxm_fields oFieldEthSrc = new oxm_fields();
			OXMTlv fieldEthSrc = new OXMTlv();
			fieldEthSrc.setField("eth_src");
			fieldEthSrc.setValue(eth_src);
			oFieldEthSrc.setOXMTlv(fieldEthSrc);
				
			oxm_fields oFieldEthDst = new oxm_fields();
			OXMTlv fieldEthDst = new OXMTlv();
			fieldEthDst.setField("eth_dst");
			fieldEthDst.setValue("ff:ff:ff:ff:ff:ff");
			oFieldEthDst.setOXMTlv(fieldEthDst);
			
			oxm_fields oFieldPort = new oxm_fields();
			OXMTlv fieldPort = new OXMTlv();
			
			for ( ports port : OFPMessage.getOFPPacketIn().getDatapath().getPorts()) {
				if(port.port_no.equals(in_port)) {
					continue;
				}
				message = new OpenFlow();
				fieldsList = new ArrayList<>();
				
				oFieldPort = new oxm_fields();
				fieldPort = new OXMTlv();
				fieldPort.setField("in_port");
				fieldPort.setValue(port.port_no);
				oFieldPort.setOXMTlv(fieldPort);
				
				fieldsList.add(oFieldEthSrc);
				fieldsList.add(oFieldEthDst);
				fieldsList.add(oFieldPort);

				OFPInstructionGotoTable goTo = new OFPInstructionGotoTable();
				goTo.setTable_id(this.droppingTable);

				OFPFlowMod flowMod = OFPMessageFactory.CreateFlowModGoTo(goTo, fieldsList, OFPMessage);
				flowMod.setPriority(200);
				flowMod.setTable_id(this.broadcastTable);
				message.setOFPFlowMod(flowMod);
				network.Send(message);
			}
			
			//Now we can safly flood, without a loop
			message = new OpenFlow();
			OFPPacketOut packetOut = OFPMessageFactory.CreatePacketOut("FLOOD", OFPMessage);
			message.setOFPPacketOut(packetOut);
			network.Send(message);
			
			message = new OpenFlow();
			fieldsList = new ArrayList<>();
			
			actions actions = new actions();
			OFPActionOutput action = OFPMessageFactory.CreateActionOutput("FLOOD");
			actions.setOFPActionOutput(action);
			
			oFieldPort = new oxm_fields();
			fieldPort = new OXMTlv();
			fieldPort.setField("in_port");
			fieldPort.setValue(in_port);
			oFieldPort.setOXMTlv(fieldPort);
			
			fieldsList.add(oFieldEthSrc);
			fieldsList.add(oFieldEthDst);
			fieldsList.add(oFieldPort);
			
			//Since we have the fields and the action, we can create a FlowMod
			OFPFlowMod flowMod = OFPMessageFactory.CreateFlowModAction(actions, fieldsList, OFPMessage);
			flowMod.setTable_id(this.broadcastTable);
			flowMod.setPriority(213);

			//now compile the message
			message.setOFPFlowMod(flowMod);
			network.Send(message);
		}
		
	}
	
}
