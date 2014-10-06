/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sdk4sdn;

import java.util.List;
import ro.fortsoft.pf4j.DefaultPluginManager;
import ro.fortsoft.pf4j.PluginManager;
/**
 *
 * @author aepp
 */
public class Sdk4Sdn {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		//The Plugin path, FIXME: I think this is not needed
		System.setProperty("pf4j.pluginsDir", "/home/staff/aepp/unix/NetBeansProjects/jsdk4sdn/src/app");
		
		// Load and start the user SDN applications
		PluginManager pluginManager = new DefaultPluginManager();
		pluginManager.loadPlugins();
		pluginManager.startPlugins();
		
		//Show a list of all subscribed plugins to the events
		List<OFPEventPacketIn> events = pluginManager.getExtensions(OFPEventPacketIn.class);
        System.out.println(String.format("Found %d subscription for OFPEventPacketIn point '%s'", events.size(), OFPEventPacketIn.class.getName()));
		
		// Create a brand new controller<->sdk4sdn connection
		// Start the subscriber and connect
		Network RyuConnection = new Network("skd4sdn", "controller", pluginManager);
		RyuConnection.CreateSubscriber();
		RyuConnection.Connect();
	}
	
}
