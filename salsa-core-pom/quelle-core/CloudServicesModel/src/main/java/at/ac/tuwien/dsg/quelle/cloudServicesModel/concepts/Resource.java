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

import at.ac.tuwien.dsg.quelle.cloudServicesModel.util.conversions.helper.PropertiesAdapter;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Metric;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MetricValue;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MonitoringEntriesAdapter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.Attributes;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Resource")
public class Resource extends Unit implements Cloneable {

    @XmlJavaTypeAdapter(PropertiesAdapter.class)
    private Map<Metric, MetricValue> properties;
//    private List<Quality> resourceQuality;

    {
        properties = new LinkedHashMap<Metric, MetricValue>();
//        resourceQuality = new ArrayList<Quality>();
    }
//
//    public void addQualityProperty(Quality quality) {
//        resourceQuality.add(quality);
//    }
//
//    public void removeQuality(Quality quality) {
//        resourceQuality.remove(quality);
//    }

    public void addProperty(Metric metric, MetricValue metricValue) {
        properties.put(metric, metricValue);
    }

    public void removeProperty(Metric metric) {
        properties.remove(metric);
    }
//
//    public List<Quality> getResourceQuality() {
//        return resourceQuality;
//    }

    public void setProperties(Map<Metric, MetricValue> properties) {
        this.properties = properties;
    }

    public Resource() {
    }

    public Resource(String name) {
        super(name);
    }

    public Map<Metric, MetricValue> getProperties() {
        return properties;
    }

    public Resource clone() {
        Unit clone = super.clone();
        Resource newResource = new Resource(name);
        newResource.setId(clone.id);
        for (Map.Entry<Metric, MetricValue> e : properties.entrySet()) {
            //currently this is NOT cloned. if needed, can be easily also clone (both are cloneable)
            newResource.addProperty(e.getKey(), e.getValue());
        }

        return newResource;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Resource) {
            Resource their = (Resource) obj;
            Map<Metric, MetricValue> theirProperties = their.properties;

            //if they have different number of properties then they are not equal
            if (properties.keySet().size() != theirProperties.keySet().size()) {
                return false;
            } else {
                //else check that all values are equal
                for (Metric metric : properties.keySet()) {
                    if (their.properties.containsKey(metric)) {
                        return their.properties.get(metric).equals(properties.get(metric));
                    } else {
                        //if they have different properties
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Resource{" + "name=" + name + ", id=" + this.getId() + ", properties=" + properties + '}';
    }
}
