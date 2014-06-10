package at.ac.tuwien.dsg.cloud.salsa.cloud_connector.flexiant;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;

import com.extl.jade.user.ExtilityException;
import com.extl.jade.user.Job;
import com.extl.jade.user.NetworkType;
import com.extl.jade.user.Nic;
import com.extl.jade.user.Server;
import com.extl.jade.user.ServerStatus;
import com.extl.jade.user.UserAPI;
import com.extl.jade.user.UserService;


public class FlexiantConnector {
	
	String userEmailAddress ;
	String apiUserName ;
	String customerUUID ;
	String password ;
	String endpoint = "https://api.sd1.flexiant.net:4442";
	String vdcUUID ; // salsa_vdc
	String defaultProductOfferUUID;
	String deploymentInstanceUUID;
	String clusterUUID ; // VMWare cluster
	String sshKey = "root";
	String networkUUID ; // default network
	Logger logger;
	String ENDPOINT_ADDRESS_PROPERTY = "https://api.sd1.flexiant.net:4442";
	String DEFAULT_IMAGE;
	
	public FlexiantConnector(Logger logger, String email, String apiUserName, String cuscomerUUID, String password, String endpoint, String vdcUUID, String defaultProductOfferUUID, String deploymentInstanceUUID, String getClusterUUID, String sshKey, String networkUUID){
		this.logger = logger;
		this.userEmailAddress = email;
		this.apiUserName = apiUserName;
		this.customerUUID = cuscomerUUID;
		this.password = password;
		this.endpoint = endpoint;
		this.vdcUUID = vdcUUID;
		this.defaultProductOfferUUID = defaultProductOfferUUID;
		this.deploymentInstanceUUID = deploymentInstanceUUID;
		this.clusterUUID = getClusterUUID;
		this.sshKey = sshKey;
		this.networkUUID = networkUUID;
	}
	
	public FlexiantConnector(){
		
	}
		
	/*
	 * create new server
	 */
	public String createNewServer(String serverName, String imageUUID, int cpu,
			int mem) {

		UserService service;

		//URL url = ClassLoader.getSystemClassLoader().getResource("/Flexiant/UserAPI.wsdl");
		URL url = FlexiantConnector.class.getClassLoader().getResource(".");
				

		// Get the UserAPI
		System.out.println("UserAPI init. Localtion: " + url.toString());
		UserAPI api = new UserAPI(url, new QName("http://extility.flexiant.net", "UserAPI"));

		// and set the service port on the service
		service = api.getUserServicePort();

		// Get the binding provider
		BindingProvider portBP = (BindingProvider) service;

		// and set the service endpoint
		portBP.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				ENDPOINT_ADDRESS_PROPERTY);

		// and the caller's authentication details and password
		portBP.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
				userEmailAddress + "/" + customerUUID);
		portBP.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
				password);

		Server skeletonServer = new Server();
		skeletonServer.setVdcUUID(this.vdcUUID);
		// skeletonServer.setCpu(cpu);
		skeletonServer.setInitialUser("ubuntu");

		skeletonServer.setCustomerUUID(this.customerUUID);
		// skeletonServer.setProductOfferUUID("8a657434-b0c5-3a99-83bf-87cf4c9dedb8");
		skeletonServer.setProductOfferUUID(defaultProductOfferUUID);
		// skeletonServer.setRam(mem);
		// skeletonServer.setImageName(serverName);
		logger.info("~~~~~~~~~~~~~~~~~~~~~~~ Creating Server from image "+imageUUID);
		skeletonServer.setImageUUID(imageUUID);
		skeletonServer.setDeploymentInstanceUUID(deploymentInstanceUUID);
		skeletonServer.setClusterUUID(clusterUUID);
		
		List<String> sshs = new ArrayList<String>();
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		sshs.add(this.sshKey);
		Nic networkInterface = new Nic();
		networkInterface.setClusterUUID(this.clusterUUID);
		networkInterface
				.setCustomerUUID(this.customerUUID);
		networkInterface.setDeploymentInstanceUUID(this.deploymentInstanceUUID);
		//networkInterface.setProductOfferUUID("");

		networkInterface.setNetworkUUID(this.networkUUID);
		networkInterface.setVdcUUID(this.vdcUUID);
		networkInterface.setNetworkType(NetworkType.IP);
		// networkInterface.setServerUUID("");
		Date date = new Date();

		DatatypeFactory datatypeFactory = null;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage()); return "";
		}
		XMLGregorianCalendar now = datatypeFactory
				.newXMLGregorianCalendar(gregorianCalendar);
		int mins = date.getMinutes();
		int sec = date.getSeconds();
		int hours = date.getHours();
		
		Job createServerJob = null;
		
		date = new Date();
		datatypeFactory = null;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage()); return "";
		}
		now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
		 mins = date.getMinutes();
		 sec = date.getSeconds();
		 hours = date.getHours();

			sec += 10;
			if (sec >= 60) {
				sec -= 60;
				mins += 1;
			}
			if (mins==60){
				mins=59;
			}
			
		now.setTime(hours, mins, sec);
		skeletonServer.setResourceName(serverName);
		skeletonServer.getNics().add(networkInterface);
		try {
			createServerJob = service.createServer(skeletonServer, sshs, now );
		} catch (ExtilityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage()); return "";
		}

		try {
			service.waitForJob(createServerJob.getResourceUUID(), false);
		} catch (ExtilityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage()); return "";
		}

		
		date = new Date();
		now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
		 mins = date.getMinutes();
		 sec = date.getSeconds();
		 hours = date.getHours();
		sec += 10;
		if (sec >= 60) {
			sec -= 60;
			mins += 1;
		}
		if (mins==60){
			mins=59;
		}
		
		
		now.setTime(hours, mins, sec);

		Job startServer = null;
		try {
			startServer = service.changeServerStatus(
					createServerJob.getItemUUID(), ServerStatus.RUNNING, true,
					skeletonServer.getResourceMetadata(), now);
		} catch (ExtilityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage()); return "";
		}

		try {
			service.waitForJob(startServer.getResourceUUID(), false);
		} catch (ExtilityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage()); return "";
		}
		logger.info("~~~~~~~~~~~~~~Created server with uuid "+createServerJob.getItemUUID());
		return createServerJob.getItemUUID();

		// createdServer.getNics().get(0).getIpAddresses().get(0).getIpAddress();
	}
	
	
	
	
}
