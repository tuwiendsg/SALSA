
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for transactionState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="transactionState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INPROGRESS"/>
 *     &lt;enumeration value="SUCCESS"/>
 *     &lt;enumeration value="FAILURE"/>
 *     &lt;enumeration value="NOT_STARTED"/>
 *     &lt;enumeration value="AWAITINGINTERACTIVEINPUT"/>
 *     &lt;enumeration value="CANCELLED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "transactionState")
@XmlEnum
public enum TransactionState {


    /**
     * In progress
     * 
     */
    INPROGRESS,

    /**
     * Success
     * 
     */
    SUCCESS,

    /**
     * Failure
     * 
     */
    FAILURE,

    /**
     * Waiting
     * 
     */
    NOT_STARTED,

    /**
     * Awaiting interactive input
     * 
     */
    AWAITINGINTERACTIVEINPUT,

    /**
     * Cancelled
     * 
     */
    CANCELLED;

    public String value() {
        return name();
    }

    public static TransactionState fromValue(String v) {
        return valueOf(v);
    }

}
