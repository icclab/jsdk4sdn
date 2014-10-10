/*
* File:   L2Switch.java
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

package app;

import java.util.HashMap;
import ro.fortsoft.pf4j.Extension;
import sdk4sdn.Network;
import sdk4sdn.openflow13.*;

/**
 *
 * @author aepp
 */
@Extension
public class L2Switch implements OFPEventPacketIn, OFPEventSwitchFeatures {
	
	HashMap<String, String> ethDstMap = new HashMap<>();
	
	public L2Switch(){
		//FIXME: Currently unused, maybe initialize the map or remove
	}

	@Override
	public void packetIn(OpenFlow OFPMessage, Network network) {
		//FIXME: This API looks like RYU some time ago
		String eth_src = "";
		String eth_dst = "";
		String in_port = "";
		String out_port;
		
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
		ethDstMap.put(eth_src, in_port);
		
		if(ethDstMap.get(eth_dst) != null) {
			//We know the out port
			out_port = ethDstMap.get(eth_dst);
		}
		else {
			//We do not know the out port
			out_port = "FLOOD";
		}
		
		if("FLOOD".equals(out_port)) {
			OpenFlow message = new OpenFlow();
			//Create a packetOut message
			OFPPacketOut packetOut = OFPMessageFactory.CreatePacketOut(out_port, OFPMessage);

			//e.g. Set the in_port to a different value
			//packetOut.setIn_port(in_port);
			
			//now compile the message
			message.setOFPPacketOut(packetOut);
			network.Send(message);
		}
		else {
			OpenFlow message = new OpenFlow();
			//Create an actions object that holds all actions
			actions actions = new actions();
			OFPActionOutput action = OFPMessageFactory.CreateActionOutput(out_port);
			actions.setOFPActionOutput(action);
			
			//Create a match object that holds all matches
			oxm_fields fields = new oxm_fields();
			OXMTlv fieldEthDst = new OXMTlv();
			fieldEthDst.setField("eth_dst");
			fieldEthDst.setValue(eth_dst);
			fields.setOXMTlv(fieldEthDst);
			
			//Since we have the fields and the action, we can create a FlowMod
			OFPFlowMod flowMod = OFPMessageFactory.CreateFlowMod(actions, fields, OFPMessage);

			//now compile the message
			message.setOFPFlowMod(flowMod);
			network.Send(message);
			
			//The packet still needs to be send to the destination
			message = new OpenFlow();
			OFPPacketOut packetOut = OFPMessageFactory.CreatePacketOut(out_port, OFPMessage);
			message.setOFPPacketOut(packetOut);
			network.Send(message);
		}

		
	}

	@Override
	public void switchFeatures(OpenFlow OFPMessage, Network network) {
		//Just to show how it works if your app listns to multiple events
		System.out.println("Found a new device with ID: " + OFPMessage.getOFPSwitchFeatures().getDatapath_id());
	}
}