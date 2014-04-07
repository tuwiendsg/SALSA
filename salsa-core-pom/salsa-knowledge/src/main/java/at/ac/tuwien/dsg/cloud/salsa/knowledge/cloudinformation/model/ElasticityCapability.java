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
package at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ElasticityCapability")
public class ElasticityCapability extends Entity {

    //entity for which the characteristic is defined (Resource or Quality)
    //this is abstract: Example: Computing
    @XmlElement(name = "CapabilityTarget", required = false)
    private List<Entity> capabilityTargets;
    //this si concrete
    //Example: Computing x64, and Computing x86
//    private List<Entity> capabilityOptions;
    @XmlAttribute(name = "type", required = true)
    private String type;
    @XmlAttribute(name = "elasticityPhase", required = true)
    private String phase = Phase.INSTANTIATION_TIME;

    {
//        capabilityOptions = new ArrayList<Entity>();
        capabilityTargets = new ArrayList<Entity>();
    }

    public ElasticityCapability() {
    }

    public ElasticityCapability(String name) {
        super(name);
    }

    public ElasticityCapability(Entity characteristicTarget, String name) {
        super(name);
        this.capabilityTargets.add(characteristicTarget);
    }

    public ElasticityCapability(Entity characteristicTarget) {
        this.capabilityTargets.add(characteristicTarget);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void setCapabilityTargets(List<Entity> capabilityTargets) {
        this.capabilityTargets = capabilityTargets;
    }

    public List<Entity> getCapabilityTargets() {
        return capabilityTargets;
    }

    public void addCapabilityTarget(Entity e) {
        capabilityTargets.add(e);
    }

    public void removeCapabilityTarget(Entity e) {
        capabilityTargets.remove(e);
    }

    public void addCapabilityTargets(List<Entity> e) {
        capabilityTargets.addAll(e);
    }

    public void removeCapabilityTargets(List<Entity> e) {
        capabilityTargets.removeAll(e);
    }

    /**
     * assumes that the ElasticityCapability hlds entities of the same type
     *
     * @return
     */
    public Class getTargetType() {
        if (capabilityTargets.isEmpty()) {
            return null;
        } else {
            Entity target = capabilityTargets.get(0);
            return target.getClass();
        }
    }

//    public List<Entity> getCapabilityOptions() {
//        return capabilityOptions;
//    }
//    
//    public void addCapabilityOption(Entity e){
//        capabilityOptions.add(e);
//    }
//    
//    public void removeCapabilityOption(Entity e){
//        capabilityOptions.remove(e);
//    }
//    
//    
//    public void addCapabilityOptions(List<Entity> e){
//        capabilityOptions.addAll(e);
//    }
//    
//    public void removeCapabilityOptions(List<Entity> e){
//        capabilityOptions.removeAll(e);
//    }
//    
//    
    @Override
    public String toString() {
        return "ElasticityCapability{" + "name=" + name + "type=" + type + ", phase=" + phase + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
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
        final ElasticityCapability other = (ElasticityCapability) obj;
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public interface Type {

        String MANDATORY_ASSOCIATION = "MandatoryAssociation";
        String OPTIONAL_ASSOCIATION = "OptionalAssociation";
    }

    public interface Phase {

        String RUN_TIME = "ExecutionTime";
        String INSTANTIATION_TIME = "InstantiationTime";
    }
}
