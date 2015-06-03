package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TDefinitions;

import java.io.File;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription.AssociatedVM;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription.DeploymentDescription;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription.DeploymentUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineImplAll;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.log4j.Logger;

public class TestOtherFunctions {

    public static void main(String[] args) throws Exception {
        testSYBLGeneration();
    }

    public static void testSYBLGeneration() throws Exception {
        getServiceSYBL_DEP_DESP("/storage/ElasticIoTPlatform.data");
    }
    static Logger logger = Logger.getLogger("EngineLogger");

    public static String getServiceSYBL_DEP_DESP(String dataFile) {
        logger.debug("Generating deployment desp for SYBL");
        try {
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(dataFile);
            logger.debug("Service id: " + service.getId());
            DeploymentDescription sybl = new DeploymentDescription();
            sybl.setAccessIP("localhost");
            for (ServiceTopology topo : service.getComponentTopologyList()) {
                logger.debug("Topo ID: " + topo.getId());
                List<ServiceUnit> units = topo.getComponentsByType(SalsaEntityType.SOFTWARE);
                units.addAll(topo.getComponentsByType(SalsaEntityType.WAR));
                sybl.setCloudServiceID(dataFile);
                for (ServiceUnit unit : units) {
                    logger.debug("NodeID: " + unit.getId());
                    DeploymentUnit syblDepUnit = new DeploymentUnit();
                    syblDepUnit.setServiceUnitID(unit.getId());

                    for (ServiceInstance instance : unit.getInstancesList()) {
                        ServiceUnit hostedUnit = topo.getComponentById(unit.getHostedId());
                        ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());
                        // in the case we have more than one software stack
                        while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
                                && !hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
                            hostedUnit = topo.getComponentById(hostedUnit.getHostedId());
                            hostedInstance = hostedUnit.getInstanceById(hostedInstance.getHostedId_Integer());
                        }
                        logger.debug("Host instance: " + hostedUnit.getId() + "/" + hostedInstance.getInstanceId());
                        SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) hostedInstance.getProperties().getAny();
                        AssociatedVM assVM = new AssociatedVM();
                        assVM.setIp(vm.getPrivateIp().trim());
                        assVM.setUuid(vm.getInstanceId());
                        syblDepUnit.addAssociatedVM(assVM);
                    }
                    sybl.getDeployments().add(syblDepUnit);
                }
            }
            JAXBContext a = JAXBContext.newInstance(DeploymentDescription.class);
            Marshaller mar = a.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter xmlWriter = new StringWriter();
            mar.marshal(sybl, xmlWriter);
            System.out.println(xmlWriter.toString());
            return xmlWriter.toString();
        } catch (JAXBException e1) {
            String errorMsg = "Internal error: JAXB couldn't parse the service data";
            logger.error(errorMsg);
            return errorMsg;
        } catch (IOException e2) {
            String errorMsg = "Internal error: Couldn't read the service data.";
            logger.error(errorMsg);
            return errorMsg;
        }
    }

    public static void testBuildDataFromTosca() throws Exception {
        TDefinitions def = ToscaXmlProcess.readToscaFile("/home/hungld/test/tosca/2-DeployExecutableOnVM.xml");
        SalsaToscaDeployer deployer = new SalsaToscaDeployer(new File("/etc/cloudUserParameters.ini"));
        CloudService service = SalsaToscaDeployer.buildRuntimeDataFromTosca(def);
        SalsaXmlDataProcess.writeCloudServiceToFile(service, "/tmp/testSalsa.data");
    }

    public static void testCreateSalsaEntity() throws Exception {
//		CloudService service = new CloudService();
//		ConfigurationCapabilities confCatas = new ConfigurationCapabilities();
//		service.setConfiguationCapapabilities(confCatas);
//		ConfigurationCapability e = new ConfigurationCapability();
//		e.getMechanism().setExecutionType(ExecutionType.command);
//		e.getMechanism().setExecutionREF("/bin/date");		
//		confCatas.getConfigurationCapabilties().add(e);
//		SalsaXmlDataProcess.writeCloudServiceToFile(service, "/tmp/testSalsa.data");

    }

    public static void testConvertFromTosca() throws Exception {
        SalsaToscaDeployer deployer = new SalsaToscaDeployer(new File(SalsaEngineImplAll.class.getResource("/cloudUserParameters.ini").getFile()));
        TDefinitions def = ToscaXmlProcess.readToscaFile("/tmp/salsa/enriched1.xml");
        CloudService service = SalsaToscaDeployer.buildRuntimeDataFromTosca(def);
        System.out.println(service.getName());
        System.out.println(service.getAllComponent());
        SalsaXmlDataProcess.writeCloudServiceToFile(service, "/tmp/salsa/datafile.xml");
    }

}
