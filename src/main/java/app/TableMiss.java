/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import ro.fortsoft.pf4j.Extension;
import sdk4sdn.Network;
import sdk4sdn.openflow13.OFPActionOutput;
import sdk4sdn.openflow13.OFPEventSwitchFeatures;
import sdk4sdn.openflow13.OFPFlowMod;
import sdk4sdn.openflow13.OFPMessageFactory;
import sdk4sdn.openflow13.OXMTlv;
import sdk4sdn.openflow13.OpenFlow;
import sdk4sdn.openflow13.actions;
import sdk4sdn.openflow13.oxm_fields;

/**
 *
 * @author aepp
 */
@Extension
public class TableMiss implements OFPEventSwitchFeatures{

	@Override
	public void switchFeatures(OpenFlow OFPMessage, Network network) {
		OpenFlow message = new OpenFlow();
		//Create an actions object that holds all actions
		actions actions = new actions();
		OFPActionOutput action = OFPMessageFactory.CreateActionOutput("OFPP_CONTROLLER");
		actions.setOFPActionOutput(action);
			
		//A table miss is an empty match, so create one
		oxm_fields fields = new oxm_fields();
		OXMTlv fieldEthDst = new OXMTlv();
		fieldEthDst.setField("");
		fieldEthDst.setValue("");
		fields.setOXMTlv(fieldEthDst);
			
		//Since we have the fields and the action, we can create a FlowMod
		OFPFlowMod flowMod = OFPMessageFactory.CreateFlowMod(actions, fields);
		flowMod.setDatapath_id(OFPMessage.getOFPSwitchFeatures().getDatapath_id());
		flowMod.setTable_id(0);
		flowMod.setPriority(0);

		//now compile the message
		message.setOFPFlowMod(flowMod);
		network.Send(message);
	}
	
}
