package at.ac.tuwien.dsg.cloud.salsa.pioneer;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TDeploymentArtifact;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRequirement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.PrimitiveOperation;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.StacksConfigurator.DockerConfigurator;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.AptGetInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.BashContinuousInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.BashInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.ChefInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.ChefSoloInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.InstrumentInterface;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.InstrumentShareData;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.WarInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.type.PropertyVMExpose;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.type.SalsaArtifactType;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import generated.oasis.tosca.TArtifactTemplate;
import java.io.FileReader;
import java.util.logging.Level;

public class ArtifactDeployer {

    private String serviceId;
    private String topologyId;
    private String nodeId;	// this is VM node ID. It may contain other nodes on it
    private int hostedVmInstanceId;		// this is Replica number of the VM
    private TDefinitions def;
    private SalsaCenterConnector centerCon;
    private CloudService serviceRuntimeInfo;
    private Logger logger = LoggerFactory.getLogger("PioneerLogger");

    public ArtifactDeployer(String serviceId, String topologyId, String nodeId, int thisInstanceId, TDefinitions def, SalsaCenterConnector centerCon, CloudService serviceRuntimeInfo) {
        this.serviceId = serviceId;
        this.topologyId = topologyId;
        this.nodeId = nodeId;
        this.hostedVmInstanceId = thisInstanceId;
        this.def = def;
        this.centerCon = centerCon;
        this.serviceRuntimeInfo = serviceRuntimeInfo;
    }

    // Deploy upper nodes which are hosted on the current VM node
    public void deployNodeChain(TNodeTemplate thisNode)
            throws IOException, SalsaEngineException {
			// download full topology from web
        //def = centerCon.updateTopology();
        //PioneerLogger.logger.debug("Update topology service: " + def.getId());
        // List of upper nodes, which will be deployed

        List<TNodeTemplate> upperNodes = ToscaStructureQuery
                .getNodeTemplateWithRelationshipChain("HOSTON", thisNode, def);

        logger.debug("Chain for node: " + thisNode.getId());
        for (TNodeTemplate chainNode : upperNodes) {
				//SalsaInstanceDescription_Artifact artiProps = new SalsaInstanceDescription_Artifact();			

            logger.debug("Starting deploy node: " + chainNode.getId());
				// don't need it ? setting state for ServiceUnit, not for Instance. Set -1 as instanceId will do the job
            //centerCon.updateNodeState(topologyId, chainNode.getId(), -1, SalsaEntityState.ALLOCATING);

            // Get the number of node to be deploy
            int quantity = chainNode.getMinInstances();
            logger.debug("Number of instance to deploy: " + quantity);
            serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);

            int startId = serviceRuntimeInfo.getComponentById(topologyId, chainNode.getId()).getIdCounter();
            logger.debug("Starting ID: " + startId);
            centerCon.updateNodeIdCounter(serviceId, topologyId, chainNode.getId(), startId + quantity);

            // calculate the quantity of node to be deploy
            ServiceUnit unit = serviceRuntimeInfo.getComponentById(topologyId, chainNode.getId());
            int existInstanceNumber = unit.getInstanceHostOn(hostedVmInstanceId).size();

            List<Integer> instanceIdList = new ArrayList<>();
            // Create quantity node instances(instance of software) for this chainNode(software)
            for (int i = startId; i < startId + quantity - existInstanceNumber; i++) {
                instanceIdList.add(i);
                ServiceInstance data = new ServiceInstance(i);
                data.setHostedId_Integer(hostedVmInstanceId);
                data.setState(SalsaEntityState.ALLOCATING);	// waiting for other conditions
                centerCon.addInstanceUnitMetaData(serviceId, topologyId, chainNode.getId(), data);	// add the 					
            }
            waitingForCapabilities(chainNode, def);
            // wait for downloading and configuring artifact itself
            logger.debug("OK, we have " + instanceIdList.size() + " of instances");
            for (Integer i : instanceIdList) {
                logger.debug("Set status for instance " + i + " to CONFIGURING !");
                centerCon.updateNodeState(serviceId, topologyId, chainNode.getId(), i, SalsaEntityState.CONFIGURING);
            }
				//downloadNodeArtifacts(chainNode, def);
            // execute multi threads for multi instance
            multiThreadRunArtifacts(chainNode, instanceIdList);

        }

    }

    // a NodeTemplate can have multiple DeploymentArtifact. This get FIRST the reference of type.
    private String getDeploymentArtifactURIOfType(TNodeTemplate node, String type) {
        if (node.getDeploymentArtifacts() == null) {
            logger.debug("getDeploymentArtifactURIOfType: Having no Deployment artifact in node: " + node.getId());
            return null;
        }
        for (TDeploymentArtifact deArt : node.getDeploymentArtifacts().getDeploymentArtifact()) {
            logger.debug("getDeploymentArtifactURIOfType: Checking node: " + node.getId() + ", artifact: " + deArt.getName() + ", type:" + deArt.getArtifactType().getLocalPart());
            if (deArt.getArtifactType().getLocalPart().equals(type)) {
                TArtifactTemplate artifact = ToscaStructureQuery.getArtifactTemplateById(deArt.getArtifactRef().getLocalPart(), def);
                if (artifact != null) {
                    logger.debug("getDeploymentArtifactURIOfType: Checking node: " + node.getId() + " ==> RETURN: " + artifact.getArtifactReferences().getArtifactReference().get(0).getReference());
                    return artifact.getArtifactReferences().getArtifactReference().get(0).getReference();
                }
            }
        }
        logger.debug("getDeploymentArtifactURIOfType: Checking node: " + node.getId() + " ==> RETURN NULL");
        return null;
    }

    public String deploySingleNode(TNodeTemplate node, int instanceId) throws SalsaEngineException {
        logger.debug("Starting deploy node: " + node.getId() + "/" + instanceId);
        centerCon.logMessage("Deploy Single Node: " + node.getId() + "/" + instanceId + " of service: " + serviceId);
        serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);
        // check if node is a docker node, install docker and start new container
        if (node.getType().getLocalPart().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
            // check of download Node have some artifact
            downloadNodeArtifacts(node, def, instanceId);
            PioneerLogger.logger.debug("THIS IS THE DOCKER NODE, INSTALL IT !");
            DockerConfigurator docker = new DockerConfigurator(node.getId());
            centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.CONFIGURING);

            String dockerFileURI = getDeploymentArtifactURIOfType(node, SalsaArtifactType.dockerfile.getString());

            // run docker with salsa if there is no deployment Artifact of  dockerfile
            if (dockerFileURI == null) {
                docker.initDocker(true);
                logger.debug("There is no dockerfile input, use the default docker file with SALSA pioneer ! Node/instance: " + node.getId() + "/" + instanceId);
                String containerID = docker.installDockerNodeWithSALSA(node.getId(), instanceId);
                logger.debug("Create docker container done. ID: " + containerID);
                SalsaInstanceDescription_VM dockerVM = docker.getDockerInfo(containerID);
                centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.INSTALLING);
                logger.debug("Updating docker info. ID = " + dockerVM.getInstanceId() + ", IP = " + dockerVM.getInstanceId());
                centerCon.updateInstanceUnitProperty(serviceId, topologyId, nodeId, instanceId, dockerVM);
                return Integer.toString(instanceId);
            } else {    // run a docker file
                String dockerFileName = FilenameUtils.getName(dockerFileURI);
                logger.debug("Found a dockerfile, prepare to run: " + dockerFileName);
                docker.initDocker(false);
                String containerID = docker.installDockerNodeWithDockerFile(node.getId(), instanceId, dockerFileName);
                logger.debug("Create docker container done. ID: " + containerID);
                SalsaInstanceDescription_VM dockerVM = docker.getDockerInfo(containerID);
                if (containerID==null || containerID.isEmpty()){
                    centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.ERROR);
                    return null;
                }
                // read first line of the dockerfile
                String localDockerFile = SalsaPioneerConfiguration.getWorkingDirOfInstance(node.getId(), instanceId)+"/Dockerfile";
                try {                    
                    BufferedReader brTest = new BufferedReader(new FileReader(localDockerFile));
                    String text = brTest.readLine().substring(5); // extract the name only in the first line: FROM imageName
                    dockerVM.setBaseImage(text);
                } catch (IOException e) {
                    logger.error("Cannot read dockerfile at: " + localDockerFile);
                }
                centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.INSTALLING);
                while (!dockerVM.getState().equals("RUNNING")){
                    dockerVM = docker.getDockerInfo(containerID);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        PioneerLogger.logger.error("Interrupt sleep!");
                    }
                }
                
                centerCon.updateInstanceUnitProperty(serviceId, topologyId, nodeId, instanceId, dockerVM);
                logger.debug("Updating docker info. ID = " + dockerVM.getInstanceId() + ", IP = " + dockerVM.getInstanceId());
                centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.DEPLOYED);
                return Integer.toString(instanceId);
            }

        }

        // check if it is hosted by a docker, forward the request to that docker
        logger.debug("[123456] 1 Starting deploy node: " + node.getId() + "/" + instanceId);
        ServiceUnit unit = serviceRuntimeInfo.getComponentById(node.getId());
        logger.debug("[123456] 2 Starting deploy node: " + node.getId() + "/" + instanceId);
        ServiceInstance instance = serviceRuntimeInfo.getInstanceById(node.getId(), instanceId);
        logger.debug("[123456] 3 Starting deploy node: " + node.getId() + "/" + instanceId);

        ServiceUnit hostNode = serviceRuntimeInfo.getComponentById(unit.getHostedId());
        logger.debug("[123456] 4 Starting deploy node: " + node.getId() + "/" + instanceId);
        logger.debug("[123456] 4.1 unit.getHostID: " + unit.getHostedId() + "/ instance.gethostid_integer: " + instance.getHostedId_Integer());
        ServiceInstance hostInstance = serviceRuntimeInfo.getInstanceById(unit.getHostedId(), instance.getHostedId_Integer());
        logger.debug("[123456] 5 Starting deploy node: " + node.getId() + "/" + instanceId);

        // Get the number of node to be deploy
        int quantity = 1;
        logger.debug("Number of instance to deploy: " + quantity);
        serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);
        logger.debug("Instance ID: " + instanceId);

        waitingForCapabilities(node, def);
			// wait for downloading and configuring artifact itself

        logger.debug("Set status for instance " + instanceId + " to CONFIGURING !");
        centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.CONFIGURING);

        boolean downloadDone = downloadNodeArtifacts(node, def, instanceId);
        
        if (!downloadDone){
            centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.ERROR);
            return null;
        }
        
        // deploy the artifact
        logger.debug("Executing the deployment for node: " + node.getId() + ", instance: " + instanceId);

        centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.INSTALLING);

        runNodeArtifacts(node, Integer.toString(instanceId), def);
        centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.DEPLOYED);
        return Integer.toString(instanceId);
    }

    public void removeSingleNodeInstance(TNodeTemplate node, String instanceId) {
        InstrumentShareData.killProcessInstance(serviceId, topologyId, nodeId, instanceId);
        //centerCon.removeOneInstance(serviceId, topologyId, node.getId(), Integer.parseInt(instanceId));
    }

    private void multiThreadRunArtifacts(TNodeTemplate node, List<Integer> instanceIds) {
        List<Thread> threads = new ArrayList<Thread>();

        for (Integer i : instanceIds) {
            logger.debug("STARTING Thread for node ID :" + i);
            Thread thread = new Thread(new deployOneArtifactThread(node, i, def));
            thread.start();
            threads.add(thread);
        }
			// waiting for all thread run and put node in to finish

    }

    private class deployOneArtifactThread implements Runnable {

        TDefinitions def;
        TNodeTemplate node;
        int instanceId;

        public deployOneArtifactThread(TNodeTemplate node, int instanceId, TDefinitions def) {
            logger.debug("Thread processind: nodeId=" + node.getId() + " -- InstanceID: " + instanceId);
            this.def = def;
            this.node = node;
            this.instanceId = instanceId;
        }

        private synchronized void executeDeploymentNode() throws SalsaEngineException {
            logger.debug("Executing the deployment for node: " + node.getId() + ", instance: " + instanceId);
            //runNodeArtifacts(node, Integer.toString(instanceId), def);
            String id = deploySingleNode(node, instanceId);
        }

        @Override
        public void run() {
            try {
                executeDeploymentNode();
            } catch (SalsaEngineException e) {
                PioneerLogger.logger.error(e.getMessage());
            }

        }

    }

    // waiting for capabilities and fulfill requirements
    private void waitingForCapabilities(TNodeTemplate node, TDefinitions def) throws SalsaEngineException {
        logger.debug("WaitingForCapabilities: " + node.getId());
        if (node.getRequirements() != null) {
            PioneerLogger.logger.debug("There are some explicit requirement, wait for them.");
            List<TRequirement> reqs = node.getRequirements().getRequirement();
            for (TRequirement req : reqs) {
                logger.debug("Checking requirement " + req.getId());
                TCapability cap = ToscaStructureQuery.getCapabilitySuitsRequirement(req, def);
                logger.debug("Waiting for capability: " + cap.getId());
                String value = waitRelationshipReady(topologyId, hostedVmInstanceId, cap, req);
            }
        }
        logger.debug("Trying to get relationship host on");
        List<TRelationshipTemplate> relas = ToscaStructureQuery.getRelationshipTemplateList(SalsaRelationshipType.HOSTON.getRelationshipTypeString(), def);
        logger.debug("number of relationship to check: " + relas.size());
        for (TRelationshipTemplate rela : relas) {
            logger.debug("Check rela: " + rela.getId());
            if (rela.getSourceElement().getRef().equals(node)) {
                logger.debug("Found target equal to current node : " + node.getId());
                TNodeTemplate hostNode = (TNodeTemplate) rela.getTargetElement().getRef();
                hostNode.getId();
                logger.debug("Checking state of host node: " + hostNode.getId());
                CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
                SalsaEntityState state = SalsaEntityState.CONFIGURING;
                //while (state!=SalsaEntityState.RUNNING && state!=SalsaEntityState.DEPLOYED){
                while (state != SalsaEntityState.DEPLOYED) {
                    state = service.getComponentById(hostNode.getId()).getState();
                    service = centerCon.getUpdateCloudServiceRuntime(serviceId);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

    }

    // Download node artifact
    private boolean downloadNodeArtifacts(TNodeTemplate node, TDefinitions def, int instanceID) {
        PioneerLogger.logger.debug("Preparing artifact for node: " + node.getId());
        if (node.getDeploymentArtifacts() == null) {
            PioneerLogger.logger.debug("There is no node artifact to download");
            return true;
        }
        PioneerLogger.logger.debug("Number of artifact: " + node.getDeploymentArtifacts().getDeploymentArtifact().size());
        PioneerLogger.logger.debug("Debug: " + node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getName());
        PioneerLogger.logger.debug("Debug: " + node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactType());
        PioneerLogger.logger.debug("Debug: " + node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactType().getLocalPart());

        if (node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactType().getLocalPart().equals(SalsaArtifactType.chef.getString())) {
            PioneerLogger.logger.debug("Chef artifact");
            return true;
        }
        // get Artifact list
        List<String> arts = ToscaStructureQuery.getDeployArtifactTemplateReferenceList(node, def);
        for (String art : arts) {
            try {
                PioneerLogger.logger.debug("Downloading artifact for: " + node.getId() + ". URL:" + art);
                URL url = new URL(art);
                String filePath = SalsaPioneerConfiguration.getWorkingDirOfInstance(node.getId(), instanceID)
                        + File.separator + FilenameUtils.getName(url.getFile());
					// get the last file in the list
                // TODO: there could be multi mirror of an artifact, check !
                //runArt = FilenameUtils.getName(url.getFile()); 
                // download file to dir: nodeId/fileName
                PioneerLogger.logger.debug("Download file from:" + url.toString() + "\nSave to file:" + filePath);
                FileUtils.copyURLToFile(url, new File(filePath));
                (new File(filePath)).setExecutable(true);

                // if the artifact is an archieve, try to extract it
                extractFile(filePath, SalsaPioneerConfiguration.getWorkingDirOfInstance(node.getId(), instanceID));
            } catch (IOException e) {   // in the case cannot create artifact
                PioneerLogger.logger.error("Error while downloading artifact for: " + node.getId() + ". URL:" + art);
                PioneerLogger.logger.error(e.toString());
                return false;
            }
        }
        return true;
    }

    private void extractFile(String filePath, String workingDir) {
        if (filePath.endsWith("tar.gz")) {
            SystemFunctions.executeCommand("tar -xvzf " + filePath, workingDir, this.centerCon, this.nodeId + "/" + this.hostedVmInstanceId);
        }
    }

    // Download and run 1 node of software
    private void runNodeArtifacts(TNodeTemplate node, String instanceId, TDefinitions def) {
        PioneerLogger.logger.debug("==> Run artifact for node: " + node.getId());
        // get Artifact list
        String runArt = "";
        String artType = "";  // currently being get from Deployment Artifact
        try {
            if (node.getDeploymentArtifacts() == null) {
                PioneerLogger.logger.debug("Node " + node.getId() + " has no artifact, checking the script");
                // try to get script from the actions
                ServiceUnit unit = serviceRuntimeInfo.getComponentById(node.getId());
                PrimitiveOperation po = unit.getPrimitiveByName("deploy");
                if (!po.equals(null)) {
                    if (po.getExecutionType().equals(PrimitiveOperation.ExecutionType.SCRIPT)) {
                        PioneerLogger.logger.debug("Component will be deployed by command: " + po.getExecutionREF());
                        //executeCommand(po.getExecutionREF());	// not running here, just return. 
                    }
                }
                return;
            }
            // get the first artifact which is not misc
            TDeploymentArtifact actualArtifact = null;
            for(TDeploymentArtifact aart: node.getDeploymentArtifacts().getDeploymentArtifact()){
                if (!aart.getArtifactType().getLocalPart().equals(SalsaArtifactType.misc.getString())){
                     PioneerLogger.logger.debug("Found an artifact to deploy: " + aart.getName() + ", type: " + aart.getArtifactType().getLocalPart());
                    actualArtifact = aart;
                    break;
                }
            }
            
            if (actualArtifact == null){
                PioneerLogger.logger.error("Node " + node.getId() + " has artifact, but none of them can be used for deploying");
                return;
            }
            
            artType = actualArtifact.getArtifactType().getLocalPart();
            PioneerLogger.logger.debug("Artifact type:" + artType);

            List<String> arts = ToscaStructureQuery.getDeployArtifactTemplateReferenceList(node, def);            
            URL url = new URL(arts.get(0));	// run the first artifact

            runArt = FilenameUtils.getName(url.getFile());
            PioneerLogger.logger.debug("Artifact reference:" + runArt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (runArt.equals("")) {
            PioneerLogger.logger.debug(node.getId() + " doesn't have a deploy artifact");
            return;
        }
        if (SalsaArtifactType.fromString(artType) == null) {
            PioneerLogger.logger.debug(node.getId() + " use unsupport artifact type: " + artType);
            return;
        }

        InstrumentInterface instrument = null;
        switch (SalsaArtifactType.fromString(artType)) {
            case sh:
                instrument = new BashInstrument();
                break;
            case shcont:
                instrument = new BashContinuousInstrument();
                break;
            case war:
                instrument = new WarInstrument();
                break;
            case chef:
                instrument = new ChefInstrument();
                break;
            case chefSolo:
                instrument = new ChefSoloInstrument();
                break;
            case apt:
                instrument = new AptGetInstrument();
                break;
            default:
                instrument = new BashInstrument();
                break;
        }

        PioneerLogger.logger.debug("ArtifactDeploy prepare to initiate node.");
        instrument.initiate(node);
        Object monitorObj = instrument.deployArtifact(runArt, instanceId);
        if (monitorObj == null) {
            return;
        }
        PioneerLogger.logger.debug("The artifact class is: " + monitorObj.getClass().toString());
        if (artType.equals(SalsaArtifactType.shcont.getString())) {
            PioneerLogger.logger.debug("Ok, we can add this to the process list for monitoring");
            InstrumentShareData.addInstanceProcess(serviceId, topologyId, node.getId(), instanceId, (Process) monitorObj);
        }

        // if this node is part of CONNECTTO, send the IP after running artifact
        if (node.getCapabilities() != null) {
            logger.debug("This node has ConnectTo capability, set it !");
            for (TCapability capa : node.getCapabilities().getCapability()) {
                TRequirement req = ToscaStructureQuery.getRequirementSuitsCapability(capa, def);
                TRelationshipTemplate rela = ToscaStructureQuery.getRelationshipBetweenTwoCapaReq(capa, req, def);
                if (rela.getType().getLocalPart().equals(SalsaRelationshipType.CONNECTTO.getRelationshipTypeString())) {
//						try{
                    logger.debug("Sending the IP of this node to the capability of CONNECTTO");
//							String ip = InetAddress.getLocalHost().getHostAddress();
//							SalsaCapaReqString capaString = new SalsaCapaReqString(capa.getId(), ip);
                    SalsaCapaReqString capaString = new SalsaCapaReqString(capa.getId(), "salsa:localIP");
                    centerCon.updateInstanceUnitCapability(serviceId, topologyId, node.getId(), Integer.parseInt(instanceId), capaString);
//						} catch (UnknownHostException e){
//							PioneerLogger.logger.error("Cannot get the IP of the host of node: " + node.getId());
//						}
                }
            }
        }

    }

    /*
     * Check if an instance of node is deployed, then its capability will be ready
     * Note: we don't need nodeID because capaId is unique inside a Topology
     * CHECK THE STATE OF THE FIRST INSTANCE OF THE COMPONENT
     * CURRENTLY, instanceId is not need, but future.
     */
    public boolean checkCapabilityReady(String capaId) throws SalsaEngineException {
        logger.debug("Check capability for capaid: " + capaId);
        TNodeTemplate node = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(capaId, def);
        if (node == null) {	// capaId is not valid
            logger.debug("Check capability. Wrong capability Id");
            return false;
        }

			// check if there are a replica of node with Ready state
        // it doesn't care about which node, just check if existing ONE replica
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        logger.debug("checkCapabilityReady - service: " + service.getId());
        ServiceUnit component = service.getComponentById(node.getId());	// no topology as parameter, so we can check crossing to other topology			
        logger.debug("checkCapabilityReady - service unit: " + component.getId());
        int number = component.getInstanceNumberByState(SalsaEntityState.INSTALLING) + component.getInstanceNumberByState(SalsaEntityState.DEPLOYED);
        logger.debug("Check capability. Checking component id " + component.getId() + "  -- " + number + " number of running or finished instances.");
        if (number == 0) {
            logger.debug("CHECK CAPABILITY FALSE. " + number);
            return false;
        } else {
            logger.debug("CHECK CAPABILITY TRUE. " + number);
            return true;
        }

    }

    // can handle null value (see SalsaCenterConnector)
    private String waitRelationshipReady(String topoId, int replica, TCapability capa, TRequirement req) throws SalsaEngineException {
        TRelationshipTemplate rela = ToscaStructureQuery.getRelationshipBetweenTwoCapaReq(capa, req, def);
        while (!checkCapabilityReady(capa.getId())) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
        // try to get value, retry every 5 secs if it is not a hoston. Because the HOSTON is always available if node is ready, but not for software
        if (rela.getType().getLocalPart().equals(SalsaRelationshipType.HOSTON.getRelationshipTypeString())) {
            return "ready";	// just return for HOSTON
        }
        logger.debug("WaitRelationshipReady Mar4 - capa: " + capa.getId());
        TNodeTemplate nodeOfCapa = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(capa, def);

        // note: 0 is the ID of the first node, which provide the capability - NOTE: this id is depricate. See salsa service
        String value = centerCon.getCapabilityValue(serviceId, topoId, nodeOfCapa.getId(), 0, capa.getId());
        logger.debug("waitRelationshipReady - Get the value is: " + value);
        while (value == null) {
            try {
                logger.debug("waitRelationshipReady - Get the value is: " + value);
                Thread.sleep(5000);
                value = centerCon.getCapabilityValue(serviceId, topoId, nodeOfCapa.getId(), 0, capa.getId());

            } catch (InterruptedException e) {
            }
        }
			// set the environment variable. Write the value to node_capability_id
        // if the value is of CONNECTTO relationship, write to NodeOfCapability_IP
        // SystemFunctions.writeSystemVariable(nodeOfCapa.getId()+"_"+capa.getId(), value);
        PioneerLogger.logger.debug("123. Check the relationship: " + rela.getType().getLocalPart());
        if (rela.getType().getLocalPart().equals(SalsaRelationshipType.CONNECTTO.toString())) {
            PioneerLogger.logger.debug("123. Relationship type is CONNECTTO !");
            // get the IP form the center
            String ip = value;
            try {
                SystemFunctions.writeSystemVariable(nodeOfCapa.getId() + "_IP", ip);
                SystemFunctions.writeSystemVariable(rela.getId() + "_IP", ip);
            } catch (Exception e) {
                PioneerLogger.logger.error("Couldn't get IP of host !");
            }
        }
        return value;
    }

    public String getVMProperty(String propName) throws SalsaEngineException {
        String res = "";
        ServiceInstance nodeData = serviceRuntimeInfo.getComponentById(topologyId, nodeId).getInstanceById(hostedVmInstanceId);
        SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) nodeData.getProperties().getAny();
        PropertyVMExpose proptype = PropertyVMExpose.fromString(propName);
        switch (proptype) {
            case ip:
            case private_ip:
                res = vm.getPrivateIp();
                break;
            case public_ip:
                res = vm.getPublicIp();
                break;
        }
        System.out.println(res);
        return res;
    }

}
