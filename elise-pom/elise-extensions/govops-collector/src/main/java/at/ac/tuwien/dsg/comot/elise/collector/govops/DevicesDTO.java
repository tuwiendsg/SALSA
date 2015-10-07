/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.comot.elise.collector.govops;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungld
 */
public class DevicesDTO {

    private List<DeviceDTO> devices = new ArrayList<>();

    public void setDevices(List<DeviceDTO> devices) {
        this.devices = devices;
    }

    public List<DeviceDTO> getDevices() {
        return this.devices;
    }

    public void addDTO(DeviceDTO dto) {
        this.devices.add(dto);
    }
}
