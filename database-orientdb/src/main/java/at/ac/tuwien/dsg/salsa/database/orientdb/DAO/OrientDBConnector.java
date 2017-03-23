/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.orientdb.DAO;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OStorageException;

/**
 * This version use only Document database. In the future can take graphDB
 *
 * @author hungld
 */
public class OrientDBConnector {

    // a singleton instance holder
    protected static OrientDBConnector INSTANCE;

    // TODO: move to configuration file
    final String dbPath = "./salsa.db";
    final String username = "admin";
    final String password = "admin";
    ODatabaseDocumentTx connection;

    public synchronized static OrientDBConnector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrientDBConnector();
        }
        return INSTANCE;
    }
    
    
    public ODatabaseDocumentTx getConnection() {
        if (this.connection == null) {
            MyCustomRecordFactory factory = new MyCustomRecordFactory("plocal:" + dbPath, username, password);
            // try to get, if null create a new database
            try {
                connection = (ODatabaseDocumentTx) factory.getThreadDatabase();
            } catch (OStorageException e) {
                try {
//                    e.printStackTrace();
                    System.out.println("DB is not opened, trying to open it....");
                    connection = new ODatabaseDocumentTx("plocal:" + dbPath).open(username, password);
                } catch (Exception e1) {
//                    e1.printStackTrace();
                    System.out.println("DB is not create, creating a new one ....");
                    connection = new ODatabaseDocumentTx("plocal:" + dbPath).create();                    
                }
            }

        }
        return this.connection;
    }

    public void closeConnection() {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }
    }

    public class MyCustomRecordFactory implements ODatabaseThreadLocalFactory {

        private OPartitionedDatabasePool pool;

        public MyCustomRecordFactory(String url, String user, String password) {
            pool = new OPartitionedDatabasePool(url, user, password);
        }

        @Override
        public ODatabaseDocumentInternal getThreadDatabase() {
            return pool.acquire();
        }
    }

    public static void commit() {
        INSTANCE.getConnection().commit();
        INSTANCE.closeConnection();
    }
    
    
}
