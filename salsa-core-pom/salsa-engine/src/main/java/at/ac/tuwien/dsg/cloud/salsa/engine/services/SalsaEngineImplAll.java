package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import generated.oasis.tosca.TArtifactReference;
import generated.oasis.tosca.TArtifactTemplate;
import generated.oasis.tosca.TArtifactTemplate.ArtifactReferences;
import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TDeploymentArtifact;
import generated.oasis.tosca.TDeploymentArtifacts;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRelationshipTemplate.SourceElement;
import generated.oasis.tosca.TRelationshipTemplate.TargetElement;
import generated.oasis.tosca.TRequirement;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.SalsaEntity;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance.Capabilities;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance.Properties;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology.SalsaReplicaRelationships;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnitRelationship;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription.AssociatedVM;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription.DeploymentDescription;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription.DeploymentUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaEngineServiceIntenal;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata.ServiceJsonList;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.MutualFileAccessControl;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SystemFunctions;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_Docker;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

@Service
//@Path("/")
public class SalsaEngineImplAll implements SalsaEngineServiceIntenal {

    static Logger logger;
    static File configFile;
    SalsaToscaDeployer deployer = new SalsaToscaDeployer(configFile);

    static {
        logger = Logger.getLogger("EngineLogger");
        String userFile = SalsaConfiguration.getCloudUserParameters();
        if (userFile != null && !userFile.equals("")) {
            logger.debug("Found the user file in the main engine configuration. Load cloud configuration at: " + userFile);
            configFile = new File(userFile);
        } else {
            String CURRENT_DIR = System.getProperty("user.dir");
            File file1 = new File(CURRENT_DIR + "/cloudUserParameters.ini");
            File tmpFile = new File("/etc/cloudUserParameters.ini");
            if (file1.exists()) {
                logger.debug("Load cloud configuration at: " + file1.getAbsolutePath());
                configFile = file1;
            } else if (tmpFile.exists()) {
                logger.debug("Load cloud configuration at: " + tmpFile.getAbsolutePath());
                configFile = tmpFile;
            } else {
                logger.debug("Load cloud configuration at: default resource folder");
                configFile = new File(SalsaEngineImplAll.class.getResource("/cloudUserParameters.ini").getFile());
            }

        }
    }

    /*
     * MAIN SERVICES TO EXPOSE TO USERS
     */
    @Override
    public Response deployService(String serviceName, InputStream uploadedInputStream) throws SalsaEngineException {
        logger.debug("Recieved deployment request with name: " + serviceName);
        String tmp_id = UUID.randomUUID().toString();
        String tmpFile = "/tmp/salsa_tmp_" + tmp_id;
        serviceName = serviceName.replaceAll("\\s", "");
        if (!checkForServiceName(serviceName)) {
            return Response.status(404).entity("Error. Service Name is bad: " + serviceName).build();
        }
        try {
            MutualFileAccessControl.writeToFile(uploadedInputStream, tmpFile);
            TDefinitions def = ToscaXmlProcess.readToscaFile(tmpFile);

            this.deployer = new SalsaToscaDeployer(configFile);
            //CloudService service = deployer.deployNewService(def, serviceName);
            CloudService service = deployer.orchestrateNewService(def, serviceName);
            String output = "Deployed service. Id: " + service.getId();
            logger.debug(output);

            // delete tmp file
            try {
                File file = new File(tmpFile);
                file.delete();
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }

            // return 201: resource created
            return Response.status(201).entity(service.getId()).build();
        } catch (JAXBException e) {
            logger.error("Error when parsing Tosca: " + e);
            e.printStackTrace();
            // return 400: bad request, the XML is malformed and could not process 
            return Response.status(400).entity("Error. Unable to parse Tosca. Error: " + e).build();
        } catch (IOException e) {
            logger.error("Error reading file: " + tmpFile + ". Error: " + e);
            //return 500: intenal server error. The server cannot create and process tmp Tosca file 
            return Response.status(500).entity("Error when process Tosca file. Error: " + e).build();
        }
    }

    @Override
    public Response deployServiceFromXML(String uploadedInputStream) throws SalsaEngineException {
        String tmp_id = UUID.randomUUID().toString();
        String tmpFile = "/tmp/salsa_tmp_" + tmp_id;

        try {
            FileUtils.writeStringToFile(new File(tmpFile), uploadedInputStream);

            TDefinitions def = ToscaXmlProcess.readToscaFile(tmpFile);
            String serviceId = def.getId();
            if (!checkForServiceName(serviceId)) {
                return Response.status(404).entity("Error. Service Name is bad: " + serviceId).build();
            }
            //CloudService service = deployer.deployNewService(def, serviceId);
            CloudService service = deployer.orchestrateNewService(def, serviceId);
            String output = "Deployed service. Id: " + service.getId();
            logger.debug(output);
            // return 201: resource created
            return Response.status(201).entity(serviceId).build();
        } catch (JAXBException e) {
            logger.error("Error when parsing Tosca: " + e);
            e.printStackTrace();
            // return 400: bad request, the XML is malformed and could not process
            return Response.status(400).entity("Unable to parse the Tosca XML. Error: " + e).build();
        } catch (IOException e) {
            logger.error("Error reading file: " + tmpFile + ". Error: " + e);
            return Response.status(500).entity("Error when process Tosca file. Error: " + e).build();
        }
    }

    public Response redeployService(String serviceId) throws SalsaEngineException {
        String ogininalToscaFile = SalsaConfiguration.getServiceStorageDir() + "/" + serviceId + ".original";
        try {
            String originalTosca = FileUtils.readFileToString(new File(ogininalToscaFile));
            undeployService(serviceId);
            Thread.sleep(3000); // sleep 3 secs
            deployService(serviceId, new ByteArrayInputStream(originalTosca.getBytes("UTF-8")));
        } catch (IOException e) {
            logger.error("Error when reading data file! Error: " + e);
            return Response.status(500).entity("Error when reading orgininal tosca file for service: " + serviceId + ". Error: " + e).build();
        } catch (InterruptedException ie) {
            logger.error("Interrup error");
            return Response.status(500).entity("Error when reading orgininal tosca file for service: " + serviceId + ". Error: " + ie).build();
        }
        return Response.status(201).entity(serviceId).build();
    }

    /* (non-Javadoc)
     * @see at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineIntenalInterface#undeployService(java.lang.String)
     */
    @Override
    public Response undeployService(String serviceId) throws SalsaEngineException {
        logger.debug("DELETING SERVICE: " + serviceId);
        if (deployer.cleanAllService(serviceId)) {
			// deregister service here

            String fileName = SalsaConfiguration.getServiceStorageDir() + "/" + serviceId;
            File file = new File(fileName);
            File datafile = new File(fileName.concat(".data"));
            File originalFile = new File(fileName.concat(".original"));
            if (originalFile.delete() && file.delete() && datafile.delete()) {
                logger.debug("Deregister service done: " + serviceId);
                return Response.status(200).entity("Deregistered service: " + serviceId).build();
            } else {
                logger.debug("Could not found service to deregister: " + serviceId);
                return Response.status(500).entity("Service not found to deregister: " + serviceId).build();
            }
        } else {
            // return 404: not found the service to be undeployed
            logger.error("Could not found service to deregister: " + serviceId);
            return Response.status(404).entity("Error: Fail to clean service: " + serviceId).build();
        }
    }

    /* (non-Javadoc)
     * @see at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineIntenalInterface#spawnInstance(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public Response spawnInstance(String serviceId,
            String topologyId,
            String nodeId,
            int quantity) throws SalsaEngineException {
        logger.debug("SPAWNING MORE INSTANCE: " + serviceId + "/" + topologyId + "/" + nodeId + ". Quantity:" + quantity);
        
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        TDefinitions def = centerCon.getToscaDescription(serviceId);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit node = service.getComponentById(nodeId);
        if (node == null) {
            EngineLogger.logger.error("May be the id of node is invalided");
            return Response.status(500).entity("Error: Node ID is not found.").build();
        }
        String correctTopologyID = service.getTopologyOfNode(node.getId()).getId();
        centerCon.updateNodeIdCounter(serviceId, correctTopologyID, nodeId, node.getIdCounter() + quantity); // update first the number + quantity		
        String returnVal = "";
        for (int i = node.getIdCounter(); i < node.getIdCounter() + quantity; i++) {
            //centerCon.addInstanceUnit(topologyId, nodeId, i);
            new Thread(new asynSpawnInstances(deployer, serviceId, correctTopologyID, nodeId, i, def, service)).start();
            returnVal += i + " ";
        }
        return Response.status(201).entity(returnVal).build();
    }

    @Override
    public Response scaleOutNode(String serviceId, String nodeId) throws SalsaEngineException {
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceTopology topo = service.getTopologyOfNode(nodeId);

        String instanceId = ((String) spawnInstance(serviceId, topo.getId(), nodeId, 1).getEntity()).trim();
        logger.debug("Generate instance id: " + instanceId);
        service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit unit = service.getComponentById(nodeId);
        ServiceInstance instance = unit.getInstanceById(Integer.parseInt(instanceId));

        int counter = 0;
        while (instance == null || !instance.getState().equals(SalsaEntityState.DEPLOYED)) {
            try {
                if (counter > 300) {
                    break;
                }
                Thread.sleep(3000);
                counter++;
            } catch (Exception e) {
                logger.debug("Interrupt when waiting for the status of the node");
                return Response.status(500).entity("Could not get the IP of the scaled out node").build();
            }
            service = centerCon.getUpdateCloudServiceRuntime(serviceId);
            unit = service.getComponentById(nodeId);
            instance = unit.getInstanceById(Integer.parseInt(instanceId));
            //logger.debug("Scaling out actions: waiting for the node is up. Node: " + unit.getId() + "/" + instance.getInstanceId());
        }

        ServiceUnit hostedUnit = service.getComponentById(unit.getHostedId());
        logger.debug("HostNode: " + hostedUnit.getId() + ". Prepare to get its instance: " + instance.getHostedId_Integer());
        ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());

        logger.debug("HostNode/InstanceId: " + hostedUnit.getId() + "/" + hostedInstance.getInstanceId());
        while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
                && !hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
            hostedUnit = service.getComponentById(hostedUnit.getHostedId());
            hostedInstance = hostedUnit.getInstanceById(hostedInstance.getHostedId_Integer());
            logger.debug("HostNode: " + hostedUnit.getId() + "/" + hostedInstance.getInstanceId());
        }
        SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) hostedInstance.getProperties().getAny();
        return Response.status(201).entity(vm.getPrivateIp()).build();
    }

    public Response scaleInNode(String serviceId, String nodeId) throws SalsaEngineException {
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceTopology topo = service.getTopologyOfNode(nodeId);
        ServiceUnit unit = topo.getComponentById(nodeId);
        List<ServiceInstance> instances = unit.getInstancesList();
        if (instances.size() > 0) {
            return destroyInstance(serviceId, topo.getId(), nodeId, instances.get(0).getInstanceId());
        }
        return Response.status(404).entity("Found no instance to remove").build();
    }

    public Response scaleInVM(String serviceId, String vmIp) throws SalsaEngineException {
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        for (ServiceTopology topo : service.getComponentTopologyList()) {
            for (ServiceUnit unit : topo.getComponentsByType(SalsaEntityType.OPERATING_SYSTEM)) {
                EngineLogger.logger.debug("Scaling in VM. Checking OS unit: " + unit.getId());
                for (ServiceInstance vm : unit.getInstancesList()) {
                    SalsaInstanceDescription_VM vmProp = (SalsaInstanceDescription_VM) vm.getProperties().getAny();
                    if (vmProp.getPrivateIp().equals(vmIp)) {
                        return destroyInstance(serviceId, topo.getId(), unit.getId(), vm.getInstanceId());
                    }
                }
            }
            for (ServiceUnit unit : topo.getComponentsByType(SalsaEntityType.DOCKER)) {
                EngineLogger.logger.debug("Scaling in VM/docker. Checking docker unit: " + unit.getId());
                for (ServiceInstance vm : unit.getInstancesList()) {
                    EngineLogger.logger.debug("Scaling in VM/docker. Checking docker instance: " + vm.getInstanceId());
                    SalsaInstanceDescription_VM vmProp = (SalsaInstanceDescription_VM) vm.getProperties().getAny();
                    if (vmProp.getPrivateIp().equals(vmIp)) {
                        EngineLogger.logger.debug("Scaling in VM. GOT A DOCKER NODE TO SCALE-IN: " + vm.getInstanceId() + "/" + vmProp.getPrivateIp());
                        return destroyInstance(serviceId, topo.getId(), unit.getId(), vm.getInstanceId());
                    }
                }
            }
        }
        return Response.status(404).entity("Not found a VM nodes of IP: " + vmIp).build();
    }

    public Response scaleOutVM(String serviceId, String vmIp) throws SalsaEngineException {
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);

        return null;
    }

    /**
     * This function add new ServiceUnit to the existing application structure, also deploy minimum instances of the node
     *
     * @return The list of the instance IDs
     */
//	@POST
//	@Path("/services/{serviceId}/topologies/{topologyId}/nodes")
//	@Consumes(MediaType.APPLICATION_XML)
//	public Response spawnServiceUnit(
//			String xmlObject,
//			@PathParam("serviceId") String serviceId,
//            @PathParam("topologyId") String topologyId){
//		
//		return null;
//	}
    private class asynSpawnInstances implements Runnable {

        SalsaToscaDeployer deployer;
        TDefinitions def;
        CloudService service;
        String serviceId, topoId, nodeId;
        int instanceId;

        asynSpawnInstances(SalsaToscaDeployer deployer, String serviceId, String topoId, String nodeId, int instanceId, TDefinitions def, CloudService service) {
            this.deployer = new SalsaToscaDeployer(configFile);
            this.serviceId = serviceId;
            this.topoId = topoId;
            this.nodeId = nodeId;
            this.def = def;
            this.service = service;
            this.instanceId = instanceId;
        }

        @Override
        public void run() {
            try {
                deployer.deployOneMoreInstance(serviceId, topoId, nodeId, instanceId, def, service);
            } catch (SalsaEngineException e) {
                EngineLogger.logger.error(e.getMessage());
            }
        }
    }

    @Override
    public Response deployInstance(
            String serviceId,
            String topologyId,
            String nodeId,
            int instanceId) throws SalsaEngineException {
        logger.debug("Deployment request for this node: " + serviceId + " - " + nodeId + " - " + instanceId);
        logger.debug("PUT 1 MORE INSTANCE: " + serviceId + "/" + topologyId + "/" + nodeId);

        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        TDefinitions def = centerCon.getToscaDescription(serviceId);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit node = service.getComponentById(nodeId);
        if (node == null) {
            EngineLogger.logger.error("May be the id of node is invalided");
            return Response.status(500).entity("Error: Node ID is not found.").build();
        }
        String correctTopologyID = service.getTopologyOfNode(node.getId()).getId();
        new Thread(new asynSpawnInstances(deployer, serviceId, correctTopologyID, nodeId, instanceId, def, service)).start();
        return Response.status(201).entity(instanceId).build();
        //TODO: What happen if it is fail to spawn a VM ? 
    }

    @Override
    public Response destroyInstance(
            String serviceId,
            String topologyId,
            String nodeId,
            int instanceId) throws SalsaEngineException {
        logger.debug("Removing service instance: " + serviceId + "/" + topologyId + "/" + nodeId + "/" + instanceId);
        if (deployer.removeOneInstance(serviceId, topologyId, nodeId, instanceId)) {
            return Response.status(200).entity("Undeployed instance: " + serviceId + "/" + nodeId + "/" + instanceId).build();
        } else {
            // return 404: resource not found. The instance is not found to be undeployed
            return Response.status(404).entity("Could not undeployed instance.").build();
        }
    }

    @Override
    public Response removeInstanceMetadata(
            String serviceId,
            String nodeId,
            int instanceId) throws SalsaEngineException {
        // remove metadata
        try {
            MutualFileAccessControl.lockFile();
            String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            //ServiceTopology topo = service.getComponentTopologyById(topologyId);
            ServiceUnit nodeData = service.getComponentById(nodeId);
            ServiceInstance instanceData = nodeData.getInstanceById(instanceId);
            if (nodeData.getReference() != null) {
                return Response.status(404).entity("Cannot remove instance because it is a reference: " + instanceId + ", on node: " + nodeId + ", on service " + serviceId).build();
            }
            // remove hosted on node
            for (ServiceUnit unit : service.getAllComponent()) {
                if (unit.getHostedId().equals(nodeData.getId())) {
                    for (ServiceInstance instance : unit.getInstancesList()) {
                        if (instance.getHostedId_Integer() == instanceData.getInstanceId()) {
                            unit.getInstancesList().remove(instance);
                        }
                    }
                }
            }
            nodeData.getInstancesList().remove(instanceData);
            updateComponentStateBasedOnInstance(service);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return Response.status(404).entity("Cannot remove instance: " + instanceId + ", on node: " + nodeId + ", on service " + serviceId).build();
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        return Response.status(200).entity("Undeployed instance: " + serviceId + "/" + nodeId + "/" + instanceId).build();
    }

    private boolean checkForServiceName(String serviceName) {
        if (serviceName.equals("")) {
            return false;
        }
        String pathName = SalsaConfiguration.getServiceStorageDir();

        ServiceJsonList serviceList = new ServiceJsonList(pathName);
        for (ServiceJsonList.ServiceInfo serv : serviceList.getServicesList()) {
            if (serviceName.equals(serv.getServiceId())) {
                return false;
            }
        }

        return true;
    }

    public Response deployTopology(String serviceId, String topologyId) throws SalsaEngineException {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
        try {
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            List<ServiceUnit> units = topo.getComponentsByType(SalsaEntityType.SOFTWARE);
            for (ServiceUnit u : units) {
                spawnInstance(serviceId, topologyId, u.getId(), 1);
            }
            return Response.status(200).entity("Deploy the whole topology").build();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Response.status(404).entity("Cannot read the service data file").build();
        } catch (JAXBException e1) {
            logger.error(e1.getMessage());
            return Response.status(500).entity("Cannot parse the service data file").build();
        }
    }

    public Response undeployTopology(String serviceId, String topologyId) throws SalsaEngineException {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
        try {
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            List<ServiceUnit> units = topo.getComponentsByType(SalsaEntityType.OPERATING_SYSTEM);
            for (ServiceUnit u : units) {
                for (ServiceInstance instance : u.getInstancesList()) {
                    destroyInstance(serviceId, topologyId, u.getId(), instance.getInstanceId());
                }
            }
            return Response.status(200).entity("Deploy the whole topology").build();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Response.status(404).entity("Cannot read the service data file").build();
        } catch (JAXBException e1) {
            logger.error(e1.getMessage());
            return Response.status(500).entity("Cannot parse the service data file").build();
        }
    }

    public Response destroyInstanceOfNodeType(String serviceId, String topologyId, String nodeId) throws SalsaEngineException {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
        logger.debug("Remove all instances of node: " + nodeId);
        try {
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceUnit unit = service.getComponentById(nodeId);
            for (ServiceInstance instance : unit.getInstancesList()) {
                destroyInstance(serviceId, topologyId, nodeId, instance.getInstanceId());
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Response.status(404).entity("Cannot read the service data file").build();
        } catch (JAXBException e1) {
            logger.error(e1.getMessage());
            return Response.status(500).entity("Cannot parse the service data file").build();
        }

        return Response.status(200).entity("Undeploy nodes").build();
    }

    /*
     * INTERNAL SERVICES FOR CLOUD SERVICES
     */
	//static boolean getServiceLocked=false;
    @Override
    public Response getService(String serviceDeployId) throws SalsaEngineException {
        if (serviceDeployId.equals("")) {
            return Response.status(400).entity("").build();
        }
        String fileName = SalsaConfiguration.getServiceStorageDir() + "/" + serviceDeployId + ".data";
        // wait for unlock !
//		int count=0;
//		while (!getServiceLocked && count < 50){ // wait for maximum 5 sec and release the lock 			
//			try{
//				Thread.sleep(100);
//			} catch (InterruptedException e) {}
//		}
//		getServiceLocked=false;
        try {
            String xml = FileUtils.readFileToString(new File(fileName));
            return Response.status(200).entity(xml).build();
        } catch (Exception e) {
            logger.error("Could not find service: " + serviceDeployId + ". Data did not be sent. Error: " + e.toString());
            logger.debug("THROWING AN EXCEPTION OF FAILURE TO GET SERVICE !");
            throw new SalsaEngineException("Could not find service: " + serviceDeployId, false);
        }
    }

//	@Override
//	public Response getServiceAndLock(@PathParam("serviceId")String serviceDeployId){
//		getServiceLocked = true;
//		return getService(serviceDeployId);
//	}
//	
//	@Override
//	public Response getServiceToUnLock(@PathParam("serviceId")String serviceDeployId){
//		getServiceLocked=false;
//		return Response.status(200).entity("done").build();
//	}
    @Override
    public Response getToscaService(String serviceDeployId) {
        logger.debug("Read Tosca file and return !");
        if (serviceDeployId.equals("")) {
            return Response.status(400).entity("").build();
        }
        String fileName = SalsaConfiguration.getServiceStorageDir() + "/" + serviceDeployId;
        try {
            String xml = FileUtils.readFileToString(new File(fileName));
            return Response.status(200).entity(xml).build();
        } catch (Exception e) {
            logger.error("Could not find service: " + serviceDeployId + ". Data did not be sent. Error: " + e.toString());
            logger.debug("YOU ARE HERE FAIL !!");
            return Response.status(500).entity("").build();
        }
    }

    @Override
    public Response getServiceSYBL_DEP_DESP(String serviceId) {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
        logger.debug("Generating deployment desp for SYBL");
        try {
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            logger.debug("Service id: " + service.getId());
            DeploymentDescription sybl = new DeploymentDescription();
            sybl.setAccessIP("localhost");
            for (ServiceTopology topo : service.getComponentTopologyList()) {
                logger.debug("Topo ID: " + topo.getId());
                List<ServiceUnit> units = topo.getComponentsByType(SalsaEntityType.SOFTWARE);
                units.addAll(topo.getComponentsByType(SalsaEntityType.WAR));
                sybl.setCloudServiceID(serviceId);
                for (ServiceUnit unit : units) {
                    logger.debug("NodeID: " + unit.getId());
                    DeploymentUnit syblDepUnit = new DeploymentUnit();
                    syblDepUnit.setServiceUnitID(unit.getId());

                    for (ServiceInstance instance : unit.getInstancesList()) {
                        ServiceUnit hostedUnit = topo.getComponentById(unit.getHostedId());
                        ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());
                        // in the case we have more than one software stack
                        while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
                                && !hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
                            hostedUnit = topo.getComponentById(hostedUnit.getHostedId());
                            hostedInstance = hostedUnit.getInstanceById(hostedInstance.getHostedId_Integer());
                        }
                        logger.debug("Host instance: " + hostedUnit.getId() + "/" + hostedInstance.getInstanceId());
                        SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) hostedInstance.getProperties().getAny();
                        AssociatedVM assVM = new AssociatedVM();
                        assVM.setIp(vm.getPrivateIp().trim());
                        assVM.setUuid(vm.getInstanceId());
                        syblDepUnit.addAssociatedVM(assVM);
                    }
                    sybl.getDeployments().add(syblDepUnit);
                }
            }
            JAXBContext a = JAXBContext.newInstance(DeploymentDescription.class);
            Marshaller mar = a.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter xmlWriter = new StringWriter();
            mar.marshal(sybl, xmlWriter);
            return Response.status(200).entity(xmlWriter.toString()).build();
        } catch (JAXBException e1) {
            String errorMsg = "Internal error: JAXB couldn't parse the service data";
            logger.error(errorMsg);
            return Response.status(500).entity(errorMsg).build();
        } catch (IOException e2) {
            String errorMsg = "Internal error: Couldn't read the service data.";
            logger.error(errorMsg);
            return Response.status(500).entity(errorMsg).build();
        }
    }

    private String getIpOfServiceInstance(String serviceId, String nodeId, int instanceId) {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
        try {
            logger.debug("Searching IP of instance");
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceUnit unit = service.getComponentById(nodeId);
            ServiceInstance instance = unit.getInstanceById(instanceId);
            logger.debug("Searching IP of instance 1");
            ServiceUnit hostedUnit = service.getComponentById(unit.getHostedId());
            logger.debug("Searching IP of instance 2");
            ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());
            logger.debug("Searching IP of instance 3");
            while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
                    && !hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
                logger.debug("Searching IP of instance 4");
                hostedUnit = service.getComponentById(hostedUnit.getHostedId());
                hostedInstance = hostedUnit.getInstanceById(hostedInstance.getHostedId_Integer());
            }
            logger.debug("Searching IP of instance 5");
            if (hostedInstance.getProperties() == null) {
                return "";
            }
            SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) hostedInstance.getProperties().getAny();
            logger.debug("Searching IP of instance 6. VMID=" + vm.getInstanceId());
            logger.debug("Searching IP of instance 6. IP=" + vm.getPrivateIp());
            return vm.getPrivateIp().trim();
        } catch (IOException e) {
            logger.debug(e.getMessage());
            return "";
        } catch (JAXBException e1) {
            logger.debug(e1.getMessage());
            return "";
        }
    }

    /*
     * INTERFNAL SERVICES FOR TOPOLOGY
     */
    /* (non-Javadoc)
     * @see at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineIntenalInterface#addRelationship(javax.xml.bind.JAXBElement, java.lang.String, java.lang.String)
     */
    @Override
    public Response addRelationship(
            ServiceUnitRelationship data,
            String serviceId,
            String topologyId) {
        String salsaFile = SalsaConfiguration.getServiceStorageDir()
                + File.separator + serviceId + ".data";
        try {
            MutualFileAccessControl.lockFile();
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            SalsaReplicaRelationships rels = topo.getRelationships();
            if (rels == null) {
                rels = new SalsaReplicaRelationships();
                topo.setRelationships(rels);
            }
            //rels.addRelationship(data.getValue());
            rels.addRelationship(data);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
        } catch (Exception e) {
            logger.error("Could not add relationship for: " + serviceId + ", " + topologyId);
            logger.error(e.toString());
            return Response.status(404).entity("Error update node status").build();
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        return Response.status(200).entity("Updated relationship ").build();

    }

    /* (non-Javadoc)
     * @see at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineIntenalInterface#updateNodeIdCounter(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public Response updateNodeIdCounter(
            String serviceId,
            String topologyId,
            String nodeId,
            int value) {
        try {
            MutualFileAccessControl.lockFile();
            String salsaFile = SalsaConfiguration.getServiceStorageDir()
                    + File.separator + serviceId + ".data";
            CloudService service = SalsaXmlDataProcess
                    .readSalsaServiceFile(salsaFile);
            ServiceTopology topo = service
                    .getComponentTopologyById(topologyId);
            ServiceUnit nodeData = topo.getComponentById(nodeId);
            nodeData.setIdCounter(value);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
        } catch (Exception e) {
            logger.error(e.toString());
            return Response.status(404).entity("Cannot uodate node " + nodeId + " on service " + serviceId
                    + " node ID counter: " + value).build();
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        return Response.status(200).entity("Updated node " + nodeId + " on service " + serviceId
                + " node ID counter: " + value).build();
    }

    /*
     * INTERNAL SERVICES FOR INSTANCE UNITS
     */
    @Override
    public Response addInstanceUnitMetaData(ServiceInstance data,
            String serviceId,
            String topologyId,
            String nodeId) {
        EngineLogger.logger.debug("addInstanceUnitMetaData. STARTING: " + serviceId + "/" + topologyId + "/" + nodeId + "/" + data.getInstanceId());
        String fileName = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
        try {
            MutualFileAccessControl.lockFile();
            EngineLogger.logger.debug("addInstanceUnitMetaData. STARTING TO TRY BLOCK");
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(fileName);
            EngineLogger.logger.debug("addInstanceUnitMetaData. Compo.id" + service.getId());
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            EngineLogger.logger.debug("addInstanceUnitMetaData. Compo.id" + topo.getId());
            ServiceUnit compo = topo.getComponentById(nodeId);
            EngineLogger.logger.debug("addInstanceUnitMetaData. Compo.id" + compo.getId());
            ServiceInstance replicaData = data;//.getValue();
            EngineLogger.logger.debug("addInstanceUnitMetaData. Instance id: " + replicaData.getInstanceId());
            int id = replicaData.getInstanceId();
            ServiceInstance existedInstance = compo.getInstanceById(id);
            if (existedInstance == null) {
                EngineLogger.logger.debug("Create new instance meta-data");
                compo.addInstance(replicaData);
            } else {
                EngineLogger.logger.debug("Update old instance meta-data");
                compo.getInstanceById(id).setHostedId_Integer(replicaData.getHostedId_Integer());
                existedInstance = replicaData;
            }
            EngineLogger.logger.debug("addInstanceUnitMetaData. writing down service id: " + service.getId());
            SalsaXmlDataProcess.writeCloudServiceToFile(service, fileName);
        } catch (JAXBException e1) {
            logger.error(e1);
        } catch (IOException e2) {
            logger.error(e2);
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        MutualFileAccessControl.releaseFile();
        return Response.status(201).entity("Spawned VM").build();
        //TODO: What happen if it is fail to spawn a VM ? 
    }

    @Override
    public Response addServiceUnitMetaData(ServiceUnit data,
            String serviceId, String topologyId) {
        String fileName = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
        String toscaFileName = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId;
        try {
            MutualFileAccessControl.lockFile();
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(fileName);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            topo.addComponent(data);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, fileName);

            // update the Tosca
            TDefinitions def = ToscaXmlProcess.readToscaFile(toscaFileName);
            TNodeTemplate toscaNode = new TNodeTemplate();
            toscaNode.setId(data.getId());
            toscaNode.setMinInstances(data.getMin());
            toscaNode.setMaxInstances(Integer.toString(data.getMax()));
            toscaNode.setType(new QName(data.getType()));
            TDeploymentArtifact dA = new TDeploymentArtifact();
            dA.setArtifactRef(new QName(toscaNode.getId() + "_artifact"));
            dA.setArtifactType(new QName(data.getArtifactType()));
            TDeploymentArtifacts dAs = new TDeploymentArtifacts();
            dAs.getDeploymentArtifact().add(dA);
            toscaNode.setDeploymentArtifacts(dAs);
            //ToscaStructureQuery.getFirstServiceTemplate(def).getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().add(toscaNode);
            ToscaStructureQuery.getTopologyTemplate(topologyId, def).getNodeTemplateOrRelationshipTemplate().add(toscaNode);

            TArtifactTemplate artTemp = new TArtifactTemplate();
            ArtifactReferences artRefs = new ArtifactReferences();
            TArtifactReference artRef = new TArtifactReference();
            artRef.setReference(data.getArtifactURL());

            artRefs.getArtifactReference().add(artRef);
            artTemp.setArtifactReferences(artRefs);
            artTemp.setId(toscaNode.getId() + "_artifact");
            artTemp.setType(new QName(data.getArtifactType()));
            def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(artTemp);

            ServiceUnit hostNode = service.getComponentById(topologyId, data.getHostedId());
            if (hostNode == null) {
                logger.error("Cannot find the host node for this node.");
                return Response.status(401).entity("Cannot find the host node for this node.").build();
            }
            TNodeTemplate toscaHostNode = ToscaStructureQuery.getNodetemplateById(hostNode.getId(), def);
            TRelationshipTemplate rela = new TRelationshipTemplate();
            SourceElement source = new SourceElement();
            source.setRef(toscaNode);
            TargetElement target = new TargetElement();
            target.setRef(toscaHostNode);
            rela.setSourceElement(source);
            rela.setTargetElement(target);
            //ToscaStructureQuery.getFirstServiceTemplate(def).getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().add(rela);
            ToscaStructureQuery.getTopologyTemplate(topologyId, def).getNodeTemplateOrRelationshipTemplate().add(rela);

            ToscaXmlProcess.writeToscaDefinitionToFile(def, toscaFileName);
        } catch (IOException e1) {
            logger.error(e1);
        } catch (JAXBException e2) {
            logger.error(e2);
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        MutualFileAccessControl.releaseFile();
        return Response.status(201).entity("Added service unit").build();
    }

    @Override
    public Response addServiceUnitMetaData(String toscaXML,
            String serviceId, String topologyId) {
        String fileName = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
        try {
            MutualFileAccessControl.lockFile();
            TNodeTemplate toscaNode = ToscaXmlProcess.readToscaNodeTemplateFromString(toscaXML);
            if (toscaNode == null || toscaNode.getId() == null || toscaNode.getType() == null) {
                logger.error("Wrong format of ToscaXML !");
                return Response.status(404).entity("Wrong format of ToscaXML !").build();
            }
            String id = toscaNode.getId();
            String type = toscaNode.getType().getLocalPart();
            ServiceUnit data = new ServiceUnit(id, type);
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(fileName);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            topo.addComponent(data);
        } catch (IOException e1) {
            logger.error(e1);
        } catch (JAXBException e2) {
            logger.error(e2);
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        MutualFileAccessControl.releaseFile();
        return Response.status(201).entity("Added service unit").build();
    }

    @Override
    public Response updateInstanceUnitCapability(SalsaCapaReqString data,
            String serviceId,
            String topologyId,
            String nodeId,
            int instanceId) {
        MutualFileAccessControl.lockFile();
        try {
            String serviceFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(serviceFile);
            ServiceInstance rep = service.getInstanceById(nodeId, instanceId);
            Capabilities capas = rep.getCapabilities();
            if (capas == null) { // there is no capability list before, create a new
                capas = new Capabilities();
                rep.setCapabilities(capas);
            }
            List<SalsaCapaReqString> capaLst = capas.getCapability();	// get the list			
            //capaLst.add(data.getValue());
            // replace data if the value is "salsa:ip"
            if (data.getValue().equals("salsa:localIP")) {
                logger.debug("Update instance unit capability - Get string => salsa:localIP");
                data.setValue(getIpOfServiceInstance(serviceId, nodeId, instanceId));
            }
            capaLst.add(data);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, serviceFile);
        } catch (Exception e) {
            logger.error("Could not read service for update capability: " + serviceId + "/" + topologyId + "/" + nodeId + "/" + instanceId);
            e.printStackTrace();
            return Response.status(404).entity("Error update capability").build();
        } finally {
            MutualFileAccessControl.releaseFile();
        }

        return Response.status(200).entity("Updated capability for node: " + nodeId + " on service "
                + serviceId).build();
    }

    @Override
    public Response updateInstanceUnitProperties(
            //JAXBElement<Object> data,
            String data,
            String serviceId,
            String topologyId,
            String nodeId,
            int instanceId) {
        logger.debug("update instance unit prop 1. Raw string data: " + data);
        MutualFileAccessControl.lockFile();
        try {
            logger.debug("update instance unit prop 2");
            String serviceFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
            logger.debug("Setting property. Read service file: " + serviceFile);
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(serviceFile);
            ServiceUnit unit = service.getComponentById(nodeId);
            logger.debug("update instance unit prop 3: " + service.getId());
            ServiceInstance rep = service.getInstanceById(nodeId, instanceId);

            Properties props = rep.getProperties();
            if (props == null) {
                props = new Properties();
            }
            // marshall data and add to props. Currently only can receive VM and Docker properties
            JAXBContext context;            
//            context = JAXBContext.newInstance(SalsaInstanceDescription_Docker.class, SalsaInstanceDescription_VM.class); // both, not sure it run
            if (unit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
                logger.debug("update instance unit: marshall VM description ");
                context = JAXBContext.newInstance(SalsaInstanceDescription_VM.class);
                Unmarshaller um = context.createUnmarshaller();
                SalsaInstanceDescription_VM propData = (SalsaInstanceDescription_VM) um.unmarshal(new StringReader(data));
                logger.debug("VM properties captured: " + propData.getInstanceId() +", ip: " + propData.getPrivateIp());
                props.setAny(propData);
            } else if (unit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())){
                logger.debug("update instance unit: marshall DOCKER description ");
                context = JAXBContext.newInstance(SalsaInstanceDescription_Docker.class);
                Unmarshaller um = context.createUnmarshaller();
                SalsaInstanceDescription_Docker propData = (SalsaInstanceDescription_Docker)um.unmarshal(new StringReader(data));
                logger.debug("Docker properties captured: " + propData.getDockername() +", portmap: " + propData.getPortmap());
                props.setAny(propData);
            } 
            
            
            rep.setProperties(props);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, serviceFile);
            logger.debug("update instance unit prop END: " + service.getId());

        } catch (JAXBException e) {
            logger.error("updateInstanceUnitProperties - Cannot parse the service file: " + serviceId);
            logger.error(e);
            return Response.status(404).entity("updateInstanceUnitProperties - Error update node property").build();
        } catch (IOException e1) {
            logger.error("updateInstanceUnitProperties - Could not read service for update property: " + serviceId);
            logger.error(e1);
            return Response.status(500).entity("updateInstanceUnitProperties - Error update node property").build();
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        return Response.status(200).entity("Updated capability for node: " + nodeId + ", replica: " + instanceId + " on service "
                + serviceId).build();
    }

    @Override
    public Response updateNodeState(String serviceId,
            String topologyId, // topology is not need
            String nodeId,
            int instanceId,
            String value) {
        try {
            java.util.Date date = new java.util.Date();
            logger.debug("UPDATE NODE STATE: " + nodeId + ", instance: " + instanceId + ", state: " + value);
            MutualFileAccessControl.lockFile();
            String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceUnit nodeData = service.getComponentById(nodeId);

            if (instanceId == -1) {	// update for node data
                nodeData.setState(SalsaEntityState.fromString(value));
                updateComponentStateBasedOnInstance(service);
                SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
            } else { // update for instance
                ServiceInstance replicaInst = nodeData.getInstanceById(instanceId);
                if (SalsaEntityState.fromString(value) != null) {
                    replicaInst.setState(SalsaEntityState.fromString(value));
                    updateComponentStateBasedOnInstance(service);	// update the Tosca node
                    SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
                    // ToscaXmlProcess.writeToscaDefinitionToFile(def, toscaFile);
                } else {

                    return Response.status(404).entity("Unknown node state of node " + nodeId
                            + " on service " + serviceId
                            + " deployed status: " + value).build();
                }
            }

        } catch (Exception e) {
            logger.error("Could not read service for update node status: " + nodeId + "/" + instanceId);
            logger.error(e.toString());
            e.printStackTrace();
            return Response.status(500).entity("Error update node status").build();
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        logger.debug("UPDATE NODE STATE: " + nodeId + ", instance: " + instanceId + ", state: " + value + " - DONE !");
        return Response.status(200).entity("Updated node " + nodeId + " on service " + serviceId + " deployed status: " + value).build();
    }

    @Override
    public Response getRequirementValue(
            String serviceId,
            String topologyId,
            String nodeId,
            int instanceId,
            String reqId) {
        try {
            // read current TOSCA and SalsaCloudService
            String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
//			SalsaComponentData nodeData = topo.getComponentById(nodeId);
//			SalsaComponentInstanceData instanceData = nodeData.getInstanceById(instanceId);		

            String toscaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId;
            TDefinitions def = ToscaXmlProcess.readToscaFile(toscaFile);
            TRequirement req = (TRequirement) ToscaStructureQuery.getRequirementOrCapabilityById(reqId, def);
            TCapability capa = ToscaStructureQuery.getCapabilitySuitsRequirement(req, def);
            String capaid = capa.getId();
            TNodeTemplate toscanode = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(capa, def);
            // get the capability of the first instance of the node which have id
            ServiceUnit nodeData = topo.getComponentById(toscanode.getId());
            if (nodeData.getInstancesList().size() == 0) {
                return Response.status(201).entity("").build();
            }
            ServiceInstance nodeInstanceOfCapa = nodeData.getInstanceById(0);
            String reqAndCapaValue = nodeInstanceOfCapa.getCapabilityValue(capaid);
            return Response.status(200).entity(reqAndCapaValue).build();
        } catch (IOException e1) {
            logger.error(e1);
        } catch (JAXBException e2) {
            logger.error(e2);
        }
        return Response.status(201).entity("").build();
    }

    @Override
    public Response queueAction(
            String serviceId,
            String nodeId,
            int instanceId,
            String actionName) {
        try {
            EngineLogger.logger.debug("Queueing action: " + serviceId + "/" + nodeId + "/" + instanceId + "/" + actionName);
            MutualFileAccessControl.lockFile();
            String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceUnit nodeData = service.getComponentById(nodeId);

            //ConfigurationCapability confCapa = nodeData.getCapabilityByName(actionName);
            ServiceInstance instance = nodeData.getInstanceById(instanceId);
            instance.queueAction(actionName);
            instance.setState(SalsaEntityState.STAGING_ACTION);

            SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
        } catch (Exception e) {
            logger.error(e.toString());
            return Response.status(404).entity("Cannot queue task for node " + nodeId + " on service " + serviceId
                    + " node ID counter: " + actionName).build();
        } finally {
            MutualFileAccessControl.releaseFile();
        }

        return Response.status(201).entity("ok").build();
    }

    @Override
    public Response unqueueAction(
            String serviceId,
            String nodeId,
            int instanceId) {
        try {
            EngineLogger.logger.debug("Unqueueing action: " + serviceId + "/" + nodeId + "/" + instanceId);
            MutualFileAccessControl.lockFile();
            String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
            EngineLogger.logger.debug("Debug - Unqueueing action 1");
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            EngineLogger.logger.debug("Debug - Unqueueing action 2");
            ServiceUnit nodeData = service.getComponentById(nodeId);
            EngineLogger.logger.debug("Debug - Unqueueing action 3. Node: " + nodeData.getId());
            ServiceInstance instance = nodeData.getInstanceById(instanceId);
            EngineLogger.logger.debug("Debug - Unqueueing action 4");
            EngineLogger.logger.debug("Debug - Instance: " + instance.getInstanceId() + "/" + instance.getInstanceState());
            instance.unqueueAction();
            EngineLogger.logger.debug("Debug - Unqueueing action 5");

            SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return Response.status(404).entity("Cannot queue task for node " + nodeId + " on service " + serviceId).build();
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        return Response.status(201).entity("ok").build();
    }

    @Override
    public Response getPioneerArtifact(String fileName) {
        logger.debug("Getting pioneer artifact and return: " + SalsaConfiguration.getPioneerLocalFile());
        File file = new File(SalsaConfiguration.getPioneerLocalFile());
        if (fileName == null || fileName.equals("")) {
            fileName = file.getName();
        }
        if (file.exists()) {
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();
        }
        logger.debug("Not found the pioneer artifact !");
        return Response.status(404).entity("Do not found pioneer artifact: " + file.getAbsolutePath()).build();
    }

    protected void updateComponentStateBasedOnInstance(SalsaEntity nodeData) {
        Map<SalsaEntityState, Integer> rankState = new HashMap<>();
        rankState.put(SalsaEntityState.UNDEPLOYED, 0);
        rankState.put(SalsaEntityState.ERROR, 1);
        rankState.put(SalsaEntityState.ALLOCATING, 2);
        rankState.put(SalsaEntityState.STAGING, 3);
        rankState.put(SalsaEntityState.STAGING_ACTION, 4);
        rankState.put(SalsaEntityState.CONFIGURING, 5);
        //rankState.put(SalsaEntityState.INSTALLING, 6);
        rankState.put(SalsaEntityState.DEPLOYED, 7);
        rankState.put(SalsaEntityState.INSTALLING, 8);
        if (nodeData.getClass().equals(CloudService.class)) {
            logger.debug("Updating the state of the whole service !");
        }

        List<SalsaEntity> insts = new ArrayList<>();
        if (nodeData.getClass().equals(ServiceUnit.class)) {
            List<ServiceInstance> serviceInst = ((ServiceUnit) nodeData).getInstancesList();
            for (ServiceInstance instance : serviceInst) {
                insts.add(instance);
            }
        } else if (nodeData.getClass().equals(ServiceTopology.class)) {
            List<ServiceUnit> serviceUnits = ((ServiceTopology) nodeData).getComponents();
            for (ServiceUnit unit : serviceUnits) {
                updateComponentStateBasedOnInstance(unit);
                insts.add(unit);
            }
        } else if (nodeData.getClass().equals(CloudService.class)) {
            List<ServiceTopology> serviceTopos = ((CloudService) nodeData).getComponentTopologyList();
            for (ServiceTopology topo : serviceTopos) {
                updateComponentStateBasedOnInstance(topo);
                insts.add(topo);
            }
        }

        int minState = 8;
        if (insts.isEmpty()) {
            nodeData.setState(SalsaEntityState.UNDEPLOYED);
            return;
        }
        for (SalsaEntity inst : insts) {
            if (minState >= rankState.get(inst.getState())) {
                nodeData.setState(inst.getState());
                minState = rankState.get(inst.getState());
            }
        }
    }

    private CloudService getCloudService(String serviceId) {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + ".data";
        try {
            return SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
        } catch (JAXBException | IOException e) {
            logger.debug("Not found the pioneer artifact !");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String health() {
        try {
            List<String> str = SystemFunctions.getEndPoints();
            String rs = "";
            for (String s : str) {
                rs += s + ",";
            }
            return rs;
        } catch (Exception e) {
            return "Working but cannot get the endpoint";
        }
    }

    @Override
    public Response logMessage(String data) {
        EngineLogger.logger.debug("Receive a log message from pioneer: " + data);
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/tmp/salsa.pioneer.message.log", true)))) {
            out.println(data);
            out.close();
        } catch (IOException e) {
            EngineLogger.logger.error(e.toString());
        }
        return Response.status(201).entity("saved message: " + data).build();
    }

    private Response logAndResponse(LOGLEVEL level, String message) {
        switch (level) {
            case INFO:
                logger.info(message);
                return Response.status(200).entity(message).build();
            case USER_ERROR:
                logger.error(message);
                return Response.status(404).entity(message).build();
            case SERVER_ERROR:
                logger.error(message);
                return Response.status(500).entity(message).build();
            default:
                logger.debug(message);
                return Response.status(200).entity(message).build();
        }
    }

    private enum LOGLEVEL {

        INFO, USER_ERROR, SERVER_ERROR, DEBUG
    }

}
