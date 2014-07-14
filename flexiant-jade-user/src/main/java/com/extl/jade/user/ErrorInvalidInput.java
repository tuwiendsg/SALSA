
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for errorInvalidInput.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="errorInvalidInput">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="REQUIRED_FIELD_NOT_SET"/>
 *     &lt;enumeration value="INVALID_DATE_FORMAT"/>
 *     &lt;enumeration value="INVALID_DATE_RANGE"/>
 *     &lt;enumeration value="INVALID_REFERRAL_PRIORITY"/>
 *     &lt;enumeration value="INVALID_REFERRAL_SCHEME_NAME"/>
 *     &lt;enumeration value="NO_REFERRAL_PROMOTION_UUID_DEFINED"/>
 *     &lt;enumeration value="INVALID_REFERRAL_EMAIL_SUBJECT"/>
 *     &lt;enumeration value="INVALID_REFERRAL_EMAIL_BODY"/>
 *     &lt;enumeration value="INVALID_REFERRAL_INVITEE_UNITS"/>
 *     &lt;enumeration value="INVALID_REFERRAL_INVITOR_UNITS"/>
 *     &lt;enumeration value="INVALID_INVITEE_MINIMUM_PURCHASE"/>
 *     &lt;enumeration value="INVALID_REFERRAL_UNIT_COST"/>
 *     &lt;enumeration value="INVALID_MAX_OUTSTANDING"/>
 *     &lt;enumeration value="INVALID_CURRENT_OUTSTANDING"/>
 *     &lt;enumeration value="INVALID_EXPIRY_DAYS"/>
 *     &lt;enumeration value="NO_REFERRAL_SCHEME_DEFINED"/>
 *     &lt;enumeration value="NO_REFERRAL_PROMO_CODE_DEFINED"/>
 *     &lt;enumeration value="INVALID_INVITOR_CUSTOMER_ID"/>
 *     &lt;enumeration value="INVALID_EMAIL_ADDRESS"/>
 *     &lt;enumeration value="INVALID_EXPIRY_DATE"/>
 *     &lt;enumeration value="INVALID_STATUS"/>
 *     &lt;enumeration value="NO_PRODUCT_OFFER_DEFINED"/>
 *     &lt;enumeration value="INVALID_PRODUCT_OFFER"/>
 *     &lt;enumeration value="INVALID_BILLING_PERIOD"/>
 *     &lt;enumeration value="NO_PRODUCT_ID_DEFINED"/>
 *     &lt;enumeration value="PROMOTION_IS_EXPIRED"/>
 *     &lt;enumeration value="PROMOTION_NAME_ALREADY_EXISTS"/>
 *     &lt;enumeration value="INVALID_PROMO_CODE"/>
 *     &lt;enumeration value="NO_PROMOTION_DEFINED"/>
 *     &lt;enumeration value="NO_PROMOTION_CODE_DEFINED"/>
 *     &lt;enumeration value="NO_PROMOTION_FILTER_DEFINED"/>
 *     &lt;enumeration value="NO_PROMOTION_NAME_DEFINED"/>
 *     &lt;enumeration value="CANNOT_ASSOCIATE_CUSTOMER_TO_PROMOTION"/>
 *     &lt;enumeration value="INVALID_PROMO_TYPE_ID"/>
 *     &lt;enumeration value="NO_PROMO_CODE_DEFINED"/>
 *     &lt;enumeration value="PROMOTION_CODE_NAME_ALREADY_EXISTS"/>
 *     &lt;enumeration value="NO_PROMOTION_UUID_DEFINED"/>
 *     &lt;enumeration value="NO_BILLING_ENTITY_DEFINED"/>
 *     &lt;enumeration value="NO_BILLING_ENTITY_NAME_DEFINED"/>
 *     &lt;enumeration value="INVALID_BILLING_ENTITY"/>
 *     &lt;enumeration value="INVALID_CURRENCY_ID"/>
 *     &lt;enumeration value="NO_USER_DEFINED"/>
 *     &lt;enumeration value="NO_PASSWORD_DEFINED"/>
 *     &lt;enumeration value="NO_CUSTOMER_UUID_DEFINED"/>
 *     &lt;enumeration value="NO_CUSTOMER_ID_DEFINED"/>
 *     &lt;enumeration value="NO_ORGANISATION_NAME_DEFINED"/>
 *     &lt;enumeration value="NO_ADDRESS_DEFINED"/>
 *     &lt;enumeration value="NO_CUSTOMER_DEFINED"/>
 *     &lt;enumeration value="NO_DESCRIPTION_DEFINED"/>
 *     &lt;enumeration value="NO_CURRENCYID_DEFINED"/>
 *     &lt;enumeration value="NO_USERID_DEFINED"/>
 *     &lt;enumeration value="NO_3DS_28_DAY_SPEND_LIMIT_DEFINED"/>
 *     &lt;enumeration value="NO_OVERALL_28_DAY_SPEND_LIMIT_DEFINED"/>
 *     &lt;enumeration value="NO_LIMITS_DEFINED"/>
 *     &lt;enumeration value="NO_VDC_DEFINED"/>
 *     &lt;enumeration value="NO_CREDIT_LIMIT_DEFINED"/>
 *     &lt;enumeration value="NO_EMAIL_TEMPLATE_DEFINED"/>
 *     &lt;enumeration value="NO_EMAIL_TYPE_DEFINED"/>
 *     &lt;enumeration value="NO_USER_UUID_DEFINED"/>
 *     &lt;enumeration value="INVALID_GROUP"/>
 *     &lt;enumeration value="INVALID_GROUP_TYPE"/>
 *     &lt;enumeration value="NO_TRANSACTION_TYPE_DEFINED"/>
 *     &lt;enumeration value="NO_UNITS_DEFINED"/>
 *     &lt;enumeration value="NO_UNIT_TYPE_DEFINED"/>
 *     &lt;enumeration value="NO_TRANSACTION_AMOUNT_DEFINED"/>
 *     &lt;enumeration value="NO_INVOICE_NO_DEFINED"/>
 *     &lt;enumeration value="INVALID_RESOURCE_UUID"/>
 *     &lt;enumeration value="RESOURCE_KEY_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="INVALID_RESOURCE_METADATA"/>
 *     &lt;enumeration value="INVALID_RESOURCE_KEY_TYPE"/>
 *     &lt;enumeration value="INVALID_SERVER_UUID"/>
 *     &lt;enumeration value="NIC_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="DISK_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="INVALID_DISK_UUID"/>
 *     &lt;enumeration value="INVALID_IMAGE_UUID"/>
 *     &lt;enumeration value="NO_DISK_NAME_SPECIFIED"/>
 *     &lt;enumeration value="VDC_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="INVALID_START_DATE"/>
 *     &lt;enumeration value="INVALID_PRIVATE_METADATA"/>
 *     &lt;enumeration value="INVALID_PUBLIC_METADATA"/>
 *     &lt;enumeration value="INVALID_RESTRICTED_METADATA"/>
 *     &lt;enumeration value="INVALID_NIC_UUID"/>
 *     &lt;enumeration value="INVALID_VDC_UUID"/>
 *     &lt;enumeration value="INVALID_BASE_TYPE"/>
 *     &lt;enumeration value="SERVER_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="INVALID_SERVER_NAME"/>
 *     &lt;enumeration value="NETWORK_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="INVALID_NETWORK_TYPE"/>
 *     &lt;enumeration value="INVALID_JOB_UUID"/>
 *     &lt;enumeration value="NO_RESOURCE_TYPE_DEFINED"/>
 *     &lt;enumeration value="INVALID_SSHKEY_UUID"/>
 *     &lt;enumeration value="INVALID_INDEX"/>
 *     &lt;enumeration value="RESOURCE_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="NO_RESOURCE_KEY_DEFINED"/>
 *     &lt;enumeration value="NO_FETCH_PARAMETERS_DEFINED"/>
 *     &lt;enumeration value="RESOURCE_NAME_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="NO_RESOURCE_NAME_DEFINED"/>
 *     &lt;enumeration value="SSHKEY_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="INVALID_FIREWALL_PROTOCOL"/>
 *     &lt;enumeration value="INVALID_FIREWALL_DIRECTION"/>
 *     &lt;enumeration value="INVALID_FIREWALL_ACTION"/>
 *     &lt;enumeration value="INVALID_FIREWALL_CONN_STATE"/>
 *     &lt;enumeration value="INVALID_SUBNET_TYPE"/>
 *     &lt;enumeration value="INVALID_USER_NAME_FORMAT"/>
 *     &lt;enumeration value="INVALID_PASSWORD_FORMAT"/>
 *     &lt;enumeration value="INVALID_CHECKSUM_FORMAT"/>
 *     &lt;enumeration value="NO_JOB_FOUND"/>
 *     &lt;enumeration value="INVALID_IP_ADDRESS"/>
 *     &lt;enumeration value="INVALID_NETMASK"/>
 *     &lt;enumeration value="INVALID_IP_TYPE"/>
 *     &lt;enumeration value="SNAPSHOT_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="NO_SNAPSHOT_TYPE_DEFINED"/>
 *     &lt;enumeration value="NO_SNAPSHOT_PARENT_UUID_DEFINED"/>
 *     &lt;enumeration value="NO_SNAPSHOT_NAME_DEFINED"/>
 *     &lt;enumeration value="INVALID_NETWORK_UUID"/>
 *     &lt;enumeration value="INVALID_SUBNET_UUID"/>
 *     &lt;enumeration value="NO_BASE_UUID_SET"/>
 *     &lt;enumeration value="CAPABILITIES_SET_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="INVALID_FILTER_FIELD"/>
 *     &lt;enumeration value="INVALID_SERVER_STATE"/>
 *     &lt;enumeration value="INVALID_CAPABILITY"/>
 *     &lt;enumeration value="INVALID_RESOURCE_TYPE"/>
 *     &lt;enumeration value="ENUM_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="ENUM_DO_NOT_IMPLEMENT_DBENUM"/>
 *     &lt;enumeration value="NO_VDC_UUID_DEFINED"/>
 *     &lt;enumeration value="NO_CLUSTER_DEFINED"/>
 *     &lt;enumeration value="NO_CLUSTER_NAME_DEFINED"/>
 *     &lt;enumeration value="RESOURCE_IS_NOT_VIRTUAL_RESOURCE"/>
 *     &lt;enumeration value="INVALID_CLUSTER_UUID"/>
 *     &lt;enumeration value="INVALID_INPUT"/>
 *     &lt;enumeration value="INVALID_USER_UUID"/>
 *     &lt;enumeration value="GROUP_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="NO_PAYMENT_CARD_DEFINED"/>
 *     &lt;enumeration value="INVALID_PAYMENT_METHOD_ID"/>
 *     &lt;enumeration value="CLUSTER_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="STORAGE_UNIT_CANNOT_BE_NULL"/>
 *     &lt;enumeration value="NO_PRODUCT_DEFINED"/>
 *     &lt;enumeration value="PRODUCT_ID_FOR_DIFFERENT_PRODUCT"/>
 *     &lt;enumeration value="NO_PAYMENT_METHOD_DEFINED"/>
 *     &lt;enumeration value="NO_PAYMENT_PROVIDER_DEFINED"/>
 *     &lt;enumeration value="NO_PAYMENT_METHOD_NAME_DEFINED"/>
 *     &lt;enumeration value="NO_PAYMENT_METHOD_UUID_DEFINED"/>
 *     &lt;enumeration value="INVALID_PAYMENT_METHOD_UUID"/>
 *     &lt;enumeration value="INVALID_PRODUCT_COMPONENTS"/>
 *     &lt;enumeration value="NO_INVOICE_DEFINED"/>
 *     &lt;enumeration value="INVALID_INVOICE_NUMBER"/>
 *     &lt;enumeration value="INVALID_INVOICE_SOURCE"/>
 *     &lt;enumeration value="INVALID_INVOICE_PAYMENT_PROVIDER"/>
 *     &lt;enumeration value="INVALID_INVOICE_PAYMENT_PROVIDER_REFERENCE"/>
 *     &lt;enumeration value="NO_PURCHASE_REFERENCE_DEFINED"/>
 *     &lt;enumeration value="NO_INVOICE_UUID_DEFINED"/>
 *     &lt;enumeration value="NO_TRANSACTION_LOG_DEFINED"/>
 *     &lt;enumeration value="NO_SERVER_SPECIFIED"/>
 *     &lt;enumeration value="NO_AGGREGATION_FUNCTION_DEFINED"/>
 *     &lt;enumeration value="NO_AGGREGATION_FIELD_DEFINED"/>
 *     &lt;enumeration value="INVALID_ORDER_BY_FIELD"/>
 *     &lt;enumeration value="INVALID_INVOICE_DATE"/>
 *     &lt;enumeration value="NO_OUTPUT_FIELDS_DEFINED"/>
 *     &lt;enumeration value="NO_START_OR_END_DATE_DEFINED"/>
 *     &lt;enumeration value="INVALID_AGGREGATION_CONDITION_IN_FILTER"/>
 *     &lt;enumeration value="NO_DEPLOYMENT_TEMPLATE_DEFINED"/>
 *     &lt;enumeration value="NO_DEPLOYMENT_TEMPLATE_UUID_DEFINED"/>
 *     &lt;enumeration value="INVALID_DEPLOYMENT_INSTANCE_UUID"/>
 *     &lt;enumeration value="NO_DEPLOYMENT_INSTANCE_DEFINED"/>
 *     &lt;enumeration value="NO_NETWORK_DEFINED"/>
 *     &lt;enumeration value="NO_CURRENCY_DEFINED"/>
 *     &lt;enumeration value="NO_CURRENCY_CODE_DEFINED"/>
 *     &lt;enumeration value="CURRENCY_CODE_LENGTH_CANNOT_BE_GREATER_THAN_3"/>
 *     &lt;enumeration value="EXISTING_CURRENCY_CODE"/>
 *     &lt;enumeration value="INVALID_NAMESPACE"/>
 *     &lt;enumeration value="NO_SUPPORT_FOR_MULTI_VALUES"/>
 *     &lt;enumeration value="INVALID_VALUE_SET_FOR_FIELD"/>
 *     &lt;enumeration value="INVALID_VALUE_INPUT"/>
 *     &lt;enumeration value="FDL_COMPILE_ERROR"/>
 *     &lt;enumeration value="FDL_REGISTER_ERROR"/>
 *     &lt;enumeration value="NO_SIZE_SPECIFIED"/>
 *     &lt;enumeration value="INVALID_URL"/>
 *     &lt;enumeration value="INVALID_PCT"/>
 *     &lt;enumeration value="PCT_NOT_DEFINED"/>
 *     &lt;enumeration value="INVALID_BILLING_METHOD"/>
 *     &lt;enumeration value="FDL_CODE_SIGNING_ERROR"/>
 *     &lt;enumeration value="NO_PRODUCT_COMPONENT_SPECIFIED"/>
 *     &lt;enumeration value="REQUIRED_PRODUCT_COMPONENTS_NOT_SET"/>
 *     &lt;enumeration value="INVALID_TAG_NAME"/>
 *     &lt;enumeration value="NO_PAYMENT_REFERENCE_DEFINED"/>
 *     &lt;enumeration value="MISMATCHED_BILLING_ENTITIES"/>
 *     &lt;enumeration value="HASH_INVALID"/>
 *     &lt;enumeration value="NO_CONFIGURED_VALUES_SPECIFIED"/>
 *     &lt;enumeration value="NO_BLOB_DEFINED"/>
 *     &lt;enumeration value="NO_BLOB_RESOURCE_UUID_DEFINED"/>
 *     &lt;enumeration value="NO_PAYMENT_METHOD_INSTANCE_DEFINED"/>
 *     &lt;enumeration value="NO_PAYMENT_METHOD_INSTANCE_NAME_DEFINED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "errorInvalidInput")
@XmlEnum
public enum ErrorInvalidInput {


    /**
     * The value of a required field has not been set
     * 
     */
    REQUIRED_FIELD_NOT_SET("REQUIRED_FIELD_NOT_SET"),

    /**
     * The format of a date field specified is invalid
     * 
     */
    INVALID_DATE_FORMAT("INVALID_DATE_FORMAT"),

    /**
     * An invalid date range has been specified
     * 
     */
    INVALID_DATE_RANGE("INVALID_DATE_RANGE"),

    /**
     * A referral scheme priority value is invalid
     * 
     */
    INVALID_REFERRAL_PRIORITY("INVALID_REFERRAL_PRIORITY"),

    /**
     * A referral scheme name is invalid
     * 
     */
    INVALID_REFERRAL_SCHEME_NAME("INVALID_REFERRAL_SCHEME_NAME"),

    /**
     * No referral promotion UUID is defined
     * 
     */
    NO_REFERRAL_PROMOTION_UUID_DEFINED("NO_REFERRAL_PROMOTION_UUID_DEFINED"),

    /**
     * A referral email subject is invalid
     * 
     */
    INVALID_REFERRAL_EMAIL_SUBJECT("INVALID_REFERRAL_EMAIL_SUBJECT"),

    /**
     * A referral email body is invalid
     * 
     */
    INVALID_REFERRAL_EMAIL_BODY("INVALID_REFERRAL_EMAIL_BODY"),

    /**
     * A referral invitee units value is invalid
     * 
     */
    INVALID_REFERRAL_INVITEE_UNITS("INVALID_REFERRAL_INVITEE_UNITS"),

    /**
     * A referral invitor units value is invalid
     * 
     */
    INVALID_REFERRAL_INVITOR_UNITS("INVALID_REFERRAL_INVITOR_UNITS"),

    /**
     * A referral minimum purchase level is invalid
     * 
     */
    INVALID_INVITEE_MINIMUM_PURCHASE("INVALID_INVITEE_MINIMUM_PURCHASE"),

    /**
     * A referral unit cost is invalid
     * 
     */
    INVALID_REFERRAL_UNIT_COST("INVALID_REFERRAL_UNIT_COST"),

    /**
     * The value for the maximum number of outstanding referrals is invalid
     * 
     */
    INVALID_MAX_OUTSTANDING("INVALID_MAX_OUTSTANDING"),

    /**
     * The value for the current number of outstanding referrals is invalid
     * 
     */
    INVALID_CURRENT_OUTSTANDING("INVALID_CURRENT_OUTSTANDING"),

    /**
     * A referral expiry days value is invalid
     * 
     */
    INVALID_EXPIRY_DAYS("INVALID_EXPIRY_DAYS"),

    /**
     * No referral scheme has been defined
     * 
     */
    NO_REFERRAL_SCHEME_DEFINED("NO_REFERRAL_SCHEME_DEFINED"),

    /**
     * No referral promotion code has been defined
     * 
     */
    NO_REFERRAL_PROMO_CODE_DEFINED("NO_REFERRAL_PROMO_CODE_DEFINED"),

    /**
     * The referral customer ID is invalid
     * 
     */
    INVALID_INVITOR_CUSTOMER_ID("INVALID_INVITOR_CUSTOMER_ID"),

    /**
     * The email address is invalid
     * 
     */
    INVALID_EMAIL_ADDRESS("INVALID_EMAIL_ADDRESS"),

    /**
     * The expiry date is invalid
     * 
     */
    INVALID_EXPIRY_DATE("INVALID_EXPIRY_DATE"),

    /**
     * The status is invalid
     * 
     */
    INVALID_STATUS("INVALID_STATUS"),

    /**
     * No product offer has been defined
     * 
     */
    NO_PRODUCT_OFFER_DEFINED("NO_PRODUCT_OFFER_DEFINED"),

    /**
     * An invalid product offer has been passed
     * 
     */
    INVALID_PRODUCT_OFFER("INVALID_PRODUCT_OFFER"),

    /**
     * A product offer billing period is invalid
     * 
     */
    INVALID_BILLING_PERIOD("INVALID_BILLING_PERIOD"),

    /**
     * No valid product id has been passed
     * 
     */
    NO_PRODUCT_ID_DEFINED("NO_PRODUCT_ID_DEFINED"),

    /**
     * The promotion concerned is expired
     * 
     */
    PROMOTION_IS_EXPIRED("PROMOTION_IS_EXPIRED"),

    /**
     * A promotion of that type/name already exists
     * 
     */
    PROMOTION_NAME_ALREADY_EXISTS("PROMOTION_NAME_ALREADY_EXISTS"),

    /**
     * A promotion code is invalid
     * 
     */
    INVALID_PROMO_CODE("INVALID_PROMO_CODE"),

    /**
     * No promotion has been defined
     * 
     */
    NO_PROMOTION_DEFINED("NO_PROMOTION_DEFINED"),

    /**
     * No promotion code has been defined
     * 
     */
    NO_PROMOTION_CODE_DEFINED("NO_PROMOTION_CODE_DEFINED"),

    /**
     * No promotion filter has been defined
     * 
     */
    NO_PROMOTION_FILTER_DEFINED("NO_PROMOTION_FILTER_DEFINED"),

    /**
     * No promotion name has been defined
     * 
     */
    NO_PROMOTION_NAME_DEFINED("NO_PROMOTION_NAME_DEFINED"),

    /**
     * Failed to associate a customer with a given promotion
     * 
     */
    CANNOT_ASSOCIATE_CUSTOMER_TO_PROMOTION("CANNOT_ASSOCIATE_CUSTOMER_TO_PROMOTION"),

    /**
     * An invalid promotion type ID has been passed
     * 
     */
    INVALID_PROMO_TYPE_ID("INVALID_PROMO_TYPE_ID"),

    /**
     * No promotion code has been defined
     * 
     */
    NO_PROMO_CODE_DEFINED("NO_PROMO_CODE_DEFINED"),

    /**
     * That promotion code name already exists
     * 
     */
    PROMOTION_CODE_NAME_ALREADY_EXISTS("PROMOTION_CODE_NAME_ALREADY_EXISTS"),

    /**
     * No Promotion UUID been defined
     * 
     */
    NO_PROMOTION_UUID_DEFINED("NO_PROMOTION_UUID_DEFINED"),

    /**
     * No billing entity has been defined
     * 
     */
    NO_BILLING_ENTITY_DEFINED("NO_BILLING_ENTITY_DEFINED"),

    /**
     * No billing entity name has been defined
     * 
     */
    NO_BILLING_ENTITY_NAME_DEFINED("NO_BILLING_ENTITY_NAME_DEFINED"),

    /**
     * An invalid billing entity has been passed
     * 
     */
    INVALID_BILLING_ENTITY("INVALID_BILLING_ENTITY"),

    /**
     * An invalid currency ID has been passed
     * 
     */
    INVALID_CURRENCY_ID("INVALID_CURRENCY_ID"),

    /**
     * No user has been defined
     * 
     */
    NO_USER_DEFINED("NO_USER_DEFINED"),

    /**
     * No password has been defined
     * 
     */
    NO_PASSWORD_DEFINED("NO_PASSWORD_DEFINED"),

    /**
     * No valid customer UUID has been defined
     * 
     */
    NO_CUSTOMER_UUID_DEFINED("NO_CUSTOMER_UUID_DEFINED"),

    /**
     * No valid customer ID has been defined
     * 
     */
    NO_CUSTOMER_ID_DEFINED("NO_CUSTOMER_ID_DEFINED"),

    /**
     * No organisation name has been defined
     * 
     */
    NO_ORGANISATION_NAME_DEFINED("NO_ORGANISATION_NAME_DEFINED"),

    /**
     * No address has been defined
     * 
     */
    NO_ADDRESS_DEFINED("NO_ADDRESS_DEFINED"),

    /**
     * No customer has been defined
     * 
     */
    NO_CUSTOMER_DEFINED("NO_CUSTOMER_DEFINED"),

    /**
     * No description has been defined
     * 
     */
    NO_DESCRIPTION_DEFINED("NO_DESCRIPTION_DEFINED"),

    /**
     * No currency ID has been defined
     * 
     */
    NO_CURRENCYID_DEFINED("NO_CURRENCYID_DEFINED"),

    /**
     * No user ID has been defined
     * 
     */
    NO_USERID_DEFINED("NO_USERID_DEFINED"),

    /**
     * No 28 day spend limit for 3DS transactions has been defined
     * 
     */
    @XmlEnumValue("NO_3DS_28_DAY_SPEND_LIMIT_DEFINED")
    NO_3_DS_28_DAY_SPEND_LIMIT_DEFINED("NO_3DS_28_DAY_SPEND_LIMIT_DEFINED"),

    /**
     * No overall 28 day spend limit has been defined
     * 
     */
    NO_OVERALL_28_DAY_SPEND_LIMIT_DEFINED("NO_OVERALL_28_DAY_SPEND_LIMIT_DEFINED"),

    /**
     * No limits have been defined
     * 
     */
    NO_LIMITS_DEFINED("NO_LIMITS_DEFINED"),

    /**
     * No VDC has been defined
     * 
     */
    NO_VDC_DEFINED("NO_VDC_DEFINED"),

    /**
     * No credit limit has been defined
     * 
     */
    NO_CREDIT_LIMIT_DEFINED("NO_CREDIT_LIMIT_DEFINED"),

    /**
     * No email template has been defined
     * 
     */
    NO_EMAIL_TEMPLATE_DEFINED("NO_EMAIL_TEMPLATE_DEFINED"),

    /**
     * No email type has been defined
     * 
     */
    NO_EMAIL_TYPE_DEFINED("NO_EMAIL_TYPE_DEFINED"),

    /**
     * No user UUID has been defined
     * 
     */
    NO_USER_UUID_DEFINED("NO_USER_UUID_DEFINED"),

    /**
     * The group specified is invalid
     * 
     */
    INVALID_GROUP("INVALID_GROUP"),

    /**
     * The group type specified is invalid
     * 
     */
    INVALID_GROUP_TYPE("INVALID_GROUP_TYPE"),

    /**
     * No transaction type has been defined
     * 
     */
    NO_TRANSACTION_TYPE_DEFINED("NO_TRANSACTION_TYPE_DEFINED"),

    /**
     * No units have been specified
     * 
     */
    NO_UNITS_DEFINED("NO_UNITS_DEFINED"),

    /**
     * No units type has been defined
     * 
     */
    NO_UNIT_TYPE_DEFINED("NO_UNIT_TYPE_DEFINED"),

    /**
     * No transaction amount has been defined
     * 
     */
    NO_TRANSACTION_AMOUNT_DEFINED("NO_TRANSACTION_AMOUNT_DEFINED"),

    /**
     * No invoice has been defined
     * 
     */
    NO_INVOICE_NO_DEFINED("NO_INVOICE_NO_DEFINED"),

    /**
     * An invalid resource UUID has been specified
     * 
     */
    INVALID_RESOURCE_UUID("INVALID_RESOURCE_UUID"),

    /**
     * A null resource key has been specified
     * 
     */
    RESOURCE_KEY_CANNOT_BE_NULL("RESOURCE_KEY_CANNOT_BE_NULL"),

    /**
     * Invalid metadata has been provided
     * 
     */
    INVALID_RESOURCE_METADATA("INVALID_RESOURCE_METADATA"),

    /**
     * An invalid key type has been provided
     * 
     */
    INVALID_RESOURCE_KEY_TYPE("INVALID_RESOURCE_KEY_TYPE"),

    /**
     * An invalid server UUID has been provided
     * 
     */
    INVALID_SERVER_UUID("INVALID_SERVER_UUID"),

    /**
     * The NIC parameter cannot be passed as null
     * 
     */
    NIC_CANNOT_BE_NULL("NIC_CANNOT_BE_NULL"),

    /**
     * The Disk parameter cannot be passed as NULL
     * 
     */
    DISK_CANNOT_BE_NULL("DISK_CANNOT_BE_NULL"),

    /**
     * Invalid disk UUID
     * 
     */
    INVALID_DISK_UUID("INVALID_DISK_UUID"),

    /**
     * Invalid image UUID
     * 
     */
    INVALID_IMAGE_UUID("INVALID_IMAGE_UUID"),

    /**
     * No disk name has been specified
     * 
     */
    NO_DISK_NAME_SPECIFIED("NO_DISK_NAME_SPECIFIED"),

    /**
     * The VDC parameter cannot be passed as NULL
     * 
     */
    VDC_CANNOT_BE_NULL("VDC_CANNOT_BE_NULL"),

    /**
     * An invalid start date has been specified
     * 
     */
    INVALID_START_DATE("INVALID_START_DATE"),

    /**
     * Invalid private metadata
     * 
     */
    INVALID_PRIVATE_METADATA("INVALID_PRIVATE_METADATA"),

    /**
     * Invalid public metadata
     * 
     */
    INVALID_PUBLIC_METADATA("INVALID_PUBLIC_METADATA"),

    /**
     * Invalid restricted metadata
     * 
     */
    INVALID_RESTRICTED_METADATA("INVALID_RESTRICTED_METADATA"),

    /**
     * Invalid NIC UUID
     * 
     */
    INVALID_NIC_UUID("INVALID_NIC_UUID"),

    /**
     * Invalid VDC UUID
     * 
     */
    INVALID_VDC_UUID("INVALID_VDC_UUID"),

    /**
     * Invalid base type
     * 
     */
    INVALID_BASE_TYPE("INVALID_BASE_TYPE"),

    /**
     * Server parameter cannot be null
     * 
     */
    SERVER_CANNOT_BE_NULL("SERVER_CANNOT_BE_NULL"),

    /**
     * Invalid server name
     * 
     */
    INVALID_SERVER_NAME("INVALID_SERVER_NAME"),

    /**
     * Network cannot be null
     * 
     */
    NETWORK_CANNOT_BE_NULL("NETWORK_CANNOT_BE_NULL"),

    /**
     * Invalid network type
     * 
     */
    INVALID_NETWORK_TYPE("INVALID_NETWORK_TYPE"),

    /**
     * Invalid Job UUID
     * 
     */
    INVALID_JOB_UUID("INVALID_JOB_UUID"),

    /**
     * No resource type defined
     * 
     */
    NO_RESOURCE_TYPE_DEFINED("NO_RESOURCE_TYPE_DEFINED"),

    /**
     * Invalid SSHKey UUID
     * 
     */
    INVALID_SSHKEY_UUID("INVALID_SSHKEY_UUID"),

    /**
     * Invalid index
     * 
     */
    INVALID_INDEX("INVALID_INDEX"),

    /**
     * Resource parameter cannot be null
     * 
     */
    RESOURCE_CANNOT_BE_NULL("RESOURCE_CANNOT_BE_NULL"),

    /**
     * No resource key has been defined
     * 
     */
    NO_RESOURCE_KEY_DEFINED("NO_RESOURCE_KEY_DEFINED"),

    /**
     * No fetch parameters have been defined
     * 
     */
    NO_FETCH_PARAMETERS_DEFINED("NO_FETCH_PARAMETERS_DEFINED"),

    /**
     * The resource name cannot be NULL
     * 
     */
    RESOURCE_NAME_CANNOT_BE_NULL("RESOURCE_NAME_CANNOT_BE_NULL"),

    /**
     * No resource name has been defined
     * 
     */
    NO_RESOURCE_NAME_DEFINED("NO_RESOURCE_NAME_DEFINED"),

    /**
     * The SSHKey parameter cannot be null
     * 
     */
    SSHKEY_CANNOT_BE_NULL("SSHKEY_CANNOT_BE_NULL"),

    /**
     * Invalid firewall protocol
     * 
     */
    INVALID_FIREWALL_PROTOCOL("INVALID_FIREWALL_PROTOCOL"),

    /**
     * Invalid firewall direction
     * 
     */
    INVALID_FIREWALL_DIRECTION("INVALID_FIREWALL_DIRECTION"),

    /**
     * Invalid firewall action
     * 
     */
    INVALID_FIREWALL_ACTION("INVALID_FIREWALL_ACTION"),

    /**
     * Invalid firewall connection state
     * 
     */
    INVALID_FIREWALL_CONN_STATE("INVALID_FIREWALL_CONN_STATE"),

    /**
     * Invalid subnet type
     * 
     */
    INVALID_SUBNET_TYPE("INVALID_SUBNET_TYPE"),

    /**
     * Invalid username format
     * 
     */
    INVALID_USER_NAME_FORMAT("INVALID_USER_NAME_FORMAT"),

    /**
     * Invalid password format
     * 
     */
    INVALID_PASSWORD_FORMAT("INVALID_PASSWORD_FORMAT"),

    /**
     * Invalid checksum format
     * 
     */
    INVALID_CHECKSUM_FORMAT("INVALID_CHECKSUM_FORMAT"),

    /**
     * No such job found
     * 
     */
    NO_JOB_FOUND("NO_JOB_FOUND"),

    /**
     * Invalid IP address
     * 
     */
    INVALID_IP_ADDRESS("INVALID_IP_ADDRESS"),

    /**
     * Invalid Netmask
     * 
     */
    INVALID_NETMASK("INVALID_NETMASK"),

    /**
     * Invalid IP type
     * 
     */
    INVALID_IP_TYPE("INVALID_IP_TYPE"),

    /**
     * Snapshot parameter cannot be null
     * 
     */
    SNAPSHOT_CANNOT_BE_NULL("SNAPSHOT_CANNOT_BE_NULL"),

    /**
     * Snapshot type has not been defined
     * 
     */
    NO_SNAPSHOT_TYPE_DEFINED("NO_SNAPSHOT_TYPE_DEFINED"),

    /**
     * No snapshot parent UUID has been defined
     * 
     */
    NO_SNAPSHOT_PARENT_UUID_DEFINED("NO_SNAPSHOT_PARENT_UUID_DEFINED"),

    /**
     * No snapshot name has been defined
     * 
     */
    NO_SNAPSHOT_NAME_DEFINED("NO_SNAPSHOT_NAME_DEFINED"),

    /**
     * Invalid network UUID
     * 
     */
    INVALID_NETWORK_UUID("INVALID_NETWORK_UUID"),

    /**
     * Invalid subnet UUID
     * 
     */
    INVALID_SUBNET_UUID("INVALID_SUBNET_UUID"),

    /**
     * No based UUID has been set
     * 
     */
    NO_BASE_UUID_SET("NO_BASE_UUID_SET"),

    /**
     * The capabilities set cannot be null
     * 
     */
    CAPABILITIES_SET_CANNOT_BE_NULL("CAPABILITIES_SET_CANNOT_BE_NULL"),

    /**
     * The filter field is invalid
     * 
     */
    INVALID_FILTER_FIELD("INVALID_FILTER_FIELD"),

    /**
     * The server state is invalid
     * 
     */
    INVALID_SERVER_STATE("INVALID_SERVER_STATE"),

    /**
     * The capability is invalid
     * 
     */
    INVALID_CAPABILITY("INVALID_CAPABILITY"),

    /**
     * The resource type is invalid
     * 
     */
    INVALID_RESOURCE_TYPE("INVALID_RESOURCE_TYPE"),

    /**
     * An enum value cannot be passed as null
     * 
     */
    ENUM_CANNOT_BE_NULL("ENUM_CANNOT_BE_NULL"),

    /**
     * Internal error - enum cannot be mapped to database value
     * 
     */
    ENUM_DO_NOT_IMPLEMENT_DBENUM("ENUM_DO_NOT_IMPLEMENT_DBENUM"),

    /**
     * No VDC UUID has been defined
     * 
     */
    NO_VDC_UUID_DEFINED("NO_VDC_UUID_DEFINED"),

    /**
     * No cluster has been defined
     * 
     */
    NO_CLUSTER_DEFINED("NO_CLUSTER_DEFINED"),

    /**
     * No cluster name has been defined
     * 
     */
    NO_CLUSTER_NAME_DEFINED("NO_CLUSTER_NAME_DEFINED"),

    /**
     * The resource passed is not a virtual resource
     * 
     */
    RESOURCE_IS_NOT_VIRTUAL_RESOURCE("RESOURCE_IS_NOT_VIRTUAL_RESOURCE"),

    /**
     * Invalid cluster UUID
     * 
     */
    INVALID_CLUSTER_UUID("INVALID_CLUSTER_UUID"),

    /**
     * The input passed is invalid
     * 
     */
    INVALID_INPUT("INVALID_INPUT"),

    /**
     * The user UUID is invalid
     * 
     */
    INVALID_USER_UUID("INVALID_USER_UUID"),

    /**
     * The group passed cannot be null
     * 
     */
    GROUP_CANNOT_BE_NULL("GROUP_CANNOT_BE_NULL"),

    /**
     * No payment card has been defined
     * 
     */
    NO_PAYMENT_CARD_DEFINED("NO_PAYMENT_CARD_DEFINED"),

    /**
     * The payment method ID is invalid
     * 
     */
    INVALID_PAYMENT_METHOD_ID("INVALID_PAYMENT_METHOD_ID"),

    /**
     * The cluster cannot be null
     * 
     */
    CLUSTER_CANNOT_BE_NULL("CLUSTER_CANNOT_BE_NULL"),

    /**
     * The storage unit cannot be null
     * 
     */
    STORAGE_UNIT_CANNOT_BE_NULL("STORAGE_UNIT_CANNOT_BE_NULL"),

    /**
     * No product has been defined
     * 
     */
    NO_PRODUCT_DEFINED("NO_PRODUCT_DEFINED"),

    /**
     * No payment method has been defined
     * 
     */
    PRODUCT_ID_FOR_DIFFERENT_PRODUCT("PRODUCT_ID_FOR_DIFFERENT_PRODUCT"),

    /**
     * No payment method has been defined
     * 
     */
    NO_PAYMENT_METHOD_DEFINED("NO_PAYMENT_METHOD_DEFINED"),

    /**
     * No payment provider has been defined
     * 
     */
    NO_PAYMENT_PROVIDER_DEFINED("NO_PAYMENT_PROVIDER_DEFINED"),

    /**
     * No payment method name has been defined
     * 
     */
    NO_PAYMENT_METHOD_NAME_DEFINED("NO_PAYMENT_METHOD_NAME_DEFINED"),

    /**
     * No payment method UUID has been defined
     * 
     */
    NO_PAYMENT_METHOD_UUID_DEFINED("NO_PAYMENT_METHOD_UUID_DEFINED"),

    /**
     * The payment method UUID is invalid
     * 
     */
    INVALID_PAYMENT_METHOD_UUID("INVALID_PAYMENT_METHOD_UUID"),

    /**
     * The product components are invalid
     * 
     */
    INVALID_PRODUCT_COMPONENTS("INVALID_PRODUCT_COMPONENTS"),

    /**
     * No invoice has been defined
     * 
     */
    NO_INVOICE_DEFINED("NO_INVOICE_DEFINED"),

    /**
     * The invoice number is invalid
     * 
     */
    INVALID_INVOICE_NUMBER("INVALID_INVOICE_NUMBER"),

    /**
     * The invoice source is invalid
     * 
     */
    INVALID_INVOICE_SOURCE("INVALID_INVOICE_SOURCE"),

    /**
     * Invalid invoice payment provider
     * 
     */
    INVALID_INVOICE_PAYMENT_PROVIDER("INVALID_INVOICE_PAYMENT_PROVIDER"),

    /**
     * Invalid invoice payment provider reference
     * 
     */
    INVALID_INVOICE_PAYMENT_PROVIDER_REFERENCE("INVALID_INVOICE_PAYMENT_PROVIDER_REFERENCE"),

    /**
     * No purchase reference has been defined
     * 
     */
    NO_PURCHASE_REFERENCE_DEFINED("NO_PURCHASE_REFERENCE_DEFINED"),

    /**
     * No invoice uuid is defined
     * 
     */
    NO_INVOICE_UUID_DEFINED("NO_INVOICE_UUID_DEFINED"),

    /**
     * No transaction log is defined
     * 
     */
    NO_TRANSACTION_LOG_DEFINED("NO_TRANSACTION_LOG_DEFINED"),

    /**
     * No server has been specified
     * 
     */
    NO_SERVER_SPECIFIED("NO_SERVER_SPECIFIED"),

    /**
     * No aggregation function set
     * 
     */
    NO_AGGREGATION_FUNCTION_DEFINED("NO_AGGREGATION_FUNCTION_DEFINED"),

    /**
     * No aggregation field set
     * 
     */
    NO_AGGREGATION_FIELD_DEFINED("NO_AGGREGATION_FIELD_DEFINED"),

    /**
     * Invalid order by field is defined
     * 
     */
    INVALID_ORDER_BY_FIELD("INVALID_ORDER_BY_FIELD"),

    /**
     * The invoice date is invalid
     * 
     */
    INVALID_INVOICE_DATE("INVALID_INVOICE_DATE"),

    /**
     * No output fields have been defined for the query
     * 
     */
    NO_OUTPUT_FIELDS_DEFINED("NO_OUTPUT_FIELDS_DEFINED"),

    /**
     * No start date or end date defined
     * 
     */
    NO_START_OR_END_DATE_DEFINED("NO_START_OR_END_DATE_DEFINED"),

    /**
     * The condition can not be used in aggregation filter
     * 
     */
    INVALID_AGGREGATION_CONDITION_IN_FILTER("INVALID_AGGREGATION_CONDITION_IN_FILTER"),

    /**
     * No deployment template defined
     * 
     */
    NO_DEPLOYMENT_TEMPLATE_DEFINED("NO_DEPLOYMENT_TEMPLATE_DEFINED"),

    /**
     * No deployment template UUID defined
     * 
     */
    NO_DEPLOYMENT_TEMPLATE_UUID_DEFINED("NO_DEPLOYMENT_TEMPLATE_UUID_DEFINED"),

    /**
     * The deployment instance UUID is invalid
     * 
     */
    INVALID_DEPLOYMENT_INSTANCE_UUID("INVALID_DEPLOYMENT_INSTANCE_UUID"),

    /**
     * No deployment instance defined
     * 
     */
    NO_DEPLOYMENT_INSTANCE_DEFINED("NO_DEPLOYMENT_INSTANCE_DEFINED"),

    /**
     * No network defined
     * 
     */
    NO_NETWORK_DEFINED("NO_NETWORK_DEFINED"),

    /**
     * No currency defined
     * 
     */
    NO_CURRENCY_DEFINED("NO_CURRENCY_DEFINED"),

    /**
     * No currency code defined
     * 
     */
    NO_CURRENCY_CODE_DEFINED("NO_CURRENCY_CODE_DEFINED"),

    /**
     * Length of the currency code string cannot exceed 3
     * 
     */
    CURRENCY_CODE_LENGTH_CANNOT_BE_GREATER_THAN_3("CURRENCY_CODE_LENGTH_CANNOT_BE_GREATER_THAN_3"),

    /**
     * Currency already exists for the given code
     * 
     */
    EXISTING_CURRENCY_CODE("EXISTING_CURRENCY_CODE"),

    /**
     * Invalid name space
     * 
     */
    INVALID_NAMESPACE("INVALID_NAMESPACE"),

    /**
     * Does not support multiple values
     * 
     */
    NO_SUPPORT_FOR_MULTI_VALUES("NO_SUPPORT_FOR_MULTI_VALUES"),

    /**
     * Value set for given field is not valid
     * 
     */
    INVALID_VALUE_SET_FOR_FIELD("INVALID_VALUE_SET_FOR_FIELD"),

    /**
     * Invalid input value
     * 
     */
    INVALID_VALUE_INPUT("INVALID_VALUE_INPUT"),

    /**
     * Error on FDL code while compiling
     * 
     */
    FDL_COMPILE_ERROR("FDL_COMPILE_ERROR"),

    /**
     * Error on FDL when trying to register code to the system
     * 
     */
    FDL_REGISTER_ERROR("FDL_REGISTER_ERROR"),

    /**
     * No size specified for the resource
     * 
     */
    NO_SIZE_SPECIFIED("NO_SIZE_SPECIFIED"),

    /**
     * The URL provided is invalid
     * 
     */
    INVALID_URL("INVALID_URL"),

    /**
     * The provided PCT is invalid
     * 
     */
    INVALID_PCT("INVALID_PCT"),

    /**
     * The PCT is not provided
     * 
     */
    PCT_NOT_DEFINED("PCT_NOT_DEFINED"),

    /**
     * The provided billing method is invalid
     * 
     */
    INVALID_BILLING_METHOD("INVALID_BILLING_METHOD"),

    /**
     * Error signing FDL code
     * 
     */
    FDL_CODE_SIGNING_ERROR("FDL_CODE_SIGNING_ERROR"),

    /**
     * No Product component specified
     * 
     */
    NO_PRODUCT_COMPONENT_SPECIFIED("NO_PRODUCT_COMPONENT_SPECIFIED"),

    /**
     * Required Product components not set
     * 
     */
    REQUIRED_PRODUCT_COMPONENTS_NOT_SET("REQUIRED_PRODUCT_COMPONENTS_NOT_SET"),

    /**
     * Invalid tag name set
     * 
     */
    INVALID_TAG_NAME("INVALID_TAG_NAME"),

    /**
     * No Payment reference defined
     * 
     */
    NO_PAYMENT_REFERENCE_DEFINED("NO_PAYMENT_REFERENCE_DEFINED"),

    /**
     * Billing entities do not match
     * 
     */
    MISMATCHED_BILLING_ENTITIES("MISMATCHED_BILLING_ENTITIES"),

    /**
     * The hash does not match the expected value
     * 
     */
    HASH_INVALID("HASH_INVALID"),

    /**
     * No configured values specified
     * 
     */
    NO_CONFIGURED_VALUES_SPECIFIED("NO_CONFIGURED_VALUES_SPECIFIED"),

    /**
     * No Blob provided
     * 
     */
    NO_BLOB_DEFINED("NO_BLOB_DEFINED"),

    /**
     * No Blob resource uuid provided
     * 
     */
    NO_BLOB_RESOURCE_UUID_DEFINED("NO_BLOB_RESOURCE_UUID_DEFINED"),

    /**
     * No payment method instance defined
     * 
     */
    NO_PAYMENT_METHOD_INSTANCE_DEFINED("NO_PAYMENT_METHOD_INSTANCE_DEFINED"),

    /**
     * No payment method instance name defined
     * 
     */
    NO_PAYMENT_METHOD_INSTANCE_NAME_DEFINED("NO_PAYMENT_METHOD_INSTANCE_NAME_DEFINED");
    private final String value;

    ErrorInvalidInput(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ErrorInvalidInput fromValue(String v) {
        for (ErrorInvalidInput c: ErrorInvalidInput.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
