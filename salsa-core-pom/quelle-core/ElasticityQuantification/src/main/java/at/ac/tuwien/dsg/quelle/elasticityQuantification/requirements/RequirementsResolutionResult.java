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

import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.engines.ServiceUnitComparators;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.engines.ServiceUnitOptions;
import at.ac.tuwien.dsg.mela.common.requirements.Requirements;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 * Decorator over MultiLevelRequirements Adds the result of the utility matching
 * process
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RequirementsStructure")
public class RequirementsResolutionResult {

    //requirements
    private Map<MultiLevelRequirements, Map<Requirements, List<ServiceUnitOptions>>> matchedOptions;

    {
        matchedOptions = new HashMap<>();
    }

    /**
     * Does not check if the levelRequirements already exist. If they do, they
     * will be overridden.
     *
     * @param levelRequirements
     * @param options
     */
    public void addMatchedOption(MultiLevelRequirements levelRequirements, Requirements requirements, ServiceUnitOptions options) {
        if (matchedOptions.containsKey(levelRequirements)) {
            Map<Requirements, List<ServiceUnitOptions>> levelmatched = matchedOptions.get(levelRequirements);
            if (levelmatched.containsKey(requirements)) {
                levelmatched.get(requirements).add(options);
            } else {
                List<ServiceUnitOptions> list = new ArrayList<ServiceUnitOptions>();
                list.add(options);
                levelmatched.put(requirements, list);
            }
        } else {
            List<ServiceUnitOptions> list = new ArrayList<ServiceUnitOptions>();
            list.add(options);
            Map<Requirements, List<ServiceUnitOptions>> levelmatched = new HashMap<Requirements, List<ServiceUnitOptions>>();
            levelmatched.put(requirements, list);
            matchedOptions.put(levelRequirements, levelmatched);
        }
    }

    public void removeMatchedOption(MultiLevelRequirements levelRequirements) {
        if (matchedOptions.containsKey(levelRequirements)) {
            matchedOptions.remove(levelRequirements);
        }
    }

    /**
     * Applies the respective optimization strategies.
     *
     * @return for each level of requirements, the
     * ServiceUnitConfigurationSolution for each Requirements block
     */
    public Map<MultiLevelRequirements, Map<Requirements, List<ServiceUnitConfigurationSolution>>> getConcreteConfigurations(ServiceUnitComparators serviceUnitComparators) {

        Map<MultiLevelRequirements, Map<Requirements, List<ServiceUnitConfigurationSolution>>> best = new HashMap<MultiLevelRequirements, Map<Requirements, List<ServiceUnitConfigurationSolution>>>();

        for (MultiLevelRequirements multiLevelRequirements : matchedOptions.keySet()) {

            //optimization strategies can be different on each multi level requirements
            final List<Comparator<ServiceUnitOptions>> strategiesComparators = serviceUnitComparators.getStrategiesComparators(multiLevelRequirements.getOptimizationStrategies());

            Map<Requirements, List<ServiceUnitOptions>> options = matchedOptions.get(multiLevelRequirements);
            Map<Requirements, List<ServiceUnitConfigurationSolution>> solution = new HashMap<Requirements, List<ServiceUnitConfigurationSolution>>();
            best.put(multiLevelRequirements, solution);

            for (Requirements r : options.keySet()) {
                List<ServiceUnitOptions> rOptions = options.get(r);
                //get an iterator over the strategies comparators
                //to do : apply iterator.next, if equals with 0, apply next iterator.

                //sort the collection
                Collections.sort(rOptions, new Comparator<ServiceUnitOptions>() {
                    public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
                        final Iterator<Comparator<ServiceUnitOptions>> strategiesIterator = strategiesComparators.iterator();
                        //if no strategy, just leave them unsorted 
                        int result = 0;

                        //while we have strategies and the LAST result of the comparator was 0,
                        //continue and discriminate with next comparator. Else, return result.
                        while (strategiesIterator.hasNext() && result == 0) {
                            Comparator comparator = strategiesIterator.next();
                            result = comparator.compare(o1, o2);
                        }
                        return result;
                    }
                });

                //go again trough the collection and KEEP ONLY EQUAL elements
                Iterator<ServiceUnitOptions> rIterator = rOptions.iterator();
                if (!rIterator.hasNext()) {
                    continue;
                }
                ServiceUnitOptions current = rIterator.next();

                //add the first service unit option to the solution
                if (solution.containsKey(r)) {
                    solution.get(r).add(current.applyStrategy(strategiesComparators));
                } else {
                    List<ServiceUnitConfigurationSolution> possibleSolutions = new ArrayList<ServiceUnitConfigurationSolution>();
                    possibleSolutions.add(current.applyStrategy(strategiesComparators));
                    solution.put(r, possibleSolutions);
                }

                while (rIterator.hasNext()) {
                    ServiceUnitOptions next = rIterator.next();

                    //check for the two service units are equal, and if so, keep them, else discard second
                    final Iterator<Comparator<ServiceUnitOptions>> strategiesIterator = strategiesComparators.iterator();
                    //if no strategy, just leave them unsorted 
                    int result = 0;

                    //while we have strategies and the LAST result of the comparator was 0,
                    //continue and discriminate with next comparator. Else, return result.
                    while (strategiesIterator.hasNext() && result == 0) {
                        Comparator comparator = strategiesIterator.next();
                        result = comparator.compare(current, next);
                    }
                    //if equal, add the element to the potential solutions
                    if (result == 0) {
                        if (solution.containsKey(r)) {
                            solution.get(r).add(next.applyStrategy(strategiesComparators));
                        } else {
                            List<ServiceUnitConfigurationSolution> possibleSolutions = new ArrayList<ServiceUnitConfigurationSolution>();
                            possibleSolutions.add(next.applyStrategy(strategiesComparators));
                            solution.put(r, possibleSolutions);
                        }
                    } else {
                        //stop adding
                        break;
                    }

                    current = next;
                }
//                solution.put(r, rOptions.get(0).applyStrategy(strategiesComparators));

            }

        }
        return best;
    }

    /**
     * combines solutions for all requirements among themselves. combinations of
     * n of n
     *
     * @param configs
     * @return
     */
    public List<List<ServiceUnitConfigurationSolution>> joinSolsForSameUnits(Map<Requirements, List<ServiceUnitConfigurationSolution>> configs) {
        List<List<ServiceUnitConfigurationSolution>> result = new ArrayList<List<ServiceUnitConfigurationSolution>>();
        result.add(new ArrayList<ServiceUnitConfigurationSolution>());

        for (List<ServiceUnitConfigurationSolution> options : configs.values()) {
            List<List<ServiceUnitConfigurationSolution>> newResult = new ArrayList<List<ServiceUnitConfigurationSolution>>();

            for (ServiceUnitConfigurationSolution option : options) {

                for (List<ServiceUnitConfigurationSolution> list : result) {
                    List<ServiceUnitConfigurationSolution> combined = new ArrayList<ServiceUnitConfigurationSolution>();
                    if (!combined.contains(option)) {
                        combined.add(option);
                    }
                    combined.addAll(list);
                    newResult.add(combined);
                }

            }
            result = newResult;

        }

        return result;
    }

    public Map<MultiLevelRequirements, Map<Requirements, List<ServiceUnitOptions>>> getMatchedOptions() {
        return matchedOptions;
    }
}
