/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.util.HashMap;
import ro.fortsoft.pf4j.Extension;
import sdk4sdn.OFPEventPacketIn;
import sdk4sdn.openflow13.*;

/**
 *
 * @author aepp
 */
@Extension
public class L2Switch implements OFPEventPacketIn {
	
	HashMap<String, String> map = new HashMap<String, String>();
	
	public L2Switch(){
		//FIXME: Currently unused, maybe initialize the map or remove
	}

	@Override
	public void packetIn(OpenFlow OFPMessage) {
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
		map.put(eth_src, in_port);
		
		if(map.get(eth_dst) != null) {
			//We know the out port
		}
		else {
			//We do not know the out port
		}
	}
	
}