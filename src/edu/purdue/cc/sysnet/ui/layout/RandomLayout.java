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

import java.awt.geom.Point2D;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.purdue.cc.sysnet.util.Correlation;


/**
 * A JUNG Graph Layout which clusters molecules that are closely
 * correlated together and separates those which are not.
 * 
 * @author Thomas McGrew
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
		int multiplier = 
			Math.max( this.getSize( ).height, this.getSize( ).width ) / 2;
		int iterations = multiplier * 16;
		Point2D vPos, wPos;
		double x, y, r, theta, newR;
		int compare;
		Point2D v,w;
		boolean moved = true;
		// put all vertices somewhere random;
		for ( V vertex : this.graph.getVertices( )) {
			this.locations.get( vertex ).setLocation( 
				Math.random( ) * this.size.width, 
				Math.random( ) * this.size.height );
		}
	}
	
	/**
	 * Calls initialize( )
	 */
	public void reset( ) {
		this.initialize( );
	}

}


