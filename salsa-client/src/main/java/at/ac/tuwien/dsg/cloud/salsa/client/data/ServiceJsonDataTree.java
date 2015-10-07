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
package at.ac.tuwien.dsg.cloud.salsa.client.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The class contains information to be visualized as graph
 *
 * @author Duc-Hung Le
 *
 */
public class ServiceJsonDataTree {

    String id;
    String uuid;
    String state;
    Map<String, String> properties;
    Object monitoring;
    List<ServiceJsonDataTree> children;
    boolean isAbstract = true;	// true: tosca node, false: instance node
    String nodeType;
    String artifactType;
    List<String> connectto = new ArrayList<>();

    public ServiceJsonDataTree() {
    }

    public ServiceJsonDataTree(String id) {
        this.id = id;
    }

    public ServiceJsonDataTree(int id) {
        this.id = Integer.toString(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Object getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(Object monitoring) {
        this.monitoring = monitoring;
    }

    public List<ServiceJsonDataTree> getChildren() {
        return children;
    }

    public void setChildren(List<ServiceJsonDataTree> children) {
        this.children = children;
    }

    public boolean isIsAbstract() {
        return isAbstract;
    }

    public void setIsAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public List<String> getConnectto() {
        return connectto;
    }

    public void setConnectto(List<String> connectto) {
        this.connectto = connectto;
    }

    public String getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(String artifactType) {
        this.artifactType = artifactType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
