package edu.purdue.jsysnet.ui;

import java.util.ArrayList;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import edu.purdue.jsysnet.util.*;

public class MoleculeDetailPanel extends JPanel {
	private Molecule molecule;
	private StaticJTable moleculeDetailTable;
	private StaticJTable correlationsTable;

	public MoleculeDetailPanel ( Molecule molecule ) {
		super( new BorderLayout( ));
		this.molecule = molecule;

		this.moleculeDetailTable = this.buildAttributeTable( this.molecule );
		this.correlationsTable = this.buildCorrelationTable( this.molecule );
		this.moleculeDetailTable = buildAttributeTable( this.molecule );
		this.add( new JScrollPane( this.moleculeDetailTable ), BorderLayout.EAST );
		this.moleculeDetailTable.setFillsViewportHeight( true );
		this.moleculeDetailTable.setPreferredSize( new Dimension( 400, 400 ));
	}

	private StaticJTable buildAttributeTable( Molecule molecule ) {
		DefaultTableModel returnValue = new DefaultTableModel( );
		String [] attributes = this.molecule.getAttributeNames( );
		String [][] values = new String[ 2 ][ attributes.length ];
//		return new StaticJTable( values, new String[]{ "", "" });
		for ( int i=0; i < attributes.length; i++ ) {
			returnValue.addRow( new Object[]{ attributes[i], this.molecule.getAttribute( attributes[ i ])});
		}
		return new StaticJTable( returnValue );
	}

	private StaticJTable buildCorrelationTable( Molecule molecule ) {
		DefaultTableModel returnValue = new DefaultTableModel( );
	  ArrayList <Correlation> correlations = molecule.getCorrelations( );
		String [][] data = new String [ 3 ][ correlations.size( ) ];
		for ( int i=0,s=correlations.size( ); i < s; i++ ) {
			data[ 0 ][ i ] = String.format( "%f", correlations.get( i ).getValue( ));
			data[ 1 ][ i ] = correlations.get( i ).getOpposite( molecule ).getAttribute( "group" );
			data[ 2 ][ i ] = correlations.get( i ).getOpposite( molecule ).getAttribute( "molecule" );
		}
		return new StaticJTable( returnValue );
	}


	protected class StaticJTable extends JTable {

		public StaticJTable( TableModel t ){
			super( t );
		}

		public boolean isCellEditable( int row, int col ) {
			return false;
		}
	}
}
