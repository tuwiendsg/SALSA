package at.ac.tuwien.dsg.cloud.salsa.knowledge.architecturerefine.process;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.model.CloudProvider;

public class CloudInformationHelper {
	
	public CloudProvider readXML(File input) throws JAXBException{
		JAXBContext jaxbContext = JAXBContext.newInstance(CloudProvider.class);
		 
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		CloudProvider p = (CloudProvider) jaxbUnmarshaller.unmarshal(input);		
		return p;
	}
	
}
