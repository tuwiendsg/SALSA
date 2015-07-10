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
package at.ac.tuwien.dsg.quelle.elasticityQuantification.requirements;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CostFunction;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Quality;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Resource;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.engines.RequirementsMatchingEngine;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.engines.RequirementsMatchingEngine.RequirementsMatchingReport;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Metric;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MetricValue;
import at.ac.tuwien.dsg.mela.common.requirements.Requirement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
public class ServiceUnitConfigurationSolution {

    //unit for which we have the options and connected units
    private CloudOfferedService serviceUnit;
    private List<RequirementsMatchingEngine.RequirementsMatchingReport<Quality>> chosenQualityOptions;
    private List<RequirementsMatchingEngine.RequirementsMatchingReport<Resource>> chosenResourceOptions;
    private List<Requirement> overallMatched;
    private List<Requirement> overallUnMatched;
    private List<ServiceUnitConfigurationSolution> mandatoryAssociatedServiceUnits;
    private List<ServiceUnitConfigurationSolution> optionallyAssociatedServiceUnits;
    private List<CostFunction> costFunctions;

    {
        chosenQualityOptions = new ArrayList<RequirementsMatchingEngine.RequirementsMatchingReport<Quality>>();
        chosenResourceOptions = new ArrayList<RequirementsMatchingEngine.RequirementsMatchingReport<Resource>>();
        mandatoryAssociatedServiceUnits = new ArrayList<ServiceUnitConfigurationSolution>();
        optionallyAssociatedServiceUnits = new ArrayList<ServiceUnitConfigurationSolution>();
        costFunctions = new ArrayList<CostFunction>();
    }

    public void removeQualityOption(RequirementsMatchingEngine.RequirementsMatchingReport<Quality> su) {
        chosenQualityOptions.remove(su);
    }

    public void addQualityOption(RequirementsMatchingEngine.RequirementsMatchingReport<Quality> su) {
        chosenQualityOptions.add(su);
    }

    public void removeResourceOption(RequirementsMatchingEngine.RequirementsMatchingReport<Resource> su) {
        chosenResourceOptions.remove(su);
    }

    public void addResourceOption(RequirementsMatchingEngine.RequirementsMatchingReport<Resource> su) {
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

    public List<RequirementsMatchingReport<Quality>> getChosenQualityOptions() {
        return chosenQualityOptions;
    }

    public void setChosenQualityOptions(List<RequirementsMatchingReport<Quality>> chosenQualityOptions) {
        this.chosenQualityOptions = chosenQualityOptions;
    }

    public List<RequirementsMatchingReport<Resource>> getChosenResourceOptions() {
        return chosenResourceOptions;
    }

    public void setChosenResourceOptions(List<RequirementsMatchingReport<Resource>> chosenResourceOptions) {
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

    public JSONObject toJSON() {
        JSONObject jsonDescription = new JSONObject();
        jsonDescription.put("serviceUnit", serviceUnit.getName());

        //quality options
        {
            JSONArray array = new JSONArray();
            for (RequirementsMatchingEngine.RequirementsMatchingReport<Quality> qualityReport : chosenQualityOptions) {

                Quality quality = qualityReport.getConcreteConfiguration();
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
            for (RequirementsMatchingEngine.RequirementsMatchingReport<Resource> qualityReport : chosenResourceOptions) {

                Resource quality = qualityReport.getConcreteConfiguration();
                JSONObject qualityDescription = new JSONObject();
                jsonDescription.put("resource", quality.getName());
                JSONArray qualityProperties = new JSONArray();

                for (Map.Entry<Metric, MetricValue> property : quality.getProperties().entrySet()) {
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
        final ServiceUnitConfigurationSolution other = (ServiceUnitConfigurationSolution) obj;
        return serviceUnit.getName().equals(other.serviceUnit.getName());
    }
    
    
}
