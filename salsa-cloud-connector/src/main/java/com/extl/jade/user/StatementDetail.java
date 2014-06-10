
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>StatementDetail represent a single credit and/or debit event that occurs to a customer. This includes receiving and sending payments, generating an invoice, and generating a credit note.</p>
 * 
 * <p>Java class for statementDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statementDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="debitAmount" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="creditAmount" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="customerUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="balanceTodate" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="recordType" type="{http://extility.flexiant.net}statementType" minOccurs="0"/>
 *         &lt;element name="lastBalance" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "statementDetail", propOrder = {
    "reference",
    "description",
    "debitAmount",
    "creditAmount",
    "timestamp",
    "customerUUID",
    "balanceTodate",
    "recordType",
    "lastBalance"
})
public class StatementDetail {

    protected String reference;
    protected String description;
    protected double debitAmount;
    protected double creditAmount;
    protected long timestamp;
    protected String customerUUID;
    protected double balanceTodate;
    protected StatementType recordType;
    protected double lastBalance;

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference(String value) {
        this.reference = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the debitAmount property.
     * 
     */
    public double getDebitAmount() {
        return debitAmount;
    }

    /**
     * Sets the value of the debitAmount property.
     * 
     */
    public void setDebitAmount(double value) {
        this.debitAmount = value;
    }

    /**
     * Gets the value of the creditAmount property.
     * 
     */
    public double getCreditAmount() {
        return creditAmount;
    }

    /**
     * Sets the value of the creditAmount property.
     * 
     */
    public void setCreditAmount(double value) {
        this.creditAmount = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     */
    public void setTimestamp(long value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the customerUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerUUID() {
        return customerUUID;
    }

    /**
     * Sets the value of the customerUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerUUID(String value) {
        this.customerUUID = value;
    }

    /**
     * Gets the value of the balanceTodate property.
     * 
     */
    public double getBalanceTodate() {
        return balanceTodate;
    }

    /**
     * Sets the value of the balanceTodate property.
     * 
     */
    public void setBalanceTodate(double value) {
        this.balanceTodate = value;
    }

    /**
     * Gets the value of the recordType property.
     * 
     * @return
     *     possible object is
     *     {@link StatementType }
     *     
     */
    public StatementType getRecordType() {
        return recordType;
    }

    /**
     * Sets the value of the recordType property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatementType }
     *     
     */
    public void setRecordType(StatementType value) {
        this.recordType = value;
    }

    /**
     * Gets the value of the lastBalance property.
     * 
     */
    public double getLastBalance() {
        return lastBalance;
    }

    /**
     * Sets the value of the lastBalance property.
     * 
     */
    public void setLastBalance(double value) {
        this.lastBalance = value;
    }

}
