
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
