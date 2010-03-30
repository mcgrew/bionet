package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Molecule;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;

public class CorrelationGraphVisualizer extends GraphVisualizer<Molecule,Correlation>{
	private CorrelationFilterPanel correlationFilterPanel;
	private CorrelationDisplayPanel correlationDisplayPanel;

	public void setCorrelationFilterPanel( CorrelationFilterPanel cfp ){
		this.correlationFilterPanel = cfp;
	}

	public void setCorrelationDisplayPanel( CorrelationDisplayPanel cdp ){
		this.correlationDisplayPanel = cdp;
	}

	public CorrelationFilterPanel getCorrelationFilterPanel( ) {
		return this.correlationFilterPanel;
	}

	public CorrelationDisplayPanel getCorrelationDisplayPanel( ) {
		return this.correlationDisplayPanel;
	}


	public int filterEdges( ) {
		
		int returnValue = 0;
		for( Correlation correlation : this.correlationDisplayPanel.experiment.getCorrelations( )) {
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
					this.graph.removeEdge( correlation );
				}
			}
		}
			return returnValue;
	}
	public boolean isValidEdge( Correlation correlation ) {
		Molecule [] molecules = correlation.getMolecules( );
		return ( this.graph.containsVertex( molecules[ 0 ] ) &&
						 this.graph.containsVertex( molecules[ 1 ] ) &&
						 this.correlationFilterPanel.getRange( ).contains( 
							 Math.abs( correlation.getValue( ))));
	}

}
