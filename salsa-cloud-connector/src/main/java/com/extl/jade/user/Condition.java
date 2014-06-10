
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for condition.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="condition">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="IS_EQUAL_TO"/>
 *     &lt;enumeration value="IS_NOT_EQUAL_TO"/>
 *     &lt;enumeration value="IS_GREATER_THAN"/>
 *     &lt;enumeration value="IS_LESS_THAN"/>
 *     &lt;enumeration value="IS_GREATER_THAN_OR_EQUAL_TO"/>
 *     &lt;enumeration value="IS_LESS_THAN_OR_EQUAL_TO"/>
 *     &lt;enumeration value="CONTAINS"/>
 *     &lt;enumeration value="NOT_CONTAINS"/>
 *     &lt;enumeration value="STARTS_WITH"/>
 *     &lt;enumeration value="NOT_STARTS_WITH"/>
 *     &lt;enumeration value="ENDS_WITH"/>
 *     &lt;enumeration value="NOT_ENDS_WITH"/>
 *     &lt;enumeration value="BETWEEN"/>
 *     &lt;enumeration value="NOT_BETWEEN"/>
 *     &lt;enumeration value="LATER_THAN"/>
 *     &lt;enumeration value="EARLIER_THAN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "condition")
@XmlEnum
public enum Condition {


    /**
     * True if FQL field concerned is equal to one of the values supplied as an array
     * 
     */
    IS_EQUAL_TO,

    /**
     * True if FQL field concerned is not equal to any of the values supplied as an array
     * 
     */
    IS_NOT_EQUAL_TO,

    /**
     * True if FQL field concerned is greater than the value supplied
     * 
     */
    IS_GREATER_THAN,

    /**
     * True if FQL field concerned is less than the value supplied
     * 
     */
    IS_LESS_THAN,

    /**
     * True if FQL field concerned is greater than or equal to than the value supplied
     * 
     */
    IS_GREATER_THAN_OR_EQUAL_TO,

    /**
     * True if FQL field concerned is less than or equal to than the value supplied
     * 
     */
    IS_LESS_THAN_OR_EQUAL_TO,

    /**
     * True if FQL field concerned when parsed as a string contains the value supplied
     * 
     */
    CONTAINS,

    /**
     * True if FQL field concerned when parsed as a string does contain the value supplied
     * 
     */
    NOT_CONTAINS,

    /**
     * True if FQL field concerned when parsed as a string starts with the value supplied
     * 
     */
    STARTS_WITH,

    /**
     * True if FQL field concerned when parsed as a string does not start with the value supplied
     * 
     */
    NOT_STARTS_WITH,

    /**
     * True if FQL field concerned when parsed as a string ends with the value supplied
     * 
     */
    ENDS_WITH,

    /**
     * True if FQL field concerned when parsed as a string does not end with the value supplied
     * 
     */
    NOT_ENDS_WITH,

    /**
     * True if FQL field concerned lies between the two values supplied (i.e. is greater than or equal to the first and is less than or equal to the second)
     * 
     */
    BETWEEN,

    /**
     * True if FQL field concerned does not lie between the two values supplied (i.e. is less than the first or is greater than the second)
     * 
     */
    NOT_BETWEEN,

    /**
     * True if FQL field concerned is later than the value supplied
     * 
     */
    LATER_THAN,

    /**
     * True if FQL field concerned is earlier than the value supplied
     * 
     */
    EARLIER_THAN;

    public String value() {
        return name();
    }

    public static Condition fromValue(String v) {
        return valueOf(v);
    }

}
