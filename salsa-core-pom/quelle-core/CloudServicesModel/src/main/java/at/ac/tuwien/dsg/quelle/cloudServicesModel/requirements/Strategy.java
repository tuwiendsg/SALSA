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
package at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OptimizationStrategy")
@XmlType(propOrder = {"strategyCategory", "targets"})
public class Strategy implements Serializable {

    @XmlAttribute(name = "type", required = true)
    private StrategyCategory strategyCategory;
    @XmlElement(name = "Target", required = false)
    private List<String> targets;

    {
        targets = new ArrayList<String>();
    }

    public StrategyCategory getStrategyCategory() {
        return strategyCategory;
    }

    public void setStrategyCategory(StrategyCategory strategyCategory) {
        this.strategyCategory = strategyCategory;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }

    public void addTarget(String target) {
        if (!this.targets.contains(target)) {
            this.targets.add(target);
        }
    }

    public void removeTarget(String target) {
        if (this.targets.contains(target)) {
            this.targets.remove(target);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.strategyCategory);
        hash = 61 * hash + Objects.hashCode(this.targets);
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
        final Strategy other = (Strategy) obj;
        if (this.strategyCategory != other.strategyCategory) {
            return false;
        }
        if (!Objects.equals(this.targets, other.targets)) {
            return false;
        }
        return true;
    }

    public Strategy withCategoryString(String categoryName) {
        switch (categoryName) {
            case "COST_ELASTICITY":
                strategyCategory = StrategyCategory.COST_ELASTICITY;
                break;
            case "COST_PROPERTIES":
                strategyCategory = StrategyCategory.COST_PROPERTIES;
                break;
            case "COST_REQUIREMENTS":
                strategyCategory = StrategyCategory.COST_REQUIREMENTS;
                break;
            case "MINIMUM_COST":
                strategyCategory = StrategyCategory.MINIMUM_COST;
                break;
            case "MINIMUM_RESOURCES":
                strategyCategory = StrategyCategory.MINIMUM_RESOURCES;
                break;
            case "OVERALL_ELASTICITY":
                strategyCategory = StrategyCategory.OVERALL_ELASTICITY;
                break;
            case "QUALITY_ELASTICITY":
                strategyCategory = StrategyCategory.QUALITY_ELASTICITY;
                break;
            case "QUALITY_PROPERTIES":
                strategyCategory = StrategyCategory.QUALITY_PROPERTIES;
                break;
            case "QUALITY_REQUIREMENTS":
                strategyCategory = StrategyCategory.QUALITY_REQUIREMENTS;
                break;

            case "OVERALL_REQUIREMENTS":
                strategyCategory = StrategyCategory.OVERALL_REQUIREMENTS;
                break;

            case "RESOURCE_ELASTICITY":
                strategyCategory = StrategyCategory.RESOURCE_ELASTICITY;
                break;
            case "RESOURCE_REQUIREMENTS":
                strategyCategory = StrategyCategory.RESOURCE_REQUIREMENTS;
                break;
            case "SERVICE_UNITS_ASSOCIATIONS_ELASTICITY":
                strategyCategory = StrategyCategory.SERVICE_UNITS_ASSOCIATIONS_ELASTICITY;
                break;
        }

        return this;
    }

}
