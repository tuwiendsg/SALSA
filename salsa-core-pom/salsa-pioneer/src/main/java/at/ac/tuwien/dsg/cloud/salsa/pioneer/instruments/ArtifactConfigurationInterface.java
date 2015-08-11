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
package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureState;


public interface ArtifactConfigurationInterface {

    /**
     * Configure an artifact
     *
     * @param configInfo The configuration info
     * @return The state of the configuration either successful or error
     */
    public SalsaMsgConfigureState configureArtifact(SalsaMsgConfigureArtifact configInfo);

    /**
     * Define how to get status of a running instance. E.g, with system service-based, state can be get via "system serviceName status"
     *
     * @param configInfo The info of the instance
     * @return A custom description of the status
     */
    public String getStatus(SalsaMsgConfigureArtifact configInfo);

  
}
