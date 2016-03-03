/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads.DeliseMeta;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Capability;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.CloudConnectivity;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ExecutionEnvironment;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * This manage a temporary storage for the information
 *
 * @author hungld
 * @param <ClassName> The class name to write
 */
public class Cache<ClassName> {

    public enum CacheInfo {
        sdgateway, router, delise
    }

    List<ClassName> cache;
    CacheInfo cacheInfo;

    public Cache() {
    }

    public Cache(CacheInfo cacheInfo) {
        this.cacheInfo = cacheInfo;
    }

    public void writeListOfGateways(List<SoftwareDefinedGateway> data) throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(data);
        writeStringToFile(json, getFileName());
    }

    public void writeListOfRouter(List<VNF> data) throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(data);
        writeStringToFile(json, getFileName());
    }

//    public List<ClassName> loadObjectList() {
//        ObjectMapper mapper = new ObjectMapper();
//        Cache<ClassName> myCache;
//        if (!(new File(getFileName())).exists()) { // file is not existing
//            return null;
//        }
//        try {
//            List<ClassName> list = mapper.readValue(new File(getFileName()), new TypeReference<List<ClassName>>() {
//            });
//            return list;
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//    }
    private void writeStringToFile(String data, String fileName) throws IOException {
        Files.write(Paths.get(fileName), data.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String getFileName() {
        String dir = "/tmp/delise/";
        File fDir = new File(dir);
        if (!fDir.exists()) {
            fDir.mkdirs();
        }
        return dir + this.cacheInfo + ".cache";
    }

    public List<ClassName> getCache() {
        return cache;
    }

    public void setCache(List<ClassName> cache) {
        this.cache = cache;
    }

    public CacheInfo getCacheInfo() {
        return cacheInfo;
    }

    public void setCacheInfo(CacheInfo cacheInfo) {
        this.cacheInfo = cacheInfo;
    }

    // for unknow error, these class is written specific way
    public List<DeliseMeta> loadDelisesCache() {
        ObjectMapper mapper = new ObjectMapper();
        if (!(new File(getFileName())).exists()) { // file is not existing
            return null;
        }
        try {
            return mapper.readValue(new File(getFileName()), new TypeReference<List<DeliseMeta>>() {
            });
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<SoftwareDefinedGateway> loadGatewaysCache() {
        ObjectMapper mapper = new ObjectMapper();
        if (!(new File(getFileName())).exists()) { // file is not existing
            return null;
        }
        try {
            return mapper.readValue(new File(getFileName()), new TypeReference<List<SoftwareDefinedGateway>>() {
            });
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<VNF> loadRoutersCache() {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerSubtypes(VNF.class, Capability.class, CloudConnectivity.class, ControlPoint.class, DataPoint.class, ExecutionEnvironment.class);
        if (!(new File(getFileName())).exists()) { // file is not existing
            return null;
        }
        try {
            return mapper.readValue(new File(getFileName()), new TypeReference<List<VNF>>() {
            });
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
