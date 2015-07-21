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
package at.ac.tuwien.dsg.quelle.elasticityQuantification.dtos;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CostFunction;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Quality;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Resource;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.engines.RequirementsMatchingEngine;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.requirements.ServiceUnitConfigurationSolution;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Metric;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MetricValue;
import at.ac.tuwien.dsg.mela.common.requirements.Requirement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel Moldovan E-Mail: d.moldovan@dsg.tuwien.ac.at
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CloudServiceConfigurationRecommendation")
public class CloudServiceConfigurationRecommendation {
//unit for which we have the options and connected units

    @XmlElement(name = "Name", required = false)
    private String recommendationName;

    @XmlElement(name = "CloudService", required = false)
    private CloudOfferedService serviceUnit;

    @XmlElement(name = "Quality", required = false)
    private List<Quality> chosenQualityOptions;

    @XmlElement(name = "Resource", required = false)
    private List<Resource> chosenResourceOptions;

    @XmlElement(name = "MatchedRequirement", required = false)
    private List<Requirement> overallMatched;

    @XmlElement(name = "UnMatchedRequirement", required = false)
    private List<Requirement> overallUnMatched;

    @XmlElement(name = "MandatoryAssociatedCloudService", required = false)
    private List<ServiceUnitConfigurationSolution> mandatoryAssociatedServiceUnits;

    @XmlElement(name = "OptionallyAssociatedCloudService", required = false)
    private List<ServiceUnitConfigurationSolution> optionallyAssociatedServiceUnits;

    @XmlElement(name = "CostFunction", required = false)
    private List<CostFunction> costFunctions;

//    @XmlElement(name = "Description", required = false)
//    private String description;
//   
    @XmlElement(name = "CostElasticity", required = false)
    private double costElasticity = Double.POSITIVE_INFINITY;

    @XmlElement(name = "SUElasticity", required = false)
    private double suelasticity = Double.POSITIVE_INFINITY;

    @XmlElement(name = "ResourceElasticity", required = false)
    private double resourceElasticity = Double.POSITIVE_INFINITY;

    @XmlElement(name = "QualityElasticity", required = false)
    private double qualityElasticity = Double.POSITIVE_INFINITY;

    {
        chosenQualityOptions = new ArrayList<>();
        chosenResourceOptions = new ArrayList<>();
        mandatoryAssociatedServiceUnits = new ArrayList<>();
        optionallyAssociatedServiceUnits = new ArrayList<>();
        costFunctions = new ArrayList<>();
    }

    public String getRecommendationName() {
        return recommendationName;
    }

    public void setRecommendationName(String recommendationName) {
        this.recommendationName = recommendationName;
    }

    public void removeQualityOption(Quality su) {
        chosenQualityOptions.remove(su);
    }

    public void addQualityOption(Quality su) {
        chosenQualityOptions.add(su);
    }

    public void removeResourceOption(Resource su) {
        chosenResourceOptions.remove(su);
    }

    public void addResourceOption(Resource su) {
        chosenResourceOptions.add(su);
    }

    public void removeOptionalServiceUnitRecommendation(ServiceUnitConfigurationSolution su) {
        optionallyAssociatedServiceUnits.remove(su);
    }

    public void addOptionalServiceUnitRecommendation(ServiceUnitConfigurationSolution su) {
        optionallyAssociatedServiceUnits.add(su);
    }

    public void removeMandatoryServiceUnitRecommandation(ServiceUnitConfigurationSolution su) {
        optionallyAssociatedServiceUnits.remove(su);
    }

    public void addMandatoryServiceUnitRecommandation(ServiceUnitConfigurationSolution su) {
        optionallyAssociatedServiceUnits.add(su);
    }

    public void removeCostFunctionn(CostFunction su) {
        costFunctions.remove(su);
    }

    public void addCostFunction(CostFunction su) {
        costFunctions.add(su);
    }

    public List<ServiceUnitConfigurationSolution> getMandatoryAssociatedServiceUnits() {
        return mandatoryAssociatedServiceUnits;
    }

    public void setMandatoryAssociatedServiceUnits(List<ServiceUnitConfigurationSolution> mandatoryAssociatedServiceUnits) {
        this.mandatoryAssociatedServiceUnits = mandatoryAssociatedServiceUnits;
    }

    public List<ServiceUnitConfigurationSolution> getOptionallyAssociatedServiceUnits() {
        return optionallyAssociatedServiceUnits;
    }

    public void setOptionallyAssociatedServiceUnits(List<ServiceUnitConfigurationSolution> optionallyAssociatedServiceUnits) {
        this.optionallyAssociatedServiceUnits = optionallyAssociatedServiceUnits;
    }

    public CloudOfferedService getServiceUnit() {
        return serviceUnit;
    }

    public void setServiceUnit(CloudOfferedService serviceUnit) {
        this.serviceUnit = serviceUnit;
    }

    public List<Quality> getChosenQualityOptions() {
        return chosenQualityOptions;
    }

    public void setChosenQualityOptions(List<Quality> chosenQualityOptions) {
        this.chosenQualityOptions = chosenQualityOptions;
    }

    public List<Resource> getChosenResourceOptions() {
        return chosenResourceOptions;
    }

    public void setChosenResourceOptions(List<Resource> chosenResourceOptions) {
        this.chosenResourceOptions = chosenResourceOptions;
    }

    public List<Requirement> getOverallMatched() {
        return overallMatched;
    }

    public void setOverallMatched(List<Requirement> overallMatched) {
        this.overallMatched = overallMatched;
    }

    public List<Requirement> getOverallUnMatched() {
        return overallUnMatched;
    }

    public void setOverallUnMatched(List<Requirement> overallUnMatched) {
        this.overallUnMatched = overallUnMatched;
    }

    public List<CostFunction> getCostFunctions() {
        return costFunctions;
    }

    public void setCostFunctions(List<CostFunction> costFunctions) {
        this.costFunctions = costFunctions;
    }

    public double getQualityElasticity() {
        return qualityElasticity;
    }

    public void setQualityElasticity(double qualityElasticity) {
        this.qualityElasticity = qualityElasticity;
    }

    public CloudServiceConfigurationRecommendation withServiceUnitConfigurationSolution(String name, ServiceUnitConfigurationSolution solution, double costElasticity, double suelasticity, double resourceElasticity, double qualityElasticity) {

        for (RequirementsMatchingEngine.RequirementsMatchingReport<Quality> report : solution.getChosenQualityOptions()) {
            chosenQualityOptions.add(report.getConcreteConfiguration());
        }

        for (RequirementsMatchingEngine.RequirementsMatchingReport<Resource> report : solution.getChosenResourceOptions()) {
            chosenResourceOptions.add(report.getConcreteConfiguration());
        }

        overallMatched = solution.getOverallMatched();
        overallUnMatched = solution.getOverallUnMatched();
        mandatoryAssociatedServiceUnits = solution.getMandatoryAssociatedServiceUnits();
        optionallyAssociatedServiceUnits = solution.getOptionallyAssociatedServiceUnits();
        costFunctions = solution.getCostFunctions();
        serviceUnit = solution.getServiceUnit();

        this.costElasticity = costElasticity;
        this.suelasticity = suelasticity;
        this.resourceElasticity = resourceElasticity;
        this.qualityElasticity = qualityElasticity;
        
        this.recommendationName = name;

        return this;
    }

    public double getCostElasticity() {
        return costElasticity;
    }

    public void setCostElasticity(double costElasticity) {
        this.costElasticity = costElasticity;
    }

    public double getSuelasticity() {
        return suelasticity;
    }

    public void setSuelasticity(double suelasticity) {
        this.suelasticity = suelasticity;
    }

    public double getResourceElasticity() {
        return resourceElasticity;
    }

    public void setResourceElasticity(double resourceElasticity) {
        this.resourceElasticity = resourceElasticity;
    }

    public JSONObject toJSON() {
        JSONObject jsonDescription = new JSONObject();
        jsonDescription.put("serviceUnit", serviceUnit.getName());

        //quality options
        {
            JSONArray array = new JSONArray();
            for (Quality quality : chosenQualityOptions) {

                JSONObject qualityDescription = new JSONObject();
                jsonDescription.put("quality", quality.getName());
                JSONArray qualityProperties = new JSONArray();

                for (Map.Entry<Metric, MetricValue> property : quality.getProperties().entrySet()) {
                    JSONObject jsonPropertyDescription = new JSONObject();
                    jsonPropertyDescription.put("property", property.getKey().getName() + " [" + property.getKey().getMeasurementUnit() + "]");
                    jsonPropertyDescription.put("value", property.getValue().getValueRepresentation());
                    qualityProperties.add(jsonPropertyDescription);
                }
                qualityDescription.put("properties", qualityProperties);
                array.add(qualityDescription);
            }

            if (!array.isEmpty()) {
                jsonDescription.put("optionalQualitiesRecommended", array);
            }
        }

        //resource options
        {
            JSONArray array = new JSONArray();
            for (Resource resource : chosenResourceOptions) {

                JSONObject qualityDescription = new JSONObject();
                jsonDescription.put("resource", resource.getName());
                JSONArray qualityProperties = new JSONArray();

                for (Map.Entry<Metric, MetricValue> property : resource.getProperties().entrySet()) {
                    JSONObject jsonPropertyDescription = new JSONObject();
                    jsonPropertyDescription.put("property", property.getKey().getName() + " [" + property.getKey().getMeasurementUnit() + "]");
                    jsonPropertyDescription.put("value", property.getValue().getValueRepresentation());
                    qualityProperties.add(jsonPropertyDescription);
                }
                qualityDescription.put("resources", qualityProperties);
                array.add(qualityDescription);
            }

            if (!array.isEmpty()) {
                jsonDescription.put("optionalResourcesRecommended", array);
            }
        }

        //mandatory service units
        {
            JSONArray array = new JSONArray();
            for (ServiceUnitConfigurationSolution configurationSolution : mandatoryAssociatedServiceUnits) {
                array.add(configurationSolution.toJSON());
            }
            if (!array.isEmpty()) {
                jsonDescription.put("mandatoryAssociatedServiceUnitsRecommendedConfigurations", array);
            }
        }

        //optional service units
        {
            JSONArray array = new JSONArray();
            for (ServiceUnitConfigurationSolution configurationSolution : optionallyAssociatedServiceUnits) {
                array.add(configurationSolution.toJSON());
            }
            if (!array.isEmpty()) {
                jsonDescription.put("optionalAssociatedServiceUnitsRecommendedConfigurations", array);
            }
        }

        return jsonDescription;
    }

    public void addCostFunctions(List<CostFunction> costFunctions) {
        this.costFunctions.addAll(costFunctions);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.serviceUnit != null ? this.serviceUnit.hashCode() : 0);
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
        final CloudServiceConfigurationRecommendation other = (CloudServiceConfigurationRecommendation) obj;
        return serviceUnit.getName().equals(other.serviceUnit.getName());
    }
}
