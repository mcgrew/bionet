package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Settings;

import java.awt.event.MouseEvent;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class CorrelationGraphMouseListener implements GraphMouseListener<Molecule> {
	MoleculePopup popup = new MoleculePopup( );

	public void graphClicked( Molecule m, MouseEvent e ) {
		if ( e.getButton( ) == MouseEvent.BUTTON3 ) {
			if ( Settings.DEBUG )
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
		protected JMenuItem exploreCorrelationsMenuItem = new JMenuItem( "Explore Correlations" );
		protected Molecule molecule;
		
		public MoleculePopup ( ) {
			this.add( this.detailsMenuItem );
			this.add( this.selectMoleculesMenuItem );
			this.add( this.selectCorrelationsMenuItem );
			this.add( this.exploreCorrelationsMenuItem );
			this.detailsMenuItem.addActionListener( this );
			this.selectMoleculesMenuItem.addActionListener( this );
			this.selectCorrelationsMenuItem.addActionListener( this );
			this.exploreCorrelationsMenuItem.addActionListener( this );
			
		}

		public void show( Component invoker, int x, int y, Molecule m ) {
			this.molecule = m;
			this.show( invoker, x, y );
		}


		public void actionPerformed ( ActionEvent e ) {
			GraphVisualizer<Molecule,Correlation> graph = (GraphVisualizer<Molecule,Correlation>) this.getInvoker( );

			if ( e.getSource( ) == this.detailsMenuItem ) {
				if ( Settings.DEBUG )
					System.err.println( "Opening Detail Window for " + molecule.toString( ));
//				new DetailWindow( "", this.molecule );
			} 
			else if ( e.getSource( ) == this.selectMoleculesMenuItem ) {
				PickedState<Molecule> state = graph.getPickedVertexState( );
				for ( Molecule m : graph.getNeighbors( this.molecule )) {
					state.pick( m, true );
				}
			} 
			else if ( e.getSource( ) == this.selectCorrelationsMenuItem ) {
				PickedState<Correlation> state = graph.getPickedEdgeState( );
				for ( Correlation c : graph.getIncidentEdges( this.molecule )) {
					state.pick( c, true );
				}
			} 
			else if ( e.getSource( ) == this.exploreCorrelationsMenuItem ) {
			}
		}
		
	}
}

