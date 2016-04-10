/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.Neo4jMapper;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import org.springframework.core.convert.converter.Converter;

/**
 * Guide is here : https://jira.spring.io/browse/DATAGRAPH-242
 * @author hungld
 */
public class DomainEntityToString implements Converter<DomainEntity, String>{

    @Override
    public String convert(DomainEntity s) {
        return s.toJson();
    }
    
}
