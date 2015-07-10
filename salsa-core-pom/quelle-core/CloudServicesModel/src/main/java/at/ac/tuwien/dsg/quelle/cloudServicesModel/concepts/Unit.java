/**
 * Copyright 2013 Technische Universitaet Wien (TUW), Distributed Systems Group
 * E184
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts;

import java.io.Serializable;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Entity")
public class Unit implements Serializable {

    //used for neo4J storage
    @XmlAttribute(name = "id")
    protected Long id;

    @XmlAttribute(name = "uuid" )
    protected UUID uuid;

    @XmlAttribute(name = "name")
    protected String name;

    public final Long getId() {
        return id;
    }

    public final void setId(Long id) {
        this.id = id;
    }

    public Unit() {
    }

    public Unit(Long id) {
        this.id = id;
    }

    public Unit(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    public Unit(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Unit(Long id, UUID uuid, String name) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
    }

    public Unit(Long id, String name) {
        this.id = id;
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    public final UUID getUuid() {
        return uuid;
    }

    public final void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public Unit clone() {
        return new Unit(id, name);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final Unit other = (Unit) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public final Unit withId(final Long id) {
        this.id = id;
        return this;
    }

    public Unit withUuid(final UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public Unit withName(final String name) {
        this.name = name;
        return this;
    }
}
