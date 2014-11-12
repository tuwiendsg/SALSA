package at.ac.tuwien.dsg.cloud.salsa.cloudconnector.openstack;

import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.ParameterStringsEnumInterface;


public enum OpenStackParameterStrings implements ParameterStringsEnumInterface {
	SECRET_KEY("secret_key"),
	ACCESS_KEY("access_key"),
	PORT("port"),
	SSH_KEY_NAME("sshKeyName"),
	END_POINT("end_point"),
	USERNAME("username"),
	PASSWORD("password"),
	TENANT("tenant"),
	KEYSTONE_ENDPOINT("keystone_endpoint");
	
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
