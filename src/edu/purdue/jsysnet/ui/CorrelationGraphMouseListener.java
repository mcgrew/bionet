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

import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Range;
import edu.purdue.jsysnet.JSysNet;

import java.util.HashMap;
import java.awt.event.MouseEvent;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class CorrelationGraphMouseListener implements GraphMouseListener<Molecule> {
	MoleculePopup popup = new MoleculePopup( );

	public void graphClicked( Molecule m, MouseEvent e ) {
		if ( e.getButton( ) == MouseEvent.BUTTON3 ) {
			if ( JSysNet.settings.getBoolean( "debug" ))
				System.err.println( "Right Click on " + m );
			popup.show( e.getComponent( ), e.getX( ), e.getY( ), m );

		}
	}
	public void graphPressed(  Molecule m, MouseEvent e ) { } 
	public void graphReleased( Molecule m, MouseEvent e ) { }

	protected class MoleculePopup extends JPopupMenu implements ActionListener {
		protected JMenuItem detailsMenuItem = new JMenuItem( "Details" );
		protected JMenuItem selectMoleculesMenuItem = new JMenuItem( "Select Correlated" );
		protected JMenuItem selectCorrelationsMenuItem = new JMenuItem( "Select Correlations" );
		protected JMenuItem exploreCorrelationsMenu = new JMenu( "Explore Correlations" );
		protected Molecule molecule;
		protected HashMap <JMenuItem,Correlation> correlationMap = 
			new HashMap <JMenuItem,Correlation>( );
		
		public MoleculePopup ( ) {
			this.add( this.detailsMenuItem );
			this.add( this.selectMoleculesMenuItem );
			this.add( this.selectCorrelationsMenuItem );
			this.add( this.exploreCorrelationsMenu );
			this.detailsMenuItem.addActionListener( this );
			this.selectMoleculesMenuItem.addActionListener( this );
			this.selectCorrelationsMenuItem.addActionListener( this );
			
		}

		public void show( Component invoker, int x, int y, Molecule m ) {
			this.exploreCorrelationsMenu.removeAll( );
			this.correlationMap.clear( );
			this.molecule = m;
			Range range =
				((CorrelationGraphVisualizer)invoker).getCorrelationFilterPanel( ).getRange( );
			for( Correlation c : m.getCorrelations( )) {
				if ( range.contains( Math.abs( c.getValue( )))) {
					JMenuItem menuItem = new JMenuItem( c.getOpposite( m ).toString( ));
					this.correlationMap.put( menuItem, c );
					this.exploreCorrelationsMenu.add( menuItem );
					menuItem.addActionListener( this );
				}
			}
			this.show( invoker, x, y );
		}


		public void actionPerformed ( ActionEvent e ) {
			CorrelationGraphVisualizer graph = (CorrelationGraphVisualizer) this.getInvoker( );
			Range range = graph.getCorrelationFilterPanel( ).getRange( );
			Object source = e.getSource( );

			if ( this.correlationMap.containsKey( source )) {
				new DetailWindow( "Detail", this.correlationMap.get( source ), new Range( 0.6, 1 ));
			}
			else if ( source == this.detailsMenuItem ) {
				if ( JSysNet.settings.getBoolean( "debug" ))
					System.err.println( "Opening Detail Window for " + molecule.toString( ));
				new DetailWindow( "Detail", this.molecule, range );
			} 
			else if ( source == this.selectMoleculesMenuItem ) {
				PickedState<Molecule> state = graph.getPickedVertexState( );
				for ( Molecule m : graph.getNeighbors( this.molecule )) {
					state.pick( m, true );
				}
			} 
			else if ( source == this.selectCorrelationsMenuItem ) {
				PickedState<Correlation> state = graph.getPickedEdgeState( );
				for ( Correlation c : graph.getIncidentEdges( this.molecule )) {
					state.pick( c, true );
				}
			} 
		}
		
	}
}

