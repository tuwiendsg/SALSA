/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.clientgui.globalmanager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author hungld
 */
public class DeliseMetaModel {

    private final StringProperty uuid;
    private final StringProperty ip;
    private final StringProperty adaptor;
    private final StringProperty sourceType;
    private final StringProperty source;
    private final StringProperty settings;

    public DeliseMetaModel(String uuid, String ip, String adaptor, String sourceType, String source, String settings) {
        this.uuid = new SimpleStringProperty(uuid);
        this.ip = new SimpleStringProperty(ip);
        this.adaptor = new SimpleStringProperty(adaptor);
        this.sourceType = new SimpleStringProperty(sourceType);
        this.source = new SimpleStringProperty(source);
        this.settings = new SimpleStringProperty(settings);
    }

    public StringProperty uuidProperty() {
        return uuid;
    }

    public StringProperty ipProperty() {
        return ip;
    }

    public StringProperty adaptorProperty() {
        return adaptor;
    }

    public StringProperty sourceTypeProperty() {
        return sourceType;
    }

    public StringProperty sourceProperty() {
        return source;
    }

    public StringProperty settingsProperty() {
        return settings;
    }
}
