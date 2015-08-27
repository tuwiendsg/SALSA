/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.collectorinterfaces;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung Le
 */
public abstract class GenericCollector {

    static Logger logger = LoggerFactory.getLogger(GenericCollector.class);
    String ADAPTOR_FILE = "./adaptor.conf";
    String name;
    String config;
    
    public GenericCollector(){
        logger.debug("Construction the GenericCollector");
        ADAPTOR_FILE = getPathFromLink(getClassContainer(this.getClass())) + "/adaptor.conf";
        ADAPTOR_FILE = ADAPTOR_FILE.replace("jar:file:", "");
    }

    public final String readAdaptorConfig(String key) {
        return readConfigProperty(key, ADAPTOR_FILE);
    }

    private String readConfigProperty(String key, String propertiesFile) {
        Properties prop = new Properties();
        try {
            InputStream input = new FileInputStream(propertiesFile);
            prop.load(input);
            return prop.getProperty(key);
        } catch (FileNotFoundException e) {
            logger.error("Do not found configuration file for adaptor. Error: " + e.getMessage());
        } catch (IOException e1) {
            logger.error("Cannot read configuratin file for adaptor. Error: " + e1.getMessage());
        }
        return null;
    }

    public String readAllAdaptorConfig() {
        try {
            logger.debug("Trying to read config of class: {}. AdaptorFile: {}", this.getClass().getName(), this.ADAPTOR_FILE);            
            return FileUtils.readFileToString(new File(ADAPTOR_FILE));
        } catch (IOException ex) {
            logger.error("Cannot read configuration file", ex);
            return null;
        }
    }

    public abstract String getName();
    
    public static String getPathFromLink(String nativeDir){
        logger.debug("Getting dir from path. nativeDir: {}, path: {}", nativeDir, nativeDir.substring(0, nativeDir.lastIndexOf(File.separator)));        
        return nativeDir.substring(0, nativeDir.lastIndexOf(File.separator));
    }

    public static String getClassContainer(Class c) {
        logger.debug("Get class container of class: {}", c.getSimpleName());
        if (c == null) {
            throw new NullPointerException("The Class passed to this method may not be null");
        }
        try {
            while (c.isMemberClass() || c.isAnonymousClass()) {
                c = c.getEnclosingClass(); //Get the actual enclosing file
            }
            if (c.getProtectionDomain().getCodeSource() == null) {
                //This is a proxy or other dynamically generated class, and has no physical container,
                //so just return null.
                return null;
            }
            String packageRoot;
            try {
                //This is the full path to THIS file, but we need to get the package root.
                String thisClass = c.getResource(c.getSimpleName() + ".class").toString();
                packageRoot = replaceLast(thisClass, Pattern.quote(c.getName().replaceAll("\\.", "/") + ".class"), "");
                if (packageRoot.endsWith("!/")) {
                    packageRoot = replaceLast(packageRoot, "!/", "");
                }
            } catch (Exception e) {
                //Hmm, ok, try this then
                packageRoot = c.getProtectionDomain().getCodeSource().getLocation().toString();
            }
            packageRoot = URLDecoder.decode(packageRoot, "UTF-8");
            logger.debug("Return class container of class: {}", packageRoot);
            return packageRoot;
        } catch (Exception e) {
            throw new RuntimeException("While interrogating " + c.getName() + ", an unexpected exception was thrown.", e);
        }
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

}
