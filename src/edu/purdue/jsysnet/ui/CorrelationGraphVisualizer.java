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
					this.getPickedEdgeState( ).pick( correlation, false );
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
