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
package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "ServiceTopology")
@XmlSeeAlso({  
    ServiceUnit.class,
    ServiceUnitRelationship.class
})
public class ServiceTopology extends SalsaEntity {
	@XmlAttribute(name = "replica")
	int replica=0;
	
	@XmlElement(name = "ServiceUnit")
	List<ServiceUnit> components = new ArrayList<>();
	
	@XmlElement(name = "ServiceTopology")
	List<ServiceTopology> topologies = new ArrayList<>();
	
	@XmlElement(name = "Relationships")
	SalsaReplicaRelationships relationships;

	public static class SalsaReplicaRelationships{
		@XmlElement(name = "Relationship")
		List<ServiceUnitRelationship> relList = new ArrayList<>();
		
		public void addRelationship(ServiceUnitRelationship rel){
			relList.add(rel);
		}
	}
	
	public ServiceTopology(){
	}
	
	public void addComponent(ServiceUnit component){
		if (components==null){
			components = new ArrayList<>();
		}
		this.components.add(component);
	}
	
	public ServiceUnit getComponentById(String id){
		for (ServiceUnit node : components) {
			if (node.getId().equals(id)){
				return node;
			}
		}
		return null;
	}
	
	public void removeComponent(ServiceUnit component){
		this.components.remove(component);
	}

	public int getReplica() {
		return replica;
	}

	public void setReplica(int replica) {
		this.replica = replica;
	}

	public List<ServiceUnit> getComponents() {
		return components;
	}
	
	public List<ServiceUnit> getComponentsByType(SalsaEntityType type){
		List<ServiceUnit> lst = new ArrayList<>();
		for (ServiceUnit node : components) {
			if (SalsaEntityType.fromString(node.getType()) == type){
				lst.add(node);
			}
		}
		return lst;
	}

	public SalsaReplicaRelationships getRelationships() {
		return relationships;
	}
	
	public List<ServiceUnitRelationship> getRelationshipsList() {
		if (this.relationships != null){		
			return relationships.relList;
		}
		return null;
	}

	public void setRelationships(SalsaReplicaRelationships relationships) {
		this.relationships = relationships;
	}
	
}
