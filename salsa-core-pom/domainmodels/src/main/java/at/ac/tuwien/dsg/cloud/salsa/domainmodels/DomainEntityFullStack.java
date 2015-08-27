/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.domainmodels;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This class manages set of DomainEntity, which used for collecting both application and infrastructure information
 * @author Duc-Hung LE
 */
public class DomainEntityFullStack {
    protected List<DomainEntity> fullStack;

    public DomainEntityFullStack() {
    }
    
    public DomainEntityFullStack(List<DomainEntity> list){
        this.fullStack = list;
    }
    
    public DomainEntityFullStack hasDomainEntity(DomainEntity d){
        if (this.fullStack == null){
            this.fullStack = new ArrayList<>();
        }
        this.fullStack.add(d);
        return this;
    }
    
    public DomainEntity findDomainEntityByCategory(ServiceCategory category){
        if (fullStack == null){
            return null;
        }
        for(DomainEntity e: fullStack){
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
            Logger.getLogger(DomainEntityFullStack.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static DomainEntityFullStack fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);

        try {
            return mapper.readValue(json, DomainEntityFullStack.class);
        } catch (IOException ex) {
            Logger.getLogger(DomainEntityFullStack.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public DomainEntity findDomainInfoByName(String name) {
        if (fullStack==null){
            return null;
        }        
        for (DomainEntity d : fullStack) {
            if (d.getName().equals(name)) {
                return d;
            }
        }
        return null;
    }

    
}
