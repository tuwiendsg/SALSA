package at.ac.tuwien.dsg.cloud.salsa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector.HttpVerb;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;

public class TestExp2 {

	static String SERVICE_ID = "multicloud";	
	static String TOPO_ID="DataMarketAgence";
	static String INST_ID_stratus="agence_stratus";
	static String INST_ID_dsg="agence_dsg";
	static java.util.Date date= new java.util.Date();
	static String url1 = "http://128.130.172.215:8080/salsa-engine/rest/"
			+ "services/" + SERVICE_ID + "/"
			+ "topologies/"+TOPO_ID+"/"
			+ "nodes/"+INST_ID_stratus+"/"
			+ "instance-count/1";
	static String url2 = "http://128.130.172.215:8080/salsa-engine/rest/"
			+ "services/" + SERVICE_ID + "/"
			+ "topologies/"+TOPO_ID+"/"
			+ "nodes/"+INST_ID_dsg+"/"
			+ "instance-count/1";
	static String url_remove = "http://128.130.172.215:8080/salsa-engine/rest/"
			+ "services/" + SERVICE_ID + "/"
			+ "topologies/"+TOPO_ID+"/"
			+ "nodes/"+INST_ID_dsg+"/"
			+ "instances/";
	//http://128.130.172.215:8080/salsa-engine/rest/services/
	//3ee1bd18-32c3-4d35-acd0-c52a820b0c33/
	//topologies/DataMarketAgence/
	//nodes/agence_os/instances/323
	public static void main(String[] args) {
		//expSinusoidalShapheData();
		//expLinearData();
		
		SalsaCenterConnector con = new SalsaCenterConnector("http://128.130.172.215:8080/salsa-engine", "", EngineLogger.logger);
		System.out.println(con.getUpdateCloudServiceRuntime("M2MDaaS").getId());
		
	}
	
	static private void expLinearData(){		
		Thread thread = startProcessMonitor();
		for (int i=1; i<60; i++){
			System.out.println("Query time: " + i);
			queryDataToCenter(url2, HttpVerb.POST, "", "", "");
			try{
				Thread.sleep(300);
			} catch (Exception e) {}
			System.out.println("Query time: " + i);
			queryDataToCenter(url1, HttpVerb.POST, "", "", "");
			try{
				Thread.sleep(300);
			} catch (Exception e) {}
		}
	}
	
	
	static private void addMultiple(){
		List<String> insts = new ArrayList<>();
		startProcessMonitor();	
		System.out.println("Start running requests");
		String id = queryDataToCenter(url1, HttpVerb.POST, "", "", "");
		System.out.println(id);
	}
	
	
	
	static private void expSinusoidalShapheData(){
		List<String> insts = new ArrayList<>();
		startProcessMonitor();		
		System.out.println("Start running sinusoidal shape requests");
			for (int i=1; i<=40; i++){
				String id = queryDataToCenter(url2, HttpVerb.POST, "", "", "");
				System.out.println("Loop i: " + i + ". Deploy node: " + id);
				insts.add(id);
				System.out.println("Instances left: " + insts.size());
				try{
					Thread.sleep(10000);
				} catch (Exception e) {}
			}
			
			try{
				Thread.sleep(30000);
			} catch (Exception e) {}
			
			for (int i=40; i>=10; i--){
				String pop = insts.get(0);		
				String id = queryDataToCenter(url_remove + pop.trim(), HttpVerb.DELETE, "", "", "");
				System.out.println("Loop i: " + i + ". Remove node: " + id);
				System.out.println("Instances left: " + insts.size());
				insts.remove(0);
				try{
					Thread.sleep(10000);
				} catch (Exception e) {}
			}
			
			try{
				Thread.sleep(30000);
			} catch (Exception e) {}
			
			for (int i=10; i<=70; i++){
				String id = queryDataToCenter(url2, HttpVerb.POST, "", "", "");
				System.out.println("Loop i: " + i + ". Deploy node: " + id);
				insts.add(id);
				System.out.println("Instances left: " + insts.size());
				try{
					Thread.sleep(10000);
				} catch (Exception e) {}
			}
			
//			for (int i=60; i>=1; i--){
//				String pop = insts.get(0);		
//				String id = queryDataToCenter(url_remove + pop.trim(), HttpVerb.DELETE, "", "", "");
//				System.out.println("Loop i: " + i + ". Remove node: " + id);
//				System.out.println("Instances left: " + insts.size());
//				insts.remove(0);
//				try{
//					Thread.sleep(10000);
//				} catch (Exception e) {}
//			}
//			
//			for (int i=35; i<=70; i++){
//				String id = queryDataToCenter(url, HttpVerb.POST, "", "", "");
//				System.out.println("Loop i: " + i + ". Deploy node: " + id);
//				insts.add(id);
//				System.out.println("Instances left: " + insts.size());
//				try{
//					Thread.sleep(10000);
//				} catch (Exception e) {}
//			}
			
		
	}
	
	
	
	
	public static Thread startProcessMonitor(){
		EngineLogger.logger.debug("Start the thread for monitoring ...");		
		Thread thread = new Thread(new monitoringThread());
		thread.start();
		return thread;		
	}
	
	private static class monitoringThread implements Runnable {		
		
		@Override
		public void run() {
			// monitor here
			EngineLogger.logger.debug("Thread for monitoring is running...");
			SalsaCenterConnector con = new SalsaCenterConnector(
					"http://128.130.172.215:8080/salsa-engine",
					"", EngineLogger.logger);
			File file =new File("/tmp/salsa-exp2-data.txt");
			try{
				if(!file.exists()){
	    			file.createNewFile();
	    		}

				FileWriter fileWritter = new FileWriter(file.getAbsoluteFile(),true);
		        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		        int count=0;
				while (true){

					CloudService service = con.getUpdateCloudServiceRuntime(SERVICE_ID);

					ServiceUnit unit = service.getComponentById(TOPO_ID, INST_ID_dsg);

					int iRun = unit.getInstanceByState(SalsaEntityState.RUNNING).size() + unit.getInstanceByState(SalsaEntityState.FINISHED).size();
					int iLst = unit.getInstancesList().size();
					String data = iRun+", " + iLst + ", " + date.getTime() + "\n";
					//System.out.println("Thread is writing: " + data);
					
					bufferWritter.write(data);
					bufferWritter.flush();
					fileWritter.flush();
					Thread.sleep(5000);
					
					count++;
					if (count > 3000) {
						break;
					}
				}
				bufferWritter.close();
			} catch (Exception e) {			
				System.out.println(e);				
			}
		}
	}
	
	
	static private String queryDataToCenter(String input_url, HttpVerb method, String data, String type, String accept) {
		try {
			URL url = new URL(input_url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method.toString());
				
			if (accept.equals("")){			
				conn.setRequestProperty("Accept", MediaType.TEXT_PLAIN);
			} else {
				conn.setRequestProperty("Accept", accept);
			}
			
			if (type.equals("")){
				conn.setRequestProperty("Type", MediaType.TEXT_PLAIN);
			} else {
				conn.setRequestProperty("Type", type);
			}
			EngineLogger.logger.debug("Execute a query. URL: " + url +". Method: " +method + ". Data: " + data +". Sending type:" + type + ". Recieving type: " + accept);
			
			
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			 
			String output;
			String result = "";
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				result+=output;
			} 
			conn.disconnect();
			
			return result;	
		} catch (Exception e){
			EngineLogger.logger.error("Error when executing the query. Error: " + e);
			return null;
		}
	}
	
	
	
	
		
}
