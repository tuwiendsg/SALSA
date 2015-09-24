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
import java.util.List;
import java.util.Objects;

/**
 *
 * This class acts as a container for all the information of Salsa Virtual Machine instances
 *
 * @author Duc-Hung Le TODO: Unified instance type. Currently: use String.
 */
public class VirtualMachineInfo extends DomainEntity {

    // info get from SALSA
    protected String provider;
    protected String baseImageID;
    protected String baseImageName;
    protected String instanceId;
    protected String privateIp;
    protected String publicIp;
    protected PackagesDependencies packagesDependencies;

    // Some info can get from OpenStack-based cloud
    protected String configDrive;
    protected String flavorName;
    protected String flavorID;
    protected String hostId;
    protected String keyname;
    protected String status;
    protected String tenantId;
    protected String updated;
    protected String userID;
    
    // some info at Operating system level
    protected String osName;
    protected String osArch;
    protected String osVersion;
    protected String sunOsPatchLevel;
    protected String sunCpuEndian;
    
    protected String javaVersion;
    protected String javaVendor;    
    protected String javaRuntimeName;    
    
    protected String fileEncoding;
    protected String fileEncodingPacket;


    public enum State {
        unknown, spawning, configuring, running, stopped, error
    }

    public VirtualMachineInfo() {
    }

    public VirtualMachineInfo(String provider, String instanceId, String name) {
        super(ServiceCategory.VirtualMachine, instanceId, name);
        this.provider = provider;
        this.instanceId = instanceId;
        
        // we always need this to update the unified state
        updateStateList(State.values());
    }

    @Override
    public String toString() {
        return "SalsaInstanceDescription [provider=" + provider
                + ", instanceId=" + instanceId + ", privateIp=" + privateIp + ", state=" + status + "]";
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

//    public Map<String, String> exportToMap() {
//        Map<String, String> resMap = new HashMap<>();
//        Map<String, String> map = new HashMap<>();
//        map.put("provider", this.provider);
//        map.put("baseImage", this.baseImage);
//        map.put("instanceType", this.instanceType);
//        map.put("instanceId", this.instanceId);
//        map.put("publicIp", this.publicIp);
//        map.put("privateIp", this.privateIp);
//        map.put("publicDNS", this.publicDNS);
//        Iterator iterator = map.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator.next();
//            if (mapEntry.getValue() != null) {
//                resMap.put(mapEntry.getKey(), mapEntry.getValue());
//            }
//        }
//
//        return resMap;
//    }
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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getBaseImageID() {
        return baseImageID;
    }

    public void setBaseImageID(String baseImageID) {
        this.baseImageID = baseImageID;
    }

    public String getBaseImageName() {
        return baseImageName;
    }

    public void setBaseImageName(String baseImageName) {
        this.baseImageName = baseImageName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getPrivateIp() {
        return privateIp;
    }

    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public PackagesDependencies getPackagesDependencies() {
        return packagesDependencies;
    }

    public void setPackagesDependencies(PackagesDependencies packagesDependencies) {
        this.packagesDependencies = packagesDependencies;
    }

    public String getConfigDrive() {
        return configDrive;
    }

    public void setConfigDrive(String configDrive) {
        this.configDrive = configDrive;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public void setFlavorName(String flavorName) {
        this.flavorName = flavorName;
    }

    public String getFlavorID() {
        return flavorID;
    }

    public void setFlavorID(String flavorID) {
        this.flavorID = flavorID;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getKeyname() {
        return keyname;
    }

    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getSunOsPatchLevel() {
        return sunOsPatchLevel;
    }

    public void setSunOsPatchLevel(String sunOsPatchLevel) {
        this.sunOsPatchLevel = sunOsPatchLevel;
    }

    public String getSunCpuEndian() {
        return sunCpuEndian;
    }

    public void setSunCpuEndian(String sunCpuEndian) {
        this.sunCpuEndian = sunCpuEndian;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getJavaVendor() {
        return javaVendor;
    }

    public void setJavaVendor(String javaVendor) {
        this.javaVendor = javaVendor;
    }

    public String getJavaRuntimeName() {
        return javaRuntimeName;
    }

    public void setJavaRuntimeName(String javaRuntimeName) {
        this.javaRuntimeName = javaRuntimeName;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public String getFileEncodingPacket() {
        return fileEncodingPacket;
    }

    public void setFileEncodingPacket(String fileEncodingPacket) {
        this.fileEncodingPacket = fileEncodingPacket;
    }    
}
