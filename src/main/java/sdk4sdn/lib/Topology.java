/*
* File:   Topology.java
* Author: aepp
*
* Created on 16.10.2014, 14:11
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

import java.util.List;

public class Topology{
   	private String dpid;
   	private List <Ports> ports;
   	private Dst dst;
   	private Src src;

 	public String getDpid(){
		return this.dpid;
	}
	public void setDpid(String dpid){
		this.dpid = dpid;
	}
 	public List <Ports> getPorts(){
		return this.ports;
	}
	public void setPorts(List <Ports> ports){
		this.ports = ports;
	}
 	public Dst getDst(){
		return this.dst;
	}
	public void setDst(Dst dst){
		this.dst = dst;
	}
 	public Src getSrc(){
		return this.src;
	}
	public void setSrc(Src src){
		this.src = src;
	}
}
