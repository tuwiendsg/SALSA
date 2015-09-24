/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.operatingsystemcollector;

import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.UnitInstanceCollector;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.VirtualMachineInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Duc-Hung LE
 */
public class Main extends UnitInstanceCollector {

    @Override
    public Set<UnitInstance> collectAllInstance() {
        Set<UnitInstance> instances = new HashSet<>();
        UnitInstance instance = new UnitInstance(null, ServiceCategory.VirtualMachine);

        VirtualMachineInfo vmInfo = new VirtualMachineInfo();

        vmInfo.setOsArch(System.getProperty("os.arch"));
        vmInfo.setOsName(System.getProperty("os.name"));
        vmInfo.setOsVersion(System.getProperty("os.version"));
        vmInfo.setJavaVendor(System.getProperty("java.vendor"));
        vmInfo.setJavaVersion(System.getProperty("java.version"));
        
        
        instance.setDomainInfo(null);
        instances.add(instance);
        return instances;
    }

    // domainID is not used here
    @Override
    public UnitInstance collectInstanceByID(String domainID) {
        return collectAllInstance().iterator().next();
    }

    @Override
    public LocalIdentification identify(UnitInstance paramUnitInstance) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "OperatingSystemCollector";
    }

}
