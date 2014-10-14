
package sdk4sdn.openflow13;

import java.util.List;

public class datapath{
   	private List<ports> ports;

 	public List<ports> getPorts(){
		return this.ports;
	}
	public void setPorts(List<ports> ports){
		this.ports = ports;
	}
}
