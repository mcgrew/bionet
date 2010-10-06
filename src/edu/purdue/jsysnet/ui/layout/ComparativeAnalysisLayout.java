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
import edu.purdue.jsysnet.util.MoleculeGroup;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.ListIterator;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.Shape;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class ComparativeAnalysisLayout extends AbstractLayout<Molecule,Object> {
	private List<Molecule> arcPath;
	private double radius;
	private Collection <Experiment> experiments;
	List<String> moleculeIds; 

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
			arcPath = new ArrayList<Molecule>( );

			// build a list of the valid MoleculeGroups and Molecule ids
			Collection <String> groups = new TreeSet <String>( );
			HashMap<String,Collection<String>> moleculeMap = 
				new HashMap<String,Collection<String>>( );
			experiments = new TreeSet<Experiment>( );
			double height = d.getHeight();
			double width = d.getWidth();
			// initialize the base radius if it isn't already set.
			if ( Double.compare( this.radius, 0.0 ) <= 0 )
				this.radius = Math.min( height, width ) * 0.4;

			for ( Molecule m : this.getGraph( ).getVertices( )) {
				groups.add(( m.getGroup( )));
				if ( !moleculeMap.containsKey( m.getGroup( )))
					moleculeMap.put( m.getGroup( ), new TreeSet<String>( ));
				moleculeMap.get( m.getGroup( )).add( m.getAttribute( "id" ));
				experiments.add( m.getExperiment( ));
			}
			
			double thisRadius;
			int i=0, j, k, x, y;
			Molecule vertex;
			Point2D.Double center = new Point2D.Double( width/2.0, height/2.0 );
			PolarPoint2D coord = new PolarPoint2D( 0, 0, center );
			double theta;
			double sectionTheta = ( 2 * Math.PI / groups.size( ));
			this.arcPath = new ArrayList<Molecule>( );
			for ( Experiment exp : experiments ) {
				thisRadius = ( Math.min( height, width )) * ( 0.04 * i ) + this.radius;
				j=0;
				for ( String key : groups ) {
					k=0;
					Collection<String> idList = moleculeMap.get( key );
					for ( String id : idList ) {
						theta = ( sectionTheta * j ) + ( sectionTheta * (k+1) /( idList.size( ) + 2 ));
						MoleculeGroup mg = exp.getMoleculeGroup( key );
						coord.setLocation( thisRadius, theta, PolarPoint2D.POLAR );
						if (( vertex = mg.getMolecule( id )) != null ) {
							this.setLocation( vertex, coord );

							// Add this vertex as the next in the arc trace.
							arcPath.add( vertex );
						}
						k++;
					}
					j++;
				}
				i++;
			}
		}
	}

	public PathIterator getLabelPath( ) {
		Path2D labelPath = new Path2D.Double( );
		Collection<String> ids = new TreeSet<String>( );
		Dimension d = this.getSize( );
		double width = d.getWidth( );
		double height = d.getHeight( );
		PolarPoint2D coords = new PolarPoint2D( 0, 0, width/2, height/2 );
		Collection<Molecule> moleculeSet = new TreeSet<Molecule>( );
		moleculeSet.addAll( this.getGraph( ).getVertices( ));
		Collection<String> labelled = new HashSet<String>( );
		boolean pathStarted = false;
		for ( Molecule m : moleculeSet ) {
			if ( labelled.add( m.getAttribute( "id" ))) {
				coords.setLocation( locations.get( m ).getX( ), locations.get( m ).getY( ));
				coords.setLocation( 
					Math.min( width, height ) * ( 0.04 * this.experiments.size( ) - 0.02 ) + this.radius,
					coords.theta, PolarPoint2D.POLAR );
				if ( pathStarted ) {
					labelPath.lineTo( coords.x, coords.y );
				} else {
					pathStarted = true;
					labelPath.moveTo( coords.x, coords.y );
				}
			}
		}
		return labelPath.getPathIterator( null, Integer.MAX_VALUE );
	}

	public PathIterator getArcs( ) {
		return new ArcIterator( );
	}

	private class ArcIterator implements PathIterator {
		private ListIterator <Molecule> iterator;
		private MoleculeGroup lastMoleculeGroup = null;
		private Molecule currentMolecule = null;

		public ArcIterator( ) {
			this.iterator = arcPath.listIterator( );
			if ( iterator.hasNext( ));
				this.currentMolecule = this.iterator.next( );
		}

		public int currentSegment( double [] coords ) {
			coords[0] = locations.get( this.currentMolecule ).getX( );
			coords[1] = locations.get( this.currentMolecule ).getY( );
			if ( this.lastMoleculeGroup == this.currentMolecule.getMoleculeGroup( ))
				return PathIterator.SEG_LINETO;
			else
				return PathIterator.SEG_MOVETO;
		}
	
		public int currentSegment( float [] coords ) {
			coords[0] = (float)locations.get( this.currentMolecule ).getX( );
			coords[1] = (float)locations.get( this.currentMolecule ).getY( );
			if ( this.lastMoleculeGroup == this.currentMolecule.getMoleculeGroup( ))
				return PathIterator.SEG_LINETO;
			else
				return PathIterator.SEG_MOVETO;
		}
	
		public int getWindingRule( ) {
			return PathIterator.WIND_EVEN_ODD;	
		}
	
		public boolean isDone( ) {
			return !this.iterator.hasNext( );
		}
	
		public void next( ) {
			this.lastMoleculeGroup = this.currentMolecule.getMoleculeGroup( );
			this.currentMolecule = this.iterator.next( );
		}
	}
}

