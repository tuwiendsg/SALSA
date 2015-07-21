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
package generated.JaxbGangliaEntities;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CLUSTER")
public class GangliaClusterInfo {
    @XmlAttribute(name = "NAME")
    private String name;

    @XmlAttribute(name = "OWNER")
    private String owner;

    @XmlAttribute(name = "LATLONG")
    private String latlong;

    @XmlAttribute(name = "URL")
    private String url;

    @XmlAttribute(name = "LOCALTIME")
    private String localtime;

    @XmlElement(name = "HOST")
    private Collection<GangliaHostInfo> hostsInfo;

    {
        hostsInfo = new ArrayList<GangliaHostInfo>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocaltime() {
        return localtime;
    }

    public void setLocaltime(String localtime) {
        this.localtime = localtime;
    }

    public Collection<GangliaHostInfo> getHostsInfo() {
        return hostsInfo;
    }

    public void setHostsInfo(Collection<GangliaHostInfo> hostsInfo) {
        this.hostsInfo = hostsInfo;
    }

    public Collection<GangliaHostInfo> searchHostsByName(String name) {
        Collection<GangliaHostInfo> hosts = new ArrayList<GangliaHostInfo>();
        for (GangliaHostInfo hostInfo : this.hostsInfo) {
            if (hostInfo.getName().contains(name)) {
                hosts.add(hostInfo);
            }
        }
        return hosts;
    }

    public Collection<GangliaHostInfo> searchHostsByAppropximateIP(String ip) {
        Collection<GangliaHostInfo> hosts = new ArrayList<GangliaHostInfo>();
        for (GangliaHostInfo hostInfo : this.hostsInfo) {
            if (hostInfo.getIp().contains(ip)) {
                hosts.add(hostInfo);
            }
        }
        return hosts;
    }
    
    public GangliaHostInfo searchHostsByExactIP(String ip) {
       for (GangliaHostInfo hostInfo : this.hostsInfo) {
            if (hostInfo.getIp().equals(ip)) {
               return hostInfo;
            }
        }
        return null;
    }

    //if gmodstart has same value means same machine
    public Collection<GangliaHostInfo> searchHostsByGmodStart(String gmodstarted) {
        Collection<GangliaHostInfo> hosts = new ArrayList<GangliaHostInfo>();
        for (GangliaHostInfo hostInfo : this.hostsInfo) {
            if (hostInfo.getGmondStarted().contains(gmodstarted)) {
                hosts.add(hostInfo);
            }
        }
        return hosts;
    }

    @Override
    public String toString() {
        String info = "ClusterInfo{" +
                "name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", latlong='" + latlong + '\'' +
                ", url='" + url + '\'' +
                ", localtime='" + localtime + '\'' + ", hostsInfo=";


        for (GangliaHostInfo hostInfo : hostsInfo) {
            info += "\n " + hostInfo.toString() + "\n";
        }


        info += '}';
        return info;
    }
}
