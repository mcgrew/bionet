package edu.purdue.jsysnet.ui;

import java.util.Collection;
import java.awt.Dimension;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.*;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.graph.util.Pair;


public class GraphVisualizer<V,E> extends VisualizationViewer<V,E> implements Graph<V,E> {
	protected Graph<V,E> graph = new UndirectedSparseGraph<V,E>( );


	public GraphVisualizer( ) {
		this( CircleLayout.class );
	}

	public GraphVisualizer( Class <? extends AbstractLayout> layout ) {
		super(( Layout<V,E> )GraphVisualizer.getLayoutInstance( layout ));
		this.setupGraph( );
	}

	public GraphVisualizer( Class <? extends AbstractLayout> layout, Dimension size ) {
		super(( Layout<V,E> )GraphVisualizer.getLayoutInstance( layout ), size );
		this.setupGraph( );
	}
	
	// override the inherited constructors
	public GraphVisualizer( Layout <V,E> layout ) {
		super( layout );
		this.setupGraph( );
	}

	public GraphVisualizer( Layout <V,E> layout, Dimension preferredSize ) {
		super( layout, preferredSize );
		this.setupGraph( );
	}

	public GraphVisualizer( VisualizationModel <V,E> model ) {
		super( model );
		this.setupGraph( );
	}

	public GraphVisualizer( VisualizationModel <V,E> model, Dimension preferredSize ) {
		super( model, preferredSize );
		this.setupGraph( );
	}

	protected static Layout getLayoutInstance( Class <? extends AbstractLayout> layout ) {
		return GraphVisualizer.getLayoutInstance( layout, new UndirectedSparseGraph( ));
	}

	protected static Layout getLayoutInstance( Class <? extends AbstractLayout> layout, Graph graph ) {
		try {  
			return layout.getConstructor( Graph.class ).newInstance( graph );

		} catch ( NoSuchMethodException e ) { 
			e.printStackTrace( System.err );
		} catch ( InstantiationException e ) { 
			e.printStackTrace( System.err );
		} catch ( IllegalAccessException e ) { 
			e.printStackTrace( System.err );
		} catch ( java.lang.reflect.InvocationTargetException e ) { 
			e.printStackTrace( System.err );
		}
		return null;
	}

	protected void setupGraph( ) {
		this.graph = ( UndirectedSparseGraph<V,E> )this.getGraphLayout( ).getGraph( );
		this.getRenderContext( ).setVertexLabelTransformer( new ToStringLabeller<V>( ));
//		this.getRenderContext( ).setEdgeLabelTransformer( new ToStringLabeller<E>( ));
		this.getRenderer( ).getVertexLabelRenderer( ).setPosition( Position.CNTR );
		DefaultModalGraphMouse mouse = new DefaultModalGraphMouse( );
		mouse.setMode( ModalGraphMouse.Mode.PICKING );
		this.setGraphMouse( mouse );
	}


	public void setGraphLayout( Class <? extends AbstractLayout> layout ){
			this.setGraphLayout(( Layout<V,E> )GraphVisualizer.getLayoutInstance( layout, this.graph ));
	}

	public void setGraphLayout( Layout <V,E> layout ){
		super.setGraphLayout( layout );
		this.graph = ( UndirectedSparseGraph<V,E> )this.getGraphLayout( ).getGraph( );
	}

	public void resetLayout( ) {
		this.getGraphLayout( ).reset( );
	}

	// Graph interface Methods
	public boolean addEdge( E e, V v1, V v2 ) {
		return this.graph.addEdge( e, v1, v2 );
	}

	public boolean addEdge( E e, V v1, V v2, EdgeType edgetype ) {
		return this.graph.addEdge( e, v1, v2, edgetype );
	}

	public V getDest( E directed_edge ) {
		return this.graph.getDest( directed_edge );
	}

	public Pair<V> getEndpoints( E edge ) {
		return this.graph.getEndpoints( edge );
	}

	public Collection<E> getInEdges( V vertex ) {
		return this.graph.getInEdges( vertex );
	}

	public V getOpposite( V vertex, E edge ) {
		return this.graph.getOpposite( vertex, edge );
	}

	public Collection<E> getOutEdges( V vertex ) {
		return this.graph.getOutEdges( vertex );
	}

	public int getPredecessorCount( V vertex ) {
		return this.graph.getPredecessorCount( vertex );
	}

	public Collection<V> getPredecessors( V vertex ) {
		return this.graph.getPredecessors( vertex );
	}

	public V getSource( E directed_edge ) {
		return this.graph.getSource( directed_edge );
	}

	public int getSuccessorCount( V vertex ) {
		return this.graph.getSuccessorCount( vertex );
	}

	public Collection<V> getSuccessors( V vertex ) {
		return this.graph.getSuccessors( vertex );
	}

	public int inDegree( V vertex ) {
		return this.graph.inDegree( vertex );
	}
	
	public boolean isDest( V vertex, E edge ) {
		return this.graph.isDest( vertex, edge );
	}

	public boolean isPredecessor( V v1, V v2 ) {
		return this.graph.isPredecessor( v1, v2 );
	}

	public boolean isSource( V vertex, E edge ) {
		return this.graph.isSource( vertex, edge );
	}

	public boolean isSuccessor( V v1, V v2 ) {
		return this.graph.isSuccessor( v1, v2 );
	}

	public int outDegree( V vertex ) {
		return this.graph.outDegree( vertex );
	}

	// Hypergraph interface methods
	public boolean addEdge( E edge, Collection<? extends V> vertices ) {
		return this.graph.addEdge( edge, vertices );
	}

	public boolean addEdge( E edge, Collection<? extends V> vertices, EdgeType edge_type ) {
		return this.graph.addEdge( edge, vertices, edge_type );
	}

	public boolean addVertex( V vertex ) {
		return this.graph.addVertex( vertex );
	}

	public boolean containsEdge( E edge ) {
		return this.graph.containsEdge( edge );
	}

	public boolean containsVertex( V vertex ) {
		return this.graph.containsVertex( vertex );
	}


	public E findEdge( V v1, V v2 ) {
		return this.graph.findEdge( v1, v2 );
	}

	public Collection<E> findEdgeSet( V v1, V v2 ) {
		return this.graph.findEdgeSet( v1, v2 );
	}

	public int getEdgeCount( ) {
		return this.graph.getEdgeCount( );
	}

	public int getEdgeCount( EdgeType edge_type ) {
		return this.graph.getEdgeCount( edge_type );
	}

	public Collection<E> getEdges( ) {
		return this.graph.getEdges( );
	}

	public Collection<E> getEdges( EdgeType edge_type ) {
		return this.graph.getEdges( edge_type );
	}

	public int degree( V vertex ) {
		return this.graph.degree( vertex );
	}

	public EdgeType getDefaultEdgeType( ) {
		return this.graph.getDefaultEdgeType( );
	}

	public EdgeType getEdgeType( E edge ) {
		return this.graph.getEdgeType( edge );
	}

	public int getIncidentCount( E edge ) {
		return this.graph.getIncidentCount( edge );
	}

	public Collection<E> getIncidentEdges( V vertex ) {
		return this.graph.getIncidentEdges( vertex );
	}

	public Collection<V> getIncidentVertices( E edge ) {
		return this.graph.getIncidentVertices( edge );
	}

	public int getNeighborCount( V vertex ) {
		return this.graph.getNeighborCount( vertex );
	}

	public Collection<V> getNeighbors( V vertex ) {
		return this.graph.getNeighbors( vertex );
	}

	public int getVertexCount( ) {
		return this.graph.getVertexCount( );
	}

	public Collection<V> getVertices( ) {
		return this.graph.getVertices( );
	}

	public boolean isIncident( V vertex, E edge ) {
		return this.graph.isIncident( vertex, edge );
	}

	public boolean isNeighbor( V v1, V v2 ) {
		return this.graph.isNeighbor( v1, v2 );
	}

	public boolean removeEdge( E edge ) {
		return this.graph.removeEdge( edge );
	}

	public boolean removeVertex( V vertex ) {
		return this.graph.removeVertex( vertex );
	}

	//UndirectedSparseGraph pass through Methods
	public boolean addEdge( E edge, Pair<? extends V> endpoints, EdgeType edgeType ) {
		return this.graph.addEdge( edge, endpoints, edgeType );
	}
}

