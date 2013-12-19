package at.ac.tuwien.dsg.cloud.salsa.engine.impl;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TCapabilityDefinition;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TEntityTemplate.Properties;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TNodeTemplate.Capabilities;
import generated.oasis.tosca.TNodeTemplate.Requirements;
import generated.oasis.tosca.TNodeType;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRelationshipTemplate.SourceElement;
import generated.oasis.tosca.TRelationshipTemplate.TargetElement;
import generated.oasis.tosca.TRequirement;
import generated.oasis.tosca.TRequirementDefinition;
import generated.oasis.tosca.TTopologyTemplate;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.process.KnowledgeGraph;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescriptionFuzzy;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.ToscaVMNodeTemplatePropertiesEntend;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

/**
 * This class contains number of method for enriching high level Tosca.
 * @author Le Duc Hung
 *
 */
public class ToscaEnricher {
	
	TDefinitions toscaDef;
	KnowledgeGraph kgraph;
	final String BASETYPE = "salsa-base";
	int counter=0;
	
	public ToscaEnricher(TDefinitions def, KnowledgeGraph kgraph){
		this.toscaDef = def;
		this.kgraph = kgraph;
		enrichHighLevelTosca();
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
		
		return null;
	}
	
	// check if this node need to be enriched ?
	private boolean needToBeEnriched(TEntityTemplate req, TTopologyTemplate topo){
//		if (req.getProperties() == null){
//			return false;		// no properties, then no enriched
//		}
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
		
//		// in case if relationship is between capabilities and requirements
//		if (node.getRequirements() == null){
//			return true;
//		}
//		List<TRequirement> reqs = node.getRequirements().getRequirement();
//		for (TRequirement req : reqs) {
//			boolean found = false;
//			for (TRelationshipTemplate rela : relas) {
//				TEntityTemplate hoster = (TEntityTemplate) rela.getTargetElement().getRef();
//				if (hoster.getId().equals(req.getId())){
//					found = true;	// found something hosts it
//				}
//			}
//			if (!found){		// if nothing hosts it
//				return true;	// that's mean, it's a leaf
//			}			
//		}		
//		return false;
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
				TNodeType nextObj = kgraph.searchTargetForObject(node.getType().getLocalPart());	
				
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
		if (req1.getProperties().getAny() == null){
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
	
	
	
	/**
	 * Flow: node1 host node2, deploy node1 then node2, node1 is new which is target of node2
	 * @param node1
	 * @param node2
	 * @return
	 */
	private TRelationshipTemplate createNewRelationship(TCapability node1, TRequirement node2, String id){
		TRelationshipTemplate newRela = new TRelationshipTemplate();
		newRela.setId(id);
		newRela.setType(new QName(SalsaRelationshipType.HOSTON.getRelationshipTypeString()));
		SourceElement sou = new SourceElement();
		sou.setRef(node1);
		newRela.setSourceElement(sou);
		TargetElement tar = new TargetElement();
		tar.setRef(node2);
		newRela.setTargetElement(tar);
		
		createRelationshipWithCapabilityAndRequirement(newRela);
		return newRela;
	}
	
	
	private TNodeTemplate parseDeploymentObjectToNodeTemplate(TNodeType obj, String fatherNodeId){
		TNodeTemplate node = new TNodeTemplate();
		// add basic info
		node.setId(obj.getName()+"_for_"+fatherNodeId);
		node.setType(new QName(obj.getName()));
		// add properties
		
		// add capabilities and requirements		
		if (obj.getCapabilityDefinitions() != null){
			Capabilities newCapa = new Capabilities();
			for (TCapabilityDefinition capaDef : obj.getCapabilityDefinitions().getCapabilityDefinition()) {
				TCapability capa = new TCapability();
				capa.setId(node.getId()+"."+capaDef.getName()+".capa");
				capa.setType(new QName(capaDef.getName()));
				newCapa.getCapability().add(capa);
			}
			node.setCapabilities(newCapa);			
		}
		
		if (obj.getRequirementDefinitions() != null){
			Requirements newReq = new Requirements();
			for (TRequirementDefinition reqDef : obj.getRequirementDefinitions().getRequirementDefinition()) {
				TRequirement req = new TRequirement();
				req.setId(node.getId()+"."+reqDef.getName()+".req");
				req.setType(new QName(reqDef.getName()));
				newReq.getRequirement().add(req);
			}
			node.setRequirements(newReq);
		}
		return node;
	}
	
	@Deprecated		
	public void enrichOneNodeTemplate(TNodeTemplate node, TTopologyTemplate topo){
		if (node.getProperties()==null){
			return;
		}
				
		// Ok, now having a Instance object, make a OPERATING_SYSTEM node and connect to this node
		List<TEntityTemplate> entities = topo.getNodeTemplateOrRelationshipTemplate();
		TNodeTemplate newOSNode = new TNodeTemplate();
		
		// add basic info
		newOSNode.setId(node.getId()+"_OS");
		newOSNode.setType(new QName(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString()));		
		
		// create new properties data
		SalsaInstanceDescription vm;
		Object target = node.getProperties().getAny();
		if (target.getClass().equals(SalsaInstanceDescriptionFuzzy.class)){
			vm = matchingFuzzyNode(node);
		} else {
			vm = (SalsaInstanceDescription) node.getProperties().getAny();
		}
		ToscaVMNodeTemplatePropertiesEntend newProp = new ToscaVMNodeTemplatePropertiesEntend();
		newProp.setBaseImage(vm.getBaseImage());
		newProp.setCloudProvider(vm.getProvider().getCloudProviderString());;
		newProp.setInstanceType(vm.getInstanceType());
		
		Properties propObject = new Properties();
		propObject.setAny(newProp);
		newOSNode.setProperties(propObject);
		
		entities.add(newOSNode);
		
		// Create new requirement, capability and add relationship for this node and newOsNode		
		if (node.getRequirements() == null){
			node.setRequirements(new Requirements());	
		}		 
		TRequirement thisReq = new TRequirement();
		thisReq.setId(node.getId() + "_OS_Req");
		thisReq.setName(node.getId() + "_OS_Req");
		node.getRequirements().getRequirement().add(thisReq);
		
		newOSNode.setCapabilities(new Capabilities());
		TCapability newCapa = new TCapability();
		newCapa.setId(newOSNode.getId()+"_OS_Req");
		newCapa.setName(newOSNode.getId()+"_OS_Req");
		newOSNode.getCapabilities().getCapability().add(newCapa);
		
		TRelationshipTemplate rela = new TRelationshipTemplate();
		rela.setId(node.getId() + "_2_" + newOSNode.getId());
		rela.setName(node.getId() + "_2_" + newOSNode.getId());
		rela.setType(new QName("HOSTON"));
		SourceElement sou = new SourceElement();
		sou.setRef(newCapa);
		TargetElement tar = new TargetElement();
		tar.setRef(thisReq);
		rela.setSourceElement(sou);
		rela.setTargetElement(tar);
		
		entities.add(rela);
		
	}
	
	/*
	 * Currently set default for the matching
	 */
	private SalsaInstanceDescription matchingFuzzyNode(TNodeTemplate node){
		SalsaInstanceDescription target = new SalsaInstanceDescription();
		target.setProvider(SalsaCloudProviders.OPENSTACK);
		target.setBaseImage("ami-00000163");
		target.setInstanceType("m1.small");
		return target;
		//node.getProperties().setAny(target);		
	}
	
	/*
	 * Add capability and requirement for nodes if relationship is at two node
	 */
	private void createRelationshipWithCapabilityAndRequirement(TRelationshipTemplate rela){
		SourceElement testNode = rela.getSourceElement();
		if (testNode.getRef().getClass().equals(TNodeTemplate.class)){	// rela between 2 node
			TNodeTemplate node1 = (TNodeTemplate)rela.getSourceElement().getRef();
			TNodeTemplate node2 = (TNodeTemplate)rela.getTargetElement().getRef();
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
