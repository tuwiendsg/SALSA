/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.comot.elise.collector.rSYBL;

import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.UnitInstanceCollector;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Capability;
import at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels.RestExecution;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.IDType;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.CloudServiceXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceTopologyXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceUnitXML;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Duc-Hung LE
 */
public class rSYBLCollector extends UnitInstanceCollector {

    // http://128.130.172.216:8280/rSYBL/restWS/ElasticIoTPlatform/description
    String endpoint = "";

    {
        this.endpoint = readAdaptorConfig("endpoint");
        // default in the case iCOMOT are deployed on the same VM
        if (endpoint == null || endpoint.isEmpty()) {
            endpoint = "http://localhost:8280/rSYBL/restWS";
        }
    }

    // https://github.com/tuwiendsg/rSYBL/tree/3c2fc522790bdeb70206b6faee9b30fc4a2480c5/rSYBL-control-service-pom/rSYBL-cloud-application-dependency-graph/src/main/java/at/ac/tuwien/dsg/csdg/inputProcessing/multiLevelModel/abstractModelXML
    @Override
    public Set<UnitInstance> collectAllInstance() {
        Set<UnitInstance> unitInstances = new HashSet<>();
        String listOfServices = RestHandler.callRest(endpoint + "/elasticservices", RestHandler.HttpVerb.GET, null, null, MediaType.TEXT_PLAIN);
        if (listOfServices == null || listOfServices.isEmpty()) {
            System.out.println("SYBL does not manage any service");
            return null;
        }
        String[] arrayOfServices = listOfServices.split(",");
        for (String s : arrayOfServices) {
            System.out.println("Checking service with id: " + s);
            if (!s.trim().isEmpty()) {
                String serviceDescString = RestHandler.callRest(endpoint + "/" + s + "/description", RestHandler.HttpVerb.GET, null, null, MediaType.APPLICATION_XML);
                System.out.println("Description for service " + s + "is: \n" + serviceDescString);

                JAXBContext jc;
                CloudServiceXML cloudServiceXML = null;
                try {
                    System.out.println("Debug 1 - start to marshall data");
                    jc = JAXBContext.newInstance(CloudServiceXML.class);
                    Unmarshaller u = jc.createUnmarshaller();
                    System.out.println("created marshaller");
                    cloudServiceXML = (CloudServiceXML) u.unmarshal(new StringReader(serviceDescString));
                    System.out.println("marshalled cloud service xml done");
                    System.out.println("cloud service id: " + cloudServiceXML.getId());
                    for (ServiceTopologyXML topo : cloudServiceXML.getServiceTopologies()) {
                        System.out.println(" -- Checking topology id: " + topo.getId());
                        for (ServiceUnitXML unit : topo.getServiceUnits()) {
                            System.out.println(" ---- Checking service unit id: " + unit.getId());
                            if (unit.getXMLAnnotation() != null) {
                                System.out.println(" ---- >> The unit service has a Directive");
                                UnitInstance instance = new UnitInstance(cloudServiceXML.getId() + "/" + topo.getId() + "/" + unit.getId() + "/instances", ServiceCategory.ElasticPlatformService);
                                instance.hasCapability(new Capability("control", Capability.ExecutionMethod.REST, new RestExecution(endpoint + "/" + cloudServiceXML.getId() + "/startControlOnExisting", RestExecution.RestMethod.PUT, "")).executedBy("rSYBL"));
                                instance.hasExtra("rSYBL_UNIT_ID", cloudServiceXML.getId() + "/" + unit.getId());
                                unitInstances.add(instance);
                            }
                        }
                    }
                } catch (JAXBException e) {
                    e.printStackTrace();
                }

//                JAXBContext jaxbContext;
                //                try {
                //                    jaxbContext = JAXBContext.newInstance(CloudService.class);
                //                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                //                    CloudService cloudService = (CloudService) jaxbUnmarshaller.unmarshal(new StringReader(serviceDescString));
                //                    if (cloudService==null){
                //                        System.out.println("CloudService description is failed to marshall");
                //                        continue;
                //                    } else {
                //                        System.out.println("Seem to marshall done, service id: " + cloudService.getId());
                //                    }
                //                    for(ServiceTopology topo: cloudService.getTopology()){
                //                        for(ServiceUnit unit:topo.getServiceunits()){
                //                            if (unit.getDirective()!=null){
                //                                // create unit instance here
                //                                UnitInstance instance = new UnitInstance(cloudService.getId()+"/"+topo.getId()+"/"+unit.getId()+"/instances", ServiceCategory.ElasticPlatformService);
                //                                instance.hasCapability(new Capability("control", Capability.ExecutionMethod.REST, new RestExecution(endpoint+"/"+cloudService.getId()+"/startControlOnExisting", RestExecution.RestMethod.PUT, "")).executedBy("rSYBL"));
                //                                instance.hasExtra("rSYBL-Unit-ID", cloudService.getId()+"/"+unit.getId());
                //                            }
                //                        }
                //                    }
                //                } catch (JAXBException ex) {
                //                    ex.printStackTrace();
                //                    Logger.getLogger(rSYBLCollector.class.getName()).log(Level.SEVERE, null, ex);
                //                }
            }
        }
        return unitInstances;
    }

    @Override
    public UnitInstance collectInstanceByID(String domainID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LocalIdentification identify(UnitInstance paramUnitInstance) {
        String unitID = paramUnitInstance.getExtra().get("rSYBL_UNIT_ID");
        return new LocalIdentification(ServiceCategory.ElasticPlatformService, "rSYBL").hasIdentification(IDType.SALSA_UNIT.toString(), paramUnitInstance.getExtra().get("rSYBL-Unit-ID"));
    }

    @Override
    public String getName() {
        return "rSYBL-collector";
    }

}
