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
import edu.purdue.jsysnet.util.Spectrum;
import edu.purdue.jsysnet.util.SplitSpectrum;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;

import org.apache.commons.collections15.Transformer;

import java.awt.Paint;
import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class CorrelationGraphVisualizer extends GraphVisualizer<Molecule,Correlation> implements ChangeListener,GraphMouseListener<Correlation> {
	public MonitorableRange range;
	public Experiment experiment;
	protected Spectrum spectrum;

	public CorrelationGraphVisualizer( Experiment experiment, MonitorableRange range ) {
		super( );
		this.setRange( range );
		this.setExperiment( experiment );
		this.addGraphMouseEdgeListener( this );

		this.spectrum = new SplitSpectrum( range );
		this.spectrum.setOutOfRangePaint( Color.WHITE );
		Transformer e = new Transformer<Correlation,Paint>( ) {
			public Paint transform( Correlation e ) {
				if ( getPickedEdgeState( ).isPicked( e )) {
					return pickedEdgePaint;
				} else {
					return spectrum.getPaint( e.getValue( ));
				}
			}
		};
		this.getRenderContext( ).setEdgeDrawPaintTransformer( e );
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

	public void graphClicked( Correlation edge, MouseEvent event ) {
		if ( event.getButton( ) == MouseEvent.BUTTON1 ) {
			Molecule [] m = edge.getMolecules( );
			PickedState <Molecule> state = this.getPickedVertexState( );
			if(( event.getModifiers( ) & ( InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK )) == 0 ) 
					state.clear( );
			state.pick( m[0], true );
			state.pick( m[1], true );
		}
	}
	
	public void graphPressed( Correlation edge, MouseEvent event ) { }
	public void graphReleased( Correlation edge, MouseEvent event ) { }

}
