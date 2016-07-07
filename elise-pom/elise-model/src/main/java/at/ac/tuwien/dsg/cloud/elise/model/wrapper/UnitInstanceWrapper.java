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
package at.ac.tuwien.dsg.cloud.elise.model.wrapper;

import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * For transferring collection data
 * @author Duc-Hung LE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class UnitInstanceWrapper {

    protected Set<UnitInstance> unitInstances = new HashSet<>();

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
