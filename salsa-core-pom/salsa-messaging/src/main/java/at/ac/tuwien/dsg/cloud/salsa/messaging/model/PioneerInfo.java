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
package at.ac.tuwien.dsg.cloud.salsa.messaging.model;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Duc-Hung Le
 */
public class PioneerInfo {

    String userName;

    String id;
    String ip;
    String service;
    String topology;
    String unit;
    int instance;

    public PioneerInfo() {
    }

    public PioneerInfo(String userName, String id, String ip, String service, String topology, String unit, int instance) {
        this.userName = userName;
        this.id = id;
        this.ip = ip;
        this.service = service;
        this.topology = topology;
        this.unit = unit;
        this.instance = instance;
    }

    public String getUserName() {
        return userName;
    }

    public String getIp() {
        return ip;
    }

    public String getService() {
        return service;
    }

    public String getTopology() {
        return topology;
    }

    public String getUnit() {
        return unit;
    }

    public int getInstance() {
        return instance;
    }

    public String getId() {
        return id;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static PioneerInfo fromJson(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, PioneerInfo.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
