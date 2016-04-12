/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.dataAccess;

import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseRepository;
import at.ac.tuwien.dsg.cloud.elise.model.provider.ServiceTemplate;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.elise.model.extra.contract.Contract;
import at.ac.tuwien.dsg.cloud.elise.model.extra.contract.ContractItem;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Capability;
import at.ac.tuwien.dsg.cloud.elise.model.generic.ExtensibleModel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;

/**
 *
 * @author hungld
 */
public class testContractorModel {

    public static void main(String[] args) {
        
        /** CREATE A CONTRACT AND ATTACH TO A SERVICE TEMPLATE **/        
        Contract contract = new Contract();
        contract.setName("MyContract");
        Set<ContractItem> items = new HashSet<>();
        
        ContractItem item1 = new ContractItem();        
        item1.setName("BandwidthGuarantee");
        item1.setService("MonitorService1");
        
        ContractItem item2 = new ContractItem();
        item2.setName("Availability");
        item2.setService("MonitorService2");
        
        items.addAll(Arrays.asList(item1, item2));
        contract.setItems(items);
        
        // create new service template add the contract as an extensible model
        ServiceTemplate serviceTemplate = new ServiceTemplate("template1", ServiceCategory.SystemService);
        String uuid = UUID.randomUUID().toString();
        serviceTemplate.setUuid(uuid);
        
        serviceTemplate.setContract(contract);  
        
        /** CREATE PROXY TO THE REPOSITORY SERVICE AND SAVE THE SERVICE TEMPLATE **/
        String endpoint = "http://localhost:8080/salsa-engine/rest/elise/";
        EliseRepository mng = JAXRSClientFactory.create(endpoint, EliseRepository.class, Arrays.asList(new JacksonJaxbJsonProvider()));
        
        mng.saveServiceTemplate(serviceTemplate);
        
        /** READ AGAIN AND PRINT OUT **/
        ServiceTemplate templateRead = mng.readServiceTemplate(uuid);
        System.out.println(templateRead.toJson());
        
        /** TRY TO EDIT, SAVE AND PRINT AGAIN **/
        Contract contractRead = templateRead.getContract();
        ContractItem item3 = new ContractItem();
        item3.setName("QualityOfData");
        item3.setService("MonitorService3");
        contractRead.getItems().add(item3);
        
        serviceTemplate.setContract(contractRead);
        mng.saveServiceTemplate(serviceTemplate); // save again
        
        templateRead = mng.readServiceTemplate(uuid);  // read again
        System.out.println("\n=============\n"+templateRead.toJson());
        
    }
}
