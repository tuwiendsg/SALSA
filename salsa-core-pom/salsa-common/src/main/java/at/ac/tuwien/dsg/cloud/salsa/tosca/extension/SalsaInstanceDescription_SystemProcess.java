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
package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;

/**
 *
 * This class acts as a container for all the information of Salsa system process instances
 *
 * @author Duc-Hung Le TODO: Unified instance type. Currently: use String.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "SalsaInstanceDescription_SystemProcess")
public class SalsaInstanceDescription_SystemProcess {

    @XmlElement(name = "pid")
    private String pid;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "state")
    private String state;

    public SalsaInstanceDescription_SystemProcess() {
    }

    public SalsaInstanceDescription_SystemProcess(String process_id) {
        this.pid = process_id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateFromMappingProperties(SalsaMappingProperties maps) {
        for (SalsaMappingProperty map : maps.getProperties()) {
            if (map.getType().equals(SalsaEntityType.SERVICE.getEntityTypeString())) {
                this.pid = map.get("pid");
                this.state = map.get("state");
                this.name = map.get("name");
            }
        }
    }

    public Map<String, String> exportToMap() {
        Map<String, String> resMap = new HashMap<String, String>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("pid", this.pid);
        map.put("state", this.state);
        map.put("name", this.name);

        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator.next();
            if (mapEntry.getValue() != null) {
                resMap.put(mapEntry.getKey(), mapEntry.getValue());
            }
        }
        return resMap;
    }
}
