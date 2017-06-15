/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.idmanager.GlobalIdentification;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;
import java.util.HashMap;
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

    String pioneer = "";

    // custom properties, internal and external properties
    Map<String, String> context;

    public ServiceInstance() {
    }

    public void updateState(SalsaConfigureResult confResult) {
        this.state = confResult.getState();
        if (this.context == null) {
            this.context = new HashMap<>();
        }
        this.context.putAll(confResult.getEffects());
    }

}
