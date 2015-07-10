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
package at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts;

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
@XmlRootElement(name = "CloudProvider")
public class CloudProvider extends Unit {

    @XmlElement(name = "ServiceUnit", required = false)
    private List<CloudOfferedService> serviceUnits;
    @XmlAttribute(name = "type", required = false)
    private String type = Type.IAAS;

    {
        serviceUnits = new ArrayList<CloudOfferedService>();
    }

    public CloudProvider() {
    }

    public CloudProvider(String name) {
        super(name);
    }

    public CloudProvider(String name, String type) {
        super(name);
        this.type = type;
    }

    public List<CloudOfferedService> getCloudOfferedServices() {
        return serviceUnits;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addCloudOfferedService(CloudOfferedService u) {
        this.serviceUnits.add(u);
    }

    public void removeCloudOfferedService(CloudOfferedService u) {
        this.serviceUnits.remove(u);
    }

    public void setCloudOfferedServices(List<CloudOfferedService> serviceUnits) {
        this.serviceUnits = serviceUnits;
    }

    @Override
    public String toString() {
        return "CloudProvider{" + "name=" + name + ", type=" + type + '}';
    }

    public interface Type {

        public static final String IAAS = "IAAS";
        public static final String PAAS = "PAAS";
    }

    public CloudProvider withCloudOfferedServices(final List<CloudOfferedService> serviceUnits) {
        this.serviceUnits = serviceUnits;
        return this;
    }

    public CloudProvider withCloudOfferedService(CloudOfferedService serviceUnit) {
        this.serviceUnits.add(serviceUnit);
        return this;
    }

    public CloudProvider withType(final String type) {
        this.type = type;
        return this;
    }

}
