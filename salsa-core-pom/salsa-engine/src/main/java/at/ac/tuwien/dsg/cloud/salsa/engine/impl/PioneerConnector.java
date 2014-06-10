package at.ac.tuwien.dsg.cloud.salsa.engine.impl;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaPioneerInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;

public class PioneerConnector {

	SalsaPioneerInterface pioneer;
	URL url = null;
	public PioneerConnector(String ip) {
		try{
			this.url = new URL("http://"+ ip +":9000/pioneer?wsdl");			
		} catch (MalformedURLException e){
			EngineLogger.logger.error("Error when connecting to the pioneer !");			
		}
	}

	public String deploySoftwareNode(String nodeID, int instanceId){
		EngineLogger.logger.debug("Try to send command to pioneer to deploy node:  " + nodeID);
		Service service=Service.create(this.url, new QName("http://services.pioneer.salsa.cloud.dsg.tuwien.ac.at/","PioneerServiceImplementationService"));
		this.pioneer = service.getPort(SalsaPioneerInterface.class);		
		String result = this.pioneer.deployNode(nodeID, instanceId); 
		return result;
	}
	
	public String removeSoftwareNode(String nodeId, int instanceId){
		EngineLogger.logger.debug("Try to send command to pioneer to remove node: " + nodeId +"/" + instanceId);
		Service service=Service.create(this.url, new QName("http://services.pioneer.salsa.cloud.dsg.tuwien.ac.at/","PioneerServiceImplementationService"));
		this.pioneer = service.getPort(SalsaPioneerInterface.class);
		String result = this.pioneer.removeNodeInstance(nodeId, instanceId);
		return result;
	}
	
	public boolean checkHealth(){
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
}
