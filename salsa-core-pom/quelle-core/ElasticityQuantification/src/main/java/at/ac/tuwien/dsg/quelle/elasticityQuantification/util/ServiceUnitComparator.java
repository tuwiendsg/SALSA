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
