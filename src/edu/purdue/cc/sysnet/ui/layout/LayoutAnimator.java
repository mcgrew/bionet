/*

This file is part of SysNet.

SysNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SysNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SysNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.sysnet.ui.layout;

import edu.purdue.cc.sysnet.util.PolarPoint2D;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.LayoutDecorator;
import edu.uci.ics.jung.graph.Graph;

import java.util.HashMap;
import java.util.Collection;
import java.util.Vector;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;


/**
 * An abstract class for animating the JUNG layouts.
 */
public abstract class LayoutAnimator<V,E> implements Runnable {
	
	protected AbstractLayout<V,E> layout;
	protected ObservableCachingLayout<V,E> observableLayout;
	protected Graph graph;
	protected boolean stopped = true;
	protected double attractionStep = 0.2;
	protected double repulsionStep = 0.05;
	protected ArrayList<ChangeListener> animationListeners =
		new ArrayList<ChangeListener>( );

	/**
	 * Constructs a LayoutAnimator object.
	 * 
	 * @param layout The layout to be animated.
	 */
	public LayoutAnimator( Layout<V,E> layout ) {
		this.observableLayout = (ObservableCachingLayout)layout;
		while ( !AbstractLayout.class.isAssignableFrom( layout.getClass( ))) 
			layout = ((LayoutDecorator<V,E>)layout).getDelegate( );
		this.layout = ( AbstractLayout )layout;
		this.graph = this.layout.getGraph( );
	}

	/**
	 * The run( ) method for the Runnable interface - starts the animation thread.
	 * 
	 */
	public void run( ) {
		
		this.stopped = false;
		this.fireChangeListeners( );
		while( !this.isStopped( )) {
			synchronized ( this.observableLayout.getGraph( )) {
				this.step( );
			}
			try {
				Thread.sleep( 20 );
			} catch ( Exception e ) {
				Logger.getLogger( getClass( )).error( e );
			}
		}
		this.fireChangeListeners( );
	}

	/**
	 * Performs one step in the layout animation.
	 * 
	 */
	protected void step ( ) {
		HashMap<V,PolarPoint2D> map = this.getMap( );
		HashMap<V,PolarPoint2D> newMap = this.getMap( );
		Collection<V> vertices = map.keySet( );
		for( V vertex1 : vertices ) {
			PolarPoint2D v1Location = map.get( vertex1 );
			for ( V vertex2 : vertices ) {
				if ( this.isRepulsedBy( vertex1, vertex2 )) {
					PolarPoint2D v2Location = map.get( vertex2 );
					v1Location.setOrigin( v2Location ); 
					double displacement = -this.getRepulsion( vertex1, vertex2 );
					newMap.get( vertex2 ).move(
						displacement, v1Location.getTheta( ), PolarPoint2D.POLAR );

				}
			}
		}
		for ( E edge : new Vector<E>( this.getEdges( ))) {
			// do something.
			V [] pair = (V[])this.graph.getIncidentVertices( edge ).toArray( );
			if ( this.isAttractedBy( pair[ 0 ], pair[ 1 ])) {
				PolarPoint2D vLocation = map.get( pair[ 0 ]);
				vLocation.setOrigin( map.get( pair[ 1 ]));
				double displacement = this.getAttraction( pair[ 0 ], pair[ 1 ]);
				newMap.get( pair[ 1 ] ).move( 
					displacement, vLocation.getTheta( ), PolarPoint2D.POLAR );

			}
		}
		updateLocations( newMap );
	}

	/**
	 * Updates the positions of the vertices on the graph based on the passed in map.
	 * 
	 * @param newMap A HashMap<V,PolarPoint3D> containing the new locations for the vertices.
	 */
	protected void updateLocations( HashMap<V,PolarPoint2D> newMap ) {
		double height = this.observableLayout.getSize( ).height;
		double width = this.observableLayout.getSize( ).width;
		for ( V vertex : newMap.keySet( )) {
			Point2D vLocation = newMap.get( vertex );
			vLocation.setLocation( 
				Math.max( 10, Math.min( width - 10, vLocation.getX( ))),
				Math.max( 10, Math.min( height - 10, vLocation.getY( ))));

			// update the delegate layout to avoid a repaint
			this.layout.setLocation( vertex, vLocation );
		}
		// let the observableLayout know things have changed (repaint).
		this.observableLayout.fireStateChanged( );
	}

	/**
	 * Stops the animation.
	 * 
	 */
	public void stop( ) {
		this.stopped = true;
	}

	/**
	 * Returns true if the animation has been stopped.
	 * 
	 * @return true if the animation is stopped.
	 */
	public boolean isStopped( ) {
		return this.stopped;
	}

	/**
	 * Returns a HasMap of each vertex in the graph and it's location as a 
	 * PolarPoint2D. Each PolarPoint2D has it's origin at (0,0)
	 * 
	 * @return A HashMap containing the vertices and their locations.
	 */
	protected HashMap<V,PolarPoint2D> getMap( ) {
		Collection<V> vertices = new Vector( this.layout.getGraph( ).getVertices( ));
		HashMap <V,PolarPoint2D> map = new HashMap<V,PolarPoint2D>( );
		for( V v : vertices ){
			map.put( v, new PolarPoint2D( layout.getX( v ), layout.getY( v )));
		}
		return map;
	}

	/**
	 * Returns all edges contained in the graph.
	 * 
	 * @return A collection containing the edges.
	 */
	protected Collection<E> getEdges( ) {
		return this.graph.getEdges( );
	}

	/**
	 * Determines whether the 2 vertices are repulsed by one another.
	 * Any methods overriding this method should not be order-dependent.
	 * This implementation returns true unless v1 == v2.
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @return True if the 2 vertices are affected by one another.
	 */
	protected boolean isRepulsedBy( V v1, V v2 ) {
		return v1 != v2;
	}

	/**
	 * Determines whether the 2 vertices are attracted to one another.
	 * Any methods overriding this method should not be order-dependent.
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @return True if the 2 vertices are affected by one another.
	 */
	protected boolean isAttractedBy( V v1, V v2 ) {
		return this.graph.isNeighbor( v1, v2 );
	}

	/**
	 * Returns the attraction force between two vertices. When repulsion - attraction
	 * is negative, the vertices are not affected.
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex. 
	 * @return The attraction force.
	 */
	protected abstract double getAttraction( V v1, V v2 );

	/**
	 * Returns the repulsion force between 2 vertices. In the default implementation this
	 * is a constant.
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex. 
	 * @return The repulsion force between two vertices.
	 */
	protected abstract double getRepulsion( V v1, V v2 );

	public void addAnimationListener( ChangeListener c ) {
		this.animationListeners.add( c );
	}

	protected void fireChangeListeners( ) {
		for ( ChangeListener c : this.animationListeners ) {
			c.stateChanged( new ChangeEvent( this ));
		}
	}

}


