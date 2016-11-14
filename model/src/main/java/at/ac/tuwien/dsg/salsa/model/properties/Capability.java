/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model.properties;

/**
 *
 * @author hungld
 */
public class Capability {

    public enum ExecutionType {
        SCRIPT, RESTful, SALSA_CONNECTOR;
    }

    String name;
    String executionREF;
    ExecutionType executionType = ExecutionType.SCRIPT;

    public Capability(String name, String executionREF) {
        this.name = name;
        this.executionREF = executionREF;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecutionREF() {
        return executionREF;
    }

    public void setExecutionREF(String executionREF) {
        this.executionREF = executionREF;
    }

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
    }

}
