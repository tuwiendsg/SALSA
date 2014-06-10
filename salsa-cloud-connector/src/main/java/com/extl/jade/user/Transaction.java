
package com.extl.jade.user;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * An instance of a payment transaction against an invoice using a particular payment method.
 * 
 * <p>Java class for transaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transaction">
 *   &lt;complexContent>
 *     &lt;extension base="{http://extility.flexiant.net}customerResource">
 *       &lt;sequence>
 *         &lt;element name="paymentMethodInstanceUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="state" type="{http://extility.flexiant.net}transactionState" minOccurs="0"/>
 *         &lt;element name="invoiceUUIDList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="providerReference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="publicData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="redirectURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="redirectData">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="interactiveURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="allowInteractive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="currencyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="errorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="errorReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paymentAmount" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="paymentMethodInstanceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transactionFunction" type="{http://extility.flexiant.net}paymentFunction" minOccurs="0"/>
 *         &lt;element name="endTimestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="startTimestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="merchantDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="linkedTransaction" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transaction", propOrder = {
    "paymentMethodInstanceUUID",
    "state",
    "invoiceUUIDList",
    "providerReference",
    "publicData",
    "redirectURL",
    "redirectData",
    "interactiveURL",
    "allowInteractive",
    "currencyCode",
    "errorCode",
    "errorReason",
    "paymentAmount",
    "paymentMethodInstanceName",
    "transactionFunction",
    "endTimestamp",
    "startTimestamp",
    "merchantDescription",
    "linkedTransaction"
})
public class Transaction
    extends CustomerResource
{

    protected String paymentMethodInstanceUUID;
    protected TransactionState state;
    @XmlElement(nillable = true)
    protected List<String> invoiceUUIDList;
    protected String providerReference;
    protected String publicData;
    protected String redirectURL;
    @XmlElement(required = true)
    protected Transaction.RedirectData redirectData;
    protected String interactiveURL;
    protected boolean allowInteractive;
    protected String currencyCode;
    protected String errorCode;
    protected String errorReason;
    protected double paymentAmount;
    protected String paymentMethodInstanceName;
    protected PaymentFunction transactionFunction;
    protected long endTimestamp;
    protected long startTimestamp;
    protected String merchantDescription;
    protected String linkedTransaction;

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
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionState }
     *     
     */
    public TransactionState getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionState }
     *     
     */
    public void setState(TransactionState value) {
        this.state = value;
    }

    /**
     * Gets the value of the invoiceUUIDList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the invoiceUUIDList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInvoiceUUIDList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getInvoiceUUIDList() {
        if (invoiceUUIDList == null) {
            invoiceUUIDList = new ArrayList<String>();
        }
        return this.invoiceUUIDList;
    }

    /**
     * Gets the value of the providerReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProviderReference() {
        return providerReference;
    }

    /**
     * Sets the value of the providerReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProviderReference(String value) {
        this.providerReference = value;
    }

    /**
     * Gets the value of the publicData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicData() {
        return publicData;
    }

    /**
     * Sets the value of the publicData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicData(String value) {
        this.publicData = value;
    }

    /**
     * Gets the value of the redirectURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRedirectURL() {
        return redirectURL;
    }

    /**
     * Sets the value of the redirectURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRedirectURL(String value) {
        this.redirectURL = value;
    }

    /**
     * Gets the value of the redirectData property.
     * 
     * @return
     *     possible object is
     *     {@link Transaction.RedirectData }
     *     
     */
    public Transaction.RedirectData getRedirectData() {
        return redirectData;
    }

    /**
     * Sets the value of the redirectData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Transaction.RedirectData }
     *     
     */
    public void setRedirectData(Transaction.RedirectData value) {
        this.redirectData = value;
    }

    /**
     * Gets the value of the interactiveURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInteractiveURL() {
        return interactiveURL;
    }

    /**
     * Sets the value of the interactiveURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInteractiveURL(String value) {
        this.interactiveURL = value;
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
     * Gets the value of the currencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the value of the currencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrencyCode(String value) {
        this.currencyCode = value;
    }

    /**
     * Gets the value of the errorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorCode(String value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the errorReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorReason() {
        return errorReason;
    }

    /**
     * Sets the value of the errorReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorReason(String value) {
        this.errorReason = value;
    }

    /**
     * Gets the value of the paymentAmount property.
     * 
     */
    public double getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * Sets the value of the paymentAmount property.
     * 
     */
    public void setPaymentAmount(double value) {
        this.paymentAmount = value;
    }

    /**
     * Gets the value of the paymentMethodInstanceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentMethodInstanceName() {
        return paymentMethodInstanceName;
    }

    /**
     * Sets the value of the paymentMethodInstanceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentMethodInstanceName(String value) {
        this.paymentMethodInstanceName = value;
    }

    /**
     * Gets the value of the transactionFunction property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentFunction }
     *     
     */
    public PaymentFunction getTransactionFunction() {
        return transactionFunction;
    }

    /**
     * Sets the value of the transactionFunction property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentFunction }
     *     
     */
    public void setTransactionFunction(PaymentFunction value) {
        this.transactionFunction = value;
    }

    /**
     * Gets the value of the endTimestamp property.
     * 
     */
    public long getEndTimestamp() {
        return endTimestamp;
    }

    /**
     * Sets the value of the endTimestamp property.
     * 
     */
    public void setEndTimestamp(long value) {
        this.endTimestamp = value;
    }

    /**
     * Gets the value of the startTimestamp property.
     * 
     */
    public long getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * Sets the value of the startTimestamp property.
     * 
     */
    public void setStartTimestamp(long value) {
        this.startTimestamp = value;
    }

    /**
     * Gets the value of the merchantDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerchantDescription() {
        return merchantDescription;
    }

    /**
     * Sets the value of the merchantDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerchantDescription(String value) {
        this.merchantDescription = value;
    }

    /**
     * Gets the value of the linkedTransaction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkedTransaction() {
        return linkedTransaction;
    }

    /**
     * Sets the value of the linkedTransaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkedTransaction(String value) {
        this.linkedTransaction = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "entry"
    })
    public static class RedirectData {

        protected List<Transaction.RedirectData.Entry> entry;

        /**
         * Gets the value of the entry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEntry().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Transaction.RedirectData.Entry }
         * 
         * 
         */
        public List<Transaction.RedirectData.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<Transaction.RedirectData.Entry>();
            }
            return this.entry;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "key",
            "value"
        })
        public static class Entry {

            protected String key;
            protected String value;

            /**
             * Gets the value of the key property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKey() {
                return key;
            }

            /**
             * Sets the value of the key property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKey(String value) {
                this.key = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

        }

    }

}
