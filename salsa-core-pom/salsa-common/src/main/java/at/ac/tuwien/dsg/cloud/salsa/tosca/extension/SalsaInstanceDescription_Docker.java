/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Description for docker instances
 *
 * @author hungld
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "SalsaInstanceDescriptionDocker")
public class SalsaInstanceDescription_Docker extends SalsaInstanceDescription_VM {

    @XmlElement(name = "dockername")
    String dockername;

    /**
     * List of port map dockerPort:hostPort, e.g.: 80:9080,8080:9999
     */
    @XmlElement(name = "portmap")
    String portmap;

    public SalsaInstanceDescription_Docker() {}
    
    public SalsaInstanceDescription_Docker(String provider, String instanceId, String dockerName){
		super(provider, instanceId);
                this.dockername = dockerName;
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
    
    @Override
    public Map<String, String> exportToMap() {
        Map<String, String> resMap = new HashMap<String, String>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("provider", this.provider);
        map.put("baseImage", this.baseImage);
        map.put("instanceType", this.instanceType);
        map.put("instanceId", this.instanceId);
        map.put("publicIp", this.publicIp);
        map.put("privateIp", this.privateIp);
        map.put("publicDNS", this.publicDNS);
        map.put("dockername", this.dockername);
        map.put("portmap", this.portmap);
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator.next();
            if (mapEntry.getValue() != null) {
                resMap.put(mapEntry.getKey(), mapEntry.getValue());
            }
        }

        return resMap;
    }
}
