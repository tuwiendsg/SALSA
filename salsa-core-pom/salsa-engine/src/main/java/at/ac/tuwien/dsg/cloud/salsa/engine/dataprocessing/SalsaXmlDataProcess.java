/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.PrimitiveOperation;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnitRelationship;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_Docker;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_SystemProcess;
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
							SalsaInstanceDescription_VM.class,	// describe VM, Docker
                                                        SalsaInstanceDescription_Docker.class,
                                                        SalsaInstanceDescription_SystemProcess.class,
							SalsaCapaReqString.class,
							ServiceUnitRelationship.class,
							PrimitiveOperation.class);	// capability
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
                                                SalsaInstanceDescription_Docker.class,
                                                SalsaInstanceDescription_SystemProcess.class,
						SalsaCapaReqString.class,
						ServiceUnitRelationship.class,
                                                PrimitiveOperation.class);	// capability
						
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
						SalsaInstanceDescription_VM.class,
                                                SalsaInstanceDescription_Docker.class,
                                                SalsaInstanceDescription_SystemProcess.class,
                                                SalsaCapaReqString.class,
						ServiceUnitRelationship.class,
                                                PrimitiveOperation.class);
						
		Unmarshaller um = context.createUnmarshaller();
		StringReader reader = new StringReader(xml);
		return (CloudService) um.unmarshal(reader);	
	}
	
	
}
