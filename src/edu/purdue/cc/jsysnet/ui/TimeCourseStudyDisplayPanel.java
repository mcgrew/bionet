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
import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.cc.jsysnet.util.Sample;
import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.NumberList;
import edu.purdue.cc.jsysnet.io.JavaMLTranslator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.SOM;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.StreamHandler;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;

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

import org.apache.log4j.Logger;

public class TimeCourseStudyDisplayPanel extends JPanel {
	private SelectorTreePanel selectorTree;
	private JSplitPane splitPane;
	private ClusterGraph graph;
	private Collection<Experiment> experiments;
	private List<Sample> samples;

	private static final int CLUSTER = 1;
	private static final int INSTANCE = 2;

	public TimeCourseStudyDisplayPanel( ) {
		super( new BorderLayout( ));
	}
		
	public boolean createGraph( Collection <Experiment> experiments ) {
		Logger logger = Logger.getLogger( getClass( ));
		this.experiments = experiments;
		this.samples = new ArrayList<Sample>( );
		for( Experiment experiment : experiments ) {
			samples.addAll( experiment.getSamples( ));
		}
		try {
			int len;
			InputStream dataStream = new JavaMLTranslator( experiments );
			logger.debug( "Starting Clustering ..." );
			Dataset data = StreamHandler.load( 
				new InputStreamReader( dataStream ), 0, ",");
			
			RunnableClusterer som = new RunnableClusterer(
				new SOM(
					5,                             // number of dimensions on the x axis
					5,                             // number of dimensions on the y axis
					SOM.GridType.HEXAGONAL,        // type of grid.
					50000,                          // number of iterations
					0.1,                           // learning rate of algorithm
					8,                             // initial radius
					SOM.LearningType.LINEAR,       // type of learning to use
					SOM.NeighbourhoodFunction.STEP // neighborhood function.
				),
				data );
			Thread somThread = new Thread( som );
			somThread.start( );

			logger.debug( "Waiting for SOM Clustering to complete..." );
			somThread.join( );
			Collection<Dataset> clusters = new TreeSet<Dataset>( new DatasetComparator( ));
			for ( Dataset d : som.getResult( )) {
				clusters.add( d );
			}
			int i=0;
			this.splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
			this.selectorTree = new SelectorTreePanel( clusters );
			this.splitPane.setLeftComponent( this.selectorTree );
//			this.splitPane.setRightComponent( new JPanel( )); // placeholder
			this.graph = new ClusterGraph( );
			this.splitPane.setRightComponent( this.graph );
			this.selectorTree.getTree( ).addTreeSelectionListener( this.graph );
			this.add( this.splitPane, BorderLayout.CENTER );
			this.splitPane.setDividerLocation( 250 );

		} catch ( Exception e ) {
			logger.error( e, e );
			return false;
		}
		
		return true;
	}

	public String getTitle( ) {
		return "Time Course Study";
	}

	private class RunnableClusterer implements Runnable {
		private Clusterer clusterer;
		private Dataset dataset;
		private Dataset[] result;
		
		public RunnableClusterer( Clusterer clusterer ) {
			this.clusterer = clusterer;
		}

		public RunnableClusterer( Clusterer clusterer, Dataset dataset ) {
			this.clusterer = clusterer;
			this.dataset = dataset;
		}

		public void setDataset ( Dataset dataset ) {
			this.dataset = dataset;
		}

		public void run( ) {
			this.result = this.clusterer.cluster( this.dataset );
		}

		public Dataset[] getResult( ) {
			return result;
		}
	}

	private class DatasetComparator implements Comparator<Dataset> {

		public int compare( Dataset d1, Dataset d2 ) {
			int returnValue = d2.size( ) - d1.size( );
			int i=0;
			while ( returnValue == 0 && i < d1.size( )) {
				returnValue = d1.get( i ).classValue( ).toString( ).compareTo( 
					d2.get( i ).classValue( ).toString( ));
			}
			return returnValue;
		}
	}

	private class SelectorTreePanel extends JPanel {
		private CheckboxTree tree;
		private Map<Instance,DefaultMutableTreeNode> nodeMap;

		public SelectorTreePanel( Collection<Dataset> clusters )  {
			super( new BorderLayout( ));
			Language language = Settings.getLanguage( );
			this.nodeMap = new HashMap<Instance,DefaultMutableTreeNode>( );
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode( 
				language.get( "Clusters" ));
			
			String clusterString = language.get( "Cluster" ) + " ";
			int clusterCount = 0;
			for ( Collection<Instance> cluster : clusters ) {
				clusterCount++;
				DefaultMutableTreeNode clusterNode = 
					new CustomMutableTreeNode( cluster, clusterString+clusterCount );
				for ( Instance instance : cluster ) {
					DefaultMutableTreeNode instanceNode = 
						new DefaultMutableTreeNode( instance ) {
							public String toString( ) {
								return ((Instance)this.getUserObject( )).classValue( ).toString( );
							}
						};
					for( Sample sample : samples ) {
						DefaultMutableTreeNode sampleNode = 
							new DefaultMutableTreeNode( sample );
						instanceNode.add( sampleNode );
					}
					// save the node in a map for later lookup.
					nodeMap.put( instance, instanceNode );
					clusterNode.add( instanceNode );
				}
				rootNode.add( clusterNode );
			}
			this.tree = new CheckboxTree( rootNode );
			this.tree.setRootVisible( false );
			this.tree.setCheckingPath( new TreePath( rootNode ));
			this.tree.setSelectsByChecking( false );
			this.tree.getCheckingModel( ).setCheckingMode( 
				TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );
			this.add( new JScrollPane( tree ), BorderLayout.CENTER );
		}

		public CheckboxTree getTree( ) {
			return this.tree;
		}

		public Map<Sample,Number> getSamplesFiltered( Instance instance ) {
			Map <Sample,Number> returnValue = new HashMap<Sample,Number>( );
			for ( Integer i : instance.keySet( )) {
				returnValue.put( samples.get( i.intValue( )), instance.get( i ));
			}
			return returnValue;
		}
	}

	private class ClusterGraph extends JPanel
			implements TreeSelectionListener,TreeCheckingListener {
		private JFreeChart chart;

		public ClusterGraph( ) {
			super( );
		}
		
		/**
		 * Sets the graph to display data from each experiment on the passed in molecule id.
		 * 
		 * @param 
		 * @returns true if creating the graph was successful.
		 */
		public boolean setGraph( DefaultMutableTreeNode node ) {
			Object userObject = node.getUserObject( );
			Language language = Settings.getLanguage( );
			XYSeriesCollection xyDataset = new XYSeriesCollection( );
			Dataset dataset;
			if ( userObject instanceof Dataset ) {
				dataset = (Dataset)userObject;
			} else {
				dataset = new DefaultDataset( );
				dataset.add( (Instance)userObject);
			}
			for ( Instance instance : dataset ) {
				XYSeries data = new XYSeries( instance.classValue( ).toString( ));
				for ( Map.Entry<Integer,Double> value : instance.entrySet( ))	{
					data.add( value.getKey( ), value.getValue( ));
				}
				xyDataset.addSeries( data );
			}
			this.chart = ChartFactory.createXYLineChart( 
				language.get( "Sample concentrations" ), //title
				language.get( "Sample" ),        // x axis label
				language.get( "Concentration" ), // y axis label
				xyDataset,                         // plot data
				PlotOrientation.VERTICAL,        // Plot Orientation
				true,                           // show legend
				false,                           // use tooltips
				false                            // configure chart to generate URLs (?!)
			);

			XYPlot plot = this.chart.getXYPlot( );
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer( );
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
			if ( level > INSTANCE ) { 
				this.setGraph( (DefaultMutableTreeNode)path.getPathComponent( INSTANCE ));
			} else if ( level > CLUSTER ) {
				this.setGraph( (DefaultMutableTreeNode)path.getPathComponent( CLUSTER ));
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



