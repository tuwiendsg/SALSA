/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.neo4j.mapper;

import at.ac.tuwien.dsg.salsa.model.idmanager.GlobalIdentification;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author hungld
 */
public class GlobalIdentificationToString implements Converter<GlobalIdentification, String> {

    @Override
    public String convert(GlobalIdentification s) {
        return s.toJson();
    }
    
}
