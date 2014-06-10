
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Input parameters for method payInvoice
 * 
 * <p>Java class for payInvoice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="payInvoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="invoiceUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paymentMethodInstanceUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="allowInteractive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="interactiveInputReturnURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="when" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "payInvoice", propOrder = {
    "invoiceUUID",
    "paymentMethodInstanceUUID",
    "allowInteractive",
    "interactiveInputReturnURL",
    "when"
})
public class PayInvoice {

    protected String invoiceUUID;
    protected String paymentMethodInstanceUUID;
    protected boolean allowInteractive;
    protected String interactiveInputReturnURL;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar when;

    /**
     * Gets the value of the invoiceUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvoiceUUID() {
        return invoiceUUID;
    }

    /**
     * Sets the value of the invoiceUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoiceUUID(String value) {
        this.invoiceUUID = value;
    }

    /**
     * Gets the value of the paymentMethodInstanceUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentMethodInstanceUUID() {
        return paymentMethodInstanceUUID;
    }

    /**
     * Sets the value of the paymentMethodInstanceUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentMethodInstanceUUID(String value) {
        this.paymentMethodInstanceUUID = value;
    }

    /**
     * Gets the value of the allowInteractive property.
     * 
     */
    public boolean isAllowInteractive() {
        return allowInteractive;
    }

    /**
     * Sets the value of the allowInteractive property.
     * 
     */
    public void setAllowInteractive(boolean value) {
        this.allowInteractive = value;
    }

    /**
     * Gets the value of the interactiveInputReturnURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInteractiveInputReturnURL() {
        return interactiveInputReturnURL;
    }

    /**
     * Sets the value of the interactiveInputReturnURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInteractiveInputReturnURL(String value) {
        this.interactiveInputReturnURL = value;
    }

    /**
     * Gets the value of the when property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getWhen() {
        return when;
    }

    /**
     * Sets the value of the when property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setWhen(XMLGregorianCalendar value) {
        this.when = value;
    }

}
