/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.comot.elise.collector.govops;

import java.util.HashMap;

/**
 *
 * @author hungld
 */
public class DeviceDTO {

    public String id;
    public String name;
    public String ipAddress;
    public String metaInfo;
    private HashMap<String, String> meta = new HashMap<>();

    public DeviceDTO() {
    }

    public DeviceDTO(String id, String name, String metaInfo) {
        this.id = id;
        this.name = name;
        this.ipAddress = "";
        this.metaInfo = metaInfo;
    }

    public void addMetaData(String key, String value) {
        this.meta.put(key, value);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "Device:[id: " + this.id + ", IPAddr: " + this.ipAddress + ", metaInfo: " + this.meta + "]";
    }

    public HashMap<String, String> getMeta() {
        return this.meta;
    }

    public void setMeta(HashMap<String, String> meta) {
        this.meta = meta;
    }
}
