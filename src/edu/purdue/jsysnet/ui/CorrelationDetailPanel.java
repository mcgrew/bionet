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
import java.util.Iterator;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.GradientPaint;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartFactory;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.plot.XYPlot;

import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Range;

/**
 * A panel for displaying detailed information about a correlation.
 */
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

	/**
	 * Constructs a new CorrelationDetailPanel
	 * 
	 * @param correlation The Correlation to display information about.
	 * @param range The valid range for correlations displayed in this window.
	 * @param detailWindow The parent window of this panel.
	 */
	public CorrelationDetailPanel( Correlation correlation, Range range, DetailWindow detailWindow ) {
		super( new BorderLayout( ));
		this.correlation = correlation;
		this.correlationRange = range.clone( );
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

		JPanel moleculePane = new JPanel( new GridLayout( 2, 1 ));
		moleculePane.add( topMoleculePanel );
		moleculePane.add( bottomMoleculePanel );
		JPanel moleculePanel = new JPanel( new BorderLayout( ));
		moleculePanel.add( moleculePane, BorderLayout.CENTER );
		JPanel graphPanel = new ScatterPlot( correlation.getMolecules( ));
		JPanel infoPanel = new InfoPanel( 
			this.correlation.getValue( ), 
			molecule0Samples.size( )
		);

		JPanel mainPanel = new JPanel( new BorderLayout( ));
		mainPanel.add( moleculePanel, BorderLayout.WEST );
		mainPanel.add( graphPanel, BorderLayout.CENTER );
		mainPanel.add( infoPanel, BorderLayout.EAST );
		moleculePanel.setPreferredSize( new Dimension( 200, 400 ));
		infoPanel.setPreferredSize( new Dimension( 300, 400 ));
		this.add( mainPanel, BorderLayout.CENTER );

	}

	/**
	 * The actionPerformed method of the ActionListener interface.
	 * 
	 * @param event The event which triggered this action.
	 */
	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
		if ( source == this.molecule0Button ) {
			this.detailWindow.show( this.correlation.getMolecules( )[ 0 ]);
		}
		else if ( source == this.molecule1Button ) {
			this.detailWindow.show( this.correlation.getMolecules( )[ 1 ]);
		}
	}

	/**
	 * A panel for displaying a scatter plot containing molecule information.
	 */
	private class ScatterPlot extends JPanel implements XYItemLabelGenerator {
		private JFreeChart chart;

		/**
		 * Construcs a new ScatterPlot instance.
		 * 
		 * @param molecules An array of 2 molecules to display the information of.
		 */
		public ScatterPlot ( Molecule [] molecules ) {
			super( );
			List <Double> mol0Samples = molecules[ 0 ].getSamples( );
			List <Double> mol1Samples = molecules[ 1 ].getSamples( );
			XYSeries data = new XYSeries( "Sample Data" );
			Iterator <Double> mol0Iterator = mol0Samples.iterator( );
			Iterator <Double> mol1Iterator = mol1Samples.iterator( );
			while ( mol0Iterator.hasNext( )) {
				data.add( mol0Iterator.next( ).doubleValue( ),
				          mol1Iterator.next( ).doubleValue( ));
			}
			XYSeriesCollection dataset = new XYSeriesCollection( );
			dataset.addSeries( data );
			this.chart = ChartFactory.createScatterPlot(
				null, //title
				molecules[ 0 ].toString( ), // x axis label
				molecules[ 1 ].toString( ), // y axis label
				dataset, // plot data
				PlotOrientation.VERTICAL, // Plot Orientation
				false, // show legend
				false, // use tooltips
				false  // configure chart to generate URLs (?!)
			);

			this.chart.setBackgroundPaint( new GradientPaint( 
				0, 1000, Color.WHITE, 1000, 0, Color.GRAY ));
			XYPlot plot = this.chart.getXYPlot( );
			plot.setBackgroundPaint( Color.WHITE );
			plot.setRangeGridlinePaint( Color.GRAY );
			plot.setDomainGridlinePaint( Color.GRAY );
			XYItemRenderer renderer = plot.getRenderer( );
			renderer.setItemLabelGenerator( this );
			renderer.setItemLabelsVisible( true );
		}

		/**
		 * The generateLabel method of the XYItemLabelGenerator interface.
		 * 
		 * @param dataset The dataset to generate labels for.
		 * @param series The series for which the item belongs to.
		 * @param item The item in the series to generate a label for.
		 * @return The label for the item as a String.
		 */
		public String generateLabel( XYDataset dataset, int series, int item ) {
			return "S" + Integer.toString( item + 1 );
		}

		/**
		 * Draws the graph.
		 * 
		 * @param g The Graphics object of this component.
		 */
		public void paintComponent ( Graphics g ) {
			super.paintComponent( g );
			Dimension size = this.getSize( null );
			BufferedImage drawing = chart.createBufferedImage( size.width, size.height );
			g.drawImage( drawing, 0, 0, Color.WHITE, this );
		}
	}
	
	/**
	 * A panel for displaying information about this Correlation.
	 */
	private class InfoPanel extends JPanel {
		private double coefficient;
		private int sampleVolume;
		private double [] criticalValues;

		/**
		 * Constructs a new InfoPanel.
		 * 
		 * @param coefficient
		 * @param sampleVolume
		 * @param significance.
		 */
		public InfoPanel( double coefficient, int sampleVolume ) {
			this.coefficient = coefficient;
			this.sampleVolume = sampleVolume;
			this.criticalValues = Correlation.getCriticalValues( 
					Correlation.getDefaultMethod( ), this.sampleVolume );
		}

		/**
		 * Redraws this component.
		 * 
		 * @param g The Graphics object corresponding to this component.
		 */
		public void paintComponent( Graphics g ) {
			int [] xpos = { 20, 90, 150, 220 };

			g.setColor( Color.WHITE );
			g.fillRect( xpos[0]-5, 246, xpos[3]-xpos[0]+50, 16 );
			g.fillRect( 137, 56, 25, 16 );
			g.fillRect( 227, 26, 55, 16 );

			g.setColor( Color.BLACK );
			g.setFont( new Font( "SansSerif", Font.PLAIN, 14 ));
			g.drawString( "Current correlation coefficient:", 10, 40 );
			g.drawString( "Sample Volume N:", 10, 70 );
			g.drawString( "0.05",   xpos[0], 180 );
			g.drawString( "0.025",  xpos[1], 180 );
			g.drawString( "0.05",   xpos[2], 180 );
			g.drawString( "0.005",  xpos[3], 180 );
			g.drawString( "0.1",   xpos[0], 220 );
			g.drawString( "0.05",  xpos[1], 220 );
			g.drawString( "0.02",  xpos[2], 220 );
			g.drawString( "0.01",  xpos[3], 220 );

			g.setFont( new Font( "SansSerif", Font.BOLD, 14 ));
			g.drawString( "Table of critical values for", 10, 100 );
			g.drawString( "correlation test", 10, 120 );
			g.drawString( "One-tailed level of significance", 10, 160 );
			g.drawString( "Two-tailed levelof significance", 10, 200 );
			for( int i=0; i < 4; i++ ) {
				if ( this.criticalValues[ i ] < 0 )
					g.drawString( "-", xpos[ i ], 260 );
				else
					g.drawString( Double.toString( this.criticalValues[ i ]), xpos[ i ], 260 );
			}
			g.drawString( Integer.toString( this.sampleVolume ), 140, 70 ); // sample volume
			g.drawString( String.format( "%.3f", this.coefficient ), 230, 40 ); // correlation coefficient

			g.setFont( new Font( "SansSerif", Font.BOLD | Font.ITALIC, 14 ));
			g.setColor( Color.BLUE );
			g.drawString( Correlation.NAME[ Correlation.getDefaultMethod( )], 125, 120 ); // correlation name;
		}

	}

}


