/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;



/**
 *
 * @author hungld
 *
 * Each metric has an unique ID This class extends Entity just for using EntityRepository, not for semantic of the model
 */
@NodeEntity
public class Metric {

    @GraphId Long graphID;
    
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
