/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hungld
 */
public class IdentificationDB {
    Set<GlobalIdentification> identifications = new HashSet<>();


    public IdentificationDB() {
    }

    public IdentificationDB hasIdentification(GlobalIdentification id){
        this.identifications.add(id);
        return this;
    }
    
    public Set<GlobalIdentification> getIdentifications() {
        return identifications;
    }
    
    
    
}
