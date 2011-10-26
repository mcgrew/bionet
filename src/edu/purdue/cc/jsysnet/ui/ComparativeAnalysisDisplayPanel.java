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

import edu.purdue.bbc.util.CurveFitting;
import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.SparseMatrix;
import edu.purdue.bbc.util.Statistics;
import edu.purdue.bbc.util.equation.Equation;
import edu.purdue.bbc.util.equation.Polynomial;
import edu.purdue.cc.jsysnet.io.SaveImageAction;
import edu.purdue.cc.jsysnet.util.Experiment;
import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.cc.jsysnet.util.Sample;
import edu.purdue.cc.jsysnet.util.SampleGroup;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;

import org.apache.log4j.Logger;

/**
 * A class for comparative analysis view of multiple experiments.
 */
public class ComparativeAnalysisDisplayPanel extends JPanel 
		implements DisplayPanel,ComponentListener,ActionListener {

	private JMenuBar menuBar;
	private JMenu curveFittingMenu;
	private JMenu groupsMenu;
	private JMenuItem chooseSampleGroupsMenuItem;
	private JMenuItem removeSampleGroupsMenuItem;

	private Collection<Experiment> experiments;
	private Set<Molecule> molecules;
	private Set<Sample> samples;
	private JSplitPane mainSplitPane;
	private JSplitPane graphSplitPane;
	private ExperimentSelectorTreePanel selectorTree;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JPanel experimentGraphPanel;
	private Collection<SampleGroup> sampleGroups;
	private JPanel fitSelectorPanel;
	private JRadioButtonMenuItem noFitButton;
	private JRadioButtonMenuItem robustFitButton;
	private JRadioButtonMenuItem chiSquareFitButton;
	private ButtonGroup fitButtonGroup;
	private boolean graphSplitPaneDividerLocationSet = false;

	private static final int MOLECULE      = 1;
	private static final int EXPERIMENT    = 2;
	private static final int SAMPLE        = 3;

	/**
	 * Creates a new ComparativeAnalysisDisplayPanel.
	 */
	public ComparativeAnalysisDisplayPanel ( ) {
		super( new BorderLayout( ));
		Language language = Settings.getLanguage( );
		this.menuBar = new JMenuBar( );
		this.curveFittingMenu = new JMenu( 
			language.get( "Curve Fitting" ));
		this.curveFittingMenu.setMnemonic( KeyEvent.VK_C );
		this.groupsMenu = new JMenu( language.get( "Groups" ));
		this.groupsMenu.setMnemonic( KeyEvent.VK_G );
		this.removeSampleGroupsMenuItem = 
			new JMenuItem( language.get( "Reset Sample Groups" ), KeyEvent.VK_R );
		this.chooseSampleGroupsMenuItem = 
			new JMenuItem( language.get( "Choose Sample Groups" ), KeyEvent.VK_C );
		this.fitSelectorPanel = new JPanel( new GridLayout( 4, 1 ));
		this.noFitButton = new JRadioButtonMenuItem( 
			language.get( "No Fit" ));
		this.robustFitButton = new JRadioButtonMenuItem( 
			language.get("Robust Linear Fit"));
		this.chiSquareFitButton = new JRadioButtonMenuItem( 
			language.get("Chi Square Fit"));
		this.fitButtonGroup = new ButtonGroup( );
		this.fitButtonGroup.add( this.noFitButton );
		this.fitButtonGroup.add( this.robustFitButton );
		this.fitButtonGroup.add( this.chiSquareFitButton );
		this.noFitButton.setSelected( true );
		this.curveFittingMenu.add( this.noFitButton );
		this.curveFittingMenu.add( this.robustFitButton );
		this.curveFittingMenu.add( this.chiSquareFitButton );
		this.groupsMenu.add( this.removeSampleGroupsMenuItem );
		this.groupsMenu.add( this.chooseSampleGroupsMenuItem );
		this.chooseSampleGroupsMenuItem.addActionListener( this );
		this.removeSampleGroupsMenuItem.addActionListener( this );
		this.add( menuBar, BorderLayout.NORTH );
		this.menuBar.add( this.curveFittingMenu );
		this.menuBar.add( this.groupsMenu );
	}

	/**
	 * Creates a new view containing the specified experiments.
	 * 
	 * @param experiments The experiments to display in this view.
	 * @return A boolean indicating whether creating the view was successful.
	 */
	public boolean createView( Collection<Experiment> experiments ) {
		this.experiments = experiments;
		this.molecules = new TreeSet<Molecule>( );
		this.samples = new TreeSet<Sample>( );
		for ( Experiment e : experiments ) {
			this.molecules.addAll( e.getMolecules( ));
			this.samples.addAll( e.getSamples( ));
		}
		SampleGroup sampleGroup = new SampleGroup( 
			Settings.getLanguage( ).get( "All Samples" ),
			this.samples );
		ArrayList <SampleGroup> sampleGroups = new ArrayList<SampleGroup>( );
		sampleGroups.add( sampleGroup );
		Language language = Settings.getLanguage( );

		this.topPanel = new JPanel( new BorderLayout( ));
		this.bottomPanel = new JPanel( new GridLayout( 1, 1 ));
		this.experimentGraphPanel = new JPanel( new GridLayout( 1, 1 ));

		this.selectorTree = new ExperimentSelectorTreePanel( experiments );
		this.mainSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		this.graphSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );

		JPanel topRightPanel = new JPanel( new BorderLayout( ));
		topRightPanel.add( this.fitSelectorPanel, BorderLayout.NORTH );
		topRightPanel.add( new JPanel( ), BorderLayout.CENTER );
		this.topPanel.add( topRightPanel, BorderLayout.EAST );
		this.topPanel.add( this.experimentGraphPanel, BorderLayout.CENTER );

		this.add( mainSplitPane, BorderLayout.CENTER );
		this.mainSplitPane.setLeftComponent( this.selectorTree );
		this.mainSplitPane.setDividerLocation( 200 );
		this.mainSplitPane.setRightComponent( this.graphSplitPane ); 
		this.graphSplitPane.setTopComponent( this.topPanel );
		this.graphSplitPane.setBottomComponent( this.bottomPanel );
		this.setSampleGroups( sampleGroups );

		this.addComponentListener( this );

		return true;
	}

	/**
	 * Adds an Experiment to this panel.
	 * 
	 * @param experiment The experiment to be added.
	 * @return A boolean indicating whether the operation was successful.
	 */
	public boolean addExperiment( Experiment experiment ) {
		this.molecules.addAll( experiment.getMolecules( ));
		return experiments.add( experiment );
	}

	/**
	 * Removes an experiment from the panel.
	 * 
	 * @param experiment The expeiment to be removed.
	 * @return A boolean indicating whether or not the operation was successful.
	 */
	public boolean removeExperiment( Experiment experiment ) {
		return experiments.remove( experiment );
	}

	/**
	 * Gets the title for this panel.
	 * 
	 * @return The title for this panel.
	 */
	public String getTitle( ) {
		return Settings.getLanguage( ).get( "Comparative Analysis" );
	}

	public Collection<SampleGroup> getSampleGroups( ) {
		return this.sampleGroups;
	}

	/**
	 * Sets the SampleGroups for this panel.
	 * 
	 * @param sampleGroups The new set of groups
	 */
	public void setSampleGroups( Collection<SampleGroup> sampleGroups ) {

		this.sampleGroups = sampleGroups;

		// clear the listeners
		CheckboxTree tree = this.selectorTree.getTree( );
		for ( TreeSelectionListener t : tree.getTreeSelectionListeners( )) {
			tree.removeTreeSelectionListener( t );
			// CheckboxTree doesn't have a method for getting TreeCheckingListeners,
			// so we'll try this and catch any exceptions.
			try {
				tree.removeTreeCheckingListener( (TreeCheckingListener)t );
			} catch ( ClassCastException e ){ }
		}
		for ( ItemListener i : this.noFitButton.getItemListeners( )) {
			this.noFitButton.removeItemListener( i );
		}
		for ( ItemListener i : this.robustFitButton.getItemListeners( )) {
			this.robustFitButton.removeItemListener( i );
		}
		for ( ItemListener i : this.chiSquareFitButton.getItemListeners( )) {
			this.chiSquareFitButton.removeItemListener( i );
		}

		this.experimentGraphPanel.removeAll( );
		this.bottomPanel.removeAll( );
		int cols = (int)Math.ceil( Math.sqrt( sampleGroups.size( )));
		int rows = (int)Math.ceil( sampleGroups.size( ) / cols );
		GridLayout layout = (GridLayout)this.experimentGraphPanel.getLayout( );
		layout.setRows( rows );
		layout.setColumns( cols );
		layout = (GridLayout)this.bottomPanel.getLayout( );
		layout.setRows( rows );
		layout.setColumns( cols );

		if ( selectorTree.getTree( ).isSelectionEmpty( )) {
			selectorTree.getTree( ).setSelectionRow( 0 );
		}
		TreePath path = selectorTree.getTree( ).getSelectionPath( );
		int level = path.getPathCount( );
		DefaultMutableTreeNode selectedNode = null;
		if ( level > MOLECULE ) {
			selectedNode = (DefaultMutableTreeNode)path.getPathComponent( MOLECULE );
		}

		for ( SampleGroup sampleGroup : sampleGroups ) {
			SampleGraph sampleGraph = new SampleGraph( experiments, sampleGroup );
			ExperimentGraph experimentGraph = new ExperimentGraph( experiments, sampleGroup );
			this.bottomPanel.add( sampleGraph );
			this.selectorTree.getTree( ).addTreeSelectionListener( sampleGraph );
			this.experimentGraphPanel.add( experimentGraph, BorderLayout.CENTER );
			// add the listeners
			this.selectorTree.getTree( ).addTreeCheckingListener( sampleGraph );
			this.selectorTree.getTree( ).addTreeSelectionListener( experimentGraph );
			this.selectorTree.getTree( ).addTreeCheckingListener( experimentGraph );
			this.noFitButton.addItemListener( experimentGraph );
			this.robustFitButton.addItemListener( experimentGraph );
			this.chiSquareFitButton.addItemListener( experimentGraph );
			if ( selectedNode != null ) {
				sampleGraph.setGraph( selectedNode );
				experimentGraph.setGraph( selectedNode );
			}
		}
		this.removeSampleGroupsMenuItem.setEnabled( 
			sampleGroups != null && sampleGroups.size( ) > 1 );
		this.experimentGraphPanel.validate( );
		this.bottomPanel.validate( );

		
	}

	public void componentHidden( ComponentEvent e ) { } 
	public void componentMoved( ComponentEvent e ) { } 
	public void componentResized( ComponentEvent e ) { } 
	public void componentShown( ComponentEvent e ) { 
		if ( !this.graphSplitPaneDividerLocationSet ) {
			this.graphSplitPaneDividerLocationSet = true;
			this.graphSplitPane.setDividerLocation( 0.5 );
		}
	} 

	/**
	 * The actionPerformed method of the ActionListener interface.
	 * 
	 * @see ActionListener#actionPerformed( ActionEvent )
	 * @param e The event which triggered this action.
	 */
	public void actionPerformed( ActionEvent e ) {
		Logger logger = Logger.getLogger( getClass( ));
		Language language = Settings.getLanguage( );
		Object source = e.getSource( );
		if ( source == this.chooseSampleGroupsMenuItem ) {
			// Choose sample groups.
			Component frame = this;
			while( !(frame instanceof Frame) && frame != null ) {
				frame = frame.getParent( );
			}
			Collection<SampleGroup> groups = 
				SampleGroupingDialog.showInputDialog( 
					(Frame)frame, Settings.getLanguage( ).get( "Choose groups" ), 
					this.samples );
			if ( groups != null ) {

				if ( this.sampleGroups != null ) {
					for ( SampleGroup group : this.getSampleGroups( )) {
						logger.debug( group.toString( ));
						for ( Sample sample : group ) {
							logger.debug( "\t" + sample.toString( ));
						}
					}
				}
				this.setSampleGroups( groups );
			}
		} else if ( source == this.removeSampleGroupsMenuItem ) {
			Collection<SampleGroup> groups = new ArrayList<SampleGroup>( );
			groups.add( new SampleGroup( "", this.samples ));
			this.setSampleGroups( groups );
		}
	}

	// =========================== PRIVATE CLASSES ===============================
	// ====================== ExperimentSelectorTreePanel ========================
	/**
	 * A class for displaying the selector tree
	 */
	private class ExperimentSelectorTreePanel extends CheckboxTreePanel {
		public static final int MOLECULE = 1;
		public static final int EXPERIMENT = 2;
		public static final int SAMPLE = 3;

		/**
		 * Creates a new ExperimentSelectorTreePanel based on the passed in 
		 * Experiments.
		 * 
		 * @param experiments The Experiment data to be shown in the tree.
		 */
		public ExperimentSelectorTreePanel( Collection <Experiment> experiments ) {
			super( new DefaultMutableTreeNode(	
				Settings.getLanguage( ).get( "All Experiments" )));

			// first get all possible group names
			TreeSet<Molecule> molecules = new TreeSet<Molecule>( );
			for ( Experiment e : experiments ) {
				molecules.addAll( e.getMolecules( ));
			}
			for ( Molecule molecule : molecules ) {
				DefaultMutableTreeNode moleculeNode = 
					new DefaultMutableTreeNode( molecule );

				for ( Experiment e : experiments ) {
					DefaultMutableTreeNode experimentNode = 
						new DefaultMutableTreeNode( e );
					for ( Sample sample : e.getSamples( )) {
						DefaultMutableTreeNode sampleNode = 
							new DefaultMutableTreeNode( sample );	
						experimentNode.add( sampleNode );
					}
					moleculeNode.add( experimentNode );
				}
				this.getRoot( ).add( moleculeNode );
			}
			this.tree.setRootVisible( false );
			this.check( this.getRoot( ));
			this.reload( );
		}

		/**
		 * Returns a mapping of Samples that are checked along with their values
		 * 
		 * @param node A tree node which contains the sample nodes to be retrieved.
		 * @return A Map containing the requested values.
		 */
		public Collection<Sample> getSamplesFiltered( DefaultMutableTreeNode node ){
			Collection<Sample> returnValue = new ArrayList<Sample>( );
			Iterator<TreeNode> nodeIter = this.checkedDescendantIterator( node, SAMPLE );
			while( nodeIter.hasNext( )) {
				returnValue.add( (Sample)(
					(DefaultMutableTreeNode)nodeIter.next( )).getUserObject( ));
			}
			return returnValue;
		}
	}

	// =========================== ExperimentGraph ===============================
	/**
	 * A class for displaying molecular data across experiments
	 */
	private class ExperimentGraph extends JPanel 
		implements TreeSelectionListener,TreeCheckingListener,ItemListener {
		private JFreeChart chart;
		private SortedSet <Experiment> experiments;
		private SampleGroup sampleGroup;
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
		public ExperimentGraph( Collection <Experiment> experiments, 
		                        SampleGroup sampleGroup ) {
			super( );
			this.experiments = new TreeSet( experiments );
			this.sampleGroup = sampleGroup;
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
			// add a context menu for saving the graph to an image
			new ContextMenu( this ).add( new SaveImageAction( this ));
		}

		/**
		 * Sets the graph to display data from each experiment on the passed in 
		 * molecule.
		 *
		 * This could be a String with the molecule ID, or an instance of the 
		 * Molecule.
		 * 
		 * @param node The TreeNode containing the molecule object to be graphed.
		 */
		public boolean setGraph( DefaultMutableTreeNode node ) {
			Language language = Settings.getLanguage( ); 
			Molecule molecule = (Molecule)node.getUserObject( );
			DefaultBoxAndWhiskerXYDataset boxDataSet = 
				new DefaultBoxAndWhiskerXYDataset( new Integer( 1 ));
			double [] fitValues = new double[ this.experiments.size( ) + 1];
			fitValues[ 0 ] = Double.NaN;
			int expIndex = 1;
			int expCount = 0;
			for( int i=0; i < node.getChildCount( ); i++ ) {
				DefaultMutableTreeNode expNode = 
					(DefaultMutableTreeNode)node.getChildAt( i );
				Experiment e = (Experiment)expNode.getUserObject( );
				fitValues[ expIndex ] = Double.NaN;
				if ( selectorTree.isChecked( node )) {
					expCount++;
					try {
						Collection<Sample> samples = 
							selectorTree.getSamplesFiltered( expNode );
						samples.retainAll( this.sampleGroup );
						BoxAndWhiskerItem item = 
							BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics( 
								new ArrayList<Number>( molecule.getValues( samples )));
						boxDataSet.add( new Date((long)expIndex), item );

						// robust fit uses the median instead of the mean.
						if ( robustFitButton.isSelected( )) { 
							fitValues[ expIndex ] = item.getMedian( ).doubleValue( );
						} else {
							fitValues[ expIndex ] = item.getMean( ).doubleValue( );
						}
					} catch ( IllegalArgumentException exc ) { 
						// ignore this error.
					}
				}
				expIndex++;
			}

			if ( expCount < 1 ) { // nothing to graph.
				this.chart = null;
				return false;
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
							"Unable to find solution for " + 
							fitEquation.toString( ) + " where x=" + i, exc );
					}
				}
			}
			this.chart = ChartFactory.createBoxAndWhiskerChart (
				String.format( language.get( "%s across experiments" ) + " - %s", 
					molecule.toString( ),
					this.sampleGroup.toString( )), //title
				language.get( "Time" ), // x axis label
				language.get( "Response" ),   // y axis label
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
			plot.getDomainAxis( ).setStandardTickUnits( 
				NumberAxis.createIntegerTickUnits( ));
			plot.getDomainAxis( ).setVerticalTickLabels( true );
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
			return true;
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
												size.height - 15 );
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
				this.setGraph( (DefaultMutableTreeNode)path.getPathComponent( MOLECULE ));
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
			if ( selectorTree.getTree( ).isSelectionEmpty( )) {
				selectorTree.getTree( ).setSelectionRow( 0 );
			}
			TreePath path = selectorTree.getTree( ).getSelectionPath( );
			int level = path.getPathCount( );
			if ( level > MOLECULE ) {
				this.setGraph( (DefaultMutableTreeNode)path.getPathComponent( MOLECULE ));
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
						this.setGraph( 
							(DefaultMutableTreeNode)path.getPathComponent( MOLECULE ));
					} else {
						this.chart = null;
					}
					this.repaint( );
				}
			}
		}
	}

	// ============================ SampleGraph ==================================
	/**
	 * A class for showing sample concentrations in a graph.
	 */
	private class SampleGraph extends JPanel 
		implements TreeSelectionListener,TreeCheckingListener {

		private SortedSet <Experiment> experiments;
		private SampleGroup sampleGroup;
		private JFreeChart chart;
		private Object selectedObject;

		public SampleGraph( Collection <Experiment> experiments, 
		                    SampleGroup sampleGroup ) {
			super( );
			this.experiments = new TreeSet<Experiment>( experiments );
			this.sampleGroup = sampleGroup;
			// add a context menu for saving the graph to an image
			new ContextMenu( this ).add( new SaveImageAction( this ));
		}

		/**
		 * Sets the graph to display data from the given object in the Node
		 * 
		 * @param node The node to show information about.
		 */
		public boolean setGraph( DefaultMutableTreeNode node ) {
			Language language = Settings.getLanguage( );
			XYSeriesCollection dataset = new XYSeriesCollection( );
			LegendItemCollection legendItems = new LegendItemCollection( );
			CustomXYLineAndShapeRenderer renderer = 
				new CustomXYLineAndShapeRenderer( true, true );
			int nodeLevel = node.getLevel( );
			Logger.getLogger( getClass( )).debug( 
				"Selected Node level: " + nodeLevel );
			Collection<Sample> samples = null;

			if ( nodeLevel == MOLECULE || nodeLevel == EXPERIMENT ) {
				Molecule molecule = null;
				if ( nodeLevel == MOLECULE ) {
					molecule = (Molecule)node.getUserObject( );
				} else {
					molecule = (Molecule)((DefaultMutableTreeNode)
							node.getParent( )).getUserObject( );
				}
				samples = new TreeSet<Sample>( new SampleValueComparator( ));
				Iterator<TreeNode> sampleNodeIter = 
					selectorTree.checkedDescendantIterator( node, SAMPLE );
				XYSeries data = new XYSeries( language.get( "Samples" ));
				int index = 0;
				while( sampleNodeIter.hasNext( )) {
					samples.add((Sample)
						((DefaultMutableTreeNode)sampleNodeIter.next( )).getUserObject( ));
				}
				samples.retainAll( this.sampleGroup );
				for ( Sample sample : samples ) {
					Number value = molecule.getValue( sample );
					data.add( index, value );
					index++;
				}
				renderer.setSeriesOutlierInfo( dataset.getSeriesCount( ),
					BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics( 
						molecule.getValues( samples )));
				dataset.addSeries( data );

			} else { 
				this.chart = null;
				return false;
			}
			if ( dataset.getSeriesCount( ) < 1 ) {
				this.chart = null;
				return false;
			}

			this.chart = ChartFactory.createXYLineChart( 
				String.format( language.get( "%s sample concentrations" ) + " - %s", 
				node.toString( ),
				this.sampleGroup.toString( )),         // title
				language.get( "Sample" ),        // x axis label
				language.get( "Response" ),      // y axis label
				dataset,                         // plot data
				PlotOrientation.VERTICAL,        // Plot Orientation
				true,                            // show legend
				false,                           // use tooltips
				false                            // configure chart to generate URLs (?)
			);

			XYPlot plot = this.chart.getXYPlot( );
			plot.setRenderer( renderer );
			// this is a single experiment graph, so pick the color to be consistent
			// with the multi-experiment graph.
			int experimentCount = node.getParent( ).getChildCount( );
			int index = 0;
			renderer.setSeriesPaint( 0,
				Color.getHSBColor( 0.5f, 1.0f, 0.5f ));
			renderer.setSeriesStroke( 0, new BasicStroke( 2 ));
			renderer.setSeriesShapesVisible( 0, true );

			try { 
				// set up the colors for the graph items.
				int experimentIndex = 0;
				for ( Experiment experiment : experiments ) {
					int sampleIndex = 0;
					Paint p = Color.getHSBColor( 
						(float)experimentIndex/experiments.size( ), 1.0f, 0.5f );
						// add a legend item for this experiment.
						legendItems.add( new LegendItem(
							language.get( "Experiment" ) + " " + experiment.getId( ),// label
							null,                                          // description
							null,                                          // toolTipText
							null,                                          // urlText
							renderer.getItemShape( 0, samples.size( )/2 ), // shape
							p,                                             // fillPaint
							renderer.getSeriesStroke( 0 ),                 // outlineStroke
							p                                              // outlinePaint
						));

					for ( Sample sample : samples ) {
						if ( experiment.getSamples( ).contains( sample )) {
							renderer.setItemShapePaint( 0, sampleIndex, p );
						}
						sampleIndex++;
					}
					experimentIndex++;
				}
			} catch ( IndexOutOfBoundsException e ) {
				this.chart = null;
				return false;
			}
				
			plot.setBackgroundPaint( Color.WHITE );
			plot.setRangeGridlinePaint( Color.GRAY );
			plot.setDomainGridlinePaint( Color.GRAY );
			TickUnits tickUnits = new TickUnits( );
			double tickIndex = 0.0;
			List<Sample> sampleList = new ArrayList<Sample>( samples );
			for ( Sample sample : sampleList ) {
				tickUnits.add( new SampleTickUnit( tickIndex, sampleList ));
				tickIndex++;
			}
			plot.getDomainAxis( ).setStandardTickUnits( tickUnits );
			plot.getDomainAxis( ).setVerticalTickLabels( true );
//			LegendItemCollection legendItems = plot.getLegendItems( );
//			if ( legendItems == null ) {
//				legendItems = new LegendItemCollection( );
//			}
			legendItems.add( renderer.getOutlierLegendItem( ));
			plot.setFixedLegendItems( legendItems );
			return true;
		}

		/**
		 * The valueChanged method of the TreeSelectionListener interface
		 * 
		 * @param e The event which triggered this action.
		 */
		public void valueChanged( TreeSelectionEvent e ) {
			TreePath path = e.getPath( );
				if ( path != null ) {
				int level = path.getPathCount( );
				if ( level > EXPERIMENT ) {
					this.setGraph((DefaultMutableTreeNode)path.getPathComponent( EXPERIMENT ));
				} else if ( level > MOLECULE ) { 
					this.setGraph((DefaultMutableTreeNode)path.getPathComponent( MOLECULE ));
				} else {
					this.chart = null;
				}
				this.repaint( );
			}
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
				BufferedImage drawing = this.chart.createBufferedImage( size.width, 
				                                                        size.height );
				g.drawImage( drawing, 0, 0, Color.WHITE, this );
			}
		}

		// =============== SampleValueComparator ===========================
		private class SampleValueComparator implements Comparator<Sample> {

			public int compare( Sample s1, Sample s2 ) {
				int returnValue = (int)Math.signum( 
					Integer.parseInt( s1.getAttribute( "Time" )) -
					Integer.parseInt( s2.getAttribute( "Time" )));
				if ( returnValue == 0 ) {
					returnValue = s1.toString( ).compareTo( s2.toString( ));
				}
				return returnValue;
			}
		}
	}

	// ================== CustomXyLineAndShapeRenderer ===========================

	/**
	 * A customized version of XYLineAndShapeRenderer
	 */
	private class CustomXYLineAndShapeRenderer extends XYLineAndShapeRenderer {
		private List<BoxAndWhiskerItem> outlierInfo;
		private SparseMatrix<Paint> itemShapePaints;
		private Paint outlierPaint;
		private Shape outlierShape;
		private Stroke outlierStroke;
		private XYDataset dataset;
		private int currentPass;

		public CustomXYLineAndShapeRenderer( ) {
			super( );
			this.outlierInfo = new ArrayList<BoxAndWhiskerItem>( );
			this.outlierPaint = Color.RED;
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
			this.itemShapePaints = new SparseMatrix<Paint>( );
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
		 * @param plot - the plot (can be used to obtain standard color 
		 *	information etc).
		 * @param domainAxis - the domain axis.
		 * @param rangeAxis - the range axis.
		 * @param dataset - the dataset.
		 * @param series - the series index (zero-based).
		 * @param item - the item index (zero-based).
		 * @param crosshairState - crosshair information for the plot (null 
		 *	permitted).
		 * @param pass - the pass index.
		 * @see XYLineAndShapeRenderer#drawItem( Graphics2D, XYItemRenderState,
		 *	Rectangle2D, PlotRenderingInfo, XYPlot, ValueAxis, ValueAxis XYDataset,
		 *  int, int, CrosshairState, int )
		 */
		@Override
		public void drawItem( Graphics2D g2, XYItemRendererState state, 
		                      Rectangle2D dataArea, PlotRenderingInfo info, 
													XYPlot plot, ValueAxis domainAxis, 
													ValueAxis rangeAxis, XYDataset dataset, int series, 
													int item, CrosshairState crosshairState, int pass ) {
			this.currentPass = pass;
			super.drawItem( g2, state, dataArea, info, plot, domainAxis, rangeAxis, 
			                dataset, series, item, crosshairState, pass );
		}

		/**
		 * Sets the outlier information for a series.
		 * 
		 * @param index The index of the series to set the outlier information for.
		 * @param item A BoxandWhiskerItem which will be used to determine outliers.
		 */
		public void setSeriesOutlierInfo( int index, BoxAndWhiskerItem item ) {
			while ( index >= outlierInfo.size( )){
				outlierInfo.add( null );
			}
			outlierInfo.set( index, item );
		}

		/**
		 * Returns the series outlier information for this series.
		 * 
		 * @param index The index of the series.
		 * @return The BoxAndWhiskerItem used to calculate outliers.
		 */
		public BoxAndWhiskerItem getSeriesOutlierInfo( int index ) {
			if ( index >= outlierInfo.size( ))
				return null;
			return outlierInfo.get( index );
		}

		/**
		 * Determines if the given item in a series is an outlier.
		 * 
		 * @param series The series the item belongs to.
		 * @param item The item in question.
		 * @return A boolean indicating whether the item is an outlier.
		 */
		private boolean isOutlier( int series, int item ) {
			if ( this.dataset == null ) {
				this.dataset = this.getPlot( ).getDataset( 0 ); 
			}
			BoxAndWhiskerItem stats = this.getSeriesOutlierInfo( series );
			if ( stats == null )
				return false;
			double y = this.dataset.getYValue( series, item );
			return ( 
				Double.compare( y, stats.getMinRegularValue( ).doubleValue( )) < 0 || 
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
	 @Override
		public Paint getItemPaint( int row, int column ) {
			if ( this.isItemPass( this.currentPass )) {
				if ( this.isOutlier( row, column )) {
					return this.outlierPaint;
				} else {
					Paint returnValue = this.itemShapePaints.get( row, column );
					if ( returnValue == null )
						return super.getItemPaint( row, column );
					return returnValue;
				}
			} else {
				return super.getItemPaint( row, column );
			}
		}

		/**
		 * Sets the shape color for a particular item
		 * 
		 * @param row The row (or series) of the item
		 * @param column The column of the item.
		 * @param paint The Paint to use when drawing the item.
		 */
		public void setItemShapePaint( int row, int column, Paint paint ) {
			this.itemShapePaints.set( row, column, paint );
		}

		/**
		 * Returns an appropriate LegendItem for the Outliers
		 * 
		 * @return A LegendItem for the outliers.
		 */
		public LegendItem getOutlierLegendItem( ) {
			return new LegendItem( 
				"Outlier",             // label
				null,                  // description
				null,                  // toolTipText
				null,                  // urlText
				this.outlierShape,     // shape
				this.outlierPaint,     // fillPaint
				this.outlierStroke,    // outlineStroke
				this.outlierPaint      // outlinePaint
			);
		}
	}
}


