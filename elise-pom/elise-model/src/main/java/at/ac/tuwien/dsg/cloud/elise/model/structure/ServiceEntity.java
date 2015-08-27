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
package at.ac.tuwien.dsg.cloud.elise.model.structure;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.neo4j.annotation.NodeEntity;
import at.ac.tuwien.dsg.cloud.elise.model.generic.ServiceUnit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * The service entity, which is generic for different levels of the the structure
 * @author Duc-Hung LE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
@NodeEntity
public abstract class ServiceEntity extends ServiceUnit {

    protected Set<String> syblDirectives = new HashSet<>();

    public ServiceEntity() {
    }

    public ServiceEntity(String id) {
        this.id = id;
    }

    public ServiceEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public ServiceEntity hasDirective(String directive) {
        if (syblDirectives == null) {
            syblDirectives = new HashSet<>();
        }
        syblDirectives.add(directive);
        return this;
    }

    public Set<String> getSyblDirectives() {
        return syblDirectives;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ServiceEntity other = (ServiceEntity) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
