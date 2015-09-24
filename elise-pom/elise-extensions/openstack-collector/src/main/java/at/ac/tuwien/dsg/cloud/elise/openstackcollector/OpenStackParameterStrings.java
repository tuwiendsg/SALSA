/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.openstackcollector;

/**
 *
 * @author Duc-Hung Le
 */
public enum OpenStackParameterStrings {

    PORT("port"),
    SSH_KEY_NAME("sshKeyName"),
    END_POINT("end_point"),
    USERNAME("username"),
    PASSWORD("password"),
    TENANT("tenant"),
    KEYSTONE_ENDPOINT("keystone_endpoint");

    private String value;

    private OpenStackParameterStrings(String value) {
        this.value = value;
    }

    public String getString() {
        return this.value;
    }

    @Deprecated
    public static OpenStackParameterStrings fromString(String text) {
        if (text != null) {
            for (OpenStackParameterStrings b : values()) {
                if (text.equalsIgnoreCase(b.getString())) {
                    return b;
                }
            }
        }
        return null;
    }
}