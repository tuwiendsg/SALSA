/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.executionmodels;

/**
 *
 * @author hungld
 */
public class ScriptExecution extends PrimitiveToStringConverable {

    String scriptname;
    String workingFolder;
    String environment;

    public ScriptExecution() {
    }

    /**
     *
     * @param scriptName The relative path to the script in the artifact
     * @param work working directory, if not defined, it is the
     * @param env additional environment
     */
    public ScriptExecution(String scriptName, String work, String env) {
        this.scriptname = scriptName;
        this.workingFolder = work;
        this.environment = env;
    }

//    public ScriptExecution(String scriptName, String work, String env, String... parameters) {
//        this.scriptname = scriptName;
//        this.workingFolder = work;
//        this.environment = env;
//        for (String message : parameters) {
//           // add here
//        }
//    }

    public String getScriptname() {
        return this.scriptname;
    }

    public String getWorkingFolder() {
        return this.workingFolder;
    }

    public String getEnvironment() {
        return this.environment;
    }

}
