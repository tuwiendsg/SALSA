package at.ac.tuwien.dsg.cloud.salsa.pioneer.type;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;

public enum PropertyVMExpose {
	ip("ip"),
	public_ip("public_ip"),
	private_ip("private_ip");
	
	private String prop;
	
	private PropertyVMExpose(String propString){
		this.prop = propString;
	}

	public String getString() {
		return prop;
	}
	
	public static PropertyVMExpose fromString(String text) {
	    if (text != null) {
	      for (PropertyVMExpose b : PropertyVMExpose.values()) {
	        if (text.equalsIgnoreCase(b.getString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}