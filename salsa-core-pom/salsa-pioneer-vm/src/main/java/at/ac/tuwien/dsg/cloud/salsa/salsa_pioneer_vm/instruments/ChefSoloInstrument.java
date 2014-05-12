package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.instruments;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import generated.oasis.tosca.TNodeTemplate;

public class ChefSoloInstrument implements InstrumentInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChefSoloInstrument.class);
	private String chefScript="chef-solo-install-1.0.sh";
	// REPO: https://github.com/pjungwir/cookbooks
	
	@Override
	public void initiate(TNodeTemplate node) {
		// Install chef solo
		try {
			installChefAndGit();
			// download salsa script
			// http://128.130.172.215:8080/nexus-2.8.0-05/service/local/repositories/salsa-artifacts/content/chef/chef-solo-install/1.0/chef-solo-install-1.0.sh
			Process p = Runtime.getRuntime().exec("wget -q -L http://128.130.172.215:8080/nexus-2.8.0-05/service/local/repositories/salsa-artifacts/content/chef/chef-solo-install/1.0/chef-solo-install-1.0.sh");
			p.waitFor();
		} catch (Exception e){
			LOGGER.error(e.toString());
		}
	}

	@Override
	public String deployArtifact(String uri, String id) {
		// install the artifact with chef name
		try {	
			String cmd = this.chefScript + " " + uri;
			Process p = Runtime.getRuntime().exec("");
			p.waitFor();
		} catch (Exception e){
			
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
	
	
	private void installChefAndGit(){
		// install chef-client, chef-solo
		try{
			LOGGER.debug("Prepare installing CHEF...");
			Process p = Runtime.getRuntime().exec("curl -L https://www.opscode.com/chef/install.sh");
			p.waitFor();

			LOGGER.debug("Writing installation file...");
			StringBuffer sb = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			BufferedWriter out = new BufferedWriter(new FileWriter("install.sh"));
			out.write(sb.toString());
			out.flush();
			out.close();

			// RUN THE INSTALLTION
			LOGGER.debug("RUN THE INSTALATION...");
			p = Runtime.getRuntime().exec("bash install.sh");
			p.waitFor();
			// SEE THE OUTPUT
			sb = new StringBuffer();
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			System.out.println(sb.toString());
			
			
			// install GIT
			p = Runtime.getRuntime().exec("apt-get -y install git");
			p.waitFor();
			
			
		} catch (Exception e){
			LOGGER.error("Error while install CHEF and GIT");
		}
		
	}

}
