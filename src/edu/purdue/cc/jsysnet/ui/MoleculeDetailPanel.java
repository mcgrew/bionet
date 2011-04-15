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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Range;
import edu.purdue.bbc.util.Settings;
import edu.purdue.cc.jsysnet.util.Correlation;
import edu.purdue.cc.jsysnet.util.Experiment;
import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.cc.jsysnet.util.Sample;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.apache.log4j.Logger;

public class MoleculeDetailPanel extends JPanel implements ActionListener {
	private DetailWindow detailWindow;
	private Experiment experiment;
	private JButton showCorrelationButton;
	private JButton showElementButton;
	private JLabel coefLabel;
	private JLabel selectedMoleculeLabel;
	private JTable correlationsTable;
	private JTable moleculeDetailTable;
	private Molecule molecule;
	private Range correlationRange;
	private int correlationMethod;

	public MoleculeDetailPanel ( Molecule molecule, Range range, 
	                             DetailWindow detailWindow, 
	                             int correlationMethod ) {
		super( new BorderLayout( ));
		Logger logger = Logger.getLogger( getClass( ));
		this.molecule = molecule;
		this.experiment = detailWindow.getExperiment( );
		this.correlationMethod = correlationMethod;
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
			                              this.correlationRange,
			                              this.correlationMethod );
		this.selectedMoleculeLabel = new JLabel( language.get( "Selected Molecule" ));
		this.showElementButton = new JButton( language.get( "Show Element" ));
		this.showCorrelationButton = new JButton( language.get( "Show Correlation" ));
		this.coefLabel = new JLabel( String.format( 
			language.get( "Correlations between %.3f and %.3f" ),
			this.correlationRange.getMin( ),
			this.correlationRange.getMax( )));
//		JPanel leftPanel = new JPanel( new BorderLayout( ));
//		JPanel rightPanel = new JPanel( new BorderLayout( ));
		JPanel moleculePanel = new JPanel( new BorderLayout( ));
		JPanel correlationPanel = new JPanel( new BorderLayout( ));
		JPanel buttonPanel = new JPanel( new BorderLayout( ));
		this.showElementButton.addActionListener( this );
		this.showCorrelationButton.addActionListener( this );
		buttonPanel.add( this.showElementButton, BorderLayout.WEST );
		buttonPanel.add( this.showCorrelationButton, BorderLayout.EAST );

		moleculePanel.add( this.selectedMoleculeLabel, BorderLayout.NORTH );
		moleculePanel.add( new JScrollPane( this.moleculeDetailTable ), BorderLayout.CENTER );

		correlationPanel.add( this.coefLabel, BorderLayout.NORTH );
		correlationPanel.add( new JScrollPane( this.correlationsTable ), BorderLayout.CENTER );
		correlationPanel.add( buttonPanel, BorderLayout.SOUTH );

		JSplitPane leftPanel = new JSplitPane( JSplitPane.VERTICAL_SPLIT, moleculePanel, correlationPanel );
		leftPanel.setDividerLocation( 200 );
//		leftPanel.add( moleculePanel, BorderLayout.NORTH );
//		leftPanel.add( correlationPanel, BorderLayout.CENTER );

		JPanel rightPanel = new ConcentrationGraph( 
			this.molecule.getSampleMap( experiment.getSamples( )));

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
				Settings.getLanguage( ).get( 
					"You must select a molecule from the table to view its details" ));
		}

	}

	private List <Molecule> getMoleculesInRange( ) {
		List <Molecule> returnValue = new ArrayList( );
		for ( Correlation c : this.experiment.getCorrelations( molecule )) {
			if ( this.correlationRange.contains( 
				     Math.abs( c.getValue( this.correlationMethod )))) {
					returnValue.add( c.getOpposite( this.molecule ));
			}
		}
		return returnValue;
	}

	private class ConcentrationGraph extends JPanel {
		private JFreeChart chart;

		public ConcentrationGraph( Map<Sample,Number> sampleMap ) {
			super( );
			Language language = Settings.getLanguage( );
			XYSeriesCollection dataset = new XYSeriesCollection( );

			List<Sample> sampleList = new ArrayList<Sample>( sampleMap.keySet( ));
			XYSeries data = new XYSeries( molecule.getId( ));
			TickUnits tickUnits = new TickUnits( );
			int index = 0;
			for ( Map.Entry<Sample,Number> sample : sampleMap.entrySet( )) {
				data.add( index, sample.getValue( ));
				tickUnits.add( new SampleTickUnit( index, sampleList ));
				index++;
			}
			dataset.addSeries( data );
			
			this.chart = ChartFactory.createXYLineChart( 
				String.format( language.get( "%s sample concentrations" ), 
				molecule.getId( )),               // title
				language.get( "Sample" ),        // x axis label
				language.get( "Concentration" ), // y axis label
				dataset,                         // plot data
				PlotOrientation.VERTICAL,        // Plot Orientation
				false,                           // show legend
				false,                           // use tooltips
				false                            // configure chart to generate URLs (?)
			);
			XYPlot plot = this.chart.getXYPlot( );
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer( );
			renderer.setSeriesPaint( 0, Color.getHSBColor( 0.5f, 1.0f, 0.5f ));
			renderer.setSeriesStroke( 0, new BasicStroke( 2 ));
			renderer.setSeriesShapesVisible( 0, true );
			plot.setBackgroundPaint( Color.WHITE );
			plot.setRangeGridlinePaint( Color.GRAY );
			plot.setDomainGridlinePaint( Color.GRAY );
			plot.getDomainAxis( ).setStandardTickUnits( tickUnits );
			plot.getDomainAxis( ).setVerticalTickLabels( true );
		}

		/**
		 * Draws the graph.
		 * 
		 * @param g The Graphics object of this component.
		 */
		public void paintComponent ( Graphics g ) {
			super.paintComponent( g );
			if ( chart != null ) {
				Dimension size = this.getSize( null );
				BufferedImage drawing = this.chart.createBufferedImage( size.width, 
				                                                        size.height );
				g.drawImage( drawing, 0, 0, Color.WHITE, this );
			}
		}
	}
}
