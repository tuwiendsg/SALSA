package at.ac.tuwien.dsg.cloud.salsa;

import java.util.UUID;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.manifest.StaticServiceDescriptionFactory;
import at.ac.tuwien.dsg.cloud.modules.CloudAppModule;
import at.ac.tuwien.dsg.cloud.services.ServiceDeployer;

public class TestDeploy {

	public static void main(String[] args) {
		String manifestFile=TestDeploy.class.getResource("/cassandra.manifest.xml").getFile();
		//String manifestFile=TestDeploy.class.getResource("/doodle-manifest.xml").getFile();
		//"http://www.inf.usi.ch/phd/gambi/attachments/autocles/doodle-manifest.xml"
		//String manifestFile="http://134.158.75.167/xml/cassandra.manifest.xml";
		
		String[] _args = new String[5];
		_args[0] = "DEPLOY";
		_args[1] = "var1";
		_args[2] = "var2";
		_args[3] = "var3";
		_args[4] = manifestFile;
		System.getProperties().put("at.ac.tuwien.dsg.cloud.configuration", TestDeploy.class.getResource("/cloud.properties").getFile());
		StaticServiceDescription serviceSpec = null;
		
		try{
			//TDefinitions def = ToscaXmlProcess.readToscaFile(TestDeploy.class.getResource("/cassandra.tosca.xml").getFile());
			//serviceSpec = ToscaParserToStaticDescription.parse(def);			
			serviceSpec = StaticServiceDescriptionFactory.fromXML(manifestFile);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("TestDeployOpenStack.main() "
				+ serviceSpec.getOrderedVees());
		UUID deployId = null;
		RegistryBuilder builder = new RegistryBuilder();
		builder.add(CloudAppModule.class);
		builder.add(at.ac.tuwien.dsg.cloud.openstack.modules.CloudAppModule.class);
		
		final Registry reg = builder.build();
		reg.performRegistryStartup();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){

			@Override
			public void run() {
				reg.cleanupThread();
				reg.shutdown();
			}			
		});
		
		if (deployId == null){
			try{
				deployId = reg.getService("OSServiceDeployer",ServiceDeployer.class).deployService(_args[1], _args[2], _args[3], serviceSpec);				
			} catch (ServiceDeployerException e) {
				if (e.getMessage().contains("under deployment")) {

					String _deployID = e.getMessage().substring(
							e.getMessage().lastIndexOf(" "),
							e.getMessage().length());
					System.out.println("TestDeploy.main() " + _deployID);

					deployId = UUID.fromString(_deployID);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
				System.out.println("Sleep 60s ...");
				Thread.sleep(60000);
			} catch (InterruptedException e) {				
			}
			
			try {
				System.out.println("Try to undeploy all ...");
				reg.getService("OSServiceDeployer",ServiceDeployer.class).undeployService(_args[1], _args[2], _args[3], deployId,serviceSpec);
				System.out.println("YOOOOOOOOOOOO");
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		
	}

}
