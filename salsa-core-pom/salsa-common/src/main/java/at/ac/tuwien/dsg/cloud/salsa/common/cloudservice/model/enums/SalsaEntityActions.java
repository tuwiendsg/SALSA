package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * This enum contains actions which Salsa can to on Application entities.
 * These values must be describe exactly in Tosca, at ScriptArtifactProperty
 * @author hungld
 *
 */
@XmlType(name = "SalsaEntityActionsEnum")
@XmlEnum
public enum SalsaEntityActions {
	DEPLOY("deploy"),
	UNDEPLOY("undeploy"),	
	CONFIGURE("start"),
	START("start"),
	STOP("stop");
	
	private String nodeState;
	
	private SalsaEntityActions(String nodeState){
		this.nodeState = nodeState;
	}

	public String getNodeStateString() {
		return nodeState;
	}
	
	public static SalsaEntityActions fromString(String text) {
	    if (text != null) {
	      for (SalsaEntityActions b : SalsaEntityActions.values()) {
	        if (text.equalsIgnoreCase(b.getNodeStateString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

		
}
