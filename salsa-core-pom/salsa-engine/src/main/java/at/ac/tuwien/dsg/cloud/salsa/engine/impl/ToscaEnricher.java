package at.ac.tuwien.dsg.cloud.salsa.engine.impl;

import generated.oasis.tosca.TArtifactTemplate;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TDeploymentArtifact;
import generated.oasis.tosca.TDeploymentArtifacts;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRequirement;
import generated.oasis.tosca.TAppliesTo.NodeTypeReference;
import generated.oasis.tosca.TEntityTemplate.Properties;
import generated.oasis.tosca.TRelationshipTemplate.SourceElement;
import generated.oasis.tosca.TRelationshipTemplate.TargetElement;
import generated.oasis.tosca.TServiceTemplate;
import generated.oasis.tosca.TTopologyTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import at.ac.tuwien.dsg.cloud.salsa.common.artifact.Artifact;
import at.ac.tuwien.dsg.cloud.salsa.common.artifact.Artifact.Operation;
import at.ac.tuwien.dsg.cloud.salsa.common.artifact.ArtifactFormat;
import at.ac.tuwien.dsg.cloud.salsa.common.artifact.Artifacts;
import at.ac.tuwien.dsg.cloud.salsa.common.artifact.Repositories;
import at.ac.tuwien.dsg.cloud.salsa.common.artifact.RepositoryFormat;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;


/**
 * This class contain methods to enrich the Tosca:
 *  - Generate dependency nodes
 *  - Reduce node to the infrastructure level at different format
 * @author hungld
 *
 */
public class ToscaEnricher {
	
	TDefinitions toscaDef;
	TDefinitions knowledgeBase;
	TTopologyTemplate knowledgeTopo;
	Artifacts artifactList = new Artifacts();
	Repositories repoList = new Repositories();
	
	public ToscaEnricher(TDefinitions def){
		this.toscaDef = def;
		try {
			knowledgeBase = ToscaXmlProcess.readToscaFile(ToscaEnricher.class.getResource(
				"/data/salsa.knowledge.xml").getFile());
			
			knowledgeTopo = ToscaStructureQuery.getFirstServiceTemplate(knowledgeBase).getTopologyTemplate();
			
			artifactList.importFromXML(ToscaEnricher.class.getResource("/data/salsa.artifacts.xml").getFile());
			repoList.importFromXML(ToscaEnricher.class.getResource("/data/salsa.repo.xml").getFile());			
			
		} catch (IOException e1){
			EngineLogger.logger.error("Not found knowledge base file. " + e1);
		} catch (JAXBException e2){
			EngineLogger.logger.error("Error when parsing knowledge base file. " + e2);
		}
	}
	
	
	public TDefinitions enrichHighLevelTosca(){
		// loop all ServiceTemplate Topology
		// TODO: implement
		
		TTopologyTemplate topo = ToscaStructureQuery.getFirstServiceTemplate(toscaDef).getTopologyTemplate();
		// Refine RelationShip
//		List<TRelationshipTemplate> relas = ToscaStructureQuery.getRelationshipTemplateList(toscaDef);
//		for (TRelationshipTemplate rela : relas) {
//			createRelationshipWithCapabilityAndRequirement(rela);
//		}
		
		// read all NodeTemplate of first ServiceTemplate
		List<TNodeTemplate> nodeLst = ToscaStructureQuery.getNodeTemplateList(toscaDef);
		
		for (TNodeTemplate node : nodeLst) {			
			enrichOneNodeTemplate(node, topo);
		}
		cleanEmptyProperties(nodeLst);
		
		
		return null;
	}
	
	
	
	/*
	 * This method generate the node that can host the current node.
	 * E.g: war -> tomcat -> jre -> VM
	 * 		software -> VM
	 * 		jar	-> jre -> VM
	 */
	int counter=0;
	public void enrichOneNodeTemplate(TNodeTemplate node, TTopologyTemplate topo){
		// if this node have no requirement, no need to be enriched
//		if (node.getRequirements() == null){
//			return;
//		}
		/* iterate through the knowledge topology and extend a node if needed.
		 *  - Retrieve new node, add an artifact reference, add into this.def
		 *  - Create new HOSTON relationship, add into this.def
		 *  - Query the artifact repo, create artifact template with id of the reference above
		 */
		
		//generateOperationsForNode(node);
		
		TNodeTemplate nextNode =  nextNewNode(node);
		if (nextNode == null){
			return; // no more extend
		}
		topo.getNodeTemplateOrRelationshipTemplate().add(nextNode);		
		
		Artifact art = this.artifactList.searchArtifact(nextNode.getId());
		nextNode.setType(new QName(nextNode.getId()));
		nextNode.setId(nextNode.getId() + "_OF_" + node.getId());		
		if (art != null){	// some node have artifact. some node like os doesn't
			String artId = "Artifact_" + nextNode.getId();
			
			// build the deployment artifact and attach to the new node
			TDeploymentArtifact dArt = new TDeploymentArtifact();
			dArt.setArtifactRef(new QName(artId));
			dArt.setArtifactType(new QName(art.getFirstMirror().getArtifactFormat().toString()));		
			dArt.setName("Artifact for " + nextNode.getId());
			TDeploymentArtifacts dArts = new TDeploymentArtifacts();
			dArts.getDeploymentArtifact().add(dArt);
			nextNode.setDeploymentArtifacts(dArts);
			
			
			// merge the repository info with above artifact to have full direct URL
			String repoEndpoint = repoList.getRepoEndpoint(art.getFirstMirror().getRepository());		
			TArtifactTemplate artTemp = new TArtifactTemplate();
			artTemp.setId(repoEndpoint+art.getFirstMirror().getReference());
					
			TDeploymentArtifact da = new TDeploymentArtifact();
			da.setArtifactType(new QName(art.getFirstMirror().getArtifactFormat().toString()));		
			da.setArtifactRef(new QName("Artifact_OF_" + nextNode.getId()));
			nextNode.getDeploymentArtifacts().getDeploymentArtifact().add(da);
			
			toscaDef.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(artTemp);
		}
		
		// relationship between node and newNode
		TRelationshipTemplate rela = new TRelationshipTemplate();
		rela.setId(node.getId() + "_HOSTON_" + nextNode.getId());
		rela.setType(new QName(SalsaRelationshipType.HOSTON.getRelationshipTypeString()));
		SourceElement source = new SourceElement();
		source.setRef(node);
		TargetElement target = new TargetElement();
		target.setRef(nextNode);
		rela.setSourceElement(source);
		rela.setTargetElement(target);
		topo.getNodeTemplateOrRelationshipTemplate().add(rela);
		
		// move properties
		moveProperties(node, nextNode, node.getType().getLocalPart());
		
		// Add the operations to that node properties. After the moving above, we have properties
		SalsaMappingProperties operProps = (SalsaMappingProperties)nextNode.getProperties().getAny();
		SalsaMappingProperties.SalsaMappingProperty map = new SalsaMappingProperties.SalsaMappingProperty();
		map.setPropType("operations");
		if (art!=null && art.getOperationList()!=null){
			for (Operation ope : art.getOperationList()) {
				map.put(ope.getOpName(), ope.getOpCommand());
			}
			operProps.getProperties().add(map);
		}
		
		//some debug
		ToscaXmlProcess.writeToscaDefinitionToFile(toscaDef, "/tmp/salsa/"+counter+".xml");
		
		// recursive
		enrichOneNodeTemplate(nextNode, topo);
		
		
		counter++;
		
		
//		ArrayList<TRequirement> reqs = (ArrayList<TRequirement>)node.getRequirements().getRequirement();		
//		for (TRequirement req : reqs) {
//			if (needToBeEnriched(req, topo)){
//				DeploymentObject nextObj = kgraph.searchTargetForObject(node.getType().getLocalPart());	
//				
//				if (nextObj == null){
//					return;				// have no knowledge to deploy requirement
//				}	
//				
//				// create one node for the requirement of the target
//				TNodeTemplate newNode = parseDeploymentObjectToNodeTemplate(nextObj, node.getId());
//				TCapability newCapa = null;
//				
//				for (TCapability newCapa1 : newNode.getCapabilities().getCapability()) {
//					if (newCapa1.getType().getLocalPart().equals(req.getType().getLocalPart())){		
//						newCapa = newCapa1;
//					}
//				}
//				// create a relationship for this node and new node (connect their capa and req)
//				TRelationshipTemplate newRela = createNewRelationship(newCapa, req, node.getId()+".HOSTON."+newNode.getId());		
//				
//				// move the properties which not belong to it down.
//				//moveProperties(req, newNode, node.getType().getLocalPart());
//				
//				// add new node and new relationship to current topology
//				topo.getNodeTemplateOrRelationshipTemplate().add(newNode);
//				topo.getNodeTemplateOrRelationshipTemplate().add(newRela);
//				
//				// recursive for the extended node
//				enrichOneNodeTemplate(newNode, topo, kgraph);
//				
//				ToscaXmlProcess.writeToscaDefinitionToFile(toscaDef, "/tmp/salsa/"+counter+".xml");
//				counter++;
//			} // end if
//		} //end for	
		
	} // end method
	
	
	private TNodeTemplate nextNewNode(TNodeTemplate node){
		// search on the knowledge the relationship which node is the source 
		List<TRelationshipTemplate> relaList = ToscaStructureQuery.getRelationshipTemplateList(knowledgeTopo);
		for (TRelationshipTemplate rela : relaList) {
			TNodeTemplate source = (TNodeTemplate) rela.getSourceElement().getRef();			
			if (source.getId().equals(node.getType().getLocalPart())){
				TNodeTemplate newNode = new TNodeTemplate();
				TNodeTemplate refNode = (TNodeTemplate) rela.getTargetElement().getRef(); 
				newNode.setId(refNode.getId());
				return newNode;
			}
		}
		return null;
	}
	
	
	
	/*
	 * Mode all properties which not belong to node1 to node2
	 * node2.properties will be deleted and replace with new one
	 */
	private void moveProperties(TNodeTemplate node1, TNodeTemplate node2, String keepType){
		if (node1.getProperties() == null || node1.getProperties().getAny() == null){
			return;
		}
		System.out.println("moving " + node1.getId() + " to " + node2.getId());
		SalsaMappingProperties p1 = (SalsaMappingProperties)node1.getProperties().getAny();
		
		SalsaMappingProperties p2 = new SalsaMappingProperties();
		List<SalsaMappingProperty> fixedListP1 = new ArrayList<>(p1.getProperties());		
		for (SalsaMappingProperty prop : fixedListP1) {
			if (!prop.getType().equals(keepType) && !prop.getType().equals("BundleConfig") && !prop.getType().equals("Operations")){	// move from req.prop ==> node.prop				
				p2.getProperties().add(prop);
				p1.getProperties().remove(prop);
			}
		}
		Properties newProp = new Properties();
		newProp.setAny(p2);
		node2.setProperties(newProp);
	}
	
	
	private void cleanEmptyProperties(List<TNodeTemplate> nodeLst){
		for (TNodeTemplate node : nodeLst) {
			if (node.getProperties() != null){
				SalsaMappingProperties maps = (SalsaMappingProperties)node.getProperties().getAny();
				if (maps.getProperties().size() == 0){
					node.setProperties(null);
				}
			}
		}
	}
	
//	// generate operation for supported artifact: sh, service, war
//	private SalsaMappingProperty generateOperationsForNode(TNodeTemplate node){		
//		if (node.getType().getLocalPart().equals(SalsaEntityType.OPERATING_SYSTEM)){
//			return null;
//		}
//		SalsaMappingProperty map = new SalsaMappingProperty();
//		String artifactType = node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactType().getLocalPart();
//		String artifactRef = ToscaStructureQuery.getDeployArtifactTemplateReferenceList(node, toscaDef).get(0);
//		String file = artifactRef.substring(artifactRef.lastIndexOf('/')+1);
//		switch(artifactType){
//		case "sh": {			
//			map.put("deploy", "bash " + file);			
//			break;
//		}
//		case "jar": {
//			map.put("deploy", "java -jar " + file);			
//			break;
//		}
//		}
//		return map;
//	}
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * Generate: knowledge base, artifact mapping, repositories
	 */
	public static void main(String[] args) throws Exception {
		// generate a set of node template of specific type SALSA support which use for the enricher
		TDefinitions defi = new TDefinitions();
		TServiceTemplate ser = new TServiceTemplate();
		defi.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(ser);
		ser.setTopologyTemplate(new TTopologyTemplate());		
		ser.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
		
		List<TEntityTemplate> en = ser.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();
		en.add(generateNode("os"));
		en.add(generateNode("bin"));
		en.add(generateNode("sh"));
		en.add(generateNode("jre"));
		en.add(generateNode("tomcat"));
		en.add(generateNode("war"));
		en.add(generateNode("image"));
		en.add(generateNode("software"));
		
		en.add(generateRela("software", "os", SalsaRelationshipType.HOSTON, defi));
		en.add(generateRela("sh", "os", SalsaRelationshipType.HOSTON, defi));
		en.add(generateRela("bin", "os", SalsaRelationshipType.HOSTON, defi));
		en.add(generateRela("jre", "os", SalsaRelationshipType.HOSTON, defi));
		en.add(generateRela("tomcat", "os", SalsaRelationshipType.HOSTON, defi));
		en.add(generateRela("war", "tomcat", SalsaRelationshipType.HOSTON, defi));
		en.add(generateRela("image", "os", SalsaRelationshipType.HOSTON, defi));
		
		en.add(generateRela("tomcat", "jre", SalsaRelationshipType.LOCAL, defi));	// remove tomcat-LOCAL-jre, add jre-hoston-os_tomcat
		
		ToscaXmlProcess.writeToscaElementToFile(defi, "/tmp/salsa.knowledge.xml");
		
		// Generate the repository
		Repositories repos = new Repositories();
		repos.addRepo("salsa-repo", RepositoryFormat.maven2, "http://128.130.172.215:8080/nexus-2.8.0-05/service/local/artifact/maven/redirect?");
		repos.addRepo("github", RepositoryFormat.git, "https://github.com/");
		repos.addRepo("dsg-openstack-image", RepositoryFormat.nova, "http://openstack.infosys.tuwien.ac.at:8774/v2/9fee130e30784e33a7d3c9bd4f5a60ce");
		repos.addRepo("local", RepositoryFormat.apt, "");
		repos.exportToXML("/tmp/salsa.repo.xml");
		
		// generate the artifact type
		Artifacts arts = new Artifacts();
		Artifact art = new Artifact("tomcat7", ArtifactFormat.deb, "local", "tomcat7");
		art.addOperation("deploy", "apt-get -y install tomcat7");
		art.addOperation("undeploy", "apt-get -y remove tomcat7");
		art.addOperation("start", "service tomcat7 start");
		art.addOperation("stop", "service tomcat7 start");
		art.addOperation("pid", "/var/run/tomcat7.pid");
		arts.add(art);
		
		art = new Artifact("apache2", ArtifactFormat.deb, "local", "apache2");
		art.addOperation("deploy", "apt-get -y install apache2");
		art.addOperation("undeploy", "apt-get -y remove apache2");
		art.addOperation("start", "service apache2 start");
		art.addOperation("stop", "service apache2 start");
		art.addOperation("pid", "/run/apache2.pid");
		arts.add(art);
		
		art = new Artifact("mela", ArtifactFormat.sh, "salsa-repo", "r=salsa-artifacts&g=mela-server&a=install-mela&v=LATEST&e=sh");
		art.addOperation("deploy", "install-mela.sh");
		art.addOperation("start", "/etc/init.d/mela-data-service start; /etc/init.d/mela-analysis-service start");
		art.addOperation("stop", "/etc/init.d/mela-data-service stop; /etc/init.d/mela-analysis-service stop");
		art.addOperation("pid", "/tmp/mela-analysis-service.pid");
		arts.add(art);
		
		//arts.add(new Artifact("hadoop", ArtifactFormat.sh, "salsa-repo", ""));
		arts.exportToXML("/tmp/salsa.artifacts.xml");
		
		
	}
	
	private static TRelationshipTemplate generateRela(String source, String target, SalsaRelationshipType type, TDefinitions def){
		TRelationshipTemplate rela = new TRelationshipTemplate();
		SourceElement s = new SourceElement();
		s.setRef(ToscaStructureQuery.getNodetemplateById(source, def));
		TargetElement t = new TargetElement();
		t.setRef(ToscaStructureQuery.getNodetemplateById(target, def));
		rela.setSourceElement(s);
		rela.setTargetElement(t);
		rela.setType(new QName(type.toString()));		
		return rela;
	}
	
	private static TNodeTemplate generateNode(String type){
		TNodeTemplate node = new TNodeTemplate();
		node.setId(type);	
		return node;
	}
	
	
}
