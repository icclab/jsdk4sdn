/*
* File:   OFPPacketIn.java
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

public class OFPPacketIn{
   	private Number buffer_id;
   	private String data;
   	private datapath datapath;
   	private Number datapath_id;
   	private match match;
   	private Number msg_len;

 	public Number getBuffer_id(){
		return this.buffer_id;
	}
	public void setBuffer_id(Number buffer_id){
		this.buffer_id = buffer_id;
	}
 	public String getData(){
		return this.data;
	}
	public void setData(String data){
		this.data = data;
	}
 	public datapath getDatapath(){
		return this.datapath;
	}
	public void setDatapath(datapath datapath){
		this.datapath = datapath;
	}
 	public Number getDatapath_id(){
		return this.datapath_id;
	}
	public void setDatapath_id(Number datapath_id){
		this.datapath_id = datapath_id;
	}
 	public match getMatch(){
		return this.match;
	}
	public void setMatch(match match){
		this.match = match;
	}
 	public Number getMsg_len(){
		return this.msg_len;
	}
	public void setMsg_len(Number msg_len){
		this.msg_len = msg_len;
	}
}
