/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuqien.dsg.salsa.mock;

import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
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
        ServiceUnit unit = new ServiceUnit();
        unit.hasCapability(new Capability("date", "/bin/date"));

        service.hasTopology(topo);
        topo.hasUnit(unit);

//        System.out.println(service.toJson());

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setSerializationInclusion(Include.NON_NULL);
        System.out.println(mapper.writeValueAsString(service));
    }
}
