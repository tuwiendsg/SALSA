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

import at.ac.tuwien.dsg.cloud.elise.model.generic.ServiceUnit;
import at.ac.tuwien.dsg.cloud.elise.model.provider.ServiceTemplate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * A topology group multiple service unit templates and/or topologies
 *
 * @author Duc-Hung LE
 */
@NodeEntity
public class ServiceTopology extends ServiceUnit {


    protected Set<ServiceTemplate> serviceUnits = new HashSet<>();
    protected Set<ServiceTopology> serviceTopologies = new HashSet<>();

    public ServiceTopology() {
    }

    public ServiceTopology(
            Set<ServiceTemplate> serviceUnits,
            Set<ServiceTopology> serviceTopologies) {
        this.serviceUnits = serviceUnits;
        this.serviceTopologies = serviceTopologies;
    }

    public void addServiceUnit(ServiceTemplate serviceUnit) {
        if (serviceUnits == null) {
            serviceUnits = new HashSet<>();
        }
        serviceUnits.add(serviceUnit);
    }

    public void addTopology(ServiceTopology serviceTopology) {
        if (serviceTopologies == null) {
            serviceTopologies = new HashSet<>();
        }
        serviceTopologies.add(serviceTopology);
    }

    public List<ServiceTopology> getServiceTopologiesList() {
        return new ArrayList<>(serviceTopologies);
    }

    public List<ServiceTemplate> getServiceUnitsList() {
        return new ArrayList<>(serviceUnits);
    }

    public List<ServiceTemplate> getServiceUnitList() {
        return new ArrayList<>(serviceUnits);
    }

    // GENERATED METHODS
    public Set<ServiceTopology> getServiceTopologies() {
        return serviceTopologies;
    }

    public Set<ServiceTemplate> getServiceUnits() {
        return serviceUnits;
    }

    public void setServiceUnits(Set<ServiceTemplate> serviceUnits) {
        this.serviceUnits = serviceUnits;
    }

    public void setServiceTopologies(Set<ServiceTopology> serviceTopologies) {
        this.serviceTopologies = serviceTopologies;
    }

}
