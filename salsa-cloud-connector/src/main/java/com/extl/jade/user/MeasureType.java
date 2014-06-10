
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for measureType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="measureType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="KB"/>
 *     &lt;enumeration value="MB"/>
 *     &lt;enumeration value="GB"/>
 *     &lt;enumeration value="NUMERIC"/>
 *     &lt;enumeration value="STRING"/>
 *     &lt;enumeration value="RESOURCE_UUID"/>
 *     &lt;enumeration value="UNIT"/>
 *     &lt;enumeration value="CURRENCY"/>
 *     &lt;enumeration value="TB"/>
 *     &lt;enumeration value="B"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "measureType")
@XmlEnum
public enum MeasureType {


    /**
     * Kilobytes
     * 
     */
    KB,

    /**
     * Megabytes
     * 
     */
    MB,

    /**
     * Gigabytes
     * 
     */
    GB,

    /**
     * A numeric unitless value
     * 
     */
    NUMERIC,

    /**
     * String value 
     * 
     */
    STRING,

    /**
     * The UUID of a resource
     * 
     */
    RESOURCE_UUID,

    /**
     * Units
     * 
     */
    UNIT,

    /**
     * Currency
     * 
     */
    CURRENCY,

    /**
     * Terabytes
     * 
     */
    TB,

    /**
     * Bytes
     * 
     */
    B;

    public String value() {
        return name();
    }

    public static MeasureType fromValue(String v) {
        return valueOf(v);
    }

}
