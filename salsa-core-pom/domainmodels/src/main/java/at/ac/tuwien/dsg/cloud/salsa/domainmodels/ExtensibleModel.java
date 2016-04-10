/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.domainmodels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 *
 * @author hungld
 */
public class ExtensibleModel {

    Class clazz;

    public ExtensibleModel(Class clazz) {
        this.clazz = clazz;
    }

    public Object readFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException ex) {
            return null;
        }
    }
    
    public static ExtensibleModel fromJson(String json){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, ExtensibleModel.class);
        } catch (IOException ex) {
            return null;
        }
    }

    public String writeToJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    public Class getClazz() {
        return clazz;
    }

}
