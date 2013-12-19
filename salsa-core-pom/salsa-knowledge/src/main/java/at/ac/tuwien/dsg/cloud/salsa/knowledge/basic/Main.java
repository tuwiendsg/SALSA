package at.ac.tuwien.dsg.cloud.salsa.knowledge.basic;

import java.io.File;
import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.process.DeploymentObject;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.process.KnowledgeGraph;

public class Main {
	
	/**
	 * Main funtion 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		generateToscaFiles("/tmp/salsa_knowledge");
		KnowledgeGraph kg = new KnowledgeGraph("/tmp/salsa_knowledge");
		kg.showGraph();
	}
	
	private static void generateToscaFiles(String storePath) throws Exception{
		List<Class<? extends DeploymentObject>> lst = ListOfClass.getClassList();
		File file = new File(storePath);
		if (!file.exists()) {
			file.mkdir();
		}
		for (Class<? extends DeploymentObject> c : lst) {
			DeploymentObject obj = c.newInstance();
			obj.exportToXMLFile(new File(storePath+File.separator+obj.getType()));
		}
	}
	
}
