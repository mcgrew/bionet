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
import edu.purdue.cc.jsysnet.util.Experiment;
import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.cc.jsysnet.util.Sample;
import edu.purdue.cc.jsysnet.util.SampleGroup;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.SOM;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.tools.data.StreamHandler;
import net.sf.javaml.core.DefaultDataset;

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
	private JMenuBar menuBar;
	private JMenu viewMenu;
	private JMenuItem chooseSampleGroupsMenuItem;
	private JMenuItem removeSampleGroupsMenuItem;

	private JPanel selectorPanel;
	private SampleSelectorTreePanel sampleSelectorTree;
	private JButton recomputeButton;
	private JSplitPane splitPane;
	private JSplitPane treeSplitPane;
	private JPanel clusterGraphPanel;
	private Collection<Experiment> experiments;
	private List<Sample> samples;
	private Collection<Molecule> molecules;
	private Clusterer clusterer;
	private Collection<SampleGroup> sampleGroups;

	private static final int ROOT = 0;
	private static final int CLUSTER = 1;
	private static final int MOLECULE = 2;

	/**
	 * A class for displaying information about a Clustering
	 */
	public TimeCourseStudyDisplayPanel( ) {
		super( new BorderLayout( ));
		Language language = Settings.getLanguage( );
		this.addComponentListener( new InitialSetup( ));
		this.menuBar = new JMenuBar( );
		this.viewMenu = new JMenu( language.get( "View" ));
		this.viewMenu.setMnemonic( KeyEvent.VK_V );
		this.removeSampleGroupsMenuItem = 
			new JMenuItem( language.get( "Reset Sample Groups" ), KeyEvent.VK_G );
		this.chooseSampleGroupsMenuItem = 
			new JMenuItem( language.get( "Choose Sample Groups" ), KeyEvent.VK_G );
		this.viewMenu.add( this.removeSampleGroupsMenuItem );
		this.viewMenu.add( this.chooseSampleGroupsMenuItem );
		this.chooseSampleGroupsMenuItem.addActionListener( this );
		this.removeSampleGroupsMenuItem.addActionListener( this );
		this.add( menuBar, BorderLayout.NORTH );
		this.menuBar.add( this.viewMenu );
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
		this.samples = new SampleGroup( "" );
		this.molecules = new TreeSet<Molecule>( );
		for( Experiment experiment : experiments ) {
			this.samples.addAll( experiment.getSamples( ));
			this.molecules.addAll( experiment.getMolecules( ));
		}
		Collection<SampleGroup> sampleGroups = new ArrayList<SampleGroup>( );
		sampleGroups.add( new SampleGroup( "", samples ));
		this.sampleSelectorTree = new SampleSelectorTreePanel( this.experiments );

		this.splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		this.treeSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		this.recomputeButton = 
			new JButton( Settings.getLanguage( ).get( "Recompute" ));
		this.recomputeButton.addActionListener( this );
		JPanel treePanel = new JPanel( new BorderLayout( ));
		this.treeSplitPane.setBottomComponent( this.sampleSelectorTree );
		treePanel.add( this.treeSplitPane, BorderLayout.CENTER );
		treePanel.add( this.recomputeButton, BorderLayout.SOUTH );
		this.splitPane.setLeftComponent( treePanel );
		this.clusterGraphPanel = new JPanel( new GridLayout( 1, 1, 3, 3 ));
		this.clusterGraphPanel.setBackground( Color.LIGHT_GRAY );
		this.splitPane.setRightComponent( this.clusterGraphPanel );
		this.add( this.splitPane, BorderLayout.CENTER );
		this.splitPane.setDividerLocation( 250 );

		this.selectorPanel = new JPanel( new GridLayout( 1, 1 ));
		this.treeSplitPane.setTopComponent( this.selectorPanel );
		this.setSampleGroups( sampleGroups );

		return true;
	}

	/**
	 * Sets the clusterer to be used by this TimeCourseStudyDisplayPanel.
	 * 
	 * @param clusterer The new clusterer to use.
	 */
	private void setClusterer( Clusterer clusterer ) {
		this.clusterer = clusterer;
	}

	/**
	 * Gets the current clusterer being used by this TimeCourseStudyDisplayPanel.
	 * 
	 * @return The clusterer currently in use.
	 */
	private Clusterer getClusterer( ) {
		return this.clusterer;
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
		Object classValue = i.classValue( );
		if ( classValue instanceof Molecule ) {
			return (Molecule)classValue;
		}
		for ( Molecule m : this.molecules ) {
			if ( m.getId( ).equals( classValue.toString( ))) {
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
		return "Clustering";
	}

	/**
	 * The actionPerformed method of the ActionListener interface. Performs 
	 * cluster recalculation when the 'recalculate' button is clicked.
	 * 
	 * @param e The event which triggered this action.
	 */
	public void actionPerformed( ActionEvent e ) {
		Logger logger = Logger.getLogger( getClass( ));
		Language language = Settings.getLanguage( );
		Object source = e.getSource( );
		if ( source == this.recomputeButton ) {
			this.setSampleGroups( this.sampleGroups );
		} else if ( source == this.chooseSampleGroupsMenuItem ) {
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

	public void setSampleGroups( Collection<SampleGroup> sampleGroups ) {
		Component frame = this;
		while( !(frame instanceof Frame) && frame != null ) {
			frame = frame.getParent( );
		}
		ClusterSelectionDialog dialog =
			new ClusterSelectionDialog( (Frame)frame, 
				Settings.getLanguage( ).get( "Choose Clustering Method" ),
				this.sampleGroups != null );

		if ( dialog.getReturnValue( ) == null ) {
			return;
		}
		this.sampleGroups = sampleGroups;
		Collection<Molecule> molecules;
		try {
			ClusterSelectorTreePanel clusterPanel = 
				(ClusterSelectorTreePanel)this.selectorPanel.getComponent( 0 );
			molecules = clusterPanel.getCheckedMolecules( );
		} catch (ArrayIndexOutOfBoundsException e ) {
			molecules = this.molecules;
		} catch ( ClassCastException e ) {
			molecules = this.molecules;
			Logger.getLogger( this.getClass( )).error( e, e );
		}
		this.selectorPanel.removeAll( );
		this.clusterGraphPanel.removeAll( );
		int rows = (int)Math.ceil( Math.sqrt( sampleGroups.size( )));
		int cols = (int)Math.ceil( sampleGroups.size( ) / rows );
		GridLayout layout = (GridLayout)this.selectorPanel.getLayout( );
		layout.setRows( rows );
		layout.setColumns( cols );
		layout = (GridLayout)this.clusterGraphPanel.getLayout( );
		layout.setRows( rows );
		layout.setColumns( cols );

		Map<Thread,RunnableClusterer> clusterers = 
			new HashMap<Thread,RunnableClusterer>( );
		for( SampleGroup group : sampleGroups ) {
			RunnableClusterer clusterer = 
				new RunnableClusterer( dialog.getReturnValue( ));
			SampleGroup filteredGroup = new SampleGroup( group );
			filteredGroup.retainAll( this.sampleSelectorTree.getSamples( ));
			clusterer.setDataset( this.getDataset( molecules, filteredGroup ));
			Thread thread = new Thread( clusterer );
			thread.start( );
			clusterers.put( thread, clusterer );
		}

		Iterator<SampleGroup> groupIter = sampleGroups.iterator( );
		Collection<ClusterSelectorTreePanel> clusterTreeList = 
			new ArrayList<ClusterSelectorTreePanel>( );
		for ( Map.Entry<Thread,RunnableClusterer> clusterer : clusterers.entrySet( )) {
			try { 
				clusterer.getKey( ).join( );
				Dataset[] result = clusterer.getValue( ).getResult( );
				Collection<Collection<Molecule>> clusters = 
					new TreeSet<Collection<Molecule>>( new DatasetComparator( ));
				Collection<Molecule> unclustered = 
					new ArrayList<Molecule>( this.molecules );
				for ( Dataset dataset : result ) {
					Collection<Molecule> cluster = this.getMoleculesForDataset( dataset );
					clusters.add( cluster );
					unclustered.removeAll( cluster );
				}

				// create a new clustertree and add the appropriate listeners
				ClusterSelectorTreePanel clusterTree = 
					new ClusterSelectorTreePanel( clusters );
				if ( unclustered.size( ) > 0 ) {
					clusterTree.add( 
						unclustered, 
						String.format( Settings.getLanguage( ).get( "Unclustered" ) + " (%d)",
							unclustered.size( )),
						false );
				}
				for ( ClusterSelectorTreePanel tree : clusterTreeList ) {
					tree.addTreeCheckingListener( clusterTree );
					clusterTree.addTreeCheckingListener( tree );
				}
				clusterTreeList.add( clusterTree );

				this.selectorPanel.add( clusterTree );
				ClusterGraph graph = new ClusterGraph( this.sampleSelectorTree, 
																							 clusterTree, 
																							 groupIter.next( ));
				this.clusterGraphPanel.add( graph );
				graph.setMeanGraph( clusterTree.getRoot( ));
			} catch ( InterruptedException e ) {
				Logger.getLogger( this.getClass( )).debug( e, e );
			}
		}
		this.removeSampleGroupsMenuItem.setEnabled( sampleGroups.size( ) > 1 );
		this.validate( );
	}

	/**
	 * Creates a Dataset for use with JavaML
	 * 
	 * @param molecules A Collection of Molecules to use in the Dataset
	 * @param samples A Collection of Samples to use for determining values.
	 * @return A Dataset suitable for use with JavaML Clustering.
	 */
	public Dataset getDataset( Collection<Molecule> molecules, 
	                           Collection<Sample> samples ) {
		Dataset returnValue = new DefaultDataset( );
		for ( Molecule m : molecules ) {
			returnValue.add( 
				new DenseInstance( m.getValues( samples ).toDoubleArray( ), m ));
		}
		return returnValue;
	}

	public Collection<SampleGroup> getSampleGroups( ) {
		return this.sampleGroups;
	}

	// ============================= PRIVATE CLASSES =============================
	// ============================ RunnableClusterer ============================
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
	// ============================ InitialSetup =================================
	/**
	 * A class for handling the sizng of panels and such in 
	 * TimeCourseStudyDisplayPanel when it is initially shown.
	 */
	private class InitialSetup extends ComponentAdapter {
		public InitialSetup( ) {
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

	// =========================== DatasetComparator =============================
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

	// ======================= ClusterSelectorTreePanel ==========================
	/**
	 * A Panel containing a CheckboxTree with all clusters and their associated 
	 * Molecules.
	 */
	private class ClusterSelectorTreePanel extends CheckboxTreePanel 
	                                       implements TreeCheckingListener {
		private JScrollPane scrollPane;
		private static final int CLUSTER = 1;
		private static final int MOLECULE = 2;

		/**
		 * Creates a new emtpy ClusterSelectorTreePanel.
		 */
		public ClusterSelectorTreePanel( ) {
			super( new DefaultMutableTreeNode( 
				Settings.getLanguage( ).get( "Clusters" )));
		}

		public ClusterSelectorTreePanel( TreeNode rootNode ) {
			super( rootNode );
		}

		/**
		 * Creates a new ClusterSelectorTreePanel
		 * 
		 * @param clusters The clusters to be displayed in the panel.
		 */
		public ClusterSelectorTreePanel( TreeNode rootNode,
		                                 Collection<Collection<Molecule>> clusters )  {
			this( rootNode );
			int clusterCount = 0;
			String clusterString = 
				Settings.getLanguage( ).get( "Cluster" ) + " %d (%d)";
			for ( Collection<Molecule> cluster : clusters ) {
				if ( cluster.size( ) > 0 ) {
					this.add( cluster, 
										String.format( clusterString, 
																	 ++clusterCount, 
																	 cluster.size( )));
				}
			}
		}

		public ClusterSelectorTreePanel( Collection<Collection<Molecule>> clusters )  {
			this( new DefaultMutableTreeNode( 
				Settings.getLanguage( ).get( "Clusters" )), clusters );
		}

		public void clear( ) {
			DefaultMutableTreeNode rootNode = this.getRoot( );
			while( rootNode.getChildCount( ) > 0 ) {
				rootNode.remove( 0 );
			}
			((DefaultTreeModel)this.tree.getModel( )).reload( );
			this.reload( );
		}

		/**
		 * Adds a cluster of molecules to the tree and sets it as checked.
		 * 
		 * @param cluster The cluster to be added to the tree
		 * @param name The name for this cluster, usually "Cluster" followed by a
		 *	number.
		 */
		public void add( Collection<Molecule> cluster, String name ) {
			this.add( cluster, name, true );
		}

		/**
		 * Adds a cluster of molecules to the tree.
		 * 
		 * @param cluster The cluster to be added to the tree
		 * @param name The name for this cluster, usually "Cluster" followed by a
		 * @param checked whether the initial state should be checked.
		 *	number.
		 */
		public void add( Collection<Molecule> cluster, String name, 
		                  boolean checked ) {
			Language language = Settings.getLanguage( );
			
			int clusterCount = 0;
			// add the cluster to the tree.
			DefaultMutableTreeNode clusterNode = 
				new CustomMutableTreeNode( cluster, name );
			for ( Molecule molecule : cluster ) {
				DefaultMutableTreeNode moleculeNode = 
					new DefaultMutableTreeNode( molecule );
				clusterNode.add( moleculeNode );
			}

			((DefaultTreeModel)this.tree.getModel( )).insertNodeInto( 
				clusterNode, this.getRoot( ), this.getRoot( ).getChildCount( ));
			if ( checked ) {
				this.check( this.getRoot( ));
				this.check( clusterNode );
			} else {
				this.uncheck( clusterNode );
			}
			this.tree.expandPath( new TreePath( this.getRoot( ).getPath( )));
			this.tree.setSelectsByChecking( false );
			this.tree.getCheckingModel( ).setCheckingMode( 
				TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );
			this.repaint( );
		}

		/**
		 * Returns a Collection containing all of the molecules whose checkboxes are
		 * selected.
		 * 
		 * @return A Collection of the "Checked" molecules in the tree.
		 */
		public Collection<Molecule> getCheckedMolecules( ) {
			Collection<Molecule> returnValue = new TreeSet<Molecule>( );
			Enumeration nodeEnum = 
				((DefaultMutableTreeNode)this.getTree( ).getModel( ).getRoot( )).
					breadthFirstEnumeration( );

			while ( nodeEnum.hasMoreElements( )) {
				DefaultMutableTreeNode node = 
					(DefaultMutableTreeNode)nodeEnum.nextElement( );
				if ( node.getLevel( ) == MOLECULE && this.isChecked( node )) {
					returnValue.add( (Molecule)node.getUserObject( ));
				}
			}

			return returnValue;
		}

		/**
		 * The valueChanged method of the TreeCheckingListener interface. Listens for
		 * changes to other ClusterTreeSelectorPanels and mirrors them.
		 * 
		 * @param e The event which triggered this action.
		 */
		public void valueChanged( TreeCheckingEvent e ) {
			boolean checked = e.isCheckedPath( );
			ClusterSelectorTreePanel eventPanel = 
				(ClusterSelectorTreePanel)e.getSource( );
			TreeNode eventNode = (TreeNode)e.getPath( ).getLastPathComponent( );

			Logger.getLogger( this.getClass( )).debug( 
				e.getPath( ).getLastPathComponent( ).toString( ) + " " + 
				( checked ? "checked" : "unchecked" ));
			int level = e.getPath( ).getPathCount( ) - 1;

			if ( level <= MOLECULE )	{
				// cache the list of Molecule nodes for performance.
				ArrayList<TreeNode> myMoleculeNodes = new ArrayList<TreeNode>( );
				Iterator<TreeNode> descIter = 
					this.descendantIterator( this.getRoot( ), MOLECULE );

				Iterator<TreeNode> otherDescIter = 
					eventPanel.descendantIterator( eventNode, MOLECULE );
				while( descIter.hasNext( )) {
					myMoleculeNodes.add( descIter.next( ));
				}

				while ( otherDescIter.hasNext( )) {
					TreeNode otherMoleculeNode = otherDescIter.next( );
					String changed = eventNode.toString( );
					for ( TreeNode node : myMoleculeNodes ) {
						if ( node.toString( ).equals( otherMoleculeNode.toString( ))) {
							if ( this.isChecked( node ) != checked ) {
								this.setChecked( node, checked );
							}
						}
					}
				}
			}
		}
	}

	// ========================== ClusterGraph ===================================
	/**
	 * A class which displays the visualization of Sample values in a JFreeChart.
	 */
	private class ClusterGraph extends JPanel
			implements TreeSelectionListener,TreeCheckingListener {
		private JFreeChart chart;
		private SampleSelectorTreePanel sampleTree;
		private ClusterSelectorTreePanel clusterTree;
		private SampleGroup samples;

		/**
		 * Creates a new ClusterGraph.
		 *
		 * @param sampleTree The SampleSelectorTreePanel used to manipulate
		 *	this graph.
		 */
		public ClusterGraph( SampleSelectorTreePanel sampleTree, 
		                     ClusterSelectorTreePanel clusterTree,
												 SampleGroup samples ) {
			super( );
			this.sampleTree = sampleTree;
			this.clusterTree = clusterTree;
			this.samples = samples;
			this.sampleTree.addTreeCheckingListener( this );
			this.clusterTree.addTreeCheckingListener( this );
			this.clusterTree.addTreeSelectionListener( this );
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
			SampleGroup samples = new SampleGroup( this.samples );
			samples.retainAll( this.sampleTree.getSamples( ));
			if ( samples.size( ) < 1 ) {
				this.chart = null;
				return false;
			}
			XYSeriesCollection xyDataset = new XYSeriesCollection( );
			Collection <Dataset> clusters = null;
			// If the root node is selected (which should be the only time this
			// method is called)
			if ( node.getLevel( ) == ROOT ) {
				// make sure the root node has children.
				if ( node.getChildCount( ) < 1 ) {
					this.chart = null;
					return false;
				}
				// iterate through each of the nodes indicating a dataset.
				for ( DefaultMutableTreeNode datasetNode = 
					    (DefaultMutableTreeNode)node.getFirstChild( );
				      datasetNode != null; datasetNode = datasetNode.getNextSibling( )){

					if ( clusterTree.isChecked( datasetNode )) {

						XYSeries data = new XYSeries( datasetNode.toString( ));
						// iterate through each of the nodes indicating a Molecule
						Collection<Molecule> molecules = new ArrayList<Molecule>( );
						for ( DefaultMutableTreeNode moleculeNode = 
									(DefaultMutableTreeNode)datasetNode.getFirstChild( );
									moleculeNode != null; 
									moleculeNode = moleculeNode.getNextSibling( )) {

							if ( clusterTree.isChecked( moleculeNode )) {
								molecules.add( (Molecule)moleculeNode.getUserObject( ));
							}
						}
						int index = 0;
						for ( Sample sample : samples ) {
							data.add( index++, sample.getValues( molecules ).getMean( ));
						}
						xyDataset.addSeries( data );
					}
				}
			}
			// check to make sure there is actually data in the XYSeriesCollection
			if ( xyDataset.getSeriesCount( ) == 0 ) {
				this.chart = null;
				return false;
			}
			String chartTitle = (( samples == null ) ? "" :
				samples.toString( ) + " " ) + 
				language.get( "Sample concentrations" );
			this.chart = ChartFactory.createXYLineChart( 
				chartTitle,                      //title
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
			SampleGroup samples = new SampleGroup( this.samples );
			samples.retainAll( this.sampleTree.getSamples( ));
			if ( samples.size( ) < 1 ) {
				this.chart = null;
				return false;
			}
			XYSeriesCollection xyDataset = new XYSeriesCollection( );
			XYSeries data;
			if ( node.getLevel( ) == CLUSTER ) {
				// If a cluster node is selected, follow the tree down to molecule nodes
				// and add them to the xyDataset if they are checked.
				if ( node.getChildCount( ) > 0 ) {
					for( DefaultMutableTreeNode moleculeNode =
							 (DefaultMutableTreeNode)node.getFirstChild( );
							 moleculeNode != null;
							 moleculeNode = moleculeNode.getNextSibling( )) {

						if ( clusterTree.isChecked( moleculeNode )) {
							Molecule molecule = (Molecule)moleculeNode.getUserObject( );
							data = new XYSeries( molecule.getId( ));
							int index = 0;
							for ( Sample sample : samples )	{
								data.add( index++, sample.getValue( molecule ));
							}
							xyDataset.addSeries( data );
						}
					}
				}
			} else if ( node.getLevel( ) == MOLECULE ) {
				// If an instance node is selected, add it if it is checked.
				if ( clusterTree.isChecked( node )){
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
			String chartTitle = (( samples == null ) ? "" :
				samples.toString( ) + " " ) + 
				language.get( "Sample concentrations" );
			this.chart = ChartFactory.createXYLineChart( 
				chartTitle,                      //title
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
			if ( clusterTree.getTree( ).isSelectionEmpty( )) {
				clusterTree.getTree( ).setSelectionRow( 0 );
			}
			this.valueChanged( new TreeSelectionEvent( 
				e.getSource( ),
				clusterTree.getTree( ).getSelectionPath( ),
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



