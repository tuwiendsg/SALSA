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
package at.ac.tuwien.dsg.cloud.salsa.salsa_center_services.jsondata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentInstanceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaTopologyData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityType;
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
	List<ServiceJsonDataTree> children;
	boolean isAbstract=true;	// true: tosca node, false: instance node
	
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
		this.properties.put(key, value);
	}
	
	// convert component and all it stack
	public void loadData(SalsaComponentData data, int hostOnId, SalsaTopologyData topo){
//		logger.debug("Starting loading abstract node: " + data.getId());
		this.id = data.getId();
		this.setState(data.getState());
		this.isAbstract = true;
		List<SalsaComponentInstanceData> instances;
//		logger.debug("PI123 - nodeid:" + data.getId() +" which hoston " + hostOnId);
//		logger.debug("PI123 - The all instance list: " + data.getAllInstanceList().size());
//		logger.debug("PI123 - The hoston instance list: " + data.getInstanceHostOn(hostOnId).size());
		if (hostOnId < 0){
			instances = data.getAllInstanceList();
		} else {
			instances = data.getInstanceHostOn(hostOnId);
		}
		List<SalsaComponentData> hostOnCompos = new ArrayList<>();
		for (SalsaComponentData compo : topo.getComponents()) {
//			logger.debug("Comparing this id: " + data.getId() + " and other abstact node id: " + compo.getId() +" which has hosted id: " + compo.getHostedId() );
			if (compo.getHostedId().equals(data.getId())){	// compare ID on TOSCA node
				hostOnCompos.add(compo);
//				logger.debug("Then add to hostOnCompos !");
			}
		}
//		logger.debug("This abstract node has instances: " + instances.size());
//		logger.debug("This abstract node host on abstractnode: " + hostOnCompos.size());
		// updatethe list of instance to this "children" 
		for (SalsaComponentInstanceData instance : instances) {
//			logger.debug("Work with instance id: " + instance.getInstanceId());
			ServiceJsonDataTree oneChild = new ServiceJsonDataTree(instance.getInstanceId());
			addChild(oneChild);			
			// get the list of host on this components, which will come after the instances
			oneChild.loadData(instance, hostOnCompos, data, topo);
		}
		
	}
	
	// convert instance. abstractNode is for geting TYPE, knowing how to parse properties
	public void loadData(SalsaComponentInstanceData instance, List<SalsaComponentData> hostOnCompos, SalsaComponentData abstractNode, SalsaTopologyData topo){
		logger.debug("Adding instance node id: " + instance.getInstanceId());
		this.id = Integer.toString(instance.getInstanceId());
		this.setState(instance.getState());
		this.isAbstract = false;
		this.setState(instance.getState());
		//logger.debug("abstractNode.getType(): " + abstractNode.getType());
		SalsaEntityType type = SalsaEntityType.fromString(abstractNode.getType());
		//logger.debug("The instance node know it parent type is: " + type);
		// put properties
		if (type.equals(SalsaEntityType.OPERATING_SYSTEM) && instance.getProperties()!=null){
			logger.debug("Putting property of OS type");			
			SalsaInstanceDescription_VM props = (SalsaInstanceDescription_VM)instance.getProperties().getAny();
			this.setProperties(props.exportToMap());
		}
		// TODO: properties for others
							
		// recursive components which host on this node
		logger.debug("Will recursive by hostOnCompos.size(): " + hostOnCompos.size());
		for (SalsaComponentData compo : hostOnCompos) {
			logger.debug(" -- PI123 - Recursiving id: " + compo.getId());
			ServiceJsonDataTree newNode = new ServiceJsonDataTree();
			newNode.loadData(compo, instance.getInstanceId(), topo);
			addChild(newNode);
		}
	}
	
	
}
