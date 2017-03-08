
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
package at.ac.tuwien.dsg.salsa.engine.services;

import at.ac.tuwien.dsg.salsa.engine.dataprocessing.ToscaStructureQuery;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaArtifactType;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.salsa.model.properties.Artifact;
import at.ac.tuwien.dsg.salsa.model.properties.Capability;
import at.ac.tuwien.dsg.salsa.model.relationship.RelationshipType;
import at.ac.tuwien.dsg.salsa.model.toscaDomain.SalsaMappingProperties;
import generated.oasis.tosca.TArtifactTemplate;
import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TDeploymentArtifact;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRequirement;
import generated.oasis.tosca.TServiceTemplate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung Le
 */
public class InfoParser {

    static Logger logger = LoggerFactory.getLogger("salsa");

    //    static SalsaCenterConnector centerCon;
//
//    static {
//        try {
//            centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", logger);
//        } catch (EngineConnectionException ex) {
//            logger.error("Cannot connect to SALSA service in localhost: " + SalsaConfiguration.getSalsaCenterEndpointLocalhost() + ". This is a fatal error !");
//        }
//    }
//    public static void cloneDataForReferenceNodes(CloudService service) throws SalsaException {
//        List<ServiceUnit> units = service.getAllComponent();
//        // clone data of all reference node
//        for (ServiceUnit unit : units) {
//            ServiceUnit refUnit = InfoParser.getReferenceServiceUnit(unit);
//            if (refUnit != null) {
//                logger.debug("orchestrateNewService-Clone ref data for node: " + unit.getUuid());
//                String topoID = service.getTopologyOfNode(refUnit.getUuid()).getUuid();
//                InfoParser.updateInstancesForReferenceNode(refUnit, service.getUuid(), topoID, unit.getUuid());
//            }
//        }
//    }
//    public static void updateInstancesForReferenceNode(ServiceUnit sourceSU, String targetServiceID, String targetTopoID, String targetSUID) throws SalsaException {
//        for (ServiceInstance instance : sourceSU.getInstances()) {
//            logger.debug("Adding instance to: " + targetServiceID + "/" + targetSUID + "/" + instance.getIndex());
//            Object tmpProp = null;
//            if (instance.getProperties() != null) {
//                tmpProp = instance.getProperties().getAny();
//            }
//            instance.setProperties(null);
//            centerCon.addInstanceUnitMetaData(targetServiceID, targetTopoID, targetSUID, instance);
//            if (tmpProp != null) {
//                centerCon.updateInstanceUnitProperty(targetServiceID, targetTopoID, targetSUID, instance.getInstanceId(), tmpProp);
//            }
//        }
//    }
//    public static ServiceUnit getReferenceServiceUnit(ServiceUnit input) throws SalsaException {
//        logger.debug("Checking the reference for ServiceUnit: " + input.getId());
//        if (input.getReference() == null) {
//            logger.debug("Checking the reference for ServiceUnit: " + input.getId() + " => Not be a reference node.");
//            return null;
//        }
//        logger.debug("Checking the referece for ServiceUnit: " + input.getId() + " => We got the reference string: " + input.getReference());
//        String[] refStr = input.getReference().split("/");
//        if (refStr.length < 2) {
//            throw new AppDescriptionException(input.getId(), "Too short reference String, should be in format [serviceID/serviceunitID], but number of elements is only" + refStr.length);
//        }
//
//        SalsaCenterConnector otherSalsa;
//        String otherCloudServiceID;
//        String otherServiceUnitID;
//        if (refStr.length == 3) {
//            logger.debug("Parsing reference node of other salsa at: " + refStr[0].trim());
//            String otherSalsaEndpoint = "http://" + refStr[0] + "/salsa-engine";
//            otherSalsa = new SalsaCenterConnector(otherSalsaEndpoint, "/tmp", logger);
//            otherCloudServiceID = refStr[1];
//            otherServiceUnitID = refStr[2];
//        } else {
//            otherSalsa = centerCon;
//            otherCloudServiceID = refStr[0];
//            otherServiceUnitID = refStr[1];
//        }
//        logger.debug("Getting information from other SALSA for service: " + otherCloudServiceID + ", node:" + otherServiceUnitID);
//
//        CloudService service = otherSalsa.getUpdateCloudServiceRuntime(otherCloudServiceID);
//        if (service != null) {
//            logger.debug("Checking the reference for ServiceUnit: " + input.getId() + " => FOUND !");
//            return service.getComponentById(otherServiceUnitID);
//        }
//        throw new AppDescriptionException(input.getId(), "A reference but could not find the target service to refer to.");
//    }
    public static CloudService buildRuntimeDataFromTosca(TDefinitions def) {
        logger.info("Start building runtime from Tosca file");
        CloudService service = new CloudService();
        service.setState(ConfigurationState.UNDEPLOYED);
        List<TServiceTemplate> serviceTemplateLst = ToscaStructureQuery.getServiceTemplateList(def);
        for (TServiceTemplate st : serviceTemplateLst) {
            ServiceTopology topo = new ServiceTopology();
            topo.setUuid(UUID.randomUUID().toString());
            topo.setName(st.getId());
            // all other nodes
            List<TNodeTemplate> nodes = ToscaStructureQuery.getNodeTemplateList(st);
            List<TRelationshipTemplate> relas_hoston = ToscaStructureQuery
                    .getRelationshipTemplateList(RelationshipType.HOSTON.name(), st);
            List<TRelationshipTemplate> relas_connectto = ToscaStructureQuery
                    .getRelationshipTemplateList(RelationshipType.CONNECTTO.name(), st);
            logger.debug("Number of HostOn relationships: " + relas_hoston.size());
            for (TNodeTemplate node : nodes) {
                /**
                 * TRANSLATE BASIC INFORMATION AS Name, Min/max, Reference
                 */
                ServiceUnit nodeData = new ServiceUnit(UUID.randomUUID().toString(), node.getType().getLocalPart());
                nodeData.setState(ConfigurationState.UNDEPLOYED);
                nodeData.setName(node.getId());
                nodeData.setMin(node.getMinInstances());
                nodeData.setReference(node.getReference());

                /**
                 * TRANSLATE CAPABILITIES FIELD
                 */
//                if (node.getCapabilities() != null && node.getCapabilities().getCapability() != null) {
//                    for (TCapability capa : node.getCapabilities().getCapability()) {
//                        if (!capa.getId().trim().isEmpty()) {
//                            nodeData.getCapabilityVars().add(capa.getId().trim());
//                        }
//                    }
//                }
                if (node.getMaxInstances().equals("unbounded")) {
                    nodeData.setMax(100); // max for experiments
                } else {
                    nodeData.setMax(Integer.parseInt(node.getMaxInstances()));
                }

                /**
                 * TRANSLATE ACTION MAPPING PROPERTIES
                 */
                if (node.getProperties() != null) {
                    if (node.getProperties().getAny() != null) {
                        SalsaMappingProperties props = (SalsaMappingProperties) node.getProperties().getAny();
                        SalsaMappingProperties.SalsaMappingProperty p = props.getByType("action");
                        if (p != null) {
                            for (SalsaMappingProperties.SalsaMappingProperty.Property pp : p.getPropertiesList()) {
                                // this point, we assume that TOSCA only provide Script capablity
                                nodeData.hasCapability(new Capability(pp.getName(), pp.getValue()));
                            }
                        }
                    }
                }

                /**
                 * TRANSLATE THE HOSTED ON RELATIONSHIP
                 */
                logger.debug("debugggg Sep 8.1 - 1");
                // find what is the host of this node, add to hostId
                for (TRelationshipTemplate rela : relas_hoston) { // search on all relationship, find the host of this "node"
                    // note that, after convert, the capa and req are reverted.
                    TEntityTemplate targetRela = (TEntityTemplate) rela.getTargetElement().getRef();
                    TEntityTemplate sourceRela = (TEntityTemplate) rela.getSourceElement().getRef();
                    TNodeTemplate target;
                    TNodeTemplate source;
                    if (targetRela.getClass().equals(TRequirement.class)) {
                        target = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(targetRela.getId(), def);
                        source = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(sourceRela.getId(), def);
                    } else {
                        target = (TNodeTemplate) sourceRela;
                        source = (TNodeTemplate) targetRela;
                    }
                    logger.debug("Is the source with id: " + target.getId() + " same with " + nodeData.getUuid());
                    if (target.getId().equals(nodeData.getName())) {
                        nodeData.setHostedUnitName(source.getId());
                        logger.debug("Found the host of node " + nodeData.getUuid() + " which is id = " + source.getId());
                    }
                }
                logger.debug("debugggg Sep 8.1 - 2");
                /**
                 * TRANSLATE CONNECT-TO RELATIONSHIPS
                 */
                // find the connect to node, add it to connecttoId, this node will be the requirement, connect to capability (reverse with the Tosca)
                for (TRelationshipTemplate rela : relas_connectto) {
                    logger.debug("buildRuntimeDataFromTosca. Let's see relationship connectto: " + rela.getId());

                    if (rela.getSourceElement().getRef().getClass().equals(TNodeTemplate.class)) {
                        TNodeTemplate sourceNode = (TNodeTemplate) rela.getSourceElement().getRef();
                        TNodeTemplate targetNode = (TNodeTemplate) rela.getTargetElement().getRef();
                        if (sourceNode.getId().equals(node.getId())) {
                            nodeData.getConnecttoUnitName().add(targetNode.getId());
                        }

                    } else {    // requirement and capability connect to
                        TRequirement targetReq = (TRequirement) rela.getTargetElement().getRef();
                        TNodeTemplate target = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(targetReq.getId(), def);
                        TCapability sourceCapa = (TCapability) rela.getSourceElement().getRef();
                        TNodeTemplate source = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(sourceCapa.getId(), def);
                        logger.debug("buildRuntimeDataFromTosca. Found the target id: {}", target.getId());
                        logger.debug("buildRuntimeDataFromTosca. Source capa: {}", sourceCapa.getId());
                        logger.debug("buildRuntimeDataFromTosca. Source: {}", source.getId());
                        if (target.getId().equals(node.getId())) {
                            nodeData.getConnecttoUnitName().add(source.getId());
                        }

                    }
                }
                logger.debug("debugggg Sep 8.1 - 3");

                /**
                 * TRANSLATE DEPLOYMENT ARTIFACT FIELDS
                 */
                if (node.getDeploymentArtifacts() != null && node.getDeploymentArtifacts().getDeploymentArtifact() != null) {
                    for (TDeploymentArtifact art : node.getDeploymentArtifacts().getDeploymentArtifact()) {
                        TArtifactTemplate artTemp = ToscaStructureQuery.getArtifactTemplateById(art.getArtifactRef().getLocalPart(), def);
                        nodeData.hasArtifact(new Artifact(art.getName(), art.getArtifactType().getLocalPart(), artTemp.getArtifactReferences().getArtifactReference().get(0).getReference()));
                    }
                }
                // add the artifact type for deploying the node. The first artifact type which is not misc or metadata will be selected.
                // we know docker, it is a bit hack, but work
                if (node.getType().getLocalPart().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
                    nodeData.setMainArtifactType(SalsaArtifactType.dockerfile.getString());
                } else if (node.getDeploymentArtifacts() != null && node.getDeploymentArtifacts().getDeploymentArtifact() != null) {
                    for (TDeploymentArtifact tArt : node.getDeploymentArtifacts().getDeploymentArtifact()) {
                        if (!tArt.getArtifactType().getLocalPart().equals(SalsaArtifactType.misc.getString())
                                && !tArt.getArtifactType().getLocalPart().equals(SalsaArtifactType.metadata.getString())
                                && !tArt.getArtifactType().getLocalPart().equals(SalsaArtifactType.contract.getString())) {
                            nodeData.setMainArtifactType(tArt.getArtifactType().getLocalPart());
                        }
                    }
                }

                /**
                 * ADD MAPPING PROPERTIES BASED ON ENTITY TYPE FOR SERVICE UNIT
                 */
                SalsaMappingProperties mapProp = (SalsaMappingProperties) node.getProperties().getAny();
                for (SalsaMappingProperties.SalsaMappingProperty p : mapProp.getProperties()) {
                    nodeData.writePropertiesFromMap(p.getMapData());
                }

//                if (node.getProperties() != null) {
//                    SalsaMappingProperties mapProp = (SalsaMappingProperties) node.getProperties().getAny();
//                    if (node.getType().getLocalPart().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
//                        SalsaInstanceDescription_VM instanceDesc = new SalsaInstanceDescription_VM();
//                        instanceDesc.updateFromMappingProperties(mapProp);
//                        nodeData.setProperties(instanceDesc.toJson());
//                    } else if (node.getType().getLocalPart().equals(SalsaEntityType.SERVICE.getEntityTypeString())) {
//                        SalsaInstanceDescription_SystemProcess instanceDesc = new SalsaInstanceDescription_SystemProcess();
//                        instanceDesc.updateFromMappingProperties(mapProp);
//                        nodeData.setProperties(instanceDesc.toJson());
//                    } else if (node.getType().getLocalPart().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
//                        SalsaInstanceDescription_Docker dockerDesc = new SalsaInstanceDescription_Docker();
//                        dockerDesc.updateFromMappingProperties(mapProp);
//                        nodeData.setProperties(dockerDesc.toJson());
//                    }
//                }
                topo.hasUnit(nodeData);
            }
            logger.debug("debugggg Sep 8.1 - last");
            service.hasTopology(topo);
        }
        return service;
    }

//    public static ServiceCategory mapOldAndNewCategory(SalsaEntityType type) {
//        switch (type) {
//            case ARTIFACT:
//            case SOFTWARE:
//            case EXECUTABLE:
//                return ServiceCategory.ExecutableApp;
//            case DOCKER:
//                return ServiceCategory.docker;
//            case TOMCAT:
//                return ServiceCategory.TomcatContainer;
//            case OPERATING_SYSTEM:
//                return ServiceCategory.VirtualMachine;
//            case SERVICE:
//                return ServiceCategory.SystemService;
//            case WAR:
//                return ServiceCategory.JavaWebApp;
//            default:
//                return ServiceCategory.SystemService;
//        }
//    }
}
