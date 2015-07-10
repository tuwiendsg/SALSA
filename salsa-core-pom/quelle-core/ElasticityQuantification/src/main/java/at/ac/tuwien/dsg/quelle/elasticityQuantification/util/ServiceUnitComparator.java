/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.quelle.elasticityQuantification.util;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import java.util.Comparator;

/**
 *
 * @author Daniel Moldovan E-Mail: d.moldovan@dsg.tuwien.ac.at
 */
public class ServiceUnitComparator implements Comparator<CloudOfferedService> {

    @Override
    public int compare(CloudOfferedService o1, CloudOfferedService o2) {
        String o1_type = o1.getCategory();
        String o2_type = o2.getCategory();
        switch (o1_type) {
            case "IaaS":
                switch (o2_type) {
                    case "IaaS":
                        return 0;
                    case "PaaS":
                        return -1;
                    case "MaaS":
                        return -1;
                }
            case "PaaS":
                switch (o2_type) {
                    case "IaaS":
                        return 1;
                    case "PaaS":
                        return 0;
                    case "MaaS":
                        return -1;
                }

            case "MaaS":
                switch (o2_type) {
                    case "IaaS":
                        return 1;
                    case "PaaS":
                        return 1;
                    case "MaaS":
                        return 0;
                }
            default:
                System.out.println("Category " + o1_type + " not recognized");
                return 0;
        }

    }

}
