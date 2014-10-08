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
package at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;



/**
 * The class contains information to be visualized as graph
 * @author hungld
 *
 */
public class ServiceJsonDataTree {
	String id;
	SalsaEntityState state;
	Map<String, String> properties;
	Object monitoring;
	public Object getMonitoring() {
		return monitoring;
	}
	public void setMonitoring(Object monitoring) {
		this.monitoring = monitoring;
	}

	List<ServiceJsonDataTree> children;
	boolean isAbstract=true;	// true: tosca node, false: instance node
	String nodeType;
	List<String> connectto = new ArrayList<>();
	
	static Logger logger;

	static {
		logger = Logger.getLogger("SalsaCenterLogger");
	}
	
	public ServiceJsonDataTree(){};
	public ServiceJsonDataTree(String id){
		this.id = id;
	}
	
	public ServiceJsonDataTree(int id){
		this.id = Integer.toString(id);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	public List<ServiceJsonDataTree> getChidren() {
		return children;
	}
	public void setChidren(List<ServiceJsonDataTree> chidren) {
		this.children = chidren;
	}
	
	public List<String> getConnectto() {
		return connectto;
	}
	public void setConnectto(List<String> connectto) {
		this.connectto = connectto;
	}

	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public boolean isAbstract() {
		return isAbstract;
	}
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}	
	
	public SalsaEntityState getState() {
		return state;
	}
	public void setState(SalsaEntityState state) {
		this.state = state;
	}
	
	
	public void addChild(ServiceJsonDataTree child){
		if (children==null){
			children = new ArrayList<>();
		}
		children.add(child);
	}
	
	public void removeChild(ServiceJsonDataTree child){
		children.remove(child);
	}
	
	public void removeChild(String id){
		for (ServiceJsonDataTree child : children) {
			if (child.getId().equals(id)){
				children.remove(child);
			}
		}
	}
	
	public void addProperty(String key, String value){
		if (this.properties == null){
			this.properties = new HashMap<String, String>();
		}
		this.properties.put(key, value);
	}
	
	// convert component and all it stack
	public void loadData(ServiceUnit data, int hostOnId, ServiceTopology topo){
		//logger.debug("Starting loading abstract node: " + data.getId());
		this.id = data.getId();
		this.setState(data.getState());
		this.isAbstract = true;
		this.setNodeType(data.getType());
		this.connectto=data.getConnecttoId();
		//logger.debug("On abstract, Connectto size: " + this.connectto.size());
				
		List<ServiceInstance> instances;
//		logger.debug("PI123 - nodeid:" + data.getId() +" which hoston " + hostOnId);
//		logger.debug("PI123 - The all instance list: " + data.getAllInstanceList().size());
//		logger.debug("PI123 - The hoston instance list: " + data.getInstanceHostOn(hostOnId).size());
		if (hostOnId < 0){
			instances = data.getInstancesList();
		} else {
			instances = data.getInstanceHostOn(hostOnId);
		}
		List<ServiceUnit> hostOnCompos = new ArrayList<>();
		for (ServiceUnit compo : topo.getComponents()) {
//			logger.debug("Comparing this id: " + data.getId() + " and other abstact node id: " + compo.getId() +" which has hosted id: " + compo.getHostedId() );
			if (compo.getHostedId().equals(data.getId())){	// compare ID on TOSCA node
				hostOnCompos.add(compo);
//				logger.debug("Then add to hostOnCompos !");
			}
		}
//		logger.debug("This abstract node has instances: " + instances.size());
//		logger.debug("This abstract node host on abstractnode: " + hostOnCompos.size());
		// updatethe list of instance to this "children" 
		for (ServiceInstance instance : instances) {
//			logger.debug("Work with instance id: " + instance.getInstanceId());
			ServiceJsonDataTree oneChild = new ServiceJsonDataTree(instance.getInstanceId());
			addChild(oneChild);			
			// get the list of host on this components, which will come after the instances
			oneChild.loadDataInstance(instance, hostOnCompos, data, topo);
		}
		
	}
	
	// convert instance. abstractNode is for geting TYPE, knowing how to parse properties
	public void loadDataInstance(ServiceInstance instance, List<ServiceUnit> hostOnCompos, ServiceUnit abstractNode, ServiceTopology topo){
		//logger.debug("Adding instance node id: " + instance.getInstanceId());
		this.id = abstractNode.getId()+"_"+Integer.toString(instance.getInstanceId());		
		this.setState(instance.getState());		
		this.isAbstract = false;
		this.setState(instance.getState());
		this.setMonitoring(instance.getMonitoring());
		
		this.setNodeType(abstractNode.getType());
		//logger.debug("Let check connectto ID ");
		if (!abstractNode.getConnecttoId().isEmpty()){
//			logger.debug("Inside the list ");
			for (String conId : abstractNode.getConnecttoId()) {
//				logger.debug("List item: " + conId);
				this.connectto.add(conId+"_0"); // all instance of this node connect to instance 0
//				logger.debug("Add done: " + conId+"_0");
			}
			
		}
		
		// set connectto links
		
		
		// if nodeType=SOFTWARE, change it into artifactType. so if it is os, leave there
		if (abstractNode.getArtifactType()!=null){
			if(!this.nodeType.equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
				this.nodeType=abstractNode.getArtifactType();
			}
		}
		
		//logger.debug("abstractNode.getType(): " + abstractNode.getType());
		SalsaEntityType type = SalsaEntityType.fromString(abstractNode.getType());
		//logger.debug("The instance node know it parent type is: " + type);
		// put properties
		if (type.equals(SalsaEntityType.OPERATING_SYSTEM) && instance.getProperties()!=null){
//			logger.debug("Putting property of OS type");			
			SalsaInstanceDescription_VM props = (SalsaInstanceDescription_VM)instance.getProperties().getAny();
			this.setProperties(props.exportToMap());
		}
		// TODO: properties for others
							
		// recursive components which host on this node
//		logger.debug("Will recursive by hostOnCompos.size(): " + hostOnCompos.size());
		for (ServiceUnit compo : hostOnCompos) {
//			logger.debug(" -- PI123 - Recursiving id: " + compo.getId());
			ServiceJsonDataTree newNode = new ServiceJsonDataTree();
			newNode.loadData(compo, instance.getInstanceId(), topo);
			addChild(newNode);
		}
	}
	
	// remove the abstract node, return instance children
	public List<ServiceJsonDataTree> compactData(){		
		List<ServiceJsonDataTree> cleanChildren=new ArrayList<>();
		if (this.children!=null) {		
			if (!this.getChidren().isEmpty()){								
				for (ServiceJsonDataTree child : this.getChidren()) {
					if (!child.isAbstract){
							// && !child.getNodeType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
						cleanChildren.add(child);
					}
				}
				
				List<ServiceJsonDataTree> newChildren=new ArrayList<>();			
				
				for (ServiceJsonDataTree child : this.getChidren()) {
					newChildren.addAll(child.compactData());
				}
				this.children.addAll(newChildren);
				
				List<ServiceJsonDataTree> toRemove=new ArrayList<>();
				for (ServiceJsonDataTree child : this.getChidren()) {
					if (child.isAbstract && !child.getNodeType().equals("TOPOLOGY")){
						toRemove.add(child);
					}
				}
				this.getChidren().removeAll(toRemove);
			}
		}
		return cleanChildren;
	}
	
	
	
	
	
}
