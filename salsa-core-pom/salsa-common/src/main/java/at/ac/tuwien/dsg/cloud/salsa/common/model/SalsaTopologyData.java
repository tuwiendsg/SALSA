package at.ac.tuwien.dsg.cloud.salsa.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "ComponentTopology")
@XmlSeeAlso({  
    SalsaComponentData.class,
    SalsaReplicaRelationship.class
})
public class SalsaTopologyData extends SalsaEntity {
	@XmlAttribute(name = "replica")
	int replica=0;
	
	@XmlElement(name = "component")
	List<SalsaComponentData> components = new ArrayList<>();
	
	@XmlElement(name = "relationships")
	SalsaReplicaRelationships relationships;
	
	public static class SalsaReplicaRelationships{
		@XmlElement(name = "relationship")
		List<SalsaReplicaRelationship> relList = new ArrayList<>();
		
		public void addRelationship(SalsaReplicaRelationship rel){
			relList.add(rel);
		}
	}
	
	public SalsaTopologyData(){
	}
	
	public void addComponent(SalsaComponentData component){
		if (components==null){
			components = new ArrayList<>();
		}
		this.components.add(component);
	}
	
	public SalsaComponentData getComponentById(String id){
		for (SalsaComponentData node : components) {
			if (node.getId().equals(id)){
				return node;
			}
		}
		return null;
	}
	
	public void removeComponent(SalsaComponentData component){
		this.components.remove(component);
	}

	public int getReplica() {
		return replica;
	}

	public void setReplica(int replica) {
		this.replica = replica;
	}

	public List<SalsaComponentData> getComponents() {
		return components;
	}
	
	public List<SalsaComponentData> getComponentsByType(SalsaEntityType type){
		List<SalsaComponentData> lst = new ArrayList<>();
		for (SalsaComponentData node : components) {
			if (SalsaEntityType.fromString(node.getType()) == type){
				lst.add(node);
			}
		}
		return lst;
	}

	public SalsaReplicaRelationships getRelationships() {
		return relationships;
	}

	public void setRelationships(SalsaReplicaRelationships relationships) {
		this.relationships = relationships;
	}
	
}
