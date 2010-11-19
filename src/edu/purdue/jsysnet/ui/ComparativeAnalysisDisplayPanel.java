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
import java.util.Enumeration;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
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
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.jfree.chart.JFreeChart;
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
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.labels.XYItemLabelGenerator;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;

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
		this.robustFitButton = new JRadioButton( language.get( "Robust Fit" ));
		this.chiSquareFitButton = new JRadioButton( language.get( "Chi Square Fit" ));
		this.fitButtonGroup = new ButtonGroup( );
		this.fitButtonGroup.add( this.noFitButton );
		this.fitButtonGroup.add( this.robustFitButton );
		this.fitButtonGroup.add( this.chiSquareFitButton );
		this.noFitButton.setSelected( true );
		this.noFitButton.addChangeListener( this.experimentGraph );
		this.robustFitButton.addChangeListener( this.experimentGraph );
		this.chiSquareFitButton.addChangeListener( this.experimentGraph );

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
			this.tree.setSelectsByChecking( false );
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
		public List<Number> getSamplesFiltered( Molecule molecule ) {
			DefaultMutableTreeNode molNode = this.nodeMap.get( molecule );
			List<Number> samples = new ArrayList<Number>( molecule.getSamples( ));
			for( int i=0; i < samples.size( ); i++ ){
				if ( !this.tree.isPathChecked( new TreePath( 
					((DefaultMutableTreeNode)molNode.getChildAt( i )).getPath( )))) {
					samples.set( i, null );
				}
			}
			return samples;
		}
	}

	/**
	 * A class for displaying molecular data across experiments
	 */
	private class ExperimentGraph extends JPanel 
		implements TreeSelectionListener,TreeCheckingListener,ChangeListener {
		private JFreeChart chart;
		private SortedSet <Experiment> experiments;
		private XYDataset fitDataset;
		
		/**
		 * Creates a new ExperimentGraph panel to show data from the passed in Experiments.
		 * 
		 * @param experiments A collection of experiments to show data from.
		 */
		public ExperimentGraph( Collection <Experiment> experiments ) {
			super( );
			this.experiments = new TreeSet( experiments );
			//this.fitDataset = new XYDataset( );
		}

		/**
		 * Sets the graph to display data from each experiment on the passed in molecule.
		 * This could be a String with the molecule ID, or an instance of the Molecule.
		 * 
		 * @param molecule An object whose toString( ) method returns the molecule id.
		 */
		public void setGraph( Object molecule ) {
			Language language = Settings.getLanguage( ); 
			String id = molecule.toString( );
			DefaultBoxAndWhiskerXYDataset dataSet = new DefaultBoxAndWhiskerXYDataset( new Integer( 1 ));
			int expIndex = 1;
			for ( Experiment e : this.experiments ) {
				Molecule mol = e.getMolecule( id );
				if ( mol != null && selectorTree.isChecked( mol )) {
					try {
						dataSet.add( 
							new Date((long)expIndex),
							BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics( 
								selectorTree.getSamplesFiltered( mol ))
						);
					} catch ( IllegalArgumentException exc ) { }
				}
				expIndex++;
			}
			this.chart = ChartFactory.createBoxAndWhiskerChart (
				String.format( language.get( "%s across experiments" ), 
					molecule.toString( )), //title
				language.get( "Experiment" ), // x axis label
				language.get( "Concentration" ), // y axis label
				dataSet, // plot data
				false // show legend
			);
			XYPlot boxPlot = this.chart.getXYPlot( );
			boxPlot.setBackgroundPaint( Color.WHITE );
			boxPlot.setRangeGridlinePaint( Color.GRAY );
			boxPlot.setDomainGridlinePaint( Color.GRAY );
			boxPlot.setDomainAxis( new NumberAxis( ));
			boxPlot.getDomainAxis( ).setStandardTickUnits( NumberAxis.createIntegerTickUnits( ));
//			XYItemRenderer = boxPlot.getRenderer( );
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

		/**
		 * The valueChanged method of the TreeCheckingListener interface.
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
		 * The stateChanged method of the ChangeListener interface
		 * 
		 * @param e The event which triggered this action.
		 */
		public void stateChanged( ChangeEvent e ) {
			Object item = e.getSource( );
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
			for ( Experiment experiment : this.experiments ) {
				Molecule molecule = experiment.getMolecule( moleculeId.toString( ));
				if ( molecule != null && selectorTree.isChecked( molecule )) {
					XYSeries data = new XYSeries( String.format( "%s %s",
						language.get( "Experiment" ),
						experiment.getAttribute( "exp_id" )
					));
					List<Number> samples = selectorTree.getSamplesFiltered( molecule );
					int index = 1;
					for ( Number value : samples )	{
						if ( value != null )
							data.add( index, value );
						index++;
					}
					dataset.addSeries( data );
				}
			}
			this.chart = ChartFactory.createXYLineChart( 
				String.format( language.get( "%s sample concentrations" ), 
				moleculeId.toString( )), //title
				language.get( "Sample" ), // x axis label
				language.get( "Concentration" ), // y axis label
				dataset, // plot data
				PlotOrientation.VERTICAL, // Plot Orientation
				true, // show legend
				false, // use tooltips
				false  // configure chart to generate URLs (?!)
			);

			XYPlot plot = this.chart.getXYPlot( );
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer( 0 );
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
			return true;
		}

		/**
		 * Sets the graph to display data about only the given molecule object.
		 * 
		 * @param molecule The molecule to display data about.
		 * @return true if setting the graph was successful.
		 */
		public boolean setGraph( Molecule molecule ) {
			Language language = Settings.getLanguage( );
			XYSeries data = new XYSeries( language.get( "Sample Data" ));
			List<Number> samples = selectorTree.getSamplesFiltered( molecule );
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
				molecule.toString( )), //title
				language.get( "Sample" ), // x axis label
				language.get( "Concentration" ), // y axis label
				dataset, // plot data
				PlotOrientation.VERTICAL, // Plot Orientation
				false, // show legend
				false, // use tooltips
				false  // configure chart to generate URLs (?!)
			);

			XYPlot plot = this.chart.getXYPlot( );
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer( 0 );
			renderer.setSeriesStroke( 0, new BasicStroke( 2 ));
			renderer.setSeriesShapesVisible( 0, true );
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

}


