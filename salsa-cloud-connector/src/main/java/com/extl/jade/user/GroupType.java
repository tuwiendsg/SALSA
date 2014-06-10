
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for groupType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="groupType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NORMAL"/>
 *     &lt;enumeration value="ADMIN"/>
 *     &lt;enumeration value="EVERYONE"/>
 *     &lt;enumeration value="LOCKED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "groupType")
@XmlEnum
public enum GroupType {


    /**
     * A normal group
     * 
     */
    NORMAL,

    /**
     * The admin group
     * 
     */
    ADMIN,

    /**
     * The everyone group
     * 
     */
    EVERYONE,

    /**
     * The locked group
     * 
     */
    LOCKED;

    public String value() {
        return name();
    }

    public static GroupType fromValue(String v) {
        return valueOf(v);
    }

}
