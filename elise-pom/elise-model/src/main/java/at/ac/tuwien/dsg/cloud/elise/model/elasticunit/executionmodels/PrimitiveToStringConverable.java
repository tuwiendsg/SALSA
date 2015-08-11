/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.executionmodels;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic.Capability;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

/**
 *
 * @author hungld
 */
public class PrimitiveToStringConverable {
    
    @Override
    public String toString(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            System.out.println("Error: Jackson cannot convert object to String. Object: " + super.toString());
            return null;
        }
    }
    
    static public PrimitiveToStringConverable fromString(String data) {
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructParametricType(ScriptExecution.class, RestExecution.class);
        try {
            return mapper.readValue(data, type);
        } catch (IOException ex) {
            System.out.println("Error: Jackson cannot convert to object this data: " + data);
            return null;
        }
    }
}
