package at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata;

import java.util.ArrayList;
import java.util.List;

public class ServiceJsonDataForceDirect {
	
	List<Node> nodes = new ArrayList<ServiceJsonDataForceDirect.Node>();
	List<Link> links = new ArrayList<ServiceJsonDataForceDirect.Link>();
	
	public class Node{		
		String name;
		String group;
		public Node(String name, String group){
			this.name = name;
			this.group = group;
		}
	}
	
	public class Link{
		String source;
		String target;
		String type;
		public Link(String source, String target, String type){
			this.source = source;
			this.target = target;
			this.type = type;			
		}
	}
	
	public void addNode(String name, String group){
		this.nodes.add(new Node(name,group));
	}
	
	public void addLink(String source, String target, String type){
		this.links.add(new Link(source, target, type));
	}
}
