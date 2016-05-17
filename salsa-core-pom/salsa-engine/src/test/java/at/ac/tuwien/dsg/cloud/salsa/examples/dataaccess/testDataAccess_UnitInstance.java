/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.examples.dataaccess;

import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.util.Arrays;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseRepository;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Capability;
import at.ac.tuwien.dsg.cloud.elise.model.provider.Artifact;
import at.ac.tuwien.dsg.cloud.elise.model.provider.ServiceTemplate;
import at.ac.tuwien.dsg.cloud.elise.model.generic.ExtensibleModel;
import at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels.RestExecution;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.State;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.VirtualMachineInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hungld
 */

public class testDataAccess_UnitInstance {

    public static void main(String[] args) {
        String endpoint = "http://localhost:9000/salsa-engine/rest/elise/";
        EliseRepository mng = JAXRSClientFactory.create(endpoint, EliseRepository.class, Arrays.asList(new JacksonJaxbJsonProvider()));
        
        
        

        UnitInstance unit = new UnitInstance("test", ServiceCategory.os);
        unit.setName("Localhost");
        unit.setUuid("73628b66-f93f-4623-97d9-c5edf8e748a9");
        
        Capability capa1 = new Capability("deploy", Capability.ExecutionMethod.REST, new RestExecution("http://example1.com", RestExecution.RestMethod.POST, ""));
        Capability capa2 = new Capability("undeploy", Capability.ExecutionMethod.REST, new RestExecution("http://example2.com", RestExecution.RestMethod.DELETE, ""));
        unit.setCapabilities(new HashSet<Capability>());
        unit.getCapabilities().add(capa1);
//        unit.getCapabilities().add(capa2);
        
        VirtualMachineInfo domain = new VirtualMachineInfo("localhost", "my-laptop", "VM:localhost");
        domain.setPublicIp("publicIP");
        domain.setPrivateIp("privateIP");
        
//        DomainEntity testDomain = new DomainEntity(ServiceCategory.Gateway, "domainID", "domainName", "started", "stopped");
//        unit.setDomain(testDomain);
        unit.setDomain(domain);
        unit.setState(State.CONFIGURING);
                
        GlobalIdentification iden = new GlobalIdentification();
        iden.addLocalIdentification((new LocalIdentification(ServiceCategory.docker, "me")).hasIdentification("key", "idenItem"));
        unit.setIdentification(iden);
        unit.setUuid("myUUID");
        
        

        System.out.println("\n CHECKING HEALTH >>>>>>>>>>>>>>>>>>>>>>>S");
        System.out.println(mng.health());
//        System.out.println(mng.getUnitCategory());

        System.out.println("\n 1--- SAVING 1 >>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(unit.toJson());
        mng.saveUnitInstance(unit);

        readAndShow(mng);

        

    }

    private static void readAndShow(EliseRepository mng) {
        System.out.println("\n READING >>>>>>>>>>>>>>>");
        System.out.println(mng.readUnitInstance("myUUID").toJson());
    }

    public static class AddressInfo extends ExtensibleModel {

        String city;
        String street;

        public AddressInfo(String city, String street) {
            super(AddressInfo.class);
            this.city = city;
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

    }
}
