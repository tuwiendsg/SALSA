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
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseManager;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.UnitInstanceInfoManagement;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Capability;
import at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels.RestExecution;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.IDType;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.DockerInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.VirtualMachineInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.application.SystemServiceInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.UnitCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.genericCapability.GenericUnitCapability;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_Docker;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_SystemProcess;
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
        logger.debug("Deploy instance {}/{}/{} before saving information to DB", serviceId, nodeId, instanceId);
        lowerCapa.deploy(serviceId, nodeId, instanceId);

        logger.debug("Saving information into elise DB: {}/{}/{}", serviceId, nodeId, instanceId);
        // save unit to ELISE        
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);

        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit unit = service.getComponentById(nodeId);
        ServiceTopology topo = service.getTopologyOfNode(nodeId);
        ServiceInstance instance = unit.getInstanceById(instanceId);

        // create unit instance and add several simple domainInfo getting from SALSA
        logger.debug("Creating unit instance");
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

        unitInst.hasExtra("salsaID", service.getId() + "/" + unit.getId() + "/" + instance.getInstanceId());

        GlobalIdentification globalID = new GlobalIdentification(unitInst.getCategory());
        globalID.setUuid(instance.getUuid().toString());
        LocalIdentification id = new LocalIdentification(unitInst.getCategory(), "SALSA");
        logger.debug("Setting identification with salsaID=" + unitInst.getExtra().get("salsaID"));
        id.hasIdentification(IDType.SALSA_SERVICE.toString(), serviceId);
        id.hasIdentification(IDType.SALSA_TOPOLOGY.toString(), serviceId + "/" + topo.getId());
        id.hasIdentification(IDType.SALSA_UNIT.toString(), serviceId + "/" + nodeId);
        id.hasIdentification(IDType.SALSA_INSTANCE.toString(), serviceId + "/" + nodeId + "/" + instanceId);

        // in the case it is a sensor (name started with sensor ??) and hosted on docker ==> we may have GovOps        
        if (unit.getId().toLowerCase().startsWith("sensor") && !unit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            logger.debug("Instance {} is a sensor, checking IP_PORT identification", unitInst.getExtra().get("salsaID"));
            ServiceUnit hostedUnit = service.getComponentById(unit.getHostedId());
            // get port map of Docker
            if (hostedUnit != null && hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
                logger.debug("The sensor is hosted on node {}", hostedUnit.getId());
                ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());
                if (hostedInstance != null && hostedInstance.getProperties() != null) {
                    logger.debug("debug1: marshalling docker description ........");
                    SalsaInstanceDescription_Docker dockerDesp = (SalsaInstanceDescription_Docker) hostedInstance.getProperties().getAny();
                    logger.debug("debug1: marshalling docker description ........ DONE !");
                    // portmap should be 2102:10.99.0.21:2102   80:10.99.0.21:9080   4567:10.99.0.21:4567
                    String portmap = dockerDesp.getPortmap() + " ";
                    logger.debug("Get port map: " + portmap);
                    if (!portmap.trim().isEmpty()) {
                        String portmap80 = portmap.substring(portmap.indexOf("80:") + 3, portmap.indexOf(" ", portmap.indexOf("80:")));
                        if (portmap80.trim().isEmpty()) {
                            logger.debug("{} will be {}", IDType.IP_PORT.toString(), portmap80);
                            id.hasIdentification(IDType.IP_PORT.toString(), portmap80.trim());
                        }
                    }
                } else {
                    logger.debug("Do not find the instance or the instance properties of node {} that hosted node {}/{}", hostedUnit.getId(), unit.getId(), instance.getInstanceId());
                }

            } else {
                logger.debug("The hostedUnit of the sensor {} is null or not a docker", unit.getId());
            }
        }

        globalID.addLocalIdentification(id);
        logger.debug("adding localIdentification for node: {}/{} with id: {}", nodeId, instanceId, globalID.toJson());
        unitInst.setIdentification(globalID.toJson());
        logger.debug("Prepare to connect to EliseManager service: {}", EliseConfiguration.getRESTEndpointLocal());
        // TODO: add more local identification here to adapt with other management tool: SYBL, rtGovOps?
        // save the UnitInstance into the graph DB
        EliseManager eliseManager = (EliseManager) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), EliseManager.class, Collections.singletonList(new JacksonJsonProvider()));
        logger.debug("It may be connectted or not ! Now cheking...");

        if (eliseManager != null) {
            logger.debug("eliseManager is not null");
            try {
                eliseManager.health();
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            logger.debug("Yes, we can check the health");
            logger.debug("Checking ELISE connectivity: [" + eliseManager.health() + "]");
        } else {
            logger.error("Cannot contact to ELISE");
        }
        UnitInstanceInfoManagement unitInstanceDAO = (UnitInstanceInfoManagement) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), UnitInstanceInfoManagement.class, Collections.singletonList(new JacksonJsonProvider()));
        if (unitInstanceDAO != null) {
            logger.debug("unitInstanceDao is not null, prepare to add");
            unitInstanceDAO.addUnitInstance(unitInst);
        } else {
            logger.error("Cannot connect to the elise DB");
        }

        return instance;
    }

    @Override
    public void remove(String serviceId, String nodeId, int instanceId) throws SalsaException {
        logger.debug("Trying to remove information of {}/{}/{} from ELISE database...", serviceId, nodeId, instanceId);
        UnitInstanceInfoManagement unitInstanceDAO = (UnitInstanceInfoManagement) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), UnitInstanceInfoManagement.class, Collections.singletonList(new JacksonJsonProvider()));
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);

        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        if (service == null) {
            EngineLogger.logger.error("Cannot get service to removed: {}", serviceId);
            return;
        }
        ServiceUnit unit = service.getComponentById(nodeId);
        if (unit == null) {
            EngineLogger.logger.error("Try to delete instance of unit {}/{} but get NULL data.", serviceId, nodeId);
            return;
        }
        ServiceInstance instance = unit.getInstanceById(instanceId);

        if (instance != null) {
            if (unitInstanceDAO != null) {
                logger.debug("Trying to delete unit with UUID: {}" + instance.getUuid());
                try {
                    unitInstanceDAO.deleteUnitInstanceByID(instance.getUuid().toString());
                    EliseManager eliseManager = ((EliseManager) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), EliseManager.class, Collections.singletonList(new JacksonJsonProvider())));
                    logger.debug("Now deleting the unit identification: " + instance.getUuid().toString());
                    eliseManager.deleteGlobalIdentification(instance.getUuid().toString());
                } catch (Exception e) {
                    logger.error("Msg: " + e.getMessage() + ", cause: " + e.getCause());
                    e.printStackTrace();
                }
            } else {
                logger.error("Cannot connect to the database to delete instance" + instance.getUuid());
            }
            lowerCapa.remove(serviceId, nodeId, instanceId);
        } else {
            logger.error("Do not found the instance to delete: {}/{}/{}", serviceId, nodeId, instanceId);
        }
    }

    private UnitInstance makeUnitInstance(CloudService service, ServiceUnit unit, ServiceInstance ins) {
        logger.debug("Making unit instance: {}/{}/{}", service.getId(), unit.getId(), ins.getInstanceId());
        UnitInstance unitInst = new UnitInstance(unit.getId(), null);
        ServiceTopology topo = service.getTopologyOfNode(unit.getId());
        unitInst.hasExtra("salsaID", service.getId() + "/" + topo.getId() + "/" + unit.getId() + "/" + ins.getInstanceId());
        unitInst.hasExtra("salsaState", ins.getState().getNodeStateString());
        unitInst.setId(ins.getUuid().toString());

        /**
         * MAKE INITIAL DOMAIN INFO
         */
        if (unit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            logger.debug("Making VM domain info: {}/{}/{}", service.getId(), unit.getId(), ins.getInstanceId());
            unitInst.setCategory(ServiceCategory.VirtualMachine);
            // get basic VM information
            SalsaInstanceDescription_VM vmDescription = getVMOfUnit(service, topo, unit, ins);
            VirtualMachineInfo VMInfo = new VirtualMachineInfo(vmDescription.getProvider(), vmDescription.getInstanceId(), "VM:" + vmDescription.getPrivateIp());
            VMInfo.setPrivateIp(vmDescription.getPrivateIp());
            VMInfo.setPublicIp(vmDescription.getPublicIp());
            if (VMInfo.getPackagesDependencies() != null && VMInfo.getPackagesDependencies().getPackageDependency() != null) {
                VMInfo.getPackagesDependencies().getPackageDependency().addAll(vmDescription.getPackagesDependenciesList().getPackageDependency());
            }
            unitInst.setDomainInfo(VMInfo.toJson());
        } else if (unit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
            logger.debug("Making App container domain info: {}/{}/{}", service.getId(), unit.getId(), ins.getInstanceId());
            unitInst.setCategory(ServiceCategory.Docker);
            // get basic docker info
            SalsaInstanceDescription_Docker dockerDescription = getDockerUnit(service, topo, unit, ins);
            if (dockerDescription != null) {
                logger.debug("Adding docker feature ...");
                DockerInfo dockerInfo = new DockerInfo("docker", dockerDescription.getInstanceId(), dockerDescription.getDockername());
                unitInst.setDomainInfo(dockerInfo.toJson());
            }

        } else if (unit.getType().equals(SalsaEntityType.TOMCAT.getEntityTypeString())) {
            unitInst.setCategory(ServiceCategory.TomcatContainer);
        } else if (unit.getId().toLowerCase().startsWith("sensor")) {
            unitInst.setCategory(ServiceCategory.Sensor);
        } else if (unit.getType().equals(SalsaEntityType.SERVICE.getEntityTypeString())) {
            // get basic information of system service
            unitInst.setCategory(ServiceCategory.SystemService);
            logger.debug("Making SystemService domain info: {}/{}/{}", service.getId(), unit.getId(), ins.getInstanceId());
            if (ins.getProperties() != null) {
                SalsaInstanceDescription_SystemProcess sysProcess = (SalsaInstanceDescription_SystemProcess) ins.getProperties().getAny();
                SystemServiceInfo systemServiceInfo = new SystemServiceInfo(sysProcess.getName(), sysProcess.getName());
                unitInst.setDomainInfo(systemServiceInfo.toJson());
            }
        } else {
            unitInst.setCategory(ServiceCategory.ExecutableApp);
        }
        unitInst.setUnitType(unit.getType());

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
        logger.debug("Creating domain info done: {}/{}/{}", service.getId(), unit.getId(), ins.getInstanceId());
        return unitInst;
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
