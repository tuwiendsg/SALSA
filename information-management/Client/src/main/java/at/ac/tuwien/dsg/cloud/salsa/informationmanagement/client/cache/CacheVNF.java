/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungld
 */
public class CacheVNF extends Cache {

    List<VNF> gateways = new ArrayList<>();

    public CacheVNF() {
        super(Cache.CacheInfo.router);
    }

    public static CacheVNF newInstance() {
        return new CacheVNF();
    }

    public List<VNF> loadGatewaysCache() {
        ObjectMapper mapper = new ObjectMapper();
        if (!(new File(getFileName())).exists()) { // file is not existing
            return null;
        }
        try {
            CacheVNF cache = mapper.readValue(new File(getFileName()), CacheVNF.class);
            return cache.gateways;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void writeGatewayCache(List<VNF> gateways) {
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

    public List<VNF> getGateways() {
        return gateways;
    }

    public void setGateways(List<VNF> gateways) {
        this.gateways = gateways;
    }

}
