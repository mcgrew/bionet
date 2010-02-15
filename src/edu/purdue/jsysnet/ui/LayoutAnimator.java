package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.PolarPoint2D;
import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Settings;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.LayoutDecorator;

import java.util.HashMap;
import java.util.Collection;



/**
 * A class for animating the JUNG layouts.
 */
public class LayoutAnimator<V,E> implements Runnable {
	
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
		if ( Settings.DEBUG ) {
			System.err.println( "Animating Layout..." );
		}
		while( true ) {
			if ( stopped ) 
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
						double optimum = this.getOptimium( vertex1, vertex2 ) * layoutSize;
						if ( optiumum >= 0 ) {
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
	 * Returns the attraction force between two vertices. When attraction - repulsion
	 * is negative, the vertices are not affected. A 
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex. 
	 * @return The attraction force.
	 */
	protected double calcAttraction( V v1, V v2 ) {
		return (( Molecule )v1).getCorrelation( (Molecule)v2 ).getValue( ) * 0.75;
	}

	/**
	 * Returns the repulsion force between 2 vertices. In the default implementation this
	 * is a constant.
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex. 
	 * @return The repulsion force between two vertices.
	 */
	protected double calcRepulsion( V v1, V v2 ) {
		return 0.8;
	}

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

	public void stop( ) {
		this.stopped = true;
	}
}


