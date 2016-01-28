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

import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;

/**
 * The class represents template of a service unit, which initiate the state at deployment time
 *
 * @author Duc-Hung LE
 */
public class ServiceUnitTemplate extends ServiceEntity {

    protected Integer minInstances = 1;

    protected Integer maxInstances = 1;

    protected Boolean elasticUnit;

    protected Set<ServiceUnitTemplate> connectedTo = new HashSet<>();

    protected ServiceUnitTemplate hostedOn;

    protected Set<UnitInstance> instances = new HashSet<>();

    public ServiceUnitTemplate() {
        super();
    }

    public ServiceUnitTemplate(String id) {
        this.id = id;
    }

    public ServiceUnitTemplate(String id, String name, Integer minInstances,
            Integer maxInstances) {
        super(id, name);
        this.minInstances = minInstances;
        this.maxInstances = maxInstances;
    }

    public void addUnitInstance(UnitInstance instance) {
        if (instances == null) {
            instances = new HashSet<>();
        }
        instances.add(instance);
    }

    // GENERATED METHODS
    public Integer getMinInstances() {
        return minInstances;
    }

    public void setMinInstances(Integer minInstances) {
        this.minInstances = minInstances;
    }

    public Integer getMaxInstances() {
        return maxInstances;
    }

    public void setMaxInstances(Integer maxInstances) {
        this.maxInstances = maxInstances;
    }

    public Set<ServiceUnitTemplate> getConnectedTo() {
        return connectedTo;
    }

    public void setConnectedTo(Set<ServiceUnitTemplate> connectedTo) {
        this.connectedTo = connectedTo;
    }

    public ServiceUnitTemplate getHostedOn() {
        return hostedOn;
    }

    public void setHostedOn(ServiceUnitTemplate hostedOn) {
        this.hostedOn = hostedOn;
    }

    public Set<UnitInstance> getInstances() {
        return instances;
    }

    public void setInstances(Set<UnitInstance> instances) {
        this.instances = instances;
    }

    /**
     * Whether it is also a unit in sense of rSYBL and MELA
     *
     * @return
     */
    public Boolean getElasticUnit() {
        return elasticUnit;
    }

    public void setElasticUnit(Boolean elasticUnit) {
        this.elasticUnit = elasticUnit;
    }

}
