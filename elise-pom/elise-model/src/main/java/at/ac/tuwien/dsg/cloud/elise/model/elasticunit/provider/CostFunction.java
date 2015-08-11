/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.provider;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic.Properties;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic.GenericServiceUnit;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.neo4j.annotation.GraphId;

import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 *
 * @author hungld
 */
@NodeEntity
public class CostFunction extends Properties {

    protected Set<Properties> appliedInConjunctionWith;
    protected Set<CostElement> costElements;

    public CostFunction() {
    }

    public CostFunction(String name) {
        this.name = name;
    }

    public void assignToEntity(Properties seviceResourceOrQuality) {
        this.appliedInConjunctionWith.add(seviceResourceOrQuality);
    }

    public Set<Properties> getAppliedInConjunctionWith() {
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

    public void conjunctWith(Properties entity) {
        if (this.appliedInConjunctionWith == null) {
            this.appliedInConjunctionWith = new HashSet();
        }
        this.appliedInConjunctionWith.add(entity);
    }

    public void setAppliedInConjunctionWith(Set<Properties> appliedInConjunctionWith) {
        this.appliedInConjunctionWith = appliedInConjunctionWith;
    }

    public void setCostElements(Set<CostElement> costElements) {
        this.costElements = costElements;
    }

}
