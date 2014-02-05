package at.ac.tuwien.dsg.cloud.salsa.salsa_common;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.process.KnowledgeGraph;

public class TestKnowledgeBase {
	public static void main(String[] args) throws Exception{
		KnowledgeGraph kg = new KnowledgeGraph("/opt/salsa_knowledge");
		kg.showGraph();
	}
}
