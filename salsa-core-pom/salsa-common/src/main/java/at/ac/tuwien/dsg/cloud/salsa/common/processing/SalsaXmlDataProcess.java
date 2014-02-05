package at.ac.tuwien.dsg.cloud.salsa.common.processing;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaCloudServiceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentInstanceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaReplicaRelationship;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaTopologyData;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapabilityString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription;

public class SalsaXmlDataProcess {
	
	public static void writeCloudServiceToFile(SalsaCloudServiceData service, String fileName){
		System.out.println("Writing Salsa service to file: "+fileName);
		try {
			File file = new File(fileName);
			// add all the model and model.data into the context
			JAXBContext jaxbContext = JAXBContext
					.newInstance(SalsaCloudServiceData.class,
							SalsaTopologyData.class,
							SalsaComponentData.class,
							SalsaComponentInstanceData.class,
							SalsaInstanceDescription.class,	// describe VM
							SalsaCapabilityString.class,
							SalsaReplicaRelationship.class);	// capability
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(service, file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	
	public static SalsaCloudServiceData readSalsaServiceFile(String fileName)
			throws JAXBException, IOException {		
		// add all the model and model.data into the context
		JAXBContext context = JAXBContext.newInstance(
						SalsaCloudServiceData.class,
						SalsaTopologyData.class,
						SalsaComponentData.class,
						SalsaComponentInstanceData.class,						
						SalsaInstanceDescription.class,	// describe VM
						SalsaCapabilityString.class,
						SalsaReplicaRelationship.class);	// capability
						
		Unmarshaller um = context.createUnmarshaller();
		SalsaCloudServiceData serviceData = (SalsaCloudServiceData) um.unmarshal(new FileReader(fileName));
		return serviceData;
	}
	
	public static SalsaCloudServiceData readSalsaServiceXml(String xml)
			throws JAXBException, IOException {		
		JAXBContext context = JAXBContext.newInstance(
						SalsaCloudServiceData.class,
						SalsaTopologyData.class,
						SalsaComponentData.class,
						SalsaComponentInstanceData.class,
						SalsaInstanceDescription.class);
						
		Unmarshaller um = context.createUnmarshaller();
		StringReader reader = new StringReader(xml);
		return (SalsaCloudServiceData) um.unmarshal(reader);	
	}
	
	
}
