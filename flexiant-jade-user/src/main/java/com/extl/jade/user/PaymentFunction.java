
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paymentFunction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="paymentFunction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UNDEF"/>
 *     &lt;enumeration value="CANCEL_TRANSACTION"/>
 *     &lt;enumeration value="MAKE_PAYMENT"/>
 *     &lt;enumeration value="REFUND_PAYMENT"/>
 *     &lt;enumeration value="REGISTER_PAYMENT_INSTANCE"/>
 *     &lt;enumeration value="REMOVE_PAYMENT_INSTANCE"/>
 *     &lt;enumeration value="REGISTER_PAYMENT_METHOD"/>
 *     &lt;enumeration value="SELF_TEST"/>
 *     &lt;enumeration value="UPDATE_PAYMENT_METHOD"/>
 *     &lt;enumeration value="REFUND_TRANSACTION"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "paymentFunction")
@XmlEnum
public enum PaymentFunction {


    /**
     * Undefined
     * 
     */
    UNDEF,

    /**
     * Cancel transaction function
     * 
     */
    CANCEL_TRANSACTION,

    /**
     * Make payment function
     * 
     */
    MAKE_PAYMENT,

    /**
     * Refund payment function
     * 
     */
    REFUND_PAYMENT,

    /**
     * Register payment instance function
     * 
     */
    REGISTER_PAYMENT_INSTANCE,

    /**
     * Remove payment instance function
     * 
     */
    REMOVE_PAYMENT_INSTANCE,

    /**
     * Register payment function
     * 
     */
    REGISTER_PAYMENT_METHOD,

    /**
     * Test function
     * 
     */
    SELF_TEST,

    /**
     * Update payment function
     * 
     */
    UPDATE_PAYMENT_METHOD,

    /**
     * Refund transaction function
     * 
     */
    REFUND_TRANSACTION;

    public String value() {
        return name();
    }

    public static PaymentFunction fromValue(String v) {
        return valueOf(v);
    }

}
