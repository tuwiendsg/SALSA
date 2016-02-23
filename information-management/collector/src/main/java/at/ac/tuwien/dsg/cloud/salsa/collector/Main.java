/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.collector;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer.DataPointTransformerInterface;
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

/**
 * A collector include a gatherer and a information transformer Information source --> Retriever --> transformer --> VirtualDefinedGateway data model
 *
 * @author hungld
 */
public class Main {

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

    public static void main(String[] args) throws Exception {
        // load resource file
        System.out.println("Runing the collector...");
        InfoSourceSettings settings = InfoSourceSettings.loadDefaultFile();
        System.out.println("Load resource file done !");
        if (args.length > 0 && args[0].equals("dry")) {
            // collect one time, give the SoftwareDefinedGateway to a file
            SoftwareDefinedGateway gw = new SoftwareDefinedGateway();
            List<DataPoint> dps = runAllDataPointTransformer(settings);
            gw.getCapabilities().addAll(dps);
            System.out.println(gw.toJson());
        }
    }

    public static List<DataPoint> runAllDataPointTransformer(InfoSourceSettings settings) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("Executing all the transformer...");
        if (settings == null || settings.getSource().isEmpty()) {
            System.out.println("ERROR: No source information found. Please check configuration file.");
            return null;
        }
        List<DataPoint> dps = new ArrayList<>();
        for (InfoSourceSettings.InfoSource source : settings.getSource()) {
            System.out.println("Checking resource: " + source.getType());
            switch (source.getType()) {
                case FILE: {
                    String mainFolder = source.getEndpoint();
                    System.out.println("Checking folder:" + mainFolder);
                    // scan and read all file in dir
                    File file = new File(mainFolder);
                    String[] jsonFiles = file.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return true;
                            //return name.endsWith(".json");
                        }
                    });
                    System.out.println("There are " + jsonFiles.length + " files in the folder to read !");
                    for (String fileName : jsonFiles) {
                        String filePath = mainFolder + "/" + fileName;
                        System.out.println("Reading file: " + filePath);
                        String json = new String(Files.readAllBytes(Paths.get(filePath)));
                        System.out.println("OK, Loaded the Json file content: \n " + json);
                        // load class and transform                        
                        Class<? extends DataPointTransformerInterface> tranformClass = (Class<? extends DataPointTransformerInterface>) Class.forName(source.getTransformerClass());
                        System.out.println("Load tranform class done: " + source.getTransformerClass());

                        DataPointTransformerInterface t = tranformClass.newInstance();
                        System.out.println("Created tranformer instance done: ");

                        //DataPointTransformerInterface
                        Object domain = t.toDomainInfo(json);
                        DataPoint dp = t.toDataPoint(domain);
                        System.out.println("Tranformation done:" + dp.getName());
                        dps.add(dp);
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
        System.out.println("Getting information done. Number of datapoint: " + dps.size());
        return dps;
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
}
