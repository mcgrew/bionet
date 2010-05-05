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

/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 */
package edu.purdue.jsysnet.ui.layout;

import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.PolarPoint2D;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.algorithms.layout.*;



/**
 * A {@code Layout} implementation that positions vertices equally spaced on a regular circle.
 *
 */
public class MultipleCirclesLayout<V, E> extends AbstractLayout<V,E> {

	private double radius;
	
	/**
	 * Creates an instance for the specified graph.
	 */
	public MultipleCirclesLayout(Graph<V,E> g) {
		super(g);
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
		HashMap <String,ArrayList<V>> groups = new HashMap<String,ArrayList<V>>( );
		
		if (d != null) {
			double height = d.getHeight();
			double width = d.getWidth();

			String groupName;
			for ( V v : this.getGraph( ).getVertices( )) {
				groupName = (( Molecule )v).getGroup( );
				if ( !groups.containsKey( groupName )) {
					groups.put( groupName, new ArrayList<V>( ));
				}
				groups.get( groupName ).add( v );

			}
			
			this.radius = ( Math.min( height, width )) * 0.3;
			int groupRadius = (int)( this.radius / Math.sqrt( groups.size( )) );

			int j = 0, x, y;
			Point2D.Double graphCenter = new Point2D.Double( width/2.0, height/2.0 );
			PolarPoint2D center = new PolarPoint2D( 0, 0, graphCenter );
			PolarPoint2D coord = new PolarPoint2D( 0, 0, center );
			ArrayList<V> group;
			double theta;

			for ( String key : groups.keySet( )) {
				group = groups.get( key );
				theta = ( 2 * Math.PI * j ) / groups.size( );
				j++;
				center.setLocation( this.radius, theta, PolarPoint2D.POLAR );
				int i = 0;
				for ( V vertex : group )
				{
	
					theta = ( 2 * Math.PI * i ) / group.size();
	
					coord.setLocation( groupRadius, theta, PolarPoint2D.POLAR );
					this.setLocation( vertex, coord );
	
					i++;
				}
			}
		}
	}
}
