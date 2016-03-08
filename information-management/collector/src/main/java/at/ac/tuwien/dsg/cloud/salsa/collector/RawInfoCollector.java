/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.collector;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.InfoSourceSettings;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hungld
 */
public interface RawInfoCollector {

    // return a list of <ResourceURI,raw_information>, e.g. a set of <sensorID,sensors> in domain model, or <file_storing_sensor,sensorInfo>
    public Map<String, String> getRawInformation(InfoSourceSettings.InfoSource infoSource);
}
