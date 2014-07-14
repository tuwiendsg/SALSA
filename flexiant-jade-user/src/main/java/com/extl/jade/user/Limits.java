
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for limits.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="limits">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MAX_VDCS"/>
 *     &lt;enumeration value="MAX_VLANS"/>
 *     &lt;enumeration value="MAX_SUBNETS"/>
 *     &lt;enumeration value="MAX_RAM"/>
 *     &lt;enumeration value="MAX_CPUS"/>
 *     &lt;enumeration value="MAX_SERVERS"/>
 *     &lt;enumeration value="MAX_DISKS"/>
 *     &lt;enumeration value="MAX_STORAGEGB"/>
 *     &lt;enumeration value="MAX_SNAPSHOTS"/>
 *     &lt;enumeration value="MAX_IMAGES"/>
 *     &lt;enumeration value="MAX_CUSTOMER_USERS"/>
 *     &lt;enumeration value="CREDIT_LIMIT"/>
 *     &lt;enumeration value="NO_3DS_28_DAY_SPEND_LIMIT"/>
 *     &lt;enumeration value="OVERALL_28_DAY_SPEND_LIMIT"/>
 *     &lt;enumeration value="CUTOFF_BALANCE"/>
 *     &lt;enumeration value="MAX_IPv4_ADDRESSES"/>
 *     &lt;enumeration value="MAX_IPv6_SUBNETS"/>
 *     &lt;enumeration value="MAX_NETWORK_PUBLIC"/>
 *     &lt;enumeration value="MAX_NETWORK_PRIVATE"/>
 *     &lt;enumeration value="REFUND"/>
 *     &lt;enumeration value="CUTOFF_DUE_DAYS"/>
 *     &lt;enumeration value="CREDIT_LIMIT_DUE_DAYS"/>
 *     &lt;enumeration value="MAX_BLOBS"/>
 *     &lt;enumeration value="MAX_BLOB_SIZE"/>
 *     &lt;enumeration value="CUTOFF_LIMIT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "limits")
@XmlEnum
public enum Limits {


    /**
     * The maximum number of VDCs permitted
     * 
     */
    MAX_VDCS("MAX_VDCS"),

    /**
     * The maximum number of VLANs permitted per VDC
     * 
     */
    MAX_VLANS("MAX_VLANS"),

    /**
     * The maximum number of subnets permitted
     * 
     */
    MAX_SUBNETS("MAX_SUBNETS"),

    /**
     * The maximum amount of RAM permitted across all servers
     * 
     */
    MAX_RAM("MAX_RAM"),

    /**
     * The maximum number of CPUs permitted across all servers
     * 
     */
    MAX_CPUS("MAX_CPUS"),

    /**
     * The maximum number of servers permitted
     * 
     */
    MAX_SERVERS("MAX_SERVERS"),

    /**
     * The maximum number of disks permitted
     * 
     */
    MAX_DISKS("MAX_DISKS"),

    /**
     * The maximum amount of storage (in GB) permitted across all servers
     * 
     */
    MAX_STORAGEGB("MAX_STORAGEGB"),

    /**
     * The maximum number of snapshots permitted
     * 
     */
    MAX_SNAPSHOTS("MAX_SNAPSHOTS"),

    /**
     * The maximum number of images permitted
     * 
     */
    MAX_IMAGES("MAX_IMAGES"),

    /**
     * The maximum number of contacts/users permitted
     * 
     */
    MAX_CUSTOMER_USERS("MAX_CUSTOMER_USERS"),

    /**
     * The maximum outstanding credit balance permitted
     * 
     */
    CREDIT_LIMIT("CREDIT_LIMIT"),

    /**
     * The maximum amount billed permissible in a 28 day period without 3DS security checks
     * 
     */
    @XmlEnumValue("NO_3DS_28_DAY_SPEND_LIMIT")
    NO_3_DS_28_DAY_SPEND_LIMIT("NO_3DS_28_DAY_SPEND_LIMIT"),

    /**
     * The maximum amount billed permissible in a 28 day period overall
     * 
     */
    OVERALL_28_DAY_SPEND_LIMIT("OVERALL_28_DAY_SPEND_LIMIT"),

    /**
     * The unit balance level at which customers should be cut off
     * 
     */
    CUTOFF_BALANCE("CUTOFF_BALANCE"),

    /**
     * The maximum allowed IPv4 address for the customer
     * 
     */
    @XmlEnumValue("MAX_IPv4_ADDRESSES")
    MAX_I_PV_4_ADDRESSES("MAX_IPv4_ADDRESSES"),

    /**
     * The maximum allowed IPv6 subnets for the customer
     * 
     */
    @XmlEnumValue("MAX_IPv6_SUBNETS")
    MAX_I_PV_6_SUBNETS("MAX_IPv6_SUBNETS"),

    /**
     * The maximum allowed public networks for the customer
     * 
     */
    MAX_NETWORK_PUBLIC("MAX_NETWORK_PUBLIC"),

    /**
     * The maximum allowed private networks for the customer
     * 
     */
    MAX_NETWORK_PRIVATE("MAX_NETWORK_PRIVATE"),

    /**
     * The boolean flag which controls refund generation
     * 
     */
    REFUND("REFUND"),

    /**
     * The cut-off invoice due days limit
     * 
     */
    CUTOFF_DUE_DAYS("CUTOFF_DUE_DAYS"),

    /**
     * The credit invoice due days limit
     * 
     */
    CREDIT_LIMIT_DUE_DAYS("CREDIT_LIMIT_DUE_DAYS"),

    /**
     * The maximum number of blobs allowed
     * 
     */
    MAX_BLOBS("MAX_BLOBS"),

    /**
     * The maximum size of blobs allowed
     * 
     */
    MAX_BLOB_SIZE("MAX_BLOB_SIZE"),

    /**
     * The cut off limit
     * 
     */
    CUTOFF_LIMIT("CUTOFF_LIMIT");
    private final String value;

    Limits(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Limits fromValue(String v) {
        for (Limits c: Limits.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
