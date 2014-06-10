
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for capability.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="capability">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CREATE"/>
 *     &lt;enumeration value="DELETE"/>
 *     &lt;enumeration value="MODIFY"/>
 *     &lt;enumeration value="ATTACH_DETACH"/>
 *     &lt;enumeration value="START_STOP"/>
 *     &lt;enumeration value="PUBLISH"/>
 *     &lt;enumeration value="FETCH"/>
 *     &lt;enumeration value="CLONE"/>
 *     &lt;enumeration value="CHANGE_PERMISSIONS"/>
 *     &lt;enumeration value="DO_TRANSACTION"/>
 *     &lt;enumeration value="ALL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "capability")
@XmlEnum
public enum Capability {


    /**
     * Create resources
     * 
     */
    CREATE,

    /**
     * Delete resources
     * 
     */
    DELETE,

    /**
     * Modify resources
     * 
     */
    MODIFY,

    /**
     * Attach or detach resources
     * 
     */
    ATTACH_DETACH,

    /**
     * Start, stop, or change the status of resources (including reboot and kill)
     * 
     */
    START_STOP,

    /**
     * Publish resources
     * 
     */
    PUBLISH,

    /**
     * Fetch resources
     * 
     */
    FETCH,

    /**
     * Clone resources
     * 
     */
    CLONE,

    /**
     * Change permissions on resources
     * 
     */
    CHANGE_PERMISSIONS,

    /**
     * Do a payment transaction
     * 
     */
    DO_TRANSACTION,

    /**
     * Do any action with resources
     * 
     */
    ALL;

    public String value() {
        return name();
    }

    public static Capability fromValue(String v) {
        return valueOf(v);
    }

}
