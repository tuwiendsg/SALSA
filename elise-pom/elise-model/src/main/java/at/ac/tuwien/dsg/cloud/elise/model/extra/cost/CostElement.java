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
package at.ac.tuwien.dsg.cloud.elise.model.extra.cost;

import at.ac.tuwien.dsg.cloud.elise.model.generic.Metric;

/**
 * A cost in a period of usage, e.g. with storage service, the first 1GB is free and user must pay from there on.
 * @author Duc-Hung LE
 */
public class CostElement {

    protected String costType;
    protected Metric costIntervalFunction;
    protected Double cost;

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
