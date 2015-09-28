/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.exec;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung LE
 */
public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    private Util() {}

    /**
     * Retrieve the directory located at the given path. Checks that path indeed is a reabable
     * directory. If this does not exist, create it (and log having done so).
     * 
     * @param path directory(ies, can include parent directories) names, as forward slash ('/')
     *            separated String
     * @return safe File object representing that path name
     * @throws IllegalArgumentException If it is not a directory, or it is not readable
     */
    public static File getDirectory(String path) {
        boolean log = false;
        File dir = new File(path);
        if (!dir.exists()) {
            log = true;
            try {
                FileUtils.forceMkdir(dir);
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to create new directory at path: "
                        + path, e);
            }
        }
        String absPath = dir.getAbsolutePath();
        if (absPath.trim().length() == 0) {
            throw new IllegalArgumentException(path + " is empty");
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(path + " is not a directory");
        }
        if (!dir.canRead()) {
            throw new IllegalArgumentException(path + " is not a readable directory");
        }
        if (log) {
            logger.info("Created directory: " + absPath);
        }
        return dir;
    }

    /**
     * Check for temporary directory name.
     * 
     * @param directory directory name
     * @return true if the passed directory name starts with the system temporary directory name.
     */
    public static boolean isTemporaryDirectory(String directory) {
        return directory.startsWith(SystemUtils.JAVA_IO_TMPDIR);
    }

    public static void forceExecutable(File executableFile) throws IOException {
        if (executableFile.exists() && !executableFile.canExecute()) {
            boolean succeeded = executableFile.setExecutable(true);
            if (succeeded) {
                logger.info("chmod +x " + executableFile.toString()
                        + " (using java.io.File.setExecutable)");
            } else {
                throw new IOException("Failed to do chmod +x " + executableFile.toString()
                        + " using java.io.File.setExecutable, which will be a problem on *NIX...");
            }
        }
    }

    
}
