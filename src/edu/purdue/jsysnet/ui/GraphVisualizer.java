/*

This file is part of JSysNet.

JSysNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JSysNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with JSysNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.ui.layout.*;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JScrollBar;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.*;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.control.ViewScalingControl;
import edu.uci.ics.jung.visualization.control.AbsoluteCrossoverScalingControl;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;


/**
 * A class for visualizing a network graph. 
 */
public class GraphVisualizer<V,E> extends VisualizationViewer<V,E> implements Graph<V,E>,ItemListener {
	public Graph<V,E> graph = new UndirectedSparseGraph<V,E>( );
	private LayoutAnimator layoutAnimator;
	private Thread AnimThread;
	private AbsoluteCrossoverScalingControl absoluteViewScaler = 
		new AbsoluteCrossoverScalingControl( );
	private ArrayList <PickedStateChangeListener> pickedVertexStateChangeListeners =
		new ArrayList<PickedStateChangeListener>( );
	private ArrayList <PickedStateChangeListener> pickedEdgeStateChangeListeners =
		new ArrayList<PickedStateChangeListener>( );
//	private ViewScalingControl viewScaler = new ViewScalingControl( );
	private GraphZoomScrollPane scrollPane;
	private float currentZoom = 1.0f;
	private static float minimumZoom = 0.8f;
	private Collection <V> lastPickedVertices = new Vector<V>( );
	private Collection <E> lastPickedEdges = new Vector<E>( );;

	/**
	 * Constructs a GraphVisualizer object.
	 */
	public GraphVisualizer( ) {
		this( CircleLayout.class );
	}

	/**
	 * Constructs a GraphVisualizer Object.
	 * 
	 * @param layout A Class object of the type of class to use for the graph layout.
	 */
	public GraphVisualizer( Class <? extends AbstractLayout> layout ) {
		super(GraphVisualizer.getLayoutInstance( layout ));
		this.setup( );
	}

	/**
	 * Constructs a GraphVisualizer Object.
	 * 
	 * @param layout A Class object of the type of class to use for the graph layout.
	 * @param size The initial size of the GraphVisualizer Object.
	 */
	public GraphVisualizer( Class <? extends AbstractLayout> layout, Dimension size ) {
		super(GraphVisualizer.getLayoutInstance( layout ), size );
		this.setup( );
	}
	
	// override the inherited constructors
	/**
	 * Constructs a GraphVisualizer Object.
	 * 
	 * @param layout The Layout to use for the graph.
	 */
	public GraphVisualizer( Layout <V,E> layout ) {
		super( layout );
		this.setup( );
	}

	/**
	 * Constructs a GraphVisualizer Object.
	 * 
	 * @param layout The Layout to use for the graph.
	 * @param preferredSize The preferred size of the GraphVisualizer.
	 */
	public GraphVisualizer( Layout <V,E> layout, Dimension preferredSize ) {
		super( layout, preferredSize );
		this.setup( );
	}

	/**
	 * Constructs a GraphVisualizer Object.
	 * 
	 * @param model The VisualizationModel to use for the graph.
	 */
	public GraphVisualizer( VisualizationModel <V,E> model ) {
		super( model );
		this.setup( );
	}

	/**
	 * Constructs a GraphVisualizer Object.
	 * 
	 * @param model The VisualizationModel to use for the graph.
	 * @param preferredSize The preferred size of the graph.
	 */
	public GraphVisualizer( VisualizationModel <V,E> model, Dimension preferredSize ) {
		super( model, preferredSize );
		this.setup( );
	}

	/**
	 * Creates a new layout instance from a Class Object.
	 * 
	 * @param layout The Class object to create the instance of.
	 * @return A new instance of the Layout.
	 */
	protected static Layout getLayoutInstance( Class <? extends AbstractLayout> layout ) {
		return GraphVisualizer.getLayoutInstance( layout, new UndirectedSparseGraph( ));
	}

	/**
	 * Creates a Layout instance from a Class Object.
	 * 
	 * @param layout The class object to create the instance of.
	 * @param graph The graph to use to instantiate the Layout.
	 * @return A new instance of the Layout.
	 */
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

	/**
	 * Performs the necessary actions to set up the Graph.
	 */
	protected void setup( ) {
		this.graph = ( UndirectedSparseGraph<V,E> )this.getGraphLayout( ).getGraph( );
		this.getRenderContext( ).setVertexLabelTransformer( new ToStringLabeller<V>( ));
//		this.getRenderContext( ).setEdgeLabelTransformer( new ToStringLabeller<E>( ));
		this.getRenderer( ).getVertexLabelRenderer( ).setPosition( Position.CNTR );
		DefaultModalGraphMouse mouse = new DefaultModalGraphMouse( ) {
			public void mouseWheelMoved( MouseWheelEvent e ) {
				scale((float)Math.pow( 1.25, -e.getWheelRotation( )), e.getPoint( ));
			}
		};
		mouse.setMode( ModalGraphMouse.Mode.PICKING );
		this.setGraphMouse( mouse );
		this.getPickedVertexState( ).addItemListener( this );
		this.getPickedEdgeState( ).addItemListener( this );
	}


	/**
	 * Sets a new Layout for the graph.
	 * 
	 * @param layout A Class object containing the Layout to be used.
	 */
	public void setGraphLayout( Class <? extends AbstractLayout> layout ){
			this.setGraphLayout(( Layout<V,E> )GraphVisualizer.getLayoutInstance( layout, this.graph ));
	}

	/**
	 * Sets a new Layout for the graph.
	 * 
	 * @param layout The Layout instance to use for the new Graph Layout.
	 */
	public void setGraphLayout( Layout <V,E> layout ){
		Layout<V,E> l = ( Layout<V,E> )this.getGraphLayout( );
		super.setGraphLayout( layout );
		this.graph = ( UndirectedSparseGraph<V,E> )this.getGraphLayout( ).getGraph( );
	}

	/**
	 * Starts the animation of the graph.
	 */
	public void animate( ) {
		this.animate( true );
	}

	/**
	 * Starts the animation of the graph.
	 * 
	 * @param enable True to stop animation of the Graph, false to stop.
	 */
	public void animate( boolean enable ) {
		System.err.print( (enable) ? "Starting " : "Stopping " );
		System.err.println( "animation..." );
		if ( this.layoutAnimator != null )
			this.layoutAnimator.stop( );
		if ( enable ) {
			this.layoutAnimator = new SpringLayoutAnimator( this.getGraphLayout( ));
			this.AnimThread = new Thread( this.layoutAnimator );
			this.AnimThread.start( );
		}
	}

	/**
	 * Resets the graph Layout to it's initial state.
	 */
	public void resetLayout( ) {
		this.getGraphLayout( ).reset( );
	}

	/**
	 * Sets all Graph nodes as selected.
	 */
	public void selectAll( ) {
		PickedState <V> state = this.getPickedVertexState( );
		Collection <V> vertices = this.graph.getVertices( );
		for( V v : vertices ) {
			state.pick( v, true );
		}

		PickedState <E> edgeState = this.getPickedEdgeState( );
		Collection <E> edges = this.graph.getEdges( );
		for ( E e : edges ) {
			edgeState.pick( e, true );
		}
	}

	/**
	 * Clear the selection of all nodes.
	 */
	public void clearSelection( ) {
		PickedState <V> state = this.getPickedVertexState( );
		Collection <V> pickedVertices = new Vector<V>( state.getPicked( ));
		for( V v : pickedVertices ) {
			state.pick( v, false );
		}
		PickedState <E> edgeState = this.getPickedEdgeState( );
		Collection <E> pickedEdges = new Vector<E>( edgeState.getPicked( ));
		for( E e : pickedEdges ) {
			edgeState.pick( e, false );
		}
	}

	/**
	 * Scales the graph view by the given amount.
	 * 
	 * @param amount The multiplier to apply to the scaling.
	 */
	public void scale( float amount ) {
		this.scale( amount, this.getCenterPoint( ));
	}

	/**
	 * Scales the gaph view by the givem amount, centered on the 
	 * given point.
	 * 
	 * @param amount The multiplier to apply to the scaling.
	 * @param center The center point for the scaling operation.
	 */
	public void scale( float amount, Point2D center ) {
//		this.viewScaler.scale( this, amount, center );
		this.zoomTo( currentZoom * amount, center );
	}

	/**
	 * Zooms to the given graph level, 1.0 being 100%.
	 * 
	 * @param level The level to zoom to.
	 */
	public void zoomTo( float level ) {
		this.zoomTo( level, this.getCenterPoint( ));
	}

	/**
	 * Zooms to the given graph level, 1.0 being 100%.
	 * 
	 * @param level The level to zoom to.
	 * @param center The center point for the scaling operation.
	 */
	public void zoomTo( float level, Point2D center ) {
		this.currentZoom = Math.max( minimumZoom, level );
		this.absoluteViewScaler.scale( this, level, center );
	}

	/**
	 * Centers the graph in the display.
	 */
	public void center( ) {
			// toggle the scrollbars back and forth to center the image
			JScrollBar sb = scrollPane.getHorizontalScrollBar( );
			sb.setValue( sb.getMaximum( ));
			sb.setValue( sb.getMinimum( ));
			sb.setValue(( sb.getMaximum( ) + sb.getMinimum( )) / 2 );
			sb = scrollPane.getVerticalScrollBar( );
			sb.setValue( sb.getMaximum( ));
			sb.setValue( sb.getMinimum( ));
			sb.setValue( (sb.getMaximum( ) + sb.getMinimum( )) / 2 );
	}
	
	/**
	 * Resets the zoom level to 100% and centers the viewport.
	 */
	public void resetView( ) {
		this.zoomTo( 1.0f );
		this.center( );
	}

	/**
	 * Gets the GraphZoomScrollPane associated with this viewer.
	 * 
	 * @return A GraphZoomScrollPane which contains this GraphVisualizer.
	 */
	public GraphZoomScrollPane getScrollPane( ) {
		if ( this.scrollPane == null )
			this.scrollPane = new GraphZoomScrollPane( this );
		return this.scrollPane;
	}

	/**
	 * Get the center point for the graph.
	 * 
	 * @return The center point of this graph as a Point2D.
	 */
	public Point2D getCenterPoint( ) {
		Dimension size = this.getGraphLayout( ).getSize( );
		return new Point2D.Double( size.width / 2.0, size.height / 2.0 );
	}

	public void addPickedVertexStateChangeListener( PickedStateChangeListener<V> l ) {
		this.pickedVertexStateChangeListeners.add( l );
	}

	public void addPickedEdgeStateChangeListener( PickedStateChangeListener<E> l ) {
		this.pickedEdgeStateChangeListeners.add( l );
	}

	private void firePickedVertexChangeEvent( V item, boolean picked ) {
		PickedStateChangeEvent<V> event = new PickedStateChangeEvent<V>( this, item, picked );
		for ( PickedStateChangeListener<V> p : this.pickedVertexStateChangeListeners ) {
			p.stateChanged( event );
		}
	}

	private void firePickedEdgeChangeEvent( E item, boolean picked ) {
		PickedStateChangeEvent<E> event = new PickedStateChangeEvent<E>( this, item, picked );
		for ( PickedStateChangeListener<E> p : this.pickedEdgeStateChangeListeners ) {
			p.stateChanged( event );
		}
	}

	public void itemStateChanged( ItemEvent event ) {
		Object source = event.getItem( );
		try {
			firePickedVertexChangeEvent( (V)source, event.getStateChange( ) == ItemEvent.SELECTED );
		} catch( ClassCastException e ) {
			firePickedEdgeChangeEvent( (E)source, event.getStateChange( ) == ItemEvent.SELECTED );
		}

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

