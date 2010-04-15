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

import java.util.ArrayList;
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

import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Range;

public class MoleculeDetailPanel extends JPanel implements ActionListener {
	private Molecule molecule;
	private Range correlationRange;
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

		this.moleculeDetailTable = DataTable.getMoleculeTable( this.molecule );
		this.correlationsTable = DataTable.getCorrelatedTable( this.molecule, this.correlationRange );
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

	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
	}

}
