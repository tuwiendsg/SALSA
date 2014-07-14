
package com.extl.jade.user;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>The PaymentMethod class represents a Payment Method.</p>
 * 
 * <p>Java class for paymentMethod complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paymentMethod">
 *   &lt;complexContent>
 *     &lt;extension base="{http://extility.flexiant.net}resource">
 *       &lt;sequence>
 *         &lt;element name="paymentProviderUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="configuredValues" type="{http://extility.flexiant.net}value" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="paymentProviderName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="instanceValues" type="{http://extility.flexiant.net}value" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="consolidateInvoices" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="configured" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="fdlPaymentRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paymentMethod", propOrder = {
    "paymentProviderUUID",
    "configuredValues",
    "paymentProviderName",
    "instanceValues",
    "consolidateInvoices",
    "configured",
    "fdlPaymentRef"
})
public class PaymentMethod
    extends Resource
{

    protected String paymentProviderUUID;
    @XmlElement(nillable = true)
    protected List<Value> configuredValues;
    protected String paymentProviderName;
    @XmlElement(nillable = true)
    protected List<Value> instanceValues;
    protected boolean consolidateInvoices;
    protected Boolean configured;
    protected String fdlPaymentRef;

    /**
     * Gets the value of the paymentProviderUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentProviderUUID() {
        return paymentProviderUUID;
    }

    /**
     * Sets the value of the paymentProviderUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentProviderUUID(String value) {
        this.paymentProviderUUID = value;
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
     * Gets the value of the paymentProviderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentProviderName() {
        return paymentProviderName;
    }

    /**
     * Sets the value of the paymentProviderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentProviderName(String value) {
        this.paymentProviderName = value;
    }

    /**
     * Gets the value of the instanceValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the instanceValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInstanceValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Value }
     * 
     * 
     */
    public List<Value> getInstanceValues() {
        if (instanceValues == null) {
            instanceValues = new ArrayList<Value>();
        }
        return this.instanceValues;
    }

    /**
     * Gets the value of the consolidateInvoices property.
     * 
     */
    public boolean isConsolidateInvoices() {
        return consolidateInvoices;
    }

    /**
     * Sets the value of the consolidateInvoices property.
     * 
     */
    public void setConsolidateInvoices(boolean value) {
        this.consolidateInvoices = value;
    }

    /**
     * Gets the value of the configured property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isConfigured() {
        return configured;
    }

    /**
     * Sets the value of the configured property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setConfigured(Boolean value) {
        this.configured = value;
    }

    /**
     * Gets the value of the fdlPaymentRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFdlPaymentRef() {
        return fdlPaymentRef;
    }

    /**
     * Sets the value of the fdlPaymentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFdlPaymentRef(String value) {
        this.fdlPaymentRef = value;
    }

}
