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
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.richInformationCapability;

import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.UnitInstanceDAO;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Capability;
import at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels.RestExecution;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.State;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.DockerInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.VirtualMachineInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.application.SystemServiceInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.UnitCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.genericCapability.GenericUnitCapability;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_Docker;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import java.util.Collections;
import java.util.List;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;

/**
 * This enhance the action by adding information into ELISE database
 *
 * @author Duc-Hung LE
 */
public class RichInformationUnitCapability implements UnitCapabilityInterface {

    UnitCapabilityInterface lowerCapa = new GenericUnitCapability();
    Logger logger = EngineLogger.logger;
    String salsaEndpoint = SalsaConfiguration.getSalsaCenterEndpoint();

    @Override
    public ServiceInstance deploy(String serviceId, String nodeId, int instanceId) throws SalsaException {
        lowerCapa.deploy(serviceId, nodeId, instanceId);

        // save unit to ELISE        
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        UnitInstanceDAO unitInstanceDAO = (UnitInstanceDAO) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), UnitInstanceDAO.class, Collections.singletonList(new JacksonJsonProvider()));

        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit unit = service.getComponentById(nodeId);
        ServiceTopology topo = service.getTopologyOfNode(nodeId);
        ServiceInstance instance = unit.getInstanceById(instanceId);

        // create unit instance and add several simple domainInfo getting from SALSA
        UnitInstance unitInst = makeUnitInstance(service, unit, instance);
        // find the hostedOn instance
        if (!unit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            ServiceUnit hostedUnit = service.getComponentById(unit.getHostedId());
            ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());
            UnitInstance hostedUnitInst = makeUnitInstance(service, hostedUnit, hostedInstance);
            unitInst.hostedOnInstance(hostedUnitInst);
        } 

        // find the connectedTo instance
        for (String connecttoID : unit.getConnecttoId()) {
            ServiceUnit conUnit = service.getComponentById(connecttoID);
            if (!conUnit.getInstancesList().isEmpty()) {
                ServiceInstance conInstance = conUnit.getInstancesList().get(0);
                UnitInstance conInst = makeUnitInstance(service, conUnit, conInstance);
                unitInst.connectToInstance(conInst);
            }
        }

        unitInst.hasExtra("salsaID", service.getId() + "/" + topo.getId() + "/" + unit.getId() + "/" + instance.getInstanceId());

        GlobalIdentification globalID = new GlobalIdentification(unitInst.getCategory());
        LocalIdentification id = new LocalIdentification(unitInst.getCategory(), "SALSA");
        id.hasIdentification("id", unitInst.getExtra().get("salsaID"));        
        globalID.addLocalIdentification(id);
        logger.debug("adding localIdentification for node: {}/{} with id: {}", nodeId, instanceId, globalID.toJson());
        unitInst.setIdentification(globalID.toJson());

        // TODO: add more local identification here to adapt with other management tool: SYBL, rtGovOps?
        // save the UnitInstance into the graph DB
        unitInstanceDAO.addUnitInstance(unitInst);

        return instance;
    }

    @Override
    public void remove(String serviceId, String nodeId, int instanceId) throws SalsaException {
        UnitInstanceDAO unitInstanceDAO = (UnitInstanceDAO) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), UnitInstanceDAO.class, Collections.singletonList(new JacksonJsonProvider()));
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);

        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit unit = service.getComponentById(nodeId);
        ServiceInstance instance = unit.getInstanceById(instanceId);

        unitInstanceDAO.deleteUnitInstanceByID(instance.getUuid().toString());
        lowerCapa.remove(serviceId, nodeId, instanceId);
    }

    private UnitInstance makeUnitInstance(CloudService service, ServiceUnit unit, ServiceInstance ins) {
        UnitInstance unitInst = new UnitInstance(unit.getId(), null, convertSalsaState(ins.getState()));
        ServiceTopology topo = service.getTopologyOfNode(unit.getId());
        unitInst.hasExtra("salsaID", service.getId() + "/" + topo.getId() + "/" + unit.getId() + "/" + ins.getInstanceId());
        unitInst.hasExtra("salsaState", ins.getState().getNodeStateString());
        unitInst.setId(ins.getUuid().toString());

        if (unit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            unitInst.setCategory(ServiceCategory.VirtualMachine);
            // get basic VM information
            SalsaInstanceDescription_VM vmDescription = getVMOfUnit(service, topo, unit, ins);
            VirtualMachineInfo VMInfo = new VirtualMachineInfo(vmDescription.getProvider(), vmDescription.getInstanceId(), "VM:" + vmDescription.getPrivateIp());
            VMInfo.setPrivateIp(vmDescription.getPrivateIp());
            VMInfo.setPublicIp(vmDescription.getPublicIp());
            if (VMInfo.getPackagesDependencies() != null && VMInfo.getPackagesDependencies().getPackageDependency() != null) {
                VMInfo.getPackagesDependencies().getPackageDependency().addAll(vmDescription.getPackagesDependenciesList().getPackageDependency());
            }
            VMInfo.setState(vmDescription.getState());
            unitInst.setDomainInfo(VMInfo.toJson());

        } else if (unit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
            unitInst.setCategory(ServiceCategory.AppContainer);
            // get basic docker info
            SalsaInstanceDescription_Docker dockerDescription = getDockerUnit(service, topo, unit, ins);
            if (dockerDescription != null) {
                logger.debug("Adding docker feature ...");
                DockerInfo dockerInfo = new DockerInfo("docker", dockerDescription.getInstanceId(), dockerDescription.getDockername());
                unitInst.setDomainInfo(dockerInfo.toJson());
            }

        } else if (unit.getType().equals(SalsaEntityType.TOMCAT.getEntityTypeString())) {
            unitInst.setCategory(ServiceCategory.WebContainer);
        } else if (unit.getId().toLowerCase().startsWith("sensor")) {
            unitInst.setCategory(ServiceCategory.Sensor);
        } else if (unit.getType().equals(SalsaEntityType.SOFTWARE.getEntityTypeString())) {
            // get basic information of app
            // TODO: fix service type
            unitInst.setCategory(ServiceCategory.SystemService);
            SystemServiceInfo systemServiceInfo = new SystemServiceInfo("unknownPID", "unknownName");
            systemServiceInfo.setStatus("unknown status");
            unitInst.setDomainInfo(systemServiceInfo.toJson());
        } else {
            unitInst.setCategory(ServiceCategory.ExecutableApp);
        }

        List<at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.PrimitiveOperation> pos = ins.getPrimitive();
        boolean existedDeploy = false;
        boolean existedUnDeploy = false;
        if (pos != null) {
            for (at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.PrimitiveOperation po : pos) {
                String actionName = po.getName();

                String actionREF = this.salsaEndpoint + "/services/" + service.getId() + "/nodes/" + unit.getId() + "/instances/" + ins.getInstanceId() + "/action_queue/" + actionName;
                unitInst.hasCapability(new Capability(actionName, Capability.ExecutionMethod.REST, new RestExecution(actionREF, RestExecution.RestMethod.POST, "")).executedBy("SALSA"));
                if (actionName.equals("deploy")) {
                    existedDeploy = true;
                }
                if (actionName.equals("undeploy")) {
                    existedUnDeploy = true;
                }
            }
        }

        if (!existedUnDeploy) {
            String destroyInstanceStr = this.salsaEndpoint + "/services/" + service.getId() + "/topologies/" + topo.getId() + "/nodes/" + unit.getId() + "/instances/" + ins.getInstanceId();
            unitInst.hasCapability(new Capability("undeploy", Capability.ExecutionMethod.REST, new RestExecution(destroyInstanceStr, RestExecution.RestMethod.DELETE, "")).executedBy("SALSA"));
        }
        if (!existedDeploy) {
            String deploymore = this.salsaEndpoint + "/services/" + service.getId() + "/topologies/" + topo.getId() + "/nodes/" + unit.getId() + "/instance-count/{quantity}";
            unitInst.hasCapability(new Capability("deploy", Capability.ExecutionMethod.REST, new RestExecution(deploymore, RestExecution.RestMethod.POST, "")).hasParameters("quantity", "1").executedBy("SALSA"));
        }

        return unitInst;
    }

    private State convertSalsaState(SalsaEntityState salsastate) {
        if (salsastate != null) {
            switch (salsastate) {
                case ALLOCATING:
                case CONFIGURING:
                case INSTALLING:
                case STAGING:
                case STAGING_ACTION:
                    return State.DEPLOYING;
                case DEPLOYED:
                    return State.FINAL;
                case ERROR:
                    return State.ERROR;
                case UNDEPLOYED:
                    return State.UNDEPLOYING;
            }
        }
        return State.UNDEPLOYING;
    }

    private SalsaInstanceDescription_Docker getDockerUnit(CloudService service, ServiceTopology topo, ServiceUnit unit, ServiceInstance instance) {
        logger.debug("Getting Docker information if it is available ...");

        if (unit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            logger.debug(unit.getId() + " is a VM node, there will be no docker, just done!");
            return null;
        }

        ServiceUnit hostedUnit = topo.getComponentById(unit.getHostedId());
        ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());

        if (hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
            logger.debug("Found a docker node: " + hostedUnit.getId() + "/" + hostedInstance.getInstanceId());
            if (hostedInstance.getProperties() == null || hostedInstance.getProperties().getAny() == null) {
                return null;
            }
            return (SalsaInstanceDescription_Docker) hostedInstance.getProperties().getAny();
        } else {
            logger.debug(unit.getId() + " is not hosted on a docker node. Just done!");
            return null;
        }
    }

    private SalsaInstanceDescription_VM getVMOfUnit(CloudService service, ServiceTopology topo, ServiceUnit unit, ServiceInstance instance) {
        logger.debug("Getting VM for node: " + service.getId() + "/" + topo.getId() + "/" + unit.getId() + "/" + instance.getInstanceId());
        if (unit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            logger.debug("The node is acually the OS, return !");
            return (SalsaInstanceDescription_VM) instance.getProperties().getAny();
        }
        ServiceUnit hostedUnit = topo.getComponentById(unit.getHostedId());
        ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());
        while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            logger.debug("Not a OS node, checking which hosts this node...");
            hostedUnit = topo.getComponentById(hostedUnit.getHostedId());
            hostedInstance = hostedUnit.getInstanceById(hostedInstance.getHostedId_Integer());
            logger.debug("And we found the host node is: " + hostedUnit.getId() + "/" + hostedInstance.getInstanceId());
        }
        logger.debug("IN conclude, instance is hosted on the OS node: " + hostedUnit.getId() + "/" + hostedInstance.getInstanceId());
        return (SalsaInstanceDescription_VM) hostedInstance.getProperties().getAny();
    }

}
