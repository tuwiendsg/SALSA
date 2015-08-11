/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author hungld
 */
public class LocalIdentification {

    String assignedBy = null;

    ServiceCategory category;

    Map<String, String> idItems = new HashMap<>();

    //Set<IdentificationItem> items = new HashSet<>();
    public LocalIdentification(ServiceCategory category, String assignedBy) {
        this.category = category;
        this.assignedBy = assignedBy;
    }

    public LocalIdentification() {
    }

    public LocalIdentification hasIdentification(String key, String value) {
        this.idItems.put(key, value);
        return this;
    }

//    public LocalIdentification hasIdentificationItem(IdentificationItem item) {
//        this.items.add(item);
//        return this;
//    }
//    
//     public LocalIdentification hasIdentificationItems(Set<IdentificationItem> item) {
//        this.items.addAll(item);
//        return this;
//    }
    public LocalIdentification assignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
        return this;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public Map<String, String> getIdItems() {
        return idItems;
    }
    
    

    /**
     * This equal function compare 2 identification of 2 information pieces of 2 services If they are equals, that mean 2 pieces of information belong to the
     * same service
     *
     * @param o
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.assignedBy);
        hash = 23 * hash + Objects.hashCode(this.category);
        hash = 23 * hash + Objects.hashCode(this.idItems);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LocalIdentification other = (LocalIdentification) obj;

        // if category are different, they are different !
        if (this.category != other.category) {
            System.out.println("Comparing localIDs: fail as categories are different");
            return false;
        }

        if (this.assignedBy.equals(other.assignedBy)) {
            System.out.println("Comparing localIDs: assigned by the same, compare the whole 2 maps");
            // compare 2 map completely
            return this.idItems.equals(other.idItems);
        } else {
            // compare insection
            System.out.println("Comparing localIDs: comparing insection");
            for (String key : this.idItems.keySet()) {
                if (other.idItems.containsKey(key)
                        && this.idItems.get(key).equals(other.idItems.get(key))) {
                    System.out.println("Comparing localIDs: good! having same key/value: " + key +":"+this.idItems.get(key));
                    return true;
                }
            }
            System.out.println("Comparing localIDs: no insection");
            return false;
        }
    }

//    public Set<IdentificationItem> getItems() {
//        return items;
//    }
//
//    public void setItems(Set<IdentificationItem> items) {
//        this.items = items;
//    }
    public static LocalIdentification fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, LocalIdentification.class);
        } catch (IOException ex) {
            System.out.println("Cannot convert the ServiceIdentification from the json: " + json);
            ex.printStackTrace();
            return null;
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

//    @Override
//    public boolean equals(Object o) {
//        if (o == null) {
//            return false;
//        }
//        if (!o.getClass().equals(o.getClass())) {
//            return false;
//        }
//        LocalIdentification other = (LocalIdentification) o;
//
//        if (!this.category.equals(other.getCategory())) {
//            return false;
//        }
//        
//        if (this.assignedBy != null && this.assignedBy.equals(other.assignedBy)){
//            return this.getItems().containsAll(other.getItems());
//        }
//
//        Set<IdentificationItem> intersection = new HashSet<>(this.items); // use the copy constructor
//        intersection.retainAll(other.getItems());
//        return !intersection.isEmpty();
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 37 * hash + Objects.hashCode(this.category);
//        hash = 37 * hash + Objects.hashCode(this.items);
//        return hash;
//    }
}
