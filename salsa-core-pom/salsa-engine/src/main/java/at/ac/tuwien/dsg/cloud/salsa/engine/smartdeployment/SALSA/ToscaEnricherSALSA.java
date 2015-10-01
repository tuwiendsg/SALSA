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
package at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.SALSA;

import generated.oasis.tosca.TArtifactReference;
import generated.oasis.tosca.TArtifactTemplate;
import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TDeploymentArtifact;
import generated.oasis.tosca.TDeploymentArtifacts;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TEntityTemplate.Properties;
import generated.oasis.tosca.TExtensibleElements;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TNodeTemplate.Capabilities;
import generated.oasis.tosca.TNodeTemplate.Requirements;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRelationshipTemplate.SourceElement;
import generated.oasis.tosca.TRelationshipTemplate.TargetElement;
import generated.oasis.tosca.TRequirement;
import generated.oasis.tosca.TServiceTemplate;
import generated.oasis.tosca.TTopologyTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import at.ac.tuwien.dsg.cloud.salsa.common.artifact.Artifact;
import at.ac.tuwien.dsg.cloud.salsa.common.artifact.ArtifactFormat;
import at.ac.tuwien.dsg.cloud.salsa.common.artifact.Artifacts;
import at.ac.tuwien.dsg.cloud.salsa.common.artifact.Repositories;
import at.ac.tuwien.dsg.cloud.salsa.common.artifact.RepositoryFormat;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.ServicedataProcessingException;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.ToscaXmlProcess;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contain methods to enrich the Tosca: - Generate dependency nodes - Reduce node to the infrastructure level at different format
 *
 * @author Duc-Hung Le
 *
 */
public class ToscaEnricherSALSA {

    TDefinitions toscaDef;
    TDefinitions knowledgeBase;
    TTopologyTemplate knowledgeTopo;
    Artifacts artifactList = new Artifacts();
    Repositories repoList = new Repositories();

    public ToscaEnricherSALSA(TDefinitions def) {
        this.toscaDef = def;
        try {
            knowledgeBase = ToscaXmlProcess.readToscaFile(ToscaEnricherSALSA.class
                    .getResource("/data/salsa.knowledge.xml").getFile());

            knowledgeTopo = ((TServiceTemplate) knowledgeBase.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0)).getTopologyTemplate();

            artifactList.importFromXML(ToscaEnricherSALSA.class.getResource("/data/salsa.artifacts.xml").getFile());
            repoList.importFromXML(ToscaEnricherSALSA.class.getResource("/data/salsa.repo.xml").getFile());

        } catch (IOException e1) {
            EngineLogger.logger.error("Not found knowledge base file. " + e1);
        } catch (JAXBException e2) {
            EngineLogger.logger.error("Error when parsing knowledge base file. " + e2);
        }
    }

    public TDefinitions enrichHighLevelTosca() throws SalsaException {
        // TODO: implement the looping of all ServiceTemplate Topology

        List<TExtensibleElements> ees = toscaDef.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        for (TExtensibleElements ee : ees) {
            if (ee.getClass().equals(TServiceTemplate.class)) {
                System.out.println("Enriching in: " + ((TServiceTemplate) ee).getId());

                TTopologyTemplate topo = ((TServiceTemplate) ee).getTopologyTemplate();
                //List<TEntityTemplate> nodeLst = topo.getNodeTemplateOrRelationshipTemplate();
                List<TNodeTemplate> nodeLst = ToscaStructureQuery.getNodeTemplateList((TServiceTemplate) ee);

				// read all NodeTemplate of the ServiceTemplate
                //List<TNodeTemplate> nodeLst = ToscaStructureQuery	.getNodeTemplateList(toscaDef);
                for (TEntityTemplate node : nodeLst) {
                    System.out.println("NODE: " + node.getId());
                    if (node.getClass().equals(TNodeTemplate.class)) {
                        addHostonNodeForOneNodeTemplate((TNodeTemplate) node, topo);
                    }
                }
                //cleanEmptyProperties(nodeLst);
                cleanLOCALRelationship(topo);

                enrichArtifactRepo(SalsaConfiguration.getRepoPrefix() + "/" + toscaDef.getId());
                addOSNodeConfig(topo);
                createComplexRelationship(SalsaRelationshipType.CONNECTTO, this.toscaDef);
            }
        }

		//TTopologyTemplate topo = ((TServiceTemplate) toscaDef.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0)).getTopologyTemplate();
        return null;
    }

    /*
     * This method generate the node that can host the current node. E.g: war ->
     * tomcat -> jre -> VM software -> VM jar -> jre -> VM
     */
    int counter = 0;

    public TNodeTemplate addHostonNodeForOneNodeTemplate(TNodeTemplate node, TTopologyTemplate topo) throws SalsaException{

        /* iterate through the knowledge topology and extend a node if needed.
         *  - Retrieve new node, add an artifact reference, add into this.def
         *  - Create new HOSTON relationship, add into this.def
         *  - Query the artifact repo, create artifact template with id of the reference above
         *  
         */
        EngineLogger.logger.debug("Enriching node: " + node.getId());

        // find if there is a node that host this node, so it doesn't need to be generated
        TNodeTemplate hostedNode = getHostedNode(node, topo);
        if (hostedNode != null) {
            System.out.println("This node already has a node that hosts it: " + hostedNode.getId());
            return hostedNode;
        }

        TNodeTemplate nextNode = null;

        // if there are some LOCAL relationship, and we deploy on the same target
        List<TNodeTemplate> localNodes = getLocalNode(node, topo);
        for (TNodeTemplate localNode : localNodes) {
            hostedNode = getHostedNode(localNode, topo);
            if (hostedNode != null) {
                System.out.println("Found a LOCAL rela between 2 node: " + localNode.getId() + " and " + node.getId());
                nextNode = hostedNode;
            }
        }

        // if we found no hosted node, try to create a new node
        if (nextNode == null) {
            nextNode = nextNewNode(node);

            if (nextNode == null) {
                System.out.println("This node is OS type, no more extending");
                return null; // no more extending.
            }

            EngineLogger.logger.debug("Generated a next node of type: " + nextNode.getId());
            topo.getNodeTemplateOrRelationshipTemplate().add(nextNode);

            Artifact art = this.artifactList.searchArtifact(nextNode.getId());
            // refine some fields
            nextNode.setType(new QName(nextNode.getId()));
            nextNode.setId(nextNode.getId() + "_OF_" + node.getId());
//			nextNode.setMinInstances(1);
//			nextNode.setMaxInstances("unbounded");
            if (node.getReference() != null) {
                String[] ref = node.getReference().split("/");
                nextNode.setReference(ref[0] + "/" + nextNode.getId());
            }

            if (art != null) {	// some node have artifact. some node like os doesn't
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
                artTemp.setId(repoEndpoint + art.getFirstMirror().getReference());

                TDeploymentArtifact da = new TDeploymentArtifact();
                da.setArtifactType(new QName(art.getFirstMirror().getArtifactFormat().toString()));
                da.setArtifactRef(new QName("Artifact_OF_" + nextNode.getId()));
                nextNode.getDeploymentArtifacts().getDeploymentArtifact().add(da);

                toscaDef.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(artTemp);
            }
        }

        // relationship between node and newNode
        TRelationshipTemplate rela = createNewRela(node, nextNode);
        topo.getNodeTemplateOrRelationshipTemplate().add(rela);

        // move properties
        moveProperties(node, nextNode, node.getType().getLocalPart());

        // Add the operations to that node properties. After the moving above, we have properties
        if (nextNode.getProperties() == null) {
            nextNode.setProperties(new Properties());
        }

        try {
            //		SalsaMappingProperties operProps = (SalsaMappingProperties)nextNode.getProperties().getAny();
//		SalsaMappingProperties.SalsaMappingProperty map = new SalsaMappingProperties.SalsaMappingProperty();
//		map.setPropType("operations");
//		if (art!=null && art.getOperationList()!=null){
//			for (Operation ope : art.getOperationList()) {
//				map.put(ope.getOpName(), ope.getOpCommand());
//			}
//			operProps.getProperties().add(map);
//		}
            //some debug
            ToscaXmlProcess.writeToscaDefinitionToFile(toscaDef, "/tmp/salsa/" + counter + ".xml");
        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(toscaDef.getId(), ex);
        }

        // recursive
        addHostonNodeForOneNodeTemplate(nextNode, topo);

        counter++;
        return nextNode;
    } // end method host on adding

    public void addLocalDependencyNodeForOneNodeTemplate(TNodeTemplate node, TTopologyTemplate topo) {
        EngineLogger.logger.debug("Enriching node: " + node.getId());
        List<TNodeTemplate> lst = getLocalNode(node, topo);
        for (TNodeTemplate nextNode : lst) {
            EngineLogger.logger.debug("Generated a local node of type: " + nextNode.getId());
            topo.getNodeTemplateOrRelationshipTemplate().add(nextNode);

            Artifact art = this.artifactList.searchArtifact(nextNode.getId());
            // refine some fields
            nextNode.setType(new QName(nextNode.getId()));
            nextNode.setId(nextNode.getId() + "_OF_" + node.getId());
            if (node.getReference() != null) {
                String[] ref = node.getReference().split("/");
                nextNode.setReference(ref[0] + "/" + nextNode.getId());
            }

            if (art != null) {	// some node have artifact. some node like os doesn't
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
                artTemp.setId(repoEndpoint + art.getFirstMirror().getReference());

                TDeploymentArtifact da = new TDeploymentArtifact();
                da.setArtifactType(new QName(art.getFirstMirror().getArtifactFormat().toString()));
                da.setArtifactRef(new QName("Artifact_OF_" + nextNode.getId()));
                nextNode.getDeploymentArtifacts().getDeploymentArtifact().add(da);

                toscaDef.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(artTemp);
            }

            // relationship between node and newNode
            TRelationshipTemplate rela = createNewRela(node, nextNode);
            topo.getNodeTemplateOrRelationshipTemplate().add(rela);

        }

    }

    private TRelationshipTemplate createNewRela(TNodeTemplate node, TNodeTemplate nextNode) {
        System.out.println("Create new Relationship between: " + node.getId() + " and " + nextNode.getId());
        TRelationshipTemplate rela = new TRelationshipTemplate();
        rela.setId(node.getId() + "_HOSTON_" + nextNode.getId());
        rela.setType(new QName(SalsaRelationshipType.HOSTON.getRelationshipTypeString()));
        SourceElement source = new SourceElement();
        source.setRef(node);
        TargetElement target = new TargetElement();
        target.setRef(nextNode);
        rela.setSourceElement(source);
        rela.setTargetElement(target);
        return rela;
    }

    private TNodeTemplate getHostedNode(TNodeTemplate node, TTopologyTemplate topo) {
        List<TEntityTemplate> entities = topo.getNodeTemplateOrRelationshipTemplate();
        for (TEntityTemplate enti : entities) {
            if (enti.getClass().equals(TRelationshipTemplate.class)) {
                System.out.println("Looking for HostOn node. Checking Rela: " + enti.getId() + ", type: " + ((TRelationshipTemplate) enti).getType().getLocalPart());
                if (((TRelationshipTemplate) enti).getType().getLocalPart().equals(SalsaRelationshipType.HOSTON.getRelationshipTypeString())) {
                    SourceElement source = ((TRelationshipTemplate) enti).getSourceElement();
                    if (node.getId().equals(((TEntityTemplate) source.getRef()).getId())) {
                        TargetElement target = ((TRelationshipTemplate) enti).getTargetElement();
                        return (TNodeTemplate) target.getRef();
                    }
                }
            }
        }
        return null;
    }

    private SalsaRelationshipType getRelaBetweenTwoNode(TNodeTemplate node1, TNodeTemplate node2, TTopologyTemplate topo) {
        List<TEntityTemplate> entities = topo.getNodeTemplateOrRelationshipTemplate();
        for (TEntityTemplate enti : entities) {
            if (enti.getClass().equals(TRelationshipTemplate.class)) {
                TEntityTemplate e1 = (TEntityTemplate) ((TRelationshipTemplate) enti).getSourceElement().getRef();
                TEntityTemplate e2 = (TEntityTemplate) ((TRelationshipTemplate) enti).getTargetElement().getRef();
                if (e1.getId().equals(node1.getId()) && e2.getId().equals(node2.getId())) {
                    return SalsaRelationshipType.fromString(enti.getType().getLocalPart());
                }
                if (e2.getId().equals(node1.getId()) && e1.getId().equals(node2.getId())) {
                    return SalsaRelationshipType.fromString(enti.getType().getLocalPart());
                }
            }
        }
        return null;
    }

    private List<TNodeTemplate> getLocalNode(TNodeTemplate node, TTopologyTemplate topo) {
        List<TNodeTemplate> result = new ArrayList<>();
        List<TEntityTemplate> entities = topo.getNodeTemplateOrRelationshipTemplate();
        for (TEntityTemplate enti : entities) {
            if (enti.getClass().equals(TNodeTemplate.class)) {
                if (getRelaBetweenTwoNode(node, (TNodeTemplate) enti, topo) == SalsaRelationshipType.LOCAL) {
                    result.add((TNodeTemplate) enti);
                }
            }
        }
        return result;
    }

    private TNodeTemplate nextNewNode(TNodeTemplate node) {
        // search on the knowledge the relationship which node is the source
        List<TRelationshipTemplate> relaList = ToscaStructureQuery.getRelationshipTemplateList(knowledgeTopo);
        for (TRelationshipTemplate rela : relaList) {
            TNodeTemplate source = (TNodeTemplate) rela.getSourceElement().getRef();
            if (source.getId().equals(node.getType().getLocalPart())) {
                TNodeTemplate newNode = new TNodeTemplate();
                TNodeTemplate refNode = (TNodeTemplate) rela.getTargetElement().getRef();
                newNode.setId(refNode.getId());
                if (refNode.getProperties() != null && refNode.getProperties().getAny() != null) {
                    newNode.setProperties(new Properties());
                    newNode.getProperties().setAny((refNode.getProperties().getAny()));
                    newNode.setMinInstances(refNode.getMinInstances());
                    newNode.setMaxInstances(refNode.getMaxInstances());
                }
                return newNode;
            }
        }
        return null;
    }

    private List<TNodeTemplate> nextNewNodeLOCAL(TNodeTemplate node) {
        // search on the knowledge the relationship which node is the source
        List<TRelationshipTemplate> relaList = ToscaStructureQuery.getRelationshipTemplateList(knowledgeTopo);
        List<TNodeTemplate> localDependency = new ArrayList<TNodeTemplate>();
        for (TRelationshipTemplate rela : relaList) {
            if (rela.getType().getLocalPart().equals(SalsaRelationshipType.LOCAL.getRelationshipTypeString())) {
                TNodeTemplate source = (TNodeTemplate) rela.getSourceElement().getRef();
                if (source.getId().equals(node.getType().getLocalPart())) {
                    TNodeTemplate newNode = new TNodeTemplate();
                    TNodeTemplate refNode = (TNodeTemplate) rela.getTargetElement().getRef();
                    newNode.setId(refNode.getId());
                    if (refNode.getProperties() != null && refNode.getProperties().getAny() != null) {
                        newNode.setProperties(new Properties());
                        newNode.getProperties().setAny((refNode.getProperties().getAny()));
                        newNode.setMinInstances(refNode.getMinInstances());
                        newNode.setMaxInstances(refNode.getMaxInstances());
                    }
                    localDependency.add(newNode);
                }
            }
        }
        return localDependency;
    }

    /*
     * Mode all properties which not belong to node1 to node2 node2.properties
     * will be deleted and replace with new one
     */
    private void moveProperties(TNodeTemplate node1, TNodeTemplate node2, String keepType) {
        if (node1.getProperties() == null || node1.getProperties().getAny() == null) {
            return;
        }
        System.out.println("moving " + node1.getId() + " to " + node2.getId());
        SalsaMappingProperties p1 = (SalsaMappingProperties) node1.getProperties().getAny();

        EngineLogger.logger.debug("movingProperties - 1");
        List<SalsaMappingProperty> fixedListP1 = new ArrayList<>(p1.getProperties());
        EngineLogger.logger.debug("movingProperties - 2");
        SalsaMappingProperties newProp = null;
        if (node2.getProperties() != null && node2.getProperties().getAny() != null) {
            EngineLogger.logger.debug("movingProperties - 2-1");
            newProp = (SalsaMappingProperties) node2.getProperties().getAny();
            if (newProp == null) {
                EngineLogger.logger.debug("movingProperties - 2-1-1-1-1-1");
            }
        } else {
            EngineLogger.logger.debug("movingProperties - 2-2");
            newProp = new SalsaMappingProperties();
        }
        EngineLogger.logger.debug("movingProperties - 3");
        for (SalsaMappingProperty prop : fixedListP1) {
            if (!prop.getType().equals(keepType)
                    && !prop.getType().equals("BundleConfig")
                    && !prop.getType().equals("Operations")
                    && !prop.getType().equals("action")) { // move from req.prop ==> node.prop
                EngineLogger.logger.debug("movingProperties - 4");
                //p2.getProperties().add(prop);
                newProp.getProperties().add(prop);
                EngineLogger.logger.debug("movingProperties - 4-1");
                p1.getProperties().remove(prop);
                EngineLogger.logger.debug("movingProperties - 4-2");
            }
        }
        EngineLogger.logger.debug("movingProperties - 5");
        Properties toscaProp = new Properties();
        toscaProp.setAny(newProp);
        EngineLogger.logger.debug("movingProperties - 6");
        node2.setProperties(toscaProp);
        EngineLogger.logger.debug("movingProperties - 7");
    }

    private void cleanEmptyProperties(List<TNodeTemplate> nodeLst) {
        for (TNodeTemplate node : nodeLst) {
            if (node.getProperties() != null) {
                SalsaMappingProperties maps = (SalsaMappingProperties) node.getProperties().getAny();
                if (maps.getProperties().size() == 0) {
                    node.setProperties(null);
                }
            }
        }
    }

    private void cleanLOCALRelationship(TTopologyTemplate topo) {
        List<TEntityTemplate> entities = topo.getNodeTemplateOrRelationshipTemplate();
        Iterator<TEntityTemplate> iterator = entities.iterator();
        while (iterator.hasNext()) {
            TEntityTemplate enti = iterator.next();
            if (enti.getClass().equals(TRelationshipTemplate.class)) {
                if (((TRelationshipTemplate) enti).getType().getLocalPart().equals(SalsaRelationshipType.LOCAL.getRelationshipTypeString())) {
                    iterator.remove();
                }
            }
        }
    }

    private void addOSNodeConfig(TTopologyTemplate topo) {
        List<TEntityTemplate> entities = topo.getNodeTemplateOrRelationshipTemplate();
        boolean found = false;
        for (TEntityTemplate enti : entities) {
            if (enti.getType().getLocalPart().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
                System.out.println("Checking OS configuration for node: " + enti.getId());
                TNodeTemplate node = (TNodeTemplate) enti;
                SalsaMappingProperties maps = null;

                if (node.getProperties() == null) {
                    System.out.println("OS node " + enti.getId() + " has no properties field");
                    maps = new SalsaMappingProperties();
                } else {
                    System.out.println("debug 1");
                    maps = (SalsaMappingProperties) node.getProperties().getAny();
                    System.out.println("debug 2");
                    if (maps == null) {
                        System.out.println("debug 3");
                        maps = new SalsaMappingProperties();
                    }
                    found = false;
                    for (SalsaMappingProperty imap : maps.getProperties()) {
                        System.out.println("debug 4");
                        if (imap.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
                            System.out.println("The OS node " + enti.getId() + " has the OS property already, no change.");
                            found = true;
                        }
                    }
                    if (found) {
                        continue;
                    }
                }

                if (!found) {
                    System.out.println("Start write out some configuration for node: " + enti.getId());
                    // can put some optimization processing here, now get the default values
                    InputStream input = ToscaEnricherSALSA.class.getResourceAsStream("/cloudconfig.default.properties");
                    java.util.Properties prop = new java.util.Properties();
                    try {
                        prop.load(input);
                    } catch (IOException e) {
                        EngineLogger.logger.error("Couldn't find the default cloud configuration file.");
                        return;
                    }
                    String provider = prop.getProperty("provider");
                    String instanceType = prop.getProperty("instanceType");
                    String baseImage = prop.getProperty("baseImage");
                    String packages = "";

                    System.out.println("Write the configuration: " + provider + ", " + instanceType + ", " + baseImage);

                    Map<String, String> map = new HashMap<>();
                    map.put("provider", provider);
                    map.put("instanceType", instanceType);
                    map.put("baseImage", baseImage);
                    map.put("packages", packages);
                    maps.put(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString(), map);

                    EngineLogger.logger.debug("Map of properties is created");
                    node.getProperties().setAny(maps);
                    node.setMinInstances(1);
                    node.setMaxInstances("unbounded");
                    EngineLogger.logger.debug("Done adding configuration");
                } //if !found
            }
        }
    }

    public void createComplexRelationship(SalsaRelationshipType type, TDefinitions def) {
        List<TRelationshipTemplate> lst = ToscaStructureQuery.getRelationshipTemplateList(def);
        for (TRelationshipTemplate rela : lst) {
            if (rela.getType().getLocalPart().equals(type.getRelationshipTypeString())) {
                createRelationshipWithCapabilityAndRequirement(rela);
            }
        }
    }

    /*
     * Add capability and requirement for nodes if relationship is at two node
     * Node source: requirement, node target: capability
     */
    private void createRelationshipWithCapabilityAndRequirement(TRelationshipTemplate rela) {
        SourceElement testNode = rela.getSourceElement();
        if (testNode.getRef().getClass().equals(TNodeTemplate.class)) {	// rela between 2 node
            TNodeTemplate node2 = (TNodeTemplate) rela.getSourceElement().getRef();
            TNodeTemplate node1 = (TNodeTemplate) rela.getTargetElement().getRef();
            if (node1.getCapabilities() == null) {
                node1.setCapabilities(new Capabilities());
            }
            TCapability newCapa = new TCapability();
            newCapa.setId(node1.getId() + "_capa_for_" + node2.getId());
            newCapa.setName(node1.getId() + "_capa_for_" + node2.getId());
            node1.getCapabilities().getCapability().add(newCapa);

            if (node2.getRequirements() == null) {
                node2.setRequirements(new Requirements());
            }
            TRequirement newReq = new TRequirement();
            newReq.setId(node2.getId() + "_req_" + node1.getId());
            newReq.setName(node2.getId() + "_req_" + node1.getId());
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

    private void enrichArtifactRepo(String prefixRepo) {
        for (TExtensibleElements ee : toscaDef.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
            if (ee.getClass().equals(TArtifactTemplate.class)) {
                if (((TArtifactTemplate) ee).getArtifactReferences() != null) {
                    for (TArtifactReference ref : ((TArtifactTemplate) ee).getArtifactReferences().getArtifactReference()) {
                        String refstr = ref.getReference();
                        if (!refstr.startsWith("http://") && !refstr.startsWith("file://")) {
                            refstr = refstr.startsWith("/") ? refstr.substring(1) : refstr;
                            ref.setReference(prefixRepo + "/" + refstr);
                        }
                    }
                }
            }
        }
    }

    /*
     * Generate: knowledge base, artifact mapping, repositories
     */
    public static void main(String[] args) throws Exception {
		// generate a set of node template of specific type SALSA support which
        // use for the enricher

    }

    private static void generateDataFile() {
        TDefinitions defi = new TDefinitions();
        TServiceTemplate ser = new TServiceTemplate();
        defi.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(ser);
        ser.setTopologyTemplate(new TTopologyTemplate());
        ser.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();

        List<TEntityTemplate> en = ser.getTopologyTemplate()
                .getNodeTemplateOrRelationshipTemplate();
        TNodeTemplate os_node = new TNodeTemplate();
        os_node.setId("os");
        SalsaMappingProperties map = new SalsaMappingProperties();
        map.put("os", "instanceType", "000000960");
        map.put("os", "provider", "dsg@openstack");
        map.put("os", "baseImage", "000000960");
        map.put("os", "packages", "");

        os_node.getProperties().setAny(map);

        en.add(generateNode("os"));
        en.add(generateNode("bin"));
        en.add(generateNode("sh"));
        en.add(generateNode("jre"));
        en.add(generateNode("tomcat"));
        en.add(generateNode("war"));
        en.add(generateNode("image"));
        en.add(generateNode("software"));

        en.add(generateRela("software", "os", SalsaRelationshipType.HOSTON,
                defi));
        en.add(generateRela("sh", "os", SalsaRelationshipType.HOSTON, defi));
        en.add(generateRela("bin", "os", SalsaRelationshipType.HOSTON, defi));
        en.add(generateRela("jre", "os", SalsaRelationshipType.HOSTON, defi));
        en.add(generateRela("tomcat", "os", SalsaRelationshipType.HOSTON, defi));
        en.add(generateRela("war", "tomcat", SalsaRelationshipType.HOSTON, defi));
        en.add(generateRela("image", "os", SalsaRelationshipType.HOSTON, defi));

        en.add(generateRela("tomcat", "jre", SalsaRelationshipType.LOCAL, defi)); // remove
        // tomcat-LOCAL-jre,
        // add
        // jre-hoston-os_tomcat
        try {
            ToscaXmlProcess.writeToscaElementToFile(defi,"/tmp/salsa.knowledge.xml");
        } catch (JAXBException | IOException ex) {
            System.out.println("Cannot write TOSCA knowlegde file.");
        }

        // Generate the repository
//        Repositories repos = new Repositories();
//        repos.addRepo(
//                "salsa-repo",
//                RepositoryFormat.maven2,
//                "http://128.130.172.215:8080/nexus-2.8.0-05/service/local/artifact/maven/redirect?");
//        repos.addRepo("github", RepositoryFormat.git, "https://github.com/");
//        repos.addRepo(
//                "dsg-openstack-image",
//                RepositoryFormat.nova,
//                "http://openstack.infosys.tuwien.ac.at:8774/v2/9fee130e30784e33a7d3c9bd4f5a60ce");
//        repos.addRepo("local", RepositoryFormat.apt, "");
//        repos.exportToXML("/tmp/salsa.repo.xml");

        // generate the artifact type
        Artifacts arts = new Artifacts();
        Artifact art = new Artifact("tomcat7", ArtifactFormat.deb, "local",
                "tomcat7");
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

        art = new Artifact("mela", ArtifactFormat.sh, "salsa-repo",
                "r=salsa-artifacts&g=mela-server&a=install-mela&v=LATEST&e=sh");
        art.addOperation("deploy", "install-mela.sh");
        art.addOperation("start",
                "/etc/init.d/mela-data-service start; /etc/init.d/mela-analysis-service start");
        art.addOperation("stop",
                "/etc/init.d/mela-data-service stop; /etc/init.d/mela-analysis-service stop");
        art.addOperation("pid", "/tmp/mela-analysis-service.pid");
        arts.add(art);

		// arts.add(new Artifact("hadoop", ArtifactFormat.sh, "salsa-repo",
        // ""));
        arts.exportToXML("/tmp/salsa.artifacts.xml");

    }

    private static TRelationshipTemplate generateRela(String source,
            String target, SalsaRelationshipType type, TDefinitions def) {
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

    private static TNodeTemplate generateNode(String type) {
        TNodeTemplate node = new TNodeTemplate();
        node.setId(type);
        return node;
    }

}
