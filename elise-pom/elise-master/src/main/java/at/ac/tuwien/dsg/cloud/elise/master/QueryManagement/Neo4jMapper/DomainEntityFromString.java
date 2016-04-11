/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.Neo4jMapper;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author hungld
 */
public class DomainEntityFromString implements Converter<String, DomainEntity>{

    @Override
    public DomainEntity convert(String s) {
        return DomainEntity.fromJson(s);
    }
    
}
