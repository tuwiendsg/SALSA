
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for billingEntityVAR.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="billingEntityVAR">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FROM_ADDRESS"/>
 *     &lt;enumeration value="CC_ADDRESS"/>
 *     &lt;enumeration value="BCC_ADDRESS"/>
 *     &lt;enumeration value="REPLY_TO"/>
 *     &lt;enumeration value="COMPANY_NAME"/>
 *     &lt;enumeration value="CONTROL_PANEL_URL"/>
 *     &lt;enumeration value="EMAIL_FOOTER"/>
 *     &lt;enumeration value="SUPPORT_URL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "billingEntityVAR")
@XmlEnum
public enum BillingEntityVAR {


    /**
     * The From: address on outbound emails
     * 
     */
    FROM_ADDRESS,

    /**
     * The CC: address on outbound emails
     * 
     */
    CC_ADDRESS,

    /**
     * The BCC: address on outbound emails
     * 
     */
    BCC_ADDRESS,

    /**
     * The Reply-To: address on outbound emails
     * 
     */
    REPLY_TO,

    /**
     * The company name of the Billing Entity
     * 
     */
    COMPANY_NAME,

    /**
     * The URL of the control panel
     * 
     */
    CONTROL_PANEL_URL,

    /**
     * The footer for the email
     * 
     */
    EMAIL_FOOTER,

    /**
     * The URL for technical support for the email
     * 
     */
    SUPPORT_URL;

    public String value() {
        return name();
    }

    public static BillingEntityVAR fromValue(String v) {
        return valueOf(v);
    }

}
