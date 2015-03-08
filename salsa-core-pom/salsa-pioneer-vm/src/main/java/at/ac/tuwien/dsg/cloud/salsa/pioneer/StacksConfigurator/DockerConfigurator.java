package at.ac.tuwien.dsg.cloud.salsa.pioneer.StacksConfigurator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.SystemFunctions;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

public class DockerConfigurator{	
	static String portPrefix = "498";	
	static String SALSA_DOCKER_IMAGE_NAME="leduchung/salsa";
	static String SALSA_DOCKER_PULL="leduchung/salsa:latest";
	static boolean inited=false;
	
	String dockerNodeId="default";	
	
	public void initDocker(boolean pullSalsaImage){
		if (inited) {
			PioneerLogger.logger.debug("Docker is installed, not do it again !");
			return;
		}
                PioneerLogger.logger.debug("Getting docker installtion script !");                                
                try{
                    InputStream is = DockerConfigurator.class.getResourceAsStream("/pioneer-scripts/docker_install.sh");
                    OutputStream os = new FileOutputStream(new File("/tmp/docker_install.sh"));
                    IOUtils.copy(is, os);
                    PioneerLogger.logger.debug("Getting docker installtion script done !");                
                } catch (FileNotFoundException e){
                    PioneerLogger.logger.error("Cannot write docker installation script out");
                    e.printStackTrace();
                } catch (IOException ex) {
                    Logger.getLogger(DockerConfigurator.class.getName()).log(Level.SEVERE, null, ex);
                }
		          SystemFunctions.executeCommand("/bin/bash /tmp/docker_install.sh","/tmp",null,"");
                if (pullSalsaImage){
                    SystemFunctions.executeCommand("sudo docker pull "+ SALSA_DOCKER_IMAGE_NAME,"",null,"");
                }
		inited=true;
	}	
	
	public DockerConfigurator(String dockerNodeId){
		this.dockerNodeId = dockerNodeId;
	}
	
        // this method does not install salsa pioneer
        public String installDockerNodeWithDockerFile(String nodeId, int instanceId, String dockerFile) {
            String newDockerImage=UUID.randomUUID().toString().substring(0, 5);
            if (!dockerFile.equals("Dockerfile")){
                SystemFunctions.executeCommand("mv " + dockerFile + " Dockerfile", SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId), null, "");
            }
            // add port mapping 
            SystemFunctions.executeCommand("sudo docker build -t "+ newDockerImage +" .", SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId), null, "");
            
            
            // search for porting MAP be reading the EXPOSE in dockerFile
            String portMap = "";
            String exportToDocker = "";
            String hostIP = SystemFunctions.getEth0IPAddress();            
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId)+"/Dockerfile")));
                String line;
                while ((line = br.readLine()) != null) {
                   if (line.startsWith("EXPOSE")){
                       String[] exposesFull = line.split("\\s+");
                       String[] exposes = Arrays.copyOfRange(exposesFull, 1, exposesFull.length); // remove the EXPOSE keyword
                       for(String port : exposes){
                           portMap += " -p " + hostIP +":"+getPortMapOnHost(Long.parseLong(port), instanceId)+":"+port; // aware of no space after this
                           exportToDocker += " -e SALSA_ENV_PORTMAP_"+port+"="+hostIP +":"+getPortMapOnHost(Long.parseLong(port), instanceId);
                       }
                   }
                }
                br.close();
            } catch (IOException e) {
                PioneerLogger.logger.error("Cannot read the Docker file: " + dockerFile);
            }
            
            
            String returnValue = SystemFunctions.executeCommand("sudo docker run -d --name "+nodeId+"_"+instanceId+portMap+exportToDocker+" -t " + newDockerImage, SalsaPioneerConfiguration.getWorkingDirOfInstance(nodeId, instanceId), null, "");            
            PioneerLogger.logger.debug("installDockerNodeWithDockerFile. Return value: " + returnValue);
            return returnValue;
        }
        
	public String installDockerNodeWithSALSA(String nodeId, int instanceId){
		// build new image with correct salsa.variable file
		StringBuilder sb = new StringBuilder();		
		sb.append("FROM "+ SALSA_DOCKER_PULL +" \n");
		sb.append("COPY ./salsa.variables /etc/salsa.variables \n");                
                try{
                    InputStream is = DockerConfigurator.class.getResourceAsStream("/pioneer-scripts/pioneer_install.sh");
                    OutputStream os = new FileOutputStream(new File("./pioneer_install.sh"));
                    IOUtils.copy(is, os);
                    PioneerLogger.logger.debug("Getting pioneer installtion script done !");                
                } catch (FileNotFoundException e){
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
		} catch (IOException e){
			PioneerLogger.logger.error("Could not create docker file ! Error: " + e);
			return null;
		}		
		String dockerInstanceId = String.format("%02d", instanceId);
		String newDockerImage=UUID.randomUUID().toString().substring(0, 5);		
		SystemFunctions.executeCommand("sudo docker build -t "+ newDockerImage +" . ", "./", null, null);
		
        // install pioneer on docker and send request
        String portMap=portPrefix+ dockerInstanceId;
        String cmd = "/bin/bash pioneer_install.sh " + nodeId +" " + instanceId;
        //return executeCommand("sudo docker run -p " + portMap + ":9000 " + "-d -t " + newDockerImage +" " + cmd +" ");
        
        // get the port map
        
        
        
        return SystemFunctions.executeCommand("sudo docker run -d --name "+nodeId+"_"+instanceId+" -t " + newDockerImage +" " + cmd +" ", null, null, null);
	}
	
	public SalsaInstanceDescription_VM getDockerInfo(String containerID){
            if (containerID == null || containerID.isEmpty()){
                PioneerLogger.logger.error("Cannot get Docker information. Container ID is null or empty !");
                return null;
            }
        // TODO: a bit hack here
        SalsaInstanceDescription_VM dockerMachine = new SalsaInstanceDescription_VM("local@dockerhost", containerID);
        dockerMachine.setBaseImage("salsa.ubuntu");
        dockerMachine.setInstanceType("os");
        String isRunning = SystemFunctions.executeCommand("docker inspect --format='{{.State.Running}}' " + containerID, null, null, null);
        if (isRunning.equals("true")){
        	dockerMachine.setState("RUNNING");
        } else {
        	dockerMachine.setState("STOPPED");
        }
        String ip = SystemFunctions.executeCommand("docker inspect --format='{{.NetworkSettings.IPAddress}}' " + containerID, null, null, null);
        dockerMachine.setPrivateIp(ip);
        dockerMachine.setPublicIp(ip);
        return dockerMachine;
	}
	
	public String removeDockerContainer(String containerID){
		 SystemFunctions.executeCommand("sudo docker kill " + containerID, null, null, null);
		 SystemFunctions.executeCommand("sudo docker rm -f " + containerID, null, null, null);
		 return containerID;
	}
	
	
	public String getEndpoint(int instanceId){
		String portMap=portPrefix + String.format("%02d", instanceId);
		return "http://localhost:" + portMap + "/";
//		try{
//			InetAddress ip = InetAddress.getLocalHost();
//			String ipStr = ip.getHostAddress();
//			return "http://"+ ipStr +":" + portMap + "/";
//		} catch (UnknownHostException e){
//			return "http://localhost:" + portMap + "/";
//		}
	}
	
	private Long getPortMapOnHost(Long portOnDocker, int dockerInstanceID){
            if (portOnDocker<1024){
                return portOnDocker + dockerInstanceID + 9000;
            } else {
                return portOnDocker + dockerInstanceID;
            }
        }
	
}
