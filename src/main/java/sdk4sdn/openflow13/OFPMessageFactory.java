/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
