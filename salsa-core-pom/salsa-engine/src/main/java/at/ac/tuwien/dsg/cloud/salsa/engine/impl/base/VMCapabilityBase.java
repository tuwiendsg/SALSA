package at.ac.tuwien.dsg.cloud.salsa.engine.impl.base;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.CloudInterface;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.VMStates;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.multiclouds.MultiCloudConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.UnitCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.EngineConnectionException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.IllegalConfigurationAPICallException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.VMProvisionException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.VMRemoveException;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SystemFunctions;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class contain methods for manage virtual machine stack. The class still requires some information from SALSA
 *
 * @author hungld TODO: - Instances management - Running information model
 */
public class VMCapabilityBase implements UnitCapabilityInterface {

    CloudInterface cloud;
    File configFile;
    SalsaCenterConnector centerCon;

    {
        try {
            centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        } catch (EngineConnectionException ex) {
            EngineLogger.logger.error("Cannot connect to SALSA service in localhost: " + SalsaConfiguration.getSalsaCenterEndpointLocalhost() + ". This is a fatal error !");
        }
    }

    public VMCapabilityBase() {
        this.configFile = SalsaConfiguration.getCloudUserParametersFile();
    }

    @Override
    public ServiceInstance deploy(String serviceId, String nodeId, int instanceId) throws SalsaException {
        EngineLogger.logger.info("Start to deploy new VM: {}/{}/{}",serviceId,nodeId,instanceId);
        TDefinitions def = centerCon.getToscaDescription(serviceId);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        String topologyId = service.getTopologyOfNode(nodeId).getId();
        EngineLogger.logger.debug("Creating this VM node: " + nodeId + ". Tosca ID:" + def.getId());

        TNodeTemplate enhancedNode = (TNodeTemplate) ToscaStructureQuery.getNodetemplateById(nodeId, def);

        ServiceInstance repData = centerCon.getUpdateServiceUnit(serviceId, nodeId).getInstanceById(instanceId);

        EngineLogger.logger.debug("YOUR ARE HERE TO DEPLOY 4");

        // get the static information of TOSCA and put into SalsaComponentInstanceData property
        SalsaInstanceDescription_VM instanceDesc = new SalsaInstanceDescription_VM();
        SalsaMappingProperties mapProp = (SalsaMappingProperties) enhancedNode.getProperties().getAny();
        instanceDesc.updateFromMappingProperties(mapProp);
        EngineLogger.logger.debug("YOUR ARE HERE TO DEPLOY 5");

        String userData = prepareUserData(SalsaConfiguration.getUserName(), serviceId, topologyId, nodeId, instanceId, instanceDesc, def);

        MultiCloudConnector mcc = new MultiCloudConnector(EngineLogger.logger, configFile);

        EngineLogger.logger.debug("DEBUG: " + nodeId + " --- " + instanceDesc.getInstanceType());
        EngineLogger.logger.debug("CLOUD PROVIDER = " + instanceDesc.getProvider() + "//" + SalsaCloudProviders.fromString(instanceDesc.getProvider()));

        // start the VM
        InstanceDescription indes = mcc.launchInstance(nodeId + "_" + instanceId,
                SalsaCloudProviders.fromString(instanceDesc.getProvider()),
                instanceDesc.getBaseImage(),
                "", // this is the sshKeyGen, but not need anymore. When create mcc, we pass the configFile
                userData,
                instanceDesc.getInstanceType(),
                1, 1);	// deploy min instance number of node		

        // update the instance property from cloud specific to SALSA format
        
        if (indes==null){
            throw new VMProvisionException(instanceDesc.getProvider(), serviceId, nodeId, instanceId, VMProvisionException.VMProvisionError.CLOUD_FAILURE, "Cloud connector does not send back the VM information after called, the instance description is null.");
        } else {
            if (indes.getPrivateIp()==null || indes.getPrivateIp().toString().isEmpty()){
                throw new VMProvisionException(instanceDesc.getProvider(), serviceId, nodeId, instanceId, VMProvisionException.VMProvisionError.CLOUD_FAILURE, "The VM does not have an IP");
            }
        }
        
        if (indes.getState().equals(VMStates.Failed)){
            throw new VMProvisionException(instanceDesc.getProvider(), serviceId, nodeId, instanceId, VMProvisionException.VMProvisionError.CLOUD_FAILURE, "Cloud is failure to create a VM");
        }
        
        updateVMProperties(instanceDesc, indes);

        EngineLogger.logger.debug(instanceDesc.getProvider() + " -- " + instanceDesc.getBaseImage() + " -- " + instanceDesc.getInstanceType() + " -- " + enhancedNode.getMinInstances());
        EngineLogger.logger.debug("A VM for " + nodeId + " has been created.");

        centerCon.updateInstanceUnitProperty(serviceId, topologyId, nodeId, instanceId, instanceDesc);
        EngineLogger.logger.debug("Updated VM info for node: " + nodeId);

        EngineLogger.logger.info("Finished to create a new VM: {}/{}/{}. The IP is: ",serviceId,nodeId,instanceId, instanceDesc.getPrivateIp());
        return repData;
    }

    private static void updateVMProperties(SalsaInstanceDescription_VM sid, InstanceDescription inst) throws SalsaException {
        //if (inst.getReplicaFQN()!=null){ sid.setReplicaNumber(inst.getReplicaFQN().getReplicaNum());}
        if (inst.getPrivateIp() != null) {
            sid.setPrivateIp(inst.getPrivateIp().getHostName());
        } else {
            throw new VMProvisionException(VMProvisionException.VMProvisionError.CLOUD_FAILURE, "Cannot get the IP address of the VM in the VM description.");
        }
            
        if (inst.getPublicIp() != null) {
            sid.setPublicIp(inst.getPublicIp().getHostName());
        }
        if (inst.getPrivateDNS() != null) {
            sid.setPrivateDNS(inst.getPrivateDNS());
        }
        if (inst.getPublicDNS() != null) {
            sid.setPublicDNS(inst.getPublicDNS());
        }
        //if (inst.getState()     !=null){ sid.setState(inst.getState()); }
        if (inst.getInstanceId() != null) {
            sid.setInstanceId(inst.getInstanceId());
        } else {
            throw new VMProvisionException(VMProvisionException.VMProvisionError.CLOUD_FAILURE, "Cannot get the ID of the VM in the description. The VM may be created or not but we cannot manage it.");
        }
        sid.setQuota(inst.getQuota());
    }

    private static String prepareUserData(String userName, String serviceId, String topologyId, String nodeId, int replica, SalsaInstanceDescription_VM instanceDesc, TDefinitions def) {
        StringBuilder userDataBuffer = new StringBuilder();
        userDataBuffer.append("#!/bin/bash \n");
        userDataBuffer.append("echo \"Running the customization scripts\" \n");

        // add the code to check and install java for pioneer
        File java_checking = new File(SystemFunctions.class.getResource("/scripts/java_install_default.sh").getFile());
        try (Scanner scanner = new Scanner(java_checking)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                userDataBuffer.append(line).append("\n");
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // at this point, we have an installing java. Please see the script in resources folder.
        // working dir should be specific for nodes. 		
        String specificWorkingDir = SalsaConfiguration.getPioneerWorkingDir() + "/" + serviceId + "." + nodeId + "." + replica;
        EngineLogger.logger.debug("Preparing user data. Working dir for pioneer for: " + serviceId + "/" + nodeId + "/" + replica + ": " + specificWorkingDir);
        String specificVariableFile = specificWorkingDir + "/" + SalsaConfiguration.getSalsaVariableFile();
        EngineLogger.logger.debug("Preparing user data. Variable file for pioneer for: " + serviceId + "/" + nodeId + "/" + replica + ": " + specificVariableFile);
        userDataBuffer.append("mkdir -p ").append(specificWorkingDir).append(" \n");
        userDataBuffer.append("cd ").append(specificWorkingDir).append(" \n");

        // set some variable put in variable.properties
        userDataBuffer.append("echo '# Salsa properties file. Generated at deployment time.' > ").append(specificVariableFile).append(" \n");

        userDataBuffer.append("echo 'SALSA_USER_NAME=").append(userName).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_SERVICE_ID=").append(serviceId).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_TOPOLOGY_ID=").append(topologyId).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_REPLICA=").append(replica).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_NODE_ID=").append(nodeId).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_TOSCA_FILE=").append(specificWorkingDir).append("/").append(serviceId).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_WORKING_DIR=").append(specificWorkingDir).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_PIONEER_WEB=").append(SalsaConfiguration.getPioneerWeb()).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_PIONEER_RUN=").append(SalsaConfiguration.getPioneerRun()).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_CENTER_ENDPOINT=").append(SalsaConfiguration.getSalsaCenterEndpoint()).append("' >> ").append(specificVariableFile).append(" \n");

        // download pioneer
        List<String> fileLst = Arrays.asList(SalsaConfiguration.getPioneerFiles().split(","));
        for (String file : fileLst) {
            userDataBuffer.append("wget -qN --content-disposition ").append(SalsaConfiguration.getPioneerWeb()).append("/").append(file).append(" \n");
        }

        // install all package dependencies and ganglia
        TNodeTemplate node = ToscaStructureQuery.getNodetemplateById(nodeId, def);
        if (node.getProperties() != null) {
            SalsaMappingProperties mapProp = (SalsaMappingProperties) node.getProperties().getAny();
            SalsaInstanceDescription_VM tprop = new SalsaInstanceDescription_VM();
            tprop.updateFromMappingProperties(mapProp);

            if (tprop.getPackagesDependenciesList() != null) {
                List<String> lstPkgs = tprop.getPackagesDependenciesList()
                        .getPackageDependency();
                for (String pkg : lstPkgs) {
                    EngineLogger.logger.info("Installing package: " + pkg);
                    // TODO: should change, now just support Ubuntu image			
                    userDataBuffer.append("apt-get -q -y install ").append(pkg).append(" \n");
                }
            }
        }
		// install ganglia client for monitoring this VM
        // userDataBuffer.append("apt-get -y install ganglia-monitor gmetad \n");

        // execute Pioneer
        fileLst = Arrays.asList(SalsaConfiguration.getPioneerFiles().split(","));
        userDataBuffer.append("echo Current dir `pwd` \n");
//        userDataBuffer.append("java -jar ").append(fileLst.get(0)).append(" setnodestate ").append(node.getId()).append(" ready \n");
        userDataBuffer.append("java -jar ").append(fileLst.get(0)).append(" startserver \n");

        return userDataBuffer.toString();
    }

    @Override
    public void remove(String serviceId, String nodeId, int instanceId) throws SalsaException {
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit node = service.getComponentById(nodeId);

        if (!node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            throw new IllegalConfigurationAPICallException("Remove VM on a non VM node: " + serviceId + "/" + nodeId + "/" + instanceId);
        }

        ServiceInstance vm = node.getInstanceById(instanceId);
        SalsaInstanceDescription_VM vmProps = (SalsaInstanceDescription_VM) vm.getProperties().getAny();
        if (vmProps == null){
            throw new VMRemoveException(VMRemoveException.Cause.VM_DATA_NOT_FOUND);
        }

        MultiCloudConnector cloudCon = new MultiCloudConnector(EngineLogger.logger, configFile);
        String providerName = vmProps.getProvider();
        String cloudInstanceId = vmProps.getInstanceId();
        EngineLogger.logger.debug("Removing virtual machine. Provider: " + providerName + "InstanceId: " + instanceId);
        cloudCon.removeInstance(SalsaCloudProviders.fromString(providerName), cloudInstanceId);
        centerCon.removeInstanceMetadata(serviceId, nodeId, instanceId);
    }

}
