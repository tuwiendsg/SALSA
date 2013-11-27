package at.ac.tuwien.dsg.cloud.salsa.common.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "myEnum")
@XmlEnum
public enum SalsaEntityState {
	INITIAL("initial"),
	PROLOGUE("prologue"),	
	DEPLOYED("deployed"),
	READY("ready"),
	ERROR("error"),
	UNDEPLOYED("undeployed");
	
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
