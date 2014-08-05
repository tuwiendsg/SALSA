package at.ac.tuwien.dsg.cloud.salsa.tosca.processing;

import generated.oasis.tosca.TArtifactReference;
import generated.oasis.tosca.TArtifactTemplate;
import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TDeploymentArtifact;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TExtensibleElements;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRequirement;
import generated.oasis.tosca.TServiceTemplate;
import generated.oasis.tosca.TTopologyTemplate;

import java.util.ArrayList;
import java.util.List;

public class ToscaStructureQuery {
	
	
//	public static TServiceTemplate getFirstServiceTemplate(TDefinitions def){
//		List<TExtensibleElements> lst=def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
//		for (TExtensibleElements element : lst) {
//			if (element.getClass().equals(TServiceTemplate.class)){
//				return (TServiceTemplate)element;
//			}
//		}
//		return null;		
//	}
	
	public static List<TServiceTemplate> getServiceTemplateList(TDefinitions def){
		List<TExtensibleElements> lst=def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
		List<TServiceTemplate> serviceLst = new ArrayList<TServiceTemplate>();
		for (TExtensibleElements element : lst) {
			if (element.getClass().equals(TServiceTemplate.class)){
				serviceLst.add((TServiceTemplate)element);
			}
		}
		return serviceLst;
	}
	
	public static TTopologyTemplate getTopologyTemplate(String topoID, TDefinitions def){
		List<TServiceTemplate> sers = getServiceTemplateList(def);
		for (TServiceTemplate ser : sers) {
			if (ser.getId().equals(topoID)){
				return ser.getTopologyTemplate();
			}
		}
		return null;
	}
	
		
	/**
	 * Extract NodeTemplate from Topology and put in to list
	 * @param ser
	 * @return
	 */
	public static List<TNodeTemplate> getNodeTemplateList(TServiceTemplate ser){
		if (ser == null){
			System.out.println("Some thing wrong, null TServiceTemplate.");
			return null;
		}
		TTopologyTemplate topo = ser.getTopologyTemplate();
		List<TEntityTemplate> lst= topo.getNodeTemplateOrRelationshipTemplate();
		List<TNodeTemplate> lst1=new ArrayList<TNodeTemplate>();
		for (TEntityTemplate entity : lst) {
			if (entity.getClass().equals(TNodeTemplate.class)){
				lst1.add((TNodeTemplate)entity);
			}
		}		
		return lst1;
	}
	
	
	/**
	 * Extract RelationshipTemplate from Topology and put in to list
	 * @param ser
	 * @return
	 */
	public static List<TRelationshipTemplate> getRelationshipTemplateList(TServiceTemplate ser){
		if (ser == null){
			System.out.println("Some thing wrong, null TServiceTemplate.");
			return null;
		}
		TTopologyTemplate topo = ser.getTopologyTemplate();		
		return getRelationshipTemplateList(topo);
	}
	
	public static List<TRelationshipTemplate> getRelationshipTemplateList(TTopologyTemplate topo){
		List<TEntityTemplate> lst= topo.getNodeTemplateOrRelationshipTemplate();
		List<TRelationshipTemplate> lst1=new ArrayList<TRelationshipTemplate>();
		for (TEntityTemplate entity : lst) {
			if (entity.getClass().equals(TRelationshipTemplate.class)){
				lst1.add((TRelationshipTemplate)entity);
			}
		}		
		return lst1;
	}
	
	/**
	 * Extract RelationshipTemplate from Topology and put in to list
	 * @param ser
	 * @return
	 */
	public static List<TRelationshipTemplate> getRelationshipTemplateList(String type, TServiceTemplate ser){
		if (ser == null){
			System.out.println("Some thing wrong, null TServiceTemplate.");
			return null;
		}
		TTopologyTemplate topo = ser.getTopologyTemplate();
		List<TEntityTemplate> lst= topo.getNodeTemplateOrRelationshipTemplate();
		List<TRelationshipTemplate> lst1=new ArrayList<TRelationshipTemplate>();
		for (TEntityTemplate entity : lst) {
			if (entity.getClass().equals(TRelationshipTemplate.class)){
				TRelationshipTemplate rel = (TRelationshipTemplate)entity;
				if (rel.getType().getLocalPart().equals(type)){
					lst1.add(rel);
				}
			}
		}		
		return lst1;
	}
	
	
	
	public static TEntityTemplate getRequirementOrCapabilityById(String id, TDefinitions def){
		List<TNodeTemplate> lst = getNodeTemplateList(def);
		for (TNodeTemplate node : lst) {
			if (node.getCapabilities() !=null){
				List<TCapability> lisReqCap = node.getCapabilities().getCapability();
				for (TCapability tcap : lisReqCap) {
					if (tcap.getId().equals(id)){ return tcap;}
				}
			}
			if (node.getRequirements() != null){
				List<TRequirement> lisReq = node.getRequirements().getRequirement();
				for (TRequirement treq : lisReq) {
					if (treq.getId().equals(id)){ return treq;}
				}
			}
		}
		return null;
	}
		
	
	/**
	 * Get list of NodeTemplate from first ServiceTemplate
	 * @param def
	 * @return
	 */
	public static List<TNodeTemplate> getNodeTemplateList(TDefinitions def){
		List<TExtensibleElements> serviceTemplates = def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
		List<TNodeTemplate> nodes = new ArrayList<>();
		for (TExtensibleElements st : serviceTemplates) {
			if (st.getClass().equals(TServiceTemplate.class)){
				TTopologyTemplate topo = ((TServiceTemplate) st).getTopologyTemplate();
				for (TEntityTemplate entity : topo.getNodeTemplateOrRelationshipTemplate()) {
					if (entity.getClass().equals(TNodeTemplate.class)){
						nodes.add((TNodeTemplate) entity);
					}					
				}
			}
		}
		return nodes;
	}
	
	/**
	 * Get list of RelationshipTemplate from all the ServiceTemplate
	 * @param def
	 * @return
	 */
	public static List<TRelationshipTemplate> getRelationshipTemplateList(TDefinitions def){
		List<TExtensibleElements> ees = def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
		List<TRelationshipTemplate> relas = new ArrayList<>();
		for (TExtensibleElements ee : ees) {
			if (ee.getClass().equals(TServiceTemplate.class)){
				TTopologyTemplate topo = ((TServiceTemplate) ee).getTopologyTemplate();
				for (TEntityTemplate entity : topo.getNodeTemplateOrRelationshipTemplate()) {
					if (entity.getClass().equals(TRelationshipTemplate.class)){
						relas.add((TRelationshipTemplate) entity);
					}					
				}
			}
		}
		return relas;
	}
	
	/**
	 * Get list of RelationshipTemplate from first ServiceTemplate
	 * @param def
	 * @return
	 */
	public static List<TRelationshipTemplate> getRelationshipTemplateList(String type, TDefinitions def){
		List<TExtensibleElements> serviceTemplates = def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
		List<TRelationshipTemplate> relas = new ArrayList<>();
		for (TExtensibleElements st : serviceTemplates) {
			if (st.getClass().equals(TServiceTemplate.class)){
				TTopologyTemplate topo = ((TServiceTemplate) st).getTopologyTemplate();
				for (TEntityTemplate entity : topo.getNodeTemplateOrRelationshipTemplate()) {
					if (entity.getClass().equals(TRelationshipTemplate.class) && entity.getType().getLocalPart().equals(type)){
						relas.add((TRelationshipTemplate) entity);
					}					
				}
			}
		}
		return relas;
	}
	
	
	public static TArtifactTemplate getArtifactTemplateById(String id, TDefinitions def){
		List<TExtensibleElements> lst = def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
		TArtifactTemplate art;
		for (TExtensibleElements ele : lst) {
			if (ele.getClass().equals(TArtifactTemplate.class)){
				art = (TArtifactTemplate)ele; 
				if (art.getId().equals(id)){
					return art;
				}
			}
		}
		return null;
	}
	
	public static TNodeTemplate getNodetemplateById(String id, TDefinitions def){		
		List<TNodeTemplate> lst = getNodeTemplateList(def);
		for (TNodeTemplate node : lst) {			
			if (node.getId().trim().equals(id.trim())){				
				return node;
			}			
		}
		return null;
	}
	
	
	/**
	 * check if there is a node with same ID, update it
	 * @param node
	 * @param def
	 * @return
	 */
	public static TDefinitions updateNoteTempplateById(TNodeTemplate node, TDefinitions def){
		TNodeTemplate oldNode = getNodetemplateById(node.getId(), def);
		if (oldNode != null){
			oldNode = node;			
		}
		return def;
	}
	
	/*
	 * Get all artifact URLs
	 */
	public static List<String> getDeployArtifactTemplateReferenceList(TNodeTemplate node, TDefinitions def){
		if (node.getDeploymentArtifacts() == null){
			return null;
		}
		List<TDeploymentArtifact> lst = node.getDeploymentArtifacts().getDeploymentArtifact();
		List<String> listOfFile = new ArrayList<String>();
		for (TDeploymentArtifact artiTemplate : lst) {	// list have 1 unit
			String artiTemplateId = artiTemplate.getArtifactRef().getLocalPart();
			List<TArtifactReference> lst1 = getArtifactTemplateById(artiTemplateId, def).getArtifactReferences().getArtifactReference();
			for (TArtifactReference artiref : lst1) {
				listOfFile.add(artiref.getReference());
			}
		}
		return listOfFile;
	}
	
		
	/*
	 * Use property name in ArtifactTemplate to define the name of script among artifacts
	 */
	public static TDeploymentArtifact getFirstDeployArtifactTemplate(TNodeTemplate node, TDefinitions def){
		if (node.getDeploymentArtifacts() == null) {
			return null;
		}
		List<TDeploymentArtifact> lst = node.getDeploymentArtifacts().getDeploymentArtifact();
		return lst.get(0);		
	}
	
	public static List<TNodeTemplate> getNodeTemplatesOfTypeList(String nodeType, TDefinitions def){
		List<TNodeTemplate> lst = new ArrayList<TNodeTemplate>();
		List<TNodeTemplate> allNode = getNodeTemplateList(def);
		for (TNodeTemplate node : allNode) {			
			if (node.getType().getLocalPart().toUpperCase().equals(nodeType.toUpperCase())){
				lst.add(node);
			}
		}
		return lst;
	}
	
	/**
	 * Give a node, give back a chain of nodes of same RelationShipType
	 * E.g: Get all nodes HOSTON the input node VM, result could be: container, Artifact1 (VM itself is not included)  
	 * @param relationShipType
	 * @param node
	 * @param def
	 * @return List of chain of node
	 */
	public static List<TNodeTemplate> getNodeTemplateWithRelationshipChain(
			String relationShipType, TNodeTemplate node, TDefinitions def) {
		List<TNodeTemplate> lst = new ArrayList<TNodeTemplate>();
		
		List<TRelationshipTemplate> lstrel = getRelationshipTemplateList(relationShipType, def);
		
		System.out.println("SEE NODES CONNECTION");
		for (TRelationshipTemplate rel:lstrel){
			System.out.println("Node: " + node.getId() + ". rel: " + rel.getId());
			TEntityTemplate entity = (TEntityTemplate) rel.getTargetElement().getRef();
			System.out.println("Entity: " + entity.getId());
			if (node.getId().equals(entity.getId()) && entity.getClass().equals(TNodeTemplate.class)){
				System.out.println("Node: " + node.getId() + ". rel: " + rel.getId());
				TNodeTemplate targetNode = (TNodeTemplate) rel.getSourceElement().getRef();
				lst.add(targetNode);
				System.out.println("List size: " + lst.size());
				lst.addAll(getNodeTemplateWithRelationshipChain(relationShipType, targetNode, def)); // recursive
			}
		}
		
		if (node.getCapabilities()==null){
			return lst;
		}		
		List<TCapability> nodeCaps = node.getCapabilities().getCapability();
		System.out.println("SEE CAP-REQ CONNECTION");
		for (TCapability cap : nodeCaps) {
			System.out.println("Node: " + node.getId() + ". Capa: " + cap.getId());
			for (TRelationshipTemplate rel : lstrel) {
				System.out.println("Node: " + node.getId() + ". rel: " + rel.getId());				
				TEntityTemplate relcap = (TEntityTemplate)rel.getSourceElement().getRef();
				if (cap.getId().equals(relcap.getId()) && cap.getClass().equals(TCapability.class)) {					
					TRequirement relreq = (TRequirement)rel.getTargetElement().getRef();
					TNodeTemplate neibor=getNodetemplateOfRequirementOrCapability(relreq.getId(), def);
					lst.add(neibor);
					System.out.println("List size: " + lst.size());
					lst.addAll(getNodeTemplateWithRelationshipChain(relationShipType, neibor, def)); // recursive
				}
			}
		}
		
		return lst;		
	}
	
	public static TNodeTemplate getHostOnNode(TNodeTemplate node, TDefinitions def){
		List<TRelationshipTemplate> relas = getRelationshipTemplateList(def);
		for (TRelationshipTemplate rela : relas) {
			if (rela.getSourceElement().getRef().getClass().equals(TNodeTemplate.class)){
				TNodeTemplate source = (TNodeTemplate)rela.getSourceElement().getRef();
				if (source.getId().equals(node.getId())){
					return (TNodeTemplate)rela.getTargetElement().getRef(); 
				}
			}			
		}
		return null;
				
		
	}
	
	
	/**
	 * Get NodeTemplate which have Requirement or Capability
	 * @param reqOrCap
	 * @param def
	 * @return Node or null of not found
	 */
	public static TNodeTemplate getNodetemplateOfRequirementOrCapability(TEntityTemplate reqOrCap, TDefinitions def){
		List<TNodeTemplate> lst = getNodeTemplateList(def);
		List<TEntityTemplate> lst1 = new ArrayList<TEntityTemplate>();
		for (TNodeTemplate node : lst) {
			if (node.getRequirements()!=null){
				lst1.addAll(node.getRequirements().getRequirement());
			}
			if (node.getCapabilities()!=null){
				lst1.addAll(node.getCapabilities().getCapability());
			}			
			for (TEntityTemplate entity : lst1) {
				if (entity.equals(reqOrCap)){
					return node;
				}
			}			
		}
		return null;
	}
	
	/**
	 * Get NodeTemplate which have Requirement or Capability
	 * @param reqOrCapId
	 * @param def
	 * @return Node or null of not found
	 */
	public static TNodeTemplate getNodetemplateOfRequirementOrCapability(String reqOrCapId, TDefinitions def){
		List<TNodeTemplate> lst = getNodeTemplateList(def);
		for (TNodeTemplate node : lst) {
			if (node.getCapabilities()!=null){
				List<TCapability> lstcap = node.getCapabilities().getCapability();
				for (TEntityTemplate entity : lstcap) {
					if (entity.getId().equals(reqOrCapId)){
						return node;
					}
				}
			}			
			if (node.getRequirements()!=null){
				List<TRequirement> lstreq = node.getRequirements().getRequirement();
				for (TEntityTemplate entity : lstreq) {
					if (entity.getId().equals(reqOrCapId)){
						return node;
					}
				}
			}
		}
		return null;
	}
		
	
	/**
	 * Get the relationship between 2 node. Node1: capability, node2: requirement
	 * @param node1
	 * @param node2
	 * @param def
	 * @return The relationship or null if not found
	 */
	public static TRelationshipTemplate getRelationshipBetweenTwoNode(TNodeTemplate node1, TNodeTemplate node2, TDefinitions def){
		List<TRelationshipTemplate> lstrel = getRelationshipTemplateList(def);
		for (TRelationshipTemplate rel : lstrel) {
			if (node1.getCapabilities() != null && node2.getCapabilities() != null){
				// Relationship must be (node1.capability,node2.requirement)
				if (node1.getCapabilities().getCapability().contains((TCapability)getRequirementOrCapabilityById((String)rel.getSourceElement().getRef(), def))
				 && node2.getRequirements().getRequirement().contains((TRequirement)getRequirementOrCapabilityById((String)rel.getTargetElement().getRef(), def))){
					return rel;
				}
			}
		}
		return null;
	}
	
	public static TRelationshipTemplate getRelationshipBetweenTwoCapaReq(TCapability capa, TRequirement req, TDefinitions def){
		List<TRelationshipTemplate> lstrel = getRelationshipTemplateList(def);
		System.out.println("TESTING capa: " + capa.getId() +" and req: " + req.getId());
		for (TRelationshipTemplate rel : lstrel) {
			System.out.println("RELA: "+rel.getId());
			if (rel.getSourceElement().getRef().equals(capa) && rel.getTargetElement().getRef().equals(req)){
				return rel;
			}
		}
		return null;
	}
	
	public static TCapability getCapabilitySuitsRequirement(TRequirement req, TDefinitions def){
		List<TRelationshipTemplate> rels=getRelationshipTemplateList(def);
		for (TRelationshipTemplate rel : rels) {
			TEntityTemplate target = (TEntityTemplate)rel.getTargetElement().getRef();
			if (target.getId().equals(req.getId())){
				return (TCapability) rel.getSourceElement().getRef();
			}
		}
		return null;
	}
	
	public static TRequirement getRequirementSuitsCapability(TCapability capa, TDefinitions def){
		List<TRelationshipTemplate> rels=getRelationshipTemplateList(def);
		for (TRelationshipTemplate rel : rels) {
			TEntityTemplate source = (TEntityTemplate)rel.getSourceElement().getRef();
			if (source.getId().equals(capa.getId())){
				return (TRequirement) rel.getTargetElement().getRef();
			}
		}
		return null;
	}
	
	public static TTopologyTemplate getTopologyTemplateOfNode(TNodeTemplate node, TDefinitions def){
		List<TExtensibleElements> eles = def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
		for (TExtensibleElements element : eles) {
			if (element.getClass().equals(TServiceTemplate.class)){
				TServiceTemplate ser = (TServiceTemplate) element;
				List<TEntityTemplate> entities = ser.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
				for (TEntityTemplate entity : entities) {
					if (entity.getId().equals(node.getId())){
						return ser.getTopologyTemplate();
					}
				}
			}			
		}
		return null;
	}
	
}
