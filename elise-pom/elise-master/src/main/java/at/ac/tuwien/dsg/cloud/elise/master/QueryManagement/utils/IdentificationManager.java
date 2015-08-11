    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification.IdentificationDB;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification.LocalIdentification;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

/**
 *
 * @author Duc-Hung Le
 */
public class IdentificationManager {

    static Logger logger = EliseConfiguration.logger;
    static ObjectMapper mapper = new ObjectMapper();
    static File storage = new File(EliseConfiguration.IDENTIFICATION_MAPPING_FILE);

    public static IdentificationDB load() {
        if (!storage.exists()) {
            try {
                storage.createNewFile();
            } catch (IOException ex) {
                logger.error("Cannot create identification DB. Error: " + ex);
            }
        } else {
            try {
                return (IdentificationDB) mapper.readValue(storage, IdentificationDB.class);
            } catch (IOException ex) {
                logger.debug("Identification DB is empty, create a new one! Message: " + ex);
            }
        }
        return new IdentificationDB();
    }

    public GlobalIdentification searchAndUpdate(LocalIdentification entityComposedID) {
        // search if there is an exist service identification that "equals" the the provided. Node: equals function is defined
        IdentificationDB currentDB = load();
        GlobalIdentification existGlobal = null;
        for (GlobalIdentification ite : currentDB.getIdentifications()) {
            if (ite.addLocalIdentification(entityComposedID)) {
                existGlobal = ite;
            }
        }

        // update        
        if (existGlobal == null) {  // if there is no existSI in the DB, create one
            System.out.println("There is no exist SI match with Identification, generating one...:" + entityComposedID);
            String uuid = UUID.randomUUID().toString();
            existGlobal = new GlobalIdentification(entityComposedID.getCategory());
            existGlobal.setUuid(uuid);
            existGlobal.addLocalIdentification(entityComposedID); // add again
            currentDB.hasIdentification(existGlobal);
        }        

        // and save to file        
        try {
            mapper.writeValue(storage, currentDB);
        } catch (IOException ex) {
            logger.error("Cannot save Identification. Error: " + ex);
            ex.printStackTrace();            
        }
        return existGlobal;

    }

  //  private synchronized void save(IdentificationDB idb)
//  {
//    IdentificationDB currentDB = load();
//    currentDB.getIdentifications().addAll(idb.getIdentifications());
//    try
//    {
//      mapper.writeValue(storage, currentDB);
//    }
//    catch (IOException ex)
//    {
//      logger.error("Cannot save Identification. Error: " + ex);
//    }
//  }
}
