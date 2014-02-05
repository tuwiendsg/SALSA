package at.ac.tuwien.dsg.cloud.salsa.cloud_connector;

import java.util.HashMap;
import java.util.Map;

public class CloudParameter {
	String connector;	// header of the config file
	String site;		// after slash
	
	Map<String, String> parameter = new HashMap<String, String>();
	
	public String getParameter(ParameterStringsEnumInterface paramEnum){
		return parameter.get(paramEnum.getString());
	}	
	
	public String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Map<String, String> getParameter() {
		return parameter;
	}

	public void setParameter(Map<String, String> parameter) {
		this.parameter = parameter;
	}
	
	
	
}
