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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.helpers.PropertiesAdapter;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.melaconcepts.Metric;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.melaconcepts.MetricValue;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Quality")
public class Quality extends Entity implements Cloneable {

    @XmlJavaTypeAdapter(PropertiesAdapter.class)
    private Map<Metric, MetricValue> properties;

    {
        properties = new LinkedHashMap<Metric, MetricValue>();
    }

    public Quality() {
    }

    public Quality(String name) {
        super(name);
    }

    public void addProperty(Metric metric, MetricValue metricValue) {
        properties.put(metric, metricValue);
    }

    public void removeProperty(Metric metric) {
        properties.remove(metric);
    }

    public Map<Metric, MetricValue> getProperties() {
        return properties;
    }

    public Quality clone() {
        Entity clone = super.clone();
        Quality quality = new Quality(name);
        quality.setId(clone.id);
        for (Map.Entry<Metric, MetricValue> e : properties.entrySet()) {
            //currently this is NOT cloned. if needed, can be easily also clone (both are cloneable)
            quality.addProperty(e.getKey(), e.getValue());
        }

        return quality;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void setProperties(Map<Metric, MetricValue> properties) {
        this.properties = properties;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Quality) {
            Quality their = (Quality) obj;
            Map<Metric, MetricValue> theirProperties = their.properties;

            //if they have different number of properties then they are not equal
            if (properties.keySet().size() != theirProperties.keySet().size()) {
                return false;
            } else {
                //else check that all values are equal
                for (Metric metric : properties.keySet()) {
                    if (!their.properties.containsKey(metric)) {
//                        return their.properties.get(metric).equals(properties.get(metric));
//                    } else {
                        //if they have different properties
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Quality{" + "name=" + name + ", id=" + this.getId() + ", properties=" + properties + '}';
    }
}
