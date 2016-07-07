/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.Neo4jMapper;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.VirtualMachineInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author hungld
 */
public class VirtualMachineInfoToString implements Converter<VirtualMachineInfo, String> {

    @Override
    public String convert(VirtualMachineInfo s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(s);
        } catch (IOException ex) {
            return null;
        }
    }
    
}
