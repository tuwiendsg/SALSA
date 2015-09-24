/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.extensions.salsainfocollector;


import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.UnitInstanceCollector;
import at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels.RestExecution;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Capability;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.State;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.DockerInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.VirtualMachineInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.application.SystemServiceInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.general.EngineConnectionException;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_Docker;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung LE
 */
public class CollectorForSalsa extends UnitInstanceCollector {

    static Logger logger = LoggerFactory.getLogger("SALSACollector");
    String salsaEndpoint = "http://localhost:8080/salsa-engine";

    public CollectorForSalsa() {
        this.salsaEndpoint = readAdaptorConfig("endpoint");
    }

    @Override
    public Set<UnitInstance> collectAllInstance() {
        logger.debug("SALSA collector start to collect unit instance from SALSA !");
        Set<UnitInstance> instances = new HashSet<>();

        SalsaCenterConnector centerCon;
        try {
            centerCon = new SalsaCenterConnector(this.salsaEndpoint, "/tmp", logger);
        } catch (EngineConnectionException ex) {
            logger.error("Cannot connect to local SALSA engine at: " + this.salsaEndpoint, ex);
            return null;
        }
        centerCon.getServiceListJson();
        Gson gson = new Gson();

        logger.debug("Collecting....");
        ServiceJsonList jsonList = (ServiceJsonList) gson.fromJson(centerCon.getServiceListJson(), ServiceJsonList.class);

        logger.debug("List all salsa service: " + jsonList.toString());
        List<String> listOfServices = new ArrayList(Arrays.asList(jsonList.toString().split(" ")));
        for (String s : listOfServices) {
            logger.debug("Checking instance on service : " + s);

            CloudService service;
            try {
                String url = this.salsaEndpoint + "/rest/services/" + s;
                logger.debug("QUERY URL: {}", url);
                String xml = RestfulUtils.queryDataToCenter(url, RestfulUtils.HttpVerb.GET, "", "", MediaType.TEXT_XML);
                service = SalsaXmlDataProcess.readSalsaServiceXml(xml);
                logger.debug("Query done");
            } catch (JAXBException | IOException ex) {
                logger.error("Cannot get the service data. Service ID: {}", s, ex);
                continue;
            }
            for (ServiceUnit unit : service.getAllComponent()) {
                ServiceTopology topo = service.getTopologyOfNode(unit.getId());
                for (ServiceInstance ins : unit.getInstancesList()) {
                    logger.debug("SALSA Unit Instance: " + unit.getId() + "/" + ins.getInstanceId());
                    UnitInstance unitInst = new UnitInstance(unit.getId(), null, convertSalsaState(ins.getState()));

                    unitInst.hasExtra("salsaID", service.getId() + "/" + topo.getId() + "/" + unit.getId() + "/" + ins.getInstanceId());
                    unitInst.hasExtra("salsaState", ins.getState().getNodeStateString());
                    // the ID in ELISE will be given by SALSA, that why we do integration
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
                        SystemServiceInfo systemServiceInfo = new SystemServiceInfo("unknownPID", "unknownName");
                        systemServiceInfo.setStatus("unknown status");
                        unitInst.setDomainInfo(systemServiceInfo.toJson());
                    } else {
                        unitInst.setCategory(ServiceCategory.SystemService);
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
                    
                    /**
                     * Collect relationship between instances
                     */
                    
                    
                    
                    
                    logger.debug("Adding unit instance, ID: " + unitInst.getId());
                    boolean adding = instances.add(unitInst);
                    logger.debug("Added result: " + adding + ", array now have : " + instances.size());
                }
            }
        }
        return instances;
    }

    @Override
    public UnitInstance collectInstanceByID(ServiceCategory instanceType, String domainID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LocalIdentification identify(UnitInstance instance) {
        logger.debug("Identify for unit instance: \n" + instance.getName());
        String structureID = instance.getExtra().get("salsaID");        

        LocalIdentification id = new LocalIdentification(instance.getCategory(), "SALSA");
        id.hasIdentification("id", structureID);
        
//        VirtualMachineInfo vminfo = (VirtualMachineInfo) instance.findDomainInfoByCategory(ServiceCategory.VirtualMachine);
//        String ip = vminfo.getPrivateIp();
        
        // rtGovOpsPortmap       

//        DockerInfo dockerMeta = (DockerInfo) instance.findDomainInfoByCategory(ServiceCategory.AppContainer);
//
//        if ((instance.getCategory().equals(ServiceCategory.Sensor) || instance.getCategory().equals(ServiceCategory.Gateway)) && dockerMeta != null) {
//            //2812:2826 5683:5697 80:9094
//            logger.debug("Found a docker metadata, trying to attract GovOps ID");
//            // get portmap, padding a space for something
//            String fullportmap = dockerMeta.getPortmap() + " ";
//            logger.debug("Get full portmap string: " + fullportmap);
//            // fix for generating rtGovOps ID
//            int startIndex = fullportmap.indexOf("80:") + 3;
//            String portmap = fullportmap.substring(startIndex, fullportmap.indexOf(" ", startIndex));
//            String rtGovOpsPortmap = ip + ":" + portmap;
//            logger.debug("rtGovOps ID: " + rtGovOpsPortmap);
//            id.hasIdentification("rtGovOpsID", rtGovOpsPortmap);
//        }
//        id.hasIdentification("ip", ip);
        
        return id;

    }

    @Override
    public String getName() {
        return "SalsaCollector";
    }

    public class ServiceJsonList {

        List<ServiceInfo> services = new ArrayList<>();

        public ServiceJsonList() {
        }

        public class ServiceInfo {

            String serviceName;
            String serviceId;
            String deployTime;

            public ServiceInfo(String name, String id, String deploytime) {
                this.serviceName = name;
                this.serviceId = id;
                this.deployTime = deploytime;
            }

            public String getServiceName() {
                return this.serviceName;
            }

            public String getServiceId() {
                return this.serviceId;
            }

            public String getDeployTime() {
                return this.deployTime;
            }
        }

        public List<ServiceInfo> getServicesList() {
            return this.services;
        }

        @Override
        public String toString() {
            String re = "";
            for (ServiceInfo si : this.services) {
                re = re + si.getServiceId() + " ";
            }
            return re.trim();
        }
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

    private State convertSalsaState(SalsaEntityState salsastate) {
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
        return State.UNDEPLOYING;
    }

}
