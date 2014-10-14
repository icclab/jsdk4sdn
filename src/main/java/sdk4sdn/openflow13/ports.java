
package sdk4sdn.openflow13;

import java.util.List;

public class ports{
   	private String eth_addr;
   	public String port_no;

 	public String getEth_addr(){
		return this.eth_addr;
	}
	public void setEth_addr(String eth_addr){
		this.eth_addr = eth_addr;
	}
 	public String getPort_no(){
		return this.port_no;
	}
	public void setPort_no(String port_no){
		this.port_no = port_no;
	}
}
