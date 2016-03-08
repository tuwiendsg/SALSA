/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hungld
 */
public class CacheGateway extends Cache {

    List<SoftwareDefinedGateway> gateways = new ArrayList<>();

    public CacheGateway() {
        super(CacheInfo.sdgateway);
    }

    public static CacheGateway newInstance() {
        return new CacheGateway();
    }

    public List<SoftwareDefinedGateway> loadGatewaysCache() {
        ObjectMapper mapper = new ObjectMapper();
        if (!(new File(getFileName())).exists()) { // file is not existing
            return null;
        }
        try {
            CacheGateway cache = mapper.readValue(new File(getFileName()), CacheGateway.class);
            return cache.gateways;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void writeGatewayCache(List<SoftwareDefinedGateway> gateways) {
        this.gateways = gateways;
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
            writeStringToFile(json, getFileName());
        } catch (JsonProcessingException ex) {
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<SoftwareDefinedGateway> getGateways() {
        return gateways;
    }

    public void setGateways(List<SoftwareDefinedGateway> gateways) {
        this.gateways = gateways;
    }

}
