
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for maxLimit.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="maxLimit">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MAX_DISK_PER_SERVER"/>
 *     &lt;enumeration value="MAX_NIC_PER_SERVER"/>
 *     &lt;enumeration value="MAX_REFERRAL_UNIT_CAP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "maxLimit")
@XmlEnum
public enum MaxLimit {


    /**
     * Maximum number of disks that can be attached to a server
     * 
     */
    MAX_DISK_PER_SERVER,

    /**
     * Maximum number of NICs that can be attached to a server
     * 
     */
    MAX_NIC_PER_SERVER,

    /**
     * Maximum referral units a customer can be entitled to
     * 
     */
    MAX_REFERRAL_UNIT_CAP;

    public String value() {
        return name();
    }

    public static MaxLimit fromValue(String v) {
        return valueOf(v);
    }

}
