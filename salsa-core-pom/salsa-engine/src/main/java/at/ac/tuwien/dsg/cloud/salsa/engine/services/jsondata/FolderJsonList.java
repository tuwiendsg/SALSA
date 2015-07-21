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
package at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;


/**
 * List of file in a folder without filter. May add more information.
 * @author Duc-Hung Le
 *
 */
public class FolderJsonList {
	List<FileInfo> services = new ArrayList<FileInfo>();
	
	public FolderJsonList(String storagePath){
		File folder = new File(storagePath);		
		List<File> fl = Arrays.asList(folder.listFiles());		
		try {
		for (File file : fl) {			
			Date modifiedTime = new Date(file.lastModified());
			EngineLogger.logger.debug("File modified time: "+file.lastModified());	
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd yyyy 'at' HH:mm");
			FileInfo info = new FileInfo(file.getName(), sdf.format(modifiedTime));
			services.add(info);
		}
		} catch (Exception e) {
			EngineLogger.logger.error(e.toString());
		}
		
	}
	
	public class FileInfo{
		String serviceId;
		String uploadTime;
		public FileInfo(String id, String deploytime) {
			serviceId = id;
			uploadTime = deploytime;
		}
	}
}
