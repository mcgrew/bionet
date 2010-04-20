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

import java.util.List;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
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
	private DetailWindow detailWindow;
	private JTable molecule0Table;
	private JTable molecule1Table;
	JButton molecule0Button = new JButton( "Show Correlated" );
	JButton molecule1Button = new JButton( "Show Correlated" );
	List molecule0Samples;
	List molecule1Samples;

	public CorrelationDetailPanel( Correlation correlation, Range range, DetailWindow detailWindow ) {
		super( new BorderLayout( ));
		this.correlation = correlation;
		this.correlationRange = range;
		this.detailWindow = detailWindow;

		molecule0Samples = correlation.getMolecules( )[ 0 ].getSamples( );
		molecule1Samples = correlation.getMolecules( )[ 1 ].getSamples( );

		Molecule [] molecules = correlation.getMolecules( );
		this.molecule0Table = DataTable.getMoleculeTable( molecules[ 0 ] );
		this.molecule1Table = DataTable.getMoleculeTable( molecules[ 1 ] );
		JPanel topMoleculePanel = new JPanel( new BorderLayout( ));
		JPanel bottomMoleculePanel = new JPanel( new BorderLayout( ));
		JScrollPane molecule0ScrollPane = new JScrollPane( this.molecule0Table );
		JScrollPane molecule1ScrollPane = new JScrollPane( this.molecule1Table );
		topMoleculePanel.add( molecule0ScrollPane, BorderLayout.CENTER );
		topMoleculePanel.add( this.molecule0Button, BorderLayout.SOUTH );
		bottomMoleculePanel.add( molecule1ScrollPane, BorderLayout.CENTER );
		bottomMoleculePanel.add( this.molecule1Button, BorderLayout.SOUTH );
		this.molecule0Button.addActionListener( this );
		this.molecule1Button.addActionListener( this );

		JSplitPane moleculePane = new JSplitPane( 
		  JSplitPane.VERTICAL_SPLIT, topMoleculePanel, bottomMoleculePanel );
		moleculePane.setDividerLocation( 217 );
		JPanel moleculePanel = new JPanel( new BorderLayout( ));
		moleculePanel.add( moleculePane, BorderLayout.CENTER );
		JPanel graphPanel = new JPanel( ); // Just a placeholder
		JPanel infoPanel = new InfoPanel( 
			this.correlation.getValue( ), 
			molecule0Samples.size( ),
			new double[] { 0.497, 0.576, 0.658, 0.708 }
		);

		JPanel mainPanel = new JPanel( new BorderLayout( ));
		mainPanel.add( moleculePanel, BorderLayout.WEST );
		mainPanel.add( graphPanel, BorderLayout.CENTER );
		mainPanel.add( infoPanel, BorderLayout.EAST );
		moleculePanel.setPreferredSize( new Dimension( 200, 400 ));
		infoPanel.setPreferredSize( new Dimension( 300, 400 ));
		this.add( mainPanel, BorderLayout.CENTER );

	}

	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
		if ( source == this.molecule0Button ) {
			this.detailWindow.show( this.correlation.getMolecules( )[ 0 ]);
		}
		else if ( source == this.molecule1Button ) {
			this.detailWindow.show( this.correlation.getMolecules( )[ 1 ]);
		}
	}
	
	private class InfoPanel extends JPanel {
		double coefficient;
		int sampleVolume;
		double [] significance;

		public InfoPanel( double coefficient, int sampleVolume, double [] significance ) {
			this.coefficient = coefficient;
			this.sampleVolume = sampleVolume;
			this.significance = significance;
		}

		public void paintComponent( Graphics g ) {
			g.setFont( new Font( "SansSerif", Font.PLAIN, 14 ));
			g.drawString( String.format( "Current correlation coefficient: %.3f", this.coefficient ), 10, 40 );
			g.drawString( String.format( "Sample Volume N: %d", this.sampleVolume ), 10, 70 );
			g.drawString( "Table of critical values for", 10, 100 );
			g.drawString( String.format( "correlation test %s", "" ), 10, 120 );
			g.drawString( "One-tailed level of significance", 10, 160 );
			g.drawString( "0.05",   20, 180 );
			g.drawString( "0.025",  90, 180 );
			g.drawString( "0.05",  150, 180 );
			g.drawString( "0.005", 220, 180 );
			g.drawString( "Two-tailed levelof significance", 10, 200 );
			g.drawString( "0.1",   20, 220 );
			g.drawString( "0.05",  90, 220 );
			g.drawString( "0.02", 150, 220 );
			g.drawString( "0.01", 220, 220 );
		}

	}

}


