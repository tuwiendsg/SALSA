/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *               
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.main;

import static at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger.logger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SystemFunctions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Duc-Hung LE
 */
public class CSARParser {

    public static void extractCsar(File csar, final String outputFolder) throws IOException {
        byte[] buffer = new byte[1024];

        // create output directory is not exists
        File folder = new File(outputFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }

        // get the zip file content
        ZipInputStream zis = new ZipInputStream(new FileInputStream(csar));
        // get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {
            //directories are created only for the necessary files
            if (!ze.isDirectory()) {
                String fileName = ze.getName().replace("\\", "/");
                File newFile = new File(outputFolder + File.separator + fileName);
                logger.debug("Extracting file: " + newFile.getAbsoluteFile()); //$NON-NLS-1$
                //this is where dirs are actually created
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            ze = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

    }

    public static void buildCSAR_SystemCmd(String mainFolder, String outputFileName) throws IOException {
        String zipCmd = "zip -r " + outputFileName + " *";
        String output = SystemFunctions.executeCommandGetOutput(zipCmd, mainFolder, SalsaConfiguration.getSalsaCenterEndpoint());
        System.out.println(output);
    }

    public static void buildCSAR(String mainFolder, String outputFileName) throws IOException {
        File directoryToZip = new File(mainFolder);
        List<File> fileList = new ArrayList<>();
        System.out.println("---Getting references to all files in: " + directoryToZip.getCanonicalPath());
        getAllFiles(directoryToZip, fileList);
        System.out.println("---Creating zip file");
        writeZipFile(directoryToZip, fileList, outputFileName);
        System.out.println("---Done");
    }
    
    
    
    // ZIP MANAGEMENT
    public static void getAllFiles(File dir, List<File> fileList) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    System.out.println("directory:" + file.getCanonicalPath());
                    getAllFiles(file, fileList);
                } else {
                    System.out.println("     file:" + file.getCanonicalPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeZipFile(File directoryToZip, List<File> fileList, String outputFile) {

        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            System.out.println("Writing script file to: {}" + outputFile);

            for (File file : fileList) {
                if (!file.isDirectory()) { // we only zip files, not directories
                    addToZip(directoryToZip, file, zos);
                }
            }
            zos.flush();
            fos.flush();
            zos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
            IOException {

        FileInputStream fis = new FileInputStream(file);

		// we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
                file.getCanonicalPath().length());
        System.out.println("Writing '" + zipFilePath + " to zip file");
        ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }

}
