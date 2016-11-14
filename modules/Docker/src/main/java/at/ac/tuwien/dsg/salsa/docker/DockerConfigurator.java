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
package at.ac.tuwien.dsg.salsa.docker;


import at.ac.tuwien.dsg.salsa.model.enums.SalsaArtifactType;
import at.ac.tuwien.dsg.salsa.model.salsa.confparameters.DockerParameters;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import at.ac.tuwien.dsg.salsa.model.salsa.interfaces.ConfigurationModule;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class DockerConfigurator implements ConfigurationModule {

    static final long cooldown = 5000;
    static long lastDeploymentTime = (new java.util.Date()).getTime();

    static Logger logger = LoggerFactory.getLogger("BashModule");
    static String portPrefix = "498";
    static String SALSA_DOCKER_PULL = "leduchung/ubuntu:14.04-jre8";
    static boolean inited = false;
    String currentPioneerDir = null;
    String newSalsaWorkingDirInsideDocker;

    // This assume that a docker container will always created at least 2 seconds after previous one, on a same VM
    private void waitForCooledDown() {
        long currentTime = (new java.util.Date()).getTime();
        long between = currentTime - lastDeploymentTime;
//        if (between < cooldown) {
//            try {
//                logger.debug("Waiting a " + (cooldown - between) + " miliseconds to reduce the cloud failure when create many docker at a time");
//                Thread.sleep(cooldown - between);
//            } catch (InterruptedException ex) {
//                lastDeploymentTime = currentTime;
//            }
//        }
        logger.debug("No cool down ! ");
        lastDeploymentTime = currentTime;
    }

    @Override
    public SalsaConfigureResult configureArtifact(SalsaConfigureTask configInfo, Map<String, String> parameters) {
        currentPioneerDir = parameters.get("workingdir");
        newSalsaWorkingDirInsideDocker = parameters.get("workingdir");

        logger.debug("THIS IS THE DOCKER NODE, INSTALL IT !");
        String dockerFileName = "";
        if (configInfo.getArtifacts() != null) {
            for (SalsaConfigureTask.DeploymentArtifact art : configInfo.getArtifacts()) {
                if (art.getType().equals(SalsaArtifactType.dockerfile.getString())) {
                    dockerFileName = FilenameUtils.getName(art.getReference());
                    logger.debug("Found a DockerFile: " + dockerFileName);
                }
            }
        }

        //wait for the cooldown        
        String containerID;
        // extract artifact, if there are dockerfile type       
        if (!dockerFileName.isEmpty()) {
            initDocker(false);
            waitForCooledDown();
            containerID = installDockerNodeWithDockerFile(configInfo.getUnit(), configInfo.getInstance(), dockerFileName, configInfo.getParameters(DockerParameters.preRunByMe));
        } else {
            initDocker(true);
            waitForCooledDown();
            containerID = installDockerNodeWithSALSA(configInfo.getUnit(), configInfo.getInstance(), configInfo.getParameters(DockerParameters.preRunByMe));
        }
        if (containerID == null || containerID.isEmpty()) {
            return new SalsaConfigureResult(configInfo.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.ERROR, 0, "Docker container is failed to created").hasDomainID(containerID);
        } else {
            // the successful status must be updated by the pioneer
            return new SalsaConfigureResult(configInfo.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.PROCESSING, 0, "Docker container is created.").hasDomainID(containerID);
        }
    }

    @Override
    public String getStatus(SalsaConfigureTask configInfo) {
        logger.debug("Getting status of Docker is not support yet. Will be implemented !");
        return "";
    }

    public DockerInfo getDockerInfo(String containerID) {
        if (containerID == null || containerID.isEmpty()) {
            logger.error("Cannot get Docker information. Container ID is null or empty !");
            return null;
        }
        String ip = SystemFunctions.executeCommandGetFirstLineOutput("docker inspect --format='{{.NetworkSettings.IPAddress}}' " + containerID, null, null);
        String isRunning = SystemFunctions.executeCommandGetFirstLineOutput("docker inspect --format='{{.State.Running}}' " + containerID, null, null);
        String name = SystemFunctions.executeCommandGetFirstLineOutput("docker inspect --format='{{.Name}}' " + containerID, null, null);
        String dockerPortInfo = SystemFunctions.executeCommandGetFirstLineOutput("docker inspect --format='{{.HostConfig.PortBindings}}' " + containerID, null, null);
        logger.debug("Adding docker info: ip=" + ip + ", isRunning=" + isRunning + ", portinfo: " + dockerPortInfo);
        String portmap = formatPortMap(dockerPortInfo.trim());
        logger.debug("portmap string after formating: " + portmap);

        DockerInfo dockerMachine = new DockerInfo("local@dockerhost", containerID, name);
        dockerMachine.setBaseImageID("salsa.ubuntu");
        dockerMachine.setPortmap(portmap);

        if (isRunning.equals("true")) {
            dockerMachine.setStatus("RUNNING");
        } else {
            dockerMachine.setStatus("STOPPED");
        }

        dockerMachine.setPrivateIp(ip);
        dockerMachine.setPublicIp(ip);
        dockerMachine.setPortmap(portmap);
        logger.debug("Docker get info done: " + dockerMachine.toString());
        return dockerMachine;
    }

    private void initDocker(boolean pullSalsaImage) {
        if (inited) {
            logger.debug("Docker is installed, not do it again !");
            return;
        }
        logger.debug("Getting docker installtion script !");
        try {
            InputStream is = DockerConfigurator.class.getResourceAsStream("/scripts/docker_install.sh");
            OutputStream os = new FileOutputStream(new File("/tmp/docker_install.sh"));
            IOUtils.copy(is, os);
            os.flush();
            os.close();
            is.close();
            logger.debug("Getting docker installtion script done !");
        } catch (FileNotFoundException e) {
            logger.error("Cannot write docker installation script out");
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        SystemFunctions.executeCommandGetReturnCode("/bin/bash /tmp/docker_install.sh", "/tmp", "initDocker");
        //SystemFunctions.executeCommandGetReturnCode("/tmp/docker_install.sh", "/tmp", "initDocker");
        if (pullSalsaImage) {
            SystemFunctions.executeCommandGetReturnCode("/usr/bin/docker pull " + SALSA_DOCKER_PULL, null, "initDocker");
        }
        inited = true;
    }


    // this method does not install salsa pioneer
    public String installDockerNodeWithDockerFile(String nodeId, int instanceId, String dockerFile, String preRunByMe) {
        String newDockerImage = UUID.randomUUID().toString().substring(0, 5);

        if (!dockerFile.equals("Dockerfile")) {
            try {
                FileUtils.moveFile(new File(newSalsaWorkingDirInsideDocker + "/" + dockerFile), new File(newSalsaWorkingDirInsideDocker + "/Dockerfile"));
            } catch (IOException ex) {
                logger.error("Do not found the Dockerfile, maybe it is not downloaded properly or on the wrong working folder !");
            }
//            int code = SystemFunctions.executeCommandGetReturnCode("mv " + dockerFile + " Dockerfile", newSalsaWorkingDirInsideDocker, "");
//            if (code != 0) {
//                logger.error("Do not found the Dockerfile, maybe it is not downloaded properly or on the wrong working folder !");
//            }
        }

        // copy the pioneer_install.sh to /tmp/, so the image will be build with it
        URL inputUrl = getClass().getResource("/scripts/pioneer_install.sh");
        File dest = new File(newSalsaWorkingDirInsideDocker + "/pioneer_install.sh");
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // add salsa-pioneer deployment. The COPY command in the Dockerfile get only current folder file, cannot use absolute path on HOST
        StringBuilder sb = new StringBuilder();
        try {
            FileUtils.copyFile(new File(currentPioneerDir + "/salsa.variables"), new File(newSalsaWorkingDirInsideDocker + "/salsa.variables"));
        } catch (IOException ex) {
            logger.error("Cannot copy salsa.variables file !: " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
        //SystemFunctions.executeCommandGetReturnCode("cp " + PioneerConfiguration.getWorkingDir() + "/salsa.variables " + newSalsaWorkingDirInsideDocker, newSalsaWorkingDirInsideDocker, dockerFile);
        if (preRunByMe != null) {
            sb.append("\nRUN apt-get update \n");
            String[] runThese = preRunByMe.split(";");
            for (String c : runThese) {
                if (!c.trim().isEmpty()) {
                    sb.append("\nRUN " + c + " \n");
                }
            }
        }
        sb.append("\nCOPY ./salsa.variables /etc/salsa.variables \n");
        sb.append("RUN mkdir -p " + newSalsaWorkingDirInsideDocker + "\n");
        sb.append("COPY ./pioneer_install.sh " + newSalsaWorkingDirInsideDocker + "/pioneer_install.sh \n");
        sb.append("RUN chmod +x " + newSalsaWorkingDirInsideDocker + "/pioneer_install.sh  \n");

        // and append to the Dockerfile
        try {
            String filename = newSalsaWorkingDirInsideDocker + "/Dockerfile";
            FileWriter fw = new FileWriter(filename, true);
            fw.write(sb.toString());
            fw.flush();
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

        // build the image
        SystemFunctions.executeCommandGetReturnCode("sudo docker build -t " + newDockerImage + " .", newSalsaWorkingDirInsideDocker, "");

        // search for porting MAP be reading the EXPOSE in dockerFile
        String portMap = "";
        String exportToDocker = "";
        String hostIP = SystemFunctions.getEth0IPAddress();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(newSalsaWorkingDirInsideDocker + "/Dockerfile")));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("EXPOSE")) {
                    String[] exposesFull = line.split("\\s+");
                    String[] exposes = Arrays.copyOfRange(exposesFull, 1, exposesFull.length); // remove the EXPOSE keyword
                    for (String port : exposes) {
                        portMap += " -p " + hostIP + ":" + getPortMapOnHost(Long.parseLong(port), instanceId) + ":" + port; // aware of no space after this
                        exportToDocker += " -e SALSA_ENV_PORTMAP_" + port + "=" + hostIP + ":" + getPortMapOnHost(Long.parseLong(port), instanceId);
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            logger.error("Cannot read the Docker file: " + dockerFile);
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("D_HH_mm_ss");
        String formattedDate = sdf.format(date);
        String dockerID = nodeId + "_" + instanceId + "_" + formattedDate;

        String returnValue = SystemFunctions.executeCommandGetFirstLineOutput("sudo docker run -d --name " + dockerID + portMap + exportToDocker + " -t " + newDockerImage, newSalsaWorkingDirInsideDocker, "");
        logger.debug("installDockerNodeWithDockerFile. Return value: " + returnValue);

        // run a pioneer on the container if previous done
        if (returnValue != null && !returnValue.isEmpty()) {
            logger.debug("Container spawn done, now trying to push Pioneer inside the container..");

            // and execute it
            int returnCode = SystemFunctions.executeCommandGetReturnCode("sudo docker exec -d " + returnValue + " " + newSalsaWorkingDirInsideDocker + "/pioneer_install.sh " + nodeId + " " + instanceId + " \n", newSalsaWorkingDirInsideDocker, "");
            logger.debug("Pushing pioneer command return code: " + returnCode);
        } else {
            logger.error("Container is failed to created. No pioneer is pushed.");
            return null;
        }
        // return container ID
        return returnValue;
    }

    private String installDockerNodeWithSALSA(String nodeId, int instanceId, String preRunByMe) {
        // build new image with correct salsa.variable file
        StringBuilder sb = new StringBuilder();
        sb.append("FROM " + SALSA_DOCKER_PULL + " \n");
        if (!preRunByMe.trim().isEmpty()) {
            sb.append("\nRUN apt-get update \n");
            sb.append("\nRUN " + preRunByMe + " \n");
        }
        SystemFunctions.executeCommandGetReturnCode("/bin/cp " + currentPioneerDir + "/salsa.variables " + newSalsaWorkingDirInsideDocker, newSalsaWorkingDirInsideDocker, "");
        sb.append("COPY ./salsa.variables /etc/salsa.variables \n");
        try {
            InputStream is = DockerConfigurator.class.getResourceAsStream("/scripts/pioneer_install.sh");
            OutputStream os = new FileOutputStream(new File(newSalsaWorkingDirInsideDocker + "/pioneer_install.sh"));
            IOUtils.copy(is, os);
            os.flush();
            os.close();
            is.close();
            logger.debug("Getting pioneer installtion script done !");
        } catch (FileNotFoundException e) {
            logger.error("Cannot write pioneer installation script out in /tmp");
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        sb.append("COPY ./pioneer_install.sh ./pioneer_install.sh \n");
        sb.append("EXPOSE 9000 \n");
        String pFilename = newSalsaWorkingDirInsideDocker + "/Dockerfile";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(pFilename));
            out.write(sb.toString());
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.error("Could not create docker file ! Error: " + e);
            return null;
        }
        String dockerInstanceId = String.format("%02d", instanceId);
        String newDockerImage = UUID.randomUUID().toString().substring(0, 5);

        int buildResult = 1;
        int tryTime = 0;
        //retry one more time if the first build is fail
        while (buildResult != 0 && tryTime < 3) {
            buildResult = SystemFunctions.executeCommandGetReturnCode("/usr/bin/docker build -t " + newDockerImage + " . ", newSalsaWorkingDirInsideDocker, null);
            tryTime += 1;
            if (buildResult != 0) {
                logger.debug("DockerFailed: Fail to build image, retry time:" + tryTime);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        // install pioneer on docker and send request
        String portMap = portPrefix + dockerInstanceId;
        String cmd = "/bin/bash pioneer_install.sh " + nodeId + " " + instanceId;
        //String cmd = "pioneer_install.sh " + nodeId + " " + instanceId;
        //return executeCommand("sudo docker run -p " + portMap + ":9000 " + "-d -t " + newDockerImage +" " + cmd +" ");

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("D_HH_mm_ss");
        String formattedDate = sdf.format(date);
        String dockerID = nodeId + "_" + instanceId + "_" + formattedDate;

        return SystemFunctions.executeCommandGetFirstLineOutput("sudo docker run -d --name " + dockerID + " -t " + newDockerImage + " " + cmd + " ", null, null);
    }

    /**
     * Convert from map[5683/tcp:[map[HostIp:10.99.0.32 HostPort:5686]]
     * 80/tcp:[map[HostIp:10.99.0.32 HostPort:9083]]
     * 2812/tcp:[map[HostIp:10.99.0.32 HostPort:2815]]]
     * s1-->5683/tcp:[map[HostIp:10.99.0.32 HostPort:5686]]
     * 80/tcp:[map[HostIp:10.99.0.32 HostPort:9083]]
     * 2812/tcp:[map[HostIp:10.99.0.32 HostPort:2815]]
     * s-->5683/tcp:[map[HostIp:10.99.0.32 HostPort:5686]] to 5683:5685 80:9083
     * 2812:2815
     *
     * Updated: new docker of 1.8.2 version give: map[2812/tcp:[{10.99.0.21
     * 2812}] 5683/tcp:[{10.99.0.21 5683}] 80/tcp:[{10.99.0.21 9080}]]
     *
     *
     * @param dockerPortInfo
     * @return
     */
    private String formatPortMap(String dockerPortInfo) {
        logger.debug("Formating port map");
        String s1 = dockerPortInfo.substring(4, dockerPortInfo.lastIndexOf("]"));
        logger.debug("s1: " + s1);
        String[] sa1 = s1.trim().split("] ");
        String result = "";
        for (String s : sa1) {
            logger.debug("s: " + s);
            if (!s.trim().equals("")) {
                // s:  2812/tcp:[map[HostIp:10.99.0.32 HostPort:2817]
                //s = s.replace(" ", "]");  // because HostIP and HostPort can be in reverse order
                // s: 2812/tcp:[map[HostIp:10.99.0.32]HostPort:2817]
                //int hostPortStrIndex = s.lastIndexOf("HostPort:") + 9;
                // result += s.substring(0, s.indexOf("/tcp")) + ":" + s.substring(hostPortStrIndex, s.indexOf("]", hostPortStrIndex)) + " ";

                // below code update for docker 1.8.2
                // s: 80/tcp:[{10.99.0.21 9080} ==> updated: docker 1.8.2:
                // s is convert to 80:10.99.0.21:9080
                s = s.trim();
                s = s.replace("/tcp", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(" ", ":");
                result += s + " ";
                //result += s.substring(0, s.indexOf("/tcp")) +":" + s.substring(s.lastIndexOf(" ")+1,s.lastIndexOf("}")) + " ";
            }
        }
        logger.debug("Format done: " + result.trim());
        return result.trim();
    }

//    public static void main(String[] args) {
//        DockerConfigurator docker = new DockerConfigurator("randomID");
//        String s = "map[2812/tcp:[map[HostIp:10.99.0.32 HostPort:2817]] 5683/tcp:[map[HostPort:5688 HostIp:10.99.0.32]] 80/tcp:[map[HostIp:10.99.0.32 HostPort:9085]]]";
//        System.out.println(docker.formatPortMap(s));
//    }
    public String removeDockerContainer(String containerID) {
        SystemFunctions.executeCommandGetReturnCode("/usr/bin/docker kill " + containerID, null, null);
        SystemFunctions.executeCommandGetReturnCode("/usr/bin/docker rm -f " + containerID, null, null);
        return containerID;
    }

    public String getEndpoint(int instanceId) {
        String portMap = portPrefix + String.format("%02d", instanceId);
        return "http://localhost:" + portMap + "/";
    }

    private Long getPortMapOnHost(Long portOnDocker, int dockerInstanceID) {
        if (portOnDocker < 1024) {
            return portOnDocker + dockerInstanceID + 9000;
        } else {
            return portOnDocker + dockerInstanceID;
        }
    }

}
