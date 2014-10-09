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

import java.util.Arrays;
import java.util.HashMap;
import ro.fortsoft.pf4j.Extension;
import sdk4sdn.Network;
import sdk4sdn.openflow13.OFPEventPacketIn;
import sdk4sdn.openflow13.*;

/**
 *
 * @author aepp
 */
@Extension
public class L2Switch implements OFPEventPacketIn {
	
	HashMap<String, String> ethDstMap = new HashMap<>();
	
	public L2Switch(){
		System.out.println("New instance created");
		//FIXME: Currently unused, maybe initialize the map or remove
	}

	@Override
	public void packetIn(OpenFlow OFPMessage, Network network) {
		//FIXME: This API looks like RYU some time ago
		String eth_src = "";
		String eth_dst = "";
		String in_port = "";
		String out_port = "";
		
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
			System.out.println("The out port is: "+out_port);
		}
		else {
			//We do not know the out port
			out_port = "FLOOD";
		}
		
		OpenFlow message = new OpenFlow();
		
		if("FLOOD".equals(out_port)) {
			OFPPacketOut packetOut = new OFPPacketOut();
			actions output = new actions();
			OFPActionOutput OFPOutput = new OFPActionOutput();
			OFPOutput.setOut_port(out_port);
			output.setOFPActionOutput(OFPOutput);
			
			packetOut.setActions(Arrays.asList(output));
			packetOut.setBuffer_id(OFPMessage.getOFPPacketIn().getBuffer_id());
			packetOut.setData(OFPMessage.getOFPPacketIn().getData());
			packetOut.setDatapath_id(OFPMessage.getOFPPacketIn().getDatapath_id());
			packetOut.setIn_port(in_port);
			
			message.setOFPPacketOut(packetOut);
		}
		else {
			OFPFlowMod flowMod = new OFPFlowMod();
			instructions instructionSet = new instructions();
			actions output = new actions();
			match l2match = new match();
			oxm_fields fields = new oxm_fields();

			OFPActionOutput OFPOutput = new OFPActionOutput();
			OFPOutput.setOut_port(out_port);
			output.setOFPActionOutput(OFPOutput);

			OFPInstructionActions OFPAction = new OFPInstructionActions();
			OFPAction.setActions(Arrays.asList(output));
			instructionSet.setOFPInstructionActions(OFPAction);

			OFPMatch OFPMatch = new OFPMatch();
			OXMTlv fieldEthDst = new OXMTlv();
			fieldEthDst.setField("eth_dst");
			fieldEthDst.setValue(eth_dst);
			fields.setOXMTlv(fieldEthDst);
			
			OFPMatch.setOxm_fields(Arrays.asList(fields));
			l2match.setOFPMatch(OFPMatch);

			flowMod.setBuffer_id(OFPMessage.getOFPPacketIn().getBuffer_id());
			flowMod.setDatapath_id(OFPMessage.getOFPPacketIn().getDatapath_id());
			flowMod.setPriority(123);
			flowMod.setTable_id(0);
			flowMod.setInstructions(Arrays.asList(instructionSet));
			flowMod.setMatch(l2match);
			
			message.setOFPFlowMod(flowMod);
		}

		network.send(message);
	}
}