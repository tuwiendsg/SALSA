
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for firewallRuleAction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="firewallRuleAction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ALLOW"/>
 *     &lt;enumeration value="REJECT"/>
 *     &lt;enumeration value="DROP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "firewallRuleAction")
@XmlEnum
public enum FirewallRuleAction {


    /**
     * Allow the packet through
     * 
     */
    ALLOW,

    /**
     * Reject the packet with an unreachable
     * 
     */
    REJECT,

    /**
     * Drop the packet
     * 
     */
    DROP;

    public String value() {
        return name();
    }

    public static FirewallRuleAction fromValue(String v) {
        return valueOf(v);
    }

}
