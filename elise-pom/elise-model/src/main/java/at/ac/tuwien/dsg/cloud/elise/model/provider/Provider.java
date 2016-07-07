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
import at.ac.tuwien.dsg.cloud.elise.model.generic.HasUniqueId;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Describe a provider, which include multiples Offered Service Unit
 *
 * @author Duc-Hung LE
 */
@NodeEntity
public class Provider implements HasUniqueId {

    @GraphId
    private Long graphID;

    protected String uuid;
    protected String name;
    protected ProviderType providerType = ProviderType.IAAS;
    protected Set<ServiceTemplate> offering = new HashSet();

    public static enum ProviderType {
        IAAS, PAAS, USERDEFINED;
    }

    public Provider() {
    }

    public Provider(String name) {
        this.name = name;
        this.uuid = ("Provider:" + name);
    }

    public Provider(String name, ProviderType type) {
        this.name = name;
        this.providerType = type;
        this.uuid = ("Provider:" + name);
    }

    public Provider hasOfferedServiceUnit(ServiceTemplate offered) {
        addOfferedServiceUnit(offered);
        return this;
    }

    public void addOfferedServiceUnit(ServiceTemplate offered) {
        offered.setUuid(getUuid() + "." + offered.getName());
        this.offering.add(offered);
    }

    public ProviderType getProviderType() {
        return this.providerType;
    }

    public String getName() {
        return name;
    }

    public Set<ServiceTemplate> getOffering() {
        return this.offering;
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    public void setId(String id) {
        this.uuid = id;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public void setOffering(Set<ServiceTemplate> offering) {
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
