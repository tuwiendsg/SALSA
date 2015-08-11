/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.provider;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import at.ac.tuwien.dsg.cloud.elise.model.HasUniqueId;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.neo4j.annotation.GraphId;

/**
 *
 * @author hungld
 */
@NodeEntity
public class Provider implements HasUniqueId {

    @GraphId
    Long graphID;
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

    public Set<OfferedServiceUnit> getOffering() {
        return this.offering;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
