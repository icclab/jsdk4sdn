/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdk4sdn;
import ro.fortsoft.pf4j.ExtensionPoint;
import sdk4sdn.openflow13.OpenFlow;

//FIXME: Create a new subpackge for the OFP implementation

/**
 *
 * @author aepp
 */
public interface OFPEventPacketIn extends ExtensionPoint {
	public void packetIn(OpenFlow OFPMessage);
}
