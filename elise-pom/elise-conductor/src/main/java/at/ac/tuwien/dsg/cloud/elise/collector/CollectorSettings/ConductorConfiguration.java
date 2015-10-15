package at.ac.tuwien.dsg.cloud.elise.collector.CollectorSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConductorConfiguration {

    public static Logger logger = LoggerFactory.getLogger(ConductorConfiguration.class);
    public static final String CURRENT_DIR = System.getProperty("user.dir");
    public static final String COLLECTOR_ADAPTOR_CONFIG_FILE = CURRENT_DIR + "/adaptor.conf";
    public static final String ELISE_CONFIGURATION_FILE = CURRENT_DIR + "/salsa.engine.properties";    
    private static String conductorID = null;

    public static String getJarDir() {
        try {
            CodeSource codeSource = ConductorConfiguration.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            return jarFile.getParentFile().getPath();
        } catch (URISyntaxException ex) {
            java.util.logging.Logger.getLogger(ConductorConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static String getExtensionFolder() {
        return getJarDir()+"/extensions";
    }
    
    public static String getCollectorFolder(String collectorName){
        return getExtensionFolder() +"/" + collectorName;
    }

    /*** GET parameters in the configuration file ***/    

    public static String getELISE_IP() {
        return getGenericParameter("SALSA_CENTER_IP", "localhost");
    }

    public static String getELISE_port() {
        return getGenericParameter("SALSA_CENTER_PORT", "8080");
    }

    public static String getBroker() {
        return getGenericParameter("BROKER", "tcp://iot.eclipse.org:1883");
    }

    public static String getBrokerType() {
        return getGenericParameter("BROKER_TYPE", "mqtt");
    }

    /*** GET parameters that combine of different ones ***/
    
    public static String getConductorID() {
        if (conductorID == null) {
            conductorID = UUID.randomUUID().toString();
        }
        return conductorID;
    }

    public static String getELISE_REST_ENDPOINT() {
        return "http://" + getELISE_IP() + ":" + getELISE_port() + "/elise-service/rest";
    }

    public static String getELISE_REST_ENDPOINT_LOCAL() {
        return "http://localhost:" + getELISE_port() + "/elise-service/rest";
    }

    public static String getGenericParameter(String key, String theDefault) {
        Properties prop = new Properties();
        InputStream input;
        File myFile = new File(ELISE_CONFIGURATION_FILE);

        try {
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            input = new FileInputStream(ELISE_CONFIGURATION_FILE);
            // load a properties file
            prop.load(input);
            String param = prop.getProperty(key);
            if (param != null) {   // just return default MQTT
                return param;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return theDefault;
    }
}
