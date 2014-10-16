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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import ro.fortsoft.pf4j.Extension;
import sdk4sdn.Network;
import sdk4sdn.openflow13.OFPEventPacketIn;
import sdk4sdn.openflow13.OFPMessageFactory;
import sdk4sdn.openflow13.OFPPacketOut;
import sdk4sdn.openflow13.OpenFlow;
import sdk4sdn.openflow13.oxm_fields;

/**
 *
 * @author aepp
 */
@Extension
public class OneSwitch implements OFPEventPacketIn, EventSwitchEnter, EventLinkEnter {
	
	public LinkedHashMap<String, List> PortLinks = new LinkedHashMap<>();
	
	public LinkedHashMap<String, List> PortSwitches = new LinkedHashMap<>();
	
	public LinkedHashMap<String, List> ports = new LinkedHashMap<>();
	
	public LinkedHashMap<String, String> mainDatapath = new LinkedHashMap<>();
	
	public DirectedGraph<String, DefaultEdge> directedGraph;
	
	public List<Topology> allLinks = new ArrayList<>();
	
	public MainDatapath datapath = new MainDatapath();
	
	public OneSwitch(){
		this.directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
	}

	@Override
	public void packetIn(OpenFlow OFPMessage, Network network) {
		String eth_src = "";
		String eth_dst = "";
		String in_port = "";
		
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
		
		if("01:80:c2:00:00:0e".equals(eth_dst))
			return;
		if("ff:ff:ff:ff:ff:ff".equals(eth_dst))
			return;
		if("33:33".equals(eth_dst.substring(0, 5)))
			return;
		
		for (EventMainDatapath packetIn : network.EventMainDatapathList) {
			packetIn.packetInMainDatapath(network, this, OFPMessage, OFPMessage.getOFPPacketIn().getDatapath_id() + "." + in_port);
		}
	}

	@Override
	public void switchEnter(Topology topology, Network network) {
		//DANGER ZONE, a switch has at max 48 ports
		List<String> tmpPorts = new ArrayList<>();
		for(Ports port : topology.getPorts()){
			tmpPorts.add(port.getPort_no());
		}
		this.PortSwitches.put(topology.getDpid(), tmpPorts);
		this.updateMainDatapath(topology);
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
		this.updateMainDatapath(topology);
		this.updateDijkstraGraph();
	}
	
	public void updateMainDatapath(Topology topology){
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
			
			Ports portObj = new Ports();
			portObj.setDpid(dpid);
			
			//Add all ports from the available ports list
			for (String port : portsAvailable) {
				this.mainDatapath.put(dpid + "." + port, port);
				
				portObj.setPort_no(port);
				this.datapath.addPort(portObj);
			}
			//Remove ports from mainDatapath
			for (String port : portsToRemove) {
				this.mainDatapath.remove(dpid + "." + port);
				
				portObj.setPort_no(port);
				this.datapath.deletePort(portObj);
			}
		}
	}
	
	public void updateDijkstraGraph(){
		//build the dijkstra graph
		for (Topology link : this.allLinks) {
			this.directedGraph.addVertex(link.getSrc().getDpid());
			this.directedGraph.addVertex(link.getDst().getDpid());
			this.directedGraph.addEdge(link.getSrc().getDpid(), link.getDst().getDpid());
		}
	}
	
	public void floodFromSelf(Network network, OpenFlow OFPMessage, String in_port){
		for (Map.Entry<String, String> entry : this.mainDatapath.entrySet()) {
			String identifier = entry.getKey();
			System.out.println("Send packet out on dpid: " + identifier);
			if(identifier.equals(in_port))
				continue;
			String[] parts = identifier.split(Pattern.quote("."));
			
			OpenFlow message = new OpenFlow();
			OFPPacketOut packetOut = OFPMessageFactory.CreatePacketOut(parts[1], OFPMessage);
			packetOut.setDatapath_id(Integer.parseInt(parts[0]));
			message.setOFPPacketOut(packetOut);
			network.Send(message);
		}
	}
}
