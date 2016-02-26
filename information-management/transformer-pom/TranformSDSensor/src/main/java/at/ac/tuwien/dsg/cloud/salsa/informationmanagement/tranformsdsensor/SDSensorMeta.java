/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.tranformsdsensor;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hungld
 */
public class SDSensorMeta {

    String name;
    String type;
    String rate;
    String protocol;

    Map<String, String> actions = new HashMap<>();
    Map<String, String> extra = new HashMap<>();

    public SDSensorMeta() {
    }

    public SDSensorMeta(String name, String type, String rate, String protocol) {
        this.name = name;
        this.type = type;
        this.rate = rate;
        this.protocol = protocol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, String> getActions() {
        return actions;
    }

    public void setActions(Map<String, String> actions) {
        this.actions = actions;
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }

}
