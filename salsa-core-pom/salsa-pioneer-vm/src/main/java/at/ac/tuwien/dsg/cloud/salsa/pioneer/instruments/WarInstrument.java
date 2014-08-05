package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import generated.oasis.tosca.TNodeTemplate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.print.attribute.standard.MediaSize.Engineering;

import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;

public class WarInstrument implements InstrumentInterface {

	String nodeId="";
	
	@Override
	public void initiate(TNodeTemplate node) {
		this.nodeId = node.getId();
	}

	@Override
	public Object deployArtifact(String uri, String id) {
		String webContainerDir = "/var/lib/tomcat7/webapps/";
		PioneerLogger.logger.debug("Copying war file to the target. File: " + uri);
		try {
			InputStream input = null;
			OutputStream output = null;
			try {
				input = new FileInputStream(SalsaPioneerConfiguration.getWorkingDir() +"/"+nodeId+"/"+uri);
				output = new FileOutputStream(webContainerDir + uri);
				byte[] buf = new byte[1024];
				int bytesRead;
				while ((bytesRead = input.read(buf)) > 0) {
					output.write(buf, 0, bytesRead);
				}
			} catch (Exception e4){
				e4.printStackTrace();
			} finally {
				if (input!=null) input.close();				
				if (output!=null) output.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
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
