/**
 * Copyright 2013 Technische Universitaet Wien (TUW), Distributed Systems Group
 * E184
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements;

import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MonitoredElement.MonitoredElementLevel;
import at.ac.tuwien.dsg.mela.common.requirements.Requirement;
import at.ac.tuwien.dsg.mela.common.requirements.Requirements;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 * The idea is to select for each requirement level (Service, Topology, Unit) a
 * set of service units (one for each Requirements instance). How these units
 * are enforced (example if for the units belonging to a service topology the
 * same instantiation for a service is used for all units or separate) is
 * established during enforcement time, not here. Here they are treated as
 * "separate" templates of units
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MultiLevelRequirements")
@XmlType(propOrder = {"name", "level", "optimizationStrategies", "unitRequirements", "containedElements"})
public class MultiLevelRequirements implements Iterable<MultiLevelRequirements>, Serializable {

    @XmlAttribute(name = "name", required = true)
    private String name;
    //level for which the requirements are supplied
    @XmlAttribute(name = "level", required = true)
    private MonitoredElementLevel level;
    @XmlElement(name = "OptimizationStrategy", required = false)
    private List<Strategy> optimizationStrategies;
    @XmlElement(name = "MultiLevelRequirements", required = false)
    private Collection<MultiLevelRequirements> containedElements;
    //each Requirements instance targets a SINGLE service unit 
    //(can instantiate more if mandatory associations are present)
    //but a Requirements contains the Requirement instances which are intended
    //to be fulfilled by a SINGLE ServiceUnit
    @XmlElement(name = "Requirements", required = false)
    private Collection<Requirements> unitRequirements;

    {
        containedElements = new ArrayList<MultiLevelRequirements>();
        unitRequirements = new ArrayList<Requirements>();
        optimizationStrategies = new ArrayList<Strategy>();
    }

    {
        name = UUID.randomUUID().toString();
    }

    public MultiLevelRequirements() {
    }

    public MultiLevelRequirements clone() {
        MultiLevelRequirements levelRequirements = new MultiLevelRequirements(level);
        levelRequirements.name = new String(this.name);
        for (MultiLevelRequirements child : containedElements) {
            levelRequirements.containedElements.add(child.clone());
        }
        for (Requirements requirements : unitRequirements) {
            levelRequirements.addRequirements(requirements.clone());
        }
        levelRequirements.addAllStrategies(optimizationStrategies);

        return levelRequirements;
    }

//    /**
//     * IF this is NOT SERVICE UNIT, it will add the requirements from THIS, to
//     * the flattened children. IF this is SERVICE UNIT, will return itself.
//     * OLD UNUSED as it adds 1 requirement, not blocks in parralel.
//     * @return
//     */
//    public List<MultiLevelRequirements> flatten() {
//        List<MultiLevelRequirements> flattened = new ArrayList<MultiLevelRequirements>();
//        if (this.level == MonitoredElementLevel.SERVICE_UNIT) {
//            //return clone to this to avoid modifying the original requirements
//            flattened.add(this.clone());
//        } else {
//            //currently we consider that if this is NOT service unit,
//            //it has only a single Requirements block which is generic
//            //i.e. applied to all children.
//            //might split this in the future somehow
//            List<Requirement> thisRequirements = new ArrayList<Requirement>();
//            if (!unitRequirements.isEmpty()) {
//                thisRequirements = unitRequirements.iterator().next().getRequirements();
//            }
//            //go trough all children
//            for (MultiLevelRequirements child : containedElements) {
//
//                //flatten them. this might generate more children as maybe you flatten a topology which has more service units
//                List<MultiLevelRequirements> childrenFlat = child.flatten();
//
//                //for each flattened child, update its requirements with requirements of this
//                for (MultiLevelRequirements childFlat : childrenFlat) {
//
//                    //add to child requirements the requirements from this level
//                    for (Requirement r : thisRequirements) {
//                        childFlat.addRequirement(r);
//                    }
//
//                    //add to child optimization strategies the strategies from this level
//                    List<Strategy> childStrategies = childFlat.optimizationStrategies;
////                    for(Strategy strategy: optimizationStrategies){
//                    //avoid adding to the child strategies it allready has
////                        if(!childStrategies.contains(strategy)){
//
//                    //add at the beginning of the child ones, to be applied first the most generic ones
//                    childStrategies.addAll(0, optimizationStrategies);
////                        }
////                    }
//                    flattened.add(childFlat);
//                }
//            }
//        }
//        return flattened;
//    }
    /**
     * IF this is NOT SERVICE UNIT, it will add the requirements from THIS, to
     * the flattened children. IF this is SERVICE UNIT, will return itself.
     *
     * @return
     */
    public List<MultiLevelRequirements> flatten() {
        List<MultiLevelRequirements> flattened = new ArrayList<MultiLevelRequirements>();
        if (this.level == MonitoredElementLevel.SERVICE_UNIT) {
            //return clone to this to avoid modifying the original requirements
            flattened.add(this.clone());
        } else {

            //go trough all children
            for (MultiLevelRequirements child : containedElements) {

                //flatten them. this might generate more children as maybe you flatten a topology which has more service units
                List<MultiLevelRequirements> childrenFlat = child.flatten();

                //for each flattened child, update its requirements with requirements of this
                for (MultiLevelRequirements childFlat : childrenFlat) {

                    //add to child requirements blocks the requirements from this level
                    for (Requirements requirements : unitRequirements) {

                        //check if there are requirements from this unit that are overriden by children requirements
                        if (childFlat.getUnitRequirements().contains(requirements)) {
                            List<Requirements> childReqs = new ArrayList<Requirements>(childFlat.getUnitRequirements());
                            Requirements overridenReqs = childReqs.get(childReqs.indexOf(requirements));
                            //for each of this requirements, check if it has been overiden, and if not, add it
                            //this si done in case child overrides only certain aspects (so we use requirements inheritance)

                            for (Requirement requirement : requirements.getRequirements()) {
                                //if requirement not overridden, add it
                                //requirement equals works on the target metric
                                if (!overridenReqs.getRequirements().contains(requirement)) {
                                    overridenReqs.addRequirement(requirement);
                                }
                            }

                        } else {
                            childFlat.addRequirements(requirements);
                        }
                    }

                    //add to child optimization strategies the strategies from this level
                    List<Strategy> childStrategies = childFlat.optimizationStrategies;
//                    for(Strategy strategy: optimizationStrategies){
                    //avoid adding to the child strategies it allready has
//                        if(!childStrategies.contains(strategy)){

                    //add at the beginning of the child ones, to be applied first the most generic ones
                    childStrategies.addAll(0, optimizationStrategies);
//                        }
//                    }
                    flattened.add(childFlat);
                }
            }
        }
        return flattened;
    }

    public MultiLevelRequirements(MonitoredElementLevel level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public MonitoredElementLevel getLevel() {
        return level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(MonitoredElementLevel level) {
        this.level = level;
    }

    public void setContainedElements(Collection<MultiLevelRequirements> containedElements) {
        this.containedElements = containedElements;
    }

    public void setUnitRequirements(Collection<Requirements> unitRequirements) {
        this.unitRequirements = unitRequirements;
    }

    public Collection<MultiLevelRequirements> getContainedElements() {
        return containedElements;
    }

    public Collection<Requirements> getUnitRequirements() {
        return unitRequirements;
    }

    public List<Strategy> getOptimizationStrategies() {
        return optimizationStrategies;
    }

    public void setOptimizationStrategies(List<Strategy> optimizationStrategies) {
        this.optimizationStrategies = optimizationStrategies;
    }

    /**
     * Used in the flattening process
     *
     * @param r the requirement to be added to all blocks of requirements from
     * this requirements. If r specifies a requirement over an already existing
     * metric in a requirement, block, the requirement is ignored (children
     * override parent)
     */
    public void addRequirement(Requirement r) {
        for (Requirements requirements : unitRequirements) {
            List<Requirement> reqs = requirements.getRequirements();
            if (!reqs.contains(r)) {
                reqs.add(r);
            }
        }

    }

    public void addRequirements(Requirements r) {
        unitRequirements.add(r);
    }

    public void addAllRequirements(Collection<Requirements> r) {
        unitRequirements.addAll(r);
    }

    public void removeRequirements(Requirements r) {
        unitRequirements.remove(r);
    }

    public void removeAllRequirements(Collection<Requirements> r) {
        unitRequirements.removeAll(r);
    }

    public void addMultiLevelRequirements(MultiLevelRequirements r) {
        containedElements.add(r);
    }

    public void addAllMultiLevelRequirements(Collection<MultiLevelRequirements> r) {
        containedElements.addAll(r);
    }

    public void removeMultiLevelRequirements(MultiLevelRequirements r) {
        containedElements.remove(r);
    }

    public void removeAllMultiLevelRequirements(Collection<MultiLevelRequirements> r) {
        containedElements.removeAll(r);
    }

    public void addStrategy(Strategy r) {
        optimizationStrategies.add(r);
    }

    public void addAllStrategies(Collection<Strategy> r) {
        optimizationStrategies.addAll(r);
    }

    public void removeStrategy(Strategy r) {
        optimizationStrategies.remove(r);
    }

    public void removeAllStrategies(Collection<Strategy> r) {
        optimizationStrategies.removeAll(r);
    }

    public Iterator<MultiLevelRequirements> iterator() {
        return new MultiLevelRequirementsIterator(this);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.level != null ? this.level.hashCode() : 0);
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
        final MultiLevelRequirements other = (MultiLevelRequirements) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.level != other.level) {
            return false;
        }
        return true;
    }

//    public static void marshallToXML(MultiLevelRequirements objectToMarshall, OutputStream stream) throws Exception {
//        Marshaller marshaller = JAXBContext.newInstance(MultiLevelRequirements.class).createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//        marshaller.marshal(objectToMarshall, stream);
//        stream.flush();
//    };
    @Override
    public String toString() {
        return "MultiLevelRequirements{" + "name=" + name + ", level=" + level + ", containedElements=" + containedElements + ", unitRequirements=" + unitRequirements + '}';
    }

    //traverses the monitored data tree in a breadth-first manner
    public class MultiLevelRequirementsIterator implements Iterator<MultiLevelRequirements> {

        private List<MultiLevelRequirements> elements;
        private Iterator<MultiLevelRequirements> elementsIterator;

        {
            elements = new ArrayList<MultiLevelRequirements>();
        }

        private MultiLevelRequirementsIterator(MultiLevelRequirements root) {

            //breadth-first tree traversal to create hierarchical tree structure
            List<MultiLevelRequirements> applicationNodeMonitoredDataList = new ArrayList<MultiLevelRequirements>();

            applicationNodeMonitoredDataList.add(root);
            elements.add(root);

            while (!applicationNodeMonitoredDataList.isEmpty()) {
                MultiLevelRequirements data = applicationNodeMonitoredDataList.remove(0);

                for (MultiLevelRequirements subData : data.getContainedElements()) {
                    applicationNodeMonitoredDataList.add(subData);
                    elements.add(subData);
                }
            }
            elementsIterator = elements.iterator();

        }

        @Override
        public boolean hasNext() {
            return elementsIterator.hasNext();
        }

        @Override
        public MultiLevelRequirements next() {
            return elementsIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Unsupported yet");
        }
    }
}
