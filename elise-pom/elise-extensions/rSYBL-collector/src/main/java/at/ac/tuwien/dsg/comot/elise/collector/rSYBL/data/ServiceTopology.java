/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.comot.elise.collector.rSYBL.data;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Duc-Hung LE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class ServiceTopology {
    @XmlAttribute
    String id;
    @XmlElement(name = "SYBLDirective")
    SYBLDirective directive;
    @XmlElement(name = "ServiceUnit")
    List<ServiceUnit> serviceunits;

    public ServiceTopology() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SYBLDirective getDirective() {
        return directive;
    }

    public void setDirective(SYBLDirective directive) {
        this.directive = directive;
    }

    public List<ServiceUnit> getServiceunits() {
        return serviceunits;
    }

    public void setServiceunits(List<ServiceUnit> serviceunits) {
        this.serviceunits = serviceunits;
    }
    
    
}
