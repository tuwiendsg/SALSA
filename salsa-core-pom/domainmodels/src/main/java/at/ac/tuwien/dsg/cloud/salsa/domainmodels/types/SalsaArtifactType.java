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
package at.ac.tuwien.dsg.cloud.salsa.domainmodels.types;

/**
 * The artifact type:
 *  - sh: script based deployment. The script should deploy the app and exit after running.
 *  - binary: script or executable program which does not exit after running (e.g jar program)
 *  - apt: using apt-get to install
 *  - chef: only install chef client and let the real deployment for user
 *  - chef-solo: get the input by the software name in Chef Community repository and deploy
 *  - war: copy and remove the war artifact into Tomcat webapps folder.
 * @author Duc-Hung Le
 */
public enum SalsaArtifactType {
	sh("sh"),
        shcont("shcont"),
	apt("apt-get"),
	chef("chef"),
	chefSolo("chef-solo"),        
	war("war"),
        dockerfile("dockerfile"),
        metadata("metadata"),
        misc("misc");
	
	private String prop;
	
	private SalsaArtifactType(String propString){
		this.prop = propString;
	}

	public String getString() {
		return prop;
	}
	
	public static SalsaArtifactType fromString(String text) {
	    if (text != null) {
	      for (SalsaArtifactType b : SalsaArtifactType.values()) {
	        if (text.equalsIgnoreCase(b.getString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}