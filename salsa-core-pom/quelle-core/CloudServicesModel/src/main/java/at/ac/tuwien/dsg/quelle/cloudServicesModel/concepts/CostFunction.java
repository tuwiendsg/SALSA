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
package at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CostFunction")
public class CostFunction extends Unit {

    //utility for which cost is applied
    //private serviceUnit utility;
    //if we apply the "utility" together with the entities in this LIST, then the cost
    //is the one specified by this function
    //Is Entity to support ServiceUnit, or Resource and Quality options (you pay separately per I/O performance, etc)
    @XmlElement(name = "InConjunctionWith", required = false)
    //@XmlTransient
    //if more, means logical AND between them
    private List<Unit> appliedInConjunctionWith;
    @XmlElement(name = "CostElement", required = false)
    private List<CostElement> costElements;

    {
        costElements = new ArrayList<CostElement>();
        appliedInConjunctionWith = new ArrayList<Unit>();
    }

    public CostFunction() {
    }

    public CostFunction(String name) {
        super(name);
    }

    public List<Unit> getAppliedIfServiceInstanceUses() {
        return appliedInConjunctionWith;
    }

    public void setAppliedIfServiceInstanceUses(List<Unit> appliedInConjunctionWith) {
        this.appliedInConjunctionWith = appliedInConjunctionWith;
    }

    public void setCostElements(List<CostElement> costElements) {
        this.costElements = costElements;
    }

    public List<Quality> getAppliedIfServiceInstanceUsesQuality() {
        List<Quality> list = new ArrayList<Quality>();
        for (Unit e : appliedInConjunctionWith) {
            if (e instanceof Quality) {
                list.add((Quality) e);
            }
        }
        return list;
    }

    public List<Resource> getAppliedIfServiceInstanceUsesResource() {
        List<Resource> list = new ArrayList<Resource>();
        for (Unit e : appliedInConjunctionWith) {
            if (e instanceof Resource) {
                list.add((Resource) e);
            }
        }
        return list;
    }

    public List<CloudOfferedService> getAppliedIfServiceInstanceUsesCloudOfferedServices() {
        List<CloudOfferedService> list = new ArrayList<CloudOfferedService>();
        for (Unit e : appliedInConjunctionWith) {
            if (e instanceof CloudOfferedService) {
                list.add((CloudOfferedService) e);
            }
        }
        return list;
    }

//    public serviceUnit getUtility() {
//        return utility;
//    }
//
//    public void setUtility(serviceUnit utility) {
//        this.utility = utility;
//    }
//    public CostFunction(serviceUnit utility, List<serviceUnit> appliedInConjunctionWith, Type type) {
//        this.utility = utility;
//        this.appliedInConjunctionWith = appliedInConjunctionWith;
//        this.type = type;
//    }
//
//    public CostFunction(serviceUnit utility, Type type) {
//        this.utility = utility;
//        this.type = type;
//    }
    public void addCostElement(CostElement ce) {
        costElements.add(ce);
    }

    public void removeCostElement(CostElement ce) {
        costElements.remove(ce);
    }

    public void addAppliedIfServiceInstanceUses(Unit u) {
        if (u instanceof CostFunction) {
            System.err.println("Adding " + u.name + " as in conjunction with for " + this.name);
            new Exception().printStackTrace();
        }
        this.appliedInConjunctionWith.add(u);
    }

    public void removeUtilityAppliedInConjunctionWith(Unit u) {
        this.appliedInConjunctionWith.remove(u);
    }

    public List<CostElement> getCostElements() {
        return costElements;
    }

    @Override
    public String toString() {
        return "CostFunction{" + ", costElements=" + costElements + '}';
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public CostFunction withAppliedIfServiceInstanceUses(final List<Unit> appliedInConjunctionWith) {
        this.appliedInConjunctionWith = appliedInConjunctionWith;
        return this;
    }

    public CostFunction withAppliedIfServiceInstanceUses(Unit appliedInConjunctionWith) {
        this.appliedInConjunctionWith.add(appliedInConjunctionWith);
        return this;
    }

    public CostFunction withCostElement(CostElement costElement) {
        this.costElements.add(costElement);
        return this;
    }
}
