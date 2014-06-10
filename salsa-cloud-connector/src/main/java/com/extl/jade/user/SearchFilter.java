
package com.extl.jade.user;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>The searchFilter class holds the base data for each search filter.</p><p>Search filter objects are used to restrict the data that the methods with names starting 'list' return, based on the data itself. They allow filtering of the objects returned by zero or more filterCondition objects. Each filterCondition object specifies a match criterion against one member of the complex type being searched against.</p><p>For example, if the listCustomers call is used, then customerDetails objects are searched and, if they meet the criteria supplied in the searchFilter supplied, returned. The customerDetails object contains a status field and a billingEntityUUID field. By setting filterConditions for each of these, it is possible to search on both status and billing entity UUID simultaneously.</p><p>Each filterCondition consists of a field identifier, and a condition, and a value. For more details on filterConditions, see the <a href=Complex+Types.AdminAPI#filterCondition>filterCondition</a> complex type.</p><p>Note, that each filterCondition object must refer to a different field; it is not possible to have two filterConditions which refer to the same field. In order to search records where one field falls between a range, use a single filterCondition with the BETWEEN condition selected. There is no restriction on having multiple filterConditions provided each refers to a distinct field. Only records that match all the filter conditions supplied are returned by the relevant list call.</p>
 * 
 * <p>Java class for searchFilter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchFilter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filterConditions" type="{http://extility.flexiant.net}filterCondition" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchFilter", propOrder = {
    "filterConditions"
})
public class SearchFilter {

    @XmlElement(nillable = true)
    protected List<FilterCondition> filterConditions;

    /**
     * Gets the value of the filterConditions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the filterConditions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFilterConditions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FilterCondition }
     * 
     * 
     */
    public List<FilterCondition> getFilterConditions() {
        if (filterConditions == null) {
            filterConditions = new ArrayList<FilterCondition>();
        }
        return this.filterConditions;
    }

}
