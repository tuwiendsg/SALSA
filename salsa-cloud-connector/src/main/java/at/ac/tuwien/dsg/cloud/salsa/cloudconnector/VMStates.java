package at.ac.tuwien.dsg.cloud.salsa.cloudconnector;


public enum VMStates {
	Running ("running"),
	Failed ("failed"),
	Prolog ("prolog"),
	Pending ("pending"),
	Epilog ("epilog"),
	Terminated ("terminated"),
	Unrecognized ("unrecognized"),
	Unknown ("unknown");
	
private String value;
	
	private VMStates(String value){
		this.value = value;
	}
	
	public String getString() {
		return value;
	}

	public static VMStates fromString(String text) {
	    if (text != null) {
	      for (VMStates b : VMStates.values()) {
	        if (text.equalsIgnoreCase(b.getString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }
}
