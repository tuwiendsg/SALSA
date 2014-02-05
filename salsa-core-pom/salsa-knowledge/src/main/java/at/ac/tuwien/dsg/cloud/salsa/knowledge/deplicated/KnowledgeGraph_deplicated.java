package at.ac.tuwien.dsg.cloud.salsa.knowledge.deplicated;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Paint;
import java.io.File;
import java.util.Collection;

import javax.swing.JFrame;
import javax.xml.namespace.QName;

import org.apache.commons.collections15.Transformer;

import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import generated.oasis.tosca.TCapabilityDefinition;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TExtensibleElements;
import generated.oasis.tosca.TNodeType;
import generated.oasis.tosca.TRelationshipType;
import generated.oasis.tosca.TRelationshipType.ValidSource;
import generated.oasis.tosca.TRelationshipType.ValidTarget;
import generated.oasis.tosca.TRequirementDefinition;

public class KnowledgeGraph_deplicated {
	Graph<TNodeType, TRelationshipType> graph;
	
	public KnowledgeGraph_deplicated(String repo){
		//this.kgraph = new SparseMultigraph<DeploymentObject, DeploymentObjectLink>();
		this.graph = new SparseMultigraph<TNodeType, TRelationshipType>();
		
		// read and add vertices
		File repoFolder = new File(repo);
		if (!repoFolder.exists() || repoFolder.isFile()){
			return;
		}
		for (File f : repoFolder.listFiles()) {
			if (f.isFile()){
				try{
					TDefinitions def = ToscaXmlProcess.readToscaFile(f.getPath());
					for (TExtensibleElements ele : def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
						if (ele.getClass().equals(TNodeType.class)){
							graph.addVertex((TNodeType)ele);
						}						
					}
					
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		// build edges
		for(TNodeType node1 : graph.getVertices()){
			for(TNodeType node2 : graph.getVertices()){
				TRelationshipType rela = new TRelationshipType();
				rela.setName(node1.getName()+".to."+node2.getName());
			}
		}
		
		for (TNodeType obj1 : graph.getVertices()) {			
			for (TNodeType obj2 : graph.getVertices()) {
				TRelationshipType link = addEdgeBetweenTwoVertices(obj1, obj2);
				if (link != null && !graph.getSuccessors(obj1).contains(obj2)){				
					graph.addEdge(link, obj1, obj2, EdgeType.DIRECTED);
				}
				System.out.println("Connected: " + obj1.getName() + " and " + obj2.getName());				
			}
		}
	}
	
	/*
	 * Capability ---> Requirement
	 */
	private TRelationshipType addEdgeBetweenTwoVertices(TNodeType node1, TNodeType node2){
		if (node1.getRequirementDefinitions() == null || node2.getCapabilityDefinitions() == null){
			return null;
		}
		for (TRequirementDefinition req : node1.getRequirementDefinitions().getRequirementDefinition()){
			for (TCapabilityDefinition capa : node2.getCapabilityDefinitions().getCapabilityDefinition()){
				if (req.getName().equals(capa.getName())){
					TRelationshipType rela = new TRelationshipType();					
					
					ValidSource sou = new ValidSource();
					sou.setTypeRef(new QName(node2.getName()));
					rela.setValidSource(sou);
					
					ValidTarget tar = new ValidTarget();
					tar.setTypeRef(new QName(node1.getName()));
					rela.setValidTarget(tar);
					
					return rela;
				}				
			}
		}
		return null;
	}
	
	
	public TNodeType searchObjectByType(String type){
		Collection<TNodeType> coll = graph.getVertices();
		for (TNodeType obj : coll) {
			if (obj.getName().equals(type)){
				return obj;
			}			
		}
		return null;
	}
	
	/**
	 * Looking for the deployment target of a node type
	 * @param objType
	 * @return
	 */
	public TNodeType searchTargetForObject(String objType){
		if (objType.equals("salsa-base")){
			return null;
		}
		Collection<TNodeType> coll = graph.getSuccessors(searchObjectByType(objType));
		for (TNodeType obj : coll) {
			return obj;		// have only 1 capa, will extend later
		}
		return null;
	}
	
	public void showGraph(){
		Layout<TNodeType,TRelationshipType> layout;	// for organize nodes on view
		VisualizationViewer<TNodeType,TRelationshipType> vv;	// buffer for viewing
		layout = new ISOMLayout<>(graph);
		layout.setSize(new Dimension(500, 500));		
		vv = new VisualizationViewer<TNodeType, TRelationshipType>(layout);
		vv.setPreferredSize(new Dimension(700, 700));
		
		Transformer<TNodeType, Paint> vertexPaint = new Transformer<TNodeType, Paint>() {
			public Paint transform(TNodeType obj) {
				return Color.RED;				
			}			
		};
		
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<TNodeType>());
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<TNodeType>());
		vv.getRenderContext().setEdgeLabelTransformer(new Transformer<TRelationshipType, String>() {
			public String transform(TRelationshipType arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.AUTO);
		
		DefaultModalGraphMouse<TEntityTemplate,TEntityTemplate> gm = new DefaultModalGraphMouse<>();
		gm.setMode(ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(gm);
		
		// Show everything
		JFrame frame = new JFrame("Salsa Knowledge Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = frame.getContentPane();
		content.setLayout(new FlowLayout());
		//content.setLayout(new GridLayout());
		content.add(vv);
		frame.pack();
		frame.setVisible(true);
		
		
	}
}
