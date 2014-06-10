package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import generated.oasis.tosca.TNodeTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;

public class BashInstrument extends InstrumentShareData implements InstrumentInterface  {
	TNodeTemplate node;

	@Override
	public void initiate(TNodeTemplate node) {
		PioneerLogger.logger.debug("INSTRUMENT INITIATE: BASH");
		this.node = node;
	}

	@Override
	public Object deployArtifact(String uri, String instanceId) {
		String runArt = uri;
		Process p;
		ProcessBuilder pb = new ProcessBuilder("bash",runArt);
		PioneerLogger.logger.debug("Executing command: bash " + runArt);		
		
		Map<String,String> env = pb.environment();
		String envPATH = env.get("PATH")+":"+SalsaPioneerConfiguration.getWorkingDir();
		env.put("PATH", envPATH);
		PioneerLogger.logger.debug("env PATH="+envPATH);
		// e.g, working in /opt/salsa/<nodeid>
		pb.directory(new File(SalsaPioneerConfiguration.getWorkingDir()+File.separator+node.getId()));
		try {
			p = pb.start();
			return p;
			// wait and check if this instance is in the removal queue
			// addInstanceProcess(node.getId(), instanceId, p);
			//p.waitFor();
			
//			PioneerLogger.logger.debug("Executed process. Exit value: " + p.exitValue());

//			BufferedReader reader = new BufferedReader(
//					new InputStreamReader(p.getInputStream()));
//			String line = reader.readLine();
//			while (line != null) {
//				line = reader.readLine();
//				PioneerLogger.logger.debug(line);
//			}
		} catch (IOException e) {
			PioneerLogger.logger.debug(e.toString());			
		} 
		
		return null;
	}
	

	@Override
	public String getStatus(String nodeId, String instanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String executeArtifactAction(String action, String nodeId,
			String instanceId) {
		// TODO Auto-generated method stub
		return null;
	}

}
