/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads.DeliseMeta;
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
public class CacheDelises extends Cache {

    List<DeliseMeta> gateways = new ArrayList<>();

    public static CacheDelises newInstance() {
        return new CacheDelises();
    }

    public CacheDelises() {
        super(Cache.CacheInfo.delise);
    }

    public List<DeliseMeta> loadDelisesCache() {
        ObjectMapper mapper = new ObjectMapper();
        if (!(new File(getFileName())).exists()) { // file is not existing
            return null;
        }
        try {

            CacheDelises cache = mapper.readValue(new File(getFileName()), CacheDelises.class);
            return cache.gateways;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void writeDeliseCache(List<DeliseMeta> gateways) {
        this.gateways.addAll(gateways);
        System.out.println("writng delise list: " + this.gateways.size() + " items");
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);

            System.out.println("Json: " + json);

            writeStringToFile(json, getFileName());
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<DeliseMeta> getGateways() {
        return gateways;
    }

    public void setGateways(List<DeliseMeta> gateways) {
        this.gateways = gateways;
    }

}
