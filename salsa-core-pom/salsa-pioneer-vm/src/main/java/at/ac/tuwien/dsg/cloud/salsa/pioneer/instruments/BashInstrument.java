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
        
        /**
         * This match with the "sh" artifact type, which will exit after deployment
         * So, SALSA will WAIT until the process finishes
         */
	@Override
	public Object deployArtifact(String uri, String instanceId) {
		String runArt = uri;
		Process p;
		ProcessBuilder pb = new ProcessBuilder("/bin/bash",runArt);
		PioneerLogger.logger.debug("Executing command: /bin/bash " + runArt);		
		
		Map<String,String> env = pb.environment();
		String envPATH = env.get("PATH")+":"+SalsaPioneerConfiguration.getWorkingDirOfInstance(node.getId(), Integer.parseInt(instanceId));
		env.put("PATH", envPATH);
		PioneerLogger.logger.debug("env PATH="+envPATH);
		// e.g, working in /opt/salsa/<nodeid>
		pb.directory(new File(SalsaPioneerConfiguration.getWorkingDirOfInstance(node.getId(), Integer.parseInt(instanceId))));
		try {
			p = pb.start();
                        p.waitFor();
			return p;                        
		} catch (IOException | InterruptedException e) {
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
