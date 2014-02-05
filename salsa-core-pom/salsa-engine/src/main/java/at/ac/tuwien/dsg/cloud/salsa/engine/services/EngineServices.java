package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import generated.oasis.tosca.TDefinitions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * This restful services provide couple of services for actions on services.
 * @author Le Duc Hung
 *
 */
@Path("/")
public class EngineServices {
	static Logger logger;
	File configFile = new File(EngineServices.class.getResource("/cloudUserParameters.ini").getFile());

	static {
		logger = Logger.getLogger("EngineLogger");
	}
	
	
	@POST
	@Path("/test")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	public Response test() {
		logger.debug("Test method invoked");		
		return Response.status(200).entity("Test method").build();
	}
	
	
	
	
	/**
	 * This service deploys the whole Tosca file.
	 * The form which is posted to service must contain all parameters
	 * @param uploadedInputStream The file contents
	 * @param fileDetail The file detail which is optional
	 * @param serviceName The ServiceName. This must be unique in whole system.
	 * @return The information
	 */
	@POST
	@Path("/deploy")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response deployService(//){
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("serviceName") String serviceName) {
		logger.debug("Recieved deployment request with name: " + serviceName);
		String tmp_id = UUID.randomUUID().toString();
		String tmpFile="/tmp/salsa_tmp_"+tmp_id;
		if (!checkForServiceName(serviceName)){
			return Response.status(202).entity("Error. Service Name is bad: " +serviceName).build();
		}		
		try {
			writeToFile(uploadedInputStream, tmpFile);
			TDefinitions def = ToscaXmlProcess.readToscaFile(tmpFile);
			SalsaToscaDeployer deployer = new SalsaToscaDeployer(this.configFile);
			deployer.deployNewService(def);
			String output = "Deployed service. Id: ";
			logger.debug(output);
			return Response.status(200).entity(output).build();
		} catch (JAXBException e){
			logger.error("Error when parsing Tosca: " + e);
			e.printStackTrace();
			return Response.status(201).entity("Error. Unable to parse Tosca. Error: " +e).build();
		} catch (IOException e) {
			logger.error("Error reading file: " + tmpFile + ". Error: " +e);
			return Response.status(201).entity("Error when process Tosca file. Error: " +e).build();
		}	
		
	}
	
	private boolean checkForServiceName(String serviceName){
		if (!serviceName.equals("")){
			return true;
		}
		return false;
	}
	
	@GET
	@Path("/undeploy/{serviceId}")
	public Response undeployService(@PathParam("serviceId")String serviceId){
		SalsaToscaDeployer deployer = new SalsaToscaDeployer(configFile);
		if (deployer.cleanAllService(serviceId)) {		
			return Response.status(200).entity("Cleaning service done: " + serviceId).build();
		} else {
			return Response.status(201).entity("Error: Fail to clean service: " + serviceId).build();
		}
	}
	
	
	
	// save uploaded file to new location
	private static void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) throws IOException {
		OutputStream out = new FileOutputStream(new File(
				uploadedFileLocation));
		int read = 0;
		byte[] bytes = new byte[1024];
		
		while ((read = uploadedInputStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();					
	}
	
}
