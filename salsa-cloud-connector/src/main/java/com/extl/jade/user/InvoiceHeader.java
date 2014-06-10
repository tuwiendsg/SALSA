
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for invoiceHeader.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="invoiceHeader">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INVOICE"/>
 *     &lt;enumeration value="CREDITNOTE"/>
 *     &lt;enumeration value="DUEDATE"/>
 *     &lt;enumeration value="INVOICE_NUMBER"/>
 *     &lt;enumeration value="BE_TAX_REF"/>
 *     &lt;enumeration value="INVOICE_DATE"/>
 *     &lt;enumeration value="QUANTITY"/>
 *     &lt;enumeration value="ITEM"/>
 *     &lt;enumeration value="EXCLUDE"/>
 *     &lt;enumeration value="INCLUDE"/>
 *     &lt;enumeration value="TOTAL"/>
 *     &lt;enumeration value="CUSTOMER_TAX_REF"/>
 *     &lt;enumeration value="CREDIT_NOTE_NUMBER"/>
 *     &lt;enumeration value="AMOUNT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "invoiceHeader")
@XmlEnum
public enum InvoiceHeader {


    /**
     * The value to use in place of the term invoice.
     * 
     */
    INVOICE,

    /**
     * The value to use in place of the term credit note.
     * 
     */
    CREDITNOTE,

    /**
     * The value to use in place of the term due date.
     * 
     */
    DUEDATE,

    /**
     * The value to use in place of the term invoice number.
     * 
     */
    INVOICE_NUMBER,

    /**
     * The value to use in place of the term BE tax reference.
     * 
     */
    BE_TAX_REF,

    /**
     * The value to use in place of the term issue date.
     * 
     */
    INVOICE_DATE,

    /**
     * The value to use in place of the term quantity.
     * 
     */
    QUANTITY,

    /**
     * The value to use in place of the term item.
     * 
     */
    ITEM,

    /**
     * The value to use in place of the term exclude.
     * 
     */
    EXCLUDE,

    /**
     * The value to use in place of the term include.
     * 
     */
    INCLUDE,

    /**
     * The value to use in place of the term total.
     * 
     */
    TOTAL,

    /**
     * The value to use in place of the term customer tax reference.
     * 
     */
    CUSTOMER_TAX_REF,

    /**
     * The value to use in place of the term credit note number.
     * 
     */
    CREDIT_NOTE_NUMBER,

    /**
     * The value to use in place of the term amount.
     * 
     */
    AMOUNT;

    public String value() {
        return name();
    }

    public static InvoiceHeader fromValue(String v) {
        return valueOf(v);
    }

}
