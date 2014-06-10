
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for errorResourceNotFound.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="errorResourceNotFound">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NO_CUSTOMER_FOUND"/>
 *     &lt;enumeration value="NO_USER_FOUND"/>
 *     &lt;enumeration value="NO_BILLING_ENTITY_FOUND"/>
 *     &lt;enumeration value="NO_PRODUCT_OFFER_FOUND"/>
 *     &lt;enumeration value="NO_PRODUCT_PURCHASE_FOUND"/>
 *     &lt;enumeration value="NO_UNIT_TRANSACTION_FOUND"/>
 *     &lt;enumeration value="NO_INVOICE_FOUND"/>
 *     &lt;enumeration value="NO_PRODUCT_FOUND"/>
 *     &lt;enumeration value="NO_PERIOD_FOUND"/>
 *     &lt;enumeration value="NO_PROMOTION_FOUND"/>
 *     &lt;enumeration value="NO_PROMOTION_CODE_FOUND"/>
 *     &lt;enumeration value="NO_CURRENCY_FOUND"/>
 *     &lt;enumeration value="NO_PROMOCODE_FOUND"/>
 *     &lt;enumeration value="NO_REFERRAL_SCHEME_FOUND"/>
 *     &lt;enumeration value="NO_REFERRAL_PROMO_CODE_FOUND"/>
 *     &lt;enumeration value="NO_UNIT_BALANCE_FOUND"/>
 *     &lt;enumeration value="NO_VDC_FOUND"/>
 *     &lt;enumeration value="NO_IMAGE_FOUND"/>
 *     &lt;enumeration value="NO_IMAGE_PERMISSION_FOUND"/>
 *     &lt;enumeration value="NO_JOB_FOUND"/>
 *     &lt;enumeration value="NO_SERVER_FOUND"/>
 *     &lt;enumeration value="NO_RESOURCE_FOUND"/>
 *     &lt;enumeration value="NO_DISK_FOUND"/>
 *     &lt;enumeration value="NO_SERVER_DETAILS_FOUND"/>
 *     &lt;enumeration value="FIREWALL_NOT_FOUND"/>
 *     &lt;enumeration value="BOOT_DISK_NOT_FOUND"/>
 *     &lt;enumeration value="NO_NIC_FOUND"/>
 *     &lt;enumeration value="NO_SNAPSHOT_FOUND"/>
 *     &lt;enumeration value="NO_NETWORK_FOUND"/>
 *     &lt;enumeration value="NO_SUBNET_FOUND"/>
 *     &lt;enumeration value="NO_FIREWALL_FOUND"/>
 *     &lt;enumeration value="NO_PRODUCT_COMPONENT_FOUND"/>
 *     &lt;enumeration value="NO_SSH_KEY_FOUND"/>
 *     &lt;enumeration value="NO_RESOURCE_KEY_FOUND"/>
 *     &lt;enumeration value="NO_RESOURCE_UUID_FOUND"/>
 *     &lt;enumeration value="NO_FETCH_PARAMS_FOUND"/>
 *     &lt;enumeration value="NO_CLUSTER_FOUND"/>
 *     &lt;enumeration value="NO_FIREWALL_TEMPLATE_FOUND"/>
 *     &lt;enumeration value="NO_PRODUCT_OFFER_UUID_FOUND"/>
 *     &lt;enumeration value="NO_SNAPSHOT_UUID_FOUND"/>
 *     &lt;enumeration value="NO_FIREWALL_UUID_FOUND"/>
 *     &lt;enumeration value="NO_IMAGE_UUID_FOUND"/>
 *     &lt;enumeration value="NO_NIC_UUID_FOUND"/>
 *     &lt;enumeration value="NO_SERVER_UUID_FOUND"/>
 *     &lt;enumeration value="NO_SUBNET_UUID_FOUND"/>
 *     &lt;enumeration value="NO_VDC_UUID_FOUND"/>
 *     &lt;enumeration value="NO_NETWORK_UUID_FOUND"/>
 *     &lt;enumeration value="NO_CLUSTER_UUID_FOUND"/>
 *     &lt;enumeration value="NO_JOB_UUID_FOUND"/>
 *     &lt;enumeration value="NO_FIREWALL_TEMPLATE_UUID_FOUND"/>
 *     &lt;enumeration value="NO_GROUP_FOUND"/>
 *     &lt;enumeration value="NO_IP_FOUND"/>
 *     &lt;enumeration value="NO_STORAGE_UNIT_FOUND"/>
 *     &lt;enumeration value="NO_PAYMENT_METHOD_FOUND"/>
 *     &lt;enumeration value="NO_UNLOCKED_USER_FOUND"/>
 *     &lt;enumeration value="NO_DEPLOYMENT_TEMPLATE_FOUND"/>
 *     &lt;enumeration value="NO_DEPLOYMENT_INSTANCE_FOUND"/>
 *     &lt;enumeration value="NO_TEMPLATE_PROTECTION_PERMISSION_FOUND"/>
 *     &lt;enumeration value="NO_FDL_CODE_BLOCK_FOUND"/>
 *     &lt;enumeration value="NO_PAYMENT_FOUND"/>
 *     &lt;enumeration value="NO_PAYMENT_PROVIDER_FOUND"/>
 *     &lt;enumeration value="NO_PAYMENT_METHOD_INSTANCE_FOUND"/>
 *     &lt;enumeration value="NO_TRANSACTION_FOUND"/>
 *     &lt;enumeration value="NO_BLOB_FOUND"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "errorResourceNotFound")
@XmlEnum
public enum ErrorResourceNotFound {


    /**
     * Cannot find customer
     * 
     */
    NO_CUSTOMER_FOUND,

    /**
     * Cannot find user
     * 
     */
    NO_USER_FOUND,

    /**
     * Cannot find billing entity
     * 
     */
    NO_BILLING_ENTITY_FOUND,

    /**
     * Cannot find product offer
     * 
     */
    NO_PRODUCT_OFFER_FOUND,

    /**
     * Cannot find product purchase
     * 
     */
    NO_PRODUCT_PURCHASE_FOUND,

    /**
     * Cannot find unit transaction
     * 
     */
    NO_UNIT_TRANSACTION_FOUND,

    /**
     * Cannot find invoice
     * 
     */
    NO_INVOICE_FOUND,

    /**
     * Cannot find product
     * 
     */
    NO_PRODUCT_FOUND,

    /**
     * Cannot find billing period
     * 
     */
    NO_PERIOD_FOUND,

    /**
     * Cannot find promotion
     * 
     */
    NO_PROMOTION_FOUND,

    /**
     * Cannot find promotion code
     * 
     */
    NO_PROMOTION_CODE_FOUND,

    /**
     * Cannot find currency
     * 
     */
    NO_CURRENCY_FOUND,

    /**
     * Cannot found promocode
     * 
     */
    NO_PROMOCODE_FOUND,

    /**
     * Cannot find referral scheme
     * 
     */
    NO_REFERRAL_SCHEME_FOUND,

    /**
     * Cannot find referral promocode
     * 
     */
    NO_REFERRAL_PROMO_CODE_FOUND,

    /**
     * Cannot find unit balance
     * 
     */
    NO_UNIT_BALANCE_FOUND,

    /**
     * Cannot find VDC
     * 
     */
    NO_VDC_FOUND,

    /**
     * Cannot find image
     * 
     */
    NO_IMAGE_FOUND,

    /**
     * Cannot find image permission
     * 
     */
    NO_IMAGE_PERMISSION_FOUND,

    /**
     * Cannot find job
     * 
     */
    NO_JOB_FOUND,

    /**
     * Cannot find server
     * 
     */
    NO_SERVER_FOUND,

    /**
     * Cannot find resource
     * 
     */
    NO_RESOURCE_FOUND,

    /**
     * Cannot find disk
     * 
     */
    NO_DISK_FOUND,

    /**
     * Cannot find server details
     * 
     */
    NO_SERVER_DETAILS_FOUND,

    /**
     * Cannot find firewall
     * 
     */
    FIREWALL_NOT_FOUND,

    /**
     * Cannot find boot disk
     * 
     */
    BOOT_DISK_NOT_FOUND,

    /**
     * Cannot find NIC
     * 
     */
    NO_NIC_FOUND,

    /**
     * Cannot find snapshot
     * 
     */
    NO_SNAPSHOT_FOUND,

    /**
     * Cannot find network
     * 
     */
    NO_NETWORK_FOUND,

    /**
     * Cannot find subnet
     * 
     */
    NO_SUBNET_FOUND,

    /**
     * Cannot find firewall
     * 
     */
    NO_FIREWALL_FOUND,

    /**
     * Cannot find product component
     * 
     */
    NO_PRODUCT_COMPONENT_FOUND,

    /**
     * Cannot find ssh key
     * 
     */
    NO_SSH_KEY_FOUND,

    /**
     * Cannot find resource
     * 
     */
    NO_RESOURCE_KEY_FOUND,

    /**
     * Cannot find resource UUID
     * 
     */
    NO_RESOURCE_UUID_FOUND,

    /**
     * Cannot find fetch parameters
     * 
     */
    NO_FETCH_PARAMS_FOUND,

    /**
     * Cannot find cluster
     * 
     */
    NO_CLUSTER_FOUND,

    /**
     * Cannot find firewall template
     * 
     */
    NO_FIREWALL_TEMPLATE_FOUND,

    /**
     * Cannot find product offer UUID
     * 
     */
    NO_PRODUCT_OFFER_UUID_FOUND,

    /**
     * Cannot find snapshot UUID
     * 
     */
    NO_SNAPSHOT_UUID_FOUND,

    /**
     * Cannot find firewall UUID
     * 
     */
    NO_FIREWALL_UUID_FOUND,

    /**
     * Cannot find image UUID
     * 
     */
    NO_IMAGE_UUID_FOUND,

    /**
     * Cannot find NIC UUID
     * 
     */
    NO_NIC_UUID_FOUND,

    /**
     * Cannot find server UUID
     * 
     */
    NO_SERVER_UUID_FOUND,

    /**
     * Cannot find subnet UUID
     * 
     */
    NO_SUBNET_UUID_FOUND,

    /**
     * Cannot find VDC UUID
     * 
     */
    NO_VDC_UUID_FOUND,

    /**
     * Cannot find network UUID
     * 
     */
    NO_NETWORK_UUID_FOUND,

    /**
     * Cannot find Cluster UUID
     * 
     */
    NO_CLUSTER_UUID_FOUND,

    /**
     * Cannot find job UUID
     * 
     */
    NO_JOB_UUID_FOUND,

    /**
     * Cannot find firewall template UUID
     * 
     */
    NO_FIREWALL_TEMPLATE_UUID_FOUND,

    /**
     * Cannot find group
     * 
     */
    NO_GROUP_FOUND,

    /**
     * Cannot find IP address
     * 
     */
    NO_IP_FOUND,

    /**
     * Cannot find storage unit
     * 
     */
    NO_STORAGE_UNIT_FOUND,

    /**
     * Cannot find payment method
     * 
     */
    NO_PAYMENT_METHOD_FOUND,

    /**
     * No user could be found in an unlocked state
     * 
     */
    NO_UNLOCKED_USER_FOUND,

    /**
     * No deployment template found
     * 
     */
    NO_DEPLOYMENT_TEMPLATE_FOUND,

    /**
     * No deployment instance found
     * 
     */
    NO_DEPLOYMENT_INSTANCE_FOUND,

    /**
     * No template protection permission found
     * 
     */
    NO_TEMPLATE_PROTECTION_PERMISSION_FOUND,

    /**
     * No FDL code block found
     * 
     */
    NO_FDL_CODE_BLOCK_FOUND,

    /**
     * No payment found
     * 
     */
    NO_PAYMENT_FOUND,

    /**
     * No payment provider found
     * 
     */
    NO_PAYMENT_PROVIDER_FOUND,

    /**
     * No payment method instance found
     * 
     */
    NO_PAYMENT_METHOD_INSTANCE_FOUND,

    /**
     * No transaction found
     * 
     */
    NO_TRANSACTION_FOUND,

    /**
     * No Blob found
     * 
     */
    NO_BLOB_FOUND;

    public String value() {
        return name();
    }

    public static ErrorResourceNotFound fromValue(String v) {
        return valueOf(v);
    }

}
