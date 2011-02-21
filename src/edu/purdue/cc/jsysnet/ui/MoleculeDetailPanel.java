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

import java.util.ArrayList;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;

import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.cc.jsysnet.util.Correlation;
import edu.purdue.cc.jsysnet.util.Experiment;
import edu.purdue.bbc.util.Range;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.Language;
import edu.purdue.cc.jsysnet.JSysNet;

import org.apache.log4j.Logger;

public class MoleculeDetailPanel extends JPanel implements ActionListener {
	private Molecule molecule;
	private Experiment experiment;
	private Range correlationRange;
	private DetailWindow detailWindow;
	private JTable moleculeDetailTable;
	private JTable correlationsTable;
	private JLabel selectedMoleculeLabel;
	private JLabel coefLabel;
	private JButton showElementButton;
	private JButton showCorrelationButton;

	public MoleculeDetailPanel ( Molecule molecule, Range range, 
	                             DetailWindow detailWindow ) {
		super( new BorderLayout( ));
		Logger logger = Logger.getLogger( getClass( ));
		this.molecule = molecule;
		this.experiment = detailWindow.getExperiment( );
		logger.debug( String.format(
			"Showing Molecule detail:\n\tExperiment: %s\n" +
			"\tMolecule:   %s\n\tRange:      %s\n", 
			this.experiment, this.molecule, range));
		this.correlationRange = range.clone( );
		this.detailWindow = detailWindow;

		Language language = Settings.getLanguage( );
		this.moleculeDetailTable = 
			DataTable.getMoleculeTable( this.experiment, this.molecule );
		this.correlationsTable = 
			DataTable.getCorrelatedTable( this.experiment, this.molecule,
			                              this.correlationRange );
		this.selectedMoleculeLabel = new JLabel( language.get( "Selected Molecule" ));
		this.showElementButton = new JButton( language.get( "Show Element" ));
		this.showCorrelationButton = new JButton( language.get( "Show Correlation" ));
		this.coefLabel = new JLabel( String.format( 
			language.get( "Correlation coefficient between %.3f and %.3f" ),
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
		splitPane.setDividerLocation( 300 );
		this.add( splitPane, BorderLayout.CENTER );
	}

	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
		if ( this.correlationsTable.getSelectedRow( ) >= 0 ) {
			if ( source == this.showElementButton ) {
				this.detailWindow.show( 
					this.getMoleculesInRange( ).get( 
						this.correlationsTable.getSelectedRow( )));
			}
			if ( source == this.showCorrelationButton ) {
				this.detailWindow.show( 
					this.experiment.getCorrelation( molecule, 
						this.getMoleculesInRange( ).get( 
							this.correlationsTable.getSelectedRow( ))));
			}
		} else {
			JOptionPane.showMessageDialog( this, 
				Settings.getLanguage( ).get( "You must select a molecule from the table to view its details" ));
		}

	}

	private List <Molecule> getMoleculesInRange( ) {
		List <Molecule> returnValue = new ArrayList( );
		for ( Correlation c : this.experiment.getCorrelations( molecule )) {
			if ( this.correlationRange.contains( Math.abs( c.getValue( )))) {
					returnValue.add( c.getOpposite( this.molecule ));
			}
		}
		return returnValue;
	}

}
