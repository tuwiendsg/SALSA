package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.type;


public enum SalsaArtifactType {
	sh("sh"),
	chef("chef");
	
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