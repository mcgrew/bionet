package edu.purdue.jsysnet.ui.layout;

import java.awt.geom.Point2D;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.purdue.jsysnet.util.Correlation;


/**
 * A JUNG Graph Layout which clusters molecules that are closely
 * correlated together and separates those which are not.
 * 
 * @author Thomas McGrew
 * @version 1.0
 */
public class RandomLayout<V,E> extends AbstractLayout<V,E> {

	/**
	 * Constructor.
	 * @param graph A JUNG Graph
	 */
	public RandomLayout( Graph<V,E> graph ) {
		super( graph );
	}

	/**
	 * Initializes the location of the Graph vertices.
	 */
	public void initialize( ) {
		int multiplier = Math.max( this.getSize( ).height, this.getSize( ).width ) / 2;
		int iterations = multiplier * 16;
		Point2D vPos, wPos;
		double x, y, r, theta, newR;
		int compare;
		Point2D v,w;
		boolean moved = true;
		// put all vertices somewhere random;
		for ( V vertex : this.graph.getVertices( )) {
			this.locations.get( vertex ).setLocation( Math.random( ) * this.size.width, 
				                                        Math.random( ) * this.size.height);
		}
	}
	
	/**
	 * Calls this.initialize( )
	 */
	public void reset( ) {
		this.initialize( );
	}

}


