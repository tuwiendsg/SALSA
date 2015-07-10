/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.quelle.elasticityQuantification.engines;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudProvider;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability.Dependency;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to analyze a cloud provider, such as how many configurations of services
 * of a certain type it has
 *
 * @author Daniel Moldovan E-Mail: d.moldovan@dsg.tuwien.ac.at
 */
public class CloudProviderAnalysis {

    public int getNrOfServices(CloudProvider cloudProvider, String serviceCategory, String serviceSubcategory) {
        int servicesCount = 0;
        //sum of possible configurations for each service unit
        for (CloudOfferedService serviceUnit : cloudProvider.getCloudOfferedServices()) {
            if (serviceUnit.getCategory().equals(serviceCategory) && serviceUnit.getSubcategory().equals(serviceSubcategory)) {
                System.out.println(serviceUnit.getName());
                servicesCount ++;
            }
        }
        
        return servicesCount;
    }
    public int getNrOfPossibleConfigurations(CloudProvider cloudProvider, String serviceCategory, String serviceSubcategory) {
        int possibleCFGS = 0;
        //sum of possible configurations for each service unit
        for (CloudOfferedService serviceUnit : cloudProvider.getCloudOfferedServices()) {
            if (serviceUnit.getCategory().equals(serviceCategory) && serviceUnit.getSubcategory().equals(serviceSubcategory)) {
                possibleCFGS += getNrOfPossibleCFGForServiceUnit(serviceUnit);
            }
        }
        
        return possibleCFGS;
    }

    private int getNrOfPossibleCFGForServiceUnit(CloudOfferedService serviceUnit) {
        int possibleCFGS = 1;
        for (ElasticityCapability capability : serviceUnit.getElasticityCapabilities()) {
            
            int possibleCFGSCapability = 0;

            for (Dependency dep : capability.getCapabilityDependencies()) {
                if (dep.getTarget() instanceof CloudOfferedService) {
                    //if service unit, it might have many options
                    possibleCFGSCapability += getNrOfPossibleCFGForServiceUnit((CloudOfferedService) dep.getTarget());
                } else {
                    //if dependency not service unit, counts as 1 option
                    possibleCFGSCapability++;
                }
            }
            
            //in end we multiply possibilities for each capability, as 
            //we can combine separate options for separate capabilities
            //however we want
            possibleCFGS *= possibleCFGSCapability;
        }

        return possibleCFGS;

    }
}
