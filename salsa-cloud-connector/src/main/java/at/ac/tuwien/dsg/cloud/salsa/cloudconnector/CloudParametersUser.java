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
package at.ac.tuwien.dsg.cloud.salsa.cloudconnector;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 * Read the INI configuration file for cloud connectors
 * @author Duc-Hung Le
 *
 */
public class CloudParametersUser {
	String SEPARATED_STR = "@";
	List<CloudParameter> configList = new ArrayList<CloudParameter>();
	
	public CloudParametersUser(File iniFile){
		try {
			HierarchicalINIConfiguration iniConf = new HierarchicalINIConfiguration(iniFile);
			Set<String> setOfSections = iniConf.getSections();
			Iterator<String> sectionNames = setOfSections.iterator();
			
			while (sectionNames.hasNext()){
				CloudParameter cred = new CloudParameter();
				String sectionName = sectionNames.next().toString();
				// devide connector and site
				// e.g. lal.stratuslab, dsg.openstack
				cred.setConnector(sectionName.substring(sectionName.indexOf(SEPARATED_STR)+1));
				cred.setSite(sectionName.substring(0, sectionName.indexOf(SEPARATED_STR)));
				SubnodeConfiguration sObj = iniConf.getSection(sectionName);
				Iterator it = sObj.getKeys();
				while (it.hasNext()){
					Object key = it.next();
					cred.getParameter().put(key.toString(), sObj.getString(key.toString()));
				}
				configList.add(cred);				
			}			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public CloudParameter getParameter(String site, String connector){
		for (CloudParameter cre : configList) {
			if (cre.getConnector().equals(connector) && cre.getSite().equals(site)){
				return cre;
			}
		}
		return null;
	}
	
	@Deprecated
	public String getOneParameter(String site, String connector, String key){
		for (CloudParameter cre : configList) {
			if (cre.getConnector().equals(connector) && cre.getSite().equals(site)){
				return cre.getParameter().get(key);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		String s = "CloudParameters \n";
		for (CloudParameter cre : configList) {
			s += "Connector: " + cre.getConnector() + "\n";
			s += "Site: " + cre.getSite() + "\n";
			Map<String,String> mp = cre.getParameter();
			for (String str : mp.keySet()) {
				s += str + ": " + mp.get(str)+"\n";
			}
			s+="\n";
		}
		
		return s;
	}
	
	
	
}
