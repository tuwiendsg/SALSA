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
package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Primitive")
public class PrimitiveOperation {

    @XmlAttribute(name = "name")
    String name;
    // for performing the operation
    @XmlAttribute(name = "type")
    ExecutionType executionType = ExecutionType.SCRIPT;
    @XmlElement(name = "executionREF")
    String executionREF = "/bin/date";
    @XmlElement(name = "executionParameter")
    String executionParameter = "";
    @XmlElement(name = "executionOutput")
    String executionOutput = "";

    public enum ExecutionType {

        SCRIPT, RESTful, SALSA_CONNECTOR;
    }

    public PrimitiveOperation() {
    }

    public static PrimitiveOperation newCommandType(String name, String executionCommand) {
        PrimitiveOperation po = new PrimitiveOperation();
        po.executionREF = executionCommand;
        po.executionType = ExecutionType.SCRIPT;
        po.name = name;
        return po;
    }

    public static PrimitiveOperation newScriptFromURLType(String name, String scriptRelativePath) {
        PrimitiveOperation po = new PrimitiveOperation();
        po.executionType = ExecutionType.SCRIPT;
        po.executionREF = scriptRelativePath;
        po.name = name;
        return po;
    }

    public static PrimitiveOperation newSalsaConnector(String name, String connectorName) {
        PrimitiveOperation po = new PrimitiveOperation();
        po.executionType = ExecutionType.SALSA_CONNECTOR;
        po.executionREF = connectorName;
        po.name = name;
        return po;
    }

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType_(ExecutionType executionType) {
        this.executionType = executionType;
    }

    public String getExecutionREF() {
        return executionREF;
    }

    public void setExecutionREF_(String executionREF) {
        this.executionREF = executionREF;
    }

    public String getExecutionParameter() {
        return executionParameter;
    }

    public void setExecutionParameter_(String executionParameter) {
        this.executionParameter = executionParameter;
    }

    public String getName() {
        return name;
    }

}
