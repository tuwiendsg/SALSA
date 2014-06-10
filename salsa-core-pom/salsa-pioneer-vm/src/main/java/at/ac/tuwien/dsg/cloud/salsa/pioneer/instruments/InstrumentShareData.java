package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;

public class InstrumentShareData {
		
	static List<InstanceMapping> instances = new CopyOnWriteArrayList<>();
			
	static public void addInstanceProcess(String serviceId, String topoId, String nodeId, String instanceId, Process p){
		PioneerLogger.logger.debug("Add a node to remove queue: " + nodeId +"/" +instanceId);
		instances.add(new InstanceMapping(serviceId,topoId,nodeId, instanceId, p));
	}
	
	static public boolean killProcessInstance(String serviceId, String topoId, String nodeId, String instanceId){
		String instanceURL = serviceId+"/"+topoId+"/"+nodeId+"/"+instanceId;
		PioneerLogger.logger.debug("Attempt to kill this instance: " + instanceURL);
		// copy instances to an array to avoid ConcurentModificationException
		
		for (InstanceMapping inst : instances) {
			String instURL = inst.serviceId+"/"+inst.topoId+"/"+inst.nodeId+"/"+inst.instanceId;
			PioneerLogger.logger.debug("This is in the list: " + instURL);
			if (instURL.equals(instanceURL)){
				inst.p.destroy();
				return true;
			}
		}		
		return false;
	}
	
	static protected class InstanceMapping{
		String serviceId;
		String topoId;
		String nodeId;
		String instanceId;
		Process p;
		public InstanceMapping(String serviceId, String topoId, String nodeId, String instanceId, Process p){
			this.serviceId=serviceId;
			this.topoId=topoId;
			this.nodeId=nodeId;
			this.instanceId=instanceId;
			this.p = p;
		}
	}
	
	public static void startProcessMonitor(){
		PioneerLogger.logger.debug("Start the thread for monitoring ...");		
		Thread thread = new Thread(new monitoringThread());
		thread.start();
	}
	
	private static class monitoringThread implements Runnable {		
		
		@Override
		public void run() {
			// monitor here
			PioneerLogger.logger.debug("Thread for monitoring is running...");		
			while (true){
				for (InstanceMapping inst : instances) {
					try{
						inst.p.exitValue();
						PioneerLogger.logger.debug("A process finished: " + inst.p.exitValue());
						SalsaCenterConnector con = new SalsaCenterConnector(
								SalsaPioneerConfiguration.getSalsaCenterEndpoint(), inst.serviceId,
								SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
						con.updateNodeState(inst.topoId, inst.nodeId, Integer.parseInt(inst.instanceId), SalsaEntityState.FINISHED);					
						instances.remove(inst);
					} catch (IllegalThreadStateException e){
						// happen when p.exitValue about false						
					}
				}
				try{
					Thread.sleep(2000);
				} catch (InterruptedException e){}				
			}
		}

	}
	
}
