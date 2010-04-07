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

package edu.purdue.jsysnet.ui.layout;

import edu.purdue.jsysnet.util.PolarPoint2D;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.LayoutDecorator;

import java.util.HashMap;
import java.util.Collection;



/**
 * An abstract class for animating the JUNG layouts.
 */
public abstract class LayoutAnimator<V,E> implements Runnable {
	
	protected AbstractLayout<V,E> layout;
	protected ObservableCachingLayout<V,E> observableLayout;
	protected boolean stopped = false;
	protected final double attractionStep = 0.2;
	protected final double repulsionStep = 0.05;

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
	}

	/**
	 * The run( ) method for the Runnable interface - starts the animation thread.
	 * 
	 */
	public void run( ) {

		if ( !AbstractLayout.class.isAssignableFrom( this.layout.getClass( ))) {
			System.err.println( String.format( "Class '%s' is not usable by LayoutAnimator", layout.getClass( ).toString( )));
			return;
		}
		while( true ) {
			if ( this.stopped ) 
				return;
			synchronized ( this.observableLayout.getGraph( )) {
				this.step( );
			}
			try {
				Thread.sleep( 40 );
			} catch ( Exception e ) {
				e.printStackTrace( System.err );
			}
		}
	}

	/**
	 * Performs one step in the layout animation.
	 * 
	 */
	protected void step ( ) {
		double height = this.observableLayout.getSize( ).height;
		double width = this.observableLayout.getSize( ).width;
		double layoutSize = Math.min( height, width );
		HashMap<V,PolarPoint2D> map = this.getMap( );
		Collection<V> vertices = map.keySet( );
		for ( int i=0; i < 3; i++ ) { // move 3 steps each time this method is called.
			for( V vertex1 : vertices ) {
				PolarPoint2D v1Location = map.get( vertex1 );
				for ( V vertex2 : vertices ) {
					if ( vertex1 != vertex2 ) {
						PolarPoint2D v2Location = map.get( vertex2 );
						double optimum = this.getOptimum( vertex1, vertex2 ) * layoutSize;
						if ( optimum >= 0 ) {
							v1Location.setOrigin( v2Location ); 
							double newR = v1Location.getR( );
							if ( newR > optimum )
								newR -= this.attractionStep;
								else if ( newR < optimum )
									newR += this.repulsionStep;
							v1Location.setLocation( newR, v1Location.getTheta( ), PolarPoint2D.POLAR );
							v1Location.setLocation( 
								Math.max( 10, Math.min( width - 10, v1Location.getX( ))),
								Math.max( 10, Math.min( height - 10, v1Location.getY( ))));
						}
					}
				}
				// update the delegate layout to avoid a repaint
				this.layout.setLocation( vertex1, v1Location );
			}
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
	 * Returns a HasMap of each vertex in the graph and it's location as a 
	 * PolarPoint2D. Each PolarPoint2D has it's origin at (0,0)
	 * 
	 * @return A HashMap containing the vertices and their locations.
	 */
	protected HashMap<V,PolarPoint2D> getMap( ) {
		Collection<V> vertices = this.layout.getGraph( ).getVertices( );
		HashMap <V,PolarPoint2D> map = new HashMap<V,PolarPoint2D>( );
		for( V v : vertices ){
			map.put( v, new PolarPoint2D( layout.getX( v ), layout.getY( v )));
		}
		return map;
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

	/**
	 * Returns the optimum distance for 2 vertices. Ideally this should be between 0
	 * and 1, where 1 equals the minimum of the height and width of the layout.
	 * Normally this is repulsion minus attraction, but this method can be overriden
	 * if desired.
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex. 
	 * @return The optimum distance for the 2 vertices.
	 */
	protected double getOptimum( V v1, V v2 ) {
		return this.getRepulsion( v1, v2 ) - this.getAttraction( v1, v2 );
	}
}


