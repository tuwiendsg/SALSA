/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.salsa.model.idmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The LocalIdentification is any kind of ID, assigned by a particular
 * management service
 *
 * @author Duc-Hung LE
 */
public class LocalIdentification {

    protected String assignedBy = null;

    protected String category;

    protected Map<String, String> idItems = new HashMap<>();

    //Set<IdentificationItem> items = new HashSet<>();
    public LocalIdentification(String category, String assignedBy) {
        this.category = category;
        this.assignedBy = assignedBy;
    }

    public LocalIdentification() {
    }

    public LocalIdentification hasIdentification(String key, String value) {
        this.idItems.put(key, value);
        return this;
    }

    public LocalIdentification assignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public Map<String, String> getIdItems() {
        return idItems;
    }

    /**
     * This equal function compare 2 identification of 2 information pieces of 2
     * services If they are equals, that mean 2 pieces of information belong to
     * the same service
     *
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
//        if (this.category != other.category) {
//            System.out.println("Comparing localIDs: fail as categories are different");
//            return false;
//        }
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
                    System.out.println("Comparing localIDs: good! having same key/value: " + key + ":" + this.idItems.get(key));
                    return true;
                }
            }
            System.out.println("Comparing localIDs: no insection");
            return false;
        }
    }

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

}
