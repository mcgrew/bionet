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

/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 */
package edu.purdue.cc.sysnet.ui.layout;

import edu.purdue.bbc.util.Pair;
import edu.purdue.bbc.util.Settings;
import edu.purdue.cc.sysnet.util.Molecule;
import edu.purdue.cc.sysnet.util.SampleGroup;
import edu.purdue.cc.sysnet.util.PolarPoint2D;
import edu.purdue.cc.sysnet.ui.CorrelationDisplayPanel;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.algorithms.layout.*;



/**
 * A {@code Layout} implementation that positions vertices equally spaced on a regular circle.
 *
 */
public class MultipleCirclesLayout<V,E> extends AbstractLayout<V,E> {

	private double radius;
	Pair<SampleGroup> sampleGroups;
	private double foldChange;
	
	/**
	 * Creates an instance for the specified graph.
	 */
	public MultipleCirclesLayout(Graph<V,E> g) {
		super(g);
		this.foldChange = Settings.getSettings( ).getDouble( 
			"preferences.correlation.foldChange", 2.0 );
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

	public void setSampleGroups( Pair<SampleGroup> sampleGroups ) {
		this.sampleGroups = sampleGroups;
	}

	/**
	 * Sets the initial position of the Graph nodes.
	 */
	public void initialize() {
		if ( this.sampleGroups != null ) {
			Dimension d = this.getSize();
			List<List<V>> moleculeGroups = new ArrayList<List<V>>( );
			for ( int i=0; i < 3; i++ ) {
				moleculeGroups.add( new ArrayList<V>( ));
			}
			if (d != null) {
				double height = d.getHeight();
				double width = d.getWidth();

				String groupName;
				for ( V v : this.getGraph( ).getVertices( )) {
					if ( this.isUpRegulated( sampleGroups, v ))
						moleculeGroups.get( 2 ).add( v );
					else if ( this.isDownRegulated( sampleGroups, v ))
						moleculeGroups.get( 1 ).add( v );
					else
						moleculeGroups.get( 0 ).add( v );
				}
				
				this.radius = ( Math.min( height, width )) * 0.3;
				int groupRadius = (int)( this.radius / Math.sqrt( moleculeGroups.size( )) );

				int j = 0, x, y;
				Point2D.Double graphCenter = new Point2D.Double( width/2.0, height/2.0 );
				PolarPoint2D center = new PolarPoint2D( 0, 0, graphCenter );
				PolarPoint2D coord = new PolarPoint2D( 0, 0, center );
				double theta;

				for ( List<V> group : moleculeGroups ) {
					theta = ( 2 * Math.PI * j ) / moleculeGroups.size( );
					j++;
					center.setLocation( this.radius, theta, PolarPoint2D.POLAR );
					int i = 0;
					for ( V vertex : group ) {
						theta = ( 2 * Math.PI * i ) / group.size();
						coord.setLocation( groupRadius, theta, PolarPoint2D.POLAR );
						this.setLocation( vertex, coord );
						i++;
					}
				}
			}
		}
	}

	private boolean isUpRegulated( Pair<SampleGroup> pair, V v ) {
		Molecule m = (Molecule)v;
		return m.getValues( pair.getFirst( )).getMean( ) /
			m.getValues( pair.getSecond( )).getMean( ) > this.foldChange;
	}

	private boolean isDownRegulated( Pair<SampleGroup> pair, V v ) {
		Molecule m = (Molecule)v;
		return m.getValues( pair.getSecond( )).getMean( ) /
			m.getValues( pair.getFirst( )).getMean( ) > this.foldChange;
	}
}

