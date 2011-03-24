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

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.NumberList;
import edu.purdue.bbc.util.Settings;
import edu.purdue.cc.jsysnet.io.JavaMLTranslator;
import edu.purdue.cc.jsysnet.util.Experiment;
import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.cc.jsysnet.util.Sample;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.SOM;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.StreamHandler;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
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

import org.apache.log4j.Logger;

public class TimeCourseStudyDisplayPanel extends JPanel 
                                   implements DisplayPanel, ActionListener {
	private SelectorTreePanel selectorTree;
	private SampleSelectorTreePanel sampleSelectorTree;
	private JButton recalculateButton;
	private JSplitPane splitPane;
	private JSplitPane treeSplitPane;
	private ClusterGraph graph;
	private Collection<Experiment> experiments;
	private List<Sample> samples;
	private Collection<Molecule> molecules;

	private static final int ROOT = 0;
	private static final int CLUSTER = 1;
	private static final int MOLECULE = 2;

	/**
	 * A class for displaying information about a Time Course Study
	 */
	public TimeCourseStudyDisplayPanel( ) {
		super( new BorderLayout( ));
		this.addComponentListener( new Sizer( ));
	}
		
	/**
	 * Creates the visualization instance for a TimeCourseStudyDisplayPanel
	 * 
	 * @param experiments The experiments to be associated with this instance.
	 * @return true if creating the visualization succeeded.
	 */
	public boolean createView( Collection <Experiment> experiments ) {
		Logger logger = Logger.getLogger( getClass( ));
		this.experiments = experiments;
		this.samples = new ArrayList<Sample>( );
		this.molecules = new TreeSet<Molecule>( );
		for( Experiment experiment : experiments ) {
			this.samples.addAll( experiment.getSamples( ));
			this.molecules.addAll( experiment.getMolecules( ));
		}
		this.sampleSelectorTree = new SampleSelectorTreePanel( this.samples );
		Collection<Collection<Molecule>> clusters = 
			new TreeSet<Collection<Molecule>>( new DatasetComparator( ));
		try {
			int len;
			InputStream dataStream = new JavaMLTranslator( 
					this.sampleSelectorTree.getSamples( ), this.molecules );
			logger.debug( "Starting Clustering ..." );
			Dataset data = StreamHandler.load( 
				new InputStreamReader( dataStream ), 0, ",");
			
			RunnableClusterer som = new RunnableClusterer(
				new SOM(
					8,                            // number of dimensions on the x axis
					8,                            // number of dimensions on the y axis
					SOM.GridType.HEXAGONAL,        // type of grid.
					10000,                          // number of iterations
					0.1,                           // learning rate of algorithm
					8,                             // initial radius
					SOM.LearningType.LINEAR,       // type of learning to use
					SOM.NeighbourhoodFunction.STEP // neighborhood function.
				),
				data );
			Thread somThread = new Thread( som );
			somThread.start( );

			this.setCursor( new Cursor( Cursor.WAIT_CURSOR ));
			logger.debug( "Waiting for SOM Clustering to complete..." );
			somThread.join( );
			this.setCursor( new Cursor( Cursor.DEFAULT_CURSOR ));
			for ( Dataset d : som.getResult( )) {
				clusters.add( this.getMoleculesForDataset( d ));
			}
		} catch ( Exception e ) {
			logger.error( e, e );
			return false;
		}

		this.splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		this.treeSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		this.recalculateButton = new JButton( "Recalculate" );
		JPanel treePanel = new JPanel( new BorderLayout( ));
		this.treeSplitPane.setBottomComponent( this.sampleSelectorTree );
		treePanel.add( this.treeSplitPane, BorderLayout.CENTER );
//		treePanel.add( this.recalculateButton, BorderLayout.SOUTH );
		this.splitPane.setLeftComponent( treePanel );
		this.graph = new ClusterGraph( );
		this.splitPane.setRightComponent( this.graph );
		this.sampleSelectorTree.getTree( ).addTreeCheckingListener( this.graph );
		this.add( this.splitPane, BorderLayout.CENTER );
		this.splitPane.setDividerLocation( 250 );

		this.selectorTree = new SelectorTreePanel( clusters );
		this.treeSplitPane.setTopComponent( this.selectorTree );
		this.selectorTree.getTree( ).addTreeSelectionListener( this.graph );
		this.selectorTree.getTree( ).addTreeCheckingListener( this.graph );
		this.graph.setMeanGraph( (DefaultMutableTreeNode)
			this.selectorTree.getTree( ).getModel( ).getRoot( ));

		
		return true;
	}

	/**
	 * Gets the molecules associated with a Dataset (cluster)
	 * 
	 * @param d A dataset containing instances with the same string value as
	 *	Molecules in the experiment.
	 * @return A Collection containing the molecules indicated by the instances.
	 */
	private Collection<Molecule> getMoleculesForDataset( Dataset d ) {
		Collection<Molecule> returnValue = new TreeSet<Molecule>( );
		for ( Instance i : d ) {
			Molecule m = this.getMoleculeForInstance( i );
			if ( m != null ) {
				returnValue.add( m );
			}
		}
		return returnValue;
	}

	/**
	 * Returns the particular Molecule for the experiment which has the same Id as
	 * The instance class value.
	 * 
	 * @param i The Instance to look up the Molecule for. 
	 * @return The Molecule associated with this Instance.
	 */
	private Molecule getMoleculeForInstance( Instance i ) {
		for ( Molecule m : this.molecules ) {
			if ( m.getId( ).equals( i.classValue( ).toString( ))) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Gets the title of this panel.
	 * 
	 * @return The title of this panel as a String.
	 */
	public String getTitle( ) {
		return "Time Course Study";
	}

	/**
	 * A class for running a Clusterer instance in a seperate thread.
	 */
	private class RunnableClusterer implements Runnable {
		private Clusterer clusterer;
		private Dataset dataset;
		private Dataset[] result;
		
		/**
		 * Creates a new RunnableClusterer which will run the underlying Clusterer
		 * when it's run( ) method is called.
		 * 
		 * @param clusterer The Clusterer to be run in a thread.
		 */
		public RunnableClusterer( Clusterer clusterer ) {
			this.clusterer = clusterer;
		}

		/**
		 * Creates a new RunnableClusterer which will run the underlying Clusterer
		 * when it's run( ) method is called.
		 * 
		 * @param clusterer The Clusterer to be run in a thread.
		 * @param dataset The dataset to be evaluated with the passed in Clusterer.
		 */
		public RunnableClusterer( Clusterer clusterer, Dataset dataset ) {
			this.clusterer = clusterer;
			this.dataset = dataset;
		}

		/**
		 * Sets the Dataset to be evaluated with the Clusterer.
		 * 
		 * @param dataset The Dataset to be evlauated.
		 */
		public void setDataset ( Dataset dataset ) {
			this.dataset = dataset;
		}

		/**
		 * Runs the underlying Clusterer.
		 */
		public void run( ) {
			this.result = this.clusterer.cluster( this.dataset );
		}

		/**
		 * Returns an array of Datasets which are the result of the underlying
		 * Clusterer.
		 * 
		 * @return The clusters found by the underlying Clusterer.
		 */
		public Dataset[] getResult( ) {
			return result;
		}
	}

	/**
	 * The actionPerformed method of the ActionListener interface. Performs 
	 * cluster recalculation when the 'recalculate' button is clicked.
	 * 
	 * @param e The event which triggered this action.
	 */
	public void actionPerformed( ActionEvent e ) {

	}

	/**
	 * A class for handling the sizng of panels and such in 
	 * TimeCourseStudyDisplayPanel when it is initially shown.
	 */
	private class Sizer extends ComponentAdapter {
		public Sizer( ) {
			super( );
		}

		/**
		 * Fired when the component is shown. Adjusts the size of the treeSplitPane
		 * based on the overall size of the panel when it is made visible, then
		 * unregisters itself as a listener so it is only fired once.
		 * 
		 * @param e The event which triggered this action.
		 */
		public void componentShown( ComponentEvent e ) {
			Component source = e.getComponent( );
			treeSplitPane.setDividerLocation( source.getHeight( )*2/3 );
			// we only need to perform this action once.
			source.removeComponentListener( this );
		}
	}

	/**
	 * Compares and orders a Collection of Collections of Molecules.
	 */
	private class DatasetComparator implements Comparator<Collection<Molecule>> {

		/**
		 * Compares 2 Collections of Collections of Mollecules. First compares based
		 * on size, and orders the larger one to be first. If they are the same
		 * size, the compareTo( ) method is called on the molecules contained within
		 * until they do not match, and that value is returned. If they are the same
		 * size and contain the same molecules, 0 is returned (indicating they are
		 * equal)
		 * 
		 * @param c1 The first Collection to be compared.
		 * @param c2 The second Collection to be comapred.
		 * @return A value indicating the preferred ordering of the 2 collections.
		 */
		public int compare( Collection<Molecule> c1, Collection<Molecule> c2 ) {
			int returnValue = c2.size( ) - c1.size( );
			if ( returnValue != 0 )
				return returnValue;
			Iterator<Molecule> c1Iterator = c1.iterator( );
			Iterator<Molecule> c2Iterator = c2.iterator( );
			while ( c1Iterator.hasNext( )) {
				returnValue = 
					c1Iterator.next( ).getId( ).compareTo( c2Iterator.next( ).getId( ));
				if ( returnValue != 0 )
					return returnValue;
			}
			return returnValue;
		}
	}

	/**
	 * A Panel containing a CheckboxTree with all clusters and their associated 
	 * Molecules.
	 */
	private class SelectorTreePanel extends JPanel {
		private CheckboxTree tree;

		/**
		 * Creates a new SelectorTreePanel
		 * 
		 * @param clusters The clusters to be displayed in the panel.
		 */
		public SelectorTreePanel( Collection<Collection<Molecule>> clusters )  {
			super( new BorderLayout( ));
			Language language = Settings.getLanguage( );
			DefaultMutableTreeNode rootNode = new CustomMutableTreeNode( 
				clusters, language.get( "Clusters" ));
			
			String clusterString = language.get( "Cluster" ) + " ";
			int clusterCount = 0;
			for ( Collection<Molecule> cluster : clusters ) {
				clusterCount++;
				DefaultMutableTreeNode clusterNode = 
					new CustomMutableTreeNode( cluster, clusterString+clusterCount );
				for ( Molecule molecule : cluster ) {
					DefaultMutableTreeNode moleculeNode = 
						new DefaultMutableTreeNode( molecule );
					// save the node in a map for later lookup.
					clusterNode.add( moleculeNode );
				}
				rootNode.add( clusterNode );
			}
			this.tree = new CheckboxTree( rootNode );
//			this.tree.setRootVisible( false );
			this.tree.setCheckingPath( new TreePath( rootNode ));
			this.tree.setSelectsByChecking( false );
			this.tree.getCheckingModel( ).setCheckingMode( 
				TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );
			this.add( new JScrollPane( tree ), BorderLayout.CENTER );
		}

		/**
		 * Gets the CheckboxTree contained in this Panel.
		 * 
		 * @return The CheckboxTree contained in this Panel.
		 */
		public CheckboxTree getTree( ) {
			return this.tree;
		}
	}

	/**
	 * A Panel containing a CheckBoxTree containing the available Samples in this
	 * study so they can be selected/deselected.
	 */
	private class SampleSelectorTreePanel extends JPanel {
		private CheckboxTree tree;
		private DefaultMutableTreeNode rootNode;

		/**
		 * Creates a new SampleSelectorTreePanel.
		 * 
		 * @param samples A Collection of the Samples to be displayed in this panel.
		 */
		public SampleSelectorTreePanel ( Collection<Sample> samples ) {
			super( new BorderLayout( ));
			Language language = Settings.getLanguage( );
			this.rootNode = new DefaultMutableTreeNode( language.get( "Samples" ));
			for ( Sample sample : samples ) {
				DefaultMutableTreeNode sampleNode = new DefaultMutableTreeNode( sample );
				this.rootNode.add( sampleNode );
			}
			this.tree = new CheckboxTree( this.rootNode );
			//this.tree.setRootVisible( false );
			this.tree.setCheckingPath( new TreePath( this.rootNode ));
			this.tree.setSelectsByChecking( false );
			this.tree.getCheckingModel( ).setCheckingMode( 
				TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );
			this.add( new JScrollPane( tree ), BorderLayout.CENTER );
		}

		/**
		 * Gets the CheckboxTree contained in this Panel.
		 * 
		 * @return The CheckboxTree contained in this Panel.
		 */
		public CheckboxTree getTree( ) {
			return this.tree;
		}

		/**
		 * Checks the status of the Checkboxes in the tree and returns the Samples
		 * which have been selected for display.
		 * 
		 * @return A Collection containing the selected Samples.
		 */
		public Collection<Sample> getSamples( ) {
			DefaultMutableTreeNode sampleNode = 
				(DefaultMutableTreeNode)this.rootNode.getFirstChild( );
			Collection<Sample> returnValue = new ArrayList<Sample>( );
			while( sampleNode != null ) {
				if ( this.tree.isPathChecked( new TreePath( sampleNode.getPath( )))) {
					returnValue.add( (Sample)sampleNode.getUserObject( ));
				}
				sampleNode = (DefaultMutableTreeNode)sampleNode.getNextSibling( );
			}
			return returnValue;
		}
	}

	/**
	 * A class which displays the visualization of Sample values in a JFreeChart.
	 */
	private class ClusterGraph extends JPanel
			implements TreeSelectionListener,TreeCheckingListener {
		private JFreeChart chart;

		/**
		 * Creates a new ClusterGraph.
		 */
		public ClusterGraph( ) {
			super( );
		}
		
		/**
		 * Displays a graph which contains the mean Sample value for each set of 
		 * Molecules in a cluseter.
		 * 
		 * @param node The TreeNode to display information about. Should only be 
		 *	called on the root node, or possibly an individual cluster.
		 * @return true if there is data to be displayed.
		 */
		public boolean setMeanGraph( DefaultMutableTreeNode node ) {
			Language language = Settings.getLanguage( );
			Object userObject = node.getUserObject( );
			List<Sample> samples = 
				new ArrayList<Sample>( sampleSelectorTree.getSamples( ));
			if ( samples.size( ) < 1 ) {
				this.chart = null;
				return false;
			}
			XYSeriesCollection xyDataset = new XYSeriesCollection( );
			Collection <Dataset> clusters = null;
			Map<Integer,NumberList> clusterData; 
			// If the root node is selected (which should be the only time this
			// method is called)
			if ( node.getLevel( ) == ROOT ) {
				// iterate through each of the nodes indicating a dataset.
				for ( DefaultMutableTreeNode datasetNode = 
					    (DefaultMutableTreeNode)node.getFirstChild( );
				      datasetNode != null; datasetNode = datasetNode.getNextSibling( )){

					XYSeries data = new XYSeries( datasetNode.toString( ));
					// create a map for holding the data for an entire cluster.
					clusterData = new HashMap<Integer,NumberList>( );
					// iterate through each of the nodes indicating a Molecule
					Collection<Molecule> molecules = new ArrayList<Molecule>( );
					for ( DefaultMutableTreeNode moleculeNode = 
					      (DefaultMutableTreeNode)datasetNode.getFirstChild( );
								moleculeNode != null; 
								moleculeNode = moleculeNode.getNextSibling( )) {
						molecules.add( (Molecule)moleculeNode.getUserObject( ));
					}
					int index = 0;
					for ( Sample sample : samples ) {
						data.add( index++, sample.getValues( molecules ).getMean( ));
					}
					xyDataset.addSeries( data );
				}
			}
			// check to make sure there is actually data in the XYSeriesCollection
			if ( xyDataset.getSeriesCount( ) == 0 ) {
				this.chart = null;
				return false;
			}
			this.chart = ChartFactory.createXYLineChart( 
					language.get( "Sample concentrations" ), //title
					language.get( "Sample" ),        // x axis label
					language.get( "Concentration" ), // y axis label
					xyDataset,                       // plot data
					PlotOrientation.VERTICAL,        // Plot Orientation
					true,                            // show legend
					false,                           // use tooltips
					false                            // configure chart to generate URLs
				);

				XYPlot plot = this.chart.getXYPlot( );
				XYLineAndShapeRenderer renderer = 
					(XYLineAndShapeRenderer)plot.getRenderer( );
				plot.setRenderer( renderer );
				// find the index of this experiment for appropriate coloring.
				for ( int i=0; i < xyDataset.getSeriesCount( ); i++ ) {
					renderer.setSeriesStroke( i, new BasicStroke( 2 ));
					renderer.setSeriesShapesVisible( i, true );
					renderer.setSeriesPaint( i, 
						Color.getHSBColor( (float)i/xyDataset.getSeriesCount( ), 1.0f, 0.5f ));
				}
				plot.setBackgroundPaint( Color.WHITE );
				plot.setRangeGridlinePaint( Color.GRAY );
				plot.setDomainGridlinePaint( Color.GRAY );
				TickUnits tickUnits = new TickUnits( );
				double tickIndex = 0.0;
				for ( Sample sample : samples ) {
					tickUnits.add( new SampleTickUnit( tickIndex, samples ));
					tickIndex++;
				}
				plot.getDomainAxis( ).setStandardTickUnits( tickUnits );
				plot.getDomainAxis( ).setVerticalTickLabels( true );
				return true;
		}

		/**
		 * Sets the graph to display data from each experiment on the passed in 
		 * TreeNode.
		 * 
		 * @param node The selected node for the graph.
		 * @returns true if creating the graph was successful.
		 */
		public boolean setGraph( DefaultMutableTreeNode node ) {
			Object userObject = node.getUserObject( );
			Language language = Settings.getLanguage( );
			List<Sample> samples = 
				new ArrayList<Sample>( sampleSelectorTree.getSamples( ));
			if ( samples.size( ) < 1 ) {
				this.chart = null;
				return false;
			}
			XYSeriesCollection xyDataset = new XYSeriesCollection( );
			XYSeries data;
			if ( node.getLevel( ) == CLUSTER ) {
				// If a cluster node is selected, follow the tree down to instance nodes
				// and add them to the xyDataset if they are checked.
				for( DefaultMutableTreeNode moleculeNode =
						 (DefaultMutableTreeNode)node.getFirstChild( );
						 moleculeNode != null;
						 moleculeNode = moleculeNode.getNextSibling( )) {

					if ( selectorTree.getTree( ).isPathChecked( 
							 new TreePath( moleculeNode.getPath( )))) {
						Molecule molecule = (Molecule)moleculeNode.getUserObject( );
						data = new XYSeries( molecule.getId( ));
						int index = 0;
						for ( Sample sample : samples )	{
							data.add( index++, sample.getValue( molecule ));
						}
						xyDataset.addSeries( data );
					}
				}
			} else if ( node.getLevel( ) == MOLECULE ) {
				// If an instance node is selected, add it if it is checked.
				if ( selectorTree.getTree( ).isPathChecked( 
					   new TreePath( node.getPath( )))){
					Molecule molecule = (Molecule)userObject;
					data = new XYSeries( molecule.getId( ));
					int index = 0;
					for ( Sample sample : samples ) {
						data.add( index++, sample.getValue( molecule ));
					}
					xyDataset.addSeries( data );
				}
			}
			// check to make sure there is actually data in the XYSeriesCollection
			if ( xyDataset.getSeriesCount( ) == 0 ) {
				this.chart = null;
				return false;
			}
			this.chart = ChartFactory.createXYLineChart( 
				language.get( "Sample concentrations" ), //title
				language.get( "Sample" ),        // x axis label
				language.get( "Concentration" ), // y axis label
				xyDataset,                         // plot data
				PlotOrientation.VERTICAL,        // Plot Orientation
				true,                           // show legend
				false,                           // use tooltips
				false                            // configure chart to generate URLs (?)
			);

			XYPlot plot = this.chart.getXYPlot( );
			XYLineAndShapeRenderer renderer = 
				(XYLineAndShapeRenderer)plot.getRenderer( );
			plot.setRenderer( renderer );
			// find the index of this experiment for appropriate coloring.
			for ( int i=0; i < xyDataset.getSeriesCount( ); i++ ) {
				renderer.setSeriesStroke( i, new BasicStroke( 2 ));
				renderer.setSeriesShapesVisible( i, true );
				renderer.setSeriesPaint( i, 
					Color.getHSBColor( (float)i/xyDataset.getSeriesCount( ), 1.0f, 0.5f ));
			}
			plot.setBackgroundPaint( Color.WHITE );
			plot.setRangeGridlinePaint( Color.GRAY );
			plot.setDomainGridlinePaint( Color.GRAY );
			TickUnits tickUnits = new TickUnits( );
			double tickIndex = 0.0;
			for ( Sample sample : samples ) {
				tickUnits.add( new SampleTickUnit( tickIndex, samples ));
				tickIndex++;
			}
			plot.getDomainAxis( ).setStandardTickUnits( tickUnits );
			plot.getDomainAxis( ).setVerticalTickLabels( true );
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
			if ( level > MOLECULE ) { 
				this.setGraph( (DefaultMutableTreeNode)path.getPathComponent( MOLECULE ));
			} else if ( level > CLUSTER ) {
				this.setGraph( (DefaultMutableTreeNode)path.getPathComponent( CLUSTER ));
			} else {
				this.setMeanGraph( (DefaultMutableTreeNode)path.getPathComponent( ROOT ));
			}
			this.repaint( );
		}

		/**
		 * The valueChanged method of the TreeCheckingListner interface.
		 * 
		 * @param e The event which triggered this action.
		 */
		public void valueChanged( TreeCheckingEvent e ) {
			if ( selectorTree.getTree( ).isSelectionEmpty( )) {
				selectorTree.getTree( ).setSelectionRow( 0 );
			}
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
				BufferedImage drawing = 
					this.chart.createBufferedImage( size.width, size.height );
				g.drawImage( drawing, 0, 0, Color.WHITE, this );
			}
		}
	}
}



