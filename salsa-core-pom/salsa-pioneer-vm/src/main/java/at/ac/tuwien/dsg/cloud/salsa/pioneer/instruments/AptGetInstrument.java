package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import generated.oasis.tosca.TNodeTemplate;

public class AptGetInstrument implements InstrumentInterface {

	@Override
	public void initiate(TNodeTemplate node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object deployArtifact(String uri, String id) {
		Process p;
		PioneerLogger.logger.debug("apt-get the artifact name: " + uri);	
		try {
			p = Runtime.getRuntime().exec("apt-get -y install " + uri);
			p.waitFor();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				line = reader.readLine();
				PioneerLogger.logger.debug(line);
			}
		} catch (IOException e) {
			PioneerLogger.logger.debug(e.toString());			
		} catch (InterruptedException e1) {
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
