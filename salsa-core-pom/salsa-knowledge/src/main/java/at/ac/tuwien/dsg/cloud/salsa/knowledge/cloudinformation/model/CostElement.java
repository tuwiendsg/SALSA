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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.helpers.CostIntervalMapAdapter;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.melaconcepts.Metric;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.melaconcepts.MetricValue;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CostFunction")
public class CostElement extends Entity {

    @XmlAttribute(name = "type", required = true)
    private String type;
    //metric on which cost is applied
    @XmlElement(name = "CostMetric", required = false)
    private Metric costMetric;
    //the MAP marks <upper_interval_limit, and cost>
    //the map is traversed iteratively from the lowest interval to the highest
    //example: <1,0.12> , <10,0.18>, <1024,1> will be interpreted for a 500 consumed units as 
    //1*0.12 + 9 * 0.18 + 490*1 , so first 1, then next 9 until 19, then next 490 until 500
    @XmlJavaTypeAdapter(CostIntervalMapAdapter.class)
    private Map<MetricValue, Double> costIntervalFunction;

    {
        costIntervalFunction = new LinkedHashMap<MetricValue, Double>();
    }

    public CostElement() {
    }

    public CostElement(String name) {
        super(name);
    }

    public CostElement(String name, Metric costMetric) {
        super(name);
        this.costMetric = costMetric;
    }

    public CostElement(String name, Metric costMetric, String type) {
        super(name);
        this.type = type;
        this.costMetric = costMetric;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addCostInterval(MetricValue metricValue, Double cost) {
        costIntervalFunction.put(metricValue, cost);
    }

    public void removeCostInterval(MetricValue metricValue) {
        costIntervalFunction.remove(metricValue);
    }

    public Metric getCostMetric() {
        return costMetric;
    }

    public void setCostMetric(Metric costMetric) {
        this.costMetric = costMetric;
    }

    private List<MetricValue> getCostIntervalsInAscendingOrder() {
        List<MetricValue> keys = new ArrayList<MetricValue>(costIntervalFunction.keySet());
        Collections.sort(keys, new Comparator<MetricValue>() {
            public int compare(MetricValue v1, MetricValue v2) {
                return v1.compareTo(v2);
            }
        });

        return keys;

    }

    public Map<MetricValue, Double> getCostIntervalFunction() {
        return costIntervalFunction;
    }
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.costMetric != null ? this.costMetric.hashCode() : 0);
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
        final CostElement other = (CostElement) obj;
        if (this.costMetric != other.costMetric && (this.costMetric == null || !this.costMetric.equals(other.costMetric))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CostElement{" + "costMetric=" + costMetric + "type=" + type + ", costIntervalFunction=" + costIntervalFunction + '}';
    }

    public interface Type {

        public static final String PERIODIC = "PERIODIC";
        public static final String USAGE = "USAGE";
    }
}
