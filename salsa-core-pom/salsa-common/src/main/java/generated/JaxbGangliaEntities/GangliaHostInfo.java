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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "HOST")
public class GangliaHostInfo {
    @XmlAttribute(name = "NAME", required = true)
    private String name;

    @XmlAttribute(name = "IP", required = true)
    private String ip;

    @XmlAttribute(name = "LOCATION", required = true)
    private String location;

    @XmlAttribute(name = "TAGS")
    private String tags;

    @XmlAttribute(name = "REPORTED")
    private String reported;

    @XmlAttribute(name = "TN")
    private String tn;

    @XmlAttribute(name = "TMAX")
    private String tmax;

    @XmlAttribute(name = "DMAX")
    private String dmax;

    @XmlAttribute(name = "GMOND_STARTED")
    private String gmondStarted;

    @XmlAttribute(name = "SOURCE")
    private String source;

    @XmlElement(name = "METRIC")
    Collection<GangliaMetricInfo> metrics;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getReported() {
        return reported;
    }

    public void setReported(String reported) {
        this.reported = reported;
    }

    public String getTn() {
        return tn;
    }

    public void setTn(String tn) {
        this.tn = tn;
    }

    public String getTmax() {
        return tmax;
    }

    public void setTmax(String tmax) {
        this.tmax = tmax;
    }

    public String getDmax() {
        return dmax;
    }

    public void setDmax(String dmax) {
        this.dmax = dmax;
    }

    public String getGmondStarted() {
        return gmondStarted;
    }

    public void setGmondStarted(String gmondStarted) {
        this.gmondStarted = gmondStarted;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Collection<GangliaMetricInfo> getMetrics() {
        return metrics;
    }

    public void setMetrics(Collection<GangliaMetricInfo> metrics) {
        this.metrics = metrics;
    }

    /**
     * @param name name to search for. All Metrics that CONTAIN the supplied name
     *             will be returned
     * @return
     */
    public Collection<GangliaMetricInfo> searchMetricsByName(String name) {
        List<GangliaMetricInfo> metrics = new ArrayList<GangliaMetricInfo>();
        
        for (GangliaMetricInfo metricInfo : this.metrics) {
            if (metricInfo.getName().contains(name)) {
                metrics.add(metricInfo);
            }
        }
        return metrics;
    }

    @Override
    public String toString() {
        String info = "HostInfo{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", location='" + location + '\'' +
                ", tags='" + tags + '\'' +
                ", reported='" + reported + '\'' +
                ", tn='" + tn + '\'' +
                ", tmax='" + tmax + '\'' +
                ", dmax='" + dmax + '\'' +
                ", gmondStarted='" + gmondStarted + '\'' +
                ", source='" + source + '\'' + ", metrics=";

        for (GangliaMetricInfo metricInfo : metrics) {
            info += "\n\t " + metricInfo.toString();
        }
        info += '}';
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GangliaHostInfo that = (GangliaHostInfo) o;

        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        return result;
    }
}
