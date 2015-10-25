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
package at.ac.tuwien.dsg.cloud.elise.model.runtime;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Save the list of LocalIdentification and the global ID for a particular unit instance
 *
 * @author Duc-Hung LE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class GlobalIdentification {

    protected String uuid;

    protected ServiceCategory category;

    // note: cannot use Set. These LocalIdentifications are all equal due to the hashcode and equal functions.
    protected ArrayList<LocalIdentification> localIDs = new ArrayList<>();

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

    public ArrayList<LocalIdentification> getLocalIDs() {
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
                System.out.println("Comparing with " + li.getAssignedBy() + ": " + li.toJson());
                if (local.equals(li)) {
                    System.out.println("Equal, merge!");
                    if (!local.getAssignedBy().equals(li.assignedBy)) {
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
        if (json == null || json.isEmpty()) {
            return null;
        }

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
