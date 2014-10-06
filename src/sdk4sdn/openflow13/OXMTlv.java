
package sdk4sdn.openflow13;

import java.util.List;

public class OXMTlv{
   	private String field;
   	private Number value;

 	public String getField(){
		return this.field;
	}
	public void setField(String field){
		this.field = field;
	}
 	public Number getValue(){
		return this.value;
	}
	public void setValue(Number value){
		this.value = value;
	}
}
