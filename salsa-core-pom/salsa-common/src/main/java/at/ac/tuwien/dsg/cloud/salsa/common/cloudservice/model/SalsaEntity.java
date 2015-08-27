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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import java.util.UUID;

/**
 * This class is abstract for CloudService, ComponentTopology and Component These can be map into TOSCA object in order: Definition, ServiceTemplate,
 * NodeTemplate
 *
 * @author Duc-Hung Le
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlSeeAlso({
    CloudService.class,
    ServiceTopology.class,
    ServiceUnit.class
})
public class SalsaEntity {
    @XmlAttribute(name = "id")
    String id;
    @XmlAttribute(name = "uuid")
    UUID uuid = UUID.randomUUID();
    @XmlAttribute(name = "name")
    String name;
    @XmlAttribute(name = "state")
    SalsaEntityState state = SalsaEntityState.UNDEPLOYED;
    @XmlElement(name = "monitoring")
    SalsaEntity.Monitoring monitoring;

    // Queue of Actions is used for deployment process
    @XmlElement(name = "ActionsQueue")
    List<String> actionQueue = new ArrayList<>();

    // List of Primitives is used for description of actions
    @XmlElementWrapper(name = "Primitives")
    @XmlElement(name = "Primitive")
    List<PrimitiveOperation> primitive;

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "source")
    public static class Monitoring {

        @XmlAttribute(name = "name")
        protected String source;

        @XmlAnyElement(lax = true)
        protected Object any;

        public Object getAny() {
            return any;
        }

        public void setAny(Object value) {
            this.any = value;
        }
    }

    public PrimitiveOperation getPrimitiveByName(String name) {
        if (primitive != null) {
            for (PrimitiveOperation po : primitive) {
                if (po.name.equals(name)) {
                    return po;
                }
            }
        }
        return null;
    }

    public SalsaEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SalsaEntityState getState() {
        return state;
    }

    public void setState(SalsaEntityState state) {
        this.state = state;
    }

    public UUID getUuid() {
        return uuid;
    }
    
    public SalsaEntity.Monitoring getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(Object monitoring) {
        this.monitoring = new Monitoring();
        this.monitoring.setAny(monitoring);
    }

    public class CDATAAdapter extends XmlAdapter<String, String> {

        @Override
        public String marshal(String v) throws Exception {
            return "<![CDATA[" + v + "]]>";
        }

        @Override
        public String unmarshal(String v) throws Exception {
            return v.trim();
        }
    }

    public void queueAction(String capaName) {
        actionQueue.add(capaName);
    }

    public void unqueueAction() {
        if (!actionQueue.isEmpty()) {
            actionQueue.remove(0);
        }
    }

    public String pollAction() {
        if (actionQueue != null && actionQueue.size() > 0) {
            return actionQueue.get(0);
        }
        return null;
    }

    public void addPrimitiveOperation(PrimitiveOperation opp) {
        if (this.primitive == null) {
            this.primitive = new ArrayList<PrimitiveOperation>();
        }
        this.primitive.add(opp);
    }

    public List<PrimitiveOperation> getPrimitive() {
        return primitive;
    }

    public void setPrimitive(List<PrimitiveOperation> primitive) {
        this.primitive = primitive;
    }

}
