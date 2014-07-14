package at.ac.tuwien.dsg.cloud.salsa.cloud_connector.flexiant;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.CloudInterface;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.ServiceDeployerException;

import com.extl.jade.user.Condition;
import com.extl.jade.user.ExtilityException;
import com.extl.jade.user.FilterCondition;
import com.extl.jade.user.Job;
import com.extl.jade.user.ListResult;
import com.extl.jade.user.NetworkType;
import com.extl.jade.user.Nic;
import com.extl.jade.user.QueryLimit;
import com.extl.jade.user.ResourceMetadata;
import com.extl.jade.user.ResourceType;
import com.extl.jade.user.SearchFilter;
import com.extl.jade.user.Server;
import com.extl.jade.user.ServerStatus;
import com.extl.jade.user.UserAPI;
import com.extl.jade.user.UserService;




public class FlexiantConnector implements CloudInterface{
	
	String userEmailAddress = "hungld86@gmail.com";
	String apiUserName = "hungld86@gmail.com/65c02949-d9f8-38f3-898f-9e42776635b0";
	String customerUUID = "65c02949-d9f8-38f3-898f-9e42776635b0";
	String password = "thovasoi";
	String endpoint = "https://api.sd1.flexiant.net:4442";
	String vdcUUID = "bde1ffba-3a8e-3315-a505-3ec67e6fa771"; // salsa_vdc
	String defaultProductOfferUUID="886ae014-0613-3cc8-a790-16251471e624";
	String deploymentInstanceUUID;
	String clusterUUID = "1ff16f43-4a82-34bf-8f07-ea6d210548ab"; // VMWare cluster
	String sshKey = "root";
	String networkUUID = "66ac8676-b258-36a8-a547-0151c110b556"; // default network
	Logger logger;
	String ENDPOINT_ADDRESS_PROPERTY = "https://api.sd1.flexiant.net:4442";
	String DEFAULT_IMAGE="a064bd97-c84c-38ef-aa37-c7391a8c8259";
	
	UserService service;
	
	public FlexiantConnector(Logger logger, String email, String cuscomerUUID, String password, String endpoint, String vdcUUID, String defaultProductOfferUUID, String sshKey){
		this.logger = logger;
		this.userEmailAddress = email;
		this.customerUUID = cuscomerUUID;
		this.password = password;
		this.endpoint = endpoint;
		this.vdcUUID = vdcUUID;
		this.defaultProductOfferUUID = defaultProductOfferUUID;
		this.sshKey = sshKey;
		
		 //UserService service;
        URL url = ClassLoader.getSystemClassLoader().getResource("Flexiant/UserAPI.wsdl");
        
        UserAPI api = new UserAPI(url, new QName("http://extility.flexiant.net", "UserAPI"));
        
        service = api.getUserServicePort();
        
        BindingProvider portBP = (BindingProvider) service;
        
        portBP.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                this.endpoint+"/user/");
        portBP.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
                userEmailAddress + "/" + customerUUID);
        portBP.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
                password);
		
	}
		
	@Override
	public String launchInstance(String instanceName, String imageId,
			List<String> securityGroups, String sshKeyName, String userData,
			String instType, int minInst, int maxInst)
			throws ServiceDeployerException {		
		 try {
			 Nic networkInterface = new Nic();
				networkInterface.setClusterUUID(this.clusterUUID);
				networkInterface
						.setCustomerUUID(this.customerUUID);
				networkInterface.setDeploymentInstanceUUID(this.deploymentInstanceUUID);
				//networkInterface.setProductOfferUUID("");

				networkInterface.setNetworkUUID(this.networkUUID);
				networkInterface.setVdcUUID(this.vdcUUID);
				networkInterface.setNetworkType(NetworkType.IP);
			 
	        	Server server = new Server();
	    		server.setResourceName(instanceName);
	    		server.setCustomerUUID(this.customerUUID);
	    		server.setProductOfferUUID(this.defaultProductOfferUUID);
	    		server.setVdcUUID(this.vdcUUID);
	    		server.setImageUUID(imageId);
	    		server.setInitialPassword("dsg@123");
	    		server.setCpu(1);	// default
	    		server.setRam(2);
	    		server.getNics().add(networkInterface);
	    		
	    		ResourceMetadata meta = new ResourceMetadata();
	    		meta.setPrivateMetadata(userData);	   
	    		server.setResourceMetadata(meta);
	    		DatatypeFactory datatypeFactory = null;	    		
	    		try {
	    			datatypeFactory = DatatypeFactory.newInstance();
	    		} catch (DatatypeConfigurationException e) {
	    			e.printStackTrace();
	    			logger.error(e.getMessage()); return "";
	    		}
	    		
	    		List<String> sshKeys = new ArrayList<>();
	    		Job createServerJob = service.createServer(server, sshKeys, null);
	    		
	    		
	    		try {
	    			service.waitForJob(createServerJob.getResourceUUID(), false);
	    		} catch (ExtilityException e) {    		
	    			e.printStackTrace();
	    			logger.error(e.getMessage()); return "";
	    		}
	    		logger.debug("Server created: " + createServerJob.getItemUUID());
	    		
	    		
	    		// wait for host is up   
	    		Job startServer;
	    		try {
	    			startServer = service.changeServerStatus(createServerJob.getItemUUID(), ServerStatus.RUNNING, true,
	    					server.getResourceMetadata(), null);
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
	    		logger.debug("Server is up: " + createServerJob.getItemUUID() + ". IP: ");
	    		
	    		
	    		
	    		logger.debug(createServerJob.getItemDescription());
	    		logger.info("~~~~~~~~~~~~~~Created server with uuid "+createServerJob.getItemUUID());
	    		
	    		// push pioneer by ssh
	    		
	    		
	    		return createServerJob.getItemUUID();
	    		    		
	        } catch (Exception e) {                
	                e.printStackTrace();
	        }
		
		return null;
	}
	
	
	
	
	public static String ArrayToString(ByteArrayInputStream is) {
	    int size = is.available();
	    char[] theChars = new char[size];
	    byte[] bytes    = new byte[size];

	    is.read(bytes, 0, size);
	    for (int i = 0; i < size;)
	        theChars[i] = (char)(bytes[i++]&0xff);
	    
	    return new String(theChars);
	      }

	@Override
	public InstanceDescription getInstanceDescriptionByID(String instanceID) {
		SearchFilter sf = new SearchFilter();
		FilterCondition fc = new FilterCondition();

//		fc.setCondition(Condition.IS_EQUAL_TO);
//		fc.setField("status");
//		fc.getValue().add(ServerStatus.RUNNING.name());
//		fc.getValue().add(ServerStatus.STARTING.name());
//		sf.getFilterConditions().add(fc);

		// Set a limit to the number of results
		QueryLimit lim = new QueryLimit();
		lim.setMaxRecords(40);
		
		try {
			// Call the service to execute the query
			ListResult result = service.listResources(sf, lim, ResourceType.SERVER);
	
			String ip="";
			
			// Iterate through the results
			for (Object o : result.getList()) {
				Server s = ((Server) o);
				logger.info("Server " + s.getResourceUUID());
				if (s.getResourceUUID().equals(instanceID)){				
					for (Nic nic : s.getNics()) {
						if (nic.getIpAddresses().size()>0){					
							ip = nic.getIpAddresses().get(0).getIpAddress();
							logger.debug("IP = " + ip);
						}
					}
				}
			}
			InstanceDescription inst = new InstanceDescription(instanceID, ip, ip);
			
			return inst;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	

	@Override
	public void removeInstance(String instanceToTerminateID)
			throws ServiceDeployerException {
		// TODO Auto-generated method stub
		
	}
	
	
}
