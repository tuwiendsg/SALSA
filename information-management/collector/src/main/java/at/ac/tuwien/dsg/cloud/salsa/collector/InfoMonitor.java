/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.collector;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads.UpdateGatewayStatus;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author hungld
 */
public class InfoMonitor {

    

    // it is static, so it can be changed in general scope
    static long monitorRate = 5; // seconds
    static double simulatedChangeRatio = 0.1; // seconds

    public InfoMonitor() {

    }

    public static long getMonitorRate() {
        return monitorRate;
    }

    public static void setMonitorRate(long monitorRate) {
        InfoMonitor.monitorRate = monitorRate;
    }

    // ratio should be between 0 and 1, e.g. ratio=0.2 means 10% of sensor is disapear/ 10% appear
    // this create an UpdateGatewayStatus message which include some appear and some disappear capability
    public UpdateGatewayStatus getSimulatedUpdate() {

        try {
            SoftwareDefinedGateway gw = InfoCollector.getGatewayInfo();
            UpdateGatewayStatus updateCapa = new UpdateGatewayStatus();
            int fullSize = gw.getCapabilities().size();
            double numberOfChange = ((double) fullSize) * simulatedChangeRatio / 2;
            for (int i = 0; i <= numberOfChange; i++) {
                if (i < gw.getCapabilities().size()) {
                    updateCapa.getAppear().add(gw.getCapabilities().get(i));
                    updateCapa.getDisappear().add(gw.getCapabilities().get(i));
                }
            }
            updateCapa.setTimeStamp((new Date()).getTime());
            return updateCapa;
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
