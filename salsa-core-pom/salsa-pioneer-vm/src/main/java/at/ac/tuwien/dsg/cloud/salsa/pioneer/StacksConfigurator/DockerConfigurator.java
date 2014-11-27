package at.ac.tuwien.dsg.cloud.salsa.pioneer.StacksConfigurator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;

public class DockerConfigurator{	
	static String portPrefix = "498";	
	static String SALSA_DOCKER_IMAGE_NAME="leduchung/salsa";
	static String SALSA_DOCKER_PULL="leduchung/salsa:latest";
	static boolean inited=false;
	
	String dockerNodeId="default";	
	
	public void initDocker(){
		if (inited) {
			PioneerLogger.logger.debug("Docker is installed, not do it again !");
			return;
		}
		executeCommand("wget -q -N http://128.130.172.215/salsa/upload/files/pioneer/docker_install.sh");
		executeCommand("/bin/bash docker_install.sh");
		executeCommand("sudo docker pull "+ SALSA_DOCKER_IMAGE_NAME);
		inited=true;
	}	
	
	public DockerConfigurator(String dockerNodeId){
		this.dockerNodeId = dockerNodeId;
	}
	
	public String installDockerNode(String nodeId, int instanceId){
		// build new image with correct salsa.variable file
		StringBuffer sb = new StringBuffer();
		
		sb.append("FROM "+ SALSA_DOCKER_PULL +" \n");
		sb.append("COPY ./salsa.variables /etc/salsa.variables \n");
		sb.append("RUN wget -q -N http://128.130.172.215/salsa/upload/files/pioneer/pioneer_install.sh \n");
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
		executeCommand("sudo docker build -t "+ newDockerImage +" . ");
		
        // install pioneer on docker and send request
        String portMap=portPrefix+ dockerInstanceId;
        String cmd = "/bin/bash pioneer_install.sh " + nodeId +" " + instanceId;
        //return executeCommand("sudo docker run -p " + portMap + ":9000 " + "-d -t " + newDockerImage +" " + cmd +" ");
        return executeCommand("sudo docker run -d --name "+nodeId+"_"+instanceId+" -t " + newDockerImage +" " + cmd +" ");
	}
	
	public SalsaInstanceDescription_VM getDockerInfo(String containerID){
        // TODO: a bit hack here
		SalsaInstanceDescription_VM dockerMachine = new SalsaInstanceDescription_VM("local@dockerhost", containerID);
        dockerMachine.setBaseImage("salsa.ubuntu");
        dockerMachine.setInstanceType("os");
        String isRunning = executeCommand("docker inspect --format='{{.State.Running}}' " + containerID);
        if (isRunning.equals("true")){
        	dockerMachine.setState("RUNNING");
        } else {
        	dockerMachine.setState("NOT RUNNING");
        }
        // get IP
        String ip = executeCommand("docker inspect --format='{{.NetworkSettings.IPAddress}}' " + containerID);
        dockerMachine.setPrivateIp(ip);
        dockerMachine.setPublicIp(ip);
        return dockerMachine;
	}
	
	public String removeDockerContainer(String containerID){
		 executeCommand("sudo docker kill " + containerID);
		 executeCommand("sudo docker rm -f " + containerID);
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
	
	
	private static String executeCommand(String cmd){
		PioneerLogger.logger.debug("Execute command: " + cmd);
		try {
			Process p = Runtime.getRuntime().exec(cmd);
		    
		 
		    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    String line = "";	
		    StringBuffer output = new StringBuffer();
		    while ((line = reader.readLine())!= null) {
		    	output.append(line + "\n");
		    }
		    p.waitFor();
		    PioneerLogger.logger.debug(output.toString());
			return output.toString();
		} catch (InterruptedException e1){
			PioneerLogger.logger.error("Error when execute command. Error: " + e1);
		} catch (IOException e2){
			PioneerLogger.logger.error("Error when execute command. Error: " + e2);
		}
		return null;
	}
}
