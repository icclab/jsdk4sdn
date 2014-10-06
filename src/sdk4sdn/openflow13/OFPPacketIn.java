
package sdk4sdn.openflow13;

import java.util.List;

public class OFPPacketIn{
   	private Number buffer_id;
   	private String data;
   	private Number datapath;
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
 	public Number getDatapath(){
		return this.datapath;
	}
	public void setDatapath(Number datapath){
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
