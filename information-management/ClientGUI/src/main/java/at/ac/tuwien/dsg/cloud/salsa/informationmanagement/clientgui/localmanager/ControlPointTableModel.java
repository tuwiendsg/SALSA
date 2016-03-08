/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.clientgui.localmanager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author hungld
 */
public class ControlPointTableModel {

    private final StringProperty name;
    private final StringProperty resourceID;
    private final StringProperty description;
    // for data point
    private final StringProperty invokeProtocol;
    private final StringProperty reference;

    public ControlPointTableModel(String name, String resourceID, String description, String invokeProtocol, String reference) {
        this.name = new SimpleStringProperty(name);
        this.resourceID = new SimpleStringProperty(resourceID);
        this.description = new SimpleStringProperty(description);
        this.invokeProtocol = new SimpleStringProperty(invokeProtocol);
        this.reference = new SimpleStringProperty(reference);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty resourceIDProperty() {
        return resourceID;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty invokeProtocolProperty() {
        return invokeProtocol;
    }

    public StringProperty referenceProperty() {
        return reference;
    }

}
