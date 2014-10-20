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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.fortsoft.pf4j.DefaultPluginManager;
import ro.fortsoft.pf4j.PluginManager;

import sdk4sdn.lib.EventLinkEnter;
import sdk4sdn.lib.EventMainDatapath;
import sdk4sdn.lib.EventSwitchEnter;
import sdk4sdn.openflow13.OFPEventSwitchFeatures;
import sdk4sdn.openflow13.OFPEventPacketIn;
/**
 *
 * @author aepp
 */
public class Sdk4Sdn {
	
	private static final Properties prop = new Properties();
	
	private static final Logger log = LoggerFactory.getLogger(Sdk4Sdn.class);

	/**
	 * @param args the command line arguments
	 * @throws java.io.IOException
	 */
	public static void main(String[] args) throws IOException {
		printWelcome();
		
		//FIXME: Make this variable
		String pluginPath = "/home/staff/aepp/unix/NetBeansProjects/jsdk4sdn/src/main/java/app/";
		System.setProperty("pf4j.pluginsDir", pluginPath);
		
		// Load and start the user SDN applications
		PluginManager pluginManager = new DefaultPluginManager();
		Set<String> plugins = pluginManager.getExtensionClassNames(null);
		log.info("Available Plugins: "+plugins.toString());
		
		// Create a brand new controller<->sdk4sdn connection
		//FIXME: Make the endpoints "sdk4sdn" and "controller" as variable
		Network ControllerConnection = new Network("sdk4sdn", "controller");
		
		//Get all Extensions for a Extension Point
		//FIXME: Do something here, this gonna be big
		//FIXME: Move this code in an OFPExtensionLoader
		setExtensions(ControllerConnection, pluginManager.getExtensions(OFPEventPacketIn.class), "OFPEventPacketIn", pluginPath);
		setExtensions(ControllerConnection, pluginManager.getExtensions(OFPEventSwitchFeatures.class), "OFPEventSwitchFeatures", pluginPath);
		setExtensions(ControllerConnection, pluginManager.getExtensions(EventLinkEnter.class), "EventLinkEnter", pluginPath);
		setExtensions(ControllerConnection, pluginManager.getExtensions(EventSwitchEnter.class), "EventSwitchEnter", pluginPath);
		setExtensions(ControllerConnection, pluginManager.getExtensions(EventMainDatapath.class), "EventMainDatapath", pluginPath);
		
		// Start the subscriber and connect
		ControllerConnection.CreateSubscriber();
		ControllerConnection.CreatePublisher();
		ControllerConnection.Connect();
	}
	
	public static void setExtensions(Network ControllerConnection, List Extensions, String type, String pluginPath) throws IOException {
		boolean extExists = false;
		List CleanExtensionList = new ArrayList();
		prop.load(new FileInputStream(pluginPath+"disabled.txt"));
		//Loop through the newly extension list
		for (Object extension : Extensions) {
			//Loop through the existing extensions
			for(Object existingExtension : ControllerConnection.AllExtensions) {
				//Check if we already have an instance
				if(extension.getClass().getName().equals(existingExtension.getClass().getName())) {
					if(!prop.containsKey(extension.getClass().getName().substring(extension.getClass().getName().lastIndexOf('.') + 1))) {
						CleanExtensionList.add(existingExtension);
						extExists = true;
					}
				}
			}
			if(!extExists) {
				ControllerConnection.AllExtensions.add(extension);
				if(!prop.containsKey(extension.getClass().getName().substring(extension.getClass().getName().lastIndexOf('.') + 1))) {
					log.info("Enabling Plugin: "+extension.getClass().getName());
					CleanExtensionList.add(extension);
				}
			}
		}
		switch(type) {
			case "OFPEventPacketIn":
				ControllerConnection.SetPacketInSubscribers(CleanExtensionList);
				break;
			case "OFPEventSwitchFeatures":
				ControllerConnection.SetSwitchFeaturesSubscribers(CleanExtensionList);
				break;
			case "EventLinkEnter":
				ControllerConnection.SetLinkEnterSubscribers(CleanExtensionList);
				break;
			case "EventSwitchEnter":
				ControllerConnection.SetSwitchEnterSubscribers(CleanExtensionList);
				break;
			case "EventMainDatapath":
				ControllerConnection.SetMainDatapath(CleanExtensionList);
				break;
		}
	}
	
	public static void printWelcome() {
		log.info("\n   _____ ____  __ ____ __ _____ ____  _   __\n"
				+ "  / ___// __ \\/ //_/ // // ___// __ \\/ | / /\n"
				+ "  \\__ \\/ / / / ,< / // /_\\__ \\/ / / /  |/ /\n"
				+ " ___/ / /_/ / /| /__  __/__/ / /_/ / /|  /\n"
				+ "/____/_____/_/ |_| /_/ /____/_____/_/ |_/\n");
	}
} 