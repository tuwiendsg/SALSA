package at.ac.tuwien.dsg.cloud.salsa.engine.impl;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TEntityTemplate.Properties;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TNodeTemplate.Capabilities;
import generated.oasis.tosca.TNodeTemplate.Requirements;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRelationshipTemplate.SourceElement;
import generated.oasis.tosca.TRelationshipTemplate.TargetElement;
import generated.oasis.tosca.TRequirement;
import generated.oasis.tosca.TTopologyTemplate;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.architecturerefine.DeploymentObject;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.architecturerefine.process.KnowledgeGraph;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescriptionFuzzy;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

/**
 * This class contains number of method for refining the relationships, properties 
 * @author Le Duc Hung
 *
 */
public class ToscaRefiner {
	
	TDefinitions toscaDef;
	KnowledgeGraph kgraph;
	final String BASETYPE = "os";
	int counter=0;
	
	public ToscaRefiner(TDefinitions def, KnowledgeGraph kgraph){
		this.toscaDef = def;
		this.kgraph = kgraph;
		//enrichHighLevelTosca();
	}
	
	public TDefinitions enrichHighLevelTosca(){
		// loop all ServiceTemplate Topology
		// TODO: implement
		
		TTopologyTemplate topo = ToscaStructureQuery.getFirstServiceTemplate(toscaDef).getTopologyTemplate();
		// Refine RelationShip
		List<TRelationshipTemplate> relas = ToscaStructureQuery.getRelationshipTemplateList(toscaDef);
		for (TRelationshipTemplate rela : relas) {
			createRelationshipWithCapabilityAndRequirement(rela);
		}
		
		// read all NodeTemplate of first ServiceTemplate
		List<TNodeTemplate> nodeLst = ToscaStructureQuery.getNodeTemplateList(toscaDef);
		
		for (TNodeTemplate node : nodeLst) {
			enrichOneNodeTemplate(node, topo, this.kgraph);
		}
		cleanEmptyProperties(nodeLst);		
		
		return null;
	}
	
	// check if this node need to be enriched ?
	private boolean needToBeEnriched(TEntityTemplate req, TTopologyTemplate topo){
		List<TRelationshipTemplate> relas = ToscaStructureQuery.getRelationshipTemplateList(topo);
		for (TRelationshipTemplate rela : relas) {
			//if (rela.getType().equals(SalsaRelationshipType.HOSTON.getRelationshipTypeString())){
				TEntityTemplate thisNodeInRela = (TEntityTemplate) rela.getTargetElement().getRef();
				if (thisNodeInRela.getId().equals(req.getId())){
					return false;	// not a leaf, has another node hosts it
				}
			//}
		}
		return true;
	}
	
	public void enrichOneNodeTemplate(TNodeTemplate node, TTopologyTemplate topo, KnowledgeGraph kgraph){
		// if this node have no requirement, no need to be enriched
		if (node.getRequirements() == null){
			return;
		}
		// iterate through requirements and extend a node if needed.
		ArrayList<TRequirement> reqs = (ArrayList<TRequirement>)node.getRequirements().getRequirement();		
		for (TRequirement req : reqs) {
			if (needToBeEnriched(req, topo)){
				DeploymentObject nextObj = kgraph.searchTargetForObject(node.getType().getLocalPart());	
				
				if (nextObj == null){
					return;				// have no knowledge to deploy requirement
				}	
				
				// create one node for the requirement of the target
				TNodeTemplate newNode = parseDeploymentObjectToNodeTemplate(nextObj, node.getId());
				TCapability newCapa = null;
				
				for (TCapability newCapa1 : newNode.getCapabilities().getCapability()) {
					if (newCapa1.getType().getLocalPart().equals(req.getType().getLocalPart())){		
						newCapa = newCapa1;
					}
				}
				// create a relationship for this node and new node (connect their capa and req)
				TRelationshipTemplate newRela = createNewRelationship(newCapa, req, node.getId()+".HOSTON."+newNode.getId());		
				
				// move the properties which not belong to it down.
				moveProperties(req, newNode, node.getType().getLocalPart());
				
				// add new node and new relationship to current topology
				topo.getNodeTemplateOrRelationshipTemplate().add(newNode);
				topo.getNodeTemplateOrRelationshipTemplate().add(newRela);
				
				// recursive for the extended node
				enrichOneNodeTemplate(newNode, topo, kgraph);
				
				ToscaXmlProcess.writeToscaDefinitionToFile(toscaDef, "/tmp/salsa/"+counter+".xml");
				counter++;
			} // end if
		} //end for	
		
	} // end method
	
	/*
	 * Mode properties which not belong to node1.requirement to node2
	 * node2.properties will be deleted and replace with new one
	 */
	private void moveProperties(TRequirement req1, TNodeTemplate node2, String keepType){
		if (req1.getProperties() == null || req1.getProperties().getAny() == null){
			return;
		}
		System.out.println("moving " + req1.getId() + " to " + node2.getId());
		SalsaMappingProperties p1 = (SalsaMappingProperties)req1.getProperties().getAny();
		
		SalsaMappingProperties p2 = new SalsaMappingProperties();
		List<SalsaMappingProperty> fixedListP1 = new ArrayList<>(p1.getProperties());		
		for (SalsaMappingProperty prop : fixedListP1) {
			if (!prop.getType().equals(keepType)){	// move from req.prop ==> node.prop				
				p2.getProperties().add(prop);
				p1.getProperties().remove(prop);
			}
		}
		Properties newProp = new Properties();
		newProp.setAny(p2);
		node2.setProperties(newProp);
		// move from node.prop ==> node.req.prop
		movePropertiesFromNodeToReq(node2);
	}
	
	private boolean movePropertiesFromNodeToReq(TNodeTemplate node){
		if (node.getProperties() == null || node.getRequirements() == null){
			return false;
		}
		SalsaMappingProperties props = (SalsaMappingProperties)node.getProperties().getAny();		
		for (TRequirement req : node.getRequirements().getRequirement()) {
			List<SalsaMappingProperty> fixedListP1 = new ArrayList<>(props.getProperties());
			for (SalsaMappingProperty p : fixedListP1) {			
				if (req.getType().getLocalPart().equals(p.getType())){
					if (req.getProperties() == null){
						req.setProperties(new Properties());
						SalsaMappingProperties newProps = new SalsaMappingProperties();
						req.getProperties().setAny(newProps);
					}
					SalsaMappingProperties reqP = (SalsaMappingProperties)req.getProperties().getAny();
					reqP.getProperties().add(p);
					props.getProperties().remove(p);
				}
			}
		}
		return false;
	}
	
	private void cleanEmptyProperties(List<TNodeTemplate> nodeLst){
		for (TNodeTemplate node : nodeLst) {
			if (node.getProperties() != null){
				SalsaMappingProperties maps = (SalsaMappingProperties)node.getProperties().getAny();
				if (maps.getProperties().size() == 0){
					node.setProperties(null);
				}
			}
			if (node.getRequirements() != null){
				for (TRequirement req : node.getRequirements().getRequirement()) {
					if (req.getProperties() != null){
						SalsaMappingProperties maps = (SalsaMappingProperties)req.getProperties().getAny();
						if (maps.getProperties().size() == 0){
							req.setProperties(null);
						}
					}
				}
			}
		}
	}
	
	
	
	/**
	 * Flow: node1 host node2, deploy node1 then node2, node1 is new which is target of node2
	 * @param capa
	 * @param req
	 * @return
	 */
	private TRelationshipTemplate createNewRelationship(TCapability capa, TRequirement req, String id){
		TRelationshipTemplate newRela = new TRelationshipTemplate();
		newRela.setId(id);
		newRela.setType(new QName(SalsaRelationshipType.HOSTON.getRelationshipTypeString()));
		SourceElement sou = new SourceElement();
		sou.setRef(capa);
		newRela.setSourceElement(sou);
		TargetElement tar = new TargetElement();
		tar.setRef(req);
		newRela.setTargetElement(tar);
		
		createRelationshipWithCapabilityAndRequirement(newRela);
		return newRela;
	}
	
	
	private TNodeTemplate parseDeploymentObjectToNodeTemplate(DeploymentObject obj, String fatherNodeId){
		TNodeTemplate node = new TNodeTemplate();
		// add basic info
		node.setId(obj.getName()+"_for_"+fatherNodeId);
		node.setType(new QName(obj.getName()));
		// add properties
		
			Capabilities newCapa = new Capabilities();
			TCapability capa = new TCapability();
			capa.setId(node.getId()+".capa");
			capa.setType(new QName(obj.getName()));
			newCapa.getCapability().add(capa);		
			node.setCapabilities(newCapa);
		
		
		if (obj.getDependencies() != null){
			Requirements newReq = new Requirements();
			for (String depen : obj.getDependencies().getDependencyList()){
				TRequirement req = new TRequirement();
				req.setId(node.getId()+"."+depen+".req");
				req.setType(new QName(depen));
				newReq.getRequirement().add(req);
			}
			node.setRequirements(newReq);			
		}
		
		
		return node;
	}
	
//	@Deprecated		
//	public void enrichOneNodeTemplate(TNodeTemplate node, TTopologyTemplate topo){
//		if (node.getProperties()==null){
//			return;
//		}
//				
//		// Ok, now having a Instance object, make a OPERATING_SYSTEM node and connect to this node
//		List<TEntityTemplate> entities = topo.getNodeTemplateOrRelationshipTemplate();
//		TNodeTemplate newOSNode = new TNodeTemplate();
//		
//		// add basic info
//		newOSNode.setId(node.getId()+"_OS");
//		newOSNode.setType(new QName(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString()));		
//		
//		// create new properties data
//		SalsaInstanceDescription_VM vm;
//		Object target = node.getProperties().getAny();
//		if (target.getClass().equals(SalsaInstanceDescriptionFuzzy.class)){
//			vm = matchingFuzzyNode(node);
//		} else {
//			vm = (SalsaInstanceDescription_VM) node.getProperties().getAny();
//		}
//		SalsaInstanceDescription_VM newProp = new SalsaInstanceDescription_VM();
//		newProp.setBaseImage(vm.getBaseImage());
//		newProp.setProvider(vm.getProvider());
//		newProp.setInstanceType(vm.getInstanceType());
//		
//		Properties propObject = new Properties();
//		propObject.setAny(newProp);
//		newOSNode.setProperties(propObject);
//		
//		entities.add(newOSNode);
//		
//		// Create new requirement, capability and add relationship for this node and newOsNode		
//		if (node.getRequirements() == null){
//			node.setRequirements(new Requirements());	
//		}		 
//		TRequirement thisReq = new TRequirement();
//		thisReq.setId(node.getId() + "_OS_Req");
//		thisReq.setName(node.getId() + "_OS_Req");
//		node.getRequirements().getRequirement().add(thisReq);
//		
//		newOSNode.setCapabilities(new Capabilities());
//		TCapability newCapa = new TCapability();
//		newCapa.setId(newOSNode.getId()+"_OS_Req");
//		newCapa.setName(newOSNode.getId()+"_OS_Req");
//		newOSNode.getCapabilities().getCapability().add(newCapa);
//		
//		TRelationshipTemplate rela = new TRelationshipTemplate();
//		rela.setId(node.getId() + "_2_" + newOSNode.getId());
//		rela.setName(node.getId() + "_2_" + newOSNode.getId());
//		rela.setType(new QName("HOSTON"));
//		SourceElement sou = new SourceElement();
//		sou.setRef(newCapa);
//		TargetElement tar = new TargetElement();
//		tar.setRef(thisReq);
//		rela.setSourceElement(sou);
//		rela.setTargetElement(tar);
//		
//		entities.add(rela);
//		
//	}
	
	/*
	 * Currently set default for the matching
	 */
	private SalsaInstanceDescription_VM matchingFuzzyNode(TNodeTemplate node){
		SalsaInstanceDescription_VM target = new SalsaInstanceDescription_VM();
		target.setProvider(SalsaCloudProviders.DSG_OPENSTACK.getCloudProviderString());
		target.setBaseImage("ami-00000163");
		target.setInstanceType("m1.small");
		return target;
		//node.getProperties().setAny(target);		
	}
	
	public void createComplexRelationship(TDefinitions def){
		List<TRelationshipTemplate> lst = ToscaStructureQuery.getRelationshipTemplateList(def);
		for (TRelationshipTemplate rela : lst) {
			createRelationshipWithCapabilityAndRequirement(rela);
		}
	}
	
	/*
	 * Add capability and requirement for nodes if relationship is at two node
	 * Node source: requirement, node target: capability
	 */
	private void createRelationshipWithCapabilityAndRequirement(TRelationshipTemplate rela){
		SourceElement testNode = rela.getSourceElement();
		if (testNode.getRef().getClass().equals(TNodeTemplate.class)){	// rela between 2 node
			TNodeTemplate node2 = (TNodeTemplate)rela.getSourceElement().getRef();
			TNodeTemplate node1 = (TNodeTemplate)rela.getTargetElement().getRef();
			if (node1.getCapabilities() == null){
				node1.setCapabilities(new Capabilities());
			}
			TCapability newCapa = new TCapability();
			newCapa.setId(node1.getId()+"_capa_for_"+node2.getId());
			newCapa.setName(node1.getId()+"_capa_for_"+node2.getId());			
			node1.getCapabilities().getCapability().add(newCapa);
			
			
			if (node2.getRequirements() == null){
				node2.setRequirements(new Requirements());	
			}	
			TRequirement newReq = new TRequirement();
			newReq.setId(node2.getId()+"_req_"+node1.getId());
			newReq.setName(node2.getId()+"_req_"+node1.getId());
			node2.getRequirements().getRequirement().add(newReq);
			
			SourceElement sou = new SourceElement();
			sou.setRef(newCapa);
			rela.setSourceElement(sou);
			TargetElement tar = new TargetElement();
			tar.setRef(newReq);
			rela.setTargetElement(tar);
			
			//topo.getNodeTemplateOrRelationshipTemplate().add(rela);
			
		}
	}
	
	
	public String toXML(){
		return ToscaXmlProcess.writeToscaDefinitionToXML(toscaDef);		
	}
	
	public void toXMLFile(String fileName){
		ToscaXmlProcess.writeToscaDefinitionToFile(toscaDef, fileName);
	}

	public TDefinitions getToscaDef() {
		return toscaDef;
	}

	public void setToscaDef(TDefinitions toscaDef) {
		this.toscaDef = toscaDef;
	}	
	
	
	
	
	
	
}
