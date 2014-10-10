
package sdk4sdn.openflow13;

import java.util.List;

public class OFPSwitchFeatures{
   	private Number capabilities;
   	private Number datapath_id;
   	private Number n_tables;

 	public Number getCapabilities(){
		return this.capabilities;
	}
	public void setCapabilities(Number capabilities){
		this.capabilities = capabilities;
	}
 	public Number getDatapath_id(){
		return this.datapath_id;
	}
	public void setDatapath_id(Number datapath_id){
		this.datapath_id = datapath_id;
	}
 	public Number getN_tables(){
		return this.n_tables;
	}
	public void setN_tables(Number n_tables){
		this.n_tables = n_tables;
	}
}
