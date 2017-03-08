/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services.GUI;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author hungld
 */
public class ViewTreeNode implements Serializable, Comparable<ViewTreeNode> {

    String name;
    String type;
    String status;
    String hostedOn;

    public ViewTreeNode(String name, String type, String status, String hostedOn) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.hostedOn = hostedOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHostedOn() {
        return hostedOn;
    }

    public void setHostedOn(String hostedOn) {
        this.hostedOn = hostedOn;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.type);
        hash = 67 * hash + Objects.hashCode(this.status);
        hash = 67 * hash + Objects.hashCode(this.hostedOn);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ViewTreeNode other = (ViewTreeNode) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.status, other.status)) {
            return false;
        }
        if (!Objects.equals(this.hostedOn, other.hostedOn)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ViewTreeNode o) {
        return this.getName().compareTo(o.getName());
    }

}
