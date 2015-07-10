package at.ac.tuwien.dsg.cloud.salsa.pioneer.services;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.PrimitiveOperation;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityActions;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;

import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.ArtifactDeployer;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.StacksConfigurator.DockerConfigurator;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.SystemFunctions;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_Docker;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import java.io.File;
import java.util.Map;

//@Service
//@WebService(endpointInterface = "at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaPioneerInterface")
public class PioneerServiceImplementation implements SalsaPioneerInterface {

    @Override
    public String deployNode(String nodeID, int instanceId) {

        PioneerLogger.logger.debug("Recieve command to deploy node: " + nodeID);

        Properties prop = SalsaPioneerConfiguration.getPioneerProperties();

        String serviceId = prop.getProperty("SALSA_SERVICE_ID");
        String topologyId = prop.getProperty("SALSA_TOPOLOGY_ID");
        String vm_nodeId = prop.getProperty("SALSA_NODE_ID"); // this node
        int vm_instanceId = Integer.parseInt(prop.getProperty("SALSA_REPLICA"));

        SalsaCenterConnector centerCon = new SalsaCenterConnector(
                SalsaPioneerConfiguration.getSalsaCenterEndpoint(),
                SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
        TDefinitions def = centerCon.getToscaDescription(serviceId);// get the latest service description
        CloudService serviceRuntimeInfo;
        try {
            serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);
        } catch (SalsaEngineException e) {
            return "";
        }

        ArtifactDeployer deployer = new ArtifactDeployer(serviceId, topologyId, nodeID, vm_instanceId, def, centerCon, serviceRuntimeInfo);
        TNodeTemplate thisNode = ToscaStructureQuery.getNodetemplateById(nodeID, def);
        String newId = "";
        try {
            newId = deployer.deploySingleNode(thisNode, instanceId);
        } catch (SalsaEngineException e) {
        }

        try {
            PioneerLogger.logger.debug("Checking the property of the node to search actions: " + thisNode.getId() + "/" + instanceId);
            if (thisNode.getProperties() != null) {
                centerCon.logMessage("Pioneer is checking to execute actions on: " + thisNode.getId() + "/" + instanceId);
                PioneerLogger.logger.debug("Pioneer is checking to execute actions on: " + thisNode.getId() + "/" + instanceId);
                SalsaMappingProperties allProp = (SalsaMappingProperties) thisNode.getProperties().getAny();
                PioneerLogger.logger.debug("All property of this node: " + allProp);                
                if (allProp != null) {
                    String result="0";
                    SalsaMappingProperties.SalsaMappingProperty actionProp = allProp.getByType("action");
                    PioneerLogger.logger.debug("Action properties: " + actionProp);
                    if (actionProp != null && actionProp.getMapData() != null) {
                        PioneerLogger.logger.debug("We have actions !");
                        Map<String, String> map = actionProp.getMapData();
                        PioneerLogger.logger.debug("Actions MAP : " + map);
                        if (map.get(SalsaEntityActions.DEPLOY.getActionString()) != null && !map.get(SalsaEntityActions.DEPLOY.getActionString()).isEmpty()) {
                            PioneerLogger.logger.debug("Execute DEPLOY action for: " + thisNode.getId() + "/" + instanceId);
                            result = SystemFunctions.executeCommand(map.get(SalsaEntityActions.DEPLOY.getActionString()), SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeID, instanceId), centerCon, nodeID+"_"+instanceId);                            
                            
                        }
                        if (map.get(SalsaEntityActions.START.getActionString()) != null && !map.get(SalsaEntityActions.START.getActionString()).isEmpty()) {
                            PioneerLogger.logger.debug("Execute START action for: " + thisNode.getId() + "/" + instanceId);
                            result = SystemFunctions.executeCommand(map.get(SalsaEntityActions.START.getActionString()), SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeID, instanceId), centerCon, nodeID+"_"+instanceId);
                        }
                        if (result==null){
                            centerCon.updateNodeState(serviceId, topologyId, nodeID, instanceId, SalsaEntityState.ERROR);
                        } else {                            
                            centerCon.updateNodeState(serviceId, topologyId, nodeID, instanceId, SalsaEntityState.DEPLOYED);
                        }
                    }
                }                
            }            
        } catch (Exception e) {
            PioneerLogger.logger.error(e.getMessage(), e);
        }

        return newId;
    }

    public String executeAction(String nodeId, int instanceId, String actionName) {
        PioneerLogger.logger.debug("Recieve command to executing action: " + nodeId + "/" + instanceId + "/" + actionName);
        Properties prop = SalsaPioneerConfiguration.getPioneerProperties();

        String serviceId = prop.getProperty("SALSA_SERVICE_ID");
        SalsaCenterConnector centerCon = new SalsaCenterConnector(
                SalsaPioneerConfiguration.getSalsaCenterEndpoint(),
                SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
        CloudService service;
        try {
            service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        } catch (SalsaEngineException e) {
            return null;
        }
        ServiceUnit unit = service.getComponentById(nodeId);

        if (actionName.equals(SalsaEntityActions.UNDEPLOY.getActionString())) {
            PioneerLogger.logger.debug("Found the default action: " + actionName);
            return this.removeNodeInstance(nodeId, instanceId);
        } else {
            // TODO: implement to execute other action types
            PrimitiveOperation p = unit.getPrimitiveByName(actionName);
            if (p != null) {
                PioneerLogger.logger.debug("Found a custom action: " + actionName);
                switch (p.getExecutionType()) {
                    case SCRIPT: {
                        return SystemFunctions.executeCommand(p.getExecutionREF(), SalsaPioneerConfiguration.getWorkingDir()+"/"+nodeId, centerCon, nodeId+"_"+instanceId);                        
                    }
                    default: {
                        PioneerLogger.logger.debug("Action type is not support: " + p.getExecutionType());
                        return null;
                    }
                }
            } else {
                PioneerLogger.logger.debug("Action name not found: " + actionName);
                return null;
            }
        }        
    }

    @Override
    public String health() {
        PioneerLogger.logger.debug("Health checked, Pioneer server is alive !");
        return "alive";
    }

    @Override
    public String info() {
        PioneerLogger.logger.debug("Querying info of the pioneer ");

        try {
            BufferedReader br = new BufferedReader(new FileReader(SalsaPioneerConfiguration.getSalsaVariableFile()));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            br.close();
            return sb.toString();
        } catch (FileNotFoundException e1) {
            PioneerLogger.logger.error(e1.toString());
        } catch (IOException e2) {
            PioneerLogger.logger.error(e2.toString());
        }

        return "alive";
    }

    @Override
    public String removeNodeInstance(String nodeID, int instanceId) {
        PioneerLogger.logger.debug("Recieve command to remove node: " + nodeID + "/" + instanceId);

        Properties prop = SalsaPioneerConfiguration.getPioneerProperties();

        String serviceId = prop.getProperty("SALSA_SERVICE_ID");
        String topologyId = prop.getProperty("SALSA_TOPOLOGY_ID");
        int thisInstanceId = Integer.parseInt(prop.getProperty("SALSA_REPLICA"));

        SalsaCenterConnector centerCon = new SalsaCenterConnector(
                SalsaPioneerConfiguration.getSalsaCenterEndpoint(),
                SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
        TDefinitions def = centerCon.getToscaDescription(serviceId);// get the latest service description
        CloudService serviceRuntimeInfo;
        try {
            serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);
        } catch (SalsaEngineException e) {
            return null;
        }
        ArtifactDeployer deployer = new ArtifactDeployer(serviceId, topologyId, nodeID, thisInstanceId, def, centerCon, serviceRuntimeInfo);
        TNodeTemplate nodeForRemove = ToscaStructureQuery.getNodetemplateById(nodeID, def);
        ServiceUnit unit = serviceRuntimeInfo.getComponentById(nodeID);
        ServiceInstance instance = unit.getInstanceById(instanceId);
        try {
            // check if there are an action name  stop and undeployed and execute them first (what ever node types is)
            PioneerLogger.logger.debug("Checking the property of the node to search actions: " + nodeForRemove.getId() + "/" + instanceId);
            if (nodeForRemove.getProperties() != null) {
                centerCon.logMessage("Pioneer is removing node: " + nodeForRemove.getId() + "/" + instanceId);
                PioneerLogger.logger.debug("Removing single node instance: " + nodeForRemove.getId() + "/" + instanceId);
                SalsaMappingProperties allProp = (SalsaMappingProperties) nodeForRemove.getProperties().getAny();
                if (allProp != null) {
                    SalsaMappingProperties.SalsaMappingProperty actionProp = allProp.getByType("action");
                    if (actionProp != null && actionProp.getMapData() != null) {
                        Map<String, String> map = actionProp.getMapData();
                        if (map.get(SalsaEntityActions.STOP.getActionString()) != null && !map.get(SalsaEntityActions.STOP.getActionString()).isEmpty()) {
                            PioneerLogger.logger.debug("Execute Stopping action for: " + nodeForRemove.getId() + "/" + instanceId);
                            SystemFunctions.executeCommand(map.get(SalsaEntityActions.STOP.getActionString()), SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeID, instanceId), null, null);
                        }
                        if (map.get(SalsaEntityActions.UNDEPLOY.getActionString()) != null && !map.get(SalsaEntityActions.UNDEPLOY.getActionString()).isEmpty()) {
                            PioneerLogger.logger.debug("Execute Undeploying action for: " + nodeForRemove.getId() + "/" + instanceId);
                            SystemFunctions.executeCommand(map.get(SalsaEntityActions.UNDEPLOY.getActionString()), SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeID, instanceId), null, null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            PioneerLogger.logger.error(e.getMessage(), e);
        }

        if (unit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
            SalsaInstanceDescription_Docker vm = (SalsaInstanceDescription_Docker) instance.getProperties().getAny();
            if (vm != null) {
                String containerID = vm.getInstanceId();
                DockerConfigurator docker = new DockerConfigurator("default");
                docker.removeDockerContainer(containerID);
            }
        } else {
            deployer.removeSingleNodeInstance(nodeForRemove, Integer.toString(instanceId));
        }
        PioneerLogger.logger.debug("Pioneer is updating UNDEPLOYED state for instance: " + serviceId + "/" + topologyId + "/" + nodeID + "/" + instanceId);
        String result = centerCon.updateNodeState(serviceId, topologyId, nodeID, instanceId, SalsaEntityState.UNDEPLOYED);
        PioneerLogger.logger.debug("Update string: " + result);
        
        // delete the folder of the instance
        SystemFunctions.executeCommand("rm -rf " + SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeID, instanceId), SalsaPioneerConfiguration.getWorkingDir(), centerCon, nodeID+"_"+instanceId);
        
        PioneerLogger.logger.debug("@Daniel: This ended, so it should pass " + result);
        return "Instance " + nodeID+"/" +instanceId + " is removed!";
    }

//    public String executeCommand(String cmd, String workingDir) {
//        PioneerLogger.logger.debug("Execute command: " + cmd);
//        try {
//            //Process p = Runtime.getRuntime().exec(cmd,null,new File(workingDir));
//            Process p = Runtime.getRuntime().exec(cmd);
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                PioneerLogger.logger.debug(line);
//            }
//            p.waitFor();
//            return p.exitValue()+"";
//        } catch (InterruptedException | IOException e1) {
//            PioneerLogger.logger.error("Error when execute command. Error: " + e1);
//        }
//        return "-1";
//    }

}
