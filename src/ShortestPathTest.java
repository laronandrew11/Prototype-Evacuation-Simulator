/*
 * Created on Jan 2, 2004
 */


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphMLWriter;
import edu.uci.ics.jung.io.graphml.EdgeMetadata;
import edu.uci.ics.jung.io.graphml.GraphMLReader2;
import edu.uci.ics.jung.io.graphml.GraphMetadata;
import edu.uci.ics.jung.io.graphml.HyperEdgeMetadata;
import edu.uci.ics.jung.io.graphml.NodeMetadata;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;

/**
 * Demonstrates use of the shortest path algorithm and visualization of the
 * results.
 * 
 * @author danyelf
 */
public class ShortestPathTest extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7526217664458188502L;

	/**
	 * Starting vertex
	 */
	private Node mFrom;

	/**
	 * Ending vertex
	 */	
	private Node mTo;
	
	private Node superSource;
	private Graph<Node,Edge> mGraph;
	private Set<Node> mPred;
	List<Edge> pathEdges;
	List<Node>sourceNodes=new ArrayList<Node>();
	private int population;
	
	private EdgeFactory edgeFactory;
	private VertexFactory nodeFactory;
	String imageLocation;
	private Number pathDistance;
	Layout<Node,Edge> layout;
	public ShortestPathTest() {

		nodeFactory=new VertexFactory();
		edgeFactory=new EdgeFactory();
		loadGraph();
		//this.mGraph = getGraph();
		 //replaceGraph();
		
        
     
    
	}
	
	public void replaceGraph()
	{
		 //add super-source node
        makeSuperSource();
		
        /*Background image*/
        ImageIcon mapIcon = null;
        try {
            mapIcon = 
            	    new ImageIcon(getClass().getResource(imageLocation));
        } catch(Exception ex) {
            System.err.println("Can't load \""+imageLocation+"\"");
        }
        final ImageIcon icon = mapIcon;
        
        Dimension layoutSize = new Dimension(1271,707);
        
       
        
        /*End Background image*/
        
		setBackground(Color.WHITE);
		// show graph

		Map<Node, Point2D> nodeMap=new HashMap<Node,Point2D>();
		if(mGraph!=null)
		{
		for(Object node:  mGraph.getVertices())
		{
			nodeMap.put((Node)node, ((Node)node).getCoordinates());
		}
		}
		//brute-force addition of nodes with specific coordinates
		/*coordinates.put((Node)nodes[0],new Point2D.Double(50.0,50.0));
		coordinates.put((Node)nodes[1],new Point2D.Double(50.0,100.0));
		coordinates.put((Node)nodes[2],new Point2D.Double(100.0,50.0));*/
		
		Transformer<Node,Point2D> vertexLocations = TransformerUtils.mapTransformer(nodeMap);
		
        layout = new StaticLayout<Node,Edge>(mGraph,vertexLocations);
        
        layout.setSize(layoutSize);
        
        final VisualizationViewer<Node,Edge> vv = new VisualizationViewer<Node,Edge>(layout);
        
        vv.setBackground(Color.WHITE);
        if(icon != null) {
            vv.addPreRenderPaintable(new VisualizationViewer.Paintable(){
                public void paint(Graphics g) {
                	Graphics2D g2d = (Graphics2D)g;
                	AffineTransform oldXform = g2d.getTransform();
                    AffineTransform lat = 
                    	vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getTransform();
                    AffineTransform vat = 
                    	vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getTransform();
                    AffineTransform at = new AffineTransform();
                    at.concatenate(g2d.getTransform());
                    at.concatenate(vat);
                    at.concatenate(lat);
                    g2d.setTransform(at);
                    g.drawImage(icon.getImage(), 0, 0,
                    		icon.getIconWidth(),icon.getIconHeight(),vv);
                    g2d.setTransform(oldXform);
                }
                public boolean useTransform() { return false; }
            });
        }
        vv.getRenderContext().setVertexDrawPaintTransformer(new MyVertexDrawPaintFunction<Node>());
        vv.getRenderContext().setVertexFillPaintTransformer(new MyVertexFillPaintFunction<Node>());
        vv.getRenderContext().setEdgeDrawPaintTransformer(new MyEdgePaintFunction());
        vv.getRenderContext().setEdgeStrokeTransformer(new MyEdgeStrokeFunction());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Node>());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Edge>());
        vv.setGraphMouse(new DefaultModalGraphMouse<Node, Edge>());
        vv.addPostRenderPaintable(new VisualizationViewer.Paintable(){
            
            public boolean useTransform() {
                return true;
            }
            public void paint(Graphics g) {
                if(mPred == null) return;
                
                // for all edges, paint edges that are in shortest path
                for (Edge e : layout.getGraph().getEdges()) {
                    
                    if(isBlessed(e)) {
                        Node v1 = mGraph.getEndpoints(e).getFirst();
                        Node v2 = mGraph.getEndpoints(e).getSecond();
                        Point2D p1 = layout.transform(v1);
                        Point2D p2 = layout.transform(v2);
                        p1 = vv.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
                        p2 = vv.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
                        Renderer<Node,Edge> renderer = vv.getRenderer();
                        renderer.renderEdge(
                                vv.getRenderContext(),
                                layout,
                                e);
                    }
                }
            }
        });
        
        setLayout(new BorderLayout());
        add(vv, BorderLayout.CENTER);
        // set up controls
        add(setUpControls(), BorderLayout.SOUTH);
        
        if(mGraph!=null)
        {
       findShortestExit();
        }
        //saveGraph();
	}

    boolean isBlessed( Edge e ) {
    	System.out.println("Edge is: "+e.getId());
    	Pair<Node> endpoints = mGraph.getEndpoints(e);
    	if(endpoints==null)
    	{
    	System.out.println("NO ENDPOINTS!");
    	}
    	System.out.println("Endpoints are:");
    	System.out.println(endpoints.getFirst().getId());
    	System.out.println(endpoints.getSecond().getId());
		Node v1= endpoints.getFirst()	;
		Node v2= endpoints.getSecond() ;
		return v1.equals(v2)==false && pathEdges.contains(e);
		//return v1.equals(v2) == false && mPred.contains(v1) && mPred.contains(v2);
    }
    
	/**
	 * @author danyelf
	 */
	public class MyEdgePaintFunction implements Transformer<Edge,Paint> {

		public Paint transform(Edge e) {
			if ( mPred == null || mPred.size() == 0) return Color.BLACK;
			if( isBlessed( e )) {
				return new Color(0.0f, 0.0f, 1.0f, 0.5f);//Color.BLUE;
			} else {
				return Color.LIGHT_GRAY;
			}
		}
	}

	public class MyEdgeStrokeFunction implements Transformer<Edge,Stroke> {
        protected final Stroke THIN = new BasicStroke(1);
        protected final Stroke THICK = new BasicStroke(1);

        public Stroke transform(Edge e) {
			if ( mPred == null || mPred.size() == 0) return THIN;
			if (isBlessed( e ) ) {
			    return THICK;
			} else 
			    return THIN;
        }

	}

	/**
	 * @author danyelf
	 */
	
	public class MyVertexDrawPaintFunction<V> implements Transformer<V,Paint> {

		public Paint transform(V v) {
			return Color.black;
		}

	}

	public class MyVertexFillPaintFunction<V> implements Transformer<V,Paint> {

		public Paint transform( V v ) {
			if(v==superSource)
			{
				return Color.WHITE;
			}
			
			if ( v == mFrom) {
				//System.out.println("Should be blue: "+v.toString());
				return Color.RED;
			}
			if ( v == mTo ) {
				//System.out.println("Should be blue: "+v.toString());
				return Color.BLUE;
			}
			if(((Node) v).isExit())
			{
				return Color.GREEN;
			}
			if ( mPred == null ) {
				//System.out.println("Nothing");
				return Color.LIGHT_GRAY;
			} else {
				if ( mPred.contains(v)) {
					//System.out.println("Should be red: "+v.toString());
					return Color.BLACK;
				} else {
					return Color.LIGHT_GRAY;
				}
			}
		}

	}

	/**
	 *  
	 */
	private JPanel setUpControls() {
		JButton btnLoad=new JButton("Load");
		btnLoad.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				loadGraph();
			}
		
		});
		JButton btnSave=new JButton("Save");
		btnSave.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				saveGraph();
			}
		
		});
		JPanel jp = new JPanel();
		jp.setBackground(Color.WHITE);
		jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
		jp.setBorder(BorderFactory.createLineBorder(Color.black, 3));		
		jp.add(
			new JLabel("Select a pair of vertices for which a shortest path will be displayed"));
		JPanel jp2 = new JPanel();
		jp2.add(new JLabel("vertex from", SwingConstants.LEFT));
		jp2.add(getSelectionBox(true));
		jp2.setBackground(Color.white);
		JPanel jp3 = new JPanel();
		jp3.add(new JLabel("vertex to", SwingConstants.LEFT));
		jp3.add(getSelectionBox(false));
		jp3.setBackground(Color.white);
		JPanel jp4=new JPanel();
		jp4.add(new JLabel("Population:",SwingConstants.LEFT));
		jp4.add(btnLoad);
		jp4.add(btnSave);
		jp.add( jp2 );
		jp.add( jp3 );
		jp.add(jp4);
		return jp;
	}

	private Component getSelectionBox(final boolean from) {

		Set<Node> s = new TreeSet<Node>();
		if(mGraph!=null)
		{
		for (Node v : mGraph.getVertices()) {
			s.add(v);
		}
		}
		final JComboBox choices = new JComboBox(s.toArray());
		choices.setSelectedIndex(-1);
		choices.setBackground(Color.WHITE);
		choices.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Node v = (Node)choices.getSelectedItem(); //probably source of problems
				

				if (from) {
					mFrom = v;
					//System.out.println("From: "+mFrom.toString());
				} else {
					mTo = v;
					//if(mTo!=null)
					//System.out.println("To: "+mTo.toString());
				}
				drawShortest();
				repaint();				
			}
		});
		return choices;
	}

	void makeSuperSource()
	{
		if(mGraph!=null){
		superSource=nodeFactory.create();
		mGraph.addVertex(superSource);
		for(Node n: mGraph.getVertices())
		{
			if(n.isExit()==false&&n!=superSource)
			{
				sourceNodes.add(n);
				mGraph.addEdge(edgeFactory.create(), superSource, n);
			}
		}
		}
	}
	boolean sourceHasEvacuees()
	{
		for(Node n: sourceNodes)
		{
			if(n.getCapacityUsed()!=0)
			{
				return false;
			}
		}
		return true;
	}
	void findShortestExit() //algorithm proper
	{
		mFrom=superSource;												
		HashMap<Node,Number> distances = new HashMap<Node,Number>();
		
		/*BEGIN CCRP-BASED ALGO*/
		/*while any source node s ∈ S has evacuee do { (1)*/
	/*	while(sourceHasEvacuees())
		{
			
		}*/
	
		/*
			find route R < n 0 ,n 1 ,...,n k > with time schedule < t 0 ,t 1 ,...,t k−1 >
			using one generalized shortest path search from super source s 0
			to all destinations, (where s ∈ S,d ∈ D,n 0 = s,n k = d)
			such that R has the earliest destination arrival time among
			routes between all (s,d) pairs,
			and Available Edge Capacity(e n i n i+1 ,t i ) > 0, ∀i ∈ {0,1,...,k − 1},
			and Available Node Capacity(n i+1 ,t i + Travel time(e n i n i+1 )) > 0,
			∀i ∈ {0,1,...,k − 1}; (2)
			flow = min( number of evacuees still at source node s,
			Available Edge Capacity(e n i n i+1 ,t i ), ∀i ∈ {0,1,...,k − 1},
			Available Node Capacity(n i+1 ,t i + Travel time(e n i n i+1 )),
			∀i ∈ {0,1,...,k − 1};
			); (3)
			for i = 0 to k − 1 do { (4)
			Available Edge Capacity(e n i n i+1 ,t i ) reduced by flow; (5)
			Available Node Capacity(n i+1 ,t i +Travel time(e n i n i+1 )) reduced by flow;
			(6)
			} (7)
			}*/
		/*END CCRP-based algo*/
		for(Node n: mGraph.getVertices())
		{
			if(n.isExit()==true)
			{
				mTo=n;
				drawShortest();
				distances.put(mTo, pathDistance);
			}
		}
		//sort distances by value
		distances=sortByValues(distances);
		
		//mTo=nearest exit
		mTo=distances.keySet().iterator().next();
		drawShortest();

	}
	/**
	 *  
	 */
	protected void drawShortest() {
		if (mFrom == null || mTo == null) {
			
			return;
		}
		/*Djikstra alternative starts here*/
		
		//weighted shortest path 
		Transformer<Edge, Float> wtTransformer = new Transformer<Edge,Float>() {
			public Float transform(Edge link) {
			return (Float)link.getWeight()*((mGraph.getEndpoints(link).getFirst().getHazardLevel()+mGraph.getEndpoints(link).getSecond().getHazardLevel())/2+1);
			}
			};
		
		//shortest path calculation
		DijkstraShortestPath<Node,Edge> alg = new DijkstraShortestPath(mGraph, wtTransformer);
		pathEdges = alg.getPath(mFrom, mTo);
		pathDistance = alg.getDistance(mFrom, mTo);
		//System.out.println(" The length of the path is: " + dist);
		
		mPred = new HashSet<Node>();
		for(Edge e:pathEdges)
		{
			mPred.add(mGraph.getEndpoints(e).getFirst());
			mPred.add(mGraph.getEndpoints(e).getSecond());
		}
		
		
		
		/*Djikstra alternative code ends here*/
		/*BFSDistanceLabeler<Node,Edge> bdl = new BFSDistanceLabeler<Node,Edge>();
		bdl.labelDistances(mGraph, mFrom);
		mPred = new HashSet<Node>();

		// grab a predecessor
		Node v = mTo;
		Set<Node> prd = bdl.getPredecessors(v); //gives no predecessors for some reason
		//System.out.println(prd.size());
		mPred.add( mTo );
		while( prd != null && prd.size() > 0) {
			v = prd.iterator().next();
			//System.out.println(v.toString());
			mPred.add( v );
			if ( v == mFrom ) return;
			prd = bdl.getPredecessors(v);
		}*/
	}

	public static void main(String[] s) {
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add(new ShortestPathTest());
		jf.pack();
		jf.setVisible(true);
	}

	/**
	 * @return the graph for this demo
	 */
	Graph<Node,Edge> getGraph() {
		int nodeCount=0;
		int edgeCount=0;
		Graph<Node,Edge> g =
				//manual graph construction
				new GraphFactory().create();
		Node v1=nodeFactory.create(new Point2D.Double(50.0,50.0),20,false);

		Node v2=nodeFactory.create(new Point2D.Double(50.0,125.0),20,false);
		Node v3=nodeFactory.create(new Point2D.Double(100.0,50.0),20,true);
		Node v4=nodeFactory.create(new Point2D.Double(200.0,50.0),20,true);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addEdge(edgeFactory.create(15,30), v1, v2); 
		g.addEdge(edgeFactory.create(7,30), v2, v3);
		g.addEdge(edgeFactory.create(10,30), v2, v4);
		
	
	
			//automatic graph generation
			/*new EppsteinPowerLawGenerator<Node,Edge>(
					new GraphFactory(), new VertexFactory(), new EdgeFactory(), 26, 50, 50).create();
		Set<Node> removeMe = new HashSet<Node>();
		for (Node v : g.getVertices()) {
            if ( g.degree(v) == 0 ) {
                removeMe.add( v );
            }
        }
		for(Node v : removeMe) {
			g.removeVertex(v);
		}*/
		return g;
	}
	public void loadGraph()
	{
		String filename="test.graphml";
		BufferedReader fileReader=null;
		try {
			 fileReader= new BufferedReader(
			        new FileReader(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* Create the Graph Transformer */
		Transformer<GraphMetadata, Graph<Node, Edge>>
		graphTransformer = new Transformer<GraphMetadata,
		                          Graph<Node, Edge>>() {
		 
		  public Graph<Node, Edge>
		      transform(GraphMetadata metadata) {
		        /*if (metadata.getEdgeDefault().equals(
		        metadata.getEdgeDefault().DIRECTED)) {
		            return new
		            DirectedSparseGraph<Node, Edge>();
		        } else {
		            return new
		            SparseGraph<Node, Edge>();
		        }*/
			  imageLocation=metadata.getProperty("backgroundImage");
			  return new SparseGraph<Node,Edge>();
		      }
		};
		
		/* Create the Vertex Transformer */
		Transformer<NodeMetadata, Node> vertexTransformer
		= new Transformer<NodeMetadata, Node>() {
		    public Node transform(NodeMetadata metadata) {
		        Node v =
		            nodeFactory.create();
		        v.setCoordinates(new Point2D.Double(Double.parseDouble(
		                           metadata.getProperty("x")),Double.parseDouble(
				                           metadata.getProperty("y"))));
		        v.setHazardLevel(Float.parseFloat(metadata.getProperty("hazardLevel")));
		        v.setMaxCapacity(Float.parseFloat(metadata.getProperty("maxCapacity")));
		        v.setExit(Boolean.parseBoolean(metadata.getProperty("isExit")));
		        return v;
		    }
		};
		
		/* Create the Edge Transformer */
		 Transformer<EdgeMetadata, Edge> edgeTransformer =
		 new Transformer<EdgeMetadata, Edge>() {
		     public Edge transform(EdgeMetadata metadata) {
		         Edge e = edgeFactory.create();
		         e.setMaxCapacity(Float.parseFloat(metadata.getProperty("maxCapacity")));
		         e.setLength(Float.parseFloat(metadata.getProperty("length")));
		         return e;
		     }
		 };
		
		 /* Create the Hyperedge Transformer */
		 Transformer<HyperEdgeMetadata, Edge> hyperEdgeTransformer
		 = new Transformer<HyperEdgeMetadata, Edge>() {
		      public Edge transform(HyperEdgeMetadata metadata) {
		          Edge e = edgeFactory.create();
		          return e;
		      }
		 };
		 
		 /* Create the graphMLReader2 */
		 GraphMLReader2<Graph<Node, Edge>, Node, Edge>
		 graphReader = new
		 GraphMLReader2<Graph<Node, Edge>, Node, Edge>
		       (fileReader, graphTransformer, vertexTransformer,
		        edgeTransformer, hyperEdgeTransformer);
		 
		 try {
			    /* Get the new graph object from the GraphML file */
			    mGraph = graphReader.readGraph();
			    System.out.println("New graph loaded");
			    replaceGraph();
			} catch (GraphIOException ex) {}
		 
	}
	public void saveGraph()
	{
		for(Edge edge:mGraph.getIncidentEdges(superSource))
		{
			mGraph.removeEdge(edge);
		}
		mGraph.removeVertex(superSource);
		
		String filename="test.graphml";
		PrintWriter out=null;
		GraphMLWriter<Node, Edge> graphWriter =
                new GraphMLWriter<Node, Edge> ();

		try {
			out = new PrintWriter(
			                     new BufferedWriter(
			                         new FileWriter(filename)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		graphWriter.addVertexData("x", null, "0",
			    new Transformer<Node, String>() {
			        public String transform(Node v) {
			            return Double.toString(v.getCoordinates().getX());
			        }
			    }
			);
			 
			graphWriter.addVertexData("y", null, "0",
			    new Transformer<Node, String>() {
			        public String transform(Node v) {
			            return Double.toString(v.getCoordinates().getY());
			       }
			    }
			);
			
			/*BEGIN Custom Attributes*/
			graphWriter.addGraphData("backgroundImage", null, "0", new Transformer<Hypergraph<Node,Edge>, String>(){

				
				public String transform(Hypergraph<Node,Edge> g) {

					return imageLocation;
				}
				
			}
					);
			
			graphWriter.addVertexData("hazardLevel", null, "0",
				    new Transformer<Node, String>() {
				        public String transform(Node v) {
				            return Float.toString(v.getHazardLevel());
				       }
				    }
				);
			
			graphWriter.addVertexData("maxCapacity", null, "0",
				    new Transformer<Node, String>() {
				        public String transform(Node v) {
				            return Float.toString(v.getMaxCapacity());
				       }
				    }
				);
			
			graphWriter.addVertexData("isExit", null, "false",
				    new Transformer<Node, String>() {
				        public String transform(Node v) {
				            return Boolean.toString(v.isExit());
				       }
				    }
				);
			
			graphWriter.addEdgeData("length", null, "0", 
					new Transformer<Edge, String>() {
		        public String transform(Edge e) {
		            return Float.toString(e.getLength());
		       }
		    }
					);
			graphWriter.addEdgeData("maxCapacity", null, "0", 
					new Transformer<Edge, String>() {
		        public String transform(Edge e) {
		            return Float.toString(e.getMaxCapacity());
		       }
		    }
					);
			/*END Custom Attributes*/
			
			try {
				graphWriter.save(mGraph, out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	private static HashMap sortByValues(HashMap map) { 
	       List list = new LinkedList(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o1)).getValue())
	                  .compareTo(((Map.Entry) (o2)).getValue());
	            }
	       });

	       // Here I am copying the sorted list in HashMap
	       // using LinkedHashMap to preserve the insertion order
	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	  }

	public Node getNodeFromString(String name)//(unused) TODO: change to binary search
	{
		 for(Node n: getGraph().getVertices())
		 {
			 if(name.equals(n.toString()))
				 return n;
		 }
		 System.out.println("Node not found");
		 return null;
		
	}
	
	static class GraphFactory implements Factory<Graph<Node,Edge>> {
		public Graph<Node,Edge> create() {
			return new SparseGraph<Node,Edge>();
		}
	}

	static class VertexFactory implements Factory<Node> {
		int count;
		public Node create() {
			return new Node(count++,new Point2D.Double(0.0,0.0),0,false);
		}
		public Node create(Point2D.Double coords,int maxCapacity,boolean isExit)
		{
			return new Node(count++,coords,maxCapacity,isExit);
		}

	}
	static class EdgeFactory implements Factory<Edge> {
		int count;
		public Edge create() {
			return new Edge(count++,0,99999);
		}
		public Edge create(float length, int maxCapacity) {
			return new Edge(count++,length,maxCapacity);
		}

	}

}