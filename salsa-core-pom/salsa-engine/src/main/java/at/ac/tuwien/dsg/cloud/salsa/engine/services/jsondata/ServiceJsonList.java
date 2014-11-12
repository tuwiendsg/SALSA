/*******************************************************************************
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group E184
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;

public class ServiceJsonList {
	List<ServiceInfo> services = new ArrayList<ServiceInfo>();
	
	public ServiceJsonList(String storagePath){
		File folder = new File(storagePath);
		FileFilter filter = new FileFilter() {			
			@Override
			public boolean accept(File pathname) {				
				if (pathname.getName().lastIndexOf('.') > 0){ // accept file without extension because we have .data files and tosca file
					return false;
				} else {
					return true;
				}
			}
		};
		List<File> fl = Arrays.asList(folder.listFiles(filter));		
		try {
		for (File file : fl) {			
			Date modifiedTime = new Date(file.lastModified());
			//CenterLogger.logger.debug("File modified time: "+file.lastModified());	
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd yyyy 'at' HH:mm");
			
			String modTime = sdf.format(modifiedTime);
			String serviceId = file.getName();
			String serviceName = "noname";
			String fileName = SalsaConfiguration.getServiceStorageDir()	+ File.separator + serviceId + ".data";
			try {
				CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(fileName);
				//CenterLogger.logger.debug("ListService:"+service.getName());
				if (service.getName() != null ){
					serviceName = service.getName();
				}
			} catch (IOException e) {
				EngineLogger.logger.error("Could not load service data: " + fileName);
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			ServiceInfo info = new ServiceInfo(serviceName, serviceId, modTime);
			services.add(info);
		}
		} catch (Exception e) {
			EngineLogger.logger.error(e.toString());
		}
		
	}
	
	public class ServiceInfo{
		String serviceName;
		String serviceId;
		String deployTime;
		public ServiceInfo(String name, String id, String deploytime) {
			serviceName = name;
			serviceId = id;
			deployTime = deploytime;
		}
		public String getServiceName() {
			return serviceName;
		}
		public String getServiceId() {
			return serviceId;
		}
		public String getDeployTime() {
			return deployTime;
		}
		
	}

	public List<ServiceInfo> getServicesList() {
		return services;
	}
	
}
