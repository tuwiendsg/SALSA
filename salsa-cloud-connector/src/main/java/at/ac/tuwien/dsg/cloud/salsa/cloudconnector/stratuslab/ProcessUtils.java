/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.cloudconnector.stratuslab;

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
	

