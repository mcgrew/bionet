/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Dec 4, 2003
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

	public void reset() {
		initialize();
	}

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
			
			int columns = (int)Math.ceil( Math.sqrt( groups.size( )));
			int gridWidth = (int)width / columns;
			int gridHeight = (int)height / columns;
			this.radius = (gridHeight < gridWidth ? gridHeight : gridWidth) * 0.38;

			int j = 0, x, y;
			Point2D center = new Point2D.Double( );
			ArrayList<V> a;
			PolarPoint2D coord = new PolarPoint2D( );
			for ( String key : groups.keySet( )) {
				a = groups.get( key );
				x = (int)(( j % columns ) * gridWidth + ( gridWidth / 2 ));
				y = (int)(( j / columns ) * gridHeight + ( gridHeight / 2 ));
				j++;
				center.setLocation( x, y );
				int i = 0;
				for (V v : a)
				{
	
					double angle = (2 * Math.PI * i) / a.size();
	
					coord.setLocation( this.radius, angle, PolarPoint2D.POLAR );
					coord.move( center );
					this.setLocation( v, coord );
	
//					CircleVertexData data = getCircleData(v);
//					data.setAngle(angle);
						i++;
				}
			}
		}
	}
}
