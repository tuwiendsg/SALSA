/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.MiddleLevel;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.PrimitiveOperation;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;
import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRequirement;
import generated.oasis.tosca.TServiceTemplate;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author hungld
 */
public class InfoManagement {
    static SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
    
    public static void cloneDataForReferenceNodes(CloudService service) throws SalsaEngineException {
        List<ServiceUnit> units = service.getAllComponent();
        // clone data of all reference node
        for (ServiceUnit unit : units) {
            ServiceUnit refUnit = InfoManagement.getReferenceServiceUnit(unit);
            if (refUnit != null) {
                EngineLogger.logger.debug("orchestrateNewService-Clone ref data for node: " + unit.getId());
                String topoID = service.getTopologyOfNode(refUnit.getId()).getId();
                InfoManagement.updateInstancesForReferenceNode(refUnit, service.getId(), topoID, unit.getId());
            }
        }
    }   
    
    public static void updateInstancesForReferenceNode(ServiceUnit sourceSU, String targetServiceID, String targetTopoID, String targetSUID) {
        for (ServiceInstance instance : sourceSU.getInstancesList()) {
            EngineLogger.logger.debug("Adding instance to: " + targetServiceID + "/" + targetSUID + "/" + instance.getInstanceId());
            Object tmpProp = null;
            if (instance.getProperties() != null) {
                tmpProp = instance.getProperties().getAny();
            }
            instance.setProperties(null);
            centerCon.addInstanceUnitMetaData(targetServiceID, targetTopoID, targetSUID, instance);
            if (tmpProp != null) {
                centerCon.updateInstanceUnitProperty(targetServiceID, targetTopoID, targetSUID, instance.getInstanceId(), tmpProp);
            }
        }
    }
    
    public static ServiceUnit getReferenceServiceUnit(ServiceUnit input) throws SalsaEngineException {
        EngineLogger.logger.debug("Checking the reference for ServiceUnit: " + input.getId());
        if (input.getReference() == null) {
            EngineLogger.logger.debug("Checking the reference for ServiceUnit: " + input.getId() + " => Not be a reference node.");
            return null;
        }
        EngineLogger.logger.debug("Checking the referece for ServiceUnit: " + input.getId() + " => We got the reference string: " + input.getReference());
        String[] refStr = input.getReference().split("/");
        if (refStr.length < 2) {
            EngineLogger.logger.debug("Checking the referece for ServiceUnit: " + input.getId() + " => Bad reference string, should be serviceID/serviceunitID, but length=" + refStr.length);
            return null;
        }

        SalsaCenterConnector otherSalsa;
        String otherCloudServiceID;
        String otherServiceUnitID;
        if (refStr.length == 3) {
            EngineLogger.logger.debug("Parsing reference node of other salsa at: " + refStr[0].trim());
            String otherSalsaEndpoint = "http://" + refStr[0] + "/salsa-engine";
            otherSalsa = new SalsaCenterConnector(otherSalsaEndpoint, "/tmp", EngineLogger.logger);
            otherCloudServiceID = refStr[1];
            otherServiceUnitID = refStr[2];
        } else {
            otherSalsa = centerCon;
            otherCloudServiceID = refStr[0];
            otherServiceUnitID = refStr[1];
        }
        EngineLogger.logger.debug("Getting information from other SALSA for service: " + otherCloudServiceID + ", node:" + otherServiceUnitID);

        CloudService service = otherSalsa.getUpdateCloudServiceRuntime(otherCloudServiceID);
        if (service != null) {
            EngineLogger.logger.debug("Checking the reference for ServiceUnit: " + input.getId() + " => FOUND !");
            return service.getComponentById(otherServiceUnitID);
        }
        EngineLogger.logger.debug("Checking the reference for ServiceUnit: " + input.getId() + " => A reference but could not find the target service to refer to.");
        return null;

    }
    
    
    
    
    public static CloudService buildRuntimeDataFromTosca(TDefinitions def) {
        EngineLogger.logger.debug("Building runtime from Tosca file");
        CloudService service = new CloudService();
        service.setState(SalsaEntityState.UNDEPLOYED);
        List<TServiceTemplate> serviceTemplateLst = ToscaStructureQuery.getServiceTemplateList(def);
        for (TServiceTemplate st : serviceTemplateLst) {
            ServiceTopology topo = new ServiceTopology();
            topo.setId(st.getId());
            topo.setName(st.getName());
            // all other nodes
            List<TNodeTemplate> nodes = ToscaStructureQuery.getNodeTemplateList(st);
            List<TRelationshipTemplate> relas_hoston = ToscaStructureQuery
                    .getRelationshipTemplateList(SalsaRelationshipType.HOSTON.getRelationshipTypeString(), st);
            List<TRelationshipTemplate> relas_connectto = ToscaStructureQuery
                    .getRelationshipTemplateList(SalsaRelationshipType.CONNECTTO.getRelationshipTypeString(), st);
            EngineLogger.logger.debug("Number of HostOn relationships: " + relas_hoston.size());
            for (TNodeTemplate node : nodes) {
                ServiceUnit nodeData = new ServiceUnit(node.getId(), node.getType().getLocalPart());
                nodeData.setState(SalsaEntityState.UNDEPLOYED);
                nodeData.setName(node.getName());
                nodeData.setMin(node.getMinInstances());
                nodeData.setReference(node.getReference());
                if (node.getMaxInstances().equals("unbounded")) {
                    nodeData.setMax(100); // max for experiments
                } else {
                    nodeData.setMax(Integer.parseInt(node.getMaxInstances()));
                }
				// Get the action property if there is. Just support command pattern
                // TODO: Support complex action.
                if (node.getProperties() != null) {
                    if (node.getProperties().getAny() != null) {
                        SalsaMappingProperties props = (SalsaMappingProperties) node.getProperties().getAny();
                        SalsaMappingProperties.SalsaMappingProperty p = props.getByType("action");
                        if (p != null) {
                            for (SalsaMappingProperties.SalsaMappingProperty.Property pp : p.getPropertiesList()) {
                                nodeData.addPrimitiveOperation(PrimitiveOperation.newCommandType(pp.getName(), pp.getValue()));
                            }
                        }
                    }
                }
                // add the artifact type for SOFTWARE NODE
                if (node.getType().getLocalPart().equals(SalsaEntityType.SOFTWARE.getEntityTypeString())) {
                    if (node.getDeploymentArtifacts() != null && node.getDeploymentArtifacts().getDeploymentArtifact().size() != 0) {
                        nodeData.setArtifactType(node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactType().getLocalPart());
                        String artID = node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactRef().getLocalPart();
                        String directURL = ToscaStructureQuery
                                .getArtifactTemplateById(artID, def)
                                .getArtifactReferences().getArtifactReference()
                                .get(0).getReference();
                        nodeData.setArtifactURL(directURL);
                    }
                }
                EngineLogger.logger.debug("debugggg Sep 8.1 - 1");
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
                    EngineLogger.logger.debug("Is the source with id: " + target.getId() + " same with " + nodeData.getId());
                    if (target.getId().equals(nodeData.getId())) {
                        nodeData.setHostedId(source.getId());
                        EngineLogger.logger.debug("Found the host of node " + nodeData.getId() + " which is id = " + source.getId());
                    }
                }
                EngineLogger.logger.debug("debugggg Sep 8.1 - 2");
                // find the connect to node, add it to connecttoId, this node will be the requirement, connect to capability (reverse with the Tosca)

                for (TRelationshipTemplate rela : relas_connectto) {
                    EngineLogger.logger.debug("buildRuntimeDataFromTosca. Let's see relationship connectto: " + rela.getId());

                    if (rela.getSourceElement().getRef().getClass()
                            .equals(TNodeTemplate.class)) {
                        TNodeTemplate sourceNode = (TNodeTemplate) rela
                                .getSourceElement().getRef();
                        if (sourceNode.getId().equals(node.getId())) {
                            TNodeTemplate targetNode = (TNodeTemplate) rela
                                    .getTargetElement().getRef();
                            nodeData.getConnecttoId().add(targetNode.getId());
                        }
                    } else {
                        TRequirement targetReq = (TRequirement) rela
                                .getTargetElement().getRef();
                        TNodeTemplate target = ToscaStructureQuery
                                .getNodetemplateOfRequirementOrCapability(
                                        targetReq.getId(), def);

                        if (target.getId().equals(node.getId())) {
                            EngineLogger.logger.debug("buildRuntimeDataFromTosca. Found the target id: " + target.getId());
                            TCapability sourceCapa = (TCapability) rela.getSourceElement().getRef();
                            EngineLogger.logger.debug("buildRuntimeDataFromTosca. Source capa: " + sourceCapa.getId());
                            TNodeTemplate source = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(sourceCapa.getId(), def);
                            EngineLogger.logger.debug("buildRuntimeDataFromTosca. Source  " + source.getId());
                            nodeData.getConnecttoId().add(source.getId());
                        }
                    }
                }
                EngineLogger.logger.debug("debugggg Sep 8.1 - 3");
                // manipulate properties ConfigurationCapabilities
                topo.addComponent(nodeData);
            }
            EngineLogger.logger.debug("debugggg Sep 8.1 - last");
            service.addComponentTopology(topo);
        }
        return service;
    }
}
