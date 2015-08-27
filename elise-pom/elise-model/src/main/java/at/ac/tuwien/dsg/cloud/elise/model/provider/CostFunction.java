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
package at.ac.tuwien.dsg.cloud.elise.model.provider;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Define a dynamic cost for the offered service unit
 * @author Duc-Hung LE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
@NodeEntity
public class CostFunction  {

    protected Set<OfferedServiceUnit> appliedInConjunctionWith;
    protected Set<CostElement> costElements;
    protected String name;

    public CostFunction() {
    }

    public CostFunction(String name) {
        this.name = name;
    }

    /**
     * The CostFunction can be assigned to a ServiceUnit
     * @param seviceResourceOrQuality 
     */
    public void assignToEntity(OfferedServiceUnit seviceResourceOrQuality) {
        this.appliedInConjunctionWith.add(seviceResourceOrQuality);
    }

    public Set<OfferedServiceUnit> getAppliedInConjunctionWith() {
        return this.appliedInConjunctionWith;
    }

    public Set<CostElement> getCostElements() {
        return this.costElements;
    }

    public void addCostElement(CostElement costElement) {
        if (this.costElements == null) {
            this.costElements = new HashSet();
        }
        this.costElements.add(costElement);
    }

    public void conjunctWith(OfferedServiceUnit entity) {
        if (this.appliedInConjunctionWith == null) {
            this.appliedInConjunctionWith = new HashSet();
        }
        this.appliedInConjunctionWith.add(entity);
    }

    public void setAppliedInConjunctionWith(Set<OfferedServiceUnit> appliedInConjunctionWith) {
        this.appliedInConjunctionWith = appliedInConjunctionWith;
    }

    public void setCostElements(Set<CostElement> costElements) {
        this.costElements = costElements;
    }

}
