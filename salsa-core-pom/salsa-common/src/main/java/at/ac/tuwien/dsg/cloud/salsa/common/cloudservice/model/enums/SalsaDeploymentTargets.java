package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SalsaDeploymentTargets")
@XmlEnum
public enum SalsaDeploymentTargets {
	VirtualMachine("VirtualMachine"),
	Platform("Platform"),
	IaaS("IaaS"),
	PaaS("PaaS");	
	
	private String target;
	
	private SalsaDeploymentTargets(String target){
		this.target = target;
	}

	public String getNodeStateString() {
		return target;
	}
		
	public static SalsaDeploymentTargets fromString(String text) {
	    if (text != null) {
	      for (SalsaDeploymentTargets b : SalsaDeploymentTargets.values()) {
	        if (text.equalsIgnoreCase(b.getNodeStateString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }
}
