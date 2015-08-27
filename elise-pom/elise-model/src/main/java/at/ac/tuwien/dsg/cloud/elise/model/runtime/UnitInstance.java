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

import at.ac.tuwien.dsg.cloud.elise.model.generic.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Metric;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 * The instance managed by different cloud and management services
 * @author Duc-Hung LE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@NodeEntity
public class UnitInstance extends ServiceUnit {

    protected String identification;
    
    // the configuration state. The state of the unit is in the domainInfo (see generic ServiceUnit.class)
    protected State state;
    
    @RelatedTo(type = "HostOnRelationshipInstance", direction = Direction.OUTGOING)
    protected UnitInstance hostedOn;
    
    @RelatedTo(type = "ConnectToRelationshipInstance", direction = Direction.OUTGOING)
    protected Set<UnitInstance> connectTo; 

    public UnitInstance() {
    }

    public UnitInstance(String name, ServiceCategory category, State state) {
        super(name, category);
        this.state = state;
    }
    
    public void hostedOnInstance(UnitInstance hostedInst){
        this.hostedOn = hostedInst;
    }
    
    public void connectToInstance(UnitInstance connectedToInst){
        if (this.connectTo == null){
            this.connectTo = new HashSet<>();
        }
        this.connectTo.add(connectedToInst);
    }

    // GENERATED METHODS
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public Set<Metric> findAllMetricValues(){
        return new HashSet<>();
    }

    @Override
    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }    
    
    public void mergeWith(UnitInstance otherInstance){
        this.getCapabilities().addAll(otherInstance.getCapabilities());
        this.setState(otherInstance.getState());
        // TODO: do actual merging
      //  this.getDomainInfo().addAll(otherInstance.getDomainInfo());        
    }
    

}
