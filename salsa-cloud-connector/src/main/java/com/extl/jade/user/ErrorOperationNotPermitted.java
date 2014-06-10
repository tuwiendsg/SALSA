
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for errorOperationNotPermitted.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="errorOperationNotPermitted">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MASTER_BILLING_ENTITY_ONLY"/>
 *     &lt;enumeration value="PRODUCT_OFFER_NOT_ALLOWED_FOR_THIS_CUSTOMER"/>
 *     &lt;enumeration value="PRODUCTS_OF_THIS_TYPE_CANNOT_BE_PURCHASED"/>
 *     &lt;enumeration value="UNAUTHORISED_ACCESS"/>
 *     &lt;enumeration value="PRODUCT_NOT_ACTIVE"/>
 *     &lt;enumeration value="PROMOTION_CANNOT_BE_USED"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_BILLING_ENTITIES"/>
 *     &lt;enumeration value="EMAIL_SERVICE_NOT_ALLOWED"/>
 *     &lt;enumeration value="INSUFFICIENT_PERMISSION_TO_START_SERVER"/>
 *     &lt;enumeration value="CANNOT_CHANGE_SYSTEM_OR_BILLING_KEYS"/>
 *     &lt;enumeration value="CANNOT_CHANGE_SYSTEM_KEYS"/>
 *     &lt;enumeration value="INVALID_SERVER_STATUS"/>
 *     &lt;enumeration value="NIC_ALREADY_ATTACHED_TO_SERVER"/>
 *     &lt;enumeration value="DISK_ALREADY_ATTACHED_TO_SERVER"/>
 *     &lt;enumeration value="DISK_NOT_ATTACHED_TO_SERVER"/>
 *     &lt;enumeration value="SERVER_NOT_STOPPED"/>
 *     &lt;enumeration value="DISK_CANNOT_BE_ATTACHED_TO_SERVER"/>
 *     &lt;enumeration value="DISK_AND_SERVER_NOT_IN_SAME_VDC"/>
 *     &lt;enumeration value="NO_ADDITIONAL_DISKS_CAN_BE_ADDED_TO_SERVER"/>
 *     &lt;enumeration value="NIC_NOT_ATTACHED_TO_SERVER"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_DISKS_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_STORAGE_SIZE"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_SERVER_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_IPV4_SUBNETS_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_NETWORKS_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_DISK_PRODUCT_OFFER_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_RAM_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_CPU_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_SUBNETS_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_IMAGES_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_SNAPSHOTS_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_VDC_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_CREDIT_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_USERS_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_LIMIT_ON_SERVER"/>
 *     &lt;enumeration value="INSUFFICIENT_PERMISSION_TO_CREATE_DISK"/>
 *     &lt;enumeration value="NOT_AUTHORISED_TO_USE_THIS_IMAGE"/>
 *     &lt;enumeration value="INVALID_SERVER_FOR_CUSTOMER"/>
 *     &lt;enumeration value="INSUFFICIENT_UNITS"/>
 *     &lt;enumeration value="JOB_CANNOT_BE_CANCELLED"/>
 *     &lt;enumeration value="SERVER_IS_IN_RUNNING_STATE"/>
 *     &lt;enumeration value="CANNOT_DELETE_IPV6_SUBNET"/>
 *     &lt;enumeration value="CANNOT_DELETE_SSHKEY"/>
 *     &lt;enumeration value="NETWORK_CANNOT_BE_DELETED"/>
 *     &lt;enumeration value="NETWORK_CANNOT_BE_ADDED"/>
 *     &lt;enumeration value="RESOURCE_BELONGS_TO_A_DIFFERENT_CUSTOMER"/>
 *     &lt;enumeration value="INVALID_CUSTOMER"/>
 *     &lt;enumeration value="REVERT_POSSIBLE_ONLY_FOR_SNAPSHOT"/>
 *     &lt;enumeration value="NEWER_SNAPSHOTS_EXISTS"/>
 *     &lt;enumeration value="INSUFFICIENT_PERMISSION_TO_CREATE_SERVER"/>
 *     &lt;enumeration value="INVALID_PRODUCT_OFFER_FOR_CUSTOMER"/>
 *     &lt;enumeration value="PRODUCT_COMPONENT_SET_UP_IS_INCORRECT"/>
 *     &lt;enumeration value="SERVER_VDC_DOES_NOT_MATCH_WITH_NIC"/>
 *     &lt;enumeration value="SSHKEY_IS_NOT_ATTACHED_TO_SERVER"/>
 *     &lt;enumeration value="SSHKEY_ALREADY_ATTACHED_TO_SERVER"/>
 *     &lt;enumeration value="SERVER_NOT_RUNNING"/>
 *     &lt;enumeration value="SNAPSHOTS_EXISTS_FOR_DISK"/>
 *     &lt;enumeration value="NO_ACCESS_TO_VDC"/>
 *     &lt;enumeration value="NO_ACCESS_TO_NETWORK"/>
 *     &lt;enumeration value="DO_NOT_SUPPORT_THIS_RESOURCE_TYPE"/>
 *     &lt;enumeration value="CANNOT_DELETE_CLUSTER"/>
 *     &lt;enumeration value="NO_ACCESS_TO_SERVER"/>
 *     &lt;enumeration value="NO_ACCESS_TO_RESOURCE"/>
 *     &lt;enumeration value="EXISTING_RESOURCES_FOR_FIREWALL_TEMPLATE"/>
 *     &lt;enumeration value="DISK_CANNOT_BE_DETACHED"/>
 *     &lt;enumeration value="INSUFFICIENT_PERMISSION_TO_CREATE_SNAPSHOT"/>
 *     &lt;enumeration value="CANNOT_ATTACH_SUBNET_TO_THIS_NETWORK"/>
 *     &lt;enumeration value="INSUFFICIENT_PERMISSION_TO_CLONE_DISK"/>
 *     &lt;enumeration value="INSUFFICIENT_PERMISSION_TO_DETACH"/>
 *     &lt;enumeration value="INSUFFICIENT_PERMISSION_TO_SNAPSHOT"/>
 *     &lt;enumeration value="INSUFFICIENT_PERMISSION_TO_CREATE_IMAGE"/>
 *     &lt;enumeration value="CANNOT_DELETE_RESOURCE"/>
 *     &lt;enumeration value="PROMOTION_CANNOT_BE_DELETED"/>
 *     &lt;enumeration value="ADMIN_GROUP_CANNOT_BE_EMPTY"/>
 *     &lt;enumeration value="CANNOT_DELETE_CUSTOMER"/>
 *     &lt;enumeration value="SUBNET_HAS_ACTIVE_IPS"/>
 *     &lt;enumeration value="CANNOT_DELETE_PRODUCTOFFER"/>
 *     &lt;enumeration value="VDC_NOT_EMPTY"/>
 *     &lt;enumeration value="CANNOT_MODIFY_OTHER_USER"/>
 *     &lt;enumeration value="INVALID_PRODUCT_TYPE"/>
 *     &lt;enumeration value="VDC_BELONGS_TO_A_DIFFERENT_CLUSTER"/>
 *     &lt;enumeration value="IP_ADDRESS_IN_USE"/>
 *     &lt;enumeration value="CANNOT_CREATE_IPV6_SUBNET"/>
 *     &lt;enumeration value="CANNOT_CREATE_NETWORK"/>
 *     &lt;enumeration value="CAPABILITY_NOT_PRESENT_FOR_RESOURCE"/>
 *     &lt;enumeration value="CANNOT_MOVE_RESOURCE_BETWEEN_CLUSTERS"/>
 *     &lt;enumeration value="CANNOT_MOVE_RESOURCE_BETWEEN_VDCS"/>
 *     &lt;enumeration value="IP_ADDRESS_NOT_IN_USE"/>
 *     &lt;enumeration value="INVALID_DISK_SIZE"/>
 *     &lt;enumeration value="CANNOT_CREATE_IMAGE"/>
 *     &lt;enumeration value="NO_ACCESS_TO_BILLING_ENTITY"/>
 *     &lt;enumeration value="CANNOT_ADD_USER_TO_GROUP"/>
 *     &lt;enumeration value="CANNOT_CREATE_SPECIAL_GROUP"/>
 *     &lt;enumeration value="CANNOT_CHANGE_GROUP_CUSTOMER"/>
 *     &lt;enumeration value="CANNOT_CHANGE_GROUP_TYPE"/>
 *     &lt;enumeration value="CANNOT_DELETE_SPECIAL_GROUP"/>
 *     &lt;enumeration value="SNAPSHOTS_EXIST_FOR_SERVER"/>
 *     &lt;enumeration value="DISKS_EXIST_FOR_SERVER"/>
 *     &lt;enumeration value="NICS_EXIST_FOR_SERVER"/>
 *     &lt;enumeration value="INVALID_GROUP_FOR_CUSTOMER"/>
 *     &lt;enumeration value="PRODUCT_PURCHASES_EXISTS"/>
 *     &lt;enumeration value="CANNOT_SET_PERMISSIONS_ON_USER"/>
 *     &lt;enumeration value="CANNOT_DELETE_USER"/>
 *     &lt;enumeration value="SERVER_CONTAINS_SNAPSHOTS"/>
 *     &lt;enumeration value="INVALID_USE_OF_AGGREGATION_FUNCTION"/>
 *     &lt;enumeration value="INVALID_PRODUCT_OFFER_FOR_IMAGE"/>
 *     &lt;enumeration value="CANNOT_LOCK_ONLY_ADMIN_USER"/>
 *     &lt;enumeration value="EXCEEDS_MAX_OUTSTANDING_INVITATIONS_LIMIT"/>
 *     &lt;enumeration value="NOT_ENOUGH_REFERRAL_UNITS_TO_SEND_INVITATIONS"/>
 *     &lt;enumeration value="REFERRAL_PROMOCODE_CANNOT_BE_DELETED"/>
 *     &lt;enumeration value="NO_NETWORK_DEFINED"/>
 *     &lt;enumeration value="PROMOCODE_CANNOT_BE_DELETED"/>
 *     &lt;enumeration value="PROMOCODE_CANNOT_BE_USED"/>
 *     &lt;enumeration value="NO_SERVERS_FOUND"/>
 *     &lt;enumeration value="FAILED_TO_MARSHAL"/>
 *     &lt;enumeration value="CANNOT_DELETE_DEPLOYMENT_TEMPLATE"/>
 *     &lt;enumeration value="EXISTING_RESOURCES_FOR_DEPLOYMENT_INSTANCE"/>
 *     &lt;enumeration value="CANNOT_DELETE_TEMPLATE_INSTANCE"/>
 *     &lt;enumeration value="INSTANCE_MISMATCH_WITH_TEMPLATE"/>
 *     &lt;enumeration value="VNC_HANDLER_NOT_SUPPORTED"/>
 *     &lt;enumeration value="CANNOT_CHANGE_DEPLOYMENT_INSTANCE_UUID"/>
 *     &lt;enumeration value="CANNOT_CREATE_IMAGE_FOR_ISO_DISK"/>
 *     &lt;enumeration value="INVALID_DEPLOYMENT_INSTANCE_STATUS"/>
 *     &lt;enumeration value="INVALID_VALUE_FOR_OUTER_TAG"/>
 *     &lt;enumeration value="EXISTING_BILLING_ENTITY_FOR_CURRENCY"/>
 *     &lt;enumeration value="CANNOT_CREATE_ISO_IMAGE"/>
 *     &lt;enumeration value="CANNOT_IMPORT_IP"/>
 *     &lt;enumeration value="CANNOT_IMPORT_SUBNET"/>
 *     &lt;enumeration value="CANNOT_DELETE_IMPORTED_IP"/>
 *     &lt;enumeration value="CANNOT_DELETE_IMPORTED_SUBNET"/>
 *     &lt;enumeration value="CANNOT_DELETE_PRODUCT"/>
 *     &lt;enumeration value="CANNOT_DELETE_PRODUCT_OFFER"/>
 *     &lt;enumeration value="PRODUCT_OFFER_IS_DELETED"/>
 *     &lt;enumeration value="NETWORK_IS_NOT_SERVICE_NETWORK"/>
 *     &lt;enumeration value="CANNOT_MODIFY_FDL"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_IPv4_ADDRESS_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_IPv6_SUBNET_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_PUBLIC_NETWORK_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_IP_NETWORK_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_PRIVATE_NETWORK_LIMIT"/>
 *     &lt;enumeration value="CANNOT_DELETE_PAYMENT_PROVIDER"/>
 *     &lt;enumeration value="CANNOT_MODIFY_PAYMENT_METHOD"/>
 *     &lt;enumeration value="NO_ACCESS_TO_PAYMENT_METHOD"/>
 *     &lt;enumeration value="INVOICE_ALREADY_PAID"/>
 *     &lt;enumeration value="INVALID_JADE_INSTANCE"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_BLOBS_LIMIT"/>
 *     &lt;enumeration value="CANNOT_EXCEED_MAX_BLOB_SIZE"/>
 *     &lt;enumeration value="UNPAID_INVOICES_EXIST"/>
 *     &lt;enumeration value="PAYMENT_PROVIDER_NOT_SUPPORTED"/>
 *     &lt;enumeration value="NON_INTERACTIVE_PAY_IS_FALSE"/>
 *     &lt;enumeration value="CANNOT_UPDATE_MBE_PARENT"/>
 *     &lt;enumeration value="NO_CAPABILITY_TO_DO_TRANSACTIONS"/>
 *     &lt;enumeration value="CUSTOMER_USER_NOT_IN_SAME_BE"/>
 *     &lt;enumeration value="NETWORK_AND_SUBNET_NOT_ON_SAME_CLUSTER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "errorOperationNotPermitted")
@XmlEnum
public enum ErrorOperationNotPermitted {


    /**
     * This operation can only be performed by the master billing entity
     * 
     */
    MASTER_BILLING_ENTITY_ONLY("MASTER_BILLING_ENTITY_ONLY"),

    /**
     * This customer is not permitted to use this product offer
     * 
     */
    PRODUCT_OFFER_NOT_ALLOWED_FOR_THIS_CUSTOMER("PRODUCT_OFFER_NOT_ALLOWED_FOR_THIS_CUSTOMER"),

    /**
     * Products of this type cannot be purchased using this call
     * 
     */
    PRODUCTS_OF_THIS_TYPE_CANNOT_BE_PURCHASED("PRODUCTS_OF_THIS_TYPE_CANNOT_BE_PURCHASED"),

    /**
     * Unauthorised access
     * 
     */
    UNAUTHORISED_ACCESS("UNAUTHORISED_ACCESS"),

    /**
     * This product is not active
     * 
     */
    PRODUCT_NOT_ACTIVE("PRODUCT_NOT_ACTIVE"),

    /**
     * This promotion cannot be used in this context
     * 
     */
    PROMOTION_CANNOT_BE_USED("PROMOTION_CANNOT_BE_USED"),

    /**
     * Cannot create a billing entity as to do so would exceed the maximum number permitted
     * 
     */
    CANNOT_EXCEED_MAX_BILLING_ENTITIES("CANNOT_EXCEED_MAX_BILLING_ENTITIES"),

    /**
     * Cannot use sendEmail in this context
     * 
     */
    EMAIL_SERVICE_NOT_ALLOWED("EMAIL_SERVICE_NOT_ALLOWED"),

    /**
     * Insufficient permission to start the server
     * 
     */
    INSUFFICIENT_PERMISSION_TO_START_SERVER("INSUFFICIENT_PERMISSION_TO_START_SERVER"),

    /**
     * Cannot change the system or billing keys
     * 
     */
    CANNOT_CHANGE_SYSTEM_OR_BILLING_KEYS("CANNOT_CHANGE_SYSTEM_OR_BILLING_KEYS"),

    /**
     * Cannot change the system keys
     * 
     */
    CANNOT_CHANGE_SYSTEM_KEYS("CANNOT_CHANGE_SYSTEM_KEYS"),

    /**
     * Invalid server status
     * 
     */
    INVALID_SERVER_STATUS("INVALID_SERVER_STATUS"),

    /**
     * The NIC concerned is already attached to a server
     * 
     */
    NIC_ALREADY_ATTACHED_TO_SERVER("NIC_ALREADY_ATTACHED_TO_SERVER"),

    /**
     * The disk concerned is already attached to a server
     * 
     */
    DISK_ALREADY_ATTACHED_TO_SERVER("DISK_ALREADY_ATTACHED_TO_SERVER"),

    /**
     * The disk concerned is not attached to the server
     * 
     */
    DISK_NOT_ATTACHED_TO_SERVER("DISK_NOT_ATTACHED_TO_SERVER"),

    /**
     * The server is not in a stopped state, which is necessary to perform the action concerned
     * 
     */
    SERVER_NOT_STOPPED("SERVER_NOT_STOPPED"),

    /**
     * This disk cannot be attached to a server
     * 
     */
    DISK_CANNOT_BE_ATTACHED_TO_SERVER("DISK_CANNOT_BE_ATTACHED_TO_SERVER"),

    /**
     * The disk and server are not in the same VDC
     * 
     */
    DISK_AND_SERVER_NOT_IN_SAME_VDC("DISK_AND_SERVER_NOT_IN_SAME_VDC"),

    /**
     * No additional disks can be attached to the server
     * 
     */
    NO_ADDITIONAL_DISKS_CAN_BE_ADDED_TO_SERVER("NO_ADDITIONAL_DISKS_CAN_BE_ADDED_TO_SERVER"),

    /**
     * The NIC concerned is not attached to the server
     * 
     */
    NIC_NOT_ATTACHED_TO_SERVER("NIC_NOT_ATTACHED_TO_SERVER"),

    /**
     * Cannot exceed the maximum disk limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_DISKS_LIMIT("CANNOT_EXCEED_MAX_DISKS_LIMIT"),

    /**
     * Cannot exceed the maximum storage size limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_STORAGE_SIZE("CANNOT_EXCEED_MAX_STORAGE_SIZE"),

    /**
     * Cannot exceed the maximum server limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_SERVER_LIMIT("CANNOT_EXCEED_MAX_SERVER_LIMIT"),

    /**
     * Cannot exceed the maximum IPv4 subnet limit, and to perform this operation would do so
     * 
     */
    @XmlEnumValue("CANNOT_EXCEED_MAX_IPV4_SUBNETS_LIMIT")
    CANNOT_EXCEED_MAX_IPV_4_SUBNETS_LIMIT("CANNOT_EXCEED_MAX_IPV4_SUBNETS_LIMIT"),

    /**
     * Cannot exceed the maximum networks limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_NETWORKS_LIMIT("CANNOT_EXCEED_MAX_NETWORKS_LIMIT"),

    /**
     * Cannot exceed the maximum number of disk product offfers, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_DISK_PRODUCT_OFFER_LIMIT("CANNOT_EXCEED_MAX_DISK_PRODUCT_OFFER_LIMIT"),

    /**
     * Cannot exceed the maximum RAM limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_RAM_LIMIT("CANNOT_EXCEED_MAX_RAM_LIMIT"),

    /**
     * Cannot exceed the maximum CPU limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_CPU_LIMIT("CANNOT_EXCEED_MAX_CPU_LIMIT"),

    /**
     * Cannot exceed the maximum subnets limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_SUBNETS_LIMIT("CANNOT_EXCEED_MAX_SUBNETS_LIMIT"),

    /**
     * Cannot exceed the maximum images limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_IMAGES_LIMIT("CANNOT_EXCEED_MAX_IMAGES_LIMIT"),

    /**
     * Cannot exceed the maximum snapshots limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_SNAPSHOTS_LIMIT("CANNOT_EXCEED_MAX_SNAPSHOTS_LIMIT"),

    /**
     * Cannot exceed the maximum VDC limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_VDC_LIMIT("CANNOT_EXCEED_MAX_VDC_LIMIT"),

    /**
     * Cannot exceed the maximum credit limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_CREDIT_LIMIT("CANNOT_EXCEED_MAX_CREDIT_LIMIT"),

    /**
     * Cannot exceed the maximum users per customer limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_USERS_LIMIT("CANNOT_EXCEED_MAX_USERS_LIMIT"),

    /**
     * Cannot exceed the maximum number of this resource that can be attached to the server, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_LIMIT_ON_SERVER("CANNOT_EXCEED_MAX_LIMIT_ON_SERVER"),

    /**
     * Insufficient permissions to create the disk
     * 
     */
    INSUFFICIENT_PERMISSION_TO_CREATE_DISK("INSUFFICIENT_PERMISSION_TO_CREATE_DISK"),

    /**
     * This customer is not authorised to use this image
     * 
     */
    NOT_AUTHORISED_TO_USE_THIS_IMAGE("NOT_AUTHORISED_TO_USE_THIS_IMAGE"),

    /**
     * This server is not valid for this customer
     * 
     */
    INVALID_SERVER_FOR_CUSTOMER("INVALID_SERVER_FOR_CUSTOMER"),

    /**
     * Insufficient units to perform this action
     * 
     */
    INSUFFICIENT_UNITS("INSUFFICIENT_UNITS"),

    /**
     * This job cannot be cancelled
     * 
     */
    JOB_CANNOT_BE_CANCELLED("JOB_CANNOT_BE_CANCELLED"),

    /**
     * Cannot perform this operation when the server is in a running state
     * 
     */
    SERVER_IS_IN_RUNNING_STATE("SERVER_IS_IN_RUNNING_STATE"),

    /**
     * Cannot delete an IPv6 subnet as the IPv6 subnet is a fixed entity associated with the MAC address of the NIC
     * 
     */
    @XmlEnumValue("CANNOT_DELETE_IPV6_SUBNET")
    CANNOT_DELETE_IPV_6_SUBNET("CANNOT_DELETE_IPV6_SUBNET"),

    /**
     * Cannot delete the SSHKey concerned
     * 
     */
    CANNOT_DELETE_SSHKEY("CANNOT_DELETE_SSHKEY"),

    /**
     * Cannot delete the network concerned
     * 
     */
    NETWORK_CANNOT_BE_DELETED("NETWORK_CANNOT_BE_DELETED"),

    /**
     * Cannot add the network concerned
     * 
     */
    NETWORK_CANNOT_BE_ADDED("NETWORK_CANNOT_BE_ADDED"),

    /**
     * This resource belongs to a different customer
     * 
     */
    RESOURCE_BELONGS_TO_A_DIFFERENT_CUSTOMER("RESOURCE_BELONGS_TO_A_DIFFERENT_CUSTOMER"),

    /**
     * Invalid customer
     * 
     */
    INVALID_CUSTOMER("INVALID_CUSTOMER"),

    /**
     * Only snapshots can be reverted and this resource is not a snapshot
     * 
     */
    REVERT_POSSIBLE_ONLY_FOR_SNAPSHOT("REVERT_POSSIBLE_ONLY_FOR_SNAPSHOT"),

    /**
     * This cluster cannot revert to a snapshot where a newer snapshot exists
     * 
     */
    NEWER_SNAPSHOTS_EXISTS("NEWER_SNAPSHOTS_EXISTS"),

    /**
     * Insufficient permission to create the cluster
     * 
     */
    INSUFFICIENT_PERMISSION_TO_CREATE_SERVER("INSUFFICIENT_PERMISSION_TO_CREATE_SERVER"),

    /**
     * This product offer is not valid for the customer specified
     * 
     */
    INVALID_PRODUCT_OFFER_FOR_CUSTOMER("INVALID_PRODUCT_OFFER_FOR_CUSTOMER"),

    /**
     * Product component combinations are invalid
     * 
     */
    PRODUCT_COMPONENT_SET_UP_IS_INCORRECT("PRODUCT_COMPONENT_SET_UP_IS_INCORRECT"),

    /**
     * The NIC and server must be in the same VDC
     * 
     */
    SERVER_VDC_DOES_NOT_MATCH_WITH_NIC("SERVER_VDC_DOES_NOT_MATCH_WITH_NIC"),

    /**
     * The SSHKey concerned is not attached to the server
     * 
     */
    SSHKEY_IS_NOT_ATTACHED_TO_SERVER("SSHKEY_IS_NOT_ATTACHED_TO_SERVER"),

    /**
     * The SSHKey concerned is already attached to the server
     * 
     */
    SSHKEY_ALREADY_ATTACHED_TO_SERVER("SSHKEY_ALREADY_ATTACHED_TO_SERVER"),

    /**
     * The server is not running
     * 
     */
    SERVER_NOT_RUNNING("SERVER_NOT_RUNNING"),

    /**
     * This disk has existing snapshots
     * 
     */
    SNAPSHOTS_EXISTS_FOR_DISK("SNAPSHOTS_EXISTS_FOR_DISK"),

    /**
     * The caller has no access to this VDC
     * 
     */
    NO_ACCESS_TO_VDC("NO_ACCESS_TO_VDC"),

    /**
     * The caller has no access to this network
     * 
     */
    NO_ACCESS_TO_NETWORK("NO_ACCESS_TO_NETWORK"),

    /**
     * This call does not support this type of resource
     * 
     */
    DO_NOT_SUPPORT_THIS_RESOURCE_TYPE("DO_NOT_SUPPORT_THIS_RESOURCE_TYPE"),

    /**
     * Cannot delete the cluster
     * 
     */
    CANNOT_DELETE_CLUSTER("CANNOT_DELETE_CLUSTER"),

    /**
     * No access to this server
     * 
     */
    NO_ACCESS_TO_SERVER("NO_ACCESS_TO_SERVER"),

    /**
     * No access to this resource
     * 
     */
    NO_ACCESS_TO_RESOURCE("NO_ACCESS_TO_RESOURCE"),

    /**
     * Firewalls based upon this firewall template still exist
     * 
     */
    EXISTING_RESOURCES_FOR_FIREWALL_TEMPLATE("EXISTING_RESOURCES_FOR_FIREWALL_TEMPLATE"),

    /**
     * This disk cannot be detached
     * 
     */
    DISK_CANNOT_BE_DETACHED("DISK_CANNOT_BE_DETACHED"),

    /**
     * Insufficient permission to create a snapshot
     * 
     */
    INSUFFICIENT_PERMISSION_TO_CREATE_SNAPSHOT("INSUFFICIENT_PERMISSION_TO_CREATE_SNAPSHOT"),

    /**
     * Cannot attach the subnet to this network
     * 
     */
    CANNOT_ATTACH_SUBNET_TO_THIS_NETWORK("CANNOT_ATTACH_SUBNET_TO_THIS_NETWORK"),

    /**
     * Insufficient permission to clone the disk
     * 
     */
    INSUFFICIENT_PERMISSION_TO_CLONE_DISK("INSUFFICIENT_PERMISSION_TO_CLONE_DISK"),

    /**
     * Insufficient permission to detach the disk
     * 
     */
    INSUFFICIENT_PERMISSION_TO_DETACH("INSUFFICIENT_PERMISSION_TO_DETACH"),

    /**
     * Insufficient permission to snapshot the disk
     * 
     */
    INSUFFICIENT_PERMISSION_TO_SNAPSHOT("INSUFFICIENT_PERMISSION_TO_SNAPSHOT"),

    /**
     * Insufficient permission to create an image
     * 
     */
    INSUFFICIENT_PERMISSION_TO_CREATE_IMAGE("INSUFFICIENT_PERMISSION_TO_CREATE_IMAGE"),

    /**
     * Cannot delete the resource
     * 
     */
    CANNOT_DELETE_RESOURCE("CANNOT_DELETE_RESOURCE"),

    /**
     * Cannot delete the promotion
     * 
     */
    PROMOTION_CANNOT_BE_DELETED("PROMOTION_CANNOT_BE_DELETED"),

    /**
     * The admin group must consist of at least one non-locked user, and the action specified would break this constraint
     * 
     */
    ADMIN_GROUP_CANNOT_BE_EMPTY("ADMIN_GROUP_CANNOT_BE_EMPTY"),

    /**
     * Cannot delete this customer
     * 
     */
    CANNOT_DELETE_CUSTOMER("CANNOT_DELETE_CUSTOMER"),

    /**
     * This subnet has active IP addresses
     * 
     */
    SUBNET_HAS_ACTIVE_IPS("SUBNET_HAS_ACTIVE_IPS"),

    /**
     * Cannot delete the product offer
     * 
     */
    CANNOT_DELETE_PRODUCTOFFER("CANNOT_DELETE_PRODUCTOFFER"),

    /**
     * The operation cannot be performed as the VDC is not empty
     * 
     */
    VDC_NOT_EMPTY("VDC_NOT_EMPTY"),

    /**
     * A user cannot modify another user's settings
     * 
     */
    CANNOT_MODIFY_OTHER_USER("CANNOT_MODIFY_OTHER_USER"),

    /**
     * The product type specified is invalid
     * 
     */
    INVALID_PRODUCT_TYPE("INVALID_PRODUCT_TYPE"),

    /**
     * The VDC concerned belongs to a different cluster
     * 
     */
    VDC_BELONGS_TO_A_DIFFERENT_CLUSTER("VDC_BELONGS_TO_A_DIFFERENT_CLUSTER"),

    /**
     * The IP address specified is already in use
     * 
     */
    IP_ADDRESS_IN_USE("IP_ADDRESS_IN_USE"),

    /**
     * Cannot create an IPv6 subnet as IPv6 subnets are derived from the MAC address of the NIC
     * 
     */
    @XmlEnumValue("CANNOT_CREATE_IPV6_SUBNET")
    CANNOT_CREATE_IPV_6_SUBNET("CANNOT_CREATE_IPV6_SUBNET"),

    /**
     * Cannot create a network
     * 
     */
    CANNOT_CREATE_NETWORK("CANNOT_CREATE_NETWORK"),

    /**
     * That capability is not present in respect of the resource specified
     * 
     */
    CAPABILITY_NOT_PRESENT_FOR_RESOURCE("CAPABILITY_NOT_PRESENT_FOR_RESOURCE"),

    /**
     * Cannot move a resource between clusters
     * 
     */
    CANNOT_MOVE_RESOURCE_BETWEEN_CLUSTERS("CANNOT_MOVE_RESOURCE_BETWEEN_CLUSTERS"),

    /**
     * Cannot move a resource between VDCs
     * 
     */
    CANNOT_MOVE_RESOURCE_BETWEEN_VDCS("CANNOT_MOVE_RESOURCE_BETWEEN_VDCS"),

    /**
     * That IP address is not in use
     * 
     */
    IP_ADDRESS_NOT_IN_USE("IP_ADDRESS_NOT_IN_USE"),

    /**
     * Invalid disk size
     * 
     */
    INVALID_DISK_SIZE("INVALID_DISK_SIZE"),

    /**
     * Cannot create image
     * 
     */
    CANNOT_CREATE_IMAGE("CANNOT_CREATE_IMAGE"),

    /**
     * Caller has no access to that billing entity
     * 
     */
    NO_ACCESS_TO_BILLING_ENTITY("NO_ACCESS_TO_BILLING_ENTITY"),

    /**
     * Cannot add the user to the group
     * 
     */
    CANNOT_ADD_USER_TO_GROUP("CANNOT_ADD_USER_TO_GROUP"),

    /**
     * Cannot create a special group
     * 
     */
    CANNOT_CREATE_SPECIAL_GROUP("CANNOT_CREATE_SPECIAL_GROUP"),

    /**
     * Cannot change the customer to which a group belongs
     * 
     */
    CANNOT_CHANGE_GROUP_CUSTOMER("CANNOT_CHANGE_GROUP_CUSTOMER"),

    /**
     * Cannot change the type of a group
     * 
     */
    CANNOT_CHANGE_GROUP_TYPE("CANNOT_CHANGE_GROUP_TYPE"),

    /**
     * Cannot delete special groups
     * 
     */
    CANNOT_DELETE_SPECIAL_GROUP("CANNOT_DELETE_SPECIAL_GROUP"),

    /**
     * Snapshots of this server exist
     * 
     */
    SNAPSHOTS_EXIST_FOR_SERVER("SNAPSHOTS_EXIST_FOR_SERVER"),

    /**
     * Disks exist for this server
     * 
     */
    DISKS_EXIST_FOR_SERVER("DISKS_EXIST_FOR_SERVER"),

    /**
     * NICs exist for this server
     * 
     */
    NICS_EXIST_FOR_SERVER("NICS_EXIST_FOR_SERVER"),

    /**
     * Invalid group for the specified customer
     * 
     */
    INVALID_GROUP_FOR_CUSTOMER("INVALID_GROUP_FOR_CUSTOMER"),

    /**
     * Product purchases exist
     * 
     */
    PRODUCT_PURCHASES_EXISTS("PRODUCT_PURCHASES_EXISTS"),

    /**
     * Cannot set permissions on a user
     * 
     */
    CANNOT_SET_PERMISSIONS_ON_USER("CANNOT_SET_PERMISSIONS_ON_USER"),

    /**
     * Cannot delete that user
     * 
     */
    CANNOT_DELETE_USER("CANNOT_DELETE_USER"),

    /**
     * The server contains snapshots
     * 
     */
    SERVER_CONTAINS_SNAPSHOTS("SERVER_CONTAINS_SNAPSHOTS"),

    /**
     * The use of aggregation function is invalid
     * 
     */
    INVALID_USE_OF_AGGREGATION_FUNCTION("INVALID_USE_OF_AGGREGATION_FUNCTION"),

    /**
     * Product offer and image belongs to different billing entities
     * 
     */
    INVALID_PRODUCT_OFFER_FOR_IMAGE("INVALID_PRODUCT_OFFER_FOR_IMAGE"),

    /**
     * The only admin user for the customer cannot be locked
     * 
     */
    CANNOT_LOCK_ONLY_ADMIN_USER("CANNOT_LOCK_ONLY_ADMIN_USER"),

    /**
     * Cannot exceed the maximum outstanding invitations per referral promotion
     * 
     */
    EXCEEDS_MAX_OUTSTANDING_INVITATIONS_LIMIT("EXCEEDS_MAX_OUTSTANDING_INVITATIONS_LIMIT"),

    /**
     * No enough referral units to send invitations.
     * 
     */
    NOT_ENOUGH_REFERRAL_UNITS_TO_SEND_INVITATIONS("NOT_ENOUGH_REFERRAL_UNITS_TO_SEND_INVITATIONS"),

    /**
     * Referral Promocode cannot be deleted as it is either used or expired
     * 
     */
    REFERRAL_PROMOCODE_CANNOT_BE_DELETED("REFERRAL_PROMOCODE_CANNOT_BE_DELETED"),

    /**
     * No Network has been defined
     * 
     */
    NO_NETWORK_DEFINED("NO_NETWORK_DEFINED"),

    /**
     * Promocode cannot be deleted
     * 
     */
    PROMOCODE_CANNOT_BE_DELETED("PROMOCODE_CANNOT_BE_DELETED"),

    /**
     * Promocode cannot be used
     * 
     */
    PROMOCODE_CANNOT_BE_USED("PROMOCODE_CANNOT_BE_USED"),

    /**
     * No servers found
     * 
     */
    NO_SERVERS_FOUND("NO_SERVERS_FOUND"),

    /**
     * Failed to marshal
     * 
     */
    FAILED_TO_MARSHAL("FAILED_TO_MARSHAL"),

    /**
     * Cannot delete deployment template
     * 
     */
    CANNOT_DELETE_DEPLOYMENT_TEMPLATE("CANNOT_DELETE_DEPLOYMENT_TEMPLATE"),

    /**
     * Existing resources for deployment instance
     * 
     */
    EXISTING_RESOURCES_FOR_DEPLOYMENT_INSTANCE("EXISTING_RESOURCES_FOR_DEPLOYMENT_INSTANCE"),

    /**
     * Cannot delete deployment instance
     * 
     */
    CANNOT_DELETE_TEMPLATE_INSTANCE("CANNOT_DELETE_TEMPLATE_INSTANCE"),

    /**
     * resources in deployment instance to not match that of the template
     * 
     */
    INSTANCE_MISMATCH_WITH_TEMPLATE("INSTANCE_MISMATCH_WITH_TEMPLATE"),

    /**
     * The requested VNC handler is not supported in the cluster
     * 
     */
    VNC_HANDLER_NOT_SUPPORTED("VNC_HANDLER_NOT_SUPPORTED"),

    /**
     * Cannot change the deployment instance uuid
     * 
     */
    CANNOT_CHANGE_DEPLOYMENT_INSTANCE_UUID("CANNOT_CHANGE_DEPLOYMENT_INSTANCE_UUID"),

    /**
     * Cannot create image for iso disk
     * 
     */
    CANNOT_CREATE_IMAGE_FOR_ISO_DISK("CANNOT_CREATE_IMAGE_FOR_ISO_DISK"),

    /**
     * Invalid deployment instance status
     * 
     */
    INVALID_DEPLOYMENT_INSTANCE_STATUS("INVALID_DEPLOYMENT_INSTANCE_STATUS"),

    /**
     * Invalid outer tag value
     * 
     */
    INVALID_VALUE_FOR_OUTER_TAG("INVALID_VALUE_FOR_OUTER_TAG"),

    /**
     * Billing entities present for a given currency
     * 
     */
    EXISTING_BILLING_ENTITY_FOR_CURRENCY("EXISTING_BILLING_ENTITY_FOR_CURRENCY"),

    /**
     * Cannot create images from iso disks
     * 
     */
    CANNOT_CREATE_ISO_IMAGE("CANNOT_CREATE_ISO_IMAGE"),

    /**
     * Cannot import IP address
     * 
     */
    CANNOT_IMPORT_IP("CANNOT_IMPORT_IP"),

    /**
     * Cannot import subnet
     * 
     */
    CANNOT_IMPORT_SUBNET("CANNOT_IMPORT_SUBNET"),

    /**
     * Cannot remove imported IP address
     * 
     */
    CANNOT_DELETE_IMPORTED_IP("CANNOT_DELETE_IMPORTED_IP"),

    /**
     * Cannot remove imported subnet
     * 
     */
    CANNOT_DELETE_IMPORTED_SUBNET("CANNOT_DELETE_IMPORTED_SUBNET"),

    /**
     * Cannot delete product
     * 
     */
    CANNOT_DELETE_PRODUCT("CANNOT_DELETE_PRODUCT"),

    /**
     * Cannot delete product offer
     * 
     */
    CANNOT_DELETE_PRODUCT_OFFER("CANNOT_DELETE_PRODUCT_OFFER"),

    /**
     * The product offer is deleted and cannot be used
     * 
     */
    PRODUCT_OFFER_IS_DELETED("PRODUCT_OFFER_IS_DELETED"),

    /**
     * The network must be a service network
     * 
     */
    NETWORK_IS_NOT_SERVICE_NETWORK("NETWORK_IS_NOT_SERVICE_NETWORK"),

    /**
     * Cannot modify an FDL code block
     * 
     */
    CANNOT_MODIFY_FDL("CANNOT_MODIFY_FDL"),

    /**
     * Cannot exceed the maximum IPv4 address limit, and to perform this operation would do so
     * 
     */
    @XmlEnumValue("CANNOT_EXCEED_MAX_IPv4_ADDRESS_LIMIT")
    CANNOT_EXCEED_MAX_I_PV_4_ADDRESS_LIMIT("CANNOT_EXCEED_MAX_IPv4_ADDRESS_LIMIT"),

    /**
     * Cannot exceed the maximum IPv6 subnet limit, and to perform this operation would do so
     * 
     */
    @XmlEnumValue("CANNOT_EXCEED_MAX_IPv6_SUBNET_LIMIT")
    CANNOT_EXCEED_MAX_I_PV_6_SUBNET_LIMIT("CANNOT_EXCEED_MAX_IPv6_SUBNET_LIMIT"),

    /**
     * Cannot exceed the maximum public network limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_PUBLIC_NETWORK_LIMIT("CANNOT_EXCEED_MAX_PUBLIC_NETWORK_LIMIT"),

    /**
     * Cannot exceed the maximum ip network limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_IP_NETWORK_LIMIT("CANNOT_EXCEED_MAX_IP_NETWORK_LIMIT"),

    /**
     * Cannot exceed the maximum private network limit, and to perform this operation would do so
     * 
     */
    CANNOT_EXCEED_MAX_PRIVATE_NETWORK_LIMIT("CANNOT_EXCEED_MAX_PRIVATE_NETWORK_LIMIT"),

    /**
     * Cannot delete the payment provider
     * 
     */
    CANNOT_DELETE_PAYMENT_PROVIDER("CANNOT_DELETE_PAYMENT_PROVIDER"),

    /**
     * Cannot modify the payment provider
     * 
     */
    CANNOT_MODIFY_PAYMENT_METHOD("CANNOT_MODIFY_PAYMENT_METHOD"),

    /**
     * No access to the payment method
     * 
     */
    NO_ACCESS_TO_PAYMENT_METHOD("NO_ACCESS_TO_PAYMENT_METHOD"),

    /**
     * The invoice has already been paid.
     * 
     */
    INVOICE_ALREADY_PAID("INVOICE_ALREADY_PAID"),

    /**
     * Invalid JADE instance.
     * 
     */
    INVALID_JADE_INSTANCE("INVALID_JADE_INSTANCE"),

    /**
     * Cannot exceed the maximum number of blobs limit
     * 
     */
    CANNOT_EXCEED_MAX_BLOBS_LIMIT("CANNOT_EXCEED_MAX_BLOBS_LIMIT"),

    /**
     * Cannot exceed the maximum blobs size limit
     * 
     */
    CANNOT_EXCEED_MAX_BLOB_SIZE("CANNOT_EXCEED_MAX_BLOB_SIZE"),

    /**
     * Unpaid invoices exists
     * 
     */
    UNPAID_INVOICES_EXIST("UNPAID_INVOICES_EXIST"),

    /**
     * Payment provider does not support
     * 
     */
    PAYMENT_PROVIDER_NOT_SUPPORTED("PAYMENT_PROVIDER_NOT_SUPPORTED"),

    /**
     * Non interactive pay is set to false
     * 
     */
    NON_INTERACTIVE_PAY_IS_FALSE("NON_INTERACTIVE_PAY_IS_FALSE"),

    /**
     * The master billing entities parent cannot be changed
     * 
     */
    CANNOT_UPDATE_MBE_PARENT("CANNOT_UPDATE_MBE_PARENT"),

    /**
     * No capability to do payment transactions
     * 
     */
    NO_CAPABILITY_TO_DO_TRANSACTIONS("NO_CAPABILITY_TO_DO_TRANSACTIONS"),

    /**
     * Customer and BE are in diferent BE's
     * 
     */
    CUSTOMER_USER_NOT_IN_SAME_BE("CUSTOMER_USER_NOT_IN_SAME_BE"),

    /**
     * Network and subnet are not on same cluster
     * 
     */
    NETWORK_AND_SUBNET_NOT_ON_SAME_CLUSTER("NETWORK_AND_SUBNET_NOT_ON_SAME_CLUSTER");
    private final String value;

    ErrorOperationNotPermitted(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ErrorOperationNotPermitted fromValue(String v) {
        for (ErrorOperationNotPermitted c: ErrorOperationNotPermitted.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
