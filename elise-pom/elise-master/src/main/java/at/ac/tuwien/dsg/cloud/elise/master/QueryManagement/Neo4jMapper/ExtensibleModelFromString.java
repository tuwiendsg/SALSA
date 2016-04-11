/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.Neo4jMapper;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.ExtensibleModel;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author hungld
 */
public class ExtensibleModelFromString implements Converter<String, ExtensibleModel>{

    @Override
    public ExtensibleModel convert(String s) {        
        return (ExtensibleModel)ExtensibleModel.fromJson(s);
    }
    
}
