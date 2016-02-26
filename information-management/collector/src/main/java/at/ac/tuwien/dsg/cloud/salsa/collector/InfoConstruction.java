/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.collector;

import at.ac.tuwien.dsg.cloud.salsa.collector.utils.DeliseConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer.GatewayResourceDiscoveryInterface;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;
import org.slf4j.Logger;

/**
 * A collector include a gatherer and a information transformer Information source --> Retriever --> transformer --> VirtualDefinedGateway data model
 *
 * @author hungld
 */
public class InfoConstruction {

    /**
     * The id of the collector used among DELISE
     */
    String uuid;

    /**
     * For general regconization by human
     */
    String ip;

    /**
     * Any other meta data of the environment where collector is deployed e.g. machine name, uname -a, cpu, max ram.
     */
    Map<String, String> meta;

    Logger logger = DeliseConfiguration.getLogger();

    public static SoftwareDefinedGateway getGatewayInfo() throws Exception {
        // load resource file
        System.out.println("Runing the collector...");
        InfoSourceSettings settings = InfoSourceSettings.loadDefaultFile();
        System.out.println("Load resource file done !");

        // collect one time, give the SoftwareDefinedGateway to a file
        SoftwareDefinedGateway gw = new SoftwareDefinedGateway();
        getAllPossibleCapabilities(settings, gw);
        //gw.getCapabilities().addAll(dps);
        System.out.println(gw.toJson());
        return gw;
    }

    public static SoftwareDefinedGateway getAllPossibleCapabilities(InfoSourceSettings settings, SoftwareDefinedGateway gw) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("Executing all the transformer...");
        if (settings == null || settings.getSource().isEmpty()) {
            System.out.println("ERROR: No source information found. Please check configuration file.");
            return null;
        }
        
        for (InfoSourceSettings.InfoSource source : settings.getSource()) {
            System.out.println("Checking resource: " + source.getType());
            switch (source.getType()) {
                case FILE: {
                    String mainFolder = source.getEndpoint();
                    System.out.println("Checking folder:" + mainFolder);
                    // scan and read all file in dir recursively
                    
                    List<String> fileNames = new ArrayList<>();
                    getFileNames(fileNames, Paths.get(mainFolder));
                    
                    final String nameFilter = source.getSettings();
                    if (nameFilter!=null && !nameFilter.isEmpty()){
                        filterFileNames(fileNames, nameFilter);
                    }
                    
                    System.out.println("There are " + fileNames.size() + " files in the folder to read !");
                    // each file contains info of single sensor/actuator
                    for (String filePath : fileNames) {                        
                        System.out.println("Reading file: " + filePath);
                        String json = new String(Files.readAllBytes(Paths.get(filePath)));
                        System.out.println("OK, Loaded the Json file content: \n " + json);
                        // load class and transform                        
                        Class<? extends GatewayResourceDiscoveryInterface> tranformClass = (Class<? extends GatewayResourceDiscoveryInterface>) Class.forName(source.getTransformerClass());
                        System.out.println("Load tranform class done: " + source.getTransformerClass());

                        GatewayResourceDiscoveryInterface t = tranformClass.newInstance();
                        System.out.println("Created tranformer instance done: ");

                        //DataPointTransformerInterface
                        Object domain = t.validateAndConvertToDomainModel(json, filePath);
                        DataPoint dp = t.toDataPoint(domain);
                        List<ControlPoint> cps = t.toControlPoint(domain);                        
                        
                        gw.getCapabilities().add(dp);
                        gw.getCapabilities().addAll(cps);
                        
                    }
                    break;
                }
                case REST: {
                    System.out.println("NOT IMPLEMENT REST SOURCE READER YET !");
                    break;
                }
                default: {
                    System.out.println("Unknown source type");
                    return null;
                }
            }

        }
        System.out.println("Getting information done. Number of datapoint: " + gw.getCapabilities().size());
        return gw;
    }

    //Simply read a file     
    private String readDomainDataFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    // Simply read a REST resource
    private String readDomainDataFromREST(String restEndpoint) throws IOException {
        // TODO: implement this
        return null;
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
    
    

    public static void main(String[] args) {
        List<String> files = new ArrayList<>();
        
        files = getFileNames(files, Paths.get("/home/hungld/workspace/SALSA/information-management/"));
        filterFileNames(files, "sensor.meta");

        System.out.println("YOOOOOOO");
        for (String f : files) {
            System.out.println(f);
        }
    }
}
