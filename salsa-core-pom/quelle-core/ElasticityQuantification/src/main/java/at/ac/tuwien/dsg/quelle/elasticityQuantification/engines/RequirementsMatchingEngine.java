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
package at.ac.tuwien.dsg.quelle.elasticityQuantification.engines;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudProvider;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Unit;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Quality;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Resource;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.requirements.RequirementsResolutionResult;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Metric;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MetricValue;
import at.ac.tuwien.dsg.mela.common.requirements.Condition;
import at.ac.tuwien.dsg.mela.common.requirements.Requirement;
import at.ac.tuwien.dsg.mela.common.requirements.Requirements;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * matches ServiceUnits with Requirements
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@Service
public class RequirementsMatchingEngine {

    static final Logger log = LoggerFactory.getLogger(RequirementsMatchingEngine.class);

    /**
     * 
     * @param cloudProviders completely described cloud providers, with units
     * @param requirements
     * @return 
     */
    public RequirementsResolutionResult analyzeMultiLevelRequirements(List<CloudProvider> cloudProviders, MultiLevelRequirements requirements) {
        //get all service units from the supplied cloud providers and search for match between them
        List<CloudOfferedService> serviceUnits = new ArrayList<CloudOfferedService>();
        for (CloudProvider cloudProvider : cloudProviders) {
//            CloudProvider retrieved = CloudProviderDAO.searchForCloudProvidersUniqueResult(cloudProvider);
            if (cloudProvider != null) {
                serviceUnits.addAll(cloudProvider.getCloudOfferedServices());
            } else {
                log.warn("Retrieved CloudProvider " + cloudProvider + " has no units");
            }
        }

        //match them all in turn
        RequirementsResolutionResult requirementsResolutionResult = new RequirementsResolutionResult();

        for (MultiLevelRequirements multiLevelRequirements : requirements) {
            Collection<Requirements> requirementsGroups = multiLevelRequirements.getUnitRequirements();

            for (Requirements r : requirementsGroups) {
                for (CloudOfferedService serviceUnit : serviceUnits) {
                    ServiceUnitOptions option = analyzeServiceUnitMatching(serviceUnit, r);
                    if (option.getOverallMatched().size() > 0) {
                        requirementsResolutionResult.addMatchedOption(multiLevelRequirements, r, option);
                    }
                }
            }
        }
        return requirementsResolutionResult;
    }

    /**
     * Tries to match as many requirements on a single ServiceUnit. If the unit
     * can have multiple Optional_Associations to Quality, Resource, each
     * possible association is inspected, and the SMALLEST (in terms of
     * quality/resources) that matches the requirements is chosen (intuitively
     * this should be the cheapest) in a greedy manner. Then, for all the
     * MANDATORY service unit associations, their concrete configurations are
     * chosen, and the number of requirements that they match also reported
     *
     * 1 Requirements group means that it should be instantiated on one cloud
     * offered ServiceUnit
     *
     * @param unitToMatch
     * @param requirements
     * @return
     */
    public ServiceUnitOptions analyzeServiceUnitMatching(CloudOfferedService unitToMatch, Requirements requirements) {

        //hold matched requirements by type
        Map<Metric.MetricType, List<Requirement>> matchedRequirementsMap = new EnumMap<Metric.MetricType, List<Requirement>>(Metric.MetricType.class);
        matchedRequirementsMap.put(Metric.MetricType.RESOURCE, new ArrayList<Requirement>());
        matchedRequirementsMap.put(Metric.MetricType.COST, new ArrayList<Requirement>());
        matchedRequirementsMap.put(Metric.MetricType.QUALITY, new ArrayList<Requirement>());
        matchedRequirementsMap.put(Metric.MetricType.ELASTICITY, new ArrayList<Requirement>());

        ServiceUnitOptions serviceUnitOptions = new ServiceUnitOptions(unitToMatch);
//        serviceUnitOptions.setMatchedRequirementsByServiceUnit(matchedRequirementsMap);
        serviceUnitOptions.setOverallUnMatched(new ArrayList<Requirement>(requirements.getRequirements()));

        if (requirements == null || unitToMatch == null) {
            return serviceUnitOptions;
        }

        //1 split requirements by type: Cost, Quality, Resource, Elasticity
        Map<Metric.MetricType, List<Requirement>> requirementsMapByType = new EnumMap<Metric.MetricType, List<Requirement>>(Metric.MetricType.class);

        for (Requirement requirement : requirements.getRequirements()) {
            Metric.MetricType requirementType = requirement.getMetric().getType();
            if (requirementsMapByType.containsKey(requirementType)) {
                requirementsMapByType.get(requirementType).add(requirement);
            } else {
                List<Requirement> list = new ArrayList<Requirement>();
                list.add(requirement);
                requirementsMapByType.put(requirementType, list);
            }
        }

        //2 match requirements 
        //2.1 match Resource requirements
        if (requirementsMapByType.containsKey(Metric.MetricType.RESOURCE)) {
            List<Requirement> matchedRequirements = matchedRequirementsMap.get(Metric.MetricType.RESOURCE);

            List<Requirement> resourceRequirements = requirementsMapByType.get(Metric.MetricType.RESOURCE);
            //2.1.1 match requirements on fixed resources
            for (Resource resource : unitToMatch.getResourceProperties()) {
                Map<Metric, MetricValue> resourceProperties = resource.getProperties();
                List<Requirement> requirementsMatchedForThisResource = matchRequirementsToProperties(resourceProperties, resourceRequirements);
                resourceRequirements.removeAll(requirementsMatchedForThisResource);
                matchedRequirements.addAll(requirementsMatchedForThisResource);
                serviceUnitOptions.addMatchedRequirements(requirementsMatchedForThisResource);
            }

            //the rest of the unmatched requirements are matched on the optional resources, 
            //and the optional resource which matches the most requirements is selected
            //I need set cover here
            //2.1.2 match requirements on optional resources
            //first on mandatory reqs
            for (ElasticityCapability optionalResourceCapability : unitToMatch.getResourceAssociations()) {
                //get optional resources BECAUSE it is more tricky to get them
                //the  unitToMatch.getOptionalResourceAssociations() ACTUAllY returns the target of the ElasticityCharacteristic,
                //which is a generic Entity: Example Computing.
                //The ResourceDAO returns example Computing x64, Computing x86
//                List<Resource> optionalResourcesOptions = ResourceDAO.geResourceOptionsForServiceUnitNode(unitToMatch.getId(), optionalResource.getId());
                //now match the remaining requirements on these options and sort them after how many req they fulfill
                Set<RequirementsMatchingReport<Resource>> matchingReports = matchOptionalResourceConfiguration(optionalResourceCapability.getMandatoryDependencies(), resourceRequirements);
                if (!matchingReports.isEmpty()) {
                    serviceUnitOptions.addResourceOptions(optionalResourceCapability, matchingReports);

                    //remove the requirements matched by the LARGEST match
                    //get iterator next as the first in the matchingReports must be the BEST match (sorted in decreasing nr of matched policies)
                    List<Requirement> matched = matchingReports.iterator().next().matchedRequirements.get(Metric.MetricType.RESOURCE);
                    serviceUnitOptions.addMatchedRequirements(matched);
                    resourceRequirements.removeAll(matched);
                }
            }

            for (ElasticityCapability optionalResourceCapability : unitToMatch.getResourceAssociations()) {
                //get optional resources BECAUSE it is more tricky to get them
                //the  unitToMatch.getOptionalResourceAssociations() ACTUAllY returns the target of the ElasticityCharacteristic,
                //which is a generic Entity: Example Computing.
                //The ResourceDAO returns example Computing x64, Computing x86
//                List<Resource> optionalResourcesOptions = ResourceDAO.geResourceOptionsForServiceUnitNode(unitToMatch.getId(), optionalResource.getId());
                //now match the remaining requirements on these options and sort them after how many req they fulfill
                Set<RequirementsMatchingReport<Resource>> matchingReports = matchOptionalResourceConfiguration(optionalResourceCapability.getOptionalDependencies(), resourceRequirements);
                if (!matchingReports.isEmpty()) {
                    serviceUnitOptions.addResourceOptions(optionalResourceCapability, matchingReports);

                    //remove the requirements matched by the LARGEST match
                    //get iterator next as the first in the matchingReports must be the BEST match (sorted in decreasing nr of matched policies)
                    List<Requirement> matched = matchingReports.iterator().next().matchedRequirements.get(Metric.MetricType.RESOURCE);
                    serviceUnitOptions.addMatchedRequirements(matched);
                    resourceRequirements.removeAll(matched);
                }
            }
        }

        //2.2 match Quality requirements
        if (requirementsMapByType.containsKey(Metric.MetricType.QUALITY)) {
            List<Requirement> matchedRequirements = matchedRequirementsMap.get(Metric.MetricType.QUALITY);

            List<Requirement> qualityRequirements = requirementsMapByType.get(Metric.MetricType.QUALITY);
            //2.2.1 match requirements on fixed Quality
            for (Quality quality : unitToMatch.getQualityProperties()) {
                Map<Metric, MetricValue> properties = quality.getProperties();
                List<Requirement> requirementsMatchedForThisResource = matchRequirementsToProperties(properties, qualityRequirements);
                qualityRequirements.removeAll(requirementsMatchedForThisResource);
                matchedRequirements.addAll(requirementsMatchedForThisResource);
                serviceUnitOptions.addMatchedRequirements(requirementsMatchedForThisResource);
            }

            //the rest of the unmatched requirements are matched on the optional resources, 
            //and the optional resource which matches the most requirements is selected
            //I need set cover here
            //2.2.2 match requirements on optional Quality
            //first on mandatory quality
            for (ElasticityCapability optionalQualityCapability : unitToMatch.getQualityAssociations()) {
                //now match the remaining requirements on these options and sort them after how many req they fulfill
                Set<RequirementsMatchingReport<Quality>> matchingReports = matchOptionalQualityConfiguration(optionalQualityCapability.getMandatoryDependencies(), qualityRequirements);
                if (!matchingReports.isEmpty()) {
                    serviceUnitOptions.addQualityOptions(optionalQualityCapability, matchingReports);

                    //remove the requirements matched by the LARGEST match
                    List<Requirement> matched = matchingReports.iterator().next().matchedRequirements.get(Metric.MetricType.QUALITY);
                    serviceUnitOptions.addMatchedRequirements(matched);
                    qualityRequirements.removeAll(matched);
                }
            }

            for (ElasticityCapability optionalQualityCapability : unitToMatch.getQualityAssociations()) {
                //now match the remaining requirements on these options and sort them after how many req they fulfill
                Set<RequirementsMatchingReport<Quality>> matchingReports = matchOptionalQualityConfiguration(optionalQualityCapability.getOptionalDependencies(), qualityRequirements);
                if (!matchingReports.isEmpty()) {
                    serviceUnitOptions.addQualityOptions(optionalQualityCapability, matchingReports);

                    //remove the requirements matched by the LARGEST match
                    List<Requirement> matched = matchingReports.iterator().next().matchedRequirements.get(Metric.MetricType.QUALITY);
                    serviceUnitOptions.addMatchedRequirements(matched);
                    qualityRequirements.removeAll(matched);
                }
            }
        }
        //2.3 Get all MANDATORY association ServiceUnits and check how much do they fill requirements.
        for (ElasticityCapability serviceUnitElasticityCapability : unitToMatch.getServiceUnitAssociations()) {
            Requirements r = new Requirements();
            r.setRequirements(serviceUnitOptions.getOverallUnMatched());
            for (Unit entity : serviceUnitElasticityCapability.getMandatoryDependencies()) {
                ServiceUnitOptions options = analyzeServiceUnitMatching((CloudOfferedService) entity, r);
                if (options != null) {
                    serviceUnitOptions.addMandatoryServiceUnitRaport(options);
                }
            }
        }
//        2.4 The requirements left unmatched are matched with OptionalServiceUnit Associations
        //thus, for example, if one requires a VM with X resources, and Y IOperformance, 
        //it could get a VM which optionally could have EBS,
        if (!serviceUnitOptions.getOverallUnMatched().isEmpty()) {
            for (ElasticityCapability optionalServiceUnitElasticityCapability : unitToMatch.getServiceUnitAssociations()) {
                Requirements r = new Requirements();
                r.setRequirements(serviceUnitOptions.getOverallUnMatched());
                //go and analyze each optional target from the elasticity stuff. not good though. I need to select one or the other? 
                //TODO: structure reports after ElasticityCapabilities. Example: for capability A we have Reports for units B,C,D
                for (Unit entity : optionalServiceUnitElasticityCapability.getOptionalDependencies()) {
                    ServiceUnitOptions options = analyzeServiceUnitMatching((CloudOfferedService) entity, r);
                    if (options != null) {
                        serviceUnitOptions.addOptionalServiceUnitRaport(options);
                    }
                }
            }
        }
//        //2.5 match Cost requirements (cost needs to also include the cost of the MANDATORY associations
//        //TODO: to be implemented (Continue)
//        match to cost properties
//        if (requirementsMapByType.containsKey(Metric.MetricType.COST)) {
//            List<Requirement> matchedRequirements = matchedRequirementsMap.get(Metric.MetricType.COST);
//            List<Requirement> costRequirements = requirementsMapByType.get(Metric.MetricType.COST);
//
//            List<CostFunction> costFunctions = unitToMatch.getCostFunctions();
//
//            for (CostFunction costFunction : unitToMatch.getCostFunctions()) {
//                for (CostElement element : costFunction.getCostElements()) {
//                    Metric costMetric = element.getCostMetric();
//                    Map<MetricValue, Double> properties = element.getCostIntervalFunction();
//
//
//                    List<Requirement> requirementsMatchedForThisResource = new ArrayList<Requirement>();
//
//                    Iterator<Requirement> requirementsIterator = costRequirements.iterator();
//                    while (requirementsIterator.hasNext()) {
//                        Requirement requirement = requirementsIterator.next();
//
//                        Metric requirementMetric = requirement.getMetric();
//                        if (costMetric.equals(requirementMetric)) {
//                            boolean respectsAllConditions = true;
//
//                            for (Condition condition : requirement.getConditions()) {
//                                //consider we only evaluate cost, not interval
//                                boolean conditionIsRespected = true;
//                                for (Double d : properties.values()) {
//                                    MetricValue costValue = new MetricValue(d);
//                                    if (condition.isRespectedByValue(costValue)) {
//                                        conditionIsRespected = true;
//                                        break;
//                                    }
//                                }
//                                if (!conditionIsRespected) {
//                                    respectsAllConditions = false;
//                                    break;
//                                }
//                            }
//                            if (respectsAllConditions) {
//                                //add requirement to matched requirements list
//                                requirementsMatchedForThisResource.add(requirement);
//                                //remove requirement so it is not matched again in the next searches
//                                requirementsIterator.remove();
//                            }
//                        } else {
//                            continue;
//                        }
//
//                    }
//
//                    costRequirements.removeAll(requirementsMatchedForThisResource);
//                    matchedRequirements.addAll(requirementsMatchedForThisResource);
//                    serviceUnitOptions.addMatchedRequirements(requirementsMatchedForThisResource);
//                }
//            }
//        }
//
//            //TODO: to process also cost to apply an utility in conjunction with another
//            if (!serviceUnitOptions.getOverallUnMatched().isEmpty()) {
//                //here I must check and apply it for quality, resource or ServiceUnit
//
//                for (CostFunction costFunction : unitToMatch.getCostFunctions()) {
//                    for (Quality quality : costFunction.getAppliedInConjunctionWithQuality()) {
//                        Map<Metric, MetricValue> properties = quality.getProperties();
//                        List<Requirement> requirementsMatchedForThisResource = matchRequirementsToProperties(properties, qualityRequirements);
//                        qualityRequirements.removeAll(requirementsMatchedForThisResource);
//                        matchedRequirements.addAll(requirementsMatchedForThisResource);
//                        serviceUnitOptions.addMatchedRequirements(requirementsMatchedForThisResource);
//                    }
//                }
//
//        }

        //2.6 match Elasticity requirements (also need to consider the MANDATORY associations, which reduce the elasticity)
        //TODO: to be implemented
        return serviceUnitOptions;
    }

    /**
     * Does set cover to match any optional configuration. Sorts the optional
     * elements after the number of matched properties
     *
     * @param unit
     * @param requirementsToMatch
     * @return
     */
    private Set<RequirementsMatchingReport<Resource>> matchOptionalResourceConfiguration(List<Unit> optionalConfiguration, List<Requirement> requirementsToMatch) {
        //the results will be sorted after the nr of matched stuff
        Set<RequirementsMatchingReport<Resource>> matchedReportSet = new TreeSet<RequirementsMatchingReport<Resource>>(new Comparator<RequirementsMatchingReport>() {
            public int compare(RequirementsMatchingReport o1, RequirementsMatchingReport o2) {
                Integer matched1 = o1.getMatchedResourceCountForMetricType(Metric.MetricType.RESOURCE);
                Integer matched2 = o2.getMatchedResourceCountForMetricType(Metric.MetricType.RESOURCE);
                return -1 * matched1.compareTo(matched2); //multiplied by -1 to have the largest number first
            }
        });

        //1 go trough each optional Resource and match their requirements
        for (Unit entity : optionalConfiguration) {
            if (!(entity instanceof Resource)) {
                continue;
            } else {
                Resource resource = (Resource) entity;
                Map<Metric, MetricValue> resourceProperties = resource.getProperties();
                //match as many requirements to resource properties as possible
                List<Requirement> requirementsMatchedForThisResource = matchRequirementsToProperties(resourceProperties, requirementsToMatch);
                //create the report entry 

                if (requirementsMatchedForThisResource.size() > 0) {
                    Map<Metric.MetricType, List<Requirement>> matchedRequirementsMap = new EnumMap<Metric.MetricType, List<Requirement>>(Metric.MetricType.class);
                    matchedRequirementsMap.put(Metric.MetricType.RESOURCE, requirementsMatchedForThisResource);
                    RequirementsMatchingReport<Resource> matchingReport = new RequirementsMatchingReport<Resource>(matchedRequirementsMap, resource);

                    //add report entry to the report
                    matchedReportSet.add(matchingReport);
                }
            }
        }

        return matchedReportSet;

    }

    private Set<RequirementsMatchingReport<Quality>> matchOptionalQualityConfiguration(List<Unit> optionalConfiguration, List<Requirement> requirementsToMatch) {
        //the results will be sorted after the nr of matched stuff
        Set<RequirementsMatchingReport<Quality>> matchedReportSet = new TreeSet<RequirementsMatchingReport<Quality>>(new Comparator<RequirementsMatchingReport>() {
            public int compare(RequirementsMatchingReport o1, RequirementsMatchingReport o2) {
                Integer matched1 = o1.getMatchedResourceCountForMetricType(Metric.MetricType.QUALITY);
                Integer matched2 = o2.getMatchedResourceCountForMetricType(Metric.MetricType.QUALITY);
                return -1 * matched1.compareTo(matched2); //multiplied by -1 to have the largest number first
            }
        });

        //1 go trough each optional Resource and match their requirements
        for (Unit entity : optionalConfiguration) {
            if (!(entity instanceof Quality)) {
                continue;
            } else {
                Quality quality = (Quality) entity;
                Map<Metric, MetricValue> resourceProperties = quality.getProperties();
                //match as many requirements to resource properties as possible
                List<Requirement> requirementsMatchedForThisResource = matchRequirementsToProperties(resourceProperties, requirementsToMatch);

                if (requirementsMatchedForThisResource.size() > 0) {
                    //create the report entry 
                    Map<Metric.MetricType, List<Requirement>> matchedRequirementsMap = new EnumMap<Metric.MetricType, List<Requirement>>(Metric.MetricType.class);
                    matchedRequirementsMap.put(Metric.MetricType.QUALITY, requirementsMatchedForThisResource);
                    RequirementsMatchingReport<Quality> matchingReport = new RequirementsMatchingReport<Quality>(matchedRequirementsMap, quality);

                    //add report entry to the report
                    matchedReportSet.add(matchingReport);
                }
            }
        }

        return matchedReportSet;

    }
//
//    /**
//     * Evaluates the cost for each ServiceUnitOptions object, as it needs to
//     * consider
//     *
//     * @param optionsToEvaluate
//     * @param optionalConfiguration
//     * @param requirementsToMatch
//     * @return
//     */
//    private  Set<RequirementsMatchingReport<CostFunction>> matchOptionalCostFunctionConfiguration(ServiceUnitOptions optionsToEvaluate, List<CostFunction> optionalConfiguration, List<Requirement> requirementsToMatch) {
////        //the results will be sorted after the nr of matched stuff
//        Set<RequirementsMatchingReport<CostFunction>> matchedReportSet = new TreeSet<RequirementsMatchingReport<CostFunction>>(new Comparator<RequirementsMatchingReport>() {
//            public int compare(RequirementsMatchingReport o1, RequirementsMatchingReport o2) {
//                Integer matched1 = o1.getMatchedResourceCountForMetricType(Metric.MetricType.COST);
//                Integer matched2 = o2.getMatchedResourceCountForMetricType(Metric.MetricType.COST);
//                return -1 * matched1.compareTo(matched2); //multiplied by -1 to have the largest number first
//            }
//        });
//
//
//        //1 go trough each optional CostFunction and prune the options
//        for (CostFunction costFunction : optionalConfiguration) {
//            if
//            
//            List<Quality> appliedInConjunctionWithQuality = costFunction.getAppliedInConjunctionWithQuality();
//            List<Resource> appliedInConjunctionWithResource = costFunction.getAppliedInConjunctionWithResource();
//            List<ServiceUnit> appliedInConjunctionWithServiceUnit = costFunction.getAppliedInConjunctionWithServiceUnit();
//            
//            
//            
//            
//            if () {
//                for (CostElement costElement : costFunction.getCostElements()) {
//                    costElement.
//            
//                }
//            }
//            Map<Metric, MetricValue> resourceProperties = resource.getProperties();
//            //match as many requirements to resource properties as possible
//            List<Requirement> requirementsMatchedForThisResource = matchRequirementsToProperties(resourceProperties, requirementsToMatch);
//            //create the report entry 
//            Map<Metric.MetricType, List<Requirement>> matchedRequirementsMap = new EnumMap<Metric.MetricType, List<Requirement>>(Metric.MetricType.class);
//            matchedRequirementsMap.put(Metric.MetricType.RESOURCE, requirementsMatchedForThisResource);
//            RequirementsMatchingReport<Resource> matchingReport = new RequirementsMatchingReport<Resource>(matchedRequirementsMap, resource);
//
//            //add report entry to the report
//            matchedReportSet.add(matchingReport);
//
//        }
//
//        return matchedReportSet;
//
//
//
//    }

    /**
     *
     * @param propertiesToMatch
     * @param requirementsToMatch
     * @return list of matched requirements
     */
    private List<Requirement> matchRequirementsToProperties(Map<Metric, MetricValue> propertiesToMatch, List<Requirement> requirementsToMatch) {
        List<Requirement> matchedRequirements = new ArrayList<Requirement>();

        //used to tarverse the requirements list and remove the allready matched ones
        List<Requirement> requirements = new ArrayList<Requirement>(requirementsToMatch);

        Iterator<Requirement> requirementsIterator = requirements.iterator();
        while (requirementsIterator.hasNext()) {
            Requirement requirement = requirementsIterator.next();
            Metric requirementMetric = requirement.getMetric();

            if (propertiesToMatch.containsKey(requirementMetric)) {
                MetricValue resourcePropertyValue = propertiesToMatch.get(requirementMetric);
                boolean respectsAllConditions = true;

                for (Condition condition : requirement.getConditions()) {
                    if (!condition.isRespectedByValue(resourcePropertyValue)) {
                        respectsAllConditions = false;
                        break;
                    }
                }
                if (respectsAllConditions) {
                    //add requirement to matched requirements list
                    matchedRequirements.add(requirement);
                    //remove requirement so it is not matched again in the next searches
                    requirementsIterator.remove();
                }
            }
        }
        return matchedRequirements;

    }

    public static class RequirementsMatchingReport<T> {

        //requirements matched by type
        private Map<Metric.MetricType, List<Requirement>> matchedRequirements;
        private T matchedElement;

        public RequirementsMatchingReport() {}
        
        public RequirementsMatchingReport(Map<Metric.MetricType, List<Requirement>> matchedRequirements, T matchedElement) {
            this.matchedRequirements = matchedRequirements;
            this.matchedElement = matchedElement;
        }

        public Map<Metric.MetricType, List<Requirement>> getMatchedRequirements() {
            return matchedRequirements;
        }

        public T getConcreteConfiguration() {
            return matchedElement;
        }

        public Integer getMatchedResourceCountForMetricType(Metric.MetricType metricType) {
            if (this.matchedRequirements.containsKey(metricType)) {
                return this.matchedRequirements.get(metricType).size();
            } else {
                return 0;
            }
        }

        @Override
        public String toString() {
            String description = "RequirementsMatchingReport:\n";
            for (Metric.MetricType key : matchedRequirements.keySet()) {
                description += "For requirements: " + key + " matched " + matchedRequirements.get(key).size() + "\n";
            }

            //apoi optional configs nu ai de unde sa stii
            description += matchedElement.toString();
            return description;
        }
    }
}
