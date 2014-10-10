/*
* File:   OFPMessageFactory.java
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

package sdk4sdn.openflow13;

import java.util.Arrays;

/**
 *
 * @author aepp
 */
public class OFPMessageFactory {

	public static OFPPacketOut CreatePacketOut(String out_port) {
		OFPPacketOut message = new OFPPacketOut();
		
		actions output = new actions();
		OFPActionOutput OFPOutput = new OFPActionOutput();
		OFPOutput.setOut_port(out_port);
		output.setOFPActionOutput(OFPOutput);
		message.setActions(Arrays.asList(output));
		
		return message;
	}
	
	public static OFPPacketOut CreatePacketOut(String out_port, OpenFlow inMessage) {
		OFPPacketOut message = OFPMessageFactory.CreatePacketOut(out_port);

		message.setBuffer_id(inMessage.getOFPPacketIn().getBuffer_id());
		message.setData(inMessage.getOFPPacketIn().getData());
		message.setDatapath_id(inMessage.getOFPPacketIn().getDatapath_id());
		for ( oxm_fields field : inMessage.getOFPPacketIn().getMatch().getOFPMatch().getOxm_fields()) {
			if("in_port".equals(field.getOXMTlv().getField())) {
				message.setIn_port(field.getOXMTlv().getValue());
			}
		}
		
		return message;
	}
	
	public static OFPFlowMod CreateFlowMod(actions actions, oxm_fields fields){
		OFPFlowMod message = new OFPFlowMod();
		
		instructions instructionSet = new instructions();
		match match = new match();

		OFPInstructionActions OFPAction = new OFPInstructionActions();
		OFPAction.setActions(Arrays.asList(actions));
		instructionSet.setOFPInstructionActions(OFPAction);

		OFPMatch OFPMatch = new OFPMatch();
			
		OFPMatch.setOxm_fields(Arrays.asList(fields));
		match.setOFPMatch(OFPMatch);
		message.setInstructions(Arrays.asList(instructionSet));
		message.setMatch(match);
		
		return message;
	}
	
	public static OFPFlowMod CreateFlowMod(actions actions, oxm_fields fields, OpenFlow inMessage){
		OFPFlowMod message = OFPMessageFactory.CreateFlowMod(actions, fields);
		
		message.setBuffer_id(inMessage.getOFPPacketIn().getBuffer_id());
		message.setDatapath_id(inMessage.getOFPPacketIn().getDatapath_id());
		message.setPriority(1);
		message.setTable_id(0);
		
		return message;
	}
	
	public static OFPActionOutput CreateActionOutput(String outPort){
		OFPActionOutput OFPOutput = new OFPActionOutput();
		OFPOutput.setOut_port(outPort);
		return OFPOutput;
	}
}
