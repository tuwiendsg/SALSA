/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification;

import java.util.Objects;

/**
 *
 * @author hungld
 */
public class IdentificationItem {

    String name;
    String value;
    EnvIDType type;
    EnvIDScope scope;

    public enum EnvIDType {
        IPv4,
        UUID,
        DomainID
    }
    
    public enum EnvIDScope {
        DOMAIN, CONTEXT
    }

    public IdentificationItem(String name, String value, EnvIDType type, EnvIDScope scope) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.scope = scope;
    }

    public IdentificationItem() {
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public EnvIDType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!o.getClass().equals(o.getClass())) {
            return false;
        }

        IdentificationItem other = (IdentificationItem) o;
        return this.getType().equals(other.getType())
                && this.getName().equals(other.getName())
                && this.getValue().equals(other.getValue());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.value);
        hash = 67 * hash + Objects.hashCode(this.type);
        return hash;
    }

}
