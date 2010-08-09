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
import java.util.LinkedList;
import java.util.Vector;
import java.util.HashMap;
import java.util.List;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.InputEvent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Color;
import java.awt.Paint;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

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
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

import org.apache.commons.collections15.Transformer;

/**
 * A class for visualizing a network graph. 
 */
	public class GraphVisualizer<V,E> extends VisualizationViewer<V,E> implements Graph<V,E>,ItemListener, MouseListener, Scalable {
	protected Graph<V,E> graph = new UndirectedSparseGraph<V,E>( );
	private LayoutAnimator layoutAnimator;
	private Thread AnimThread;
	private AbsoluteCrossoverScalingControl absoluteViewScaler = 
		new AbsoluteCrossoverScalingControl( );
	private JScrollPane scrollPane;
	private float currentZoom = 1.0f;
	private static float minimumZoom = 0.99f;
	private DijkstraShortestPath<V,E> dijkstra;

	private LinkedList <PickedStateChangeListener<V>> pickedVertexStateChangeListeners =
		new LinkedList<PickedStateChangeListener<V>>( );
	private LinkedList <PickedStateChangeListener<E>> pickedEdgeStateChangeListeners =
		new LinkedList<PickedStateChangeListener<E>>( );
	private LinkedList<GraphItemChangeListener<V>> vertexChangeListeners = new LinkedList<GraphItemChangeListener<V>>( );
	private LinkedList<GraphItemChangeListener<E>> edgeChangeListeners = new LinkedList<GraphItemChangeListener<E>>( );
	private LinkedList<ChangeListener> animationListeners = new LinkedList<ChangeListener>( );
	private LinkedList<GraphMouseListener<E>> graphMouseEdgeListeners = new LinkedList<GraphMouseListener<E>>( );
	protected Paint vertexPaint = Color.ORANGE;
	protected Paint pickedVertexPaint = Color.YELLOW;
	protected Paint edgePaint = Color.GREEN;
	protected Paint pickedEdgePaint = Color.BLACK;
	protected Color pickedLabelColor = Color.BLUE;

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
		PluggableGraphMouse mouse = new PluggableGraphMouse( ) {
			public void mouseWheelMoved( MouseWheelEvent e ) {
				scale((float)Math.pow( 1.25, -e.getWheelRotation( )), e.getPoint( ));
			}
		};
		mouse.add( new PickingGraphMousePlugin( ));
		mouse.add( new PickingGraphMousePlugin( 
			-1, InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK ));
		this.setGraphMouse( mouse );
		this.getPickedVertexState( ).addItemListener( this );
		this.getPickedEdgeState( ).addItemListener( this );
		this.addMouseListener( this );

		// set up coloring
		Transformer v = new Transformer<V,Paint>( ) {
			public Paint transform( V v ) {
				if ( getPickedVertexState( ).isPicked( v )) {
					return pickedVertexPaint;
				} else {
					return vertexPaint;
				}
			}
		};
		this.getRenderContext( ).setVertexFillPaintTransformer( v );
		this.getRenderContext( ).setVertexDrawPaintTransformer( v );

		Transformer e = new Transformer<E,Paint>( ) {
			public Paint transform( E e ) {
				if ( getPickedEdgeState( ).isPicked( e )) {
					return pickedEdgePaint;
				} else {
					return edgePaint;
				}
			}
		};
		this.getRenderContext( ).setEdgeDrawPaintTransformer( e );

		this.setPickedLabelColor( this.pickedLabelColor );
	}

	/**
	 * Returns the underlying graph for this GraphVisualizer.
	 * 
	 * @return The underlying graph for this GraphVisualizer.
	 */
	public Graph getGraph( ) {
		return this.graph;
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
		if ( this.layoutAnimator != null )
			this.layoutAnimator.stop( );
		if ( enable ) {
			this.layoutAnimator = new FRLayoutAnimator( this.getGraphLayout( ));
			for ( ChangeListener c : animationListeners ) {
				this.layoutAnimator.addAnimationListener( c );
			}
			this.AnimThread = new Thread( this.layoutAnimator );
			this.AnimThread.start( );
		}
	}

	public void addAnimationListener( ChangeListener c ) {
		this.animationListeners.add( c );
		if ( this.layoutAnimator != null ) {
			this.layoutAnimator.addAnimationListener( c );
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
	 * @return The new zoom level.
	 */
	public float scale( float amount ) {
		return this.scale( amount, this.getCenterPoint( ));
	}

	/**
	 * Scales the graph view by the givem amount, centered on the 
	 * given point.
	 * 
	 * @param amount The multiplier to apply to the scaling.
	 * @param center The center point for the scaling operation.
	 * @return The new zoom level.
	 */
	public float scale( float amount, Point2D center ) {
		return this.scaleTo( currentZoom * amount, center );
	}

	/**
	 * Scales to the given graph level, 1.0 being 100%.
	 * 
	 * @param level The level to zoom to.
	 * @return The new zoom level.
	 */
	public float scaleTo( float level ) {
		return this.scaleTo( level, this.getCenterPoint( ));
	}

	/**
	 * Scales to the given zoom level, 1.0 being 100%, centered on the 
	 * given point.
	 * 
	 * @param level The level to zoom to.
	 * @param center The center point for the scaling operation.
	 * @return The new zoom level.
	 */
	public float scaleTo( float level, Point2D center ) {
		float oldZoom = this.currentZoom;
		this.currentZoom = Math.max( minimumZoom, level );
//		this.absoluteViewScaler.scale( this, level, center );
		Dimension newSize = new Dimension( 
			(int)( this.scrollPane.getWidth( ) * currentZoom ),
			(int)( this.scrollPane.getHeight( ) * currentZoom ));
		this.setPreferredSize( newSize );
		this.setSize( newSize );
		new LayoutScaler( this.getGraphLayout( )).setSize( newSize );
		if ( Float.compare( level, 1.0f ) == 0 )
			this.center( );

		// translate the new view position so the mouse is in the same place
		// on the scaled view.
		JViewport vp = this.scrollPane.getViewport( );
		double centerX = center.getX( );
		double centerY = center.getY( );
		double viewPortMouseX = centerX - vp.getViewPosition( ).getX( );
		double viewPortMouseY = centerY - vp.getViewPosition( ).getY( );
		centerX *= currentZoom / oldZoom;
		centerY *= currentZoom / oldZoom;
		viewPortMouseX = centerX - viewPortMouseX;
		viewPortMouseY = centerY - viewPortMouseY;
		vp.setViewPosition( new Point( (int)viewPortMouseX, (int)viewPortMouseY ));

		return this.currentZoom;
	}

	// JUNGs built in layout scaling doesn't seem to work very well,
	// so here's my workaround implementation.
	/**
	 * A class for scaling a Layout externally.
	 */
	private class LayoutScaler {
		private AbstractLayout<V,E> layout;
		private ObservableCachingLayout<V,E> observableLayout;

		/**
		 * Creates a new LayoutScaler
		 * 
		 * @param layout The layout to be manipulated.
		 */
		public LayoutScaler( Layout layout ) {
			this.observableLayout = (ObservableCachingLayout)layout;
			while ( !AbstractLayout.class.isAssignableFrom( layout.getClass( ))) 
				layout = ((LayoutDecorator<V,E>)layout).getDelegate( );
			this.layout = ( AbstractLayout )layout;
		}

		/**
		 * Changes the size of the underlying Layout.
		 * 
		 * @param size The new size for the layout.
		 */
		private void setSize( Dimension size ) {
			// change the size of the layout without triggering the automatic resizing.
			double wScale = size.getWidth( ) / this.layout.getSize( ).getWidth( );
			double hScale = size.getHeight( ) / this.layout.getSize( ).getHeight( );
			this.layout.getSize( ).setSize( size );
			Collection<V> vertices = new Vector( this.layout.getGraph( ).getVertices( ));
			synchronized( graph ) {
				for ( V v : vertices ) {
					this.layout.setLocation( v, new Point2D.Double( 
						this.layout.getX( v ) * wScale,
						this.layout.getY( v ) * hScale ));
				}
			}
			this.observableLayout.fireStateChanged( );
		}

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
		this.scaleTo( 1.0f );
		this.center( );
	}

	/**
	 * Gets the ScrollPane associated with this viewer.
	 * 
	 * @return A ScrollPane which contains this GraphVisualizer.
	 */
	public JScrollPane getScrollPane( ) {
		if ( this.scrollPane == null ) {
			this.scrollPane = new JScrollPane( this );
			this.scrollPane.setWheelScrollingEnabled( false );
		}
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

	/**
	 * Sets the Paint to be used for filling vertices on the graph.
	 * 
	 * @param p The new Paint to use.
	 */
	public void setVertexPaint( Paint p ) {
		this.vertexPaint = p;
	}

	/**
	 * Gets the current paint that is being used to render vertices.
	 * 
	 * @return The Paint currently being used to render vertices.
	 */
	public Paint getVertexPaint( ) {
		return this.vertexPaint;
	}

	public void setPickedVertexPaint( Paint p ) {
		this.pickedVertexPaint = p;
	}

	public Paint getPickedVertexPaint( ) {
		return this.pickedVertexPaint;
	}

	public void setEdgePaint( Paint p ) {
		this.edgePaint = p;
	}

	public Paint getEdgePaint( ) {
		return this.edgePaint;
	}

	public void setPickedEdgePaint( Paint p ) {
		this.pickedEdgePaint = p;
	}

	public Paint getPickedEdgePaint( ) {
		return this.pickedEdgePaint;
	}

	public void setPickedLabelColor( Color c ) {
		this.pickedLabelColor = c;
		this.getRenderContext( ).setVertexLabelRenderer( 
			new DefaultVertexLabelRenderer( c ));
	}

	public Color getPickedLabelColor( ) {
		return this.pickedLabelColor;
	}

	/**
	 * Adds a GraphMouseListener to listen for edge clicks on the graph.
	 * 
	 * @param l The GraphMouseListener to be added.
	 */
	public void addGraphMouseEdgeListener( GraphMouseListener<E> l ) {
		this.graphMouseEdgeListeners.add( l );
	}

	/**
	 * Notifies all GraphMouseListeners listening for Edge clicks that an edge has
	 * been clicked on.
	 * 
	 * @param edge The edge that was clicked on.
	 * @param event The MouseEvent which triggered this action.
	 */
	private void fireGraphMouseEdgeClickedEvent( E edge, MouseEvent event ) {
		for ( GraphMouseListener<E> g : graphMouseEdgeListeners ) {
			g.graphClicked( edge, event );
		}
	}

	/**
	 * Notifies all GraphMouseListeners listening for Edge clicks that an edge has
	 * been clicked on.
	 * 
	 * @param edge The edge that was clicked on.
	 * @param event The MouseEvent which triggered this action.
	 */
	private void fireGraphMouseEdgePressedEvent( E edge, MouseEvent event ) {
		for ( GraphMouseListener<E> g : graphMouseEdgeListeners ) {
			g.graphPressed( edge, event );
		}
	}

	/**
	 * Notifies all GraphMouseListeners listening for Edge clicks that an edge has
	 * been clicked on.
	 * 
	 * @param edge The edge that was clicked on.
	 * @param event The MouseEvent which triggered this action.
	 */
	private void fireGraphMouseEdgeReleasedEvent( E edge, MouseEvent event ) {
		for ( GraphMouseListener<E> g : graphMouseEdgeListeners ) {
			g.graphReleased( edge, event );
		}
	}

	/**
	 * Adds a PickedVertexStateChangeListener to the graph.
	 * 
	 * @param l The PickedStateChangeListener to add.
	 */
	public void addPickedVertexStateChangeListener( PickedStateChangeListener<V> l ) {
		this.pickedVertexStateChangeListeners.add( l );
	}

	/**
	 * Adds a PickedEdgeStateChangeListener to the graph.
	 * 
	 * @param l The PickedStateChangeListener to add.
	 */
	public void addPickedEdgeStateChangeListener( PickedStateChangeListener<E> l ) {
		this.pickedEdgeStateChangeListeners.add( l );
	}

	/**
	 * Fires a PickedStateChangeEvent for a change on a vertex.
	 * 
	 * @param item The item whose state changed.
	 * @param picked Whether or not the item is selected.
	 */
	private void firePickedVertexChangeEvent( V item, boolean picked ) {
		PickedStateChangeEvent<V> event = new PickedStateChangeEvent<V>( this, item, picked );
		for ( PickedStateChangeListener<V> p : this.pickedVertexStateChangeListeners ) {
			p.stateChanged( event );
		}
	}

	/**
	 * Fires a PickedStateChangeEvent for a change on an Edge.
	 * 
	 * @param item The item whose state changed.
	 * @param picked Whether or not the item is selected.
	 */
	private void firePickedEdgeChangeEvent( E item, boolean picked ) {
		PickedStateChangeEvent<E> event = new PickedStateChangeEvent<E>( this, item, picked );
		for ( PickedStateChangeListener<E> p : this.pickedEdgeStateChangeListeners ) {
			p.stateChanged( event );
		}
	}

	public void addVertexChangeListener( GraphItemChangeListener <V> g ) {
		this.vertexChangeListeners.add( g );
	}

	public void addEdgeChangeListener( GraphItemChangeListener <E> g ) {
		this.edgeChangeListeners.add( g );
	}

	private void fireVertexChangeEvent( V item, int action ) {
		GraphItemChangeEvent <V> event = new GraphItemChangeEvent<V>( this, item, action );
		for ( GraphItemChangeListener <V> v : vertexChangeListeners ) {
			v.stateChanged( event );
		}
	}

	private void fireEdgeChangeEvent( E item, int action ) {
		GraphItemChangeEvent <E> event = new GraphItemChangeEvent<E>( this, item, action );
		for ( GraphItemChangeListener <E> e : edgeChangeListeners )
			e.stateChanged( event );
	}

	/**
	 * The itemStateChanged method of the ItemListener interface.
	 * 
	 * @param event The event which triggered this ItemListener.
	 */
	public void itemStateChanged( ItemEvent event ) {
		Object source = event.getItem( );
		try {
			firePickedVertexChangeEvent( (V)source, event.getStateChange( ) == ItemEvent.SELECTED );
		} catch( ClassCastException e ) {
			firePickedEdgeChangeEvent( (E)source, event.getStateChange( ) == ItemEvent.SELECTED );
		}

	}

	// MouseListener interface methods
	public void mouseClicked( MouseEvent event ) {
		E edge = this.getPickSupport( ).getEdge( this.getGraphLayout( ), event.getX( ), event.getY( ));
		V vertex = this.getPickSupport( ).getVertex( this.getGraphLayout( ), event.getX( ), event.getY( ));
		if ( edge != null && vertex == null ) {
			this.fireGraphMouseEdgeClickedEvent( edge, event );
		}
	}

	public void mousePressed( MouseEvent event ) {
		E edge = this.getPickSupport( ).getEdge( this.getGraphLayout( ), event.getX( ), event.getY( ));
		V vertex = this.getPickSupport( ).getVertex( this.getGraphLayout( ), event.getX( ), event.getY( ));
		if ( edge != null && vertex == null ) {
			this.fireGraphMouseEdgePressedEvent( edge, event );
		}
	}

	public void mouseReleased( MouseEvent event ) {
		E edge = this.getPickSupport( ).getEdge( this.getGraphLayout( ), event.getX( ), event.getY( ));
		V vertex = this.getPickSupport( ).getVertex( this.getGraphLayout( ), event.getX( ), event.getY( ));
		if ( edge != null && vertex == null ) {
			this.fireGraphMouseEdgeReleasedEvent( edge, event );
		}
	}

	public void mouseEntered( MouseEvent event ) { }
	public void mouseExited( MouseEvent event ) { }
	
	public List<E> getShortestPath( V v1, V v2 ) {
		if ( dijkstra == null ) {
			// create a new DijkstraShortestPath
			dijkstra = new DijkstraShortestPath<V,E>( this );
			// listen for edge changes and reset DijkstraShortestPath
			this.addEdgeChangeListener( new GraphItemChangeListener<E>( ) {
				public void stateChanged( GraphItemChangeEvent e ) {
					dijkstra.reset( );
			
				}
			});
		}
		try { 
			return dijkstra.getPath( v1, v2 );
		} catch ( NullPointerException e ) {
			return null;
		}
	}

	// Graph interface Methods
	public boolean addEdge( E e, V v1, V v2 ) {
		boolean returnValue = this.graph.addEdge( e, v1, v2 );
		this.repaint( );
		return returnValue;
	}

	public boolean addEdge( E e, V v1, V v2, EdgeType edgetype ) {
		boolean returnValue = this.graph.addEdge( e, v1, v2, edgetype );
		this.repaint( );
		return returnValue;
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
		boolean returnValue = this.graph.addEdge( edge, vertices );
		this.repaint( );
		this.fireEdgeChangeEvent( edge, GraphItemChangeEvent.ADDED );
		return returnValue;
	}

	public boolean addEdge( E edge, Collection<? extends V> vertices, EdgeType edge_type ) {
		boolean returnValue = this.graph.addEdge( edge, vertices, edge_type );
		this.repaint( );
		this.fireEdgeChangeEvent( edge, GraphItemChangeEvent.ADDED );
		return returnValue;
	}

	public boolean addVertex( V vertex ) {
		this.fireVertexChangeEvent( vertex, GraphItemChangeEvent.ADDED );
		boolean returnValue = this.graph.addVertex( vertex );
		this.repaint( );
		return returnValue;
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
		this.fireEdgeChangeEvent( edge, GraphItemChangeEvent.REMOVED );
		boolean returnValue = this.graph.removeEdge( edge );
		this.repaint( );
		return returnValue;
	}

	public boolean removeVertex( V vertex ) {
		this.fireVertexChangeEvent( vertex, GraphItemChangeEvent.REMOVED );
		boolean returnValue = this.graph.removeVertex( vertex );
		this.repaint( );
		return returnValue;
	}

	//UndirectedSparseGraph pass through Methods
	public boolean addEdge( E edge, Pair<? extends V> endpoints, EdgeType edgeType ) {
		this.fireEdgeChangeEvent( edge, GraphItemChangeEvent.ADDED );
		boolean returnValue = this.graph.addEdge( edge, endpoints, edgeType );
		this.repaint( );
		return returnValue;
	}

}

