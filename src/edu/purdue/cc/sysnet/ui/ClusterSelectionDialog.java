/*

This file is part of SysNet.

SysNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SysNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SysNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.sysnet.ui;

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.FarthestFirst;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.KMedoids;
import net.sf.javaml.clustering.SOM;
import net.sf.javaml.clustering.SOM;
import net.sf.javaml.clustering.mcl.MCL;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.distance.LinearKernel;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;
import net.sf.javaml.distance.SpearmanRankCorrelation;
import net.sf.javaml.tools.weka.WekaClusterer;

import weka.clusterers.SimpleKMeans;

import org.apache.log4j.Logger;

/**
 * A class for selecting the clustering algorithm and parameters to use for 
 * Clustering.
 */
public class ClusterSelectionDialog  extends JDialog implements ActionListener {
	protected JButton clusterButton;
	protected JButton cancelButton;
	protected ClusterParameterPanel parameterPanel;
	protected JComboBox clusterSelectorComboBox;
	private Clusterer returnValue;
	private final static int hgap = 10;
	private final static int vgap = 10;
	private boolean okPressed = false;

	private final static int SOM_METHOD = 0;
	private final static int KMEANS_METHOD = 1;
	private final static int KMEDOIDS_METHOD = 2;
	private final static int FARTHESTFIRST_METHOD = 3;


	/**
	 * Creates a new ClusterSelectionDialog with the given Frame as it's parent.
	 * 
	 * @param owner The parent Frame of this Dialog. This frame will not be able
	 *	to receive input until the dialog is closed.
	 * @param title The title for this dialog.
	 */
	public ClusterSelectionDialog( Frame owner, String title ) {
		this( owner, title, true );
	}

	/**
	 * Creates a new ClusterSelectionDialog with the given Frame as it's parent.
	 * 
	 * @param owner The parent Frame of this Dialog. This frame will not be able
	 *	to receive input until the dialog is closed.
	 * @param title The title for this dialog.
	 * @param cancelEnabled whether or not the dialog should have the 'Cancel' 
	 *	button enabled to allow the user to cancel. This option is true if not
	 *	supplied.
	 */
	public ClusterSelectionDialog( Frame owner, String title, 
	                               boolean cancelEnabled ) {
		super( owner, title );
		this.setVisible( false );
		this.getContentPane( ).setLayout( new BorderLayout( hgap, vgap ));
		this.setBounds( Settings.getSettings( ).getInt( "window.main.position.x" ),
		                Settings.getSettings( ).getInt( "window.main.position.y" ),
		                500, 200 );

		Language language = Settings.getLanguage( );
		this.clusterButton = new JButton( language.get( "Cluster" ));
		this.cancelButton = new JButton( language.get( "Cancel" ));
		JPanel bottomPanel = new JPanel( new GridLayout( 1, 2, hgap, vgap ));
		bottomPanel.add( this.clusterButton );
		bottomPanel.add( this.cancelButton );
		this.clusterButton.addActionListener( this );
		this.cancelButton.addActionListener( this );

		JPanel topPanel = new JPanel( new BorderLayout( hgap, vgap ));
		JLabel clusterSelectorLabel = new JLabel( 
			language.get( "Clustering Method" ));
		this.clusterSelectorComboBox = new JComboBox( new Object[] {
			new SOMClusterParameterPanel( ),
//			new KMeansClusterParameterPanel( ),
			new SimpleKMeansClusterParameterPanel( ),
			new KMedoidsClusterParameterPanel( ),
			new FarthestFirstClusterParameterPanel( )
			});
		this.clusterSelectorComboBox.addActionListener( this );
		this.clusterSelectorComboBox.setSelectedIndex( 
			Settings.getSettings( ).getInt( "history.clustering.lastMethod", 0 ));

		topPanel.add( clusterSelectorLabel, BorderLayout.WEST );
		topPanel.add( this.clusterSelectorComboBox, BorderLayout.CENTER );


		this.add( topPanel, BorderLayout.NORTH );
		this.add( this.parameterPanel, BorderLayout.CENTER );
		this.add( bottomPanel, BorderLayout.SOUTH );

		this.addWindowListener( new WindowAdapter( ) {
			public void windowOpened( WindowEvent e ) {
				e.getWindow( ).pack( );
			}
		});
		if ( !cancelEnabled ) {
			this.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
			this.cancelButton.setEnabled( false );
		}
		this.setModalityType( Dialog.ModalityType.APPLICATION_MODAL );
		this.setResizable( false );
		this.setVisible( true );
		this.validate( );
		this.pack( );
	}

	/**
	 * Gets the return value for this dialog. This will return the Clusterer
	 *	selected by the user.
	 * 
	 * @return The Clusterer to be used based on the user selections.
	 */
	public Clusterer getReturnValue( ) {
		if ( this.okPressed ) {
			Settings.getSettings( ).setInt( "history.clustering.lastMethod", 
				this.clusterSelectorComboBox.getSelectedIndex( ));
			return this.parameterPanel.getClusterer( );
		} else {
			return null;
		}
	}

	/**
	 * Creates a new ClusterParameterDialog and returns the SampleGroups once the
	 * selection is made.
	 * 
	 * @param owner The parent of this dialog.
	 * @param title The title for the dialog.
	 */
	public static Clusterer showInputDialog(
			Frame owner, String title ) {

		ClusterSelectionDialog dialog = 
			new ClusterSelectionDialog( owner, title );
		return dialog.getReturnValue( );
	}

	/**
	 * The actionPerformed method of the ActionListener interface.
	 * 
	 * @see ActionListener#ActionPerformed( ActionEvent )
	 * @param event The event which produced this action.
	 */
	public void actionPerformed( ActionEvent event ) {
		Component source = (Component)event.getSource( );
		if ( source == this.clusterSelectorComboBox ) {
			ClusterParameterPanel selected = 
				(ClusterParameterPanel)((JComboBox)source).getSelectedItem( );
			if ( this.parameterPanel != null ) {
				this.remove( this.parameterPanel );
			}
			this.parameterPanel = selected;
			this.add( this.parameterPanel, BorderLayout.CENTER );
			this.validate( );
			this.pack( );
			this.repaint( );
		} else if ( source == this.clusterButton ) {
			this.okPressed = true;
			this.setVisible( false );
		} else if ( source == this.cancelButton ) {
			this.setVisible( false );
		}
	}

	//======================== PRIVATE CLASSES ==================================
	//==================== DistanceMeasureComboBox ==============================

	/**
	 * A class for selecting a distance measure. Used by several of the clusterers.
	 */
	private class DistanceMeasureComboBox extends JComboBox {

		/**
		 * Creates a new DistanceMeasureComboBox.
		 */
		public DistanceMeasureComboBox( ) {
			super( new Object [] {
				"Pearson Correlation",
				"Spearman Rank Correlation",
				"Euclidian Distance",
				"Linear Kernel"
			});
		}

		/**
		 * Returns the DistanceMeasure selected by the user in the dialog.
		 * 
		 * @return The selected DistanceMeasure.
		 */
		public DistanceMeasure getSelectedDistanceMeasure( ) {
			if ( "Pearson Correlation".equals( this.getSelectedItem( ).toString( )))
				return new PearsonCorrelationCoefficient( );

			if ( "Spearman Rank Correlation".equals( this.getSelectedItem( ).toString( )))
				return new SpearmanRankCorrelation( );

			if ( "Euclidian Distance".equals( this.getSelectedItem( ).toString( )))
				return new EuclideanDistance( );

			if ( "Linear Kernel".equals( this.getSelectedItem( ).toString( )))
				return new LinearKernel( );

			return null;
		}
	}
	// ======================== ClusterParameterPanel ===========================
	/**
	 * An abstract class for creating a panel for use in selecting cluster 
	 * parameters.
	 */
	private abstract class ClusterParameterPanel extends JPanel {
		protected Clusterer clusterer;

		/**
		 * Creates a new ClusterParameterPanel.
		 */
		protected ClusterParameterPanel( ) {
			super( );
		}

		/**
		 * Creates a new ClusterParameterPanel with the specified layout.
		 * 
		 * @param layout The LayoutManager to be used.
		 */
		protected ClusterParameterPanel( LayoutManager layout ) {
			super( layout );
		}
		
		/**
		 * Returns the Clusterer selected by the user.
		 * 
		 * @return The selected Clusterer.
		 */
		public abstract Clusterer getClusterer( );
	}

	// ======================== SOMClusterParameterPanel ========================
	/**
	 * A ParameterPanel for choosing SOM Clusterer parameters.
	 */
	private class SOMClusterParameterPanel extends ClusterParameterPanel {
		private JSpinner xDimensionSpinner;
		private JSpinner yDimensionSpinner;
		private JComboBox gridTypeComboBox;
		private JSpinner iterationSpinner;
		private JSpinner learningRateSpinner;
		private JSpinner initialRadiusSpinner;
		private JComboBox learningTypeComboBox;
		private JComboBox neighborhoodFunctionComboBox;

		/**
		 * Creates a new SOMClusterParameterPanel
		 */
		public SOMClusterParameterPanel( ) {
			super( new GridLayout( 8, 2, hgap, vgap ));
			Language language = Settings.getLanguage( );
			Settings settings = Settings.getSettings( );

			xDimensionSpinner    = new JSpinner( new SpinnerNumberModel( 
				settings.getInt( "history.clustering.som.xDimension", 5 ), 0, 25, 1 ));
			yDimensionSpinner    = new JSpinner( new SpinnerNumberModel( 
				settings.getInt( "history.clustering.som.yDimension", 5 ), 0, 25, 1 ));
			iterationSpinner     = new JSpinner( new SpinnerNumberModel( 
				settings.getInt( "history.clustering.som.iterations", 1000 ), 1, 100000, 1000 ));
			learningRateSpinner  = new JSpinner( new SpinnerNumberModel( 
				settings.getDouble( "history.clustering.som.learningRate", 0.1 ), 0, 1, 0.1 ));
			initialRadiusSpinner = new JSpinner( new SpinnerNumberModel( 
				settings.getInt( "history.clustering.som.initialRadius", 10 ), 0, 25, 1 ));
			this.gridTypeComboBox = new JComboBox( new Object[] {
				SOM.GridType.HEXAGONAL,
				SOM.GridType.RECTANGLES
			});
			this.gridTypeComboBox.setSelectedIndex( 
				settings.getInt( "history.clustering.som.gridType", 0 ));
			this.learningTypeComboBox = new JComboBox( new Object[] {
				SOM.LearningType.EXPONENTIAL,
				SOM.LearningType.INVERSE,
				SOM.LearningType.LINEAR
			});
			this.learningTypeComboBox.setSelectedIndex( 
				settings.getInt( "history.clustering.som.learningType", 0 ));
			this.neighborhoodFunctionComboBox = new JComboBox( new Object[] {
				SOM.NeighbourhoodFunction.GAUSSIAN,
				SOM.NeighbourhoodFunction.STEP
			});
			this.neighborhoodFunctionComboBox.setSelectedIndex( 
				settings.getInt( "history.clustering.som.neighborhoodFunction", 0 ));
			this.add( new JLabel( language.get( "X axis dimensions" )));
			this.add( this.xDimensionSpinner );
			this.add( new JLabel( language.get( "Y axis dimensions" )));
			this.add( this.yDimensionSpinner );
			this.add( new JLabel( language.get( "Grid type" )));
			this.add( this.gridTypeComboBox );
			this.add( new JLabel( language.get( "Number of iterations" )));
			this.add( this.iterationSpinner );
			this.add( new JLabel( language.get( "Learning rate" )));
			this.add( this.learningRateSpinner );
			this.add( new JLabel( language.get( "Initial Radius" )));
			this.add( this.initialRadiusSpinner );
			this.add( new JLabel( language.get( "Learning type" )));
			this.add( this.learningTypeComboBox );
			this.add( new JLabel( language.get( "Neighborhood function" )));
			this.add( this.neighborhoodFunctionComboBox );
		}

		/**
		 * Returns a new SOM Clusterer with the parameters selected by the user.
		 * 
		 * @return The new SOM Clusterer.
		 */
		public Clusterer getClusterer( ) {
			Settings settings = Settings.getSettings( );
			settings.set( "history.clustering.som.xDimension", 
				this.xDimensionSpinner.getValue( ).toString( ));
			settings.set( "history.clustering.som.yDimension",
				this.yDimensionSpinner.getValue( ).toString( ));
			settings.setInt( "history.clustering.som.gridType",
				this.gridTypeComboBox.getSelectedIndex( ));
			settings.set( "history.clustering.som.iterations",
				this.iterationSpinner.getValue( ).toString( ));
			settings.set( "history.clustering.som.learningRate",
				this.learningRateSpinner.getValue( ).toString( ));
			settings.set( "history.clustering.som.initialRadius",
				this.initialRadiusSpinner.getValue( ).toString( ));
			settings.setInt( "history.clustering.som.learningType",
				this.learningTypeComboBox.getSelectedIndex( ));
			settings.setInt( "history.clustering.som.neighborhoodFunction",
				this.neighborhoodFunctionComboBox.getSelectedIndex( ));
			return new SOM(
				// number of dimensions on the x axis
				Integer.parseInt( this.xDimensionSpinner.getValue( ).toString( )),
				// number of dimensions on the y axis
				Integer.parseInt( this.yDimensionSpinner.getValue( ).toString( )),
				// type of grid.
				(SOM.GridType)this.gridTypeComboBox.getSelectedItem( ),
				// number of iterations
				Integer.parseInt( this.iterationSpinner.getValue( ).toString( )),
				// learning rate of algorithm
				Double.parseDouble( this.learningRateSpinner.getValue( ).toString( )),
				// initial radius
				Integer.parseInt( this.initialRadiusSpinner.getValue( ).toString( )),
				// type of learning to use
				(SOM.LearningType)this.learningTypeComboBox.getSelectedItem( ),
				// neighborhood function.
				(SOM.NeighbourhoodFunction)
					this.neighborhoodFunctionComboBox.getSelectedItem( )
			);
		}

		/**
		 * Gets the name of this clusterer.
		 * 
		 * @return The String "Self Organizing Map"
		 */
		public String toString( ) {
			return "Self Organizing Map";
		}
	}

	// There is a problem with the KMeans Clusterer in java-ml which occasionaly
	// causes it to hang. I am removing this clusterer and replacing with Weka's
	// SimpleKMeans clusterer until this can be resolved.
	// ===================== KMeansClusterParameterPanel ========================
	/**
	 * A ParameterPanel for selecting KMeans parameters
	 */
//	private class KMeansClusterParameterPanel extends ClusterParameterPanel {
//		private JSpinner clustersSpinner;
//		private JSpinner iterationsSpinner;
//
//		public KMeansClusterParameterPanel( ) {
//			super( new GridLayout( 2, 2, hgap, vgap ));
//			Language language = Settings.getLanguage( );
//			Settings settings = Settings.getSettings( );
//			clustersSpinner    = new JSpinner( new SpinnerNumberModel( 
//				settings.getInt( "history.clustering.kmeans.clusters", 5 ), 0, 500, 1 ));
//			iterationsSpinner    = new JSpinner( new SpinnerNumberModel( 
//				settings.getInt( "history.clustering.kmeans.iterations", 100 ), 1, 10000, 50 ));
//			this.add( new JLabel( language.get( "Maximum Number of Clusters" )));
//			this.add( this.clustersSpinner );
//			this.add( new JLabel( language.get( "Number of Iterations" )));
//				this.add( this.iterationsSpinner );
//		}
//
//		/**
//		 * Returns a new KMeans Clusterer with the parameters selected by the user.
//		 * 
//		 * @return The new KMeans Clusterer.
//		 */
//		public Clusterer getClusterer( ) {
//			Settings settings = Settings.getSettings( );
//			settings.set( "history.clustering.kmeans.clusters", 
//				this.clustersSpinner.getValue( ).toString( ));
//			settings.set( "history.clustering.kmeans.iterations", 
//				this.iterationsSpinner.getValue( ).toString( ));
//			return new KMeans(
//				// number of clusters
//				Integer.parseInt( this.clustersSpinner.getValue( ).toString( )),
//				// number of iterations
//				Integer.parseInt( this.iterationsSpinner.getValue( ).toString( ))
//			);
//		}
//
//		/**
//		 * Gets the name of this clusterer.
//		 * 
//		 * @return The String "K-Means"
//		 */
//		public String toString( ) {
//			return "K-Means";
//		}
//	}

	// ===================== SimpleKMeansClusterParameterPanel ========================
	/**
	 * A ParameterPanel for selecting KMeans parameters
	 */
	private class SimpleKMeansClusterParameterPanel extends ClusterParameterPanel {
		private JSpinner clustersSpinner;
		private JSpinner iterationsSpinner;

		public SimpleKMeansClusterParameterPanel( ) {
			super( new GridLayout( 2, 2, hgap, vgap ));
			Language language = Settings.getLanguage( );
			Settings settings = Settings.getSettings( );
			clustersSpinner    = new JSpinner( new SpinnerNumberModel( 
				settings.getInt( "history.clustering.simpleKmeans.clusters", 5 ), 0, 500, 1 ));
			iterationsSpinner    = new JSpinner( new SpinnerNumberModel( 
				settings.getInt( "history.clustering.simpleKmeans.iterations", 500 ), 1, 10000, 50 ));
			this.add( new JLabel( language.get( "Maximum Number of Clusters" )));
			this.add( this.clustersSpinner );
			this.add( new JLabel( language.get( "Number of Iterations" )));
				this.add( this.iterationsSpinner );
		}

		/**
		 * Returns a new KMeans Clusterer with the parameters selected by the user.
		 * 
		 * @return The new KMeans Clusterer.
		 */
		public Clusterer getClusterer( ) {
			Settings settings = Settings.getSettings( );
			Logger logger = Logger.getLogger( getClass( ));
			settings.set( "history.clustering.simpleKmeans.clusters", 
				this.clustersSpinner.getValue( ).toString( ));
			settings.set( "history.clustering.simpleKmeans.iterations", 
				this.iterationsSpinner.getValue( ).toString( ));
			SimpleKMeans kmeans = new SimpleKMeans( );
			// set the number of iterations
			StringBuilder optionString = new StringBuilder( );
			String[] options = kmeans.getOptions( );
			boolean iterationOption = false;
			for ( int i=0; i < options.length; i++ ) {
				if ( iterationOption ) {
					options[ i ] = this.iterationsSpinner.getValue( ).toString( );
				}
				iterationOption = "-I".equals( options[ i ] );
				optionString.append( options[ i ] + " " );
			}
			logger.debug( "Simple K-Means options: " + optionString );
			try {
				kmeans.setOptions( options );
				kmeans.setNumClusters( 
					// number of clusters
					Integer.parseInt( this.clustersSpinner.getValue( ).toString( ))
				);
			} catch ( Exception e ) {
				logger.error( 
				"An invalid option was specified. Using the default of " + kmeans.getNumClusters( ) +
				" instead\nThe following error was returned", e );
			}
			return new WekaClusterer( kmeans );
		}

		/**
		 * Gets the name of this clusterer.
		 * 
		 * @return The String "K-Means"
		 */
		public String toString( ) {
			return "Simple K-Means";
		}
	}

	// ======================== MCLClusterParameterPanel =======================
//	private class MCLClusterParameterPanel extends ClusterParameterPanel {
//		private JSpinner maxResidualSpinner;
//		private JSpinner pGammaSpinner;
//		private JSpinner loopGainSpinner;
//		private JSpinner maxZeroSpinner;
//		private DistanceMeasureComboBox distanceMeasure;
//
//		public MCLClusterParameterPanel ( ) {
//			super( new GridLayout( 2, 4 ));
//			Language language = Settings.getLanguage( );
//			this.maxResidualSpinner = new JSpinner( );
//			this.pGammaSpinner = new JSpinner( );
//			this.loopGainSpinner = new JSpinner( );
//			this.maxZeroSpinner = new JSpinner( );
//			this.distanceMeasure = new DistanceMeasureComboBox( );
//			this.add( new JLabel( language.get( "Distance Measure" ));
//			this.add( this.distanceMeasure );
//			this.add( new JLabel( language.get( "Max Residual" )));
//			this.add( this.maxResidualSpinner );
//			this.add( new JLabel( language.get( "P Gamma" )));
//			this.add( this.pGammaSpinner );
//			this.add( new JLabel( language.get( "Loop Gain" )));
//			this.add( this.loopGainSpinner );
//			this.add( new JLabel( language.get( "Max Zero" )));
//			this.add( this.maxZeroSpinner );
//		}
//
//		public Clusterer getClusterer( ) {
//			return new MCL( 
//				this.distanceMeasure.getSelectedDistanceMeasure( ),
//				Double.parseDouble( this.maxResidualSpinner.getValue( ).toString( )),
//				Double.parseDouble( this.pGammaSpinner.getValue( ).toString( )),
//				Double.parseDouble( this.loopGainSpinner.getValue( ).toString( )),
//				Double.parseDouble( this.maxZeroSpinner.getValue( ).toString( )),
//		}
//		public String toString( ) {
//			return "MCL";
//		}
//	}

	/**
	 * A ParameterPanel for selecting parameters for KMedoids clustering.
	 */
	private class KMedoidsClusterParameterPanel extends ClusterParameterPanel {
		private DistanceMeasureComboBox distanceMeasure;
		private JSpinner clustersSpinner;
		private JSpinner iterationsSpinner;

		public KMedoidsClusterParameterPanel( ) {
			super( new GridLayout( 3, 2, hgap, vgap ));
			Language language = Settings.getLanguage( );
			Settings settings = Settings.getSettings( );
			this.distanceMeasure = new DistanceMeasureComboBox( );
			clustersSpinner    = new JSpinner( new SpinnerNumberModel( 
				settings.getInt( "history.clustering.kmedoids.clusters", 5 ), 0, 25, 1 ));
			iterationsSpinner    = new JSpinner( new SpinnerNumberModel( 
				settings.getInt( "history.clustering.kmedoids.iterations", 100 ), 0, 10000, 50 ));
			this.add( new JLabel( language.get( "Distance Measure" )));
			this.add( this.distanceMeasure );
			this.add( new JLabel( language.get( "Maximum Number of Clusters" )));
			this.add( this.clustersSpinner );
			this.add( new JLabel( language.get( "Number of Iterations" )));
				this.add( this.iterationsSpinner );
		}

		/**
		 * Returns a new KMedoids Clusterer with the parameters selected by the user.
		 * 
		 * @return The new KMedoids Clusterer.
		 */
		public Clusterer getClusterer( ) {
			Settings settings = Settings.getSettings( );
			settings.set( "history.clustering.kmedoids.clusters", 
				this.clustersSpinner.getValue( ).toString( ));
			settings.set( "history.clustering.kmedoids.iterations", 
				this.iterationsSpinner.getValue( ).toString( ));
			return new KMedoids(
				// number of clusters
				Integer.parseInt( this.clustersSpinner.getValue( ).toString( )),
				// number of iterations
				Integer.parseInt( this.iterationsSpinner.getValue( ).toString( )),
				this.distanceMeasure.getSelectedDistanceMeasure( )
			);
		}

		/**
		 * Gets the name of this clusterer.
		 * 
		 * @return The String "K-Medoids"
		 */
		public String toString( ) {
			return "K-Medoids";
		}
	}

	/**
	 * A ParameterPanel for selecting parameters for FarthestFirst clustering.
	 */
	private class FarthestFirstClusterParameterPanel extends ClusterParameterPanel {
		private DistanceMeasureComboBox distanceMeasure;
		private JSpinner distanceSpinner;

		public FarthestFirstClusterParameterPanel( ) {
			super( new GridLayout( 2, 2, hgap, vgap ));
			Language language = Settings.getLanguage( );
			Settings settings = Settings.getSettings( );
			this.distanceMeasure = new DistanceMeasureComboBox( );
			this.distanceMeasure.setSelectedIndex( 
				settings.getInt( "history.clustering.farthestFirst.distanceMeasure", 0 ));
			this.distanceSpinner = new JSpinner( new SpinnerNumberModel( 
				settings.getInt( "history.clustering.farthestFirst.distance", 5 ), 0, 25, 1 ));
			this.add( new JLabel( language.get( "Distance Measure" )));
			this.add( this.distanceMeasure );
			this.add( new JLabel( language.get( "Maximum Number of Clusters" )));
			this.add( this.distanceSpinner );
		}

		/**
		 * Returns a new FarthestFirst Clusterer with the parameters selected by the user.
		 * 
		 * @return The new FarthestFirst Clusterer.
		 */
		public Clusterer getClusterer( ) {
				Settings settings = Settings.getSettings( );
				settings.setInt( "history.clustering.farthestFirst.distanceMeasure",
					this.distanceMeasure.getSelectedIndex( ));
				settings.set( "history.clustering.farthestFirst.distance", 
					this.distanceSpinner.getValue( ).toString( ));
			return new FarthestFirst(
				// number of clusters
				Integer.parseInt( this.distanceSpinner.getValue( ).toString( )),
				this.distanceMeasure.getSelectedDistanceMeasure( )
			);
		}

		/**
		 * Gets the name of this clusterer.
		 * 
		 * @return The String "Farthest First"
		 */
		public String toString( ) {
			return "Farthest First";
		}
	}
}

