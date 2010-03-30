package edu.purdue.jsysnet.ui;

import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import edu.purdue.jsysnet.util.*;

public class MoleculeDetailPanel extends JPanel implements ActionListener {
	private Molecule molecule;
	private Range correlationRange;
	private double lowCorrelation = 0.0;
	private double highCorrelation = 1.0;
	private JTable moleculeDetailTable;
	private JTable correlationsTable;
	private JLabel selectedMoleculeLabel = new JLabel( "Selected Molecule" );
	private JLabel coefLabel;
	private JButton showElementButton = new JButton( "Show Element" );
	private JButton showCorrelationButton = new JButton( "Show Correlation" );

	public MoleculeDetailPanel ( Molecule molecule, Range range ) {
		super( new BorderLayout( ));
		this.molecule = molecule;
		this.correlationRange = range;

		this.moleculeDetailTable = this.buildAttributeTable( this.molecule );
		this.correlationsTable = this.buildCorrelationTable( this.molecule );
		this.moleculeDetailTable = buildAttributeTable( this.molecule );
		this.coefLabel = new JLabel( String.format( 
			"Correlation coefficient between %.3f and %.3f",
			this.correlationRange.getMin( ),
			this.correlationRange.getMax( )));
		JPanel leftPanel = new JPanel( new BorderLayout( ));
		JPanel rightPanel = new JPanel( new BorderLayout( ));
		JPanel buttonPanel = new JPanel( new BorderLayout( ));
		this.showElementButton.addActionListener( this );
		this.showCorrelationButton.addActionListener( this );
		buttonPanel.add( this.showElementButton, BorderLayout.WEST );
		buttonPanel.add( this.showCorrelationButton, BorderLayout.EAST );
		leftPanel.add( this.selectedMoleculeLabel, BorderLayout.NORTH );
		leftPanel.add( new JScrollPane( this.moleculeDetailTable ), BorderLayout.CENTER );
		rightPanel.add( this.coefLabel, BorderLayout.NORTH );
		rightPanel.add( new JScrollPane( this.correlationsTable ), BorderLayout.CENTER );
		rightPanel.add( buttonPanel, BorderLayout.SOUTH );
		JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel );
		splitPane.setDividerLocation( 200 );
		this.add( splitPane, BorderLayout.CENTER );
	}

	private JTable buildAttributeTable( Molecule molecule ) {
		String [] attributes = this.molecule.getAttributeNames( );
		String [][] values = new String[ attributes.length ][ 2 ];
		for ( int i=0; i < attributes.length; i++ ) {
			values[ i ][ 0 ] = attributes[ i ];
			values[ i ][ 1 ] = this.molecule.getAttribute( attributes[ i ]);
		}
		return new StaticJTable( values, new String[]{ "Attribute", "Value" });
	}

	private JTable buildCorrelationTable( Molecule molecule ) {
		DefaultTableModel returnValue = new DefaultTableModel( );
	  ArrayList <Correlation> correlations = molecule.getCorrelations( );
		ArrayList <String[ ]> data = new ArrayList<String[ ]>( );
		double value;
		for ( Correlation c : correlations ) {
			value = c.getValue( );
			if ( this.correlationRange.contains( Math.abs( value ))) {
				String [] row = new String [ 3 ];
				row[ 0 ] = String.format( "%.3f", value );
				row[ 1 ] = c.getOpposite( molecule ).getAttribute( "group_name" );
				row[ 2 ] = c.getOpposite( molecule ).getAttribute( "name" );
				data.add( row );
			}
		}
		return new StaticJTable( data.toArray( new String[ 0 ][ 0 ]), new String[]{ "Correlation", "Group", "Molecule" });
	}

	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
	}

	protected class StaticJTable extends JTable {
		public StaticJTable( Object[][] values, Object[] columnNames ) {
			super( values, columnNames );
		}

		public StaticJTable( TableModel t ){
			super( t );
		}

		public boolean isCellEditable( int row, int col ) {
			return false;
		}
	}
}
