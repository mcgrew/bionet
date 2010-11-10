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

import edu.purdue.jsysnet.util.Experiment;
import edu.purdue.jsysnet.util.MoleculeGroup;
import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Settings;
import edu.purdue.jsysnet.util.Language;
import edu.purdue.jsysnet.util.Statistics;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Arrays;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JRadioButton;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.labels.XYItemLabelGenerator;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;

public class ComparativeAnalysisDisplayPanel extends JPanel {
	private List <Experiment> experiments;
	private Set <Molecule> molecules;
	private JSplitPane mainSplitPane;
	private JSplitPane leftSplitPane;
	private JSplitPane graphSplitPane;
	private SelectorTreePanel selectorTree;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private ExperimentGraph experimentGraph;
	private SampleGraph sampleGraph;
	private JPanel fitSelectorPanel;
	private JRadioButton noFitButton;
	private JRadioButton robustFitButton;
	private JRadioButton chiSquareFitButton;

	private static final int MOLECULEGROUP = 1;
	private static final int MOLECULE      = 2;
	private static final int EXPERIMENT    = 3;
	private static final int SAMPLE        = 4;

	public ComparativeAnalysisDisplayPanel ( ) {
		super( new BorderLayout( ));
	}

	public boolean createGraph( List <Experiment> experiments ) {
		this.experiments = experiments;
		this.molecules = new TreeSet<Molecule>( );
		for ( Experiment e : experiments ) {
			molecules.addAll( e.getMolecules( ));
		}
		Language language = Settings.getLanguage( );

		this.topPanel = new JPanel( new BorderLayout( ));
		this.bottomPanel = new JPanel( new BorderLayout( ));
		this.experimentGraph = new ExperimentGraph( experiments );
		this.sampleGraph = new SampleGraph( experiments );
		this.fitSelectorPanel = new JPanel( new GridLayout( 3, 1 ));
		this.noFitButton = new JRadioButton( language.get( "No Fit" ));
		this.robustFitButton = new JRadioButton( language.get( "Robust Fit" ));
		this.chiSquareFitButton = new JRadioButton( language.get( "Chi Square Fit" ));

		this.selectorTree = new SelectorTreePanel( experiments );
		this.mainSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		this.leftSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		this.graphSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );

		this.fitSelectorPanel.add( this.noFitButton );
		this.fitSelectorPanel.add( this.robustFitButton );
		this.fitSelectorPanel.add( this.chiSquareFitButton );
		this.topPanel.add( this.experimentGraph, BorderLayout.CENTER );
		this.topPanel.add( this.fitSelectorPanel, BorderLayout.EAST );
		this.bottomPanel.add( this.sampleGraph );

		this.add( mainSplitPane, BorderLayout.CENTER );
		this.mainSplitPane.setLeftComponent( leftSplitPane );
		this.leftSplitPane.setTopComponent( selectorTree );
		this.mainSplitPane.setRightComponent( graphSplitPane ); 
		this.mainSplitPane.setDividerLocation( 200 );
		this.graphSplitPane.setTopComponent( this.topPanel );
		this.graphSplitPane.setBottomComponent( this.bottomPanel );

		this.selectorTree.getTree( ).addTreeSelectionListener( this.experimentGraph );
		this.selectorTree.getTree( ).addTreeSelectionListener( this.sampleGraph );

		return true;
	}

	public boolean addExperiment( Experiment experiment ) {
		this.molecules.addAll( experiment.getMolecules( ));
		return experiments.add( experiment );
	}

	public boolean removeExperiment( Experiment experiment ) {

		return experiments.remove( experiment );
	}

	public String getTitle( ) {
		return Settings.getLanguage( ).get( "Comparative Analysis" );
	}

	/**
	 * A class for displaying the selector tree
	 */
	private class SelectorTreePanel extends JPanel {
		private CheckboxTree tree;

		public SelectorTreePanel( List <Experiment> experiments ) {
			super( new BorderLayout( ));
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(	
				Settings.getLanguage( ).get( "All Experiments" ));

			// first get all possible group names
			Set <String> groups = new TreeSet<String>( );
			for ( Experiment e : experiments ) {
				groups.addAll( Arrays.asList( e.getMoleculeGroupNames( )));
			}
			// now get all possible molecule ids
			HashMap<String,Set<String>> moleculeIds = new HashMap <String,Set<String>>( );
			for ( String group : groups ) {
				Set set = moleculeIds.get( group );
				if ( set == null ) {
					set = new TreeSet<String>( );
					moleculeIds.put( group, set );
				}
				for ( Experiment e : experiments ) {
					for ( Molecule m : molecules ) {
						set.add( m.getAttribute( "id" ));
					}
				}
			}

			for ( String groupName : groups ) {
				DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode( groupName );
				for ( String molId : moleculeIds.get( groupName )) {
					DefaultMutableTreeNode moleculeNode = new DefaultMutableTreeNode( molId );
					for ( Experiment e : experiments ) {
						Molecule molecule = e.getMolecule( molId );
						if ( molecule != null ) {
							DefaultMutableTreeNode experimentNode = new DefaultMutableTreeNode( e.toString( ));
							int sample = 1;
							for ( Number value : molecule.getSamples( )) {
								DefaultMutableTreeNode sampleNode = new DefaultMutableTreeNode( "S" + sample++ );	
								experimentNode.add( sampleNode );
							}
							moleculeNode.add( experimentNode );
						}
					}
					groupNode.add( moleculeNode );
				}
				rootNode.add( groupNode );
			}
			this.tree = new CheckboxTree( rootNode );
			this.tree.setRootVisible( false );
			this.tree.setCheckingPath( new TreePath( rootNode ));
			this.add( new JScrollPane( tree ), BorderLayout.CENTER );
		}

		public JTree getTree( ) {
			return this.tree;
		}

		public void addExperiment( Experiment experiment ) {
		}

		public void removeExperiment( Experiment experiment ) {
		}
	}

	/**
	 * A class for displaying molecular data across experiments
	 */
	private class ExperimentGraph extends JPanel implements TreeSelectionListener {
		private JFreeChart chart;
		private SortedSet <Experiment> experiments;
		
		public ExperimentGraph( Collection <Experiment> experiments ) {
			super( );
			this.experiments = new TreeSet( experiments );
		}

		public void setGraph( Object molecule ) {
			Language language = Settings.getLanguage( ); 
			String id = molecule.toString( );
			DefaultBoxAndWhiskerXYDataset dataSet = new DefaultBoxAndWhiskerXYDataset( new Integer( 1 ));
			int expIndex = 1;
			for ( Experiment e : this.experiments ) {
				double mean = 0.0, median = 0.0, min = 0.0, max = 0.0;
				Molecule mol = e.getMolecule( id );
				if ( mol != null ) {
					dataSet.add( 
						new Date((long)expIndex),
						BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics( mol.getSamples( ))
					);
				}
				expIndex++;
			}
			this.chart = ChartFactory.createBoxAndWhiskerChart (
				String.format( language.get( "%s across experiments" ), 
					molecule.toString( )), //title
				"Experiment", // x axis label
				"Concentration", // y axis label
				dataSet, // plot data
				false // show legend
			);
			XYPlot plot = this.chart.getXYPlot( );
			plot.setBackgroundPaint( Color.WHITE );
			plot.setRangeGridlinePaint( Color.GRAY );
			plot.setDomainGridlinePaint( Color.GRAY );
			plot.setDomainAxis( new NumberAxis( ));
			plot.getDomainAxis( ).setStandardTickUnits( NumberAxis.createIntegerTickUnits( ));
//			XYItemRenderer = plot.getRenderer( );
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
				BufferedImage drawing = this.chart.createBufferedImage( size.width, size.height );
				g.drawImage( drawing, 0, 0, Color.WHITE, this );
			}
		}

		/**
		 * The valueChanged method of the TreeSelectionListener interface
		 * 
		 * @param e The event which triggered this action.
		 */
		public void valueChanged( TreeSelectionEvent e ) {
			TreePath path = e.getPath( );
			int level = path.getPathCount( );
			if ( level > MOLECULE ) {
				this.setGraph( path.getPathComponent( MOLECULE ));
			} else {
				this.chart = null;
			}
			this.repaint( );
		}
	}

	/**
	 * A class for showing sample concentrations in a graph.
	 */
	private class SampleGraph extends JPanel implements TreeSelectionListener {
		private SortedSet <Experiment> experiments;
		private JFreeChart chart;

		public SampleGraph( Collection <Experiment> experiments ) {
			super( );
			this.experiments = new TreeSet<Experiment>( experiments );
		}

		public boolean setGraph( Object moleculeId ) {
			Language language = Settings.getLanguage( );
			XYSeriesCollection dataset = new XYSeriesCollection( );
			for ( Experiment experiment : this.experiments ) {
				Molecule molecule = experiment.getMolecule( moleculeId.toString( ));
				if ( molecule != null ) {
					XYSeries data = new XYSeries( String.format( "%s - %s %s",
						language.get( "Sample Data" ),
						language.get( "Experiment" ),
						experiment.getAttribute( "exp_id" )
					));
					List<Number> samples = molecule.getSamples( );
					int index = 1;
					for ( Number value : samples )	{
						data.add( index++, value );
					}
					dataset.addSeries( data );
				}
			}
			this.chart = ChartFactory.createXYLineChart(
				null, //title
				language.get( "Sample" ), // x axis label
				language.get( "Concentration" ), // y axis label
				dataset, // plot data
				PlotOrientation.VERTICAL, // Plot Orientation
				false, // show legend
				false, // use tooltips
				false  // configure chart to generate URLs (?!)
			);

			XYPlot plot = this.chart.getXYPlot( );
			plot.setBackgroundPaint( Color.WHITE );
			plot.setRangeGridlinePaint( Color.GRAY );
			plot.setDomainGridlinePaint( Color.GRAY );
			plot.getDomainAxis( ).setStandardTickUnits( NumberAxis.createIntegerTickUnits( ));
			return true;
		}

		public boolean setGraph( Molecule molecule ) {
			Language language = Settings.getLanguage( );
			XYSeries data = new XYSeries( language.get( "Sample Data" ));
			List<Number> samples = molecule.getSamples( );
			int index = 1;
			for ( Number value : samples )	{
				data.add( index++, value );
			}
			XYSeriesCollection dataset = new XYSeriesCollection( );
			dataset.addSeries( data );
			this.chart = ChartFactory.createXYLineChart(
				null, //title
				language.get( "Sample" ), // x axis label
				language.get( "Concentration" ), // y axis label
				dataset, // plot data
				PlotOrientation.VERTICAL, // Plot Orientation
				false, // show legend
				false, // use tooltips
				false  // configure chart to generate URLs (?!)
			);

			XYPlot plot = this.chart.getXYPlot( );
			plot.setBackgroundPaint( Color.WHITE );
			plot.setRangeGridlinePaint( Color.GRAY );
			plot.setDomainGridlinePaint( Color.GRAY );
			plot.getDomainAxis( ).setStandardTickUnits( NumberAxis.createIntegerTickUnits( ));

			return true;
		}

		/**
		 * The valueChanged method of the TreeSelectionListener interface
		 * 
		 * @param e The event which triggered this action.
		 */
		public void valueChanged( TreeSelectionEvent e ) {
			TreePath path = e.getPath( );
			int level = path.getPathCount( );
			if ( level > EXPERIMENT ) {
				String selectedExperiment = path.getPathComponent( EXPERIMENT ).toString( );
				for ( Experiment experiment : experiments ) {
					if ( experiment.toString( ).equals( selectedExperiment )) {
						this.setGraph( experiment.getMolecule( 
							path.getPathComponent( MOLECULE ).toString( )));
						break;
					}
				}
			} else if ( level > MOLECULE ) { 
				this.setGraph( path.getPathComponent( MOLECULE ));
			} else {
				this.chart = null;
			}
			this.repaint( );
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
				BufferedImage drawing = this.chart.createBufferedImage( size.width, size.height );
				g.drawImage( drawing, 0, 0, Color.WHITE, this );
			}
		}

	}

}


