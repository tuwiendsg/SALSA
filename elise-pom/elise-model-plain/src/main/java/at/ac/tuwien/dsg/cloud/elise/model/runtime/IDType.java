/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.runtime;

/**
 * Some type of the Domain ID what are support to enable the collectors to set right identification
 * @author Duc-Hung LE
 */
public enum IDType {    
    // format: serviceID
    SALSA_SERVICE,    
    // format: serviceID/topologyID
    SALSA_TOPOLOGY,    
    // format: serviceID/unitID
    SALSA_UNIT,
    // format: serviceID/unitID/instanceID (Used by salsa, mela and sybl)
    SALSA_INSTANCE,
    // format: IP:PORT (Used by GovOps)
    IP_PORT
}
