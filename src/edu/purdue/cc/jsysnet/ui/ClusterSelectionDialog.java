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

import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.SOM;
import net.sf.javaml.clustering.KMedoids;
import net.sf.javaml.clustering.FarthestFirst;
import net.sf.javaml.clustering.mcl.MCL;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;
import net.sf.javaml.distance.SpearmanRankCorrelation;
import net.sf.javaml.distance.LinearKernel;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.distance.DistanceMeasure;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.SOM;


public class ClusterSelectionDialog  extends JDialog implements ActionListener {
	protected JButton clusterButton;
	protected JButton cancelButton;
	protected ClusterParameterPanel parameterPanel;
	protected JComboBox clusterSelectorComboBox;
	private Clusterer returnValue;
	private static int hgap = 10;
	private static int vgap = 10;
	private boolean okPressed = false;

	public ClusterSelectionDialog( Frame owner, String title ) {
		this( owner, title, true );
	}

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
		this.parameterPanel = new SOMClusterParameterPanel( );
		this.clusterSelectorComboBox = new JComboBox( new Object[] {
			this.parameterPanel,
			new KMeansClusterParameterPanel( ),
			new KMedoidsClusterParameterPanel( ),
			new FarthestFirstClusterParameterPanel( )
			});
		this.clusterSelectorComboBox.addActionListener( this );

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

	public Clusterer getReturnValue( ) {
		if ( this.okPressed ) {
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

	public void actionPerformed( ActionEvent event ) {
		Component source = (Component)event.getSource( );
		if ( source == this.clusterSelectorComboBox ) {
			ClusterParameterPanel selected = 
				(ClusterParameterPanel)((JComboBox)source).getSelectedItem( );
			this.remove( this.parameterPanel );
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

	private class DistanceMeasureComboBox extends JComboBox {

		public DistanceMeasureComboBox( ) {
			super( new Object [] {
				"Pearson Correlation",
				"Spearman Rank Correlation",
				"Euclidian Distance",
				"Linear Kernel"
			});
		}

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
	private abstract class ClusterParameterPanel extends JPanel {
		protected Clusterer clusterer;

		protected ClusterParameterPanel( ) {
			super( );
		}

		protected ClusterParameterPanel( LayoutManager layout ) {
			super( layout );
		}
		
		public abstract Clusterer getClusterer( );
	}

	// ======================== SOMClusterParameterPanel ========================
	private class SOMClusterParameterPanel extends ClusterParameterPanel {
		private JSpinner xDimensionSpinner;
		private JSpinner yDimensionSpinner;
		private JComboBox gridTypeComboBox;
		private JSpinner iterationSpinner;
		private JSpinner learningRateSpinner;
		private JSpinner initialRadiusSpinner;
		private JComboBox learningTypeComboBox;
		private JComboBox neighborhoodFunctionComboBox;

		public SOMClusterParameterPanel( ) {
			super( new GridLayout( 8, 2, hgap, vgap ));
			Language language = Settings.getLanguage( );
			xDimensionSpinner    = new JSpinner( 
				new SpinnerNumberModel( 5, 0, 25, 1 ));
			yDimensionSpinner    = new JSpinner( 
				new SpinnerNumberModel( 5, 0, 25, 1 ));
			iterationSpinner     = new JSpinner( 
				new SpinnerNumberModel( 10000, 0, 100000, 5000 ));
			learningRateSpinner  = new JSpinner( 
				new SpinnerNumberModel( 0.1, 0, 1, 0.1 ));
			initialRadiusSpinner = new JSpinner( 
				new SpinnerNumberModel( 10, 0, 25, 1 ));
			this.gridTypeComboBox = new JComboBox( new Object[] {
				SOM.GridType.HEXAGONAL,
				SOM.GridType.RECTANGLES
			});
			this.learningTypeComboBox = new JComboBox( new Object[] {
				SOM.LearningType.EXPONENTIAL,
				SOM.LearningType.INVERSE,
				SOM.LearningType.LINEAR
			});
			this.neighborhoodFunctionComboBox = new JComboBox( new Object[] {
				SOM.NeighbourhoodFunction.GAUSSIAN,
				SOM.NeighbourhoodFunction.STEP
			});
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

		public Clusterer getClusterer( ) {
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

		public String toString( ) {
			return "Self Organizing Map";
		}
	}

	// ===================== KMeansClusterParameterPanel ========================
	private class KMeansClusterParameterPanel extends ClusterParameterPanel {
		private JSpinner clustersSpinner;
		private JSpinner iterationsSpinner;

		public KMeansClusterParameterPanel( ) {
			super( new GridLayout( 2, 2, hgap, vgap ));
			Language language = Settings.getLanguage( );
			clustersSpinner    = new JSpinner( 
				new SpinnerNumberModel( 5, 0, 25, 1 ));
			iterationsSpinner    = new JSpinner( 
				new SpinnerNumberModel( 5, 0, 25, 1 ));
			this.add( new JLabel( language.get( "Maximum Number of Clusters" )));
			this.add( this.clustersSpinner );
			this.add( new JLabel( language.get( "Number of Iterations" )));
				this.add( this.iterationsSpinner );
		}

		public Clusterer getClusterer( ) {
			return new KMeans(
				// number of clusters
				Integer.parseInt( this.clustersSpinner.getValue( ).toString( )),
				// number of iterations
				Integer.parseInt( this.iterationsSpinner.getValue( ).toString( ))
			);
		}

		public String toString( ) {
			return "K-Means";
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

	private class KMedoidsClusterParameterPanel extends ClusterParameterPanel {
		private DistanceMeasureComboBox distanceMeasure;
		private JSpinner clustersSpinner;
		private JSpinner iterationsSpinner;

		public KMedoidsClusterParameterPanel( ) {
			super( new GridLayout( 3, 2, hgap, vgap ));
			Language language = Settings.getLanguage( );
			this.distanceMeasure = new DistanceMeasureComboBox( );
			clustersSpinner    = new JSpinner( 
				new SpinnerNumberModel( 5, 0, 25, 1 ));
			iterationsSpinner    = new JSpinner( 
				new SpinnerNumberModel( 5, 0, 25, 1 ));
			this.add( new JLabel( language.get( "Distance Measure" )));
			this.add( this.distanceMeasure );
			this.add( new JLabel( language.get( "Maximum Number of Clusters" )));
			this.add( this.clustersSpinner );
			this.add( new JLabel( language.get( "Number of Iterations" )));
				this.add( this.iterationsSpinner );
		}

		public Clusterer getClusterer( ) {
			return new KMedoids(
				// number of clusters
				Integer.parseInt( this.clustersSpinner.getValue( ).toString( )),
				// number of iterations
				Integer.parseInt( this.iterationsSpinner.getValue( ).toString( )),
				this.distanceMeasure.getSelectedDistanceMeasure( )
			);
		}

		public String toString( ) {
			return "K-Medoids";
		}
	}

	private class FarthestFirstClusterParameterPanel extends ClusterParameterPanel {
		private DistanceMeasureComboBox distanceMeasure;
		private JSpinner clustersSpinner;

		public FarthestFirstClusterParameterPanel( ) {
			super( new GridLayout( 2, 2, hgap, vgap ));
			Language language = Settings.getLanguage( );
			this.distanceMeasure = new DistanceMeasureComboBox( );
			clustersSpinner    = new JSpinner( 
				new SpinnerNumberModel( 5, 0, 25, 1 ));
			this.add( new JLabel( language.get( "Distance Measure" )));
			this.add( this.distanceMeasure );
			this.add( new JLabel( language.get( "Maximum Number of Clusters" )));
			this.add( this.clustersSpinner );
		}

		public Clusterer getClusterer( ) {
			return new FarthestFirst(
				// number of clusters
				Integer.parseInt( this.clustersSpinner.getValue( ).toString( )),
				this.distanceMeasure.getSelectedDistanceMeasure( )
			);
		}

		public String toString( ) {
			return "Farthest First";
		}
	}
}

