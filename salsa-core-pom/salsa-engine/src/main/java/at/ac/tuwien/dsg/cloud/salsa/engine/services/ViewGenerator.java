package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import generated.JaxbGangliaEntities.GangliaHostInfo;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata.ServiceJsonDataTree;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata.ServiceJsonList;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.CenterConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
@Path("/viewgenerator")
public class ViewGenerator {
static Logger logger;
	static {
		logger = Logger.getLogger("SalsaCenterLogger");
	}
		
	@GET
	@Path("/cloudservice/json/compact/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeJsonTreeCompact(@PathParam("serviceId") String serviceDeployId) {
		if (serviceDeployId.equals("") || serviceDeployId.equals("null")){
			return "";
		}
		try {
			String salsaFile = CenterConfiguration.getServiceStoragePath() + "/"	+ serviceDeployId + ".data";
			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			
			ServiceJsonDataTree datatree = new ServiceJsonDataTree();
			datatree.setId(service.getName());
			datatree.setNodeType("CLOUD SERVICE");	
			datatree.setState(service.getState());
			
			List<ServiceTopology> topos = service.getComponentTopologyList();
			for (ServiceTopology topo : topos) {
				List<ServiceUnit> components = topo.getComponentsByType(SalsaEntityType.OPERATING_SYSTEM);
				for (ServiceUnit compo : components) {
					ServiceJsonDataTree componode = new ServiceJsonDataTree();
					componode.loadData(compo, -1, topo);	// -1 will not check instance id
					datatree.addChild(componode);	
				}
			}
			
			datatree.compactData();	// parent=null for root node
			Gson json = new GsonBuilder().setPrettyPrinting().create();
			
			return json.toJson(datatree);
			
		} catch (IOException e){
			logger.error("Cannot read service file. " + e);			
		} catch (JAXBException e1){
			logger.error("Error when parsing service file." + e1);
		}
		
		
		return "";
	}
	
	@GET
	@Path("/cloudservice/json/full/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeJsonTree(@PathParam("serviceId") String serviceDeployId,
			@DefaultValue("0") @QueryParam("health") int health) {
		if (serviceDeployId.equals("") || serviceDeployId.equals("null")){
			return "";
		}
		try {
			String salsaFile = CenterConfiguration.getServiceStoragePath() + "/"	+ serviceDeployId + ".data";			
			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			if (health==1){
				enrichWithGangliaInfo(service);
			}
			
			ServiceJsonDataTree datatree = new ServiceJsonDataTree();
			datatree.setId(service.getName());
			datatree.setNodeType("CLOUD SERVICE");
			datatree.setState(service.getState());
			
			List<ServiceTopology> topos = service.getComponentTopologyList();
			for (ServiceTopology topo : topos) {
				List<ServiceUnit> components = service.getAllComponentByType(SalsaEntityType.OPERATING_SYSTEM);
				for (ServiceUnit compo : components) {
					ServiceJsonDataTree componode = new ServiceJsonDataTree();
					componode.loadData(compo, -1, topo);	// -1 will not check instance id
					datatree.addChild(componode);
					//logger.debug("add a child node: " + componode.getId());
				}
			}
			
			
			
			
			Gson json = new GsonBuilder().setPrettyPrinting().create();		
			return json.toJson(datatree);
			
		} catch (IOException e){
			logger.error("Cannot read service file. " + e);			
		} catch (JAXBException e1){
			logger.error("Error when parsing service file." + e1);
		}
		
		
		return "";
	}
	

	@GET
	@Path("/cloudservice/xml/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeXml(@PathParam("serviceId") String serviceDeployId) {
		if (serviceDeployId.equals("") || serviceDeployId.equals("null")){
			return "";
		}
		String fileName = CenterConfiguration.getServiceStoragePath() + "/"
				+ serviceDeployId + ".data";
		try {
			String xml = FileUtils.readFileToString(new File(fileName));
			return xml;
		} catch (Exception e) {
//			logger.error("Could not find service: " + serviceDeployId
//					+ ". Data did not be sent. Error: " + e.toString());
			return "Error";
		}
	}
		
	@GET
	@Path("/cloudservice/json/list")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceJsonList() {
		String pathName = CenterConfiguration.getServiceStoragePath();
		try {
			ServiceJsonList serviceList = new ServiceJsonList(pathName);
			Gson json = new GsonBuilder().setPrettyPrinting().create();
			return json.toJson(serviceList);
		} catch (Exception e) {
			logger.error("Could not list services");
			return "";
		}

	}
	
	
	private void enrichWithGangliaInfo(CloudService service){
		for (ServiceTopology topo : service.getComponentTopologyList()) {
			for (ServiceUnit unit : topo.getComponentsByType(SalsaEntityType.OPERATING_SYSTEM)) {				
				for (ServiceInstance instance : unit.getInstancesList()) {
					SalsaMonitoringInfo moni = new SalsaMonitoringInfo();
					SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) instance.getProperties().getAny();
					String gangliaXML = moni.getVMInformation(vm);
					
					try {						
						JAXBContext jaxbContext = JAXBContext.newInstance(GangliaHostInfo.class);				 
						Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
						GangliaHostInfo gangliaObj = (GangliaHostInfo) jaxbUnmarshaller.unmarshal(new StringReader(gangliaXML));
						instance.setMonitoring(gangliaObj);
					  } catch (JAXBException e) {
						EngineLogger.logger.error(e.toString());						
					  }
					
					
				}
				
			}
			
		}
		
		
		
		for (ServiceInstance osNode : service.getAllReplicaByType(SalsaEntityType.OPERATING_SYSTEM)) {
			// get the ganglia info
			SalsaMonitoringInfo moni = new SalsaMonitoringInfo();
			
			
			//moni.getMonitorOfInstance(service.getId(), service.get, nodeId, instanceId)
			//String str = SalsaEngineInternal.
			// parse the XML to GangliaHostInfo
			
			// attach to the osNode
			//GangliaHostInfo ganglia;
			//osNode.g
		}
	}
}
