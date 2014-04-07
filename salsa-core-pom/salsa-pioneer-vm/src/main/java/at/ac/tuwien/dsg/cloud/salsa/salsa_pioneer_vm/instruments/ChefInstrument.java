package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.instruments;

import generated.oasis.tosca.TNodeTemplate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils.SalsaPioneerConfiguration;

public class ChefInstrument implements InstrumentInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChefInstrument.class);
	private static int counter=0;
	static {
		LOGGER.debug("Loading ChefInstrument!");
	}

	@Override
	public void initiate(TNodeTemplate node) {

		LOGGER.debug("Instrument instantiate chef");
		// install chef if not ready
		File chefFile = new File("/usr/bin/chef-client");
		if (!chefFile.exists()) {
			try {
				// install chef-client
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
				Process p1 = Runtime.getRuntime().exec("bash install.sh");
				p1.waitFor();
				// SEE THE OUTPUT
				sb = new StringBuffer();
				reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				System.out.println(sb.toString());

				File chefDir = new File("/etc/chef");
				chefDir.mkdirs();
				// write validation.pem

				InputStream is = ChefInstrument.class.getResourceAsStream("/chef/validation.pem");
				OutputStream os = new FileOutputStream("/etc/chef/validation.pem");
				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
				os.flush();

				is.close();
				os.close();

			} catch (Exception e) {
				LOGGER.debug("Error when installing chef. Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * put the client.rc with the chef name on it
	 */
	@Override
	public String deployArtifact(String uri, String instanceId) {
		try {

			StringBuffer sb = new StringBuffer();

			sb.append("log_level 		:auto \n");
			sb.append("log_location     STDOUT \n");
			sb.append("log_location     STDOUT \n");
			sb.append("chef_server_url  \"https://api.opscode.com/organizations/tuwien\" \n");
			sb.append("validation_client_name \"tuwien-validator\" \n");
			
			Properties prop = new Properties();
			prop.load(new FileInputStream(SalsaPioneerConfiguration.getSalsaVariableFile()));		
			String VMID = prop.getProperty("SALSA_REPLICA");	// get the VM ID
			
			//FIXME: If we run multiple instances this will cause error.
			//FIXED: read the id of the VM from /etc/salsa.variables and add to the node.
			// This method is not good by using a function from higher layer.
			sb.append("node_name \"" + uri.trim()+"_"+instanceId + "\" \n");
			
			
			BufferedWriter out = new BufferedWriter(new FileWriter("/etc/chef/client.rb"));
			out.write(sb.toString());
			out.flush();
			out.close();

			Process p = Runtime.getRuntime().exec("chef-client");
			p.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuffer sb2 = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb2.append(line + "\n");
			}

			LOGGER.debug(sb2.toString());

			// Run chef-client every 10 seconds
			LOGGER.debug("Start the loop of running Chef every 10 seconds");
			final ReadWriteLock lock = new ReentrantReadWriteLock();
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						lock.writeLock().lock();
						ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "chef-client >> /opt/chef.log 2>&1 &");
						//ProcessBuilder builder = new ProcessBuilder("screen", "-dmS", "chef", "chef-client");
						builder.redirectOutput(Redirect.appendTo(new File("/opt/chef.log")));
						builder.redirectError(Redirect.appendTo(new File("/opt/chef.log")));
						Process process = builder.start();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						lock.writeLock().unlock();
					}

				}
			};
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleAtFixedRate(r, 0, 10, TimeUnit.SECONDS);
			
			Runtime.getRuntime().exec("screen -dmS chef tail -f /opt/chef.log");
			
			return "Done";

		} catch (Exception e) {
			LOGGER.debug(e.toString());
			return null;
		}
	}

	@Override
	public String getStatus(String nodeId, String instanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String executeArtifactAction(String action, String nodeId, String instanceId) {
		// TODO Auto-generated method stub
		return null;
	}

}
