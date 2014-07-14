
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for email.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="email">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="YES"/>
 *     &lt;enumeration value="NO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "email")
@XmlEnum
public enum Email {


    /**
     * The BE can send email
     * 
     */
    YES,

    /**
     * The BE cannot send email
     * 
     */
    NO;

    public String value() {
        return name();
    }

    public static Email fromValue(String v) {
        return valueOf(v);
    }

}
