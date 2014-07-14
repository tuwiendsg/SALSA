
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for taxType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="taxType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VAT"/>
 *     &lt;enumeration value="OTHER"/>
 *     &lt;enumeration value="NONE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "taxType")
@XmlEnum
public enum TaxType {


    /**
     * Vat
     * 
     */
    VAT,

    /**
     * Other
     * 
     */
    OTHER,

    /**
     * None
     * 
     */
    NONE;

    public String value() {
        return name();
    }

    public static TaxType fromValue(String v) {
        return valueOf(v);
    }

}
