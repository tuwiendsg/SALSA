package at.ac.tuwien.dsg.cloud.salsa.common.processing;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnitRelationship;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;

public class SalsaXmlDataProcess {
	
	public static void writeCloudServiceToFile(CloudService service, String fileName){
		System.out.println("Writing Salsa service to file: "+fileName);
		try {
			File file = new File(fileName);
			// add all the model and model.data into the context
			JAXBContext jaxbContext = JAXBContext
					.newInstance(CloudService.class,
							ServiceTopology.class,
							ServiceUnit.class,
							ServiceInstance.class,
							SalsaInstanceDescription_VM.class,	// describe VM
							SalsaCapaReqString.class,
							ServiceUnitRelationship.class);	// capability
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(service, file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	
	public static CloudService readSalsaServiceFile(String fileName)
			throws JAXBException, IOException {		
		// add all the model and model.data into the context
		JAXBContext context = JAXBContext.newInstance(
						CloudService.class,
						ServiceTopology.class,
						ServiceUnit.class,
						ServiceInstance.class,						
						SalsaInstanceDescription_VM.class,	// describe VM
						SalsaCapaReqString.class,
						ServiceUnitRelationship.class);	// capability
						
		Unmarshaller um = context.createUnmarshaller();
		CloudService serviceData = (CloudService) um.unmarshal(new FileReader(fileName));
		return serviceData;
	}
	
	public static CloudService readSalsaServiceXml(String xml)
			throws JAXBException, IOException {		
		JAXBContext context = JAXBContext.newInstance(
						CloudService.class,
						ServiceTopology.class,
						ServiceUnit.class,
						ServiceInstance.class,
						SalsaInstanceDescription_VM.class);
						
		Unmarshaller um = context.createUnmarshaller();
		StringReader reader = new StringReader(xml);
		return (CloudService) um.unmarshal(reader);	
	}
	
	
}
