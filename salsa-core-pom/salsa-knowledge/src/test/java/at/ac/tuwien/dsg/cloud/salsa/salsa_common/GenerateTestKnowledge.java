package at.ac.tuwien.dsg.cloud.salsa.salsa_common;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.model.architecturerefine.DeploymentObject;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.model.architecturerefine.impl.InstrumentApt;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.model.architecturerefine.impl.InstrumentChefsolo;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.model.architecturerefine.impl.InstrumentScript;

public class GenerateTestKnowledge {

	static String path = "/opt/salsa_knowledge";
	static ArrayList<DeploymentObject> lst = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		gen_os();
		gen_vm();
		gen_tomcat();
		gen_jre();
		gen_sh();
		gen_jar();

		for (DeploymentObject obj : lst) {
			writefile(obj.exportToXML(), obj.getName());
		}
		
		
	}

	private static void gen_os() throws Exception {
		DeploymentObject obj = new DeploymentObject();
		obj.setName("os");

		obj.addDependencies("vm");
		//obj.addDependencies("image");

		obj.setInstrument(InstrumentScript.class);

		lst.add(obj);
	}
	
	private static void gen_vm() throws Exception {
		DeploymentObject obj = new DeploymentObject();

		obj.setName("vm");

		obj.addDependencies("cloud");
		
		obj.addRequirement("test");

		obj.setInstrument(InstrumentScript.class);

		lst.add(obj);
	}
	
	private static void gen_tomcat() throws Exception {
		DeploymentObject obj = new DeploymentObject();

		obj.setName("tomcat");

		obj.addDependencies("os");
		obj.addDependencies("jre");	
		
		obj.addRequirement("cpu >= 2");
		obj.addRequirement("mem > 1024");
		obj.addRequirement("storage > 100MB");

		obj.setInstrument(InstrumentChefsolo.class);

		lst.add(obj);
	}
	
	private static void gen_jre() throws Exception {
		DeploymentObject obj = new DeploymentObject();

		obj.setName("jre");

		obj.addDependencies("os");		
		
		obj.addRequirement("cpu >= 1");
		obj.addRequirement("mem > 512");
		obj.addRequirement("storage > 50MB");

		obj.setInstrument(InstrumentApt.class);

		lst.add(obj);
	}
	
	private static void gen_sh() throws Exception {
		DeploymentObject obj = new DeploymentObject();

		obj.setName("sh");

		obj.addDependencies("os");		
		
		obj.addRequirement("cpu >= 1");
		obj.addRequirement("mem > 512");
		obj.addRequirement("storage > 30MB");

		obj.setInstrument(InstrumentScript.class);

		lst.add(obj);
	}
	
	private static void gen_jar() throws Exception {
		DeploymentObject obj = new DeploymentObject();

		obj.setName("jar");

		obj.addDependencies("os");
		obj.addDependencies("jre");

		obj.setInstrument(InstrumentApt.class);

		lst.add(obj);
	}

	private static void writefile(String data, String name) throws Exception {
		File theDir = new File(path);

		if (!theDir.exists()) {
			System.out.println("creating directory: " + path);
			boolean result = theDir.mkdir();
			if (result) {
				System.out.println("DIR created");
			}
		}
		
		PrintWriter out = new PrintWriter(path+File.separator+name);
		out.println(data);
		out.close();
		System.out.println("Written: " + path+File.separator+name);
		
	}

}
