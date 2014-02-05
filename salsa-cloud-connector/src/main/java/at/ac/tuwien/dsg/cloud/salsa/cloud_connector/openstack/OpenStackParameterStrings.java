package at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.ParameterStringsEnumInterface;


public enum OpenStackParameterStrings implements ParameterStringsEnumInterface {
	SECRET_KEY("secret_key"),
	ACCESS_KEY("access_key"),
	PORT("port"),
	END_POINT("end_point");
	
	private String value;
	
	private OpenStackParameterStrings(String value){
		this.value = value;
	}
	
	public String getString() {
		return value;
	}

	@Deprecated
	public static OpenStackParameterStrings fromString(String text) {
	    if (text != null) {
	      for (OpenStackParameterStrings b : OpenStackParameterStrings.values()) {
	        if (text.equalsIgnoreCase(b.getString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}
