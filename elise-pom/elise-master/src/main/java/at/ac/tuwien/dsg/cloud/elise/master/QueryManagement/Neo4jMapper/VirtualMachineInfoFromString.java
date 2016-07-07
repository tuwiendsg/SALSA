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
public class VirtualMachineInfoFromString implements Converter<String, VirtualMachineInfo> {

    @Override
    public VirtualMachineInfo convert(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, VirtualMachineInfo.class);
        } catch (IOException ex) {
            return null;
        }
    }

}
