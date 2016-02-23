/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.model.PhysicalResource;

/**
 *
 * @author hungld
 */
public enum ResourceType {
    // sensor    
    Accelerometer,        
    GPS,
    Temperature,
    Humidity,
    Presure,
    Light,
    Magnetic_field,
    Step_counter,
    Significant_motion,
    
            
    // actuator
    Button,
    Led,
    
    // gateway device
    Raspberry_Pi,
    ODROID,    
}
