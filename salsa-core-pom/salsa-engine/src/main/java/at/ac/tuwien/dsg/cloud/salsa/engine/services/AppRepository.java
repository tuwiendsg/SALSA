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
package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata.FolderJsonList;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This Service support the customer in storing application description (TOSCA) and application artifacts. It act as a repository of service.
 *
 * @author hungld
 *
 */
@Path("/app")
public class AppRepository {

    static Logger logger = Logger.getLogger("SalsaCenterLogger");

    ;

	
	@GET
    @Path("/getservicetemplatejsonlist")
    @Produces(MediaType.TEXT_PLAIN)
    public String getservicetemplatejsonlist() {
        logger.debug("Get service template list at: " + SalsaConfiguration.getToscaTemplateStorage());
        return listFolderToJson(SalsaConfiguration.getToscaTemplateStorage());
    }

    @GET
    @Path("/getartifactjsonlist")
    @Produces(MediaType.TEXT_PLAIN)
    public String getartifactjsonlist() {

        return listFolderToJson(SalsaConfiguration.getArtifactStorage());
    }

    private String listFolderToJson(String pathName) {
        try {
            FolderJsonList serviceList = new FolderJsonList(pathName);
            Gson json = new GsonBuilder().setPrettyPrinting().create();
            return json.toJson(serviceList);
        } catch (Exception e) {
            logger.error("Could not list the folder: " + pathName);
            return "";
        }
    }

    // save uploaded file to new location
    private static void writeToFile(InputStream uploadedInputStream,
            String uploadedFileLocation) {
        try {
            OutputStream out = new FileOutputStream(new File(
                    uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            EngineLogger.logger.error("Error writing to file: "
                    + uploadedFileLocation);
            EngineLogger.logger.error(e.toString());
        }
    }

}
