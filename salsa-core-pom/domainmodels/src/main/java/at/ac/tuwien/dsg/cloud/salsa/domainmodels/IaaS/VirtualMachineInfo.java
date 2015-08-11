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

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * This class acts as a container for all the information of Salsa Virtual Machine instances
 *
 * @author Duc-Hung Le TODO:
 * Unified instance type. Currently: use String.
 */
public class VirtualMachineInfo extends DomainEntity {

    protected String provider;

    protected String baseImage;

    protected String instanceType;

    protected String instanceId;

    protected String privateIp;

    protected String publicIp;

    protected String privateDNS;

    protected String publicDNS;

    protected String state;

    protected PackagesDependencies packagesDependencies;

    public VirtualMachineInfo() {
        super(ServiceCategory.VirtualMachine, "unknown");
    }

    public VirtualMachineInfo(String provider, String instanceId) {
        super(ServiceCategory.VirtualMachine, instanceId);
        this.provider = provider;
        this.instanceId = instanceId;        
    }

    public String getProvider() {
        return provider;
    }

    public String getBaseImage() {
        return baseImage;
    }

    public String getInstanceType() {
        return instanceType;
    }

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

    public String getState() {
        return state;
    }

    public PackagesDependencies getPackagesDependencies() {
        return packagesDependencies;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setBaseImage(String baseImage) {
        this.baseImage = baseImage;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

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

    public void setState(String state) {
        this.state = state;
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
        if (obj instanceof VirtualMachineInfo) {
            return instanceId.equals(((VirtualMachineInfo) obj)
                    .getInstanceId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.instanceId);
        return hash;
    }

    public Map<String, String> exportToMap() {
        Map<String, String> resMap = new HashMap<>();
        Map<String, String> map = new HashMap<>();
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

    public static class PackagesDependencies {

        List<String> packageDependency;

        public List<String> getPackageDependency() {
            return packageDependency;
        }

        public void setPackageDependency(List<String> packageDependency) {
            if (this.packageDependency == null) {
                this.packageDependency = new ArrayList<>();
            }
            this.packageDependency = packageDependency;
        }

        @Override
        public String toString() {
            return "PackagesDependencies [packageDependency="
                    + packageDependency + "]";
        }

    }

}
