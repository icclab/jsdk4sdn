/*
* File:   Sdk4Sdn.java
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

package sdk4sdn;

import sdk4sdn.openflow13.OFPEventPacketIn;
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
		//FIXME: Remove this or extend it!!
		List<OFPEventPacketIn> events = pluginManager.getExtensions(OFPEventPacketIn.class);
        System.out.println(String.format("Found %d subscription for OFPEventPacketIn point '%s'", events.size(), OFPEventPacketIn.class.getName()));
		
		// Create a brand new controller<->sdk4sdn connection
		// Start the subscriber and connect
		Network RyuConnection = new Network("sdk4sdn", "controller", pluginManager);
		RyuConnection.CreateSubscriber();
		RyuConnection.CreatePublisher();
		RyuConnection.Connect();
	}
	
}
