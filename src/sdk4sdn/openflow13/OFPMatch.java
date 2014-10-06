
package sdk4sdn.openflow13;

import java.util.List;

public class OFPMatch{
   	private List<oxm_fields> oxm_fields;

 	public List<oxm_fields> getOxm_fields(){
		return this.oxm_fields;
	}
	public void setOxm_fields(List<oxm_fields> oxm_fields){
		this.oxm_fields = oxm_fields;
	}
}
