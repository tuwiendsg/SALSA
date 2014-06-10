
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pdfPageSize.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="pdfPageSize">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="A4"/>
 *     &lt;enumeration value="LETTER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "pdfPageSize")
@XmlEnum
public enum PdfPageSize {


    /**
     * A4 page size
     * 
     */
    @XmlEnumValue("A4")
    A_4("A4"),

    /**
     * Letter page size
     * 
     */
    LETTER("LETTER");
    private final String value;

    PdfPageSize(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PdfPageSize fromValue(String v) {
        for (PdfPageSize c: PdfPageSize.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
