package at.ac.tuwien.dsg.cloud.salsa.pioneer.StacksConfigurator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;

public class DockerConfigurator {	
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
	
	public void installDockerNode(String nodeId, int instanceId){
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
			return;
		}		
		String dockerInstanceId = String.format("%02d", instanceId);
		String newDockerImage=UUID.randomUUID().toString().substring(0, 5);		
		executeCommand("sudo docker build -t "+ newDockerImage +" . ");
		
        // install pioneer on docker and send request
        String portMap=portPrefix+ dockerInstanceId;
        String cmd = "/bin/bash pioneer_install.sh " + nodeId +" " + instanceId;
        //executeCommand("sudo docker run -p " + portMap + ":9000 " + "-d -t " + newDockerImage +" " + cmd +" ");
        executeCommand("sudo docker run -d -t " + newDockerImage +" " + cmd +" ");
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void initDocker_Depricate(){
		if (inited) {
			PioneerLogger.logger.debug("Docker is installed, not do it again !");
			return;
		}
		executeCommand("wget -q -N http://128.130.172.215/salsa/upload/files/pioneer/docker_install.sh");
		executeCommand("/bin/bash docker_install.sh");
		
		// create new docker container
		executeCommand("wget -q -N http://128.130.172.215/salsa/upload/files/pioneer/pioneer_install.sh");
		// don't know why the ADD command of docker doesn't copy file in /etc, just at current folder, so copy it to
		executeCommand("cp "+SalsaPioneerConfiguration.getSalsaVariableFile()+" ./");
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("FROM "+ SALSA_DOCKER_PULL +" \n");
		sb.append("ADD ./pioneer_install.sh ./pioneer_install.sh \n");
		sb.append("ADD ./salsa.variables /etc/salsa.variables \n");
		sb.append("RUN apt-get -q update \n");
		sb.append("RUN apt-get -q -y install openjdk-7-jre wget  \n");
		sb.append("EXPOSE 9000 \n");
		
		String pFilename = "./Dockerfile";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(pFilename));		
	        out.write(sb.toString());  
	        out.flush();
	        out.close();	        
		} catch (IOException e){
			PioneerLogger.logger.error("Could not create docker file ! Error: " + e);
			return;
		} 

		PioneerLogger.logger.debug("Prepare DOCKER image !");
        //String dockerInstanceId = String.format("%02d", instanceId);
        // build docker image
		// list docker image:
		String dockerImages = executeCommand("sudo docker images");
		boolean existing = false;
		for(String line : dockerImages.split(System.getProperty("line.separator"))){
			if (line.contains(SALSA_DOCKER_IMAGE_NAME)){
				PioneerLogger.logger.debug("There is a SALSA docker image on this host, we process faster !");
				existing = true;
				break;
			}
		}
		if (!existing) {
			PioneerLogger.logger.debug("There is no docker image on this host, create a new one !");
			executeCommand("sudo docker build -t "+ SALSA_DOCKER_IMAGE_NAME +" . ");
		}
		
		inited=true;
	}
	
}
