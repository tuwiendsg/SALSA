/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessage;

/**
 *
 * @author hungld
 */
public interface MessagePublishInterface {

    public void pushMessage(SalsaMessage content);

}
