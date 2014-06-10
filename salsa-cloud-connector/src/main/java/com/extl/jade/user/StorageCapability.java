
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for storageCapability.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="storageCapability">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CLONE"/>
 *     &lt;enumeration value="SNAPSHOT"/>
 *     &lt;enumeration value="CHILDREN_PERSIST_ON_DELETE"/>
 *     &lt;enumeration value="CHILDREN_PERSIST_ON_REVERT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "storageCapability")
@XmlEnum
public enum StorageCapability {


    /**
     * Storage unit is natively capable of cloning
     * 
     */
    CLONE,

    /**
     * Storage unit is natively capable of snapshotting
     * 
     */
    SNAPSHOT,

    /**
     * The child objects of a disk on a storage unit persist on deletion of that disk
     * 
     */
    CHILDREN_PERSIST_ON_DELETE,

    /**
     * The child objects of a disk on a storage unit persist on reversion to an earlier snapshot
     * 
     */
    CHILDREN_PERSIST_ON_REVERT;

    public String value() {
        return name();
    }

    public static StorageCapability fromValue(String v) {
        return valueOf(v);
    }

}
