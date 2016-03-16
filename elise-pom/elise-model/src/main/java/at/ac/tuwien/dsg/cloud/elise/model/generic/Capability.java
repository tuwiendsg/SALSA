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
package at.ac.tuwien.dsg.cloud.elise.model.generic;

import at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels.RestExecution;
import at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels.ScriptExecution;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * The configuration capability with different execution methods
 *
 * @author Duc-Hung LE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
@NodeEntity
public class Capability {

    @GraphId
    private Long graphID;

    @Indexed(unique = true)
    protected String id;

    protected String name;
    protected String executedBy;
    protected ExecutionMethod executionMethod;

    /**
     * The execution model is store as String and will be (un)marshal via JSON
     */
    protected String executionModel;
    protected Set<String> parameters = new HashSet<>();
    protected Set<CapabilityEffect> effects = new HashSet<>();

    public Capability() {
    }

    @XmlType
    public static enum ExecutionMethod {

        Script,
        REST,
        Dockerfile,
        Unknown;
    }

    public Capability(String name, ExecutionMethod executeMethod, Object executionModel) {
        this.name = name;
        this.executionMethod = executeMethod;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            this.executionModel = mapper.writeValueAsString(executionModel);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Capability hasParameters(String... args) {
        if (this.parameters == null) {
            this.parameters = new HashSet<>();
        }
        this.parameters.addAll(Arrays.asList(args));
        return this;
    }

    public Capability hasCapabilityEffect(CapabilityEffect effect) {
        this.effects.add(effect);
        return this;
    }

    public Capability executedBy(String executedBy) {
        this.executedBy = executedBy;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public ExecutionMethod getExecutionMethod() {
        return this.executionMethod;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public String getExecutionModel() {
        return executionModel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExecutionMethod(ExecutionMethod executionMethod) {
        this.executionMethod = executionMethod;
    }

    public void setExecutionModel(String executionModel) {
        this.executionModel = executionModel;
    }

    public Object returnExecutionModel() {
        if (this.executionModel == null) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            switch (this.executionMethod) {
                case Script:
                    return mapper.readValue(this.executionModel, ScriptExecution.class);
                case REST:
                    return mapper.readValue(this.executionModel, RestExecution.class);
            }
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.executionMethod);
        hash = 97 * hash + Objects.hashCode(this.parameters);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Capability other = (Capability) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.executionMethod != other.executionMethod) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        return true;
    }

    public Set<String> getParameters() {
        return parameters;
    }

    public Set<CapabilityEffect> getEffects() {
        return effects;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
