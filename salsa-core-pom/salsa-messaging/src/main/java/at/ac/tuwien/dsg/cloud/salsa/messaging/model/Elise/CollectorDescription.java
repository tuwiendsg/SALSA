/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Duc-Hung LE
 */
public class CollectorDescription {

    String name;
    List<String> configurations;

    public CollectorDescription() {
    }

    public CollectorDescription(String name, String[] confs) {
        this.name = name;
        System.out.println("CONSTRUCTURE THE COLLECTOR DESCRIPTION. Configurations size: " + confs.length);

        for (String c : confs) {
            System.out.println(" conf value: " + c);
        }

        this.configurations = new ArrayList<>();
        this.configurations.addAll(Arrays.asList(confs));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getConfigurations() {
        return configurations;
    }

    public CollectorDescription hasConfiguration(String conf) {
        if (this.configurations == null) {
            this.configurations = new ArrayList<>();
        }
        this.configurations.add(conf);
        return this;
    }

}
