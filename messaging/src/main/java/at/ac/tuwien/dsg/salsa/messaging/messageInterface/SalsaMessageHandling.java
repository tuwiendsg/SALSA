/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.messaging.messageInterface;

import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage;

/**
 *
 * @author Duc-Hung LE
 */
public interface SalsaMessageHandling {
    public void handleMessage(SalsaMessage salsaMessage);
}
