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
package at.ac.tuwien.dsg.salsa.messaging.protocol;

/**
 *
 * @author Duc-Hung Le
 */
public class SalsaMessageTopic {

    private static final String PREFIX = "ac.at.tuwien.dsg.cloud.salsa.";
    // From CENTER to PIONEER: send request
    public static final String CENTER_REQUEST_PIONEER = PREFIX + "center.request";
    // From Pioneer to Center: heartbeat, register
    public static final String PIONEER_REGISTER_AND_HEARBEAT = PREFIX + "pioneer.sync";
    // From Pioneer to Center: update configuration state
    public static final String PIONEER_UPDATE_CONFIGURATION_STATE = PREFIX + "pioneer.state";
    // From Pioneer to Center: Log. This is not ussually used.
    public static final String PIONEER_LOG = PREFIX + "pioneer.log";
    // From Center to External: publish event to external queue
    public static final String SALSA_PUBLISH_EVENT = PREFIX + "event";

    public static String getPioneerTopicByID(String pioneerID) {
        return CENTER_REQUEST_PIONEER + "." + pioneerID;
    }

}
