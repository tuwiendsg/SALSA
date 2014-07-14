
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for status.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="status">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DELETED"/>
 *     &lt;enumeration value="DISABLED"/>
 *     &lt;enumeration value="CLOSED"/>
 *     &lt;enumeration value="ACTIVE"/>
 *     &lt;enumeration value="ADMIN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "status")
@XmlEnum
public enum Status {


    /**
     * The customer has been deleted
     * 
     */
    DELETED,

    /**
     * The customer has been disabled
     * 
     */
    DISABLED,

    /**
     * The account has been closed
     * 
     */
    CLOSED,

    /**
     * The account is active, and a normal customer
     * 
     */
    ACTIVE,

    /**
     * The account is active, and an admin customer
     * 
     */
    ADMIN;

    public String value() {
        return name();
    }

    public static Status fromValue(String v) {
        return valueOf(v);
    }

}
