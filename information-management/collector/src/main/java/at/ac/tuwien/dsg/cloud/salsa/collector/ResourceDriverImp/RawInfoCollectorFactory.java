/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.collector.ResourceDriverImp;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.InfoSourceSettings;
import at.ac.tuwien.dsg.cloud.salsa.collector.RawInfoCollector;

/**
 *
 * @author hungld
 */
public class RawInfoCollectorFactory {

    public static RawInfoCollector getCollector(InfoSourceSettings.InformationSourceType type) {
        switch (type) {
            case FILE: {
                return new RawInfoCollectorFromFile();
            }
            case REST: {
                return new RawInfoCollectorFromREST();
            }
            case SYSCMD:{
                return new RawInfoCollectorFromSyscmd();
            }
            default: {
                return null;
            }
        }
    }
}
