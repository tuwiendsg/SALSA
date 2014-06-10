
package com.extl.jade.user;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Input parameters for method createPaymentMethodInstance
 * 
 * <p>Java class for createPaymentMethodInstance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createPaymentMethodInstance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="paymentMethodInstance" type="{http://extility.flexiant.net}paymentMethodInstance" minOccurs="0"/>
 *         &lt;element name="configuredValues" type="{http://extility.flexiant.net}value" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "createPaymentMethodInstance", propOrder = {
    "paymentMethodInstance",
    "configuredValues",
    "allowInteractive",
    "interactiveInputReturnURL",
    "when"
})
public class CreatePaymentMethodInstance {

    protected PaymentMethodInstance paymentMethodInstance;
    protected List<Value> configuredValues;
    protected boolean allowInteractive;
    protected String interactiveInputReturnURL;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar when;

    /**
     * Gets the value of the paymentMethodInstance property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentMethodInstance }
     *     
     */
    public PaymentMethodInstance getPaymentMethodInstance() {
        return paymentMethodInstance;
    }

    /**
     * Sets the value of the paymentMethodInstance property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentMethodInstance }
     *     
     */
    public void setPaymentMethodInstance(PaymentMethodInstance value) {
        this.paymentMethodInstance = value;
    }

    /**
     * Gets the value of the configuredValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the configuredValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfiguredValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Value }
     * 
     * 
     */
    public List<Value> getConfiguredValues() {
        if (configuredValues == null) {
            configuredValues = new ArrayList<Value>();
        }
        return this.configuredValues;
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
