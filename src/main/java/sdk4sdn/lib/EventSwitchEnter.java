/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdk4sdn.lib;

import ro.fortsoft.pf4j.ExtensionPoint;
import sdk4sdn.Network;

/**
 *
 * @author aepp
 */
public interface EventSwitchEnter extends ExtensionPoint {
	public void switchEnter(Topology topology, Network network);
}
