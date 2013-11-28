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
package at.ac.tuqien.dsg.cloud.salsa.salsa_center_services.jsondata;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import at.ac.tuqien.dsg.cloud.salsa.salsa_center_services.utils.CenterLogger;

public class ServiceJsonList {
	List<ServiceInfo> services = new ArrayList<ServiceInfo>();
	
	public ServiceJsonList(String storagePath){
		File folder = new File(storagePath);
		FileFilter filter = new FileFilter() {			
			@Override
			public boolean accept(File pathname) {				
				if (pathname.getName().lastIndexOf('.') > 0){ // accept file without extension
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
			CenterLogger.logger.debug("File modified time: "+file.lastModified());	
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd yyyy 'at' HH:mm");
			ServiceInfo info = new ServiceInfo(file.getName(), sdf.format(modifiedTime));
			services.add(info);			
		}
		} catch (Exception e) {
			CenterLogger.logger.error(e.toString());
		}
		
	}
	
	public class ServiceInfo{
		String serviceId;
		String deployTime;
		public ServiceInfo(String id, String deploytime) {
			serviceId = id;
			deployTime = deploytime;
		}
	}
}
