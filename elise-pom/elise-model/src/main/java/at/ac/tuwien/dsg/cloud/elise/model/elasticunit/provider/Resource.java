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
 *
 * Compatible with resource/ quality/ cost model
 */
@NodeEntity
public class Resource extends Properties {

    public Resource() {
    }

    public Resource(String name) {
        super(name);

    }
}
