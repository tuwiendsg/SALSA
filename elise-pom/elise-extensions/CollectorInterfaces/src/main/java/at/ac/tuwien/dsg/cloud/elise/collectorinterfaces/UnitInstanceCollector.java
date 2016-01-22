/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.collectorinterfaces;

import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import java.util.Set;

/**
 * This interface provides a way to implement collector plug-in.
 * 
 * At runtime, the conductor will scan for all the implementation class under the class path
 * to execute the collection method.  
 * 
 * @author Duc-Hung Le
 */
public abstract class UnitInstanceCollector extends GenericCollector {

    /**
     * This method guides the collector to collect all the possible instances 
     * This is usually used for an aggregation request, or whereas the instance has no ID
     * 
     * @return A set of unit instance the collector can retrieve
     */    
    public abstract Set<UnitInstance> collectAllInstance();

    /**
     * If the instance has its own ID, e.g. VM has cloud ID, processID, the collector can implement this
     * @param domainID The ID which is assigned to the instance by a third party
     * @return A particular unit instance
     */
    public abstract UnitInstance collectInstanceByID(String domainID);

    /**
     * If an instance is managed by some services, how to identify it in global scope?
     * This method should extract useful information which can be used to identify the unit,
     * such as IP address, port or part of the domain ID.
     * 
     * @param paramUnitInstance The unit instance which is collected
     * @return A LocalIdentification which contains the category and list of identification items
     */
    public abstract LocalIdentification identify(UnitInstance paramUnitInstance);    
    
}
