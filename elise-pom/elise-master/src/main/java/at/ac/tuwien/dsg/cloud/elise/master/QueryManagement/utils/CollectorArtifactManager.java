/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;

/**
 *
 * @author Duc-Hung LE
 */
public class CollectorArtifactManager {

    static final String collectorsDir = EliseConfiguration.CURRENT_DIR + "/extensions/";
    static Logger logger = EliseConfiguration.logger;

    public static Map<String, String> getCollectors() {
        final Properties properties = new Properties();
        try {
            File file = new File(EliseConfiguration.CURRENT_DIR + "/collectorList.properties");
            if (file.exists()) {
                logger.debug("Loading collector list in {}", file.getAbsoluteFile());
                properties.load(new FileReader(file));
            } else {
                logger.debug("Loading collector list in resources folder");                
                final InputStream stream = CollectorArtifactManager.class.getResourceAsStream("/data/collectorList.properties");
                properties.load(stream);
                stream.close();
            }

            Map<String, String> map = new HashMap<>();

            // check if the collector artifact file is existing
            for (String key : properties.stringPropertyNames()) {
                String fileName = collectorsDir + properties.getProperty(key);
                if (new File(fileName).exists()) {
                    logger.debug(" - Collector name: {}, collector file {} is existing.", key, fileName);
                    map.put(key, fileName);
                } else {
                    logger.debug(" - Collector name: {}, collector file {} is not found.", key, fileName);
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
