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
package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "ServiceUnit")
@XmlSeeAlso({
    ServiceTopology.class,
    ServiceInstance.class
})
public class ServiceUnit extends SalsaEntity {

    @XmlElement(name = "Replica")
    List<ServiceInstance> repLst = new CopyOnWriteArrayList<>();

    @XmlElementWrapper(name = "Artifacts")
    @XmlElement(name = "Artifacts")
    List<Artifacts> artifacts = new CopyOnWriteArrayList<>();

    @XmlAttribute(name = "type")
    String type;

    @XmlAttribute(name = "artifactType")
    String artifactType;

    @XmlAttribute(name = "hostedId")
    String hostedId = "";

    @XmlAttribute(name = "capabilityVars")
    List<String> capabilityVars = new ArrayList<>();

    @XmlAttribute(name = "connecttoId")
    List<String> connecttoId = new ArrayList<>();

    @XmlAttribute(name = "idCounter")
    int idCounter = 0;

    @XmlAttribute(name = "min")
    int min = 1;

    @XmlAttribute(name = "max")
    int max = 1;

    @XmlAttribute(name = "reference")
    String reference;
    
    @XmlElement(name = "properties")
    protected ServiceUnit.Properties properties;
    
    
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"any"})
    public static class Properties {

        @XmlAnyElement(lax = true)
        protected Object any;

        public Object getAny() {
            return any;
        }

        public void setAny(Object value) {
            this.any = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "DeploymentArtifact")
    public static class Artifacts {

        @XmlAttribute(name = "name")
        String name;
        @XmlAttribute(name = "type")
        String type;
        @XmlAttribute(name = "reference")
        String reference;

        public Artifacts() {
        }

        public Artifacts(String name, String type, String reference) {
            this.name = name;
            this.type = type;
            this.reference = reference;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getReference() {
            return reference;
        }
    }

    public ServiceUnit.Properties getProperties() {
        return properties;
    }

    public void setProperties(ServiceUnit.Properties properties) {
        this.properties = properties;
    }
    
    
    public void addInstance(ServiceInstance instance) {
        repLst.add(instance);
    }

    public ServiceInstance getInstanceById(int instance) {
        for (ServiceInstance node : repLst) {
            if (node.getInstanceId() == instance) {
                return node;
            }
        }
        return null;
    }

    public void addArtifact(String name, String type, String reference) {
        if (this.artifacts == null) {
            this.artifacts = new ArrayList<>();
        }
        this.artifacts.add(new Artifacts(name, type, reference));
    }

    public List<Artifacts> getArtifacts() {
        return artifacts;
    }

    public ServiceUnit(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public ServiceUnit() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getInstanceNumber() {
        return repLst.size();
    }

    public int getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    public String getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(String artifactType) {
        this.artifactType = artifactType;
    }

    public String getHostedId() {
        return hostedId;
    }

    public void setHostedId(String hostedId) {
        this.hostedId = hostedId;
    }

    public List<String> getConnecttoId() {
        return connecttoId;
    }

    public List<String> getCapabilityVars() {
        return capabilityVars;
    }

    public void setConnecttoId(List<String> connecttoId) {
        this.connecttoId = connecttoId;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<ServiceInstance> getInstanceByState(SalsaEntityState state) {
        List<ServiceInstance> lst = new ArrayList<>();
        for (ServiceInstance rep : repLst) {
            if (rep.getState() == state) {
                lst.add(rep);
            }
        }
        return lst;
    }

    public int getInstanceNumberByState(SalsaEntityState state) {
        int counter = 0;
        for (ServiceInstance rep : repLst) {
            if (rep.getState() == state) {
                counter++;
            }
        }
        return counter;
    }

    public List<ServiceInstance> getInstancesList() {
        return repLst;
    }

    public List<ServiceInstance> getInstanceHostOn(int instanceIdOfHoster) {
        List<ServiceInstance> newLst = new ArrayList<>();
        for (ServiceInstance instance : repLst) {
            if (instance.getHostedId_Integer() == instanceIdOfHoster) {
                newLst.add(instance);
            }
        }
        return newLst;
    }

}
