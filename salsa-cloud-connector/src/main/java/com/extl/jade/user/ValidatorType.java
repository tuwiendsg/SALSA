
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for validatorType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="validatorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ENUM"/>
 *     &lt;enumeration value="REGEX"/>
 *     &lt;enumeration value="NUMERIC_INT"/>
 *     &lt;enumeration value="NUMERIC_DOUBLE"/>
 *     &lt;enumeration value="PASSWORD"/>
 *     &lt;enumeration value="DATE"/>
 *     &lt;enumeration value="BIG_TEXT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "validatorType")
@XmlEnum
public enum ValidatorType {


    /**
     * An enum, i.e. a choice of a preset list of options
     * 
     */
    ENUM,

    /**
     * A regular expression
     * 
     */
    REGEX,

    /**
     * An integer value
     * 
     */
    NUMERIC_INT,

    /**
     * A floating point value
     * 
     */
    NUMERIC_DOUBLE,

    /**
     * A password
     * 
     */
    PASSWORD,

    /**
     * A date
     * 
     */
    DATE,

    /**
     * A text area
     * 
     */
    BIG_TEXT;

    public String value() {
        return name();
    }

    public static ValidatorType fromValue(String v) {
        return valueOf(v);
    }

}
