
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Output result for method listStatementDetail
 * 
 * <p>Java class for listStatementDetailResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="listStatementDetailResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listStatementDetail" type="{http://extility.flexiant.net}listResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listStatementDetailResponse", propOrder = {
    "listStatementDetail"
})
public class ListStatementDetailResponse {

    protected ListResult listStatementDetail;

    /**
     * Gets the value of the listStatementDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ListResult }
     *     
     */
    public ListResult getListStatementDetail() {
        return listStatementDetail;
    }

    /**
     * Sets the value of the listStatementDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListResult }
     *     
     */
    public void setListStatementDetail(ListResult value) {
        this.listStatementDetail = value;
    }

}
