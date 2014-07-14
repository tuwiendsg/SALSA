
package com.extl.jade.user;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * An instance of a payment method, mainly used by transactions.
 * 
 * <p>Java class for paymentMethodInstance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paymentMethodInstance">
 *   &lt;complexContent>
 *     &lt;extension base="{http://extility.flexiant.net}customerResource">
 *       &lt;sequence>
 *         &lt;element name="paymentMethodUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isDefault" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="configuredValues" type="{http://extility.flexiant.net}value" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="paymentMethodName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nonInteractivePay" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paymentMethodInstance", propOrder = {
    "paymentMethodUUID",
    "isDefault",
    "configuredValues",
    "paymentMethodName",
    "nonInteractivePay"
})
public class PaymentMethodInstance
    extends CustomerResource
{

    protected String paymentMethodUUID;
    protected boolean isDefault;
    @XmlElement(nillable = true)
    protected List<Value> configuredValues;
    protected String paymentMethodName;
    protected Boolean nonInteractivePay;

    /**
     * Gets the value of the paymentMethodUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentMethodUUID() {
        return paymentMethodUUID;
    }

    /**
     * Sets the value of the paymentMethodUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentMethodUUID(String value) {
        this.paymentMethodUUID = value;
    }

    /**
     * Gets the value of the isDefault property.
     * 
     */
    public boolean isIsDefault() {
        return isDefault;
    }

    /**
     * Sets the value of the isDefault property.
     * 
     */
    public void setIsDefault(boolean value) {
        this.isDefault = value;
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
     * Gets the value of the paymentMethodName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    /**
     * Sets the value of the paymentMethodName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentMethodName(String value) {
        this.paymentMethodName = value;
    }

    /**
     * Gets the value of the nonInteractivePay property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNonInteractivePay() {
        return nonInteractivePay;
    }

    /**
     * Sets the value of the nonInteractivePay property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNonInteractivePay(Boolean value) {
        this.nonInteractivePay = value;
    }

}
