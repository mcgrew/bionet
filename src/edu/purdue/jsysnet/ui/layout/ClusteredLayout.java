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

import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.PolarPoint2D;

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
public class ClusteredLayout extends RandomLayout<Molecule,Correlation> {

	protected int maxIterations;
	protected int currentIteration;
	protected final double attractionStep = 0.2;
	protected final double repulsionStep = 0.05;

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
		super.initialize( );
		this.maxIterations = Math.max( this.size.height, this.size.width );
		this.currentIteration = 0;
		while ( !this.done( ))
			this.step( );
	}

	public void step( ) {
		double layoutSize = Math.min( this.size.height, this.size.width );
		Collection<Molecule> vertices = this.graph.getVertices( );
		for( Molecule vertex1 : vertices ) {
			PolarPoint2D v1Location = new PolarPoint2D( this.locations.get( vertex1 ));
			for ( Molecule vertex2 : vertices ) {
				if ( vertex1 != vertex2 ) {
					Point2D v2Location = this.locations.get( vertex2 );
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
							Math.max( 10, Math.min( this.size.width - 10, v1Location.getX( ))),
							Math.max( 10, Math.min( this.size.height - 10, v1Location.getY( ))));
					}
				}
			}
			this.setLocation( vertex1, v1Location );
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
	 * Returns the attraction force between two vertices. 
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex. 
	 * @return The attraction force.
	 */
	protected double getAttraction( Molecule v1, Molecule v2 ) {
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
	protected double getRepulsion( Molecule v1, Molecule  v2 ) {
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
	protected double getOptimum( Molecule v1, Molecule v2 ) {
		return this.getRepulsion( v1, v2 ) - this.getAttraction( v1, v2 );
	}
	
	/**
	 * Resets the layout to its initial state.
	 */
	public void reset( ) {
		this.initialize( );
	}

}


