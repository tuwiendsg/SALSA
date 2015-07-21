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
package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * This class define instance states
 * @author Duc-Hung Le
 *
 */
@XmlType(name = "SalsaInstanceStateEnum")
@XmlEnum
public enum SalsaInstanceState {
	UNDEPLOYED("undeployed"),
	DEPLOYED("deployed"),
	STOPPED("stopped"),
	RUNNING("running"),
	ERROR("error");
	
	private String nodeState;
	
	private SalsaInstanceState(String nodeState){
		this.nodeState = nodeState;
	}
	
	public String getNodeStateString() {
		return nodeState;
	}
	
	public static SalsaInstanceState fromString(String text) {
	    if (text != null) {
	      for (SalsaInstanceState b : SalsaInstanceState.values()) {
	        if (text.equalsIgnoreCase(b.getNodeStateString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

		
}
