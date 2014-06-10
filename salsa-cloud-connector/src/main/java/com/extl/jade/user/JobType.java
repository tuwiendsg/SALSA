
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for jobType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="jobType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CREATE_DISK"/>
 *     &lt;enumeration value="DELETE_DISK"/>
 *     &lt;enumeration value="ATTACH_DISK"/>
 *     &lt;enumeration value="DETACH_DISK"/>
 *     &lt;enumeration value="RESIZE_DISK"/>
 *     &lt;enumeration value="CLONE_DISK"/>
 *     &lt;enumeration value="RENAME_DISK"/>
 *     &lt;enumeration value="CREATE_SNAPSHOT"/>
 *     &lt;enumeration value="DELETE_SNAPSHOT"/>
 *     &lt;enumeration value="ADD_IP_TO_NIC"/>
 *     &lt;enumeration value="ATTACH_NIC"/>
 *     &lt;enumeration value="CREATE_NIC"/>
 *     &lt;enumeration value="DELETE_NIC"/>
 *     &lt;enumeration value="REMOVE_IP_FROM_NIC"/>
 *     &lt;enumeration value="CREATE_SERVER"/>
 *     &lt;enumeration value="DELETE_SERVER"/>
 *     &lt;enumeration value="MODIFY_SERVER"/>
 *     &lt;enumeration value="REVERT_SERVER"/>
 *     &lt;enumeration value="CREATE_SUBNET"/>
 *     &lt;enumeration value="MOVE_SUBNET"/>
 *     &lt;enumeration value="DELETE_SUBNET"/>
 *     &lt;enumeration value="CREATE_VDC"/>
 *     &lt;enumeration value="DELETE_VDC"/>
 *     &lt;enumeration value="CREATE_VLAN"/>
 *     &lt;enumeration value="DELETE_VLAN"/>
 *     &lt;enumeration value="CREATE_IMAGE_TEMPLATE"/>
 *     &lt;enumeration value="DELETE_IMAGE_TEMPLATE"/>
 *     &lt;enumeration value="START_SERVER"/>
 *     &lt;enumeration value="SHUTDOWN_SERVER"/>
 *     &lt;enumeration value="KILL_SERVER"/>
 *     &lt;enumeration value="REBOOT_SERVER"/>
 *     &lt;enumeration value="REVERT_DISK"/>
 *     &lt;enumeration value="CANCEL_JOB"/>
 *     &lt;enumeration value="SCHEDULED_JOB"/>
 *     &lt;enumeration value="MODIFY_DISK"/>
 *     &lt;enumeration value="DETACH_NIC"/>
 *     &lt;enumeration value="FETCH_DISK"/>
 *     &lt;enumeration value="FETCH_IMAGE"/>
 *     &lt;enumeration value="FIREWALL_CREATE"/>
 *     &lt;enumeration value="FIREWALL_RULE_ADD"/>
 *     &lt;enumeration value="FIREWALL_RULE_MODIFY"/>
 *     &lt;enumeration value="DELETE_FIREWALL_RULE"/>
 *     &lt;enumeration value="MODIFY_FIREWALL"/>
 *     &lt;enumeration value="DELETE_FIREWALL"/>
 *     &lt;enumeration value="MAKE_SERVER_VISIBLE"/>
 *     &lt;enumeration value="DISK_MIGRATE"/>
 *     &lt;enumeration value="CLONE_SERVER"/>
 *     &lt;enumeration value="FETCH_RESOURCE"/>
 *     &lt;enumeration value="CREATE_NETWORK"/>
 *     &lt;enumeration value="DELETE_NETWORK"/>
 *     &lt;enumeration value="MODIFY_RESOURCE"/>
 *     &lt;enumeration value="ATTACH_SSHKEY"/>
 *     &lt;enumeration value="DETACH_SSHKEY"/>
 *     &lt;enumeration value="ATTACH_SUBNET"/>
 *     &lt;enumeration value="DELETE_CUSTOMER"/>
 *     &lt;enumeration value="DELETE_USER"/>
 *     &lt;enumeration value="DELETE_JOB"/>
 *     &lt;enumeration value="DELETE_PRODUCT_OFFER"/>
 *     &lt;enumeration value="CREATE_SSHKEY"/>
 *     &lt;enumeration value="DELETE_SSHKEY"/>
 *     &lt;enumeration value="CREATE_FIREWALL_TEMPLATE"/>
 *     &lt;enumeration value="MODIFY_FIREWALL_TEMPLATE"/>
 *     &lt;enumeration value="APPLY_FIREWALL_TEMPLATE"/>
 *     &lt;enumeration value="DELETE_FIREWALL_TEMPLATE"/>
 *     &lt;enumeration value="PUBLISH_IMAGE"/>
 *     &lt;enumeration value="REVOKE_IMAGE"/>
 *     &lt;enumeration value="MODIFY_SSHKEY"/>
 *     &lt;enumeration value="MODIFY_SNAPSHOT"/>
 *     &lt;enumeration value="MODIFY_NETWORK"/>
 *     &lt;enumeration value="MODIFY_SUBNET"/>
 *     &lt;enumeration value="MODIFY_NIC"/>
 *     &lt;enumeration value="MODIFY_VDC"/>
 *     &lt;enumeration value="MODIFY_IMAGE"/>
 *     &lt;enumeration value="CREATE_GROUP"/>
 *     &lt;enumeration value="MODIFY_GROUP"/>
 *     &lt;enumeration value="DELETE_GROUP"/>
 *     &lt;enumeration value="DEPLOY_TEMPLATE"/>
 *     &lt;enumeration value="CREATE_TEMPLATE"/>
 *     &lt;enumeration value="UPDATE_INSTANCE_STATE"/>
 *     &lt;enumeration value="MODIFY_TEMPLATE"/>
 *     &lt;enumeration value="MODIFY_TEMPLATE_INSTANCE"/>
 *     &lt;enumeration value="DELETE_TEMPLATE"/>
 *     &lt;enumeration value="DELETE_TEMPLATE_INSTANCE"/>
 *     &lt;enumeration value="PUBLISH_TEMPLATE"/>
 *     &lt;enumeration value="REVOKE_TEMPLATE"/>
 *     &lt;enumeration value="CREATE_PAYMENT_METHOD_INSTANCE"/>
 *     &lt;enumeration value="CONFIGURE_PAYMENT_METHOD"/>
 *     &lt;enumeration value="MAKE_PAYMENT"/>
 *     &lt;enumeration value="RESUME_TRANSACTION"/>
 *     &lt;enumeration value="CANCEL_TRANSACTION"/>
 *     &lt;enumeration value="TEST_PAYMENT_METHOD"/>
 *     &lt;enumeration value="DELETE_PAYMENT_METHOD_INSTANCE"/>
 *     &lt;enumeration value="MODIFY_PAYMENT_METHOD_INSTANCE"/>
 *     &lt;enumeration value="PURCHASE_UNIT_PRODUCT"/>
 *     &lt;enumeration value="CREATE_BLOB"/>
 *     &lt;enumeration value="REFUND_TRANSACTION"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "jobType")
@XmlEnum
public enum JobType {


    /**
     * Create disk job
     * 
     */
    CREATE_DISK,

    /**
     * Delete disk job
     * 
     */
    DELETE_DISK,

    /**
     * Attach disk job
     * 
     */
    ATTACH_DISK,

    /**
     * Detach disk job
     * 
     */
    DETACH_DISK,

    /**
     * Resize disk job
     * 
     */
    RESIZE_DISK,

    /**
     * Clone disk job
     * 
     */
    CLONE_DISK,

    /**
     * Rename disk job
     * 
     */
    RENAME_DISK,

    /**
     * Create snapshot job
     * 
     */
    CREATE_SNAPSHOT,

    /**
     * Delete snapshot job
     * 
     */
    DELETE_SNAPSHOT,

    /**
     * Add IP address to NIC job
     * 
     */
    ADD_IP_TO_NIC,

    /**
     * Attach NIC job
     * 
     */
    ATTACH_NIC,

    /**
     * Create NIC job
     * 
     */
    CREATE_NIC,

    /**
     * Delete NIC job
     * 
     */
    DELETE_NIC,

    /**
     * Remove IP address from NIC job
     * 
     */
    REMOVE_IP_FROM_NIC,

    /**
     * Create server job
     * 
     */
    CREATE_SERVER,

    /**
     * Delete server job
     * 
     */
    DELETE_SERVER,

    /**
     * Modify server job
     * 
     */
    MODIFY_SERVER,

    /**
     * Revert server job
     * 
     */
    REVERT_SERVER,

    /**
     * Create subnet job
     * 
     */
    CREATE_SUBNET,

    /**
     * Move subnet job
     * 
     */
    MOVE_SUBNET,

    /**
     * Delete subnet job
     * 
     */
    DELETE_SUBNET,

    /**
     * Create VDC job
     * 
     */
    CREATE_VDC,

    /**
     * Delete VDC job
     * 
     */
    DELETE_VDC,

    /**
     * Create VLAN job
     * 
     */
    CREATE_VLAN,

    /**
     * Delete VLAN job
     * 
     */
    DELETE_VLAN,

    /**
     * Create image template job
     * 
     */
    CREATE_IMAGE_TEMPLATE,

    /**
     * Delete image template job
     * 
     */
    DELETE_IMAGE_TEMPLATE,

    /**
     * Start server job
     * 
     */
    START_SERVER,

    /**
     * Shutdown server job
     * 
     */
    SHUTDOWN_SERVER,

    /**
     * Kill server job
     * 
     */
    KILL_SERVER,

    /**
     * Reboot server job
     * 
     */
    REBOOT_SERVER,

    /**
     * Revert disk job
     * 
     */
    REVERT_DISK,

    /**
     * Cancel job job
     * 
     */
    CANCEL_JOB,

    /**
     * Schedule job job
     * 
     */
    SCHEDULED_JOB,

    /**
     * Modify disk job
     * 
     */
    MODIFY_DISK,

    /**
     * Detach NIC job
     * 
     */
    DETACH_NIC,

    /**
     * Fetch disk job
     * 
     */
    FETCH_DISK,

    /**
     * Fetch image job
     * 
     */
    FETCH_IMAGE,

    /**
     * Create firewall job
     * 
     */
    FIREWALL_CREATE,

    /**
     * Add firewall rule job
     * 
     */
    FIREWALL_RULE_ADD,

    /**
     * Modify firewall rule job
     * 
     */
    FIREWALL_RULE_MODIFY,

    /**
     * Delete firewall rule job
     * 
     */
    DELETE_FIREWALL_RULE,

    /**
     * Modify firewall job
     * 
     */
    MODIFY_FIREWALL,

    /**
     * Delete firewall job
     * 
     */
    DELETE_FIREWALL,

    /**
     * Make server visisble job
     * 
     */
    MAKE_SERVER_VISIBLE,

    /**
     * Migrate disk job
     * 
     */
    DISK_MIGRATE,

    /**
     * Clone server job
     * 
     */
    CLONE_SERVER,

    /**
     * Fetch resource job
     * 
     */
    FETCH_RESOURCE,

    /**
     * Create network job
     * 
     */
    CREATE_NETWORK,

    /**
     * Delete network job
     * 
     */
    DELETE_NETWORK,

    /**
     * Modify resource job
     * 
     */
    MODIFY_RESOURCE,

    /**
     * Attach SSH key job
     * 
     */
    ATTACH_SSHKEY,

    /**
     * Detach SSH key job
     * 
     */
    DETACH_SSHKEY,

    /**
     * Attach subnet job
     * 
     */
    ATTACH_SUBNET,

    /**
     * Delete Customer Job
     * 
     */
    DELETE_CUSTOMER,

    /**
     * Delete user job
     * 
     */
    DELETE_USER,

    /**
     * Delete job job
     * 
     */
    DELETE_JOB,

    /**
     * Delete product offer job
     * 
     */
    DELETE_PRODUCT_OFFER,

    /**
     * Create SSH key job
     * 
     */
    CREATE_SSHKEY,

    /**
     * Delete SSH key job
     * 
     */
    DELETE_SSHKEY,

    /**
     * Create firewall template job
     * 
     */
    CREATE_FIREWALL_TEMPLATE,

    /**
     * Modify firewall template job
     * 
     */
    MODIFY_FIREWALL_TEMPLATE,

    /**
     * Apply firewall template job
     * 
     */
    APPLY_FIREWALL_TEMPLATE,

    /**
     * Delete firewall template job
     * 
     */
    DELETE_FIREWALL_TEMPLATE,

    /**
     * Publish image job
     * 
     */
    PUBLISH_IMAGE,

    /**
     * Revoke image job
     * 
     */
    REVOKE_IMAGE,

    /**
     * Modify SSH key job
     * 
     */
    MODIFY_SSHKEY,

    /**
     * Modify snapshot job
     * 
     */
    MODIFY_SNAPSHOT,

    /**
     * Modify network job
     * 
     */
    MODIFY_NETWORK,

    /**
     * Modify subnet job
     * 
     */
    MODIFY_SUBNET,

    /**
     * Modify NIC job
     * 
     */
    MODIFY_NIC,

    /**
     * Modify VDC job
     * 
     */
    MODIFY_VDC,

    /**
     * Modify image job
     * 
     */
    MODIFY_IMAGE,

    /**
     * Create group job
     * 
     */
    CREATE_GROUP,

    /**
     * Modify group job
     * 
     */
    MODIFY_GROUP,

    /**
     * Delete group job
     * 
     */
    DELETE_GROUP,

    /**
     * Deploy deployment instance job
     * 
     */
    DEPLOY_TEMPLATE,

    /**
     * Create deployment instance job
     * 
     */
    CREATE_TEMPLATE,

    /**
     * Change deployment instance state
     * 
     */
    UPDATE_INSTANCE_STATE,

    /**
     * Modify template job
     * 
     */
    MODIFY_TEMPLATE,

    /**
     * Modify deployment instance job
     * 
     */
    MODIFY_TEMPLATE_INSTANCE,

    /**
     * Delete template job
     * 
     */
    DELETE_TEMPLATE,

    /**
     * Delete deployment instance job
     * 
     */
    DELETE_TEMPLATE_INSTANCE,

    /**
     * Publish template job
     * 
     */
    PUBLISH_TEMPLATE,

    /**
     * Revoke template job
     * 
     */
    REVOKE_TEMPLATE,

    /**
     * Create a payment method instance
     * 
     */
    CREATE_PAYMENT_METHOD_INSTANCE,

    /**
     * Configure a payment method
     * 
     */
    CONFIGURE_PAYMENT_METHOD,

    /**
     * Make a payment or refund
     * 
     */
    MAKE_PAYMENT,

    /**
     * Resume a transaction
     * 
     */
    RESUME_TRANSACTION,

    /**
     * Cancel a transaction
     * 
     */
    CANCEL_TRANSACTION,

    /**
     * Test a payment method
     * 
     */
    TEST_PAYMENT_METHOD,

    /**
     * Delete a payment method instance
     * 
     */
    DELETE_PAYMENT_METHOD_INSTANCE,

    /**
     * Modify a payment method instance
     * 
     */
    MODIFY_PAYMENT_METHOD_INSTANCE,

    /**
     * Purchase a unit product
     * 
     */
    PURCHASE_UNIT_PRODUCT,

    /**
     * CreateBlob
     * 
     */
    CREATE_BLOB,

    /**
     * Refund a transaction.
     * 
     */
    REFUND_TRANSACTION;

    public String value() {
        return name();
    }

    public static JobType fromValue(String v) {
        return valueOf(v);
    }

}
