package at.ac.tuwien.dsg.cloud.salsa.common.processes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaCloudServiceData;
import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaComponentData;
import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaComponentReplicaData;
import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaTopologyData;
import at.ac.tuwien.dsg.cloud.tosca.extension.SalsaInstanceDescription;

public class SalsaXmlDataProcess {
	
	public static void writeCloudServiceToFile(SalsaCloudServiceData service, String fileName){
		System.out.println("Writing Salsa service to file: "+fileName);
		try {
			File file = new File(fileName);
			JAXBContext jaxbContext = JAXBContext
					.newInstance(SalsaCloudServiceData.class,
							SalsaTopologyData.class,
							SalsaComponentData.class,
							SalsaComponentReplicaData.class,
							SalsaInstanceDescription.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(service, file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	
	public static SalsaCloudServiceData readSalsaServiceFile(String fileName)
			throws JAXBException, IOException {		
		JAXBContext context = JAXBContext.newInstance(
						SalsaCloudServiceData.class,
						SalsaTopologyData.class,
						SalsaComponentData.class,
						SalsaComponentReplicaData.class,
						SalsaInstanceDescription.class);
						
		Unmarshaller um = context.createUnmarshaller();
		SalsaCloudServiceData td = (SalsaCloudServiceData) um.unmarshal(new FileReader(fileName));
		return td;
	}
	
}
