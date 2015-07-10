/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.tuwien.dsg.quelle.elasticityQuantification.exceptions;

import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MonitoredElement;

/**
 *
 * @author Daniel Moldovan E-Mail: d.moldovan@dsg.tuwien.ac.at
 */
public class ServiceElementNotFoundException extends RuntimeException {
    public ServiceElementNotFoundException() {
    }

    public ServiceElementNotFoundException(String message) {
        super(message);
    }

    public ServiceElementNotFoundException(MonitoredElement element) {
        super("ServiceElement not found: " + element.toString());
    }
}