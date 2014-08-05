package at.ac.tuwien.dsg.cloud.salsa.engine.impl;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaPioneerInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;

public class PioneerConnector {

	SalsaPioneerInterface pioneer;
	URL url = null;
	public PioneerConnector(String ip) {
		try{
			this.url = new URL("http://"+ ip +":9000/");
			this.pioneer = JAXRSClientFactory.create("http://"+ ip +":9000/", SalsaPioneerInterface.class);
			
		} catch (MalformedURLException e){
			EngineLogger.logger.error("Error when connecting to the pioneer !");			
		}		
	}

	public String deploySoftwareNode(String nodeID, int instanceId){		
		EngineLogger.logger.debug("Try to send command to pioneer to deploy node:  " + nodeID);
		return this.pioneer.deployNode(nodeID, instanceId);
	}
	
	public String removeSoftwareNode(String nodeId, int instanceId){
		EngineLogger.logger.debug("Try to send command to pioneer to remove node: " + nodeId +"/" + instanceId);
		return this.pioneer.removeNodeInstance(nodeId, instanceId);
	}
	
	public boolean checkHealth2(){
		try {			
			HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
			    return false;
			}
			return true;
		} catch(Exception e){
			return false;
		}
	}
	
	public boolean checkHealth(){
		try {
			String str = this.pioneer.health();
			if (str.equals("alive")){
				return true;
			}
		} catch (Exception e){
			return false;
		}
		return false;
	}
	
}
