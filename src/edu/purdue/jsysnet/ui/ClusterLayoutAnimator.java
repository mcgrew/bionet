package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.PolarPoint2D;
import edu.purdue.jsysnet.util.Molecule;

import edu.uci.ics.jung.algorithms.layout.Layout;

import java.util.HashMap;
import java.util.Collection;



/**
 * A class for Animating Laoyouts which will Cluster correlated molecules together
 * based on their Correlation value.
 * 
 */
public class ClusterLayoutAnimator<V,E> extends LayoutAnimator<V,E> {

	/**
	 * Constructs a ClusterLayoutAnimator object
	 * 
	 */
	public ClusterLayoutAnimator( Layout<V,E> layout ) {
		super( layout );
	}

	/**
	 * Returns the attraction force between two vertices. When attraction - repulsion
	 * is negative, the vertices are not affected. A 
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex. 
	 * @return The attraction force.
	 */
	protected double getAttraction( V v1, V v2 ) {
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
	protected double getRepulsion( V v1, V v2 ) {
		return 0.8;
	}
}


