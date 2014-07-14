
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resourceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="resourceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CLUSTER"/>
 *     &lt;enumeration value="CUSTOMER"/>
 *     &lt;enumeration value="USER"/>
 *     &lt;enumeration value="DISK"/>
 *     &lt;enumeration value="SNAPSHOT"/>
 *     &lt;enumeration value="FIREWALL"/>
 *     &lt;enumeration value="NIC"/>
 *     &lt;enumeration value="IMAGE"/>
 *     &lt;enumeration value="SERVER"/>
 *     &lt;enumeration value="SUBNET"/>
 *     &lt;enumeration value="VDC"/>
 *     &lt;enumeration value="NETWORK"/>
 *     &lt;enumeration value="SSHKEY"/>
 *     &lt;enumeration value="JOB"/>
 *     &lt;enumeration value="PRODUCTOFFER"/>
 *     &lt;enumeration value="FIREWALL_TEMPLATE"/>
 *     &lt;enumeration value="BILLING_ENTITY"/>
 *     &lt;enumeration value="GROUP"/>
 *     &lt;enumeration value="ANY"/>
 *     &lt;enumeration value="PAYMENTCARD"/>
 *     &lt;enumeration value="INVOICE"/>
 *     &lt;enumeration value="PROMOTION"/>
 *     &lt;enumeration value="REFERRAL_PROMOTION"/>
 *     &lt;enumeration value="DEPLOYMENT_TEMPLATE"/>
 *     &lt;enumeration value="DEPLOYMENT_INSTANCE"/>
 *     &lt;enumeration value="BILLING_METHOD"/>
 *     &lt;enumeration value="PRODUCT"/>
 *     &lt;enumeration value="PRODUCT_COMP_TYPE"/>
 *     &lt;enumeration value="FDL"/>
 *     &lt;enumeration value="PAYMENT_PROVIDER"/>
 *     &lt;enumeration value="PAYMENT_METHOD"/>
 *     &lt;enumeration value="PAYMENT_METHOD_INSTANCE"/>
 *     &lt;enumeration value="TRANSACTION"/>
 *     &lt;enumeration value="TRIGGER_METHOD"/>
 *     &lt;enumeration value="CREDIT_NOTE"/>
 *     &lt;enumeration value="BLOB"/>
 *     &lt;enumeration value="UNIT_TRANSACTION"/>
 *     &lt;enumeration value="UNIT_TRANSACTION_SUMMARY"/>
 *     &lt;enumeration value="PRODUCT_PURCHASE"/>
 *     &lt;enumeration value="CURRENCY"/>
 *     &lt;enumeration value="TRANSACTION_LOG"/>
 *     &lt;enumeration value="PERMISSION"/>
 *     &lt;enumeration value="PROMOCODE"/>
 *     &lt;enumeration value="PURCHASED_UNITS"/>
 *     &lt;enumeration value="REFERRAL_PROMOCODE"/>
 *     &lt;enumeration value="IMAGEINSTANCE"/>
 *     &lt;enumeration value="FETCH_RESOURCE"/>
 *     &lt;enumeration value="MEASUREMENT"/>
 *     &lt;enumeration value="STATEMENT_DETAIL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "resourceType")
@XmlEnum
public enum ResourceType {


    /**
     * Cluster
     * 
     */
    CLUSTER,

    /**
     * Customer
     * 
     */
    CUSTOMER,

    /**
     * User
     * 
     */
    USER,

    /**
     * Disk
     * 
     */
    DISK,

    /**
     * Snapshot of a disk or server
     * 
     */
    SNAPSHOT,

    /**
     * Firewall
     * 
     */
    FIREWALL,

    /**
     * NIC
     * 
     */
    NIC,

    /**
     * Image
     * 
     */
    IMAGE,

    /**
     * Server
     * 
     */
    SERVER,

    /**
     * Subnet
     * 
     */
    SUBNET,

    /**
     * VDC
     * 
     */
    VDC,

    /**
     * Network
     * 
     */
    NETWORK,

    /**
     * SSHKey
     * 
     */
    SSHKEY,

    /**
     * Job
     * 
     */
    JOB,

    /**
     * Product offer
     * 
     */
    PRODUCTOFFER,

    /**
     * Fireall template
     * 
     */
    FIREWALL_TEMPLATE,

    /**
     * Billing Entity
     * 
     */
    BILLING_ENTITY,

    /**
     * Group
     * 
     */
    GROUP,

    /**
     * Any resource
     * 
     */
    ANY,

    /**
     * Payment card
     * 
     */
    PAYMENTCARD,

    /**
     * Payment method
     * 
     */
    INVOICE,

    /**
     * Promotion
     * 
     */
    PROMOTION,

    /**
     * ReferralPromotion
     * 
     */
    REFERRAL_PROMOTION,

    /**
     * Template
     * 
     */
    DEPLOYMENT_TEMPLATE,

    /**
     * DeploymentInstance
     * 
     */
    DEPLOYMENT_INSTANCE,

    /**
     * BillingMethod
     * 
     */
    BILLING_METHOD,

    /**
     * Product
     * 
     */
    PRODUCT,

    /**
     * ProductComponentType
     * 
     */
    PRODUCT_COMP_TYPE,

    /**
     * FDLResource
     * 
     */
    FDL,

    /**
     * PaymentProvider
     * 
     */
    PAYMENT_PROVIDER,

    /**
     * PaymentMethod
     * 
     */
    PAYMENT_METHOD,

    /**
     * PaymentMethodInstance
     * 
     */
    PAYMENT_METHOD_INSTANCE,

    /**
     * Transaction
     * 
     */
    TRANSACTION,

    /**
     * TriggerMethod
     * 
     */
    TRIGGER_METHOD,

    /**
     * TriggerMethod
     * 
     */
    CREDIT_NOTE,

    /**
     * Blob
     * 
     */
    BLOB,

    /**
     * Unit Transaction (not a resource, for use with doQuery but cannot be used with listResources etc)
     * 
     */
    UNIT_TRANSACTION,

    /**
     * Unit Transaction Summary (not a resource, for use with doQuery but cannot be used with listResources etc)
     * 
     */
    UNIT_TRANSACTION_SUMMARY,

    /**
     * Product Purchased (not a resource, for use with doQuery but cannot be used with listResources etc)
     * 
     */
    PRODUCT_PURCHASE,

    /**
     * Currencies used with in jade (not resource can only be used with the listCurrency call)
     * 
     */
    CURRENCY,

    /**
     * Tranction logs created when using payment gateway (not a resource can only be used with the listTransactionLogs call)
     * 
     */
    TRANSACTION_LOG,

    /**
     * Permission (not a resource, for use with doQuery but cannot be used with listResources etc)
     * 
     */
    PERMISSION,

    /**
     * PromoCode (not a resource, for use with doQuery but cannot be used with listResources etc)
     * 
     */
    PROMOCODE,

    /**
     * Purchased Units (not a resource, for use with doQuery but cannot be used with listResources etc)
     * 
     */
    PURCHASED_UNITS,

    /**
     * Referral Promocode (not a resource, for use with doQuery but cannot be used with listResources etc)
     * 
     */
    REFERRAL_PROMOCODE,

    /**
     * A billable image instance
     * 
     */
    IMAGEINSTANCE,

    /**
     * A fetch parameter instance
     * 
     */
    FETCH_RESOURCE,

    /**
     * An instance of a measurement taken from a resource
     * 
     */
    MEASUREMENT,

    /**
     * This holds an instance of currency statement.
     * 
     */
    STATEMENT_DETAIL;

    public String value() {
        return name();
    }

    public static ResourceType fromValue(String v) {
        return valueOf(v);
    }

}
