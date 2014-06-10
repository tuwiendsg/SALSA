
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for emailType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="emailType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ACCOUNT_APPROVAL"/>
 *     &lt;enumeration value="ACCOUNT_ACTIVATION"/>
 *     &lt;enumeration value="INVITE_USER"/>
 *     &lt;enumeration value="REVOKE_USER"/>
 *     &lt;enumeration value="ACCOUNT_CANCELLATION"/>
 *     &lt;enumeration value="INVOICE"/>
 *     &lt;enumeration value="AUTO_TOP_UP_SUCCESS"/>
 *     &lt;enumeration value="AUTO_TOP_UP_FAIL"/>
 *     &lt;enumeration value="PASSWORD_RESET_LINK"/>
 *     &lt;enumeration value="NEW_PASSWORD_DETAILS"/>
 *     &lt;enumeration value="ZERO_BALANCE"/>
 *     &lt;enumeration value="LOW_BALANCE"/>
 *     &lt;enumeration value="GENERAL_EMAIL"/>
 *     &lt;enumeration value="CREDIT_NOTE"/>
 *     &lt;enumeration value="PAID_INVOICE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "emailType")
@XmlEnum
public enum EmailType {


    /**
     * Sent when an account has been activated manually
     * 
     */
    ACCOUNT_APPROVAL,

    /**
     * Sent when an account has been activated automatically
     * 
     */
    ACCOUNT_ACTIVATION,

    /**
     * Sent when a user is invited to join a customer account
     * 
     */
    INVITE_USER,

    /**
     * Sent when a user's access is revoked from a customer account
     * 
     */
    REVOKE_USER,

    /**
     * Sent when a customer's account is cancelled
     * 
     */
    ACCOUNT_CANCELLATION,

    /**
     * Sent with an attached invoice
     * 
     */
    INVOICE,

    /**
     * Sent to inform the customer of a successful autotopup
     * 
     */
    AUTO_TOP_UP_SUCCESS,

    /**
     * Sent to inform the customer of a failed autotopup
     * 
     */
    AUTO_TOP_UP_FAIL,

    /**
     * Sent to a user who requests a password reset
     * 
     */
    PASSWORD_RESET_LINK,

    /**
     * Sent to a user with a new password after a password reset
     * 
     */
    NEW_PASSWORD_DETAILS,

    /**
     * Sent to a customer who reaches a zero unit balance
     * 
     */
    ZERO_BALANCE,

    /**
     * Sent to a customer whose balance reaches the low balance warning threshold
     * 
     */
    LOW_BALANCE,

    /**
     * The general email template where subject and message is set by customer
     * 
     */
    GENERAL_EMAIL,

    /**
     * Sent with an attached credit note.
     * 
     */
    CREDIT_NOTE,

    /**
     * Email template when an invoice that has been emialed to the customer has been paid.
     * 
     */
    PAID_INVOICE;

    public String value() {
        return name();
    }

    public static EmailType fromValue(String v) {
        return valueOf(v);
    }

}
