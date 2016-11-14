/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services.enabler;

import at.ac.tuwien.dsg.salsa.engine.utils.SalsaConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungld
 */
public class InfoGenerator {

    static Logger logger = LoggerFactory.getLogger("salsa");

    public static String prepareUserData(String userName, String serviceId, String topologyId, String nodeId, int replica) {
        StringBuilder userDataBuffer = new StringBuilder();
        userDataBuffer.append("#!/bin/bash \n");
        userDataBuffer.append("echo \"Running the customization scripts\" \n\n");

        // add the code to check and install java for pioneer
        File java_checking = new File(InfoGenerator.class.getResource("/scripts/java1.8_update.sh").getFile());
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
        logger.debug("Preparing user data. Working dir for pioneer for: " + serviceId + "/" + nodeId + "/" + replica + ": " + specificWorkingDir);
        String specificVariableFile = specificWorkingDir + "/" + SalsaConfiguration.getSalsaVariableFile();
        logger.debug("Preparing user data. Variable file for pioneer for: " + serviceId + "/" + nodeId + "/" + replica + ": " + specificVariableFile);
        userDataBuffer.append("mkdir -p ").append(specificWorkingDir).append(" \n");
        userDataBuffer.append("cd ").append(specificWorkingDir).append(" \n");

        // set some variable put in variable.properties
        userDataBuffer.append("echo '# Generate salsa properties file. This code is generated at deployment time.' > ").append(specificVariableFile).append(" \n");

        userDataBuffer.append("echo 'SALSA_USER_NAME=").append(userName).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_SERVICE_ID=").append(serviceId).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_TOPOLOGY_ID=").append(topologyId).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_REPLICA=").append(replica).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_NODE_ID=").append(nodeId).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_TOSCA_FILE=").append(specificWorkingDir).append("/").append(serviceId).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_WORKING_DIR=").append(specificWorkingDir).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_PIONEER_WEB=").append(SalsaConfiguration.getPioneerArtifact()).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_PIONEER_RUN=").append(SalsaConfiguration.getPioneerRun()).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'SALSA_CENTER_ENDPOINT=").append(SalsaConfiguration.getSalsaCenterEndpoint()).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'BROKER=").append(SalsaConfiguration.getBroker()).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'BROKER_TYPE=").append(SalsaConfiguration.getBrokerType()).append("' >> ").append(specificVariableFile).append(" \n");
        userDataBuffer.append("echo 'ELISE_CONDUCTOR_URL=").append(SalsaConfiguration.getConductorWeb()).append("' >> ").append(specificVariableFile).append(" \n");

        // download salsa-pioneer.jar
        userDataBuffer.append("wget -qN --content-disposition ").append(SalsaConfiguration.getPioneerArtifact()).append(" \n");

        // install all package dependencies and ganglia
//        SalsaInstanceDescription_VM tprop = instanceDesc;
//        if (tprop.getPackagesDependenciesList() != null) {
//            List<String> lstPkgs = tprop.getPackagesDependenciesList().getPackageDependency();
//            if (!lstPkgs.isEmpty()) {
//                for (String pkg : lstPkgs) {
//                    if (!pkg.trim().isEmpty()) {
////                        EventPublisher.publishInstanceEvent(Joiner.on("/").join(serviceId, nodeId, replica), INFOMessage.ACTION_TYPE.DEPLOY, INFOMessage.ACTION_STATUS.PROCESSING, "VMCapabilityBase", "Installing package " + pkg);
//                        // TODO: should change, now just support Ubuntu image			                    
//                        userDataBuffer.append("apt-get -q -y install ").append(pkg).append(" \n");
//                    }
//                }
//            }
//        }
        // execute Pioneer        
        userDataBuffer.append("echo Current dir `pwd` \n");
        userDataBuffer.append("java -jar salsa-pioneer.jar \n");

        return userDataBuffer.toString();
    }
}
