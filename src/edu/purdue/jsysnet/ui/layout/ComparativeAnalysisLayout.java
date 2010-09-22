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
import edu.purdue.jsysnet.util.Experiment;
import edu.purdue.jsysnet.util.Molecule;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class ComparativeAnalysisLayout extends AbstractLayout<Molecule,Object> {

	private double radius;
	private List <Experiment> experiments;

	/**
	 * Creates an instance for the specified graph.
	 */
	public ComparativeAnalysisLayout( Graph<Molecule,Object> graph ) {
		super( graph );
	}

	/**
	 * Returns the radius of the circle.
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Sets the radius of the circle.  Must be called before
	 * {@code initialize()} is called.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * Resets the position of the Graph Nodes.
	 */
	public void reset() {
		initialize();
	}

	/**
	 * Sets the initial position of the Graph nodes.
	 */
	public void initialize() {
		
		Dimension d = this.getSize();
		
		if (d != null) {
			for ( Object edge : new Vector<Object>( this.graph.getEdges( ))) {
				this.graph.removeEdge( edge );
			}
			Collection <String> groups = new HashSet <String>( );
			HashMap<String,Collection<String>> moleculeIds = 
				new HashMap<String,Collection<String>>( );
			Collection<Experiment> experiments = new HashSet<Experiment>( );
			double height = d.getHeight();
			double width = d.getWidth();
			if ( Double.compare( this.radius, 0.0 ) <= 0 )
				this.radius = Math.min( height, width ) * 0.2;

			for ( Molecule m : this.getGraph( ).getVertices( )) {
				groups.add(( m.getGroup( )));
				if ( !moleculeIds.containsKey( m.getGroup( )))
					moleculeIds.put( m.getGroup( ), new HashSet<String>( ));
				moleculeIds.get( m.getGroup( )).add( m.getAttribute( "id" ));
				experiments.add( m.getExperiment( ));
			}
			
			double thisRadius;
			int i=0, j, k, x, y;
			Molecule vertex;
			Point2D.Double center = new Point2D.Double( width/2.0, height/2.0 );
			PolarPoint2D coord = new PolarPoint2D( 0, 0, center );
			List<Molecule> group;
			double theta;
			double sectionTheta = ( 2 * Math.PI / groups.size( ));
			for ( String key : groups ) {
				Molecule [] last = new Molecule[ experiments.size( )];
				j=0;
				Collection<String> idList = moleculeIds.get( key );
				for ( String id : idList ) {
					theta = ( sectionTheta * i ) + ( sectionTheta * (j+1) /( idList.size( ) + 2 ));
					k=0;
					for ( Experiment exp : experiments ) {
						if (( vertex = exp.getMoleculeGroup( key ).getMolecule( id )) != null ) {
							thisRadius = ( Math.min( height, width )) * ( 0.075 * k ) + this.radius;
							coord.setLocation( thisRadius, theta, PolarPoint2D.POLAR );
							this.setLocation( vertex, coord );
							if ( last[ k ] != null )
								this.graph.addEdge( new Object( ), last[k], vertex, EdgeType.UNDIRECTED );
							last[ k ] = vertex;
						}
						k++;
					}
					j++;
				}
				i++;
			}
		}
	}
}

