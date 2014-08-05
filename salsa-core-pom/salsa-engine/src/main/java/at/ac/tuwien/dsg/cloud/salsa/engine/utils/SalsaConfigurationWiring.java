package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SalsaConfigurationWiring {
	@Value("${SALSA_CENTER_ENDPOINT}") String SALSA_CENTER_ENDPOINT;
	
	@Value("${PIONEER_WEB}") String PIONEER_WEB;
	@Value("${PIONEER_FILES}") String PIONEER_FILES;
	@Value("${PIONEER_RUN}") String PIONEER_RUN;
	
	@Value("${SALSA_PRIVATE_KEY}") String SALSA_PRIVATE_KEY;
	
	@Value("${WORKING_DIR}") String WORKING_DIR;
	@Value("${VARIABLE_FILE}") String VARIABLE_FILE;
	
	@Value("${SERVICE_STORAGE}") String SERVICE_STORAGE;
	
	public void test(){
		System.out.println("work");
	}
	
	public String getSALSA_CENTER_ENDPOINT() {
		return SALSA_CENTER_ENDPOINT;
	}

	public String getPIONEER_WEB() {
		return PIONEER_WEB;
	}

	public String getPIONEER_FILES() {
		return PIONEER_FILES;
	}

	public String getPIONEER_RUN() {
		return PIONEER_RUN;
	}

	public String getSALSA_PRIVATE_KEY() {
		return SALSA_PRIVATE_KEY;
	}

	public String getWORKING_DIR() {
		return WORKING_DIR;
	}

	public String getVARIABLE_FILE() {
		return VARIABLE_FILE;
	}

	public String getSERVICE_STORAGE() {
		return SERVICE_STORAGE;
	}		
	
}

