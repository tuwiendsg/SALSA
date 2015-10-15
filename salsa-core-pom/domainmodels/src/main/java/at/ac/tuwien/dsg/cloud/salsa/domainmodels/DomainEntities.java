/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.domainmodels;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This class manages set of DomainEntity, which used for collecting both application and infrastructure information
 * @author Duc-Hung LE
 */
public class DomainEntities {
    protected Set<DomainEntity> entities;

    public DomainEntities() {
    }

    public Set<DomainEntity> getEntities() {
        return entities;
    }
    
    public DomainEntities(Set<DomainEntity> list){
        this.entities = list;
    }
    
    public DomainEntities hasDomainEntity(DomainEntity d){
        if (this.entities == null){
            this.entities = new HashSet<>();
        }
        this.entities.add(d);
        return this;
    }
    
    public DomainEntity findDomainEntityByCategory(ServiceCategory category){
        if (entities == null){
            return null;
        }
        for(DomainEntity e: entities){
            if (e.getCategory().equals(category)){
                return e;
            }
        }
        return null;
    }
    
     public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            Logger.getLogger(DomainEntities.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static DomainEntities fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);

        try {
            return mapper.readValue(json, DomainEntities.class);
        } catch (IOException ex) {
            Logger.getLogger(DomainEntities.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
        
    
    public DomainEntity findDomainInfoByName(String name) {
        if (entities==null){
            return null;
        }        
        for (DomainEntity d : entities) {
            if (d.getName().equals(name)) {
                return d;
            }
        }
        return null;
    }

    
}
