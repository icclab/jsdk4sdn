/*
* File:   L2Domain.java
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

import java.util.ArrayList;
import java.util.HashMap;
import ro.fortsoft.pf4j.Extension;
import sdk4sdn.Network;
import sdk4sdn.lib.EventMainDatapath;
import sdk4sdn.lib.OneSwitch;
import sdk4sdn.openflow13.OFPActionOutput;
import sdk4sdn.openflow13.OFPMessageFactory;
import sdk4sdn.openflow13.OXMTlv;
import sdk4sdn.openflow13.OpenFlow;
import sdk4sdn.openflow13.oxm_fields;

/**
 *
 * @author aepp
 */
@Extension
public class L2Domain implements EventMainDatapath {
	
	HashMap<String, String> ethDstMap = new HashMap<>();

	@Override
	public void packetInMainDatapath(Network network, OneSwitch mainDP, OpenFlow OFPMessage, String vPort) {
		/**
		 * The vPort is the in_port of the mainDP, but we can also get the
		 * real in_port from the OFPMessage object. ETH-Addresses are not
		 * virtual or anything like that, they can directly fetched from the
		 * OFPMessage object
		 */
		String eth_src = "";
		String eth_dst = "";
		String in_port = "";
		String out_port = "";
		
		//Get the fields 
		for ( oxm_fields field : OFPMessage.getOFPPacketIn().getMatch().getOFPMatch().getOxm_fields()) {
			if("eth_src".equals(field.getOXMTlv().getField())) {
				eth_src = field.getOXMTlv().getValue();
			}
			if("eth_dst".equals(field.getOXMTlv().getField())) {
				eth_dst = field.getOXMTlv().getValue();
			}
			if("in_port".equals(field.getOXMTlv().getField())) {
				//Don't forget, that this is the physical one
				in_port = field.getOXMTlv().getValue();
			}
		}
		
		//Learn the MAC <-> PORT
		this.ethDstMap.put(eth_src, vPort);
		
		//Check if we already know the MAC <-> PORT
		if(this.ethDstMap.get(eth_dst) != null) {
			ArrayList<oxm_fields> fieldsList = new ArrayList<>();
			oxm_fields fields = new oxm_fields();
			OXMTlv fieldEthDst = new OXMTlv();
			fieldEthDst.setField("eth_dst");
			fieldEthDst.setValue(eth_dst);
			fields.setOXMTlv(fieldEthDst);
			fieldsList.add(fields);
			
			fields = new oxm_fields();
			OXMTlv fieldEthSrc = new OXMTlv();
			fieldEthSrc.setField("eth_src");
			fieldEthSrc.setValue(eth_src);
			fields.setOXMTlv(fieldEthSrc);
			fieldsList.add(fields);
			
			out_port = this.ethDstMap.get(eth_dst);
			
			fields = new oxm_fields();
			OXMTlv fieldOutPort = new OXMTlv();
			fieldOutPort.setField("in_port");
			fieldOutPort.setValue(vPort);
			fields.setOXMTlv(fieldOutPort);
			fieldsList.add(fields);
			
			OFPActionOutput packetOut = OFPMessageFactory.CreateActionOutput(out_port);
			
			mainDP.addRouteFromSelf(network, fieldsList, packetOut);
		}
		//We flood anyways always from self to deliver the packet
		mainDP.floodFromSelf(network, OFPMessage, vPort);
	}
}
