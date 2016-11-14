/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.neo4j.mapper;

import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author hungld
 */
public class ClassFromString  implements Converter<String, Class> {

    @Override
    public Class convert(String s) {
        try {
            return Class.forName(s);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
    
}
