/*
* File:   OpenFlow.java
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

package sdk4sdn.openflow13;

public class OpenFlow{
   	private OFPPacketIn OFPPacketIn;
	private OFPFlowMod OFPFlowMod;
	private OFPPacketOut OFPPacketOut;
	private OFPSwitchFeatures OFPSwitchFeatures;

 	public OFPPacketIn getOFPPacketIn(){
		return this.OFPPacketIn;
	}
	public void setOFPPacketIn(OFPPacketIn OFPPacketIn){
		this.OFPPacketIn = OFPPacketIn;
	}
	public OFPFlowMod getOFPFlowMod(){
		return this.OFPFlowMod;
	}
	public void setOFPFlowMod(OFPFlowMod OFPFlowMod){
		this.OFPFlowMod = OFPFlowMod;
	}
	public OFPPacketOut getOFPPacketOut(){
		return this.OFPPacketOut;
	}
	public void setOFPPacketOut(OFPPacketOut OFPPacketOut){
		this.OFPPacketOut = OFPPacketOut;
	}
	public OFPSwitchFeatures getOFPSwitchFeatures(){
		return this.OFPSwitchFeatures;
	}
	public void setOFPSwitchFeatures(OFPSwitchFeatures OFPSwitchFeatures){
		this.OFPSwitchFeatures = OFPSwitchFeatures;
	}
}
