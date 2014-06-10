
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for icmpParam.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="icmpParam">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ECHO_REPLY_IPv4"/>
 *     &lt;enumeration value="ECHO_REQUEST_IPv4"/>
 *     &lt;enumeration value="DESTINATION_UNREACHABLE"/>
 *     &lt;enumeration value="SOURCE_QUENCH"/>
 *     &lt;enumeration value="REDIRECT_MESSAGE_IPv4"/>
 *     &lt;enumeration value="ALTERNATE_HOST_ADDRESS"/>
 *     &lt;enumeration value="ROUTER_ADVERTISEMENT_IPv4"/>
 *     &lt;enumeration value="ROUTER_SOLICITATION_IPv4"/>
 *     &lt;enumeration value="TIME_EXCEEDED"/>
 *     &lt;enumeration value="PARAMETER_PROBLEM"/>
 *     &lt;enumeration value="TIMESTAMP"/>
 *     &lt;enumeration value="TIMESTAMP_REPLY"/>
 *     &lt;enumeration value="INFORMATION_REQUEST"/>
 *     &lt;enumeration value="INFORMATION_REPLY"/>
 *     &lt;enumeration value="ADDRESS_MASK_REQUEST"/>
 *     &lt;enumeration value="ADDRESS_MASK_REPLY"/>
 *     &lt;enumeration value="ECHO_REQUEST_IPv6"/>
 *     &lt;enumeration value="ECHO_REPLY_IPv6"/>
 *     &lt;enumeration value="MULTICAST_LISTENER_QUERY"/>
 *     &lt;enumeration value="MULTICAST_LISTENER_REPORT"/>
 *     &lt;enumeration value="MULTICAST_LISTENER_DONE"/>
 *     &lt;enumeration value="ROUTER_SOLICITATION_IPv6"/>
 *     &lt;enumeration value="ROUTER_ADVERTISEMENT_IPv6"/>
 *     &lt;enumeration value="NEIGHBOR_SOLICITATION"/>
 *     &lt;enumeration value="NEIGHBOR_ADVERTISEMENT"/>
 *     &lt;enumeration value="REDIRECT_MESSAGE_IPv6"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "icmpParam")
@XmlEnum
public enum IcmpParam {


    /**
     * ICMP echo reply
     * 
     */
    @XmlEnumValue("ECHO_REPLY_IPv4")
    ECHO_REPLY_I_PV_4("ECHO_REPLY_IPv4"),

    /**
     * ICMP echo response
     * 
     */
    @XmlEnumValue("ECHO_REQUEST_IPv4")
    ECHO_REQUEST_I_PV_4("ECHO_REQUEST_IPv4"),

    /**
     * ICMP destination unreachable
     * 
     */
    DESTINATION_UNREACHABLE("DESTINATION_UNREACHABLE"),

    /**
     * ICMP source quench
     * 
     */
    SOURCE_QUENCH("SOURCE_QUENCH"),

    /**
     * ICMP redirect
     * 
     */
    @XmlEnumValue("REDIRECT_MESSAGE_IPv4")
    REDIRECT_MESSAGE_I_PV_4("REDIRECT_MESSAGE_IPv4"),

    /**
     * ICMP alternate host address
     * 
     */
    ALTERNATE_HOST_ADDRESS("ALTERNATE_HOST_ADDRESS"),

    /**
     * ICMP router advertisement
     * 
     */
    @XmlEnumValue("ROUTER_ADVERTISEMENT_IPv4")
    ROUTER_ADVERTISEMENT_I_PV_4("ROUTER_ADVERTISEMENT_IPv4"),

    /**
     * ICMP router solicitation
     * 
     */
    @XmlEnumValue("ROUTER_SOLICITATION_IPv4")
    ROUTER_SOLICITATION_I_PV_4("ROUTER_SOLICITATION_IPv4"),

    /**
     * ICMP time exceeded
     * 
     */
    TIME_EXCEEDED("TIME_EXCEEDED"),

    /**
     * ICMP parameter problem
     * 
     */
    PARAMETER_PROBLEM("PARAMETER_PROBLEM"),

    /**
     * ICMP timestamp
     * 
     */
    TIMESTAMP("TIMESTAMP"),

    /**
     * ICMP timestamp reply
     * 
     */
    TIMESTAMP_REPLY("TIMESTAMP_REPLY"),

    /**
     * ICMP information request
     * 
     */
    INFORMATION_REQUEST("INFORMATION_REQUEST"),

    /**
     * ICMP information reply
     * 
     */
    INFORMATION_REPLY("INFORMATION_REPLY"),

    /**
     * ICMP address mask request
     * 
     */
    ADDRESS_MASK_REQUEST("ADDRESS_MASK_REQUEST"),

    /**
     * ICMP address mask reply
     * 
     */
    ADDRESS_MASK_REPLY("ADDRESS_MASK_REPLY"),

    /**
     * ICMPv6 echo request
     * 
     */
    @XmlEnumValue("ECHO_REQUEST_IPv6")
    ECHO_REQUEST_I_PV_6("ECHO_REQUEST_IPv6"),

    /**
     * ICMPv6 echo reply
     * 
     */
    @XmlEnumValue("ECHO_REPLY_IPv6")
    ECHO_REPLY_I_PV_6("ECHO_REPLY_IPv6"),

    /**
     * ICMPv6 multicast listener query
     * 
     */
    MULTICAST_LISTENER_QUERY("MULTICAST_LISTENER_QUERY"),

    /**
     * ICMPv6 multicast listener report
     * 
     */
    MULTICAST_LISTENER_REPORT("MULTICAST_LISTENER_REPORT"),

    /**
     * ICMPv6 multicast listener done
     * 
     */
    MULTICAST_LISTENER_DONE("MULTICAST_LISTENER_DONE"),

    /**
     * ICMPv6 router solicitation
     * 
     */
    @XmlEnumValue("ROUTER_SOLICITATION_IPv6")
    ROUTER_SOLICITATION_I_PV_6("ROUTER_SOLICITATION_IPv6"),

    /**
     * ICMPv6 router advertisement
     * 
     */
    @XmlEnumValue("ROUTER_ADVERTISEMENT_IPv6")
    ROUTER_ADVERTISEMENT_I_PV_6("ROUTER_ADVERTISEMENT_IPv6"),

    /**
     * ICMPv6 neighbor solicitation
     * 
     */
    NEIGHBOR_SOLICITATION("NEIGHBOR_SOLICITATION"),

    /**
     * ICMPv6 neighbor advertisement
     * 
     */
    NEIGHBOR_ADVERTISEMENT("NEIGHBOR_ADVERTISEMENT"),

    /**
     * ICMPv6 redirect
     * 
     */
    @XmlEnumValue("REDIRECT_MESSAGE_IPv6")
    REDIRECT_MESSAGE_I_PV_6("REDIRECT_MESSAGE_IPv6");
    private final String value;

    IcmpParam(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcmpParam fromValue(String v) {
        for (IcmpParam c: IcmpParam.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
