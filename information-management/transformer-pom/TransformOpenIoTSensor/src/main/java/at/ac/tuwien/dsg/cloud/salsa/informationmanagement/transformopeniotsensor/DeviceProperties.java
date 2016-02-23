/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.transformopeniotsensor;

/**
 *
 * @author hungld
 */
public class DeviceProperties {

    String commandURL;
    String lastIP;
    boolean commands;

    public DeviceProperties() {
    }

    public String getCommandURL() {
        return commandURL;
    }

    public void setCommandURL(String commandURL) {
        this.commandURL = commandURL;
    }

    public String getLastIP() {
        return lastIP;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    public boolean isCommands() {
        return commands;
    }

    public void setCommands(boolean commands) {
        this.commands = commands;
    }

}
