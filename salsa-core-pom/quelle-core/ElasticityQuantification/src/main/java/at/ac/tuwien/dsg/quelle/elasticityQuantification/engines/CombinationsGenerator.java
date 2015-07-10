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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
public class CombinationsGenerator<K, T> {

    /**
     *
     * @param indexes the indexes corresponds to each K key, and indicate which
     * element from the possible configs to retrieve
     * @param solutions
     */
    private void generateCombinations(List<List<T>> combinations, Map<K, Integer> indexes, Map<K, List<T>> solutions) {
        List<T> combination = new ArrayList<T>();

        for (K options : indexes.keySet()) {
            int index = indexes.get(options);
            combination.add(solutions.get(options).get(index));
        }

        combinations.add(combination);
        if (incrementIndex(indexes, solutions)) {
            generateCombinations(combinations, indexes, solutions);
        }

    }

    public List<List<T>> computeCombinationsWithList(Map<K, List<T>> solutions) {
        Map<K, Integer> indexes = new LinkedHashMap<K, Integer>();
        for (K options : solutions.keySet()) {
            indexes.put(options, solutions.get(options).size());
        }

        List<List<T>> combinations = new ArrayList<List<T>>();
        generateCombinations(combinations, indexes, solutions);
        return combinations;
    }

    public List<List<T>> computeCombinationsWithSet(Map<K, Set<T>> solutions) {

        Map<K, List<T>> solutionsAsList = new HashMap<K, List<T>>();
        for (K k : solutions.keySet()) {
            List<T> l = new ArrayList<T>();
            l.addAll(solutions.get(k));
            solutionsAsList.put(k, l);
        }

        Map<K, Integer> indexes = new LinkedHashMap<K, Integer>();
        for (K options : solutionsAsList.keySet()) {
            indexes.put(options, solutionsAsList.get(options).size());
        }

        List<List<T>> combinations = new ArrayList<List<T>>();

        generateCombinations(combinations, indexes, solutionsAsList);
        return combinations;
    }

    /**
     * Rotates the index by decrementing it. Example 555, 554, 553 .. Decrement
     * such that I can always compare != 0, instead of != list.Size()
     *
     * @param indexes will modify the indexes
     * @param solutions
     */
    private boolean incrementIndex(Map<K, Integer> indexes, Map<K, List<T>> solutions) {
        ArrayList<K> indexList = new ArrayList<K>(indexes.keySet());

        //when we have decreased the first to 0, then return false
        if (indexes.get(indexList.get(0)) == 0) {
            return false;
        }

        for (int i = indexList.size(); i > 0; i--) {
            Integer index = indexes.get(indexList.get(i));
            //if index ==0
            if (index == 0) {
                index = solutions.get(indexList.get(i)).size();
                indexes.put(indexList.get(i), index);
                //borrow from adjacent index. Example
                //120 , if last = 0. then do 1 1 list_size
                Integer adjacentIndex = indexes.get(indexList.get(i - 1));
                indexes.put(indexList.get(i - 1), adjacentIndex - 1);
            }
        }

        return true;
    }
}
