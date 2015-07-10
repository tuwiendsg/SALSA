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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CloudOfferedService")
public class CloudOfferedService extends Unit {

    @XmlAttribute(name = "category", required = true)
    private String category;
    @XmlAttribute(name = "subcategory", required = true)
    private String subcategory;

    //the next three fields hold static properties
    @XmlElement(name = "CostFunction", required = false)
    private List<CostFunction> costFunctions;
    @XmlElement(name = "Resource", required = false)
    private List<Resource> resourceProperties;
    @XmlElement(name = "Quality", required = false)
    private List<Quality> qualityProperties;

    //holds dynamic properties , i.e. elasticity capabilities, something you can change
    @XmlElement(name = "ElasticityCapability", required = false)
    private List<ElasticityCapability> elasticityCapabilities;

//    
    //from here onwards associations )optional or mandatory) are seen as ElasticityCapabilities
//    private List<serviceUnit> mandatoryAssociations;
//    private List<serviceUnit> optionalAssociations;
    {
        costFunctions = new ArrayList<CostFunction>();
        qualityProperties = new ArrayList<Quality>();
        resourceProperties = new ArrayList<Resource>();
//        optionalAssociations = new ArrayList<serviceUnit>();
//        mandatoryAssociations = new ArrayList<serviceUnit>();
        elasticityCapabilities = new ArrayList<ElasticityCapability>();
    }

    public CloudOfferedService() {
    }

    public CloudOfferedService(String category, String subcategory, String name) {
        super(name);
        this.category = category;
        this.subcategory = subcategory;
    }

    public void setCostFunctions(List<CostFunction> costFunctions) {
        this.costFunctions = costFunctions;
    }

    public void setResourceProperties(List<Resource> resourceProperties) {
        this.resourceProperties = resourceProperties;
    }

    public void setQualityProperties(List<Quality> qualityProperties) {
        this.qualityProperties = qualityProperties;
    }

    public void addCostFunction(CostFunction cf) {
        if (!costFunctions.contains(cf)) {
            costFunctions.add(cf);
        }
    }

    public void removeCostFunction(CostFunction cf) {
        costFunctions.remove(cf);
    }

    public void addResourceProperty(Resource resource) {
        if (!resourceProperties.contains(resource)) {
            resourceProperties.add(resource);
        }
    }

    public void removeResourceProperty(Resource resource) {
        resourceProperties.remove(resource);
    }

    public void addQualityProperty(Quality quality) {
        if (!qualityProperties.contains(quality)) {
            qualityProperties.add(quality);
        }
    }

    public void removeQualityProperty(Quality quality) {
        qualityProperties.remove(quality);
    }

//    public void addMandatoryAssociation(serviceUnit serviceUnit){
//        mandatoryAssociations.add(serviceUnit);
//    }
//    
//    public void removeMandatoryAssociation(serviceUnit serviceUnit){
//        mandatoryAssociations.remove(serviceUnit);
//    }
//    
//    
//    public void addOptionalAssociation(serviceUnit serviceUnit){
//        optionalAssociations.add(serviceUnit);
//    }
//    
//    public void removeOptionalAssociation(serviceUnit serviceUnit){
//        optionalAssociations.remove(serviceUnit);
//    }
//
//    public List<serviceUnit> getMandatoryAssociations() {
//        return mandatoryAssociations;
//    }
//
//    public List<serviceUnit> getOptionalAssociations() {
//        return optionalAssociations;
//    }
//       
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public List<CostFunction> getCostFunctions() {
        return costFunctions;
    }

    public List<Resource> getResourceProperties() {
        return resourceProperties;
    }

    public List<Quality> getQualityProperties() {
        return qualityProperties;
    }

    public List<ElasticityCapability> getElasticityCapabilities() {
        return elasticityCapabilities;
    }

    public void setElasticityCapabilities(List<ElasticityCapability> elasticityCapabilities) {
        this.elasticityCapabilities = elasticityCapabilities;
    }

    public void addElasticityCapability(ElasticityCapability characteristic) {
        this.elasticityCapabilities.add(characteristic);
    }

    public void removeElasticityCapability(ElasticityCapability characteristic) {
        this.elasticityCapabilities.remove(characteristic);
    }

    public List<ElasticityCapability> getServiceUnitAssociations() {

        List<ElasticityCapability> mandatoryAssociations = new ArrayList<ElasticityCapability>();

        for (ElasticityCapability capability : getElasticityCapabilities()) {

            //only optional associations towards ServiceUnit
            if (!capability.getTargetType().equals(CloudOfferedService.class)) {
                continue;
            }

            mandatoryAssociations.add(capability);
        }

        return mandatoryAssociations;
    }

    public List<ElasticityCapability> getResourceAssociations() {

        List<ElasticityCapability> optionalAssociations = new ArrayList<ElasticityCapability>();

        for (ElasticityCapability capability : getElasticityCapabilities()) {

            //only optional associations towards ServiceUnit
            if (!capability.getTargetType().equals(Resource.class)) {
                continue;
            }

            optionalAssociations.add(capability);
        }

        return optionalAssociations;
    }

    public List<ElasticityCapability> getQualityAssociations() {

        List<ElasticityCapability> optionalAssociations = new ArrayList<ElasticityCapability>();

        for (ElasticityCapability capability : getElasticityCapabilities()) {

            //only optional associations towards ServiceUnit
            if (!capability.getTargetType().equals(Quality.class)) {
                continue;
            }

            optionalAssociations.add(capability);
        }

        return optionalAssociations;
    }

    public List<ElasticityCapability> getCostAssociations() {

        List<ElasticityCapability> optionalAssociations = new ArrayList<ElasticityCapability>();

        for (ElasticityCapability capability : getElasticityCapabilities()) {

            //only optional associations towards ServiceUnit
            if (!capability.getTargetType().equals(CostFunction.class)) {
                continue;
            }

            optionalAssociations.add(capability);
        }

        return optionalAssociations;
    }

    public List<ElasticityCapability> getElasticityCapabilities(Class capabilitiesTargetClass) {

        List<ElasticityCapability> optionalAssociations = new ArrayList<ElasticityCapability>();

        for (ElasticityCapability capability : getElasticityCapabilities()) {
            if (!capability.getTargetType().equals(capabilitiesTargetClass)) {
                continue;
            }
            optionalAssociations.add(capability);
        }

        return optionalAssociations;
    }

    public enum Category {

        IaaS("IaaS"),
        PaaS("PaaS"),
        MaaS("MaaS");

        private String name;

        private Category(String name) {
            this.name = name;
        }

        public boolean equals(String name) {
            return this.name.equals(name);
        }

    }

    @Override
    public String toString() {
        return "ServiceUnit{" + "name=" + name + " category=" + category + ", subcategory=" + subcategory + ", costFunctions=" + costFunctions + ", resourceProperties=" + resourceProperties + ", qualityProperties=" + qualityProperties + ", elasticityCapabilities=" + elasticityCapabilities + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.category != null ? this.category.hashCode() : 0);
        hash = 89 * hash + (this.subcategory != null ? this.subcategory.hashCode() : 0);
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
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
        final CloudOfferedService other = (CloudOfferedService) obj;
        if ((this.category == null) ? (other.category != null) : !this.category.equals(other.category)) {
            return false;
        }
        if ((this.subcategory == null) ? (other.subcategory != null) : !this.subcategory.equals(other.subcategory)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public CloudOfferedService withCategory(final String category) {
        this.category = category;
        return this;
    }

    public CloudOfferedService withSubcategory(final String subcategory) {
        this.subcategory = subcategory;
        return this;
    }

    public CloudOfferedService withCostFunctions(final List<CostFunction> costFunctions) {
        this.costFunctions = costFunctions;
        return this;
    }

    public CloudOfferedService withCostFunction(CostFunction costFunction) {
        this.costFunctions.add(costFunction);
        return this;
    }

    public CloudOfferedService withResourceProperty(Resource resourceProperty) {
        this.resourceProperties.add(resourceProperty);
        return this;
    }

    public CloudOfferedService withQualityProperty(Quality qualityProperty) {
        this.qualityProperties.add(qualityProperty);
        return this;
    }

    public CloudOfferedService withElasticityCapability(ElasticityCapability elasticityCapability) {
        this.elasticityCapabilities.add(elasticityCapability);
        return this;
    }
 
    @Override
    public CloudOfferedService withName(String name) {
        super.withName(name);
        return this;
    }

    @Override
    public CloudOfferedService withUuid(UUID uuid) {
        super.withUuid(uuid);
        return this;
    }

    }
