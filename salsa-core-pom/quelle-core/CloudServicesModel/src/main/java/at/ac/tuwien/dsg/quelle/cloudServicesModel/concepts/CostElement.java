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

import at.ac.tuwien.dsg.quelle.cloudServicesModel.util.conversions.helper.CostIntervalMapAdapter;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Metric;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MetricValue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CostFunction")
public class CostElement extends Unit implements Comparable<CostElement> {

    @XmlAttribute(name = "type", required = true)
    private String type;
    //metric on which cost is applied
    @XmlElement(name = "CostMetric", required = false)
    private Metric costMetric;

    //the MAP marks <upper_interval_limit, and cost>
    //the map is traversed iteratively from the lowest interval to the highest
    //example: <1,0.12> , <10,0.18>, <1024,1> will be interpreted for a 500 consumed units as 
    //1*0.12 + 9 * 0.18 + 490*1 , so first 1, then next 9 until 10, then next 490 until 500
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
        this.costMetric.setType(Metric.MetricType.COST);

    }

    public CostElement(String name, Metric costMetric, String type) {
        super(name);
        this.type = type;
        this.costMetric = costMetric;
        this.costMetric.setType(Metric.MetricType.COST);

    }

    public CostElement(String name, Metric costMetric, BillingCycle billingPeriod, String type) {
        super(name);
        this.type = type;
        this.costMetric = costMetric;
        this.costMetric.setMeasurementUnit(this.costMetric.getMeasurementUnit() + "/" + billingPeriod.toString());
        this.costMetric.setType(Metric.MetricType.COST);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addBillingInterval(MetricValue metricValue, Double cost) {
        costIntervalFunction.put(metricValue, cost);
    }

    public void removeBillingInterval(MetricValue metricValue) {
        costIntervalFunction.remove(metricValue);
    }

    public Metric getCostMetric() {
        return costMetric;
    }

    public void setCostMetric(Metric costMetric) {
        this.costMetric = costMetric;
//        this was not commented, which started triggering problems with cost composition
//        this.costMetric.setType(Metric.MetricType.COST);
    }

    public List<MetricValue> getCostIntervalsInAscendingOrder() {
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

    public Double getCostForCostMetricValue(MetricValue value) {
        for (Entry<MetricValue, Double> entry : costIntervalFunction.entrySet()) {
            if (entry.getKey().compareTo(value) >= 0) {
                return entry.getValue();
            }
        }

        List<MetricValue> costValues = getCostIntervalsInAscendingOrder();
        //else return max cost
        return costIntervalFunction.get(costValues.get(costValues.size() - 1));

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

    @Override
    public int compareTo(CostElement o) {

        //can't compare if cost not of same type
        if (!this.type.equals(o.type)) {
            return 0;
        }

        //can't compare if cost not on same metric
        if (!this.costMetric.equals(o.costMetric)) {
            return 0;
        }

        //make cost sum = for all cost, costInterval * cost.
        Double cost_1 = 0.0;
        for (Entry<MetricValue, Double> entry : this.costIntervalFunction.entrySet()) {
            cost_1 += Double.parseDouble(entry.getKey().getValueRepresentation()) * entry.getValue();
        }

        Double cost_2 = 0.0;
        for (Entry<MetricValue, Double> entry : o.costIntervalFunction.entrySet()) {
            cost_2 += Double.parseDouble(entry.getKey().getValueRepresentation()) * entry.getValue();
        }

        return cost_1.compareTo(cost_2);

    }

    public interface Type {

        public static final String PERIODIC = "PERIODIC";
        public static final String USAGE = "USAGE";
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "Metric")
    @XmlEnum
    public enum BillingCycle implements Serializable {

        @XmlEnumValue("s")
        SECOND("s"),
        @XmlEnumValue("m")
        MINUTE("m"),
        @XmlEnumValue("h")
        HOUR("h"),
        //describes the elasticity of a monitored element
        @XmlEnumValue("d")
        DAY("d");

        private String representation;

        private BillingCycle(String representation) {
            this.representation = representation;
        }

        @Override
        public String toString() {
            return representation;
        }

        public BillingCycle fromString(String representation) {
            if (representation.equals("s")) {
                return SECOND;
            } else if (representation.equals("m")) {
                return MINUTE;
            } else if (representation.equals("h")) {
                return HOUR;
            } else if (representation.equals("d")) {
                return DAY;
            } else {
                return SECOND;
            }

        }

    }

    public CostElement withType(final String type) {
        this.type = type;
        return this;
    }

    public CostElement withCostMetric(final Metric costMetric) {
        this.costMetric = costMetric;
        return this;
    }

    public CostElement withBillingCycle(final BillingCycle billingPeriod) {
        this.costMetric.setMeasurementUnit(this.costMetric.getMeasurementUnit() + "/" + billingPeriod.toString());
        return this;
    }

    public CostElement withCostInterval(final Map<MetricValue, Double> costIntervalFunction) {
        this.costIntervalFunction = costIntervalFunction;
        return this;
    }

    public CostElement withBillingInterval(final MetricValue intervalUpperBound, Double value) {
        this.costIntervalFunction.put(intervalUpperBound, value);
        return this;
    }

}
