package edu.purdue.jsysnet.ui;

import java.util.ArrayList;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import edu.purdue.jsysnet.util.*;

public class MoleculeDetailView extends JPanel {
	private Molecule molecule;
	private StaticJTable moleculeDetail;
	private StaticJTable correlations;

	public MoleculeDetailView ( Molecule molecule ) {
		super( new BorderLayout( ));
		this.molecule = molecule;

		this.moleculeDetail = this.buildAttributeTable( this.molecule );
		this.correlations = this.buildCorrelationTable( this.molecule );
		this.add( this.moleculeDetail, BorderLayout.EAST );
	}

	private StaticJTable buildAttributeTable( Molecule molecule ) {
		String [] attributes = this.molecule.getAttributeNames( );
		String [][] values = new String[ 2 ][ attributes.length ];
		for ( int i=0; i < attributes.length; i++ ) {
			values[ 0 ][ i ] = attributes[ i ];
			values[ 1 ][ i ] = this.molecule.getAttribute( attributes[ i ]);
		}
//		return new StaticJTable( values, new String[]{ "", "" });
		return new StaticJTable( );
	}

	private StaticJTable buildCorrelationTable( Molecule molecule ) {
	  ArrayList <Correlation> correlations = molecule.getCorrelations( );
		String [][] data = new String [ 3 ][ correlations.size( ) ];
		for ( int i=0,s=correlations.size( ); i < s; i++ ) {
			data[ 0 ][ i ] = String.format( "%f", correlations.get( i ).getValue( ));
			data[ 1 ][ i ] = correlations.get( i ).getOpposite( molecule ).getAttribute( "group" );
			data[ 2 ][ i ] = correlations.get( i ).getOpposite( molecule ).getAttribute( "molecule" );
		}
//		return new StaticJTable( data, new String[]{ "Value", "Group", "Molecular Name" });
		return new StaticJTable( );
	}


	protected class StaticJTable extends JTable {

		public boolean isCellEditable( int row, int col ) {
			return false;
		}
	}
}
