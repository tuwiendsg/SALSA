package at.ac.tuwien.dsg.cloud.salsa.cloud_connector.stratuslab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessUtils {
	
		public static String execGetOutput(String[] command,String[] env) throws IOException, InterruptedException
		 {
			//getLogger().info("Calling: " + commandMessage);

			//ProcessBuilder pb = new ProcessBuilder(command);
			//pb.redirectErrorStream(true);

			//Process p = pb.start();
			//System.out.println("executing: "+ command[0]+command[1]);
			
			Process p;
			p = Runtime.getRuntime().exec(command,env);
			
			StringBuffer outputBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = reader.readLine();
			
			while (line != null) {				
				outputBuf.append(line);
				outputBuf.append("\n");
				//getLogger().info(line);
				//System.out.println(line);
				line = reader.readLine();
			}
			
			// Check for failure
			try {
				if (p.waitFor() != 0) {
					String commandMessage = "";
					for (String part : command) {
						commandMessage += part + " ";
					}
					String error = "Error executing: " + commandMessage
							+ ". With exit code = " + p.exitValue()
							+ " and output: " + outputBuf;
					System.out.println(error);
					//getLogger().severe(error);
					//throw (new SlipStreamClientException(outputBuf.toString()));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				//throw (new SlipStreamInternalException(e));
			} finally {
				reader.close();
			}
			return outputBuf.toString();
		}

		//protected static Logger getLogger() {
		//	return Logger.getLogger("SlipStream");
		//}
}
	

