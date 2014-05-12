package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;


/**
 * 
 * This class acts as a container for all the information of Salsa Virtual Machine instances
 * 
 * @author Le Duc Hung
 * TODO: Unified instance type. Currently: use String.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "SalsaInstanceDescription")
public class SalsaInstanceDescription_Artifact {

	@XmlElement(name = "hosted")
	private String hosted;
	
	@XmlElement(name = "artifactType")
	private String artifactType;

	@XmlElement(name = "id")
	private String instanceId;

	@XmlElement(name = "state")
	private String state;	// running, stopped

	public SalsaInstanceDescription_Artifact(){
	}
	
	public SalsaInstanceDescription_Artifact(String hosted, String instanceId){
		this.hosted = hosted;
		this.instanceId = instanceId;
	}		

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}
	
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	public String getInstanceId() {
		return instanceId;
	}

	@Override
	public String toString() {
		return "SalsaInstanceDescription_Artifact [hosted=" + hosted
				+ ", artifactType=" + artifactType + ", instanceId="
				+ instanceId + ", state=" + state + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SalsaInstanceDescription_Artifact) {
			return instanceId.equals(((SalsaInstanceDescription_Artifact) obj)
					.getInstanceId());
		}
		return false;
	}
	
	
	public void updateFromMappingProperties(SalsaMappingProperties maps){
		for (SalsaMappingProperty	map : maps.getProperties()) {
			if (map.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
				this.hosted = map.get("hosted");
				this.artifactType = map.get("artifactType");
				this.instanceId = map.get("instanceId");								
			}
		}		
	}
	
	

}
