package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * This is the state of configuration action
 * @author hungld
 *
 */
@XmlType(name = "SalsaEntityStateEnum")
@XmlEnum
public enum SalsaEntityState { //	UNDEPLOYED("undeployed"),
	UNDEPLOYED("undeployed"),
	ALLOCATING("allocating"),
	STAGING("staging"),
	STAGING_ACTION("staging_action"),
	CONFIGURING("configuring"),
	INSTALLING("installing"),	// the deployment action is executed
	DEPLOYED("deployed"), // deployed
	ERROR("error");
        
	
	private String nodeState;
	
	private SalsaEntityState(String nodeState){
		this.nodeState = nodeState;
	}
	
	public String getNodeStateString() {
		return nodeState;
	}
	
	public static SalsaEntityState fromString(String text) {
	    if (text != null) {
	      for (SalsaEntityState b : SalsaEntityState.values()) {
	        if (text.equalsIgnoreCase(b.getNodeStateString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

		
}
