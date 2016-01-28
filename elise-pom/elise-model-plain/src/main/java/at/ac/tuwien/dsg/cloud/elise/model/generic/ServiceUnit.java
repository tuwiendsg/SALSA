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
package at.ac.tuwien.dsg.cloud.elise.model.generic;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntities;
import java.util.HashSet;
import java.util.Set;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Duc-Hung Le
 *
 * This class contains generic information for different types of service units The other class should extend it, e.g. OfferedServiceUnit, ServiceUnitTemplate
 * and UnitInstance
 *
 */
public class ServiceUnit implements HasUniqueId {

    private Long graphID;

    /**
     * This is the global ID in ELISE, is assigned at the time a service unit is created. The particular DomainID is stored in the DomainEntity of the
     * domain-models
     */
    protected String id;

    protected String name;

    protected ServiceCategory category;

    // the concrete unit types to match with SALSA Instance type, e.g. os, software, tomcat, dockker
    protected String unitType;

    /**
     * Blind properties, just let it be custom
     */
    protected HashMap<String, String> extra;

    /**
     * Known information model. This contains a set of Domain info I (23/07/2015) don't know how to use neo4j to store a generic class. Then the domain model is
     * translate into json. Using the parseDomainInfo method to translate back The domainInfo class: DomainEntity.class
     */
    protected String domainInfo;

    /**
     * This contain other domain info, e.g information from other service like govops, mela, sybl The extendedDomainInfo class: DomainEntities.class
     */
    protected String extendedInfo;

    /**
     * All the action can be executed
     */
    protected Set<Capability> capabilities = new HashSet<>();

    public ServiceUnit() {
    }

    public ServiceUnit(String name, ServiceCategory category) {
        this.id = "TMP:" + UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
    }

    public ServiceUnit hasCapability(Capability primitive) {
        if (this.capabilities == null) {
            this.capabilities = new HashSet<>();
        }
        this.capabilities.add(primitive);
        return this;
    }

    public ServiceUnit hasCapabilities(Set<Capability> primitives) {
        if (this.capabilities == null) {
            this.capabilities = new HashSet<>();
        }
        this.capabilities.addAll(primitives);
        return this;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    @Override
    public String getId() {
        return id;
    }

    public Set<String> findAllCapabilities() {
        Set<String> names = new HashSet<>();
        for (Capability p : this.capabilities) {
            names.add(p.getName());
        }
        return names;
    }

    public ServiceUnit hasExtra(String key, String value) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.put(key, value);
        return this;
    }

    public DomainEntity parseDomainInfo() {
        if (domainInfo == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(domainInfo, DomainEntity.class);
        } catch (IOException ex) {
            Logger.getLogger("DomainModel").log(Level.SEVERE, "Cannot parse the domain info. Data to parse: {0}", domainInfo);
        }
        return null;
    }

    public DomainEntities parseExtendInfo() {
        if (extendedInfo == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(extendedInfo, DomainEntities.class);
        } catch (IOException ex) {
            Logger.getLogger("DomainModel").log(Level.SEVERE, "Cannot parse the domain info. Data to parse: {0}", extendedInfo);
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.id);
        hash = 13 * hash + Objects.hashCode(this.name);
        hash = 13 * hash + Objects.hashCode(this.category);
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
        final ServiceUnit other = (ServiceUnit) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.category != other.category) {
            return false;
        }
        return true;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            Logger.getLogger(ServiceUnit.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // other getters/setters
    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public void setCategory(ServiceCategory category) {
        this.category = category;
    }

    public HashMap<String, String> getExtra() {
        return extra;
    }

    public void setExtra(HashMap<String, String> extra) {
        this.extra = extra;
    }

    public String getDomainInfo() {
        return domainInfo;
    }

    public void setDomainInfo(String domainInfo) {
        this.domainInfo = domainInfo;
    }

    public String getExtendedInfo() {
        return extendedInfo;
    }

    public void setExtendedInfo(String extendedInfo) {
        this.extendedInfo = extendedInfo;
    }

    public Set<Capability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Set<Capability> capabilities) {
        this.capabilities = capabilities;
    }

}
