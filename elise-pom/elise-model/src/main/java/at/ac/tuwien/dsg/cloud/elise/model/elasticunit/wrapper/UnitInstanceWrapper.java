/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.wrapper;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.runtime.UnitInstance;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Duc-Hung LE
 */
public class UnitInstanceWrapper {

    Set<UnitInstance> unitInstances = new HashSet<>();

    public UnitInstanceWrapper() {
    }

    public UnitInstanceWrapper(Set<UnitInstance> instances) {
        this.unitInstances = instances;
    }

    public Set<UnitInstance> getUnitInstances() {
        return unitInstances;
    }

    public void setUnitInstances(Set<UnitInstance> unitInstances) {
        this.unitInstances = unitInstances;
    }
    
    public UnitInstanceWrapper hasInstance(UnitInstance instance){
        this.unitInstances.add(instance);
        return this;
    }
    
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            Logger.getLogger(UnitInstanceWrapper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static UnitInstanceWrapper fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, UnitInstanceWrapper.class);
        } catch (IOException ex) {
            Logger.getLogger(UnitInstanceWrapper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
