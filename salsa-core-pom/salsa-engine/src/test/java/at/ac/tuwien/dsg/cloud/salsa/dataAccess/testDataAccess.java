/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.dataAccess;

import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.util.Arrays;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseRepository;
import at.ac.tuwien.dsg.cloud.elise.model.provider.Artifact;
import at.ac.tuwien.dsg.cloud.elise.model.provider.ServiceTemplate;
import at.ac.tuwien.dsg.cloud.elise.model.generic.ExtensibleModel;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.VirtualMachineInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.application.WebAppInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType;
import java.util.Set;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author hungld
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class testDataAccess {

    public static void main(String[] args) {
        String endpoint = "http://localhost:8080/salsa-engine/rest/elise/";
        EliseRepository mng = JAXRSClientFactory.create(endpoint, EliseRepository.class, Arrays.asList(new JacksonJaxbJsonProvider()));
        
        
        

        UnitInstance unit = new UnitInstance("test", ServiceCategory.Docker);
        GlobalIdentification iden = new GlobalIdentification();
        iden.addLocalIdentification((new LocalIdentification(ServiceCategory.Docker, "me")).hasIdentification("key", "idenItem"));
        unit.setIdentification(iden);
        unit.setUuid("myUUID");

        System.out.println("\n CHECKING HEALTH >>>>>>>>>>>>>>>>>>>>>>>S");
        System.out.println(mng.health());
//        System.out.println(mng.getUnitCategory());

        System.out.println("\n 1--- SAVING 1 >>>>>>>>>>>>>>>>>>>>>>>");
        mng.saveUnitInstance(unit);

        readAndShow(mng);

        System.out.println("\n 2---- SAVING 2 >>>>>>>>>>>>>>>>>>>>>>>");
        unit.setCategory(ServiceCategory.ExecutableApp);

        readAndShow(mng);

        System.out.println("\n 3---- ADDING DOMAIN >>>>>>>>>>>>>>>>>>");        
        VirtualMachineInfo vmInfo = new VirtualMachineInfo("openstack", "randomInstanceID", "LoadBalancer");
//        WebAppInfo webAppInfo = new WebAppInfo("domainWebappID", "webappEndpoint");
//        unit.setDomain(webAppInfo);
//        unit.setDomainClazz(webAppInfo.getClass());
        mng.saveUnitInstance(unit);

        readAndShow(mng);

        System.out.println("\n 4--- ADDING Extra >>>>>>>>>>>>>>>>>>");
        AddressInfo address = new AddressInfo("Vienna", "Karlsplatz");
//        unit.hasExtra(address);
        mng.saveUnitInstance(unit);

        readAndShow(mng);

        System.out.println("\n 5--- ADDING Artifact");
        Artifact art = new Artifact("haproxy.sh", SalsaArtifactType.sh, "1.0", "http://localhost/files/haproxy.sh");
        mng.saveArtifact(art);
        System.out.println("\n READING >>>>>>>>>>>>>>>");
        Set<Artifact> arts = mng.readArtifact("haproxy.sh", null, null);
        for (Artifact a: arts){
            System.out.println(a.writeToJson());
        }
        
        System.out.println("\n 6--- ADDING Service Template");
        ServiceTemplate template = new ServiceTemplate("HAProxy", ServiceCategory.SystemService);
        template.setUuid("templateUUID");
        mng.saveServiceTemplate(template);
        System.out.println("\n READING SERVICE TEMPLATE >>>>>>>>>>>>>>>");
        System.out.println(mng.readServiceTemplate("templateUUID").toJson());
        
        System.out.println("\n 7--- EDIT Service Template");
        template.setName("HAProxy1111");
        
        mng.saveServiceTemplate(template);
        System.out.println("\n READING SERVICE TEMPLATE >>>>>>>>>>>>>>>");
        System.out.println(mng.readServiceTemplate("templateUUID").toJson());
        
        
        System.out.println("\n 8--- ADDING Artifact to template");
        template.hasArtifact(art);
        mng.saveServiceTemplate(template);
        System.out.println("\n READING SERVICE TEMPLATE >>>>>>>>>>>>>>>");
        System.out.println(mng.readServiceTemplate("templateUUID").toJson());

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
