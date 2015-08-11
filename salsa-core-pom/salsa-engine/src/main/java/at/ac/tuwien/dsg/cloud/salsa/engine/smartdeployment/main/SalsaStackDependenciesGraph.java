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
package at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.main;

import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Duc-Hung LE
 */
@XmlRootElement(name = "Node")
@XmlAccessorType(XmlAccessType.FIELD)
public class SalsaStackDependenciesGraph {

    @XmlAttribute
    String name;    // e.g. java    

    @XmlAttribute
    String type;    // e.g. sys, os, sw (to firstly support tag language)

    @XmlAttribute
    boolean inner;  // the requirement is belong to the above node

    @XmlElement
    Set<SalsaStackDependenciesGraph> childrens = new HashSet<>();

    /**
     * The script points to the PATH/URL of the script template for configure this dependency
     */
    @XmlAttribute
    String script;

    public SalsaStackDependenciesGraph(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public SalsaStackDependenciesGraph() {
    }

    public SalsaStackDependenciesGraph hasInnerDependency(SalsaStackDependenciesGraph child) {
        this.childrens.add(child);
        child.inner = true;
        return this;
    }

    public SalsaStackDependenciesGraph hasOuterDependency(SalsaStackDependenciesGraph child) {
        this.childrens.add(child);
        child.inner = false;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public SalsaStackDependenciesGraph hasScript(String script) {
        this.script = script;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isInner() {
        return inner;
    }

    public void setInner(boolean inner) {
        this.inner = inner;
    }

    public Set<SalsaStackDependenciesGraph> getChildrens() {
        return childrens;
    }

    public void setChildrens(Set<SalsaStackDependenciesGraph> childrens) {
        this.childrens = childrens;
    }

    public String toXML() {

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(SalsaStackDependenciesGraph.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();

            jaxbMarshaller.marshal(this, sw);
            return sw.toString();
        } catch (JAXBException ex) {
            EngineLogger.logger.error("Error with marshalling SalsaStackDependenciesGraph", ex);
            return null;
        }
    }

    public static SalsaStackDependenciesGraph fromXML(String xml) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(SalsaStackDependenciesGraph.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            return (SalsaStackDependenciesGraph) unmarshaller.unmarshal(reader);
        } catch (JAXBException ex) {
            Logger.getLogger(SalsaStackDependenciesGraph.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public SalsaStackDependenciesGraph findNodeByName(String name) {
        EngineLogger.logger.debug("  --> findingNodeByName({}) ?= {}", name, this.name);
        if (name.equals(this.name) || name.equals('"' + this.name + '"')) {
            EngineLogger.logger.debug("  ----> YES, it is equal !");
            return this;
        } else {
            for (SalsaStackDependenciesGraph d : this.childrens) {
                SalsaStackDependenciesGraph candidate = d.findNodeByName(name);
                if (candidate != null) {
                    return candidate;
                }
            }
        }
        return null;
    }

    // search from this node
    // requirement is a map, e.g.: os=Ubuntu, java=
    // check if this node is match with the requirements? If yes, return a list of configuration script in URL
    // after adding, recursive with children nodes
    public LinkedList<String> searchDeploymentScriptTemplate(HashMap<String, String> requirements) {
//        if (node == null) {
//            EngineLogger.logger.warn("The 'node' parameter in searchDeploymentScriptTemplate is null");
//            return null;
//        }
        LinkedList<String> scripts = new LinkedList<>();
        EngineLogger.logger.debug("searching... node: {}:{}", this.getType(), this.getName());
        // check requirement for this node
        
        String value = requirements.get(this.type);
        if (value != null) {
            if (this.name.equals(value)) {    // requirement match
                if (this.getScript()!=null && !this.getScript().isEmpty()) {
                    scripts.add(this.getScript());
                } // else do nothing
            } else { // requirement unmatch
                return null;
            }
        } else {// else do nothing: there is no match or unmatch requirements, that mean no need configuration actions
            EngineLogger.logger.debug("  - No match");
        }

        EngineLogger.logger.debug("Now recursive to all the childrens ...");
        if (childrens == null) {   // an unmatch requirement occured
            EngineLogger.logger.debug("  - Childrens is null, return null");
            return null;
        } else {
            for (SalsaStackDependenciesGraph child : this.getChildrens()) {
                EngineLogger.logger.debug("Now recursive to the child: {}", child.getName());
                LinkedList<String> recursive = child.searchDeploymentScriptTemplate(requirements);
                if (recursive != null) {
                    scripts.addAll(recursive);
                    break;// just find the first child that are match
                }
            }
        }
        return scripts;
    }

}
