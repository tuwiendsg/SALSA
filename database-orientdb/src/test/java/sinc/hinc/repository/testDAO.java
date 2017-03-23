/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.repository;

import at.ac.tuwien.dsg.salsa.database.orientdb.DAO.OrientDBConnector;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author hungld
 */
public class testDAO {

    @Test
    public void dropDB() {
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();
        db.drop();
    }

    @Test
    public void testDBCreate() {
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();

        
        db = manager.getConnection();
        try {
//            db.begin();
            ODocument animal = new ODocument("Animal");
            animal.field("name", "Gaudi");
            animal.field("location", "Madrid");
            animal.save();
//            db.commit();

            System.out.println("NOW READING");
            for (ODocument doc : db.browseClass("Animal")) {
                System.out.println(doc.field("name"));
                assertEquals("Gaudi", doc.field("name"));
                assertEquals(doc.getVersion(), 1);
                System.out.println(doc.toJSON());
            }
        } finally {
            manager.closeConnection();
        }
    }

    @Test
    public void testDBUpdate() {
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();
//        ODatabaseRecordThreadLocal.INSTANCE.set(db);
        // write
        try {
            for (ODocument doc : db.browseClass("Animal")) {
                doc.field("name", "Felix");
                doc.save();
            }
        } finally {
            manager.closeConnection();
        }

        // read
        db = manager.getConnection();
        try {
            for (ODocument doc : db.browseClass("Animal")) {
                assertEquals("Felix", doc.field("name"));
                assertEquals(doc.getVersion(), 2);
            }
        } finally {
            manager.closeConnection();
        }
    }

}
