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
package at.ac.tuqien.dsg.cloud.salsa.salsa_center_services.jsondata;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.ac.tuqien.dsg.cloud.salsa.salsa_center_services.utils.CenterLogger;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaXmlProcess;
import at.ac.tuwien.dsg.cloud.tosca.extension.ToscaCapabilityString;


/**
 * The class contains information to be visualized as graph
 * @author hungld
 *
 */
public class ServiceJsonData {
	
	List<NodeData> nodes = new ArrayList<NodeData>();
	List<RelationShipData> links = new ArrayList<ServiceJsonData.RelationShipData>();
	
	public void addNode(NodeData node){
		this.nodes.add(node);
	}
	
	public class NodeData {
		String id;
		String state;		
		String type;
		Map<String,String> properties;
		List<NodeCapabilityData> capability = new ArrayList<ServiceJsonData.NodeCapabilityData>();
		public NodeData(String id, String state, String type){
			this.id=id;
			this.state=state;
			this.type=type;
		}
	
		public void setProperties(Map<String, String> properties) {
			this.properties = properties;
		}
	
		public void setCapability(List<NodeCapabilityData> capability) {
			this.capability = capability;
		}
		
	}
	
	public class NodeCapabilityData{
		String capaId;
		String value;
		public NodeCapabilityData(String id, String value){
			this.capaId=id;
			this.value=value;
		}
	}
	
	public class RelationShipData{
		String source;
		String target;
		String value;
		public RelationShipData(String sourceId, String targetId){
			this.source=sourceId;
			this.target=targetId;
		}
	}
	
	public void loadService(String xmlFileName){
		try {
			CenterLogger.logger.debug("\n\nStart loading service to parse to XML");
			TDefinitions def = ToscaXmlProcess.readToscaFile(xmlFileName);
			CenterLogger.logger.debug("ServicejsonData loaded service: "+def.getId());
			List<TNodeTemplate> lst = ToscaStructureQuery.getNodeTemplateList(def);
			for (TNodeTemplate nt : lst) {
				CenterLogger.logger.debug("Analizing node "+nt.getId());
				NodeData tmpNode = new NodeData(nt.getId(),nt.getState(),nt.getType().getLocalPart().toString());				
				if (nt.getCapabilities() != null){
					List<TCapability> capas = nt.getCapabilities().getCapability();
					for (TCapability capa : capas) {
						if (capa.getProperties() !=null){
							TEntityTemplate.Properties prop = capa.getProperties();
							ToscaCapabilityString s = (ToscaCapabilityString)prop.getAny();
							tmpNode.capability.add(new NodeCapabilityData(capa.getId(), s.getValue()));
						}
					}
				}
				nodes.add(tmpNode);
				CenterLogger.logger.debug("Add node "+nt.getId());
			}
			
			List<TRelationshipTemplate> lst2=ToscaStructureQuery.getRelationshipTemplateList(def);
			for (TRelationshipTemplate rel : lst2) {
				CenterLogger.logger.debug("Analizing relationship "+rel.getId());
				TEntityTemplate source=(TEntityTemplate)rel.getSourceElement().getRef();
				TEntityTemplate target=(TEntityTemplate)rel.getTargetElement().getRef();
				CenterLogger.logger.debug("Source id: "+source.getId());
				if (!source.getType().equals(TNodeTemplate.class)){	// if source is not a node, take the node
					source=ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(source, def);
				}
				if (!target.getType().equals(TNodeTemplate.class)){
					target=ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(target, def);
				}
				links.add(new RelationShipData(source.getId(), target.getId()));
				CenterLogger.logger.debug("Added relationship "+rel.getId());
			}
			
		} catch (Exception e) {
			CenterLogger.logger.debug("Error in loading service file: "+xmlFileName+". "+e);
		}
	}

	@Override
	public String toString() {
		return "ServiceJsonData [node=" + nodes + ", relationship="
				+ links + "]";
	}
	
	
	
}
