package at.ac.tuwien.dsg.cloud.salsa.knowledge.architecturerefine.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.architecturerefine.InstrumentalMechanism;

public class InstrumentScript extends InstrumentalMechanism {
	
	public InstrumentScript() {
		setType(InstrumentType.script);		
	}
	
	@Override
	public void install(String entityName) {
		Process p;
		try {
			p = Runtime.getRuntime().exec("/etc/bash " + entityName);
		    p.waitFor();
		 
		    BufferedReader reader = 
		         new BufferedReader(new InputStreamReader(p.getInputStream()));
		 
		    String line = "";	
		    StringBuffer sb = new StringBuffer();
		    while ((line = reader.readLine())!= null) {
		    	sb.append(line + "\n");
		    }
		    System.out.println(sb.toString());
		} catch (Exception e){
			System.out.println(e);
		}
	}
}
