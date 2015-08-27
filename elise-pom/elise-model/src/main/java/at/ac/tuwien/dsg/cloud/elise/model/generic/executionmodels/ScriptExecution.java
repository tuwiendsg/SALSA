/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Additional parameters for executing capability via script
 * @author Duc-Hung LE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class ScriptExecution extends PrimitiveToStringConverable {

    protected String scriptname;
    protected String workingFolder;
    protected String environment;

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
