package at.ac.tuwien.dsg.cloud.salsa.common.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * This enum contains cloud provider which Salsa currently support
 * @author Le Duc Hung
 */
@XmlType(name = "SalsaCloudProviderEnum")
@XmlEnum
public enum SalsaCloudProviders {
	OPENSTACK("OPENSTACK"), 
	STRATUSLAB("STRATUSLAB");
	
	private String providerId;

	private SalsaCloudProviders(String providerId) {
		this.providerId = providerId;
	}
	
	public String getCloudProviderString() {
		return providerId;
	}
	
	public static SalsaCloudProviders fromString(String text) {
	    if (text != null) {
	      for (SalsaCloudProviders b : SalsaCloudProviders.values()) {
	        if (text.equalsIgnoreCase(b.getCloudProviderString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}
