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
package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;



public class CenterConfiguration {
	private static Properties configuration;
	static Logger logger;
	
	static {
		configuration = new Properties();
		try {
			InputStream is = CenterConfiguration.class.getClassLoader()
					.getResourceAsStream("salsa.center.properties");
			configuration.load(is);
			logger = Logger.getLogger("SalsaCenterLogger");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static String getServiceStoragePath(){
		return configuration.getProperty("SERVICE_STORAGE");
	}
	
	public static String getArtifactStoragePath(){
		return configuration.getProperty("ARTIFACT_STORAGE");
	}
	
	public static String getToscaTemplatePath(){
		return configuration.getProperty("TOSCA_TEMPLATE_STORAGE");
	}
}
