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
package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;
import java.util.Objects;

/**
 *
 * This class acts as a container for all the information of Salsa Virtual Machine instances
 *
 * @author Duc-Hung Le TODO: Unified instance type. Currently: use String.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "SalsaInstanceDescription_VM")
public class SalsaInstanceDescription_VM {

    @XmlElement(name = "provider")
    protected String provider;

    @XmlElement(name = "baseImage")
    protected String baseImage;

    @XmlElement(name = "instanceType")
    protected String instanceType;

    @XmlElement(name = "id")
    protected String instanceId;

    @XmlElement(name = "privateIp")
    protected String privateIp;
    @XmlElement(name = "publicIP")
    protected String publicIp;
    @XmlElement(name = "privateDNS")
    protected String privateDNS;
    @XmlElement(name = "publicDNS")
    protected String publicDNS;

    @XmlElement(name = "state")
    protected String state;

    @XmlElement(name = "quota")
    protected int quota;

    @XmlElement(name = "Packages")
    protected PackagesDependencies packagesDependencies;

    public SalsaInstanceDescription_VM() {
    }

    public SalsaInstanceDescription_VM(String provider, String instanceId) {
        this.provider = provider;
        this.instanceId = instanceId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    //	public int getReplicaNumber() {
//		return replicaNumber;
//	}
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public void setPrivateDNS(String privateDNS) {
        this.privateDNS = privateDNS;
    }

    public void setPublicDNS(String publicDNS) {
        this.publicDNS = publicDNS;
    }
//	public void setReplicaNumber(int replicaNumber) {
//		this.replicaNumber = replicaNumber;
//	}

    public String getInstanceId() {
        return instanceId;
    }

    public String getPrivateIp() {
        return privateIp;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public String getPrivateDNS() {
        return privateDNS;
    }

    public String getPublicDNS() {
        return publicDNS;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getBaseImage() {
        return baseImage;
    }

    public void setBaseImage(String baseImage) {
        this.baseImage = baseImage;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public PackagesDependencies getPackagesDependenciesList() {
        if (this.packagesDependencies==null){
            this.packagesDependencies = new PackagesDependencies();
        }
        return packagesDependencies;
    }

    public void setPackagesDependencies(PackagesDependencies packagesDependencies) {
        this.packagesDependencies = packagesDependencies;
    }

    @Override
    public String toString() {
        return "SalsaInstanceDescription [provider=" + provider
                + ", baseImage=" + baseImage + ", instanceType=" + instanceType
                + ", instanceId=" + instanceId + ", privateIp=" + privateIp
                + ", publicIp=" + publicIp + ", privateDNS=" + privateDNS
                + ", publicDNS=" + publicDNS + ", state=" + state + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SalsaInstanceDescription_VM) {
            return instanceId.equals(((SalsaInstanceDescription_VM) obj)
                    .getInstanceId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.instanceId);
        return hash;
    }

    public void updateFromMappingProperties(SalsaMappingProperties maps) {
        for (SalsaMappingProperty map : maps.getProperties()) {
            if (map.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
                this.provider = map.get("provider");
                this.baseImage = map.get("baseImage");
                this.instanceType = map.get("instanceType");
                String packageStr = map.get("packages");
                if (packageStr != null) {
                    List<String> packagelist = new ArrayList<>(Arrays.asList(packageStr.split(",")));
                    this.packagesDependencies = new PackagesDependencies();
                    this.packagesDependencies.setPackageDependency(packagelist);
                }
            }
        }
    }

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
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator.next();
            if (mapEntry.getValue() != null) {
                resMap.put(mapEntry.getKey(), mapEntry.getValue());
            }
        }

        return resMap;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "Packages")
    public static class PackagesDependencies {

        @XmlElement(name = "Package")
        List<String> packageDependency = new ArrayList<>();

        public List<String> getPackageDependency() {
            return packageDependency;
        }

        public void setPackageDependency(List<String> packageDependency) {            
            this.packageDependency = packageDependency;
        }

        @Override
        public String toString() {
            return "PackagesDependencies [packageDependency="
                    + packageDependency + "]";
        }

    }

}
