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

package edu.purdue.cc.jsysnet.ui;

import edu.purdue.cc.jsysnet.util.Correlation;
import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.cc.jsysnet.util.MonitorableRange;
import edu.purdue.bbc.util.Range;
import edu.purdue.cc.jsysnet.util.Experiment;
import edu.purdue.cc.jsysnet.util.Spectrum;
import edu.purdue.cc.jsysnet.util.SplitSpectrum;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;

import org.apache.commons.collections15.Transformer;

import java.awt.Paint;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JScrollPane;

/**
 * A class for displaying a JUNG graph tailored for JSysNet
 */
public class CorrelationGraphVisualizer extends GraphVisualizer<Molecule,Correlation> implements ChangeListener,GraphMouseListener<Correlation>, ComponentListener {
	public MonitorableRange range;
	public Experiment experiment;
	protected Spectrum spectrum;
	private SpectrumLegend spectrumLegend;

	/**
	 * Creates a new CorrelationGraphVisualizer.
	 * 
	 * @param experiment The experiment to be associated with this CorrelationGraphVisualizer.
	 * @param range A MonitorableRange object used to determine which Correlations to show on the graph.
	 */
	public CorrelationGraphVisualizer( Experiment experiment, MonitorableRange range ) {
		super( );
		this.setRange( range );
		this.setExperiment( experiment );
		this.addGraphMouseEdgeListener( this );
		this.addComponentListener( this );

		this.spectrum = new SplitSpectrum( range );
		this.spectrum.setOutOfRangePaint( Color.WHITE );
		this.spectrumLegend = new SpectrumLegend( this.spectrum, new Range( -1.0, 1.0 ));
		this.setLayout( null );
		this.add( this.spectrumLegend );
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

	/**
	 * Used to change the experiment which is displayed in this graph.
	 * 
	 * @param experiment The new experiment.
	 */
	public void setExperiment( Experiment experiment ) {
		// remove all edges and vertices.
		for ( Molecule v : this.getVertices( ))
			this.removeVertex( v );
		for ( Correlation e : this.getEdges( ))
			this.removeEdge( e );
		// add the new data.
		this.experiment = experiment;
		this.addVertices( );
		this.addEdges( );
	}

	/**
	 * Returns the current Experiment associated with this graph.
	 * 
	 * @return The Experiment associated with this graph.
	 */
	public Experiment getExperiment( ) {
		return this.experiment;
	}
	
	/**
	 * Sets a new range to be used for filtering.
	 * 
	 * @param range The new MonitorableRange object to use.
	 */
	public void setRange( MonitorableRange range ) {
		if ( this.range != null )
			this.range.removeChangeListener( this );
		this.range = range;
		range.addChangeListener( this );
	}

	/**
	 * Gets the current MOnitorableRange object being used.
	 * 
	 * @return The current MonitorableRange.
	 */
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

	/**
	 * Filters the edges displayed in the graph based on the MonitorableRange
	 * associated with this graph.
	 * 
	 * @return The new number of edges contained in the graph.
	 */
	public int filterEdges( ) {
		
		int returnValue = 0;
		for( Correlation correlation : this.experiment.getCorrelations( )) {
			if ( this.isValidEdge( correlation )) {
				returnValue++;
				// this Correlation belongs on the graph, make sure it is there.
				if ( !this.containsEdge( correlation )) {
					this.addEdge( correlation, 
												 new Pair <Molecule> ( correlation.getMolecules( )),
												 EdgeType.UNDIRECTED );
				}
			}
			else {
				// this Correlation does not belong on the graph, make sure it is not there.
				if ( this.containsEdge( correlation )) {
					this.getPickedEdgeState( ).pick( correlation, false );
					this.removeEdge( correlation );
				}
			}
		}
		this.repaint( );
		return returnValue;
	}

	/**
	 * Checks to see if an edge is valid and belongs in the graph.
	 * 
	 * @param correlation The edge to check.
	 * @return true If the correlation should be shown, false otherwise.
	 */
	public boolean isValidEdge( Correlation correlation ) {
		Molecule [] molecules = correlation.getMolecules( );
		return ( this.containsVertex( molecules[ 0 ] ) &&
						 this.containsVertex( molecules[ 1 ] ) &&
						 this.range.contains( 
							 Math.abs( correlation.getValue( ))));
	}

	/**
	 * The stateChanged method of the ChangeListener interface.
	 * @see java.awt.event.ChangeListener#stateChanged(java.awt.event.ChangeEvent)
	 * 
	 * @param event The event which triggered this action.
	 */
	public void stateChanged( ChangeEvent event ) {
		this.filterEdges( );
	}

	/**
	 * The graphClicked method of the GraphMouseListener interface.
	 * @see edu.uci.ics.jung.visualization.control.GraphMouseListener#graphClicked(V,java.awt.MouseEvent)
	 * 
	 * @param edge The edge which was clicked on to trigger the event.
	 * @param event The event which triggered this action.
	 */
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

	/**
	 * Called when the component is repainted.
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 * 
	 * @param g The Graphics object associated with this Component.
	 */
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
	}
	
	/**
	 * The graphPressed method of the GraphMouseListener interface. Not implemented.
	 * 
	 * @param edge The edge which was clicked on to trigger the event.
	 * @param event The event which triggered this action.
	 */
	public void graphPressed( Correlation edge, MouseEvent event ) { }

	/**
	 * The graphReleased method of the GraphMouseListener interface. Not implemented.
	 * 
	 * @param edge The edge which was clicked on to trigger the event.
	 * @param event The event which triggered this action.
	 */
	public void graphReleased( Correlation edge, MouseEvent event ) { }

	// ComponentListener Methods
	public void componentHidden( ComponentEvent e ) { }
	public void componentMoved( ComponentEvent e ) {
		int h, w;
		if ( this.scrollPane != null ) {
			Rectangle view = this.scrollPane.getViewport( ).getViewRect( );
			w = view.x;
			h = view.y + view.height;
		} else {
			w = 0;
			h = this.getHeight( );
		}
		Rectangle legendArea = new Rectangle( w + 20, h - 35, 150, 20);
		this.spectrumLegend.setBounds( legendArea );
		this.spectrumLegend.repaint( );
	}
	public void componentResized( ComponentEvent e ) { 
		componentMoved( e );
	}
	public void componentShown( ComponentEvent e ) { }

}
