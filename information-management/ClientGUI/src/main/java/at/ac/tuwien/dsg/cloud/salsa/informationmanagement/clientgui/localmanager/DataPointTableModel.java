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
public class DataPointTableModel {            
        private final StringProperty name;
        private final StringProperty resourceID ;
        private final StringProperty description;
        // for data point
        private final StringProperty measurementUnit;
        private final StringProperty rate;
        
        public DataPointTableModel(String name, String resourceID, String description, String measurementUnit, String rate) {
            this.name = new SimpleStringProperty(name);
            this.resourceID = new SimpleStringProperty(resourceID);
            this.description = new SimpleStringProperty(description);
            this.measurementUnit = new SimpleStringProperty(measurementUnit);
            this.rate = new SimpleStringProperty(rate);            
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

        public StringProperty measurementUnitProperty() {
            return measurementUnit;
        }

        public StringProperty rateProperty() {
            return rate;
        }

        
}
