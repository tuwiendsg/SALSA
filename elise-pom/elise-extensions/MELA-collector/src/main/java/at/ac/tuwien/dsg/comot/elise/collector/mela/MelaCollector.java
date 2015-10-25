/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.comot.elise.collector.mela;

import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.UnitInstanceCollector;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Capability;
import at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels.RestExecution;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.IDType;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Duc-Hung LE
 */
public class MelaCollector extends UnitInstanceCollector {

     // http://128.130.172.216:8080/MELA/REST_WS/elasticservices
    // /{serviceID}/metriccompositionrules/json
    // /{serviceID}/monitoringdata/xml
    // /home/hungld/workspace/MELA/MELA-Core/MELA-DataService/src/main/java/at/ac/tuwien/dsg/mela/dataservice/api/
    String endpoint = "";

    {
        // endpoint should be: http://128.130.172.216:8080/MELA/REST_WS
        endpoint = readAdaptorConfig("endpoint");
        // default
        if (endpoint==null){
            endpoint = "http://localhost:8080/MELA/REST_WS";
        }
    }

    @Override
    public Set<UnitInstance> collectAllInstance() {
        Set<UnitInstance> unitInstances = new HashSet<>();
        String listServiceJson = RestHandler.callRest(endpoint + "/elasticservices", RestHandler.HttpVerb.GET, null, null, MediaType.APPLICATION_JSON);
        if (listServiceJson != null && !listServiceJson.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<ElasticServiceList> theList = mapper.readValue(listServiceJson, mapper.getTypeFactory().constructCollectionType(List.class, ElasticServiceList.class));
                if (theList == null || theList.isEmpty()) {
                    System.out.println("There are no ElasticService in MELA");
                }
                for (ElasticServiceList elasticService : theList) {                    
                    UnitInstance newInstance = new UnitInstance("instanceOf_" + elasticService.getId(), ServiceCategory.ElasticPlatformService);
                    String monitorEndpoint = this.endpoint + "/" + elasticService.getId() + "/monitoringdata/json";
                    newInstance.hasCapability(new Capability("monitor", Capability.ExecutionMethod.REST, new RestExecution(monitorEndpoint, RestExecution.RestMethod.GET, null)).executedBy("MELA"));                    
                    newInstance.hasExtra("MELA_SERVICE_ID", elasticService.getId());
                    unitInstances.add(newInstance);
                }
            } catch (IOException ex) {
                System.out.println("Cannot query MELA");
            }
        } else {
            System.out.println("MELA does not manage any services");
        }
        return unitInstances;
    }

    @Override
    public UnitInstance collectInstanceByID(String domainID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LocalIdentification identify(UnitInstance paramUnitInstance) {
        String serviceID = paramUnitInstance.getExtra().get("MELA_SERVICE_ID");
        return new LocalIdentification(ServiceCategory.ElasticPlatformService, "MELA").hasIdentification(IDType.SALSA_SERVICE.toString(), serviceID);
    }

    @Override
    public String getName() {
        return "MELA-collector";
    }

}
