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
package at.ac.tuwien.dsg.cloud.elise.model.relationships;

import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * Define a UnitInstance is hosted by which
 *
 * @author Duc-Hung LE
 */
@RelationshipEntity
public class HostOnRelationshipInstance {

    @GraphId
    private Long graphID;
    @StartNode
    protected UnitInstance from;
    @EndNode
    protected UnitInstance to;

    public HostOnRelationshipInstance() {
    }

    public HostOnRelationshipInstance(UnitInstance from, UnitInstance to) {
        this.from = from;
        this.to = to;
    }

    public UnitInstance getFrom() {
        return from;
    }

    public void setFrom(UnitInstance from) {
        this.from = from;
    }

    public UnitInstance getTo() {
        return to;
    }

    public void setTo(UnitInstance to) {
        this.to = to;
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
