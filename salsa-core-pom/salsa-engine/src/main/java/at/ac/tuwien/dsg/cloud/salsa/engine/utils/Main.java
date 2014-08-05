package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import org.springframework.beans.factory.annotation.Autowired;

public class Main {
	
	@Autowired
	static SalsaConfigurationWiring conf = new SalsaConfigurationWiring();
	
	public static void main(String[] args) {
		try {
			System.out.println(conf.getSALSA_CENTER_ENDPOINT());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
