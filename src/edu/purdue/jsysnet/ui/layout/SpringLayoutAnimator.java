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
import edu.purdue.jsysnet.util.Molecule;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;

import java.util.HashMap;
import java.util.Collection;



/**
 * A class for Animating Laoyouts which will Cluster correlated molecules together
 * based on their Correlation value.
 * 
 */
public class SpringLayoutAnimator<V,E> extends LayoutAnimator<V,E> {

	protected Graph graph;
	/**
	 * Constructs a ClusterLayoutAnimator object
	 * 
	 */
	public SpringLayoutAnimator( Layout<V,E> layout ) {
		super( layout );
		this.graph = layout.getGraph( );
	}

	/**
	 * Returns the attraction force between two vertices. When repulsion - attraction
	 * is negative, the vertices are not affected.
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex. 
	 * @return The attraction force.
	 */
	protected double getAttraction( V v1, V v2 ) {
		return Math.abs((( Molecule )v1).getCorrelation( (Molecule)v2 ).getValue( )) * 0.75;
	}

	/**
	 * Returns the repulsion force between 2 vertices.
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex. 
	 * @return The repulsion force between two vertices.
	 */
	protected double getRepulsion( V v1, V v2 ) {
		return 0.8;
	}
}


