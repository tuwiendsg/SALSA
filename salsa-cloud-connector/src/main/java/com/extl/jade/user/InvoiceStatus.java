
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for invoiceStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="invoiceStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PENDING"/>
 *     &lt;enumeration value="VOID"/>
 *     &lt;enumeration value="UNPAID"/>
 *     &lt;enumeration value="PAID"/>
 *     &lt;enumeration value="LOCKED"/>
 *     &lt;enumeration value="CLOSED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "invoiceStatus")
@XmlEnum
public enum InvoiceStatus {


    /**
     * Pending (i.e. under construction)
     * 
     */
    PENDING,

    /**
     * Void (i.e. the invoice was never finalised because its creation was cancelled)
     * 
     */
    VOID,

    /**
     * Valid, finalised but unpaid
     * 
     */
    UNPAID,

    /**
     * Valid, finalised and paid
     * 
     */
    PAID,

    /**
     * Locked,for processing
     * 
     */
    LOCKED,

    /**
     * Closed,after processing
     * 
     */
    CLOSED;

    public String value() {
        return name();
    }

    public static InvoiceStatus fromValue(String v) {
        return valueOf(v);
    }

}
