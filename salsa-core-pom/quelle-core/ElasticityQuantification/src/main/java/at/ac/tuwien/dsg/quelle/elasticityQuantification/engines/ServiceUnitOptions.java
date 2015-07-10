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
package at.ac.tuwien.dsg.quelle.elasticityQuantification.engines;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CostFunction;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability.Dependency;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Quality;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Resource;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.engines.RequirementsMatchingEngine.RequirementsMatchingReport;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.requirements.ServiceUnitConfigurationSolution;
import at.ac.tuwien.dsg.mela.common.requirements.Requirement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
public class ServiceUnitOptions {

    //unit for which we have the options and connected units
    private CloudOfferedService serviceUnit;
    //requirements matched for serviceUnit (DOES NOT contain req matched by optional or mandatory elements)

    //structured weird. This actually means: for Quality X, Set of Quality instantiation options
    //so it actually supports more quality types per service unit. Similar with Resource. 
    private Map<ElasticityCapability, Set<RequirementsMatchingEngine.RequirementsMatchingReport<Quality>>> optionalQualities;
    private Map<ElasticityCapability, Set<RequirementsMatchingEngine.RequirementsMatchingReport<Resource>>> optionalResources;
    private Map<ElasticityCapability, Set<RequirementsMatchingEngine.RequirementsMatchingReport<CostFunction>>> optionalCost;
    private List<ServiceUnitOptions> mandatoryConnectedServiceUnitsReport;
    //contains service units which IF combined with the serviceUnit fulfill more requirements than just the serviceUnit
    private List<ServiceUnitOptions> optionallyConnectedServiceUnitsReport;
    //now it holds cost functions which target optional or mandatory connected service units
    //also take in consideration the optional elements and mandatory
    private List<Requirement> overallMatched;
    private List<Requirement> overallUnMatched;

    {
        optionalQualities = new HashMap<>();
        optionalResources = new HashMap<>();
        optionalCost = new HashMap<>();
        mandatoryConnectedServiceUnitsReport = new ArrayList<>();
        optionallyConnectedServiceUnitsReport = new ArrayList<>();
        overallMatched = new ArrayList<>();
        overallUnMatched = new ArrayList<>();

    }

    public ServiceUnitOptions(CloudOfferedService serviceUnit) {
        this.serviceUnit = serviceUnit;
    }

    public CloudOfferedService getServiceUnit() {
        return serviceUnit;
    }

    public Map<ElasticityCapability, Set<RequirementsMatchingReport<Quality>>> getOptionalQualities() {
        return optionalQualities;
    }

    public Map<ElasticityCapability, Set<RequirementsMatchingReport<Resource>>> getOptionalResources() {
        return optionalResources;
    }

    public List<ServiceUnitOptions> getMandatoryConnectedServiceUnitsReport() {
        return mandatoryConnectedServiceUnitsReport;
    }

    public void addResourceOptions(ElasticityCapability r, Set<RequirementsMatchingEngine.RequirementsMatchingReport<Resource>> report) {
        optionalResources.put(r, report);
    }

    public void removeResourceOptions(ElasticityCapability r) {
        optionalResources.remove(r);
    }

    public void addQualityOptions(ElasticityCapability r, Set<RequirementsMatchingEngine.RequirementsMatchingReport<Quality>> report) {
        optionalQualities.put(r, report);
    }

    public List<Quality> getSolutionQualities() {
        List<Quality> list = new ArrayList<>();

        for (Set<RequirementsMatchingEngine.RequirementsMatchingReport<Quality>> set : optionalQualities.values()) {
            for (RequirementsMatchingReport<Quality> report : set) {
                list.add(report.getConcreteConfiguration());
            }
        }

        return list;
    }

    public List<Resource> getSolutionResources() {
        List<Resource> list = new ArrayList<>();

        for (Set<RequirementsMatchingEngine.RequirementsMatchingReport<Resource>> set : optionalResources.values()) {
            for (RequirementsMatchingReport<Resource> report : set) {
                list.add(report.getConcreteConfiguration());
            }
        }

        return list;
    }

    public List<CloudOfferedService> getSolutionServiceUnits() {
        List<CloudOfferedService> list = new ArrayList<>();

        for (ServiceUnitOptions option : mandatoryConnectedServiceUnitsReport) {
            list.add(option.getServiceUnit());
        }

        for (ServiceUnitOptions option : optionallyConnectedServiceUnitsReport) {
            list.add(option.getServiceUnit());
        }

        return list;
    }

    public void removeQualityOptions(ElasticityCapability r) {
        optionalQualities.remove(r);
    }

    public void addMandatoryServiceUnitRaport(ServiceUnitOptions su) {
        mandatoryConnectedServiceUnitsReport.add(su);
        overallMatched.addAll(su.overallMatched);
        overallUnMatched.removeAll(su.overallMatched);
    }

    public void removeChosenServiceUnit(ServiceUnitOptions su) {
        mandatoryConnectedServiceUnitsReport.remove(su);
    }

    public void addOptionalServiceUnitRaport(ServiceUnitOptions su) {
        optionallyConnectedServiceUnitsReport.add(su);
        overallMatched.addAll(su.overallMatched);
        overallUnMatched.removeAll(su.overallMatched);
    }

    public void removeOptionalServiceUnit(ServiceUnitOptions su) {
        optionallyConnectedServiceUnitsReport.remove(su);
    }

    public List<ServiceUnitOptions> getOptionallyConnectedServiceUnitsReport() {
        return optionallyConnectedServiceUnitsReport;
    }

//    public Map<MetricType, List<Requirement>> getMatchedRequirementsByServiceUnit() {
//        return matchedRequirementsByServiceUnit;
//    }
//
//    public void setMatchedRequirementsByServiceUnit(Map<MetricType, List<Requirement>> matchedRequirements) {
//        this.matchedRequirementsByServiceUnit = matchedRequirements;
//    }
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

    /**
     * used to mark that a requirement is matched either by the service unit or
     * by its optional or mandatory components
     *
     * @param r
     */
    public void addMatchedRequirement(Requirement r) {
        if (overallUnMatched.contains(r)) {
            overallUnMatched.remove(r);
        }
        overallMatched.add(r);
    }

    public void addMatchedRequirements(List<Requirement> r) {
        overallUnMatched.removeAll(r);
        overallMatched.addAll(r);
    }

    public ServiceUnitConfigurationSolution applyStrategy(final List<Comparator<ServiceUnitOptions>> strategiesComparators) {
        ServiceUnitConfigurationSolution solution = new ServiceUnitConfigurationSolution();
        solution.setServiceUnit(serviceUnit);
        solution.setOverallMatched(overallMatched);
        solution.setOverallUnMatched(overallUnMatched);

        //TODO: here I would need to do Set Cover to determine which to choose. Currently I am choosing the best
        if (!optionallyConnectedServiceUnitsReport.isEmpty()) {
            Collections.sort(optionallyConnectedServiceUnitsReport, new Comparator<ServiceUnitOptions>() {
                public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
                    //get an iterator over the strategies comparators
                    //to do : apply iterator.next, if equals with 0, apply next iterator.
                    final Iterator<Comparator<ServiceUnitOptions>> strategiesIterator = strategiesComparators.iterator();
                    //if no strategy, just leave them unsorted 
                    int result = 0;

                    //while we have strategiea and the LAST result of the comparator was 0,
                    //continue and discriminate with next comparator. Else, return result.
                    while (strategiesIterator.hasNext() && result == 0) {
                        result = strategiesIterator.next().compare(o1, o2);
                    }
                    return result;
                }
            });

            //also apply same strategy to the optionallyConnected Service Unit
            if (!optionallyConnectedServiceUnitsReport.get(0).getOverallMatched().isEmpty()) {
                solution.addOptionalServiceUnitRecommendation(optionallyConnectedServiceUnitsReport.get(0).applyStrategy(strategiesComparators));
            }

        }

        for (ServiceUnitOptions mandatoryServiceUnit : mandatoryConnectedServiceUnitsReport) {
            solution.addMandatoryServiceUnitRecommandation(mandatoryServiceUnit.applyStrategy(strategiesComparators));
        }

        for (ElasticityCapability q : optionalQualities.keySet()) {
            //extract first, qhich has highest quality? where Do I know that? 
            //This is how I sort the qualities in the RequirmentsMatchingEngine in "matchOptionalQualityConfiguration"
            //what If I do not need any option? If it does not fulfill any requirements, why do I add it? WTF?
            RequirementsMatchingReport<Quality> report = optionalQualities.get(q).iterator().next();
            if (!report.getMatchedRequirements().isEmpty()) {
                solution.addQualityOption(report);
            }
        }

        for (ElasticityCapability r : optionalResources.keySet()) {
            //extract first, qhich has highest quality
            RequirementsMatchingReport<Resource> report = optionalResources.get(r).iterator().next();
            if (!report.getMatchedRequirements().isEmpty()) {
                solution.addResourceOption(report);
            }
        }

        //go trough all optional stuff, and if find something that is targeted by an optional Cost, choose it. 
        //TODO: currently does not evaluate cost if more cost options are possible
        List<CostFunction> costFunctions = new ArrayList<CostFunction>();
        costFunctions.addAll(serviceUnit.getCostFunctions());

        //add cost functions which have no target
        {

            Iterator<CostFunction> iterator = costFunctions.iterator();
            //ad cost functions which do not have associated with
            while (iterator.hasNext()) {
                CostFunction cf = iterator.next();
                if (cf.getAppliedIfServiceInstanceUses().isEmpty()) {
                    solution.addCostFunction(cf);
                    iterator.remove();
                }
            }
        }
        for (ElasticityCapability costCapability : serviceUnit.getCostAssociations()) {
            for (Dependency d : costCapability.getCapabilityDependencies()) {
                if (d.getDependencyType().equalsIgnoreCase(ElasticityCapability.Type.OPTIONAL_ASSOCIATION)) {
                    costFunctions.add((CostFunction) d.getTarget());
                }
            }
        }

        for (CostFunction cf : costFunctions) {
            List<CloudOfferedService> appliedInConjunctionWith = cf.getAppliedIfServiceInstanceUsesCloudOfferedServices();
            for (ServiceUnitConfigurationSolution optional : solution.getOptionallyAssociatedServiceUnits()) {
                if (appliedInConjunctionWith.contains(optional.getServiceUnit())) {
                    solution.addCostFunction(cf);
                    break;
                }
            }
        }

        //cost for mandatory
        for (CostFunction cf : costFunctions) {
            List<CloudOfferedService> appliedInConjunctionWith = cf.getAppliedIfServiceInstanceUsesCloudOfferedServices();
            for (ServiceUnitConfigurationSolution optional : solution.getMandatoryAssociatedServiceUnits()) {
                if (appliedInConjunctionWith.contains(optional.getServiceUnit())) {
                    solution.addCostFunction(cf);
                    break;
                }
            }
        }

        //cost for optional qualities
        for (CostFunction cf : costFunctions) {
            List<Quality> appliedInConjunctionWith = cf.getAppliedIfServiceInstanceUsesQuality();
            for (RequirementsMatchingReport<Quality> optional : solution.getChosenQualityOptions()) {
                Quality optionalQuality = optional.getConcreteConfiguration();
                for (Quality q : appliedInConjunctionWith) {
                    //TODO: somehow include equals in anme
                    if (q.getName().equals(optionalQuality.getName()) && q.equals(optionalQuality)) {
                        solution.addCostFunction(cf);
                        break;
                    }
                }

            }
        }
        //cost for optional resources
        for (CostFunction cf : costFunctions) {
            List<Resource> appliedInConjunctionWith = cf.getAppliedIfServiceInstanceUsesResource();
            for (RequirementsMatchingReport<Resource> optional : solution.getChosenResourceOptions()) {
                Resource optionalQuality = optional.getConcreteConfiguration();
                for (Resource q : appliedInConjunctionWith) {
                    //TODO: somehow include equals in anme
                    if (q.getName().equals(optionalQuality.getName()) && q.equals(optionalQuality)) {
                        solution.addCostFunction(cf);
                        break;
                    }
                }
            }
        }
        return solution;
    }
//    public ServiceUnitConfigurationSolution getBestElasticity() {
//        ServiceUnitConfigurationSolution solution = new ServiceUnitConfigurationSolution();
//        solution.setServiceUnit(serviceUnit);
//        solution.setOverallMatched(overallMatched);
//        solution.setOverallUnMatched(overallUnMatched);
//
//        //TODO: here I would need to do Set Cover to determine which to choose. Currently I am choosing the best
//        if (!optionallyConnectedServiceUnitsReport.isEmpty()) {
//            Collections.sort(optionallyConnectedServiceUnitsReport, new Comparator<ServiceUnitOptions>() {
//                public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
//                    return ((Integer) CloudServiceUnitAnalysisEngine.analyzeElasticity(o1.getServiceUnit()).getValue(CloudServiceUnitAnalysisEngine.OVERALL_ELASTICITY))
//                            .compareTo(
//                            (Integer) CloudServiceUnitAnalysisEngine.analyzeElasticity(o2.getServiceUnit()).getValue(CloudServiceUnitAnalysisEngine.OVERALL_ELASTICITY));
//                }
//            });
//
//            solution.addServiceUnitOption(optionallyConnectedServiceUnitsReport.get(0).getBestElasticity());
//        }
//
//        for (Quality q : optionalQualities.keySet()) {
//            //extract first, qhich has highest quality
//            solution.addQualityOption(optionalQualities.get(q).iterator().next());
//        }
//
//
//        for (Resource r : optionalResources.keySet()) {
//            //extract first, which has highest quality
//            solution.addResourceOption(optionalResources.get(r).iterator().next());
//        }
//
//
//        return solution;
//    }
//
//    public ServiceUnitConfigurationSolution getBestQuality() {
//        ServiceUnitConfigurationSolution solution = new ServiceUnitConfigurationSolution();
//        solution.setServiceUnit(serviceUnit);
//        solution.setOverallMatched(overallMatched);
//        solution.setOverallUnMatched(overallUnMatched);
//
//        //TODO: here I would need to do Set Cover to determine which to choose. Currently I am choosing the best
//        if (!optionallyConnectedServiceUnitsReport.isEmpty()) {
//            Collections.sort(optionallyConnectedServiceUnitsReport, new Comparator<ServiceUnitOptions>() {
//                public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
//                    Integer firstQualityMatched = 0;
//                    Integer secondQualityMatched = 0;
//
//                    for (Quality q : o1.optionalQualities.keySet()) {
//                        firstQualityMatched += o1.optionalQualities.get(q).iterator().next().getMatchedRequirements().size();
//                    }
//
//
//                    for (Quality q : o2.optionalQualities.keySet()) {
//                        secondQualityMatched += o2.optionalQualities.get(q).iterator().next().getMatchedRequirements().size();
//                    }
//
//                    return -1 * (firstQualityMatched.compareTo(secondQualityMatched));
//
//                }
//            });
//
//            solution.addServiceUnitOption(optionallyConnectedServiceUnitsReport.get(0).getBestQuality());
//        }
//
//        for (Quality q : optionalQualities.keySet()) {
//            //extract first, qhich has highest quality
//            solution.addQualityOption(optionalQualities.get(q).iterator().next());
//        }
//
//
//        for (Resource r : optionalResources.keySet()) {
//            //extract first, qhich has highest quality
//            solution.addResourceOption(optionalResources.get(r).iterator().next());
//        }
//
//
//        return solution;
//    }
//
//    public ServiceUnitConfigurationSolution getBestResource() {
//        ServiceUnitConfigurationSolution solution = new ServiceUnitConfigurationSolution();
//        solution.setServiceUnit(serviceUnit);
//        solution.setOverallMatched(overallMatched);
//        solution.setOverallUnMatched(overallUnMatched);
//
//        //TODO: here I would need to do Set Cover to determine which to choose. Currently I am choosing the best
//        if (!optionallyConnectedServiceUnitsReport.isEmpty()) {
//            Collections.sort(optionallyConnectedServiceUnitsReport, new Comparator<ServiceUnitOptions>() {
//                public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
//                    Integer firstQualityMatched = 0;
//                    Integer secondQualityMatched = 0;
//
//                    for (Resource r : o1.optionalResources.keySet()) {
//                        firstQualityMatched += o1.optionalResources.get(r).iterator().next().getMatchedRequirements().size();
//                    }
//
//
//                    for (Resource r : o2.optionalResources.keySet()) {
//                        secondQualityMatched += o2.optionalResources.get(r).iterator().next().getMatchedRequirements().size();
//                    }
//
//                    return -1 * (firstQualityMatched.compareTo(secondQualityMatched));
//
//                }
//            });
//
//            solution.addServiceUnitOption(optionallyConnectedServiceUnitsReport.get(0).getBestResource());
//        }
//
//        for (Quality q : optionalQualities.keySet()) {
//            //extract first, qhich has highest quality
//            solution.addQualityOption(optionalQualities.get(q).iterator().next());
//        }
//
//
//        for (Resource r : optionalResources.keySet()) {
//            //extract first, qhich has highest quality
//            solution.addResourceOption(optionalResources.get(r).iterator().next());
//        }
//
//
//        return solution;
//    }
//    a fool's task, it is unnecesary, has no meaning, and it is heavily in computation
//
//    /**
//     *
//     * @return a list of concrete configurations extracted by combining the
//     * options (quality, resource and service unit options)
//     *
//     * Built exhaustively
//     */
//    public List<ServiceUnitConfigurationSolution> makeConcrete() {
//        List<ServiceUnitConfigurationSolution> list = new ArrayList<ServiceUnitConfigurationSolution>();
//
//        Map<ServiceUnitOptions, List<ServiceUnitConfigurationSolution>> concreteOptionalServiceUnits = new HashMap<ServiceUnitOptions, List<ServiceUnitConfigurationSolution>>();
//        Map<ServiceUnitOptions, List<ServiceUnitConfigurationSolution>> concreteMandatoryServiceUnits = new HashMap<ServiceUnitOptions, List<ServiceUnitConfigurationSolution>>();
//
//        CombinationsGenerator<ServiceUnitOptions, ServiceUnitConfigurationSolution> serviceUnitsCombinationsGenerator = new CombinationsGenerator<ServiceUnitOptions, ServiceUnitConfigurationSolution>();
//
//        List<List<ServiceUnitConfigurationSolution>> optionalCombinations = serviceUnitsCombinationsGenerator.computeCombinationsWithList(concreteOptionalServiceUnits);
//        List<List<ServiceUnitConfigurationSolution>> mandatoryCombinations = serviceUnitsCombinationsGenerator.computeCombinationsWithList(concreteMandatoryServiceUnits);
//
//
////        List<List<ServiceUnitConfigurationSolution>> serviceUnitsCombinations = new ArrayList<List<ServiceUnitConfigurationSolution>>();
//
//        //combine every optional with mandatory
//        for (List<ServiceUnitConfigurationSolution> optional : optionalCombinations) {
//            for (List<ServiceUnitConfigurationSolution> mandatory : mandatoryCombinations) {
//                List<ServiceUnitConfigurationSolution> combined = new ArrayList<ServiceUnitConfigurationSolution>(optional.size() + mandatory.size());
//                combined.addAll(optional);
//                combined.addAll(mandatory);
//                serviceUnitsCombinations.add(combined);
//            }
//        }
//
//
//        //now do quality combinations
//        CombinationsGenerator<Quality, RequirementsMatchingEngine.RequirementsMatchingReport<Quality>> qualityCombinationsGenerator = new CombinationsGenerator<Quality, RequirementsMatchingEngine.RequirementsMatchingReport<Quality>>();
//        List<List<RequirementsMatchingEngine.RequirementsMatchingReport<Quality>>> qualityCombinations = qualityCombinationsGenerator.computeCombinationsWithSet(optionalQualities);
//
//        CombinationsGenerator<Resource, RequirementsMatchingEngine.RequirementsMatchingReport<Resource>> resourceCombinationsGenerator = new CombinationsGenerator<Resource, RequirementsMatchingEngine.RequirementsMatchingReport<Resource>>();
//        List<List<RequirementsMatchingEngine.RequirementsMatchingReport<Resource>>> resourceCombinations = resourceCombinationsGenerator.computeCombinationsWithSet(optionalResources);
//
//
//        //combine everything optional with mandatory
//        for (List<ServiceUnitConfigurationSolution> optional : optionalCombinations) {
//            for (List<ServiceUnitConfigurationSolution> mandatory : mandatoryCombinations) {
//                //units combinations
//                List<ServiceUnitConfigurationSolution> combined = new ArrayList<ServiceUnitConfigurationSolution>(optional.size() + mandatory.size());
//                combined.addAll(optional);
//                combined.addAll(mandatory);
//                
//
//                for (List<RequirementsMatchingEngine.RequirementsMatchingReport<Quality>> qualityOption : qualityCombinations) {
//                    for (List<RequirementsMatchingEngine.RequirementsMatchingReport<Resource>> resourceOption : resourceCombinations) {
//                        ServiceUnitConfigurationSolution configuration = 
//                        //extract Qualities from the report
//                        for (RequirementsMatchingEngine.RequirementsMatchingReport<Quality> qReport : qualityOption) {
//                        }
//                        
//                        for (RequirementsMatchingEngine.RequirementsMatchingReport<Resource> rReport : resourceOption) {
//                        }
//
//                    }
//
//                }
//            }
//
//
//
//            //now do resource combinations
//
//
//            for (ServiceUnitOptions mandatoryServiceUnitOptions : concreteMandatoryServiceUnits.keySet()) {
//                for (ServiceUnitConfigurationSolution mandatoryConfigurationSolution : concreteMandatoryServiceUnits.get(mandatoryServiceUnitOptions)) {
//                    //each map value contains a list of possible Resource configurations
//                    for (Resource resourceType : optionalResources.keySet()) {
//                        for (RequirementsMatchingReport<Resource> resourceMatchingReport : optionalResources.get(resourceType)) {
//                            for (Quality qualityType : optionalQualities.keySet()) {
//                                for (RequirementsMatchingReport<Quality> qualityMatchingReport : optionalQualities.get(qualityType)) {
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            return list;
//        }

    @Override
    public String toString() {
        String description = "ServiceUnitOptions:\n";
        description += "Overall matched: " + overallMatched.size() + " \n ";
        description += "Overall left unmatched: " + overallUnMatched.size() + "\n";

//        description += "Direct matched by: " + serviceUnit.getName() + " \n";
//        for (Metric.MetricType key : matchedRequirementsByServiceUnit.keySet()) {
//            description += "For requirements: " + key + " matched " + matchedRequirementsByServiceUnit.get(key).size() + "\n";
//        }
        //apoi optional configs nu ai de unde sa stii
        description += serviceUnit.toString();

        for (ServiceUnitOptions options : mandatoryConnectedServiceUnitsReport) {
            description += "\t MandatoryAssociation: " + options.toString() + "\n";
        }
        return description;
    }
}
