package at.ac.tuwien.dsg.cloud.salsa.common.data;

public enum SalsaEntityType {
	VIRTUAL_MACHINE("virtualmachine"),
	SOFTWARE("software"),
	ARTIFACT("artifact");
	
	private String entityType;
	
	private SalsaEntityType(String entityType){
		this.entityType = entityType;
	}

	public String getEntityTypeString() {
		return entityType;
	}
	
	public static SalsaEntityType fromString(String text) {
	    if (text != null) {
	      for (SalsaEntityType b : SalsaEntityType.values()) {
	        if (text.equalsIgnoreCase(b.getEntityTypeString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}
