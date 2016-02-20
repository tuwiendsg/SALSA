/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.examples;

import at.ac.tuwien.dsg.cloud.elise.model.runtime.State;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;

/**
 *
 * @author hungld
 */
public class TestExamples {
    public static void main(String[] args){
        UnitInstance sensor = new UnitInstance("GPSSensor", ServiceCategory.Sensor);
//        sensor.setCapabilities(capabilities);
//        sensor.setDomainInfo(domainInfo);
//        sensor.setExtendedInfo(extendedInfo);
//        sensor.setExtra(extra);
//        sensor.setIdentification(identification);
//        sensor.setState(State.ERROR);
//        sensor.setUnitType(unitType);
    }
}
