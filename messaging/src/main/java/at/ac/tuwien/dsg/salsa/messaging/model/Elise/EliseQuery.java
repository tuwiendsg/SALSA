/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.messaging.model.Elise;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Duc-Hung Le
 */
public class EliseQuery {

    String category;

    Set<EliseQueryRule> rules = new HashSet<>();

    Set<String> hasCapabilities = new HashSet<>();

    public EliseQuery() {
    }

    public EliseQuery(String category) {
        this.category = category;
    }

    public EliseQuery hasRule(EliseQueryRule rule) {
        this.rules.add(rule);
        return this;
    }

    public EliseQuery hasRule(String metric, String value, EliseQueryRule.OPERATION operation) {
        this.rules.add(new EliseQueryRule(metric, value, operation));
        return this;
    }

    public EliseQuery hasCapability(String capability) {
        this.hasCapabilities.add(capability);
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Set<EliseQueryRule> getRules() {
        return rules;
    }

    public Set<String> getHasCapabilities() {
        return hasCapabilities;
    }

    @Override
    public String toString() {
        return toJson();
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static EliseQuery fromJson(String data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, EliseQuery.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
