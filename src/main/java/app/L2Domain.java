/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import ro.fortsoft.pf4j.Extension;
import sdk4sdn.Network;
import sdk4sdn.lib.EventMainDatapath;
import sdk4sdn.lib.OneSwitch;
import sdk4sdn.openflow13.OpenFlow;

/**
 *
 * @author aepp
 */
@Extension
public class L2Domain implements EventMainDatapath{

	@Override
	public void packetInMainDatapath(Network network, OneSwitch mainDP, OpenFlow OFPMessage, String in_port) {
		mainDP.floodFromSelf(network, OFPMessage, in_port);
	}
	
}
