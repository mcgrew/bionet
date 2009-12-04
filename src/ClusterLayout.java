
import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;


public class ClusterLayout<V,E> extends AbstractLayout<V,E> {

	public ClusterLayout( Graph<V,E> graph ) {
		super( graph );
	}

	public void initialize( ) {
		int iterations = Math.max( this.size.height, this.size.width ) * 2;
		int multiplier = Math.min( this.size.height, this.size.width ) / 2;
		V lastV = null;
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
		V first;
		for ( int i=0; moved && i < iterations; i++ ) {
			moved = false;
			for( E e : this.graph.getEdges( )) {
				newR = multiplier * ( 1.1 - ((Correlation)e).getValue( ));
				// randomize the order of the vertices
				v = this.locations.get( first = ( Math.random( ) < 0.5 ) ?
					                      this.graph.getEndpoints( e ).getFirst( ) :
																this.graph.getEndpoints( e ).getSecond( ));
				w = this.locations.get( this.graph.getOpposite( first, e ));
				// calculate polar coordinates of w assuming origin of v
				x = w.getX( ) - v.getX( );
				y = w.getY( ) - v.getY( );
				r = Math.hypot( x, y );
				theta = Math.atan2( y, x );	
				// move w to it's new location, slightly closer to where it should be.
				compare = Double.compare( newR, r );
				if (  compare != 0) {
					moved = true;
					x = v.getX( ) + ( r+compare ) * Math.cos( theta );
					y = v.getY( ) + ( r+compare ) * Math.sin( theta );
					if ( x < 0  || x > this.size.width ) x = this.size.width / 2;
					if ( y < 0 || y > this.size.height ) y = this.size.height / 2;
//					System.out.println( String.format( "x=%f, y=%f", x, y ));
					w.setLocation( x, y );
				}
			}
		}
	}
	
	public void reset( ) {
		this.initialize( );
	}

}


