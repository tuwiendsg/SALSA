
package com.extl.jade.user;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>The Cluster class is a virtual resource that represents a cluster.</p><p>Note that a cluster at Jade level and at Tigerlily level are different types</p>
 * 
 * <p>Java class for cluster complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cluster">
 *   &lt;complexContent>
 *     &lt;extension base="{http://extility.flexiant.net}resource">
 *       &lt;sequence>
 *         &lt;element name="systemCapabilities" type="{http://extility.flexiant.net}systemCapabilitySet" minOccurs="0"/>
 *         &lt;element name="hypervisorConfig" type="{http://extility.flexiant.net}hypervisorConfig" minOccurs="0"/>
 *         &lt;element name="hypervisorSettings" type="{http://extility.flexiant.net}hypervisorSetting" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="defaultSettings" type="{http://extility.flexiant.net}hypervisorSetting" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="createDefaultVDC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cluster", propOrder = {
    "systemCapabilities",
    "hypervisorConfig",
    "hypervisorSettings",
    "defaultSettings",
    "createDefaultVDC"
})
public class Cluster
    extends Resource
{

    protected SystemCapabilitySet systemCapabilities;
    protected HypervisorConfig hypervisorConfig;
    @XmlElement(nillable = true)
    protected List<HypervisorSetting> hypervisorSettings;
    @XmlElement(nillable = true)
    protected List<HypervisorSetting> defaultSettings;
    protected Boolean createDefaultVDC;

    /**
     * Gets the value of the systemCapabilities property.
     * 
     * @return
     *     possible object is
     *     {@link SystemCapabilitySet }
     *     
     */
    public SystemCapabilitySet getSystemCapabilities() {
        return systemCapabilities;
    }

    /**
     * Sets the value of the systemCapabilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemCapabilitySet }
     *     
     */
    public void setSystemCapabilities(SystemCapabilitySet value) {
        this.systemCapabilities = value;
    }

    /**
     * Gets the value of the hypervisorConfig property.
     * 
     * @return
     *     possible object is
     *     {@link HypervisorConfig }
     *     
     */
    public HypervisorConfig getHypervisorConfig() {
        return hypervisorConfig;
    }

    /**
     * Sets the value of the hypervisorConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link HypervisorConfig }
     *     
     */
    public void setHypervisorConfig(HypervisorConfig value) {
        this.hypervisorConfig = value;
    }

    /**
     * Gets the value of the hypervisorSettings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hypervisorSettings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHypervisorSettings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HypervisorSetting }
     * 
     * 
     */
    public List<HypervisorSetting> getHypervisorSettings() {
        if (hypervisorSettings == null) {
            hypervisorSettings = new ArrayList<HypervisorSetting>();
        }
        return this.hypervisorSettings;
    }

    /**
     * Gets the value of the defaultSettings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the defaultSettings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDefaultSettings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HypervisorSetting }
     * 
     * 
     */
    public List<HypervisorSetting> getDefaultSettings() {
        if (defaultSettings == null) {
            defaultSettings = new ArrayList<HypervisorSetting>();
        }
        return this.defaultSettings;
    }

    /**
     * Gets the value of the createDefaultVDC property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCreateDefaultVDC() {
        return createDefaultVDC;
    }

    /**
     * Sets the value of the createDefaultVDC property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCreateDefaultVDC(Boolean value) {
        this.createDefaultVDC = value;
    }

}
