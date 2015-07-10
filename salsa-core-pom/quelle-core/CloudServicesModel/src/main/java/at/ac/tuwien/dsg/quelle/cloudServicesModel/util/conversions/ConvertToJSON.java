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
package at.ac.tuwien.dsg.quelle.cloudServicesModel.util.conversions;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Metric;
import at.ac.tuwien.dsg.mela.common.requirements.Condition;
import at.ac.tuwien.dsg.mela.common.requirements.Requirement;
import at.ac.tuwien.dsg.mela.common.requirements.Requirements;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
public class ConvertToJSON {

    public static String convertToJSON(MultiLevelRequirements multiLevelRequirements) {

        //traverse the tree to do the JSON properly
        List<JSONObject> jsontree = new ArrayList<JSONObject>();
        List<MultiLevelRequirements> multiLevelRequirementsTree = new ArrayList<MultiLevelRequirements>();

        JSONObject root = processMultiLevelRequirementsElement(multiLevelRequirements);

        jsontree.add(root);
        multiLevelRequirementsTree.add(multiLevelRequirements);

        //traverse the tree in a DFS manner
        while (!multiLevelRequirementsTree.isEmpty()) {
            MultiLevelRequirements currentlyProcessed = multiLevelRequirementsTree.remove(0);
            JSONObject currentlyProcessedJSONObject = jsontree.remove(0);
            JSONArray childrenArray = new JSONArray();
            //process children
            for (MultiLevelRequirements child : currentlyProcessed.getContainedElements()) {
                JSONObject childJSON = processMultiLevelRequirementsElement(child);
                childrenArray.add(childJSON);

                //next to process are children
                jsontree.add(childJSON);
                multiLevelRequirementsTree.add(child);
            }
            if(currentlyProcessedJSONObject.containsKey("children")){
                JSONArray array = (JSONArray) currentlyProcessedJSONObject.get("children");
                array.addAll(childrenArray);
            }else{
                currentlyProcessedJSONObject.put("children", childrenArray);
            }
        }

        return root.toJSONString();

    }

    private static JSONObject processMultiLevelRequirementsElement(MultiLevelRequirements multiLevelRequirements) {
        JSONObject rootJSON = new JSONObject();
        rootJSON.put("name", multiLevelRequirements.getName());
        rootJSON.put("level", multiLevelRequirements.getLevel().toString());
        rootJSON.put("type", "RequirementsLevel");

        JSONArray requirementsBlocksArray = new JSONArray();

        for (Requirements r : multiLevelRequirements.getUnitRequirements()) {
            JSONObject requirementsJSON = new JSONObject();
            requirementsJSON.put("name", r.getName());
            requirementsJSON.put("target", r.getTargetServiceID());
            requirementsJSON.put("type", "RequirementsBlock");
            

            JSONArray requirementsBlockRequirements = new JSONArray();

            for (Requirement requirement : r.getRequirements()) {
                JSONObject requirementJSON = new JSONObject();
                requirementJSON.put("type", "Requirement");
                JSONArray conditionsArray = new JSONArray();

                Metric metric = requirement.getMetric();
                requirementJSON.put("metric", metric.getName() + "[" + metric.getMeasurementUnit() + "]");
                for (Condition condition : requirement.getConditions()) {
                    JSONObject conditionJSON = new JSONObject();
                    conditionJSON.put("name", condition.toString());
                    conditionJSON.put("type", "Condition");
                    conditionsArray.add(conditionJSON);
                }
                requirementJSON.put("target", r.getTargetServiceID());
                requirementJSON.put("children", conditionsArray);
                requirementsBlockRequirements.add(requirementJSON);
            }
            requirementsJSON.put("children", requirementsBlockRequirements);
            requirementsBlocksArray.add(requirementsJSON);
        }

        rootJSON.put("children", requirementsBlocksArray);

        return rootJSON;
    }
}
