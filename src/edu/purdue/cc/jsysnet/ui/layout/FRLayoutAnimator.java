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

package edu.purdue.cc.jsysnet.ui.layout;

import edu.purdue.cc.jsysnet.util.PolarPoint2D;
import edu.purdue.cc.jsysnet.util.Molecule;

import edu.uci.ics.jung.algorithms.layout.Layout;

import java.util.HashMap;
import java.util.Collection;
import java.awt.Dimension;



/**
 * A class for Animating Laoyouts which will Cluster correlated molecules together
 * based on the Fruchterman Reingold algorithm.
 *
 * @author Thomas McGrew
 * 
 */
public class FRLayoutAnimator<V,E> extends LayoutAnimator<V,E> {
	private final double C = 0.012;
	protected double t;

	/**
	 * Constructs a FRLayoutAnimator object
	 * 
	 */
	public FRLayoutAnimator( Layout<V,E> layout ) {
		super( layout );
		this.t = this.layout.getSize( ).width / 400;
	}

	/**
	 * Returns the attraction force between two vertices.
	 * ( V<sub>c</sub> &middot; d<sup>2</sup> ) / k
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex. 
	 * @return The attraction force.
	 */
	protected double getAttraction( V v1, V v2 ) {
		double k = this.getK( );	
		return ( k == 0 ) ? 0 : Math.pow( this.getDelta( v1, v2 ), 2 ) / k;
	}

	/**
	 * Returns the repulsion force between 2 vertices.
	 * k<sup>2</sup> / ( V<sub>c</sub> &middot; d )
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex. 
	 * @return The repulsion force between two vertices.
	 */
	protected double getRepulsion( V v1, V v2 ) {
		double delta = this.getDelta( v1, v2 );
		if ( Double.compare( delta, 0.0) == 0 ) { 
			delta = 0.001;
		}
		double returnValue = Math.pow( this.getK( ), 2 ) / delta;
		return returnValue;
	}

	/**
	 * Gets the distance &Delta; between two nodes.
	 * 
	 * @param v1 The first node.
	 * @param v2 The second node.
	 * @return The distance between two nodes as a double.
	 */
	private double getDelta( V v1, V v2 ) {
		double deltaX = Math.abs( layout.getX( v1 ) - layout.getX( v2 ));
		double deltaY = Math.abs( layout.getY( v1 ) - layout.getY( v2 ));
		return Math.sqrt( deltaX * deltaX + deltaY * deltaY );
	}

	/**
	 * Gets the k value for the algorithm.
	 * 
	 * @return The k value based on the graph size and number of vertices.
	 */
	private double getK( ) {
		Dimension size = this.layout.getSize( );
		int vertexCount = this.graph.getVertexCount( );
		return ( vertexCount == 0 ) ? 0 : C * Math.sqrt(
			( size.width * size.height ) / this.graph.getVertexCount( ));
	}

	/**
	 * Updates the positions of the vertices on the graph based on the passed in map.
	 * 
	 * @param newMap A HashMap<V,PolarPoint3D> containing the new locations for the vertices.
	 */
	protected void updateLocations( HashMap<V,PolarPoint2D> newMap ) {
		HashMap <V,PolarPoint2D> map = this.getMap( );
		for( V v : map.keySet( )) {
			// find out how far the new location is from the old.
			PolarPoint2D current = newMap.get( v );
			current.setOrigin( map.get( v ));
			// decrease the movement distance to t if necessary.
			current.setLocation( Math.min( this.t, current.getR( )), 
				current.getTheta( ), PolarPoint2D.POLAR );

		}
		super.updateLocations( newMap );
		t *= 0.997;
		if ( t <= 0.1 ) {
			this.stop( );
		}
	}
}


