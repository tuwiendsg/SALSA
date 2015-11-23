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
package at.ac.tuwien.dsg.cloud.elise.model.provider;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import at.ac.tuwien.dsg.cloud.elise.model.generic.HasUniqueId;
import java.io.IOException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.neo4j.annotation.GraphId;

/**
 * Describe a provider, which include multiples Offered Service Unit
 *
 * @author Duc-Hung LE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@NodeEntity
public class Provider implements HasUniqueId {

    @GraphId
    private Long graphID;
    @Indexed(unique = true)
    protected String id;
    protected String name;
    protected ProviderType providerType = ProviderType.IAAS;
    protected Set<OfferedServiceUnit> offering = new HashSet();

    public Provider() {
    }

    public Provider(String name) {
        this.name = name;
        this.id = ("Provider:" + name);
    }

    public Provider(String name, ProviderType type) {
        this.name = name;
        this.providerType = type;
        this.id = name;
    }

    public Provider hasOfferedServiceUnit(OfferedServiceUnit offered) {
        addOfferedServiceUnit(offered);
        return this;
    }

    public void addOfferedServiceUnit(OfferedServiceUnit offered) {
        offered.setId(getId() + "." + offered.getName());
        this.offering.add(offered);
    }

    public ProviderType getProviderType() {
        return this.providerType;
    }

    public String getName() {
        return name;
    }
    
    public Set<OfferedServiceUnit> getOffering() {
        return this.offering;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlType
    public static enum ProviderType {

        IAAS, PAAS, CUSTOM;

        private ProviderType() {
        }
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public void setOffering(Set<OfferedServiceUnit> offering) {
        this.offering = offering;
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
