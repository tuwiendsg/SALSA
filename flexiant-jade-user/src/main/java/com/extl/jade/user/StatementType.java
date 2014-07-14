
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for statementType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="statementType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UNPAID_INVOICE"/>
 *     &lt;enumeration value="PAYMENT_RECEIVED"/>
 *     &lt;enumeration value="UNPAID_CREDITNOTE"/>
 *     &lt;enumeration value="PAYMENT_MADE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "statementType")
@XmlEnum
public enum StatementType {


    /**
     * This balance line happen when UNPAID invoice is created
     * 
     */
    UNPAID_INVOICE,

    /**
     * This balance line happen when we have received a payment i.e for an unpaid invoice is paid
     * 
     */
    PAYMENT_RECEIVED,

    /**
     * This balance line happen when an unpaid credit note is created
     * 
     */
    UNPAID_CREDITNOTE,

    /**
     * This balance line happen when credit note is marked as paid
     * 
     */
    PAYMENT_MADE;

    public String value() {
        return name();
    }

    public static StatementType fromValue(String v) {
        return valueOf(v);
    }

}
