package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * This class define instance states
 * @author hungld
 *
 */
@XmlType(name = "SalsaEntityStateEnum")
@XmlEnum
public enum SalsaInstanceState {
	UNDEPLOYED("undeployed"),
	DEPLOYED("deployed"),	
	STOPPED("stopped"),
	RUNNING("running"),
	ERROR("error");
	
	private String nodeState;
	
	private SalsaInstanceState(String nodeState){
		this.nodeState = nodeState;
	}
	
	public String getNodeStateString() {
		return nodeState;
	}
	
	public static SalsaInstanceState fromString(String text) {
	    if (text != null) {
	      for (SalsaInstanceState b : SalsaInstanceState.values()) {
	        if (text.equalsIgnoreCase(b.getNodeStateString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

		
}
