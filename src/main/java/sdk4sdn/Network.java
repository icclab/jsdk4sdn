/*
* File:   Network.java
* Author: aepp
*
* Created on 08.10.2014, 14:11
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

package sdk4sdn;

import sdk4sdn.openflow13.OFPEventPacketIn;
import com.google.gson.Gson;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.zeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sdk4sdn.lib.EventLinkEnter;
import sdk4sdn.lib.EventMainDatapath;
import sdk4sdn.lib.EventSwitchEnter;
import sdk4sdn.lib.Topology;
import sdk4sdn.openflow13.*;
/**
 *
 * @author aepp
 */
public class Network {
	
	ZMQ.Context PubContext;
	
	ZMQ.Context SubContext;
	
	ZMQ.Socket Publisher;
			
	ZMQ.Socket Subscriber;
	
	String PubDescriptor;
	
	String SubDescriptor;
	
	List AllExtensions = new ArrayList();
	
	List<OFPEventPacketIn> OFPEventPacketIns;
	
	List<OFPEventSwitchFeatures> OFPEventSwitchFeaturesList;
	
	List<EventLinkEnter> EventLinkEnterList;
	
	List<EventSwitchEnter> EventSwitchEnterList;
	
	public List<EventMainDatapath> EventMainDatapathList;
	
	private static final Logger log = LoggerFactory.getLogger(Network.class);
	
	public Network(String Publisher, String Subscriber){
		this.PubDescriptor = Publisher;
		this.SubDescriptor = Subscriber;
	}
	
	public void CreatePublisher(){
		try {
			this.PubContext = ZMQ.context(1);
			this.Publisher = this.PubContext.socket(ZMQ.PUB);
			this.Publisher.bind("ipc:///tmp/"+this.PubDescriptor+".ipc");
			log.info("Binding publisher to: ipc:///tmp/"+this.PubDescriptor+".ipc");
		}
		catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void CreateSubscriber(){
		try {
			this.SubContext = ZMQ.context(1);
			this.Subscriber = this.SubContext.socket(ZMQ.SUB);
		}
		catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void Connect(){
		this.RunSubscriber();
	}
	
	private void RunSubscriber(){
		//  Subscribe to zipcode, default is NYC, 10001
		this.Subscriber.connect("ipc:///tmp/controller.ipc");
		this.Subscriber.subscribe("controller".getBytes());
		this.Subscriber.subscribe("topology".getBytes());
		log.info("Connecting to: ipc:///tmp/"+this.SubDescriptor+".ipc");
		
		while (!Thread.currentThread ().isInterrupted ()) {
			OpenFlow OFPMessage = new OpenFlow();
			Topology topology = new Topology();
			//FIXME: Do something usefull with the topic
			String topic = this.Subscriber.recvStr(Charset.defaultCharset());
			String msg = this.Subscriber.recvStr(Charset.defaultCharset());
			
			Gson gson = new Gson();
			if("controller".equals(topic)) {
				try {
					OFPMessage = gson.fromJson(msg, OpenFlow.class);
				}
				catch(Exception e) {
					log.error(e.getMessage(), e);
				}
				//FIXME: Put this code somewhere else
				//Here we check for a packet_in and execute all packet_in
				//extensions
				if(OFPMessage.getOFPPacketIn() != null) {
					for (OFPEventPacketIn packetIn : this.OFPEventPacketIns) {
						packetIn.packetIn(OFPMessage, this);
					}
				}
				//Here we check for a switch_feature and execute all switch_feature
				//extensions
				else if(OFPMessage.getOFPSwitchFeatures() != null) {
					for (OFPEventSwitchFeatures switchFeature : this.OFPEventSwitchFeaturesList) {
						switchFeature.switchFeatures(OFPMessage, this);
					}
				}
				//Didn't found an event
				else {
					log.info("Event not implemented");
				}
			}
			else if("topology".equals(topic)) {
				try {
					topology = gson.fromJson(msg, Topology.class);
				}
				catch(Exception e) {
					log.error(e.getMessage(), e);
				}
				
				if(topology.getSrc() != null) {
					for (EventLinkEnter linkEnter : this.EventLinkEnterList) {
						linkEnter.linkEnter(topology, this);
					}
				}
				else if(topology.getPorts() != null) {
					for (EventSwitchEnter switchEnter : this.EventSwitchEnterList) {
						switchEnter.switchEnter(topology, this);
					}
				}
				else {
					log.info("Event not implemented");
				}
			}
		}
	}

	public void Send(OpenFlow OFPMessage) {
		String response = "";
		Gson gson = new Gson();
		
		try {
			response = gson.toJson(OFPMessage);
		}
		catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		
		this.Publisher.sendMore("sdk4sdn");
		this.Publisher.send(response);
	}
	
	public void SetPacketInSubscribers(List<OFPEventPacketIn> OFPEventPacketIns){
		this.OFPEventPacketIns = OFPEventPacketIns;
	}
	
	public void SetSwitchFeaturesSubscribers(List<OFPEventSwitchFeatures> OFPEventSwitchFeatures){
		this.OFPEventSwitchFeaturesList = OFPEventSwitchFeatures;
	}
	
	public void SetLinkEnterSubscribers(List<EventLinkEnter> EventLinkEnterList){
		this.EventLinkEnterList = EventLinkEnterList;
	}
	
	public void SetSwitchEnterSubscribers(List<EventSwitchEnter> EventSwitchEnterList){
		this.EventSwitchEnterList = EventSwitchEnterList;
	}
	
	public void SetMainDatapath(List<EventMainDatapath> EventMainDatapathList){
		this.EventMainDatapathList = EventMainDatapathList;
	}
	
}
