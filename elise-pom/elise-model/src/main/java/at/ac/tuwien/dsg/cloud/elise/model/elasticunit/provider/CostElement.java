/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.provider;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic.Metric;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic.Properties;

import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 *
 * @author hungld
 */
@NodeEntity
public class CostElement extends Properties {

    protected String costType;
    Metric costIntervalFunction;
    Double cost;

    public CostElement() {
    }

    public CostElement(String costType, Metric costInterval, Double cost) {
        this.costType = costType;
        this.costIntervalFunction = costInterval;
        this.cost = cost;
    }

    public String getCostType() {
        return this.costType;
    }

    public Metric getCostIntervalFunction() {
        return this.costIntervalFunction;
    }

    public Double getCost() {
        return this.cost;
    }

    public void setType(String costType) {
        this.costType = costType;
    }

    public void setCostIntervalFunction(Metric costIntervalFunction) {
        this.costIntervalFunction = costIntervalFunction;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

}
