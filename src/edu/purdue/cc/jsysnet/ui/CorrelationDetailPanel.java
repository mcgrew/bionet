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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Pair;
import edu.purdue.bbc.util.Range;
import edu.purdue.bbc.util.Settings;
import edu.purdue.cc.jsysnet.io.SaveImageAction;
import edu.purdue.cc.jsysnet.util.Correlation;
import edu.purdue.cc.jsysnet.util.CorrelationSet;
import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.cc.jsysnet.util.Sample;

/**
 * A panel for displaying detailed information about a correlation.
 */
public class CorrelationDetailPanel extends JPanel implements ActionListener {
	private Correlation correlation;
	private Range correlationRange;
	private DetailWindow detailWindow;
	private JTable firstMoleculeTable;
	private JTable secondMoleculeTable;
	private CorrelationSet correlations;
	JButton firstMoleculeButton;
	JButton secondMoleculeButton;
	List <Number> firstMoleculeSamples;
	List <Number> secondMoleculeSamples;
	private int correlationMethod;

	/**
	 * Constructs a new CorrelationDetailPanel
	 * 
	 * @param correlation The Correlation to display information about.
	 * @param range The valid range for correlations displayed in this window.
	 * @param detailWindow The parent window of this panel.
	 */
	public CorrelationDetailPanel( Correlation correlation, Range range, 
			DetailWindow detailWindow, int correlationMethod ) {
		super( new BorderLayout( ));
		this.correlation = correlation;
		this.correlationRange = range.clone( );
		this.detailWindow = detailWindow;
		this.correlationMethod = correlationMethod;
		this.correlations = this.detailWindow.getCorrelations( );

		String buttonText = Settings.getLanguage( ).get( "Show Correlated" );
		this.firstMoleculeButton = new JButton( buttonText );
		this.secondMoleculeButton = new JButton( buttonText );

		this.firstMoleculeTable = DataTable.getMoleculeTable( 
			correlations, correlation.getFirst( )); 
		this.secondMoleculeTable = DataTable.getMoleculeTable( 
			correlations, correlation.getSecond( ));
		JPanel topMoleculePanel = new JPanel( new BorderLayout( ));
		JPanel bottomMoleculePanel = new JPanel( new BorderLayout( ));
		JScrollPane firstMoleculeScrollPane = 
			new JScrollPane( this.firstMoleculeTable );
		JScrollPane secondMoleculeScrollPane = 
			new JScrollPane( this.secondMoleculeTable );
		topMoleculePanel.add( firstMoleculeScrollPane, BorderLayout.CENTER );
		topMoleculePanel.add( this.firstMoleculeButton, BorderLayout.SOUTH );
		bottomMoleculePanel.add( secondMoleculeScrollPane, BorderLayout.CENTER );
		bottomMoleculePanel.add( this.secondMoleculeButton, BorderLayout.SOUTH );
		this.firstMoleculeButton.addActionListener( this );
		this.secondMoleculeButton.addActionListener( this );

		JPanel moleculePane = new JPanel( new GridLayout( 2, 1 ));
		moleculePane.add( topMoleculePanel );
		moleculePane.add( bottomMoleculePanel );
		JPanel moleculePanel = new JPanel( new BorderLayout( ));
		moleculePanel.add( moleculePane, BorderLayout.CENTER );
		JPanel graphPanel = new ScatterPlot( correlation, correlations );
		JPanel infoPanel = new InfoPanel( 
			this.correlation.getValue( this.correlationMethod ), 
			((ScatterPlot)graphPanel).getSamples( ).size( ));

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
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * @param event The event which triggered this action.
	 */
	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
		if ( source == this.firstMoleculeButton ) {
			this.detailWindow.show( this.correlation.getFirst( ));
		}
		else if ( source == this.secondMoleculeButton ) {
			this.detailWindow.show( this.correlation.getSecond( ));
		}
	}

	/**
	 * A panel for displaying a scatter plot containing molecule information.
	 */
	private class ScatterPlot extends JPanel implements XYItemLabelGenerator {
		private JFreeChart chart;
		private List<Sample> samples;

		/**
		 * Construcs a new ScatterPlot instance.
		 * 
		 * @param molecules An array of 2 molecules to display the information of.
		 */
		public ScatterPlot ( Pair<Molecule> molecules, CorrelationSet correlations ) {
			super( );
			// add a context menu for saving the graph to an image
			new ContextMenu( this ).add( new SaveImageAction( this ));
			Map sortedMap = new TreeMap<Number,Sample>( );
			for ( Sample sample : correlations.getSamples( )) {
				sortedMap.put( sample.getValue( molecules.getFirst( )), sample );
			}
			this.samples = new ArrayList<Sample>( sortedMap.values( ));
			XYSeries data = new XYSeries( 
				Settings.getLanguage( ).get( "Sample Data" ));
			for ( Sample sample : new ArrayList<Sample>( this.samples )) {
				Double first = (Double)sample.getValue( molecules.getFirst( ));
				Double second = (Double)sample.getValue( molecules.getSecond( ));
				Double zero = new Double( 0.0 );
				if ( first.compareTo( zero ) == 0 || second.compareTo( zero ) == 0 ) {
					this.samples.remove( sample );
					continue;
				}
				data.add( first, second );
			}
			XYSeriesCollection dataset = new XYSeriesCollection( );
			dataset.addSeries( data );
			this.chart = ChartFactory.createScatterPlot(
				null, //title
				molecules.getFirst( ).toString( ), // x axis label
				molecules.getSecond( ).toString( ), // y axis label
				dataset, // plot data
				PlotOrientation.VERTICAL, // Plot Orientation
				false, // show legend
				false, // use tooltips
				false  // configure chart to generate URLs (?!)
			);
//			this.chart.getTitle( ).setFont( new Font( "Arial", Font.BOLD, 18 ));

			XYPlot plot = this.chart.getXYPlot( );
			plot.setBackgroundPaint( Color.WHITE );
			plot.setRangeGridlinePaint( Color.GRAY );
			plot.setDomainGridlinePaint( Color.GRAY );
			XYItemRenderer renderer = plot.getRenderer( );
			renderer.setBaseItemLabelGenerator( this );
			renderer.setBaseItemLabelsVisible( true );
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
			return this.samples.get( item ).toString( );
		}

		/**
		 * Draws the graph.
		 * 
		 * @param g The Graphics object of this component.
		 */
		public void paintComponent ( Graphics g ) {
			super.paintComponent( g );
			Dimension size = this.getSize( null );
			BufferedImage drawing = 
				this.chart.createBufferedImage( size.width, size.height );
			g.drawImage( drawing, 0, 0, Color.WHITE, this );
		}

		public List<Sample> getSamples( ) {
			return this.samples;
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
					correlationMethod, this.sampleVolume );
		}

		/**
		 * Redraws this component.
		 * 
		 * @param g The Graphics object corresponding to this component.
		 */
		public void paintComponent( Graphics g ) {
			Language language = Settings.getLanguage( );
			int [] xpos = { 20, 90, 150, 220 };

			g.setColor( Color.WHITE );
			g.fillRect( xpos[0]-5, 246, xpos[3]-xpos[0]+50, 16 );
			g.fillRect( 137, 56, 25, 16 );
			g.fillRect( 227, 26, 55, 16 );

			g.setColor( Color.BLACK );
			g.setFont( new Font( "SansSerif", Font.PLAIN, 14 ));
			g.drawString( language.get( "Current correlation coefficient" )+":", 
			              10, 40 );
			g.drawString( language.get( "Sample Volume N" )+":" , 10, 70 );
			g.drawString( "0.05",   xpos[0], 180 );
			g.drawString( "0.025",  xpos[1], 180 );
			g.drawString( "0.05",   xpos[2], 180 );
			g.drawString( "0.005",  xpos[3], 180 );
			g.drawString( "0.1",   xpos[0], 220 );
			g.drawString( "0.05",  xpos[1], 220 );
			g.drawString( "0.02",  xpos[2], 220 );
			g.drawString( "0.01",  xpos[3], 220 );

			g.setFont( new Font( "SansSerif", Font.BOLD, 14 ));
			g.drawString( language.get( "Table of critical values for" ), 10, 100 );
			g.drawString( language.get( "Correlation test" ), 10, 120 );
			g.drawString( language.get( 
				"One-tailed level of significance" ), 10, 160 );
			g.drawString( language.get( 
				"Two-tailed levelof significance" ), 10, 200 );
			for( int i=0; i < 4; i++ ) {
				if ( this.criticalValues[ i ] < 0 )
					g.drawString( "-", xpos[ i ], 260 );
				else
					g.drawString( Double.toString( 
						this.criticalValues[ i ]), xpos[ i ], 260 );
			}
			g.drawString( Integer.toString( this.sampleVolume ), 
			              140, 70 ); // sample volume
			g.drawString( String.format( "%.3f", this.coefficient ), 
			              230, 40 ); // correlation coefficient

			g.setFont( new Font( "SansSerif", Font.BOLD | Font.ITALIC, 14 ));
			g.setColor( Color.BLUE );
			g.drawString( Correlation.NAME[ correlationMethod ], 
			              125, 120 ); // correlation name;
		}

	}

}


