package at.ac.tuwien.dsg.cloud.salsa.pioneer;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaPioneerInterface;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.services.PioneerServiceImplementation;

public class TestMain {

	public static void main(String[] args) {
		startRESTService();
	}
	
	private static void startRESTService(){
		
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(SalsaPioneerInterface.class);
        sf.setResourceProvider(SalsaPioneerInterface.class, 
            new SingletonResourceProvider(new PioneerServiceImplementation()));
        sf.setAddress("http://localhost:9000/");
        sf.create();
			
			
		
	}

}
