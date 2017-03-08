/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.neo4j.mapper;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author hungld
 */
public class HashmapToString implements Converter<HashMap<String, String>, String> {

    String deli1 = "---";
    String deli2 = "+++";

    @Override
    public String convert(HashMap<String, String> s) {
        StringBuffer prop = new StringBuffer();
        for (Map.Entry<String, String> e : s.entrySet()) {
            prop.append(e.getKey()).append(deli1).append(e.getValue()).append(deli2);
        }
        return prop.toString();
    }

}
