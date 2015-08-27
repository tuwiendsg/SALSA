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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * One property which is used for the identification
 * @author Duc-Hung LE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class IdentificationItem {

    protected String name;
    protected String value;
    protected EnvIDType type;
    protected EnvIDScope scope;

    @XmlType
    public enum EnvIDType {
        IPv4,
        UUID,
        DomainID
    }
    
    @XmlType
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
