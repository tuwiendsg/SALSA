/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.Neo4jMapper;

import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author hungld
 */
public class StringToGlobalIdentification implements Converter<String, GlobalIdentification> {

    @Override
    public GlobalIdentification convert(String s) {
        return GlobalIdentification.fromJson(s);
    }
    
}
