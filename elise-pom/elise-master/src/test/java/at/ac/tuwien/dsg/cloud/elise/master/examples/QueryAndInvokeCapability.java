/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.examples;

import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.CapabilityManagement;
import at.ac.tuwien.dsg.cloud.elise.master.RESTImp.EliseCommunicationInterface;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Capability;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQuery;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQueryRule;
import java.util.Collections;
import java.util.Set;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseRepository;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 *
 * @author hungld
 */
public class QueryAndInvokeCapability {

    public static void main(String[] args) {
        String endpoint = "http://128.130.172.215:8080/salsa-engine/rest/elise/";

        // the 1st proxy to manage the queries
        EliseCommunicationInterface manageProxy = JAXRSClientFactory.create(endpoint, EliseCommunicationInterface.class);
        EliseQuery query = new EliseQuery(ServiceCategory.Gateway)
                .hasRule("location", "building1", EliseQueryRule.OPERATION.EQUAL);
        String queryID = manageProxy.querySetOfInstances(query);
        manageProxy.getQueryProcessStatus(queryID); // block

        // the 2nd proxy to manage the information
        EliseRepository localProxy = JAXRSClientFactory.create(endpoint, EliseRepository.class, Collections.singletonList(new JacksonJaxbJsonProvider()));
        Set<UnitInstance> instances = localProxy.query(query);
        for (UnitInstance instance : instances) {
            Capability c = instance.getCapabilityByName("changeRate");
            if (c != null) {
                CapabilityManagement.execute(c, new String[]{"5"});
            }
        }
    }

}
