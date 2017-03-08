/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuqien.dsg.salsa.mock;

import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.properties.Capability;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author hungld
 */
public class SensorCapability {

    public static void main(String[] args) throws IOException {
        Set<Capability> capaSet = new HashSet<>();

        capaSet.add(new Capability("start", "./sensor.sh start").hasCondition("state", "OFF").hasEffect("state", "ON"));
        capaSet.add(new Capability("stop", "./sensor.sh stop").hasCondition("state", "ON").hasEffect("state", "OFF"));
        capaSet.add(new Capability("connectMQTT", "./sensor.sh connect-mqtt"));
        capaSet.add(new Capability("changeRate", "./sensor.sh change-rate"));
        capaSet.add(new Capability("wireMQTT", "./sensor.sh connect-mqtt").hasCondition("#URL", "#").hasCondition("#type", "MQTT").hasEffect("buffer", "#URL").hasEffect("bufferType", "MQTT"));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(capaSet);

        System.out.println(json);

        TypeReference<Set<Capability>> mapType = new TypeReference<Set<Capability>>() {
        };
        try {
            Set<Capability> capaList = mapper.readValue(json, mapType);
            System.out.println("CapaList has " + capaList.size() + " items !");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ServiceUnit unit = new ServiceUnit(UUID.randomUUID().toString(), "sensor");
        unit.setCapabilities(capaSet);
        System.out.println("----------------");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(unit));

    }

}
