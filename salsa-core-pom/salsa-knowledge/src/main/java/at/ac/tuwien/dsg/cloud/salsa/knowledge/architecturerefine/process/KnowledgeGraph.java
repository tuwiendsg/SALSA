package at.ac.tuwien.dsg.cloud.salsa.knowledge.architecturerefine.process;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Paint;
import java.io.File;
import java.util.Collection;

import javax.swing.JFrame;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections15.Transformer;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.architecturerefine.DeploymentObject;
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
import generated.oasis.tosca.TEntityTemplate;

public class KnowledgeGraph {
	protected class RelationshipLink{
		String name;
		DeploymentObject source;
		DeploymentObject target;
	}
	
	Graph<DeploymentObject, RelationshipLink> graph;
	
	public KnowledgeGraph(String repo){
		//this.kgraph = new SparseMultigraph<DeploymentObject, DeploymentObjectLink>();
		this.graph = new SparseMultigraph<DeploymentObject, RelationshipLink>();
		
		// read and add vertices
		File repoFolder = new File(repo);
		if (!repoFolder.exists() || repoFolder.isFile()){
			return;
		}
		for (File f : repoFolder.listFiles()) {
			if (f.isFile()){
				try{
					DeploymentObject obj = readDeploymentObjectFile(f.getPath());
					graph.addVertex(obj);					
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		// build edges
		for (DeploymentObject obj : graph.getVertices()) {
			if (obj.getDependencies() == null){
				continue;
			}
			for (String depe : obj.getDependencies().getDependencyList()) {
				for (DeploymentObject obj2 : graph.getVertices()){
					if (depe.equals(obj2.getName())){
						RelationshipLink rela = new RelationshipLink();
						rela.name = obj.getName() + ".to." + obj2.getName();
						graph.addEdge(rela, obj, obj2, EdgeType.DIRECTED);
						System.out.println("Connected: " + obj.getName() + " and " + obj2.getName());
					}
				}
			}
			
		}
		
	}

	public DeploymentObject searchObjectByType(String type){
		Collection<DeploymentObject> coll = graph.getVertices();
		for (DeploymentObject obj : coll) {
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
	public DeploymentObject searchTargetForObject(String objType){
		if (objType.equals("salsa-base")){
			return null;
		}
		Collection<DeploymentObject> coll = graph.getSuccessors(searchObjectByType(objType));
		for (DeploymentObject obj : coll) {
			return obj;		// have only 1 capa, will extend later
		}
		return null;
	}
	
	public void showGraph(){
		Layout<DeploymentObject,RelationshipLink> layout;	// for organize nodes on view
		VisualizationViewer<DeploymentObject,RelationshipLink> vv;	// buffer for viewing
		layout = new ISOMLayout<>(graph);
		layout.setSize(new Dimension(500, 500));		
		vv = new VisualizationViewer<DeploymentObject, RelationshipLink>(layout);
		vv.setPreferredSize(new Dimension(700, 700));
		
		Transformer<DeploymentObject, Paint> vertexPaint = new Transformer<DeploymentObject, Paint>() {
			public Paint transform(DeploymentObject obj) {
				return Color.RED;				
			}			
		};
		
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<DeploymentObject>());		
		vv.getRenderContext().setEdgeLabelTransformer(new Transformer<RelationshipLink, String>() {
			public String transform(RelationshipLink arg0) {
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
	
	
	
	private DeploymentObject readDeploymentObjectFile(String filename){
		try {
			 
			File file = new File(filename);
			JAXBContext jaxbContext = JAXBContext.newInstance(DeploymentObject.class);
	 
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			DeploymentObject obj = (DeploymentObject) jaxbUnmarshaller.unmarshal(file);			
			return obj;
		  } catch (JAXBException e) {
			e.printStackTrace();
		  }
		
		return null;
	}
}
