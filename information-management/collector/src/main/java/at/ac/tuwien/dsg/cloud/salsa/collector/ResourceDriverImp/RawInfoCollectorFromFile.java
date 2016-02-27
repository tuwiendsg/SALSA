/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.collector.ResourceDriverImp;

import at.ac.tuwien.dsg.cloud.salsa.collector.InfoSourceSettings;
import at.ac.tuwien.dsg.cloud.salsa.collector.RawInfoCollector;
import at.ac.tuwien.dsg.cloud.salsa.collector.utils.DeliseConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hungld
 */
public class RawInfoCollectorFromFile implements RawInfoCollector {

    static org.slf4j.Logger logger = DeliseConfiguration.getLogger();
    InfoSourceSettings.InfoSource infoSource;
    
    

    @Override
    public Map<String, String> getRawInformation(InfoSourceSettings.InfoSource infoSource) {
        String mainFolder = infoSource.getEndpoint();
        System.out.println("Checking folder:" + mainFolder);
        // scan and read all file in dir recursively

        List<String> fileNames = new ArrayList<>();
        getFileNames(fileNames, Paths.get(mainFolder));

        final String nameFilter = infoSource.getSettings();
        if (nameFilter != null && !nameFilter.isEmpty()) {
            filterFileNames(fileNames, nameFilter);
        }

        HashMap<String,String> result = new HashMap<>();

        System.out.println("There are " + fileNames.size() + " files in the folder to read !");
        // each file contains info of single sensor/actuator
        for (String filePath : fileNames) {
            System.out.println("Reading file: " + filePath);
            String json;
            try {
                json = new String(Files.readAllBytes(Paths.get(filePath)));
                System.out.println("OK, Loaded the Json file content: \n " + json);
                result.put(filePath, json);
            } catch (IOException ex) {
                ex.printStackTrace();
                logger.error("Cannot read data from file FILE: " + filePath);
            }
        }
        return result;
    }

    static private List<String> getFileNames(List<String> fileNames, Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    getFileNames(fileNames, path);
                } else {
                    fileNames.add(path.toAbsolutePath().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    static private void filterFileNames(List<String> fileNames, String endWith) {
        Iterator<String> ite = fileNames.iterator();
        while (ite.hasNext()) {
            File f = new File(ite.next());
            if (f.isDirectory() || (!f.getName().endsWith(endWith))) {
                ite.remove();
            }
        }
    }

}
