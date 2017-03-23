/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.idmanager.GlobalIdentification;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author hungld
 */
@Data
public class ServiceInstance {

    // the ID for database
    Long id;
    // the ID in SALSA overall
    String uuid;
    // the ID under Service Unit scope
    int index;
    // uuid for management
    String serviceUnitUuid;
    String topologyUuid;
    String cloudServiceUuid;

    GlobalIdentification identification; // composition of identification
    ConfigurationState state = ConfigurationState.UNKNOWN;
    int hostedInstanceIndex;

    // custom properties, internal and external properties
    Map<String, String> context;

    public ServiceInstance() {
    }

}
