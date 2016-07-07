/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Duc-Hung LE
 */
public class CollectorDescription {

    String name;
    String assignedConductorID;
    String artifactURL;
    List<String> configurations;

    public CollectorDescription() {
    }

    public CollectorDescription(String name, String assignedConductorID, String artifactURL) {
        this.name = name;
        this.assignedConductorID = assignedConductorID;
        this.artifactURL = artifactURL;
    }

    public CollectorDescription(String name, String assignedConductorID, String artifactURL, String confs) {
        this.name = name;
        this.assignedConductorID = assignedConductorID;
        this.artifactURL = artifactURL;

        this.configurations = new ArrayList<>();
        String[] aconfs = confs.split(";");
        this.configurations.addAll(Arrays.asList(aconfs));
    }

    public List<String> getConfigurations() {
        return configurations;
    }

    public String getName() {
        return name;
    }

    public String getAssignedConductorID() {
        return assignedConductorID;
    }

    public String getArtifactURL() {
        return artifactURL;
    }

    public CollectorDescription hasConfiguration(String conf) {
        if (this.configurations == null) {
            this.configurations = new ArrayList<>();
        }
        String[] confs = conf.split(";");
        this.configurations.addAll(Arrays.asList(confs));
        return this;
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

    public static CollectorDescription fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, CollectorDescription.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
