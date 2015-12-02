/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
