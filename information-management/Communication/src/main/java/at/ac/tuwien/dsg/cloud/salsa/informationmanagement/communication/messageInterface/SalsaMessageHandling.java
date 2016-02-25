/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessage;



/**
 *
 * @author Duc-Hung LE
 */
public interface SalsaMessageHandling {
    
    // handling incoming message, and reply another
    public void handleMessage(DeliseMessage message);
}
