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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Range;

public class CorrelationDetailPanel extends JPanel implements ActionListener {
	private Correlation correlation;
	private Range correlationRange;
	private JTable molecule0Table;
	private JTable molecule1Table;

	public CorrelationDetailPanel( Correlation correlation, Range range ) {
		super( new BorderLayout( ));
		this.correlation = correlation;
		this.correlationRange = range;

		Molecule [] molecules = correlation.getMolecules( );
		this.molecule0Table = DataTable.getMoleculeTable( molecules[ 0 ] );
		this.molecule1Table = DataTable.getMoleculeTable( molecules[ 1 ] );
		JButton molecule0Button = new JButton( "Show Correlated" );
		JButton molecule1Button = new JButton( "Show Correlated" );
		JPanel topMoleculePanel = new JPanel( new BorderLayout( ));
		JPanel bottomMoleculePanel = new JPanel( new BorderLayout( ));
		topMoleculePanel.add( this.molecule0Table, BorderLayout.CENTER );
		topMoleculePanel.add( molecule0Button, BorderLayout.SOUTH );
		bottomMoleculePanel.add( this.molecule1Table, BorderLayout.CENTER );
		bottomMoleculePanel.add( molecule1Button, BorderLayout.SOUTH );

		JSplitPane moleculePanel = new JSplitPane( 
		  JSplitPane.VERTICAL_SPLIT, topMoleculePanel, bottomMoleculePanel );
		JPanel graphPanel = new JPanel( ); // Just a placeholder
		JPanel infoPanel = new JPanel( );

		JPanel mainPanel = new JPanel( new BorderLayout( ));
		mainPanel.add( moleculePanel, BorderLayout.WEST );
		mainPanel.add( graphPanel, BorderLayout.CENTER );
		mainPanel.add( infoPanel, BorderLayout.EAST );

	}

	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
	}

}


