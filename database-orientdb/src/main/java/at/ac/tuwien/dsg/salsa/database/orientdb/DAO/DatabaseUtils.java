/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.orientdb.DAO;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import static at.ac.tuwien.dsg.salsa.database.orientdb.DAO.AbstractDAO.logger;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;

/**
 *
 * @author hungld
 */
public class DatabaseUtils {

    static Class[] clazzes = {CloudService.class, ServiceTopology.class, ServiceUnit.class, ServiceInstance.class};

    public static void initDB() {
        logger.debug("Initilizing the database... any data will be cleaned!");
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();

        try {

            for (Class clazz : clazzes) {
                String className = clazz.getSimpleName();
                if (!db.getMetadata().getSchema().existsClass(className)) {
                    logger.debug("Class: " + className + " does not existed, now create it...");
                    db.getMetadata().getSchema().createClass(className);

                    db.getMetadata().getSchema().getClass(className).createProperty("uuid", OType.STRING);
                    db.getMetadata().getSchema().getClass(className).createIndex("uuidIndex_" + className, OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "uuid");

                    if (!db.getMetadata().getSchema().existsClass(className)) {
                        logger.debug("Cannot create class: " + className + ", an error of persistent could happen later!");
                    } else {
                        logger.debug("Class " + className + " is created sucessfully !");
                    }
                }
            }

        } finally {
            manager.closeConnection();
        }
        logger.debug("Initilizing the database complete!");
    }
}
