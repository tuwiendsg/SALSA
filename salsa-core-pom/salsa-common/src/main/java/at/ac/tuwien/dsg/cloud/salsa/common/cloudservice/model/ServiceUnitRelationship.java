package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaRelationshipType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Relationship")
@XmlSeeAlso({  
    ServiceTopology.class    
})
public class ServiceUnitRelationship {
	@XmlAttribute(name = "type")
	SalsaRelationshipType type;
	
	@XmlElement(name = "Source")
	String sourceId;
	@XmlElement(name = "Target")
	String targetId;
	
	public ServiceUnitRelationship(){}
	
	public ServiceUnitRelationship(String sourceid, String targetid){
		this.sourceId = sourceid;
		this.targetId = targetid;
	}
	
	public SalsaRelationshipType getType() {
		return type;
	}

	public void setType(SalsaRelationshipType type) {
		this.type = type;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	
	
		
}
