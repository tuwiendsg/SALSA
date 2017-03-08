/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.neo4j.mapper;

import java.util.HashMap;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author hungld
 */
public class HashmapFromString implements Converter<String, HashMap> {

    String deli1 = "---";
    String deli2 = "+++";

    @Override
    public HashMap convert(String s) {
        String[] items = s.split(deli1);
        HashMap<String, String> map = new HashMap<>();
        for (String i : items) {
            if (!i.trim().isEmpty()) {
                String key = i.split(deli2)[0];
                String value = i.split(deli2)[1];
                map.put(key, value);
            }
        }
        return map;
    }

}
