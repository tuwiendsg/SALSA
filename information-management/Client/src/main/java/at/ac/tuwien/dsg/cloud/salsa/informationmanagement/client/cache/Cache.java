/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * This manage a temporary storage for the information
 *
 * @author hungld
 */
public class Cache {

    public enum CacheInfo {
        sdgateway, router, delise
    }

    CacheInfo cacheInfo;
    String fileName = null;

    public Cache(CacheInfo cacheInfo) {
        this.cacheInfo = cacheInfo;
    }

    protected void writeStringToFile(String data, String fileName) throws IOException {
        Files.write(Paths.get(fileName), data.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public String getFileName() {
        if (fileName != null) {
            return fileName;
        }
        String dir = "/tmp/delise/";
        File fDir = new File(dir);
        if (!fDir.exists()) {
            fDir.mkdirs();
        }
        fileName = dir + this.cacheInfo + ".cache";
        return fileName;
    }

    public CacheInfo getCacheInfo() {
        return cacheInfo;
    }

    public void setCacheInfo(CacheInfo cacheInfo) {
        this.cacheInfo = cacheInfo;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
