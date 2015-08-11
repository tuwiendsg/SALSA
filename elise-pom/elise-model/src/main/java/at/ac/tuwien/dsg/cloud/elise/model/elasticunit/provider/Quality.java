/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.provider;

import org.springframework.data.neo4j.annotation.NodeEntity;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic.Properties;

/**
 *
 * @author hungld
 */
@NodeEntity
public class Quality extends Properties {

    public Quality() {
    }

    public Quality(String name) {
        super(name);        
    }

}
