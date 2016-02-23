/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.transformopeniotsensor;

import java.util.List;

/**
 *
 * @author hungld
 */
public class Command {

    String name;
    List<Parameter> parameters;

    public static class Parameter {

        String name;
        String type;
        String phenomenon;

        public Parameter() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPhenomenon() {
            return phenomenon;
        }

        public void setPhenomenon(String phenomenon) {
            this.phenomenon = phenomenon;
        }
    }

    public Command() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

}
