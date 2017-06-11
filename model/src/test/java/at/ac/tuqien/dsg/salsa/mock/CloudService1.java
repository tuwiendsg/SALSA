/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuqien.dsg.salsa.mock;

import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.description.ServiceFile;
import at.ac.tuwien.dsg.salsa.model.properties.Artifact;
import at.ac.tuwien.dsg.salsa.model.properties.Capability;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;

/**
 *
 * @author hungld
 */
public class CloudService1 {

    public static void main(String[] args) throws IOException {
        CloudService service = new CloudService();
        ServiceTopology topo = new ServiceTopology();
        ServiceUnit unit1 = new ServiceUnit();
        unit1.setName("Component01");
        unit1.hasArtifact(new Artifact("test.sh"));        
        unit1.hasCapability(new Capability("turn-on", "test.sh on").hasCondition("state", "OFF").hasEffect("state", "ON"));
        unit1.hasCapability(new Capability("turn-off", "test.sh off").hasCondition("state", "ON").hasEffect("state", "OFF").hasEffect("@component2.state", "OFF"));

        ServiceUnit unit2 = new ServiceUnit();
        unit2.setName("Component02");

        service.hasTopology(topo);
        topo.setUuid("topouuid");
        topo.hasUnit(unit1);
        topo.hasUnit(unit2);

        ServiceFile def = new ServiceFile("myService", unit1, unit2);
        System.out.println(def.toYaml());
        
        
        System.out.println(def.toJson());
        
    }
}
