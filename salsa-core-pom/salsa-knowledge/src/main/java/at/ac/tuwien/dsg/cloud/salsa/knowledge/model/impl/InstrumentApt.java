package at.ac.tuwien.dsg.cloud.salsa.knowledge.model.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.model.InstrumentalMechanism;

/** 
 * How to execute apt on linux
 * @author Le Duc Hung
 *
 */
public class InstrumentApt extends InstrumentalMechanism {
	
	public InstrumentApt(){
		setType(InstrumentType.apt);
	}

	@Override
	public void install(String entityName) {
		Process p;
		try {
			p = Runtime.getRuntime().exec("apt-get install " + entityName);
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
