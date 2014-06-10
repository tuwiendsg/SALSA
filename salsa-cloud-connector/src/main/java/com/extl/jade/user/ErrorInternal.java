
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for errorInternal.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="errorInternal">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FUNCTION_FAILED"/>
 *     &lt;enumeration value="FAILED_TO_SET_DISK_AS_BOOT_DISK"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_NETWORK"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_VLAN_ON_NETWORK"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_SUBNET"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_IMAGE"/>
 *     &lt;enumeration value="FAILED_TO_MAKE_PURCHASE"/>
 *     &lt;enumeration value="FAILED_TO_CANCEL_JOB"/>
 *     &lt;enumeration value="FAILED_TO_DETACH_DISK_FROM_SERVER"/>
 *     &lt;enumeration value="FAILED_TO_DELETE_DISK"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_NIC"/>
 *     &lt;enumeration value="FAILED_TO_DELETE_FIREWALL"/>
 *     &lt;enumeration value="FAILED_TO_DELETE_SERVER"/>
 *     &lt;enumeration value="FAILED_TO_DELETE_IMAGE"/>
 *     &lt;enumeration value="FAILED_TO_DELETE_NETWORK"/>
 *     &lt;enumeration value="FAILED_TO_DELETE_NIC"/>
 *     &lt;enumeration value="FAILED_TO_DELETE_SUBNET"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_FETCH_JOB"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_RESOURCE"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_SERVER"/>
 *     &lt;enumeration value="FAILED_TO_ATTACH_DISK_TO_SERVER"/>
 *     &lt;enumeration value="FAILED_TO_ATTACH_NIC_TO_SERVER"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_JOB"/>
 *     &lt;enumeration value="FAILED_TO_DETACH_NIC_FROM_SERVER"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_CUSTOMER"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_UNIT_BALANCE"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_PRODUCT_OFFER"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_USER"/>
 *     &lt;enumeration value="FAILED_TO_DEACTIVATE_PRODUCT_PURCHASE"/>
 *     &lt;enumeration value="FAILED_TO_FETCH_RESOURCE_JOB"/>
 *     &lt;enumeration value="FAILED_TO_FETCH_RESOURCE"/>
 *     &lt;enumeration value="FAILED_TO_FETCH_JOB"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_VDC"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_DISK"/>
 *     &lt;enumeration value="FAILED_TO_SET_BOOT_DISK"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_SNAPSHOT"/>
 *     &lt;enumeration value="FAILED_TO_GET_BOOT_DISK"/>
 *     &lt;enumeration value="FAILED_TO_MOVE_SUBNET_TO_NETWORK"/>
 *     &lt;enumeration value="FAILED_TO_CHANGE_NETWORK_FOR_NIC"/>
 *     &lt;enumeration value="FAILED_TO_ADD_IP_TO_NIC"/>
 *     &lt;enumeration value="FAILED_TO_RESIZE_DISK"/>
 *     &lt;enumeration value="FAILED_TO_UPDATE_SERVER"/>
 *     &lt;enumeration value="FAILED_TO_REMOVE_IP_FROM_NIC"/>
 *     &lt;enumeration value="FAILED_TO_MODIFY_FIREWALL"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_FIREWALL"/>
 *     &lt;enumeration value="FAILED_TO_LOAD_PERMISSIONS"/>
 *     &lt;enumeration value="FAILED_TO_STORE_CARD_DETAILS"/>
 *     &lt;enumeration value="FAILED_TO_LOAD_JOB"/>
 *     &lt;enumeration value="FQL_EXCEPTION"/>
 *     &lt;enumeration value="FAILED_TO_UNMARSHAL"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_DEPLOYMENT_TEMPLATE"/>
 *     &lt;enumeration value="FAILED_TRIGGER_FUNCTION"/>
 *     &lt;enumeration value="PAYMENT_ERROR"/>
 *     &lt;enumeration value="FAILED_TO_CREATE_BLOB"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "errorInternal")
@XmlEnum
public enum ErrorInternal {


    /**
     * An internal error occurred - please contact support
     * 
     */
    FUNCTION_FAILED,

    /**
     * Failed to set a disk as the boot disk
     * 
     */
    FAILED_TO_SET_DISK_AS_BOOT_DISK,

    /**
     * Failed to create a network
     * 
     */
    FAILED_TO_CREATE_NETWORK,

    /**
     * Failed to create a VLAN on a network
     * 
     */
    FAILED_TO_CREATE_VLAN_ON_NETWORK,

    /**
     * Failed to create a subnet
     * 
     */
    FAILED_TO_CREATE_SUBNET,

    /**
     * Failed to create an image
     * 
     */
    FAILED_TO_CREATE_IMAGE,

    /**
     * Failed to make a product purchase
     * 
     */
    FAILED_TO_MAKE_PURCHASE,

    /**
     * Failed to cancel a job
     * 
     */
    FAILED_TO_CANCEL_JOB,

    /**
     * Failed to detach a disk from the server
     * 
     */
    FAILED_TO_DETACH_DISK_FROM_SERVER,

    /**
     * Failed to delete a disk
     * 
     */
    FAILED_TO_DELETE_DISK,

    /**
     * Failed to create a NIC
     * 
     */
    FAILED_TO_CREATE_NIC,

    /**
     * Failed to delete a firewall
     * 
     */
    FAILED_TO_DELETE_FIREWALL,

    /**
     * Failed to delete a server
     * 
     */
    FAILED_TO_DELETE_SERVER,

    /**
     * Failed to delete an image
     * 
     */
    FAILED_TO_DELETE_IMAGE,

    /**
     * Failed to delete a network
     * 
     */
    FAILED_TO_DELETE_NETWORK,

    /**
     * Failed to delete a NIC
     * 
     */
    FAILED_TO_DELETE_NIC,

    /**
     * Failed to delete a subnet
     * 
     */
    FAILED_TO_DELETE_SUBNET,

    /**
     * Failed to create a fetch job
     * 
     */
    FAILED_TO_CREATE_FETCH_JOB,

    /**
     * Failed to create a resource
     * 
     */
    FAILED_TO_CREATE_RESOURCE,

    /**
     * Failed to create a server
     * 
     */
    FAILED_TO_CREATE_SERVER,

    /**
     * Failed to attach a disk to a server
     * 
     */
    FAILED_TO_ATTACH_DISK_TO_SERVER,

    /**
     * Failed to attach a NIC to a server
     * 
     */
    FAILED_TO_ATTACH_NIC_TO_SERVER,

    /**
     * Failed to create a job
     * 
     */
    FAILED_TO_CREATE_JOB,

    /**
     * Failed to detach a NIC from the server
     * 
     */
    FAILED_TO_DETACH_NIC_FROM_SERVER,

    /**
     * Failed to create a customer
     * 
     */
    FAILED_TO_CREATE_CUSTOMER,

    /**
     * Failed to create a unit balance
     * 
     */
    FAILED_TO_CREATE_UNIT_BALANCE,

    /**
     * Failed to create a product offer
     * 
     */
    FAILED_TO_CREATE_PRODUCT_OFFER,

    /**
     * Failed to create a user
     * 
     */
    FAILED_TO_CREATE_USER,

    /**
     * Failed to deactivate a product purchase
     * 
     */
    FAILED_TO_DEACTIVATE_PRODUCT_PURCHASE,

    /**
     * Failed to fetch an internal job resource
     * 
     */
    FAILED_TO_FETCH_RESOURCE_JOB,

    /**
     * Failed to fetch a resource
     * 
     */
    FAILED_TO_FETCH_RESOURCE,

    /**
     * Failed to fetch a job
     * 
     */
    FAILED_TO_FETCH_JOB,

    /**
     * Failed to create a VDC
     * 
     */
    FAILED_TO_CREATE_VDC,

    /**
     * Failed to create a disk
     * 
     */
    FAILED_TO_CREATE_DISK,

    /**
     * Failed to set the boot disk
     * 
     */
    FAILED_TO_SET_BOOT_DISK,

    /**
     * Failed to create a snapshot
     * 
     */
    FAILED_TO_CREATE_SNAPSHOT,

    /**
     * Failed to get the boot disk
     * 
     */
    FAILED_TO_GET_BOOT_DISK,

    /**
     * Failed to move a subnet to a network
     * 
     */
    FAILED_TO_MOVE_SUBNET_TO_NETWORK,

    /**
     * Failed to change the network attached to a NIC
     * 
     */
    FAILED_TO_CHANGE_NETWORK_FOR_NIC,

    /**
     * Failed to add an IP to a NIC
     * 
     */
    FAILED_TO_ADD_IP_TO_NIC,

    /**
     * Failed to resize a disk
     * 
     */
    FAILED_TO_RESIZE_DISK,

    /**
     * Failed to update a server
     * 
     */
    FAILED_TO_UPDATE_SERVER,

    /**
     * Failed to remove an IP address from a NIC
     * 
     */
    FAILED_TO_REMOVE_IP_FROM_NIC,

    /**
     * Failed to modify a firewall
     * 
     */
    FAILED_TO_MODIFY_FIREWALL,

    /**
     * Failed to create a firewall
     * 
     */
    FAILED_TO_CREATE_FIREWALL,

    /**
     * Failed to load permissions
     * 
     */
    FAILED_TO_LOAD_PERMISSIONS,

    /**
     * Failed to store card details
     * 
     */
    FAILED_TO_STORE_CARD_DETAILS,

    /**
     * Failed to load a job
     * 
     */
    FAILED_TO_LOAD_JOB,

    /**
     * FQL exception was thrown
     * 
     */
    FQL_EXCEPTION,

    /**
     * Failed to unmarshal the xml
     * 
     */
    FAILED_TO_UNMARSHAL,

    /**
     * Failed to create deployment template
     * 
     */
    FAILED_TO_CREATE_DEPLOYMENT_TEMPLATE,

    /**
     * Failed to execute trigger function.
     * 
     */
    FAILED_TRIGGER_FUNCTION,

    /**
     * Failed to do a customer payment.
     * 
     */
    PAYMENT_ERROR,

    /**
     * Failed to create blob
     * 
     */
    FAILED_TO_CREATE_BLOB;

    public String value() {
        return name();
    }

    public static ErrorInternal fromValue(String v) {
        return valueOf(v);
    }

}
