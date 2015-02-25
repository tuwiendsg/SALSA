package at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceJsonDataForceDirect {
	
	List<Node> nodes = new ArrayList<>();
	List<Link> links = new ArrayList<>();
        Map<String, Integer> nodeIndexMap = new HashMap<>();
        int nodeIndexCount=0;
	
	public class Node{		
		String name;
		String group;
                String state;
                String type;
		public Node(String name, String group, String type, String state){
			this.name = name;
			this.group = group;
                        this.state = state;
                        this.type = type;
		}
	}
	
	public class Link{
		int source;
		int target;
		String type;
		public Link(int source, int target, String type){
			this.source = source;
			this.target = target;
			this.type = type;	
		}
	}
	
	public void addNode(String name, String group, String type, String state){                
		this.nodes.add(new Node(name,group,type,state));
                nodeIndexMap.put(name, nodeIndexCount);
                nodeIndexCount+=1;
	}
	
	public void addLink(String source, String target, String type){
		this.links.add(new Link(nodeIndexMap.get(source), nodeIndexMap.get(target), type));
	}
}
