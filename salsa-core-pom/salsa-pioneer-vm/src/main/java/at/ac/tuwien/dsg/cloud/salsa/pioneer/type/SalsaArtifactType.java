package at.ac.tuwien.dsg.cloud.salsa.pioneer.type;


public enum SalsaArtifactType {
	sh("sh"),
	apt("apt-get"),
	chef("chef"),
	chefSolo("chef-solo");
	
	private String prop;
	
	private SalsaArtifactType(String propString){
		this.prop = propString;
	}

	public String getString() {
		return prop;
	}
	
	public static SalsaArtifactType fromString(String text) {
	    if (text != null) {
	      for (SalsaArtifactType b : SalsaArtifactType.values()) {
	        if (text.equalsIgnoreCase(b.getString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}