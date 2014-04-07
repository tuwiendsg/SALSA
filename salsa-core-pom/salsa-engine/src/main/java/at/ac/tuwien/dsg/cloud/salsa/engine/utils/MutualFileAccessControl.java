package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MutualFileAccessControl {
	
	static private boolean AccessingFile = false;
	
	public static void lockFile(){
		while (AccessingFile) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
		AccessingFile = true;
	}
	
	public static void releaseFile(){
		AccessingFile = false;
	}
	
	// save uploaded file to new location
	public static void writeToFile(InputStream uploadedInputStream,
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
			CenterLogger.logger.error("Error writing to file: "
					+ uploadedFileLocation);
			CenterLogger.logger.error(e.toString());
		}		
	}
}
