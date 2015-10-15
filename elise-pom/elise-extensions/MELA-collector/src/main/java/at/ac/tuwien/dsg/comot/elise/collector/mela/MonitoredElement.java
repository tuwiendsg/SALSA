/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group
 * E184
 *
 * This work was partially supported by the European Commission in terms of the
 * CELAR FP7 project (FP7-ICT-2011-8 \#317790)
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
package at.ac.tuwien.dsg.comot.elise.collector.mela;

import java.io.Serializable;
import javax.xml.bind.annotation.*;
import java.util.Collection;

/**
 *
 * Author: Daniel Moldovan E-Mail: d.moldovan@dsg.tuwien.ac.at *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MonitoredElement")
public class MonitoredElement {

    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "level", required = true)
    private MonitoredElementLevel level;

    @XmlElement(name = "MonitoredElement", required = false)
    private Collection<MonitoredElement> containedElements;

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "MonitoredElementLevel")
    @XmlEnum
    public enum MonitoredElementLevel implements Serializable {

        @XmlEnumValue("SERVICE")
        SERVICE,
        @XmlEnumValue("SERVICE_TOPOLOGY")
        SERVICE_TOPOLOGY,
        @XmlEnumValue("SERVICE_UNIT")
        SERVICE_UNIT,
        //rename VM to Service Unit Instance
        @XmlEnumValue("VM")
        VM,
        @XmlEnumValue("CLOUD_OFFERED_SERVICE")
        CLOUD_OFFERED_SERVICE,
        @XmlEnumValue("VIRTUAL_CLUSTER")
        VIRTUAL_CLUSTER
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

    public MonitoredElementLevel getLevel() {
        return level;
    }

    public void setLevel(MonitoredElementLevel level) {
        this.level = level;
    }

    public Collection<MonitoredElement> getContainedElements() {
        return containedElements;
    }

    public void setContainedElements(Collection<MonitoredElement> containedElements) {
        this.containedElements = containedElements;
    }

    
 


}