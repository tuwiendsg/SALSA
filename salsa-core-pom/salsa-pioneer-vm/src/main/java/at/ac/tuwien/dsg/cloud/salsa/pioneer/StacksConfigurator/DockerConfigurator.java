package at.ac.tuwien.dsg.cloud.salsa.pioneer.StacksConfigurator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import at.ac.tuwien.dsg.cloud.salsa.pioneer.SystemFunctions;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_Docker;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class DockerConfigurator {

    static String portPrefix = "498";
    static String SALSA_DOCKER_IMAGE_NAME = "leduchung/salsa";
    static String SALSA_DOCKER_PULL = "leduchung/salsa:latest";
    static boolean inited = false;

    String dockerNodeId = "default";

    public void initDocker(boolean pullSalsaImage) {
        if (inited) {
            PioneerLogger.logger.debug("Docker is installed, not do it again !");
            return;
        }
        PioneerLogger.logger.debug("Getting docker installtion script !");
        try {
            InputStream is = DockerConfigurator.class.getResourceAsStream("/pioneer-scripts/docker_install.sh");
            OutputStream os = new FileOutputStream(new File("/tmp/docker_install.sh"));
            IOUtils.copy(is, os);
            PioneerLogger.logger.debug("Getting docker installtion script done !");
        } catch (FileNotFoundException e) {
            PioneerLogger.logger.error("Cannot write docker installation script out");
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(DockerConfigurator.class.getName()).log(Level.SEVERE, null, ex);
        }
        SystemFunctions.executeCommand("/bin/bash /tmp/docker_install.sh", "/tmp", null, "");
        if (pullSalsaImage) {
            SystemFunctions.executeCommand("sudo docker pull " + SALSA_DOCKER_IMAGE_NAME, "", null, "");
        }
        inited = true;
    }

    public DockerConfigurator(String dockerNodeId) {
        this.dockerNodeId = dockerNodeId;
    }

    // this method does not install salsa pioneer
    public String installDockerNodeWithDockerFile(String nodeId, int instanceId, String dockerFile) {
        String newDockerImage = UUID.randomUUID().toString().substring(0, 5);
        String newSalsaWorkingDirInsideDocker = SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId);

        if (!dockerFile.equals("Dockerfile")) {
            SystemFunctions.executeCommand("mv " + dockerFile + " Dockerfile", SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId), null, "");
        }

        // copy the pioneer_install.sh to /tmp/, so the image will be build with it
        URL inputUrl = getClass().getResource("/pioneer-scripts/pioneer_install.sh");
        File dest = new File(newSalsaWorkingDirInsideDocker + "/pioneer_install.sh");
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            Logger.getLogger(DockerConfigurator.class.getName()).log(Level.SEVERE, null, ex);
        }

        // add salsa-pioneer deployment. The COPY command in the Dockerfile get only current folder file, cannot use absolute path on HOST
        StringBuilder sb = new StringBuilder();
        SystemFunctions.executeCommand("cp " + SalsaPioneerConfiguration.getWorkingDir() + "/salsa.variables " + newSalsaWorkingDirInsideDocker, SalsaPioneerConfiguration.getWorkingDir(), null, dockerFile);
        sb.append("\nCOPY ./salsa.variables /etc/salsa.variables \n");
        sb.append("RUN mkdir -p " + newSalsaWorkingDirInsideDocker + "\n");
        sb.append("COPY ./pioneer_install.sh " + newSalsaWorkingDirInsideDocker + "/pioneer_install.sh \n");
        sb.append("RUN chmod +x " + newSalsaWorkingDirInsideDocker + "/pioneer_install.sh  \n");

        // and append to the Dockerfile
        try {
            String filename = SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId) + "/Dockerfile";
            FileWriter fw = new FileWriter(filename, true);
            fw.write(sb.toString());
            fw.flush();
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

        // build the image
        SystemFunctions.executeCommand("sudo docker build -t " + newDockerImage + " .", SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId), null, "");

        // search for porting MAP be reading the EXPOSE in dockerFile
        String portMap = "";
        String exportToDocker = "";
        String hostIP = SystemFunctions.getEth0IPAddress();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId) + "/Dockerfile")));
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
            PioneerLogger.logger.error("Cannot read the Docker file: " + dockerFile);
        }

        String returnValue = SystemFunctions.executeCommand("sudo docker run -d --name " + nodeId + "_" + instanceId + portMap + exportToDocker + " -t " + newDockerImage, SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId), null, "");
        PioneerLogger.logger.debug("installDockerNodeWithDockerFile. Return value: " + returnValue);

        // run a pioneer on the container if previous done
        if (returnValue != null && !returnValue.isEmpty()) {
            PioneerLogger.logger.debug("Container spawn done, now trying to push Pioneer inside the container..");

            // and execute it
            String pushingResult = SystemFunctions.executeCommand("sudo docker exec -d " + returnValue + " " + SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId) + "/pioneer_install.sh " + nodeId + " " + instanceId + " \n", SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId), null, "");
            PioneerLogger.logger.debug("Pushing result: " + pushingResult);
        }
        // return container ID
        return returnValue;
    }

    public String installDockerNodeWithSALSA(String nodeId, int instanceId) {
        // build new image with correct salsa.variable file
        StringBuilder sb = new StringBuilder();
        sb.append("FROM " + SALSA_DOCKER_PULL + " \n");
        sb.append("COPY ./salsa.variables /etc/salsa.variables \n");
        try {
            InputStream is = DockerConfigurator.class.getResourceAsStream("/pioneer-scripts/pioneer_install.sh");
            OutputStream os = new FileOutputStream(new File("./pioneer_install.sh"));
            IOUtils.copy(is, os);
            PioneerLogger.logger.debug("Getting pioneer installtion script done !");
        } catch (FileNotFoundException e) {
            PioneerLogger.logger.error("Cannot write pioneer installation script out in /tmp");
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(DockerConfigurator.class.getName()).log(Level.SEVERE, null, ex);
        }
        sb.append("COPY ./pioneer_install.sh ./pioneer_install.sh \n");
        sb.append("EXPOSE 9000 \n");
        String pFilename = "./Dockerfile";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(pFilename));
            out.write(sb.toString());
            out.flush();
            out.close();
        } catch (IOException e) {
            PioneerLogger.logger.error("Could not create docker file ! Error: " + e);
            return null;
        }
        String dockerInstanceId = String.format("%02d", instanceId);
        String newDockerImage = UUID.randomUUID().toString().substring(0, 5);
        SystemFunctions.executeCommand("sudo docker build -t " + newDockerImage + " . ", "./", null, null);

        // install pioneer on docker and send request
        String portMap = portPrefix + dockerInstanceId;
        String cmd = "/bin/bash pioneer_install.sh " + nodeId + " " + instanceId;
        //return executeCommand("sudo docker run -p " + portMap + ":9000 " + "-d -t " + newDockerImage +" " + cmd +" ");

        // get the port map
        return SystemFunctions.executeCommand("sudo docker run -d --name " + nodeId + "_" + instanceId + " -t " + newDockerImage + " " + cmd + " ", null, null, null);
    }

    public SalsaInstanceDescription_Docker getDockerInfo(String containerID) {
        if (containerID == null || containerID.isEmpty()) {
            PioneerLogger.logger.error("Cannot get Docker information. Container ID is null or empty !");
            return null;
        }
        String ip = SystemFunctions.executeCommand("docker inspect --format='{{.NetworkSettings.IPAddress}}' " + containerID, null, null, null);
        String isRunning = SystemFunctions.executeCommand("docker inspect --format='{{.State.Running}}' " + containerID, null, null, null);
        String name = SystemFunctions.executeCommand("docker inspect --format='{{.Name}}' " + containerID, null, null, null);
        String dockerPortInfo = SystemFunctions.executeCommand("docker inspect --format='{{.HostConfig.PortBindings}}' " + containerID, null, null, null);
        PioneerLogger.logger.debug("Adding docker info: ip=" + ip + ", isRunning=" + isRunning + ", portinfo: " + dockerPortInfo);
        String portmap = formatPortMap(dockerPortInfo.trim());
        PioneerLogger.logger.debug("portmap string after formating: " + portmap);

        SalsaInstanceDescription_Docker dockerMachine = new SalsaInstanceDescription_Docker("local@dockerhost", containerID, name);
        dockerMachine.setBaseImage("salsa.ubuntu");
        dockerMachine.setInstanceType("os");
        dockerMachine.setPortmap(portmap);
        
        if (isRunning.equals("true")) {
            dockerMachine.setState("RUNNING");
        } else {
            dockerMachine.setState("STOPPED");
        }

        dockerMachine.setPrivateIp(ip);
        dockerMachine.setPublicIp(ip);
        dockerMachine.setPortmap(portmap);        
        PioneerLogger.logger.debug("Docker get info done: " + dockerMachine.toString());
        return dockerMachine;
    }

    /**
     * Convert from map[5683/tcp:[map[HostIp:10.99.0.32 HostPort:5686]] 80/tcp:[map[HostIp:10.99.0.32 HostPort:9083]] 2812/tcp:[map[HostIp:10.99.0.32
     * HostPort:2815]]] s1-->5683/tcp:[map[HostIp:10.99.0.32 HostPort:5686]] 80/tcp:[map[HostIp:10.99.0.32 HostPort:9083]] 2812/tcp:[map[HostIp:10.99.0.32
     * HostPort:2815]] s-->5683/tcp:[map[HostIp:10.99.0.32 HostPort:5686]] to 5683:5685 80:9083 2812:2815
     *
     * @param dockerPortInfo
     * @return
     */
    private String formatPortMap(String dockerPortInfo) {
        PioneerLogger.logger.debug("Formating port map");
        String s1 = dockerPortInfo.substring(4, dockerPortInfo.lastIndexOf("]"));
        PioneerLogger.logger.debug("s1: " + s1);
        String[] sa1 = s1.trim().split("] ");
        String result = "";
        for (String s : sa1) {
            PioneerLogger.logger.debug("s: " + s);
            if (!s.trim().equals("")) {
                // s:  2812/tcp:[map[HostIp:10.99.0.32 HostPort:2817]
                s=s.replace(" ", "]");  // because HostIP and HostPort can be in reverse order
                // s: 2812/tcp:[map[HostIp:10.99.0.32]HostPort:2817]
                int hostPortStrIndex = s.lastIndexOf("HostPort:") + 9;
                result += s.substring(0, s.indexOf("/tcp")) + ":" + s.substring(hostPortStrIndex, s.indexOf("]", hostPortStrIndex)) + " ";
            }
        }
        PioneerLogger.logger.debug("Format done: " + result.trim());
        return result.trim();
    }

    public static void main(String[] args) {
        DockerConfigurator docker = new DockerConfigurator("randomID");
        String s = "map[2812/tcp:[map[HostIp:10.99.0.32 HostPort:2817]] 5683/tcp:[map[HostPort:5688 HostIp:10.99.0.32]] 80/tcp:[map[HostIp:10.99.0.32 HostPort:9085]]]";
        System.out.println(docker.formatPortMap(s));
    }

    public String removeDockerContainer(String containerID) {
        SystemFunctions.executeCommand("sudo docker kill " + containerID, null, null, null);
        SystemFunctions.executeCommand("sudo docker rm -f " + containerID, null, null, null);
        return containerID;
    }

    public String getEndpoint(int instanceId) {
        String portMap = portPrefix + String.format("%02d", instanceId);
        return "http://localhost:" + portMap + "/";
//		try{
//			InetAddress ip = InetAddress.getLocalHost();
//			String ipStr = ip.getHostAddress();
//			return "http://"+ ipStr +":" + portMap + "/";
//		} catch (UnknownHostException e){
//			return "http://localhost:" + portMap + "/";
//		}
    }

    private Long getPortMapOnHost(Long portOnDocker, int dockerInstanceID) {
        if (portOnDocker < 1024) {
            return portOnDocker + dockerInstanceID + 9000;
        } else {
            return portOnDocker + dockerInstanceID;
        }
    }

}
