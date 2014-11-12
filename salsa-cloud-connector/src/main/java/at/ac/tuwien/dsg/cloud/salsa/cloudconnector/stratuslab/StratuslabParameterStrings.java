package at.ac.tuwien.dsg.cloud.salsa.cloudconnector.stratuslab;

import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.ParameterStringsEnumInterface;


public enum StratuslabParameterStrings implements ParameterStringsEnumInterface {
	endpoint("endpoint"),
	username("username"),
	password("password"),
	pdisk_endpoint("pdisk_endpoint"),
	user_public_key_file("user_public_key_file"),
	client_path("client_path");
	
	private String value;
	
	private StratuslabParameterStrings(String value){
		this.value = value;
	}
	
	public String getString() {
		return value;
	}

	@Deprecated
	public static StratuslabParameterStrings fromString(String text) {
	    if (text != null) {
	      for (StratuslabParameterStrings b : StratuslabParameterStrings.values()) {
	        if (text.equalsIgnoreCase(b.getString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}
