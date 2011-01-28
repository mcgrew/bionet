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

import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.cc.jsysnet.util.Correlation;
import edu.purdue.bbc.util.Range;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.Language;

import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
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

/**
 * A class for implementing a context menu on network nodes.
 */
public class CorrelationGraphMouseListener implements GraphMouseListener<Molecule> {
	MoleculePopup popup = new MoleculePopup( );

	/**
	 * The graphClicked method of the GraphMouseListener class
	 * 
	 * @param m The Molecule (node) which was clicked on.
	 * @param e The event which triggered this action.
	 */
	public void graphClicked( Molecule m, MouseEvent e ) {
		if ( e.getButton( ) == MouseEvent.BUTTON3 ) {
			popup.show( e.getComponent( ), e.getX( ), e.getY( ), m );
		} else if ( e.getButton( ) == MouseEvent.BUTTON1 && e.getClickCount( ) >= 2 ) {
			CorrelationGraphVisualizer graph = (CorrelationGraphVisualizer)e.getComponent( );
				new DetailWindow( graph.getExperiment( ).getAttribute( "description" ), 
					m, graph.getRange( ));
		}
	}
	/**
	 * The graphPressed method of the GraphMouseListener class. Not implemented.
	 * 
	 * @param m The Molecule (node) which was clicked on.
	 * @param e The event which triggered this action.
	 */
	public void graphPressed(  Molecule m, MouseEvent e ) { } 
	/**
	 * The graphPressed method of the GraphMouseListener class. Not implemented.
	 * 
	 * @param m The Molecule (node) which was clicked on.
	 * @param e The event which triggered this action.
	 */
	public void graphReleased( Molecule m, MouseEvent e ) { }

	/**
	 * A class for implementing the context menu.
	 */
	protected class MoleculePopup extends JPopupMenu implements ActionListener {
		protected JMenuItem hideMenuItem;
		protected JMenuItem detailsMenuItem;
		protected JMenuItem selectCorrelatedMenuItem;
		protected JMenuItem selectSubnetworkMenuItem;
		protected JMenuItem exploreCorrelationsMenu;
		protected Molecule molecule;
		protected HashMap <JMenuItem,Correlation> correlationMap = 
			new HashMap <JMenuItem,Correlation>( );
		
		/**
		 * Creates a new instance of the PopupMenu
		 */
		public MoleculePopup ( ) {
			Language language = Settings.getLanguage( );
			this.hideMenuItem = new JMenuItem( language.get( "Hide" ) );
			this.detailsMenuItem = new JMenuItem( language.get( "Details" ) );
			this.selectCorrelatedMenuItem = new JMenuItem( language.get( "Select Directly Correlated" ) );
			this.selectSubnetworkMenuItem = new JMenuItem( language.get( "Select Subnetwork" ) );
			this.exploreCorrelationsMenu = new JMenu( language.get( "Explore Correlations" ) );
			this.add( this.hideMenuItem );
			this.add( this.detailsMenuItem );
			this.add( this.selectCorrelatedMenuItem );
			this.add( this.selectSubnetworkMenuItem );
			this.add( this.exploreCorrelationsMenu );
			this.hideMenuItem.addActionListener( this );
			this.detailsMenuItem.addActionListener( this );
			this.selectSubnetworkMenuItem.addActionListener( this );
			this.selectCorrelatedMenuItem.addActionListener( this );
			
		}

		/**
		 * Causes the JPopupMenu to be displayed at the given coordinates.
		 * @see javax.swing.JPopupMenu#show(Component,int,int)
		 * 
		 * @param invoker The component which invoked this menu.
		 * @param x The x position to display this menu.
		 * @param y The y position to display this menu.
		 * @param m The molecule which was clicked on to trigger this popup.
		 */
		public void show( Component invoker, int x, int y, Molecule m ) {
			this.exploreCorrelationsMenu.removeAll( );
			this.correlationMap.clear( );
			this.molecule = m;
			Range range =
				((CorrelationGraphVisualizer)invoker).getRange( );
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


		/**
		 * The actionPerformed method of the ActionListener interface.
		 * @see java.awt.event.ActionListner#actionPerformed(java.awt.event.ActionEvent)
		 * 
		 * @param e the event which triggered this action.
		 */
		public void actionPerformed ( ActionEvent e ) {
			CorrelationGraphVisualizer graph = (CorrelationGraphVisualizer)this.getInvoker( );
			Range range = graph.getRange( );
			Object source = e.getSource( );

			if ( this.correlationMap.containsKey( source )) {
				new DetailWindow( graph.getExperiment( ).getAttribute( "description" ), this.correlationMap.get( source ), range );
			} else if ( source == this.hideMenuItem ) {
				graph.removeVertex( this.molecule );

			} else if ( source == this.detailsMenuItem ) {
				new DetailWindow( graph.getExperiment( ).getAttribute( "description" ), this.molecule, range );

			} else if ( source == this.selectCorrelatedMenuItem ) {
				PickedState<Molecule> state = graph.getPickedVertexState( );
				state.pick( this.molecule, true );
				for ( Molecule m : graph.getNeighbors( this.molecule )) {
					state.pick( m, true );
				}
				PickedState<Correlation> edgeState = graph.getPickedEdgeState( );
				for ( Correlation c : graph.getIncidentEdges( this.molecule )) {
					edgeState.pick( c, true );
				}

			} else if ( source == this.selectSubnetworkMenuItem ) {
				PickedState<Molecule> state = graph.getPickedVertexState( );
				PickedState<Correlation> edgeState = graph.getPickedEdgeState( );
				Collection <Molecule> subnetwork = this.getSubnetwork( this.molecule, 
	        (CorrelationGraphVisualizer)this.getInvoker( ), null);
				for( Molecule m : subnetwork ) {
					state.pick( m, true );
					for ( Correlation c : graph.getIncidentEdges( m )) {
						edgeState.pick( c, true );
					}
				}
			}
		}

		/**
		 * Recursive function for selecting connected nodes.
		 * 
		 * @param molecule The central molcule to select all connected nodes for.
		 * @param graph The graph the molecule belongs to.
		 */
		private Collection<Molecule> getSubnetwork( Molecule molecule, 
		                                            CorrelationGraphVisualizer graph, 
																								Collection<Molecule> collection ) {
			if ( collection == null ) 
				collection = new ArrayList<Molecule>( );
			if ( collection.contains( molecule ))
				return collection;

			collection.add( molecule );
			for ( Molecule m : graph.getNeighbors( molecule )) {
				this.getSubnetwork( m, graph, collection );
			}
			return collection;
		}
		
	}
}

