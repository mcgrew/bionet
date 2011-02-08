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

import edu.purdue.cc.jsysnet.util.Experiment;
import edu.purdue.cc.jsysnet.util.MoleculeGroup;
import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Statistics;
import edu.purdue.bbc.util.CurveFitting;
import edu.purdue.bbc.util.equation.Equation;
import edu.purdue.bbc.util.equation.Polynomial;

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
import java.util.Enumeration;
import java.text.AttributedString;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JLabel;
import javax.swing.tree.TreePath;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;

import org.apache.log4j.Logger;

public class ComparativeAnalysisDisplayPanel extends JPanel implements ComponentListener {
	private List <Experiment> experiments;
	private Set <Molecule> molecules;
	private JSplitPane mainSplitPane;
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
	private ButtonGroup fitButtonGroup;
	private boolean graphSplitPaneDividerLocationSet = false;

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
		this.fitSelectorPanel = new JPanel( new GridLayout( 4, 1 ));
		this.noFitButton = new JRadioButton( language.get( "No Fit" ));
		this.robustFitButton = new JRadioButton( language.get( "Robust Linear Fit" ));
		this.chiSquareFitButton = new JRadioButton( language.get( "Chi Square Fit" ));
		this.fitButtonGroup = new ButtonGroup( );
		this.fitButtonGroup.add( this.noFitButton );
		this.fitButtonGroup.add( this.robustFitButton );
		this.fitButtonGroup.add( this.chiSquareFitButton );
		this.noFitButton.setSelected( true );

		this.selectorTree = new SelectorTreePanel( experiments );
		this.mainSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		this.graphSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );

		this.fitSelectorPanel.add( new JLabel( language.get( "Visual Analysis" )));
		this.fitSelectorPanel.add( this.noFitButton );
		this.fitSelectorPanel.add( this.robustFitButton );
		this.fitSelectorPanel.add( this.chiSquareFitButton );
		this.topPanel.add( this.experimentGraph, BorderLayout.CENTER );
		JPanel topRightPanel = new JPanel( new BorderLayout( ));
		topRightPanel.add( this.fitSelectorPanel, BorderLayout.NORTH );
		topRightPanel.add( new JPanel( ), BorderLayout.CENTER );
		this.topPanel.add( topRightPanel, BorderLayout.EAST );
		this.bottomPanel.add( this.sampleGraph );

		this.add( mainSplitPane, BorderLayout.CENTER );
		this.mainSplitPane.setLeftComponent( this.selectorTree );
		this.mainSplitPane.setDividerLocation( 200 );
		this.mainSplitPane.setRightComponent( this.graphSplitPane ); 
		this.graphSplitPane.setTopComponent( this.topPanel );
		this.graphSplitPane.setBottomComponent( this.bottomPanel );

		this.noFitButton.addItemListener( this.experimentGraph );
		this.robustFitButton.addItemListener( this.experimentGraph );
		this.chiSquareFitButton.addItemListener( this.experimentGraph );
		this.selectorTree.getTree( ).addTreeSelectionListener( this.experimentGraph );
		this.selectorTree.getTree( ).addTreeSelectionListener( this.sampleGraph );
		this.selectorTree.getTree( ).addTreeCheckingListener( this.experimentGraph );
		this.selectorTree.getTree( ).addTreeCheckingListener( this.sampleGraph );
		this.addComponentListener( this );

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

	public void componentHidden( ComponentEvent e ) { } 
	public void componentMoved( ComponentEvent e ) { } 
	public void componentResized( ComponentEvent e ) { } 
	public void componentShown( ComponentEvent e ) { 
		if ( !graphSplitPaneDividerLocationSet ) {
			graphSplitPaneDividerLocationSet = true;
			this.graphSplitPane.setDividerLocation( 0.5 );
		}
	} 

	/**
	 * A class for displaying the selector tree
	 */
	private class SelectorTreePanel extends JPanel {
		private CheckboxTree tree;
		private Map <Molecule,DefaultMutableTreeNode> nodeMap;

		/**
		 * Creates a new SelectorTreePanel based on the passed in Experiments.
		 * 
		 * @param experiments The Experiment data to be shown in the tree.
		 */
		public SelectorTreePanel( List <Experiment> experiments ) {
			super( new BorderLayout( ));
			this.nodeMap = new HashMap<Molecule,DefaultMutableTreeNode>( );
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
					for ( Molecule m : e.getMoleculeGroup( group ).getMolecules( )) {
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
							// save the node in a map for later lookup.
							nodeMap.put( molecule, experimentNode ); 
							int sample = 1;
							for ( Number value : molecule.getValues( e.getSamples( ))) {
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
			this.tree.setSelectsByChecking( false );
			this.tree.getCheckingModel( ).setCheckingMode( 
				TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );
			this.add( new JScrollPane( tree ), BorderLayout.CENTER );
		}

		/**
		 * Returns the JTree object displayed in this panel.
		 * 
		 * @return The JTree object.
		 */
		public CheckboxTree getTree( ) {
			return this.tree;
		}

		/**
		 * Determines whether the tree node for the molecule is checked.
		 * 
		 * @param molecule The Molecule to search the tree for.
		 * @return True if the Molecule in the tree is checked.
		 */
		public boolean isChecked( Molecule molecule ) {
			return this.tree.isPathChecked( 
				new TreePath( this.nodeMap.get( molecule ).getPath( )));
		}

		/**
		 * Determines whether the tree node for the sample is checked.
		 * 
		 * @param molecule The molecule to search the tree for.
		 * @param sample The sample number to check the status of.
		 * @return True if the sample is checked.
		 */
		public List<Number> getSamplesFiltered( Molecule molecule, 
				Experiment experiment ) {
			DefaultMutableTreeNode molNode = this.nodeMap.get( molecule );
			List<Number> values = new ArrayList<Number>( 
				molecule.getSamples( experiment.getSamples( )));
			for( int i=0; i < values.size( ); i++ ){
				if ( !this.tree.isPathChecked( new TreePath( 
					((DefaultMutableTreeNode)molNode.getChildAt( i )).getPath( )))) {
					values.set( i, null );
				}
			}
			return values;
		}
	}

	/**
	 * A class for displaying molecular data across experiments
	 */
	private class ExperimentGraph extends JPanel 
		implements TreeSelectionListener,TreeCheckingListener,ItemListener {
		private JFreeChart chart;
		private SortedSet <Experiment> experiments;
		private XYDataset fitDataset;
		private Equation fitEquation;
		private LegendItemCollection legendItems;
		private LegendItemCollection singleExperimentLegendItems;
		private Stroke stroke;


		/**
		 * Creates a new ExperimentGraph panel to show data from the passed in 
		 * Experiments.
		 * 
		 * @param experiments A collection of experiments to show data from.
		 */
		public ExperimentGraph( Collection <Experiment> experiments ) {
			super( );
			this.experiments = new TreeSet( experiments );
			//this.fitDataset = new XYDataset( );
			this.stroke = new BasicStroke( 2 );
			this.singleExperimentLegendItems = new LegendItemCollection( );
			this.legendItems = new LegendItemCollection( );
			LegendItem meanLegendItem = 
				new LegendItem( "Mean", null, null, null,
				                 new Ellipse2D.Double( 0.0, 0.0, 4.0, 4.0 ),
				                 Color.BLACK, 
				                 this.stroke, Color.BLACK);
			LegendItem medianLegendItem = 
				new LegendItem( "Median", null, null, null,
				                new Line2D.Double( 0.0, 0.0, 9.0, 0.0 ),
				                this.stroke, Color.BLACK );
			LegendItem minMaxLegendItem1 = 
				new LegendItem( "Min/Max without Outliers", null, null, null,
				                new Line2D.Double( 0.0, 0.0, 9.0, 0.0 ),
				                this.stroke, Color.RED );
			LegendItem minMaxLegendItem2 = 
				new LegendItem( "Min/Max without Outliers", null, null, null,
				                new Line2D.Double( 0.0, 0.0, 9.0, 0.0 ),
				                this.stroke, Color.BLUE );
			LegendItem outlierLegendItem = 
				new LegendItem( "Outlier", null, null, null,
				                new Ellipse2D.Double( 0.0, 0.0, 4.0, 4.0 ),
				                Color.WHITE, 
				                this.stroke, Color.BLACK);
			this.singleExperimentLegendItems.add( meanLegendItem );
			this.singleExperimentLegendItems.add( medianLegendItem );
			this.singleExperimentLegendItems.add( minMaxLegendItem1 );
			this.singleExperimentLegendItems.add( outlierLegendItem );
			this.legendItems.add( meanLegendItem );
			this.legendItems.add( medianLegendItem );
			this.legendItems.add( minMaxLegendItem2 );
			this.legendItems.add( outlierLegendItem );

		}

		/**
		 * Sets the graph to display data from each experiment on the passed in 
		 * molecule.
		 *
		 * This could be a String with the molecule ID, or an instance of the 
		 * Molecule.
		 * 
		 * @param molecule An object whose toString( ) method returns the molecule 
		 *	id. 
		 */
		public void setGraph( Object molecule ) {
			Language language = Settings.getLanguage( ); 
			String id = molecule.toString( );
			DefaultBoxAndWhiskerXYDataset boxDataSet = 
				new DefaultBoxAndWhiskerXYDataset( new Integer( 1 ));
			double [] fitValues = new double[ this.experiments.size( ) + 1];
			fitValues[ 0 ] = Double.NaN;
			int expIndex = 1;
			int expCount = 0;
			for ( Experiment e : this.experiments ) {
				Molecule mol = e.getMolecule( id );
				fitValues[ expIndex ] = Double.NaN;
				if ( mol != null && selectorTree.isChecked( mol )) {
					expCount++;
					try {
						BoxAndWhiskerItem item = 
							BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics( 
								selectorTree.getSamplesFiltered( mol, e ));
						boxDataSet.add( new Date((long)expIndex), item );

						// robust fit uses the median instead of the mean.
						if ( robustFitButton.isSelected( )) { 
							fitValues[ expIndex ] = item.getMedian( ).doubleValue( );
						} else {
							fitValues[ expIndex ] = item.getMean( ).doubleValue( );
						}
					} catch ( IllegalArgumentException exc ) { 
					}
				}
				expIndex++;
			}
			// find the equation for the fitting curve
			this.fitEquation = null;
			if ( robustFitButton.isSelected( )) {
				this.fitEquation = CurveFitting.linearFit( fitValues );
			}
			if ( chiSquareFitButton.isSelected( )) {
				this.fitEquation = CurveFitting.chiSquareFit( fitValues );
			}
			XYSeries fitSeries = new XYSeries( language.get( "Fit Line" ));
			int xMax = this.experiments.size( );
			if ( fitEquation == null ) {
				for ( int i=1; i <= xMax; i++ ) {
					if ( !Double.isNaN( fitValues[ i ] ))
						fitSeries.add( i, fitValues[ i ]);
					}
			} else {
				for ( double i=1; i <= xMax; i+= 0.01 ) {
					try {
						fitSeries.add( i, fitEquation.solve( i ));
					} catch ( IllegalArgumentException exc ) {
						Logger.getLogger( getClass( )).debug( 
							"Unable to find solution for x=" + i, exc );
					}
				}
			}
			this.chart = ChartFactory.createBoxAndWhiskerChart (
				String.format( language.get( "%s across experiments" ), 
					molecule.toString( )), //title
				language.get( "Experiment" ), // x axis label
				language.get( "Concentration" ), // y axis label
				boxDataSet, // plot data
				true // show legend
			);
			XYSeriesCollection fitDataset = new XYSeriesCollection( );
			fitDataset.addSeries( fitSeries );
			XYPlot plot = this.chart.getXYPlot( );
			plot.setBackgroundPaint( Color.WHITE );
			plot.setRangeGridlinePaint( Color.GRAY );
			plot.setDomainGridlinePaint( Color.GRAY );
			plot.setDomainAxis( new NumberAxis( ));
			plot.getDomainAxis( ).setStandardTickUnits( NumberAxis.createIntegerTickUnits( ));
			XYItemRenderer boxRenderer = plot.getRenderer( );
			boxRenderer.setSeriesStroke( 0, this.stroke );
			boxRenderer.setSeriesOutlineStroke( 0, this.stroke );

			plot.setDataset( 1, fitDataset );
			XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer( );
			lineRenderer.setSeriesShapesVisible( 0, false );
			lineRenderer.setSeriesStroke( 0, this.stroke );
			plot.setRenderer( 1, lineRenderer );

			if ( expCount > 1 ) {
				plot.setFixedLegendItems( this.legendItems );
			} else {
				plot.setFixedLegendItems( this.singleExperimentLegendItems );
			}

			
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
				BufferedImage drawing =
					this.chart.createBufferedImage( size.width, size.height );
				g.drawImage( drawing, 0, 0, Color.WHITE, this );
				if ( this.fitEquation != null ) {
					FontMetrics f = g.getFontMetrics( );
					String notation = "y="+this.fitEquation.toString( );
					g.drawString( notation, 
					              size.width - f.stringWidth( notation ) - 20,
												20 );
				}
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

		/**
		 * The valueChanged method of the TreeCheckingListener interface.
		 * 
		 * @param e The event which triggered this action.
		 */
		public void valueChanged( TreeCheckingEvent e ) {
			TreePath path = selectorTree.getTree( ).getSelectionPath( );
			int level = path.getPathCount( );
			if ( level > MOLECULE ) {
				this.setGraph( path.getPathComponent( MOLECULE ));
			} else {
				this.chart = null;
			}
			this.repaint( );
		}

		/**
		 * The itemStateChanged method of the ItemListener interface
		 * 
		 * @param e The event which triggered this action.
		 */
		public void itemStateChanged( ItemEvent e ) {
			if ( e.getStateChange( ) == ItemEvent.SELECTED ) {
				TreePath path = selectorTree.getTree( ).getSelectionPath( );
				if ( path != null ) {
					int level = path.getPathCount( );
					if ( level > MOLECULE ) {
						this.setGraph( path.getPathComponent( MOLECULE ));
					} else {
						this.chart = null;
					}
					this.repaint( );
				}
			}
		}
	}

	/**
	 * A class for showing sample concentrations in a graph.
	 */
	private class SampleGraph extends JPanel 
		implements TreeSelectionListener,TreeCheckingListener {

		private SortedSet <Experiment> experiments;
		private JFreeChart chart;
		private Object selectedObject;

		public SampleGraph( Collection <Experiment> experiments ) {
			super( );
			this.experiments = new TreeSet<Experiment>( experiments );
		}

		/**
		 * Sets the graph to display data from each experiment on the passed in molecule id.
		 * 
		 * @param molecule An object whose toString( ) method returns the molecule id.
		 */
		public boolean setGraph( Object moleculeId ) {
			Language language = Settings.getLanguage( );
			XYSeriesCollection dataset = new XYSeriesCollection( );
			CustomXYLineAndShapeRenderer renderer = 
				new CustomXYLineAndShapeRenderer( true, true );
			for ( Experiment experiment : this.experiments ) {
				Molecule molecule = experiment.getMolecule( moleculeId.toString( ));
				if ( molecule != null && selectorTree.isChecked( molecule )) {
					XYSeries data = new XYSeries( String.format( "%s %s",
						language.get( "Experiment" ),
						experiment.getAttribute( "exp_id" )
					));
					List<Number> samples = 
						selectorTree.getSamplesFiltered( molecule, experiment );
					int index = 1;
					for ( Number value : samples )	{
						if ( value != null )
							data.add( index, value );
						index++;
					}
					renderer.setSeriesOutlierInfo( dataset.getSeriesCount( ),
						BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics( 
							samples ));
					dataset.addSeries( data );
				}
			}
			this.chart = ChartFactory.createXYLineChart( 
				String.format( language.get( "%s sample concentrations" ), 
				moleculeId.toString( )),         // title
				language.get( "Sample" ),        // x axis label
				language.get( "Concentration" ), // y axis label
				dataset,                         // plot data
				PlotOrientation.VERTICAL,        // Plot Orientation
				true,                            // show legend
				false,                           // use tooltips
				false                            // configure chart to generate URLs (?!)
			);

			XYPlot plot = this.chart.getXYPlot( );
//			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer( 0 );
			plot.setRenderer( renderer );
			for ( int i=0,c=plot.getSeriesCount( ); i < c; i++ ) {
					renderer.setSeriesPaint( i, 
						Color.getHSBColor( (float)i/c, 1.0f, 0.5f ));
				renderer.setSeriesStroke( i, new BasicStroke( 2 ));
				renderer.setSeriesShapesVisible( i, true );
			}
			plot.setBackgroundPaint( Color.WHITE );
			plot.setRangeGridlinePaint( Color.GRAY );
			plot.setDomainGridlinePaint( Color.GRAY );
			plot.getDomainAxis( ).setStandardTickUnits( NumberAxis.createIntegerTickUnits( ));
			LegendItemCollection legendItems = plot.getLegendItems( );
			if ( legendItems == null ) {
				legendItems = new LegendItemCollection( );
			}
			legendItems.add( renderer.getOutlierLegendItem( ));
			plot.setFixedLegendItems( legendItems );
			return true;
		}

		/**
		 * Sets the graph to display data about only the given molecule object.
		 * 
		 * @param molecule The molecule to display data about.
		 * @return true if setting the graph was successful.
		 */
		public boolean setGraph( Molecule molecule, Experiment experiment ) {
			Language language = Settings.getLanguage( );
			CustomXYLineAndShapeRenderer renderer = 
				new CustomXYLineAndShapeRenderer( true, true );
			XYSeries data = new XYSeries( language.get( "Sample Data" ));
			List<Number> samples = 
				selectorTree.getSamplesFiltered( molecule, experiment );
			renderer.setSeriesOutlierInfo( 0,
				BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics( 
					samples ));
			int index = 1;
			for ( Number value : samples )	{
				if ( value != null )
					data.add( index, value );
				index++;
			}
			XYSeriesCollection dataset = new XYSeriesCollection( );
			dataset.addSeries( data );
			this.chart = ChartFactory.createXYLineChart( 
				String.format( language.get( "%s sample concentrations" ), 
					molecule.toString( )) + " " +
					language.get( "Experiment" ) + " " +
					experiment.getId( ), // title
				language.get( "Sample" ),        // x axis label
				language.get( "Concentration" ), // y axis label
				dataset,                         // plot data
				PlotOrientation.VERTICAL,        // Plot Orientation
				false,                           // show legend
				false,                           // use tooltips
				false                            // configure chart to generate URLs (?!)
			);

			XYPlot plot = this.chart.getXYPlot( );
			plot.setRenderer( renderer );
			renderer.setSeriesStroke( 0, new BasicStroke( 2 ));
			renderer.setSeriesShapesVisible( 0, true );
			// find the index of this experiment for appropriate coloring.
			int expIndex = 0;
			for ( Experiment exp : this.experiments ) {
				if ( exp == experiment )
					break;
				expIndex++;
			}
			renderer.setSeriesPaint( 0, 
				Color.getHSBColor( (float)expIndex/this.experiments.size( ), 
				1.0f, 0.5f ));
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
							path.getPathComponent( MOLECULE ).toString( )), experiment );
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
		 * The valueChanged method of the TreeCheckingListner interface.
		 * 
		 * @param e The event which triggered this action.
		 */
		public void valueChanged( TreeCheckingEvent e ) {
			this.valueChanged( new TreeSelectionEvent( 
				e.getSource( ),
				selectorTree.getTree( ).getSelectionPath( ),
				false, null, null 
			));
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

	private class CustomXYLineAndShapeRenderer extends XYLineAndShapeRenderer {
		private List<BoxAndWhiskerItem> outlierInfo;
		private Paint outlierPaint;
		private Shape outlierShape;
		private Stroke outlierStroke;
		private XYDataset dataset;
		private int currentPass;

		public CustomXYLineAndShapeRenderer( ) {
			super( );
			this.outlierInfo = new ArrayList<BoxAndWhiskerItem>( );
			this.outlierPaint = Color.GRAY;
		}

		public CustomXYLineAndShapeRenderer( boolean lines, boolean shapes ) {
			super( lines, shapes );
			this.outlierInfo = new ArrayList<BoxAndWhiskerItem>( );
			this.outlierPaint = Color.RED;
			this.outlierStroke = new BasicStroke( 2 );
			this.outlierShape = new Polygon(  // Star shape;
					new int[]{ 4, 1, 0, -1, -4, -1,  0,  1 },
					new int[]{ 0, 1, 4,  1,  0, -1, -4, -1 },
					8 );

		}

		/**
		 * Draws an item on the graph. This method is being overridden only to
		 * keep track of which pass we are on, as there doesn't seem to be an
		 * easy way to determine the current pass.
		 * 
		 * @param g2 - the graphics device.
		 * @param state - the renderer state.
		 * @param dataArea - the area within which the data is being drawn.
		 * @param info - collects information about the drawing.
		 * @param plot - the plot (can be used to obtain standard color information etc).
		 * @param domainAxis - the domain axis.
		 * @param rangeAxis - the range axis.
		 * @param dataset - the dataset.
		 * @param series - the series index (zero-based).
		 * @param item - the item index (zero-based).
		 * @param crosshairState - crosshair information for the plot (null permitted).
		 * @param pass - the pass index.
		 */
		public void drawItem( Graphics2D g2, XYItemRendererState state, 
		                      Rectangle2D dataArea, PlotRenderingInfo info, 
													XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, 
													XYDataset dataset, int series, int item, 
													CrosshairState crosshairState, int pass ) {
			this.currentPass = pass;
			super.drawItem( g2, state, dataArea, info, plot, domainAxis, rangeAxis, 
			                dataset, series, item, crosshairState, pass );
		}

		public void setSeriesOutlierInfo( int index, BoxAndWhiskerItem item ) {
			while ( index >= outlierInfo.size( )){
				outlierInfo.add( null );
			}
			outlierInfo.set( index, item );
		}

		public BoxAndWhiskerItem getSeriesOutlierInfo( int index ) {
			if ( index >= outlierInfo.size( ))
				return null;
			return outlierInfo.get( index );
		}

		private boolean isOutlier( int series, int item ) {
			if ( this.dataset == null ) {
				this.dataset = this.getPlot( ).getDataset( 0 ); 
			}
			BoxAndWhiskerItem stats = this.getSeriesOutlierInfo( series );
			if ( stats == null )
				return false;
			double y = this.dataset.getYValue( series, item );
			return ( Double.compare( y, stats.getMinRegularValue( ).doubleValue( )) < 0 || 
			         Double.compare( y, stats.getMaxRegularValue( ).doubleValue( )) > 0 );
		}

		/**
		 * Overrides XYLineAndShapeRenderer.getItemShape( ).
		 * 
		 * @param row The series to get the Shape for.
		 * @param column The item to get the Shape for.
		 * @return The appropriate Shape for this item.
		 */
		public Shape getItemShape( int row, int column ) {
			if ( this.isOutlier( row, column )) {
				return this.outlierShape;
			} else {
				return super.getItemShape( row, column );
			}
		}

		/**
		 * Overrides XYLineAndShapeRenderer.getItemPaint( ). This method
		 * returns a different paint for outliers.
		 * 
		 * @param row The series to get the Paint for.
		 * @param column The item to get the Paint  for.
		 * @return The appropriate Paint for this item.
		 */
		public Paint getItemPaint( int row, int column ) {
			if ( this.isItemPass( this.currentPass ) && this.isOutlier( row, column )) {
				return this.outlierPaint;
			} else {
				return super.getItemPaint( row, column );
			}
		}

		public LegendItem getOutlierLegendItem( ) {
			return new LegendItem( "Outlier", null, null, null,
			                       this.outlierShape,
														 this.outlierPaint,
														 this.outlierStroke,
														 this.outlierPaint );
		}
	}
}


