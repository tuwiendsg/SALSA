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

import java.util.HashSet;
import java.util.Set;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.ExtensibleModel;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 *
 * @author Duc-Hung Le
 *
 * This class contains generic information for different types of service units The other class should extend it, e.g. OfferedServiceUnit, ServiceUnitTemplate
 * and UnitInstance
 *
 */
@NodeEntity
public class ServiceUnit implements HasUniqueId {

    @GraphId
    private Long graphID;
    /**
     * This is the global ID in ELISE, is assigned at the time a service unit is created. The particular DomainID is stored in the DomainEntity of the
     * domain-models
     */
    protected String uuid;

    protected String name;

    protected ServiceCategory category;

    protected Set<Capability> capabilities;

    // the actual model of the service unit, using by SALSa
    @Fetch
    protected DomainEntity domain;

    // other extended model like contract, licensing, etc
    protected Set<ExtensibleModel> extra;

    public Capability getCapabilityByName(String name) {
        for (Capability capa : capabilities) {
            if (capa.getName().equals(name)) {
                return capa;
            }
        }
        return null;
    }

    public ServiceUnit() {
    }

    public ServiceUnit(String name, ServiceCategory category) {
        this.uuid = "TMP:" + UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
    }

    public ServiceUnit hasCapability(Capability primitive) {
        if (this.capabilities == null) {
            this.capabilities = new HashSet<>();
        }
        primitive.setUuid(this.getUuid() + "." + primitive.getName());
        this.capabilities.add(primitive);
        return this;
    }

    public ServiceUnit hasCapabilities(Set<Capability> primitives) {
        if (this.capabilities == null) {
            this.capabilities = new HashSet<>();
        }
        for (Capability capa : primitives) {
            capa.setUuid(this.getUuid() + "." + capa.getName());
        }
        this.capabilities.addAll(primitives);
        return this;
    }

    public ServiceUnit hasExtra(ExtensibleModel extra) {
        if (this.extra == null) {
            this.extra = new HashSet<>();
        }
        this.extra.add(extra);
        return this;
    }

    public Set<String> findAllCapabilities() {
        Set<String> names = new HashSet<>();
        for (Capability p : this.capabilities) {
            names.add(p.getName());
        }
        return names;
    }

//    public DomainEntity parseExtendInfo() {
//        if (domain == null) {
//            return null;
//        }
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return mapper.readValue(domain, domainClazz);
//        } catch (IOException ex) {
//            Logger.getLogger("DomainModel").log(Level.SEVERE, "Cannot parse the domain info. Data to parse: {0}", domain);
//        }
//        return null;
//    }
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.uuid);
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
        if (!Objects.equals(this.uuid, other.uuid)) {
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
    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
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

    public Set<Capability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Set<Capability> capabilities) {
        this.capabilities = capabilities;
    }

    public DomainEntity getDomain() {
        return domain;
    }

    public void setDomain(DomainEntity domain) {
        this.domain = domain;
    }

    public Set<ExtensibleModel> getExtra() {
        return extra;
    }

    public void setExtra(Set<ExtensibleModel> extra) {
        this.extra = extra;
    }

}
