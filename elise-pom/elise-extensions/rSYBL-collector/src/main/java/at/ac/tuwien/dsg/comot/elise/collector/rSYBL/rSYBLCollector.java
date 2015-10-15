/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.comot.elise.collector.rSYBL;

import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.UnitInstanceCollector;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.comot.elise.collector.rSYBL.data.CloudService;
import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @Override
    public Set<UnitInstance> collectAllInstance() {
        String listOfServices = RestHandler.callRest(endpoint + "/elasticservices", RestHandler.HttpVerb.GET, null, null, MediaType.TEXT_PLAIN);
        if (listOfServices == null || listOfServices.isEmpty()) {
            System.out.println("SYBL does not manage any service");
            return null;
        }
        String[] arrayOfServices = listOfServices.split(",");
        for (String s : arrayOfServices) {
            if (!s.trim().isEmpty()) {
                String serviceDescString = RestHandler.callRest(endpoint + "/" + s+"/description", RestHandler.HttpVerb.GET, null, null, MediaType.APPLICATION_XML);
                File file = new File("C:\\file.xml");
		JAXBContext jaxbContext;
                try {
                    jaxbContext = JAXBContext.newInstance(CloudService.class);
                } catch (JAXBException ex) {
                    Logger.getLogger(rSYBLCollector.class.getName()).log(Level.SEVERE, null, ex);
                }

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		CloudService cloudService = (CloudService) jaxbUnmarshaller.unmarshal(file);
		
            }
        }
        return null;
    }

    @Override
    public UnitInstance collectInstanceByID(String domainID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LocalIdentification identify(UnitInstance paramUnitInstance) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
