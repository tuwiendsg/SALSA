
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resourceState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="resourceState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TO_BE_DELETED"/>
 *     &lt;enumeration value="DELETED"/>
 *     &lt;enumeration value="ACTIVE"/>
 *     &lt;enumeration value="HIDDEN"/>
 *     &lt;enumeration value="LOCKED"/>
 *     &lt;enumeration value="CREATING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "resourceState")
@XmlEnum
public enum ResourceState {


    /**
     * Deletion pending on deletion of child objects
     * 
     */
    TO_BE_DELETED,

    /**
     * Deleted
     * 
     */
    DELETED,

    /**
     * Active
     * 
     */
    ACTIVE,

    /**
     * Hidden
     * 
     */
    HIDDEN,

    /**
     * Locked
     * 
     */
    LOCKED,

    /**
     * Creating
     * 
     */
    CREATING;

    public String value() {
        return name();
    }

    public static ResourceState fromValue(String v) {
        return valueOf(v);
    }

}
