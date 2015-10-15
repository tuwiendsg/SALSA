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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Duc-Hung LE
 */
@XmlRootElement(name = "CloudService")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class CloudService {
    @XmlAttribute(name = "id")
    String id;
    @XmlElement(name = "SYBLDirective")
    SYBLDirective directive;
    @XmlElement(name="ServiceTopology")
    List<ServiceTopology> topology;

    public CloudService() {
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

    public List<ServiceTopology> getTopology() {
        return topology;
    }

    public void setTopology(List<ServiceTopology> topology) {
        this.topology = topology;
    }
    
    
}
