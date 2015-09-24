/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

/**
 *
 * @author Duc-Hung LE
 */
public interface CommandHandler {
    public void execute();
    
    public String getCommandDescription();
}
