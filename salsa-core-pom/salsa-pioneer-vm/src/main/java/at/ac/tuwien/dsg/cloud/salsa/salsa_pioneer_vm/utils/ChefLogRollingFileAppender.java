package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.RollingFileAppender;

public class ChefLogRollingFileAppender extends RollingFileAppender {

    @Override
    public synchronized void setFile(String fileName, boolean append,
		boolean bufferedIO, int bufferSize) throws IOException {
        File logFile = new File(fileName);
        if(logFile.createNewFile()){
        	System.out.println("Created new "+fileName+" file.");
        }else{
        	System.out.println("Reusing existing "+fileName+" file.");
        }
    	
        if(!logFile.canWrite()){
        	throw new IOException("Can not write to "+logFile.getAbsolutePath());
        }
        super.setFile(fileName, append, bufferedIO, bufferSize);
    }


}
