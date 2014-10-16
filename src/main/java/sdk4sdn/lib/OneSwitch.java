/*
* File:   OneSwitch.java
* Author: aepp
*
* Created on 14.10.2014, 14:11
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

package sdk4sdn.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import ro.fortsoft.pf4j.Extension;
import sdk4sdn.Network;
import sdk4sdn.openflow13.OFPEventPacketIn;
import sdk4sdn.openflow13.OpenFlow;

/**
 *
 * @author aepp
 */
@Extension
public class OneSwitch implements OFPEventPacketIn, EventSwitchEnter, EventLinkEnter {
	
	public HashMap<String, List> PortLinks = new HashMap<>();
	
	public HashMap<String, List> PortSwitches = new HashMap<>();
	
	public HashMap<String, List> ports = new HashMap<>();
	
	public HashMap<String, String> mainDatapath = new HashMap<>();
	
	public DirectedGraph<String, DefaultEdge> directedGraph;
	
	public List<Topology> allLinks = new ArrayList<>();
	
	public OneSwitch(){
		this.directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
		System.out.println("New object");
	}

	@Override
	public void packetIn(OpenFlow OFPMessage, Network network) {
		System.out.print("The main datapath");
		for (Map.Entry<String, String> entry : this.mainDatapath.entrySet()) {
			System.out.print("{" + entry.getKey() + " " + entry.getValue() + "}");
		}
		System.out.println("");
	}

	@Override
	public void switchEnter(Topology topology, Network network) {
		//DANGER ZONE, a switch has at max 48 ports
		List<String> tmpPorts = new ArrayList<>();
		for(Ports port : topology.getPorts()){
			tmpPorts.add(port.getPort_no());
		}
		this.PortSwitches.put(topology.getDpid(), tmpPorts);
		this.updateMainDatapath();
	}
	
	@Override
	public void linkEnter(Topology topology, Network network) {
		if(!this.allLinks.contains(topology)){
			this.allLinks.add(topology);
		}
		List<String> tmpPorts = new ArrayList<>();
		if(this.PortLinks.get(topology.getDst().getDpid()) != null) {
			this.PortLinks.get(topology.getDst().getDpid()).add(topology.getDst().getPort_no());
		}
		else {
			tmpPorts.add(topology.getDst().getPort_no());
			this.PortLinks.put(topology.getDst().getDpid(), tmpPorts);
		}
		
		tmpPorts = new ArrayList<>();
		if(this.PortLinks.get(topology.getSrc().getDpid()) != null) {
			this.PortLinks.get(topology.getSrc().getDpid()).add(topology.getSrc().getPort_no());
		}
		else {
			tmpPorts.add(topology.getSrc().getPort_no());
			this.PortLinks.put(topology.getSrc().getDpid(), tmpPorts);
		}
		this.updateMainDatapath();
		this.updateDijkstraGraph();
	}
	
	public void updateMainDatapath(){
		//build the main datapath
		for (Map.Entry<String, List> entry : this.PortSwitches.entrySet()) {
			//Check if there are links available to remove them
			//A device with no link does simply not exist
			String dpid = entry.getKey();
			if(this.PortLinks.get(dpid) == null) {
				continue;
			}
			List<String> portsToRemove = this.PortLinks.get(dpid);
			List<String> portsAvailable = entry.getValue();
			portsAvailable.removeAll(portsToRemove);
			//Add all ports from the available ports list
			for (String port : portsAvailable) {
				this.mainDatapath.put(dpid + "." + port, port);
			}
			//Remove ports from mainDatapath
			for (String port : portsToRemove) {
				this.mainDatapath.remove(dpid + "." + port);
			}
		}
	}
	
	public void updateDijkstraGraph(){
		//build the dijkstra graph
		for (Topology link : this.allLinks) {
			this.directedGraph.addVertex(link.getSrc().getDpid());
			this.directedGraph.addEdge(link.getSrc().getDpid(), link.getDst().getDpid());
		}
	}
}
