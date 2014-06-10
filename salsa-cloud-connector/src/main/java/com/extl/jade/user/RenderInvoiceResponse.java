
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Output result for method renderInvoice
 * 
 * <p>Java class for renderInvoiceResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="renderInvoiceResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="invoicePDF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "renderInvoiceResponse", propOrder = {
    "invoicePDF"
})
public class RenderInvoiceResponse {

    protected String invoicePDF;

    /**
     * Gets the value of the invoicePDF property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvoicePDF() {
        return invoicePDF;
    }

    /**
     * Sets the value of the invoicePDF property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoicePDF(String value) {
        this.invoicePDF = value;
    }

}
