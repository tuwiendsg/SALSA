/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.Neo4jMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author hungld
 */
public class DateFromString implements Converter<String, Date> {

    @Override
    public Date convert(String s) {
        Date dtReturn = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
        try {
            dtReturn = simpleDateFormat.parse(s);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return dtReturn;
    }

}
