/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.comot.elise.collector.rSYBL.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Duc-Hung LE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class SYBLDirective {
    @XmlAttribute
    String Constraints;
    @XmlAttribute
    String Monitoring;
    @XmlAttribute
    String Priorities;
    @XmlAttribute
    String Strategies;
    @XmlAttribute
    String Notifications;

    public SYBLDirective() {
    }

    public String getConstraints() {
        return Constraints;
    }

    public void setConstraints(String Constraints) {
        this.Constraints = Constraints;
    }

    public String getMonitoring() {
        return Monitoring;
    }

    public void setMonitoring(String Monitoring) {
        this.Monitoring = Monitoring;
    }

    public String getPriorities() {
        return Priorities;
    }

    public void setPriorities(String Priorities) {
        this.Priorities = Priorities;
    }

    public String getStrategies() {
        return Strategies;
    }

    public void setStrategies(String Strategies) {
        this.Strategies = Strategies;
    }

    public String getNotifications() {
        return Notifications;
    }

    public void setNotifications(String Notifications) {
        this.Notifications = Notifications;
    }

}
