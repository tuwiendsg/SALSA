/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.collectorinterfaces;

import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.util.Set;

/**
 *
 * @author Duc-Hung Le
 */
public abstract class UnitInstanceCollector extends GenericCollector {

    public abstract Set<UnitInstance> collectAllInstance();

    public abstract UnitInstance collectInstanceByID(String domainID);

    public abstract LocalIdentification identify(UnitInstance paramUnitInstance);    
    
}
