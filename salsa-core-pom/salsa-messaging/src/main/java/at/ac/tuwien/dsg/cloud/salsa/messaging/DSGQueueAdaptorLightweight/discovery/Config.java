/* 
 * Copyright 2015 Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight.discovery;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class Config {
	
	private String discoveryIp;
	private int discoveryPort;
	private String serviceName;

	public String getDiscoveryIp() {
		return discoveryIp;
	}

	public Config setDiscoveryIp(String discoveryIp) {
		this.discoveryIp = discoveryIp;
		return this;
	}

	public int getDiscoveryPort() {
		return discoveryPort;
	}

	public Config setDiscoveryPort(int discoveryPort) {
		this.discoveryPort = discoveryPort;
		return this;
	}

	public String getServiceName() {
		return serviceName;
	}

	public Config setServiceName(String serviceName) {
		this.serviceName = serviceName;
		return this;
	}	
}
