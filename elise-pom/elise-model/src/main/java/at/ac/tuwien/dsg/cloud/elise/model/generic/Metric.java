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
package at.ac.tuwien.dsg.cloud.elise.model.generic;

import javax.xml.bind.annotation.XmlType;

/**
 * This represents arbitrary metric types and value
 *
 * @author Duc-Hung Le
 */
public class Metric {

    protected String name;
    protected String measurementUnit;
    protected MetricType metricType;
    protected Object value;

    public Metric() {
    }

    public Metric(String name, Object value, String measurementUnit, MetricType metricType) {
        this.name = name;
        this.value = value;
        this.measurementUnit = measurementUnit;
        this.metricType = metricType;
    }

    @XmlType
    public enum MetricType {
        RESOURCE, QUALITY, COST, ELASTICITY
    }

    public String getName() {
        return this.name;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public Object getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
