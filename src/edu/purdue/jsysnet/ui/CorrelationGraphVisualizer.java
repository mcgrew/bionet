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

package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.MonitorableRange;
import edu.purdue.jsysnet.util.Experiment;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.EdgeType;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class CorrelationGraphVisualizer extends GraphVisualizer<Molecule,Correlation> implements ChangeListener {
	public MonitorableRange range;
	public Experiment experiment;

	public CorrelationGraphVisualizer( Experiment experiment, MonitorableRange range ) {
		super( );
		this.setRange( range );
		this.setExperiment( experiment );
	}

	public void setExperiment( Experiment experiment ) {
		this.experiment = experiment;
		this.addVertices( );
		this.addEdges( );
	}

	public Experiment getExperiment( ) {
		return this.experiment;
	}
	
	public void setRange( MonitorableRange range ) {
		if ( this.range != null )
			this.range.removeChangeListener( this );
		this.range = range;
		range.addChangeListener( this );
	}

	public MonitorableRange getRange( ) {
		return this.range;
	}

	/**
	 * Adds the Vertices (Molecules) to the Graph
	 */
	protected void addVertices( ) {
		for( Molecule molecule : this.experiment.getMolecules( ))
			this.graph.addVertex( molecule );
	}

	/**
	 * Adds the Edges (Correlations) to the Graph.
	 */
	protected void addEdges( ) {
		for( Correlation correlation : this.experiment.getCorrelations( )) {
			if ( this.isValidEdge( correlation )) {
				this.graph.addEdge( 
					correlation, 
					new Pair <Molecule> ( correlation.getMolecules( )),
					EdgeType.UNDIRECTED );
			}
		}
	}

	public int filterEdges( ) {
		
		int returnValue = 0;
		for( Correlation correlation : this.experiment.getCorrelations( )) {
			if ( this.isValidEdge( correlation )) {
				returnValue++;
				// this Correlation belongs on the graph, make sure it is there.
				if ( !this.graph.containsEdge( correlation )) {
					this.graph.addEdge( correlation, 
												 new Pair <Molecule> ( correlation.getMolecules( )),
												 EdgeType.UNDIRECTED );
				}
			}
			else {
				// this Correlation does not belong on the graph, make sure it is not there.
				if ( this.graph.containsEdge( correlation )) {
					this.getPickedEdgeState( ).pick( correlation, false );
					this.graph.removeEdge( correlation );
				}
			}
		}
		this.repaint( );
		return returnValue;
	}

	public boolean isValidEdge( Correlation correlation ) {
		Molecule [] molecules = correlation.getMolecules( );
		return ( this.graph.containsVertex( molecules[ 0 ] ) &&
						 this.graph.containsVertex( molecules[ 1 ] ) &&
						 this.range.contains( 
							 Math.abs( correlation.getValue( ))));
	}

	public void stateChanged( ChangeEvent event ) {
		this.filterEdges( );
	}

}
