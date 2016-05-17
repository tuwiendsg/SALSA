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
package at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.util.HashMap;
import java.util.Map;

/**
 * Description for docker instances
 *
 * @author Duc-Hung Le
 */
public class DockerInfo extends VirtualMachineInfo {

    protected String dockername;

    /**
     * List of port map dockerPort:hostPort, e.g.: 80:9080,8080:9999
     */
    protected String portmap;
    
    public enum States{
        creating, running
    }

    public DockerInfo() {}
    
    public DockerInfo(String provider, String instanceId, String dockerName){
		super(provider, instanceId, dockerName);
                this.setCategory(ServiceCategory.docker);
                updateStateList(States.values());                
	}

    public String getDockername() {
        return dockername;
    }

    public String getPortmap() {
        return portmap;
    }

    public void setPortmap(String portmap) {
        this.portmap = portmap;
    }

    @Override
    public String toString() {
        return "SalsaInstanceDescription_Docker{" + "dockername=" + dockername + ", portmap=" + portmap + '}';
    }    
    
//    @Override
//    public Map<String, String> exportToMap() {
//        Map<String, String> resMap = new HashMap<>();
//        Map<String, String> map = new HashMap<>();
//        map.put("provider", this.provider);
//        map.put("baseImageID", this.baseImageID);        
//        map.put("instanceId", this.instanceId);
//        map.put("publicIp", this.publicIp);
//        map.put("privateIp", this.privateIp);
//        map.put("dockername", this.dockername);
//        map.put("portmap", this.portmap);
//        for (Map.Entry<String, String> mapEntry : map.entrySet()) {
//            if (mapEntry.getValue() != null) {
//                resMap.put(mapEntry.getKey(), mapEntry.getValue());
//            }
//        }
//
//        return resMap;
//    }
}
