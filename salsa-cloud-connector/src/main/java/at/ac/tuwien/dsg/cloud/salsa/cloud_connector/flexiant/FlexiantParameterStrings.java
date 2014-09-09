package at.ac.tuwien.dsg.cloud.salsa.cloud_connector.flexiant;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.ParameterStringsEnumInterface;


public enum FlexiantParameterStrings implements ParameterStringsEnumInterface {
	email("email"),
	customerUUID("customerUUID"),	
	password("password"),
	endpoint("endpoint"),
	vdcUUID("vdcUUID"),
	defaultProductOfferUUID("defaultProductOfferUUID"),
	clusterUUID("clusterUUID"),
	networkUUID("networkUUID"),
	sshkey("sshkey");
	
	private String value;
	
	private FlexiantParameterStrings(String value){
		this.value = value;
	}
	
	public String getString() {
		return value;
	}

	@Deprecated
	public static FlexiantParameterStrings fromString(String text) {
	    if (text != null) {
	      for (FlexiantParameterStrings b : FlexiantParameterStrings.values()) {
	        if (text.equalsIgnoreCase(b.getString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}
