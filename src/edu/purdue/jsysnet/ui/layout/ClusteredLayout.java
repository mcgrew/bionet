package edu.purdue.jsysnet.ui.layout;

import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Molecule;

import java.awt.geom.Point2D;
import java.awt.Dimension;
import java.util.Collection;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import org.apache.commons.collections15.Transformer;


/**
 * A JUNG Graph Layout which clusters molecules that are closely
 * correlated together and separates those which are not.
 * 
 * @author Thomas McGrew
 * @version 1.0
 */
public class ClusteredLayout extends AbstractLayout<Molecule,Correlation> {

	protected int maxIterations;
	protected int currentIteration;
	protected int multiplier;

	/**
	 * Constructor.
	 * @param graph A JUNG Graph
	 */
	public ClusteredLayout( Graph<Molecule,Correlation> graph ) {
		super( graph );
	}

	/**
	 * Initializes the location of the Graph vertices.
	 */
	public void initialize( ) {
		this.multiplier = Math.max( this.size.height, this.size.width ) / 2;
		this.maxIterations = this.multiplier * 2;
		this.currentIteration = 0;
		// put all vertices somewhere random;
		for ( Molecule vertex : this.graph.getVertices( )) {
			this.locations.get( vertex ).setLocation( Math.random( ) * this.size.width, 
				                                        Math.random( ) * this.size.height);
		}
		while ( !this.done( ))
			this.step( );
	}

	public void step( ) {
		Point2D v,w;
		double x, y, r, theta, newR;
		int compare;
		boolean moved = true;
		Molecule first;
		moved = false;
		Collection <Molecule> moleculeList = this.graph.getVertices( );
		Correlation e;
		for( Molecule m : moleculeList) {
			for ( Molecule n : moleculeList ) {
				e = m.getCorrelation( n );
				if ( e == null )
					continue;
				newR = this.multiplier * ( 1.1 - (e.getValue( )));
				// randomize the order of the vertices
				v = this.locations.get( first = ( Math.random( ) < 0.5 ) ?  m : n );
				w = this.locations.get( e.getOpposite( first ));
				// calculate polar coordinates of w assuming origin of v
				x = w.getX( ) - v.getX( );
				y = w.getY( ) - v.getY( );
				r = Math.hypot( x, y );
				theta = Math.atan2( y, x );	
				// move w to it's new location, slightly closer to where it should be.
				compare = Double.compare( newR, r );
				if ( compare != 0 ) {
					moved = true;
					x = v.getX( ) + ( r+compare ) * Math.cos( theta );
					y = v.getY( ) + ( r+compare ) * Math.sin( theta );
					// if the vertex goes off the screen, move it to the middle.
					if ( x < 10 ) x = 10;
					if ( x > this.size.width - 10 ) x = this.size.width - 10;
					if ( y < 10 ) y = 10;
					if ( y > this.size.height - 10 ) y = this.size.height - 10;
					w.setLocation( x, y );
				}
			}
		}
		this.currentIteration++;
	}

	public boolean done( ){ 
		return ( this.currentIteration >= this.maxIterations );
	}

	public void setSize( Dimension size ){
		super.setSize( size );
		this.reset( );
	}
	
	/**
	 * Calls this.initialize( )
	 */
	public void reset( ) {
		this.initialize( );
	}

}


