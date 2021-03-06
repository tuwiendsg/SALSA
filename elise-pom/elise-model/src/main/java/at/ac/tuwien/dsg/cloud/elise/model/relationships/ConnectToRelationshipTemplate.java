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

import at.ac.tuwien.dsg.cloud.elise.model.provider.ServiceTemplate;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * The connection between two ServiceUnitTemplate
 *
 * @author Duc-Hung LE
 */
@NodeEntity
public class ConnectToRelationshipTemplate {

    @GraphId
    private Long graphID;
    @StartNode
    protected ServiceTemplate from;
    @EndNode
    protected ServiceTemplate to;

    // currently we use the format : metric1=value1;metric2=value2;...
    protected String properties;

    public ConnectToRelationshipTemplate() {
    }

    public ConnectToRelationshipTemplate(ServiceTemplate from, ServiceTemplate to, String properties) {
        this.from = from;
        this.to = to;
        this.properties = properties;
    }

    public ServiceTemplate getFrom() {
        return from;
    }

    public void setFrom(ServiceTemplate from) {
        this.from = from;
    }

    public ServiceTemplate getTo() {
        return to;
    }

    public void setTo(ServiceTemplate to) {
        this.to = to;
    }

}
