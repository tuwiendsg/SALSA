package at.ac.tuwien.dsg.cloud.salsa.knowledge.basic.deplicated;

import java.io.File;
import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.deplicated.DeploymentObject_old;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.deplicated.KnowledgeGraph_deplicated;

public class Main {
	
	/**
	 * Main funtion 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		generateToscaFiles("/opt/salsa_knowledge");
		KnowledgeGraph_deplicated kg = new KnowledgeGraph_deplicated("/opt/salsa_knowledge");
		kg.showGraph();
	}
	
	private static void generateToscaFiles(String storePath) throws Exception{
		List<Class<? extends DeploymentObject_old>> lst = ListOfClass.getClassList();
		File file = new File(storePath);
		if (!file.exists()) {
			file.mkdir();
		}
		for (Class<? extends DeploymentObject_old> c : lst) {
			DeploymentObject_old obj = c.newInstance();
			obj.exportToXMLFile(new File(storePath+File.separator+obj.getType()));
		}
	}
	
}
