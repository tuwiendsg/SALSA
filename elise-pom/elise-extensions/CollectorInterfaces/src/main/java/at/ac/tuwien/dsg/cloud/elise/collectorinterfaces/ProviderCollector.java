/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.collectorinterfaces;

import at.ac.tuwien.dsg.cloud.elise.model.provider.Provider;

/**
 *
 * @author Duc-Hung Le
 */
public interface ProviderCollector {

    public abstract Provider collect();
}
