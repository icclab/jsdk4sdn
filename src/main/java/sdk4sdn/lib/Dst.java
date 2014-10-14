
package sdk4sdn.lib;

import java.util.List;

public class Dst{
   	private String dpid;
   	private String hw_addr;
   	private String port_no;

 	public String getDpid(){
		return this.dpid;
	}
	public void setDpid(String dpid){
		this.dpid = dpid;
	}
 	public String getHw_addr(){
		return this.hw_addr;
	}
	public void setHw_addr(String hw_addr){
		this.hw_addr = hw_addr;
	}
 	public String getPort_no(){
		return this.port_no;
	}
	public void setPort_no(String port_no){
		this.port_no = port_no;
	}
}
