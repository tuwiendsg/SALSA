/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Save the list of LocalIdentification and the global ID.
 *
 * @author hungld
 */
public class GlobalIdentification {

    String uuid;

    ServiceCategory category;

    // note: cannot use Set. These LocalIdentifications are all equal due to the hashcode and equal functions.
    List<LocalIdentification> localIDs = new ArrayList<>();

    public GlobalIdentification() {
    }

    public GlobalIdentification(ServiceCategory category) {
        this.category = category;
    }

    public GlobalIdentification hasLocalIdentification(LocalIdentification local) {
        this.localIDs.add(local);
        return this;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public List<LocalIdentification> getLocalIDs() {
        return localIDs;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    /**
     * Try to add a local identification
     *
     * @param local
     * @return true if added, false if cannot
     */
    public boolean addLocalIdentification(LocalIdentification local) {
        System.out.println("Adding local assigned by: " + local.getAssignedBy() + " to the global " + this.getUuid());
        if (localIDs.isEmpty()) { 
            System.out.println("Empty list of localID then add");
            localIDs.add(local);
            return true;
        } else {
            System.out.println("Comparing localIDs");
            for (LocalIdentification li : localIDs) {
                System.out.println("Comparing with " + li.getAssignedBy() +": " +li.toJson());
                if (local.equals(li)) {
                    System.out.println("Equal, merge!");
                    if (!local.getAssignedBy().equals(li.assignedBy)){
                        System.out.println("Do not have yet, ADD!");
                        localIDs.add(local);
                    } 
                    return true;
                }
            }
            System.out.println("The local id is not added to the global ID");
            return false;
        }
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
     public static GlobalIdentification fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, GlobalIdentification.class);
        } catch (IOException ex) {
            System.out.println("Cannot convert the GlobalIdentification from the json: " + json);
            ex.printStackTrace();
            return null;
        }
    }

    
}
