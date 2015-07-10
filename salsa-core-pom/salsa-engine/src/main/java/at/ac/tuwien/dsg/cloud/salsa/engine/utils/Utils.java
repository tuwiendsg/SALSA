/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

/**
 *
 * @author hungld
 */
public class Utils {
    public static String getInstanceID(String serviceID, String unitID, int instanceID){
        return serviceID+"/"+unitID+"/"+instanceID;
    }
    
    public static String getUnitID(String serviceID, String unitID){
        return serviceID+"/"+unitID;
    }
}
