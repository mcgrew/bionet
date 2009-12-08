import javax.swing.*;
import java.awt.Canvas;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Dialog;
import java.awt.event.KeyEvent;
import java.util.ListIterator;
import java.util.ArrayList;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.*;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;



public class CorrelationDisplayPanel extends JPanel {

	private JMenuBar menuBar = new JMenuBar( );

	// calculation menu items
	private JMenu calculationMenu = new JMenu( "Calculation" );
	private ButtonGroup calculationMenuButtonGroup = new ButtonGroup( );
	private JRadioButtonMenuItem pearsonCalculationMenuItem = new JRadioButtonMenuItem( "Pearson", true );
	private JRadioButtonMenuItem spearmanCalculationMenuItem = new JRadioButtonMenuItem( "Spearman" );
	private JRadioButtonMenuItem kendallCalculationMenuItem = new JRadioButtonMenuItem( "Kendall" );

	// view menu items
	private JMenu viewMenu = new JMenu( "View" );
	private JMenuItem zoomInViewMenuItem = new JMenuItem( "Zoom In", KeyEvent.VK_I );
	private JMenuItem zoomOutViewMenuItem = new JMenuItem( "Zoom Out", KeyEvent.VK_O );
	private JMenuItem fitToWindowViewMenuItem = new JMenuItem( "Fit to Window", KeyEvent.VK_F );
	
	private JLabel sortLabel = new JLabel( "Sort list by " );
	private JComboBox sortComboBox = new JComboBox( );
	private JPanel correlationDisplayPanel = new JPanel( );
	private JButton resetButton = new JButton( "Reset" );
	private JButton allButton = new JButton( "All" );
	private JScrollPane moleculeList = new JScrollPane( );

	private JComboBox colorComboBox = new JComboBox( );
	private JComboBox mapComboBox = new JComboBox( );
	
	private JLabel minCorrelationLabel = new JLabel( 
		"Correlation Coefficient Higher Than: ", SwingConstants.RIGHT );
	private JLabel maxCorrelationLabel = new JLabel( 
		"Correlation Coefficient Lower Than: ", SwingConstants.RIGHT );
	private JSpinner minCorrelationSpinner = 
		new JSpinner( new SpinnerNumberModel( 0.5, 0.0, 1.0, 0.01 ));
	private JSpinner maxCorrelationSpinner = 
		new JSpinner( new SpinnerNumberModel( 1.0, 0.0, 1.0, 0.01 ));

	protected UndirectedSparseGraph <Molecule,Correlation> graph = new UndirectedSparseGraph <Molecule,Correlation>( );

	protected DataHandler data = null;
	protected Experiment experiment = null;
	
	public CorrelationDisplayPanel ( ) {
		super( new BorderLayout( ) );
		this.buildPanel( );
	}
	public CorrelationDisplayPanel ( DataHandler data ) {
		super( new BorderLayout( ) );
		this.buildPanel( );
		this.createGraph( data );
	}

	/**
	 * Adds all of the necessary Components to this Component.
	 */
	private void buildPanel ( ) {

		JPanel sortSelectionPanel = new JPanel( new BorderLayout( ));
		sortSelectionPanel.add( this.sortLabel, BorderLayout.CENTER );
		sortSelectionPanel.add( this.sortComboBox, BorderLayout.EAST );

		JPanel leftPanel = new JPanel( new BorderLayout( ));
		leftPanel.add( sortSelectionPanel, BorderLayout.NORTH );
		leftPanel.add( this.moleculeList, BorderLayout.CENTER );

		// ALL & RESET BUTTONS
		JPanel moleculeButtonPanel = new JPanel( new BorderLayout( ) );
		this.allButton.setPreferredSize( new Dimension( 75, 40 ));
		this.resetButton.setPreferredSize( new Dimension( 75, 40 ));
		moleculeButtonPanel.add( this.allButton, BorderLayout.WEST );
		moleculeButtonPanel.add( this.resetButton, BorderLayout.EAST );	

		// CORRELATION CUTOFF VALUE ELEMENTS
		JPanel correlationValuePanel = new JPanel( new BorderLayout( ));
		correlationValuePanel.add( this.minCorrelationSpinner, BorderLayout.NORTH );
		correlationValuePanel.add( this.maxCorrelationSpinner, BorderLayout.SOUTH );
		this.minCorrelationSpinner.setPreferredSize( new Dimension( 70, 25 ));
		this.maxCorrelationSpinner.setPreferredSize( new Dimension( 70, 25 ));

		// CORRELATION LABELS
		JPanel correlationLabelPanel = new JPanel( new BorderLayout( ));
		correlationLabelPanel.add( this.minCorrelationLabel, BorderLayout.NORTH );
		correlationLabelPanel.add( this.maxCorrelationLabel, BorderLayout.SOUTH );
		
		JPanel bottomLeftPanel = new JPanel( new BorderLayout( ));
		bottomLeftPanel.add( moleculeButtonPanel, BorderLayout.WEST );
		bottomLeftPanel.add( correlationValuePanel, BorderLayout.EAST );

		JPanel bottomRightPanel = new JPanel( new BorderLayout( ));
		bottomRightPanel.add( this.colorComboBox, BorderLayout.NORTH );
		bottomRightPanel.add( this.mapComboBox, BorderLayout.SOUTH );

		JPanel bottomPanel = new JPanel( new BorderLayout( ));
		bottomPanel.add( bottomLeftPanel, BorderLayout.CENTER );
		bottomLeftPanel.add( correlationLabelPanel, BorderLayout.CENTER );
		bottomPanel.add( bottomRightPanel, BorderLayout.EAST ); 
	
		//CALCULATION MENU
		this.calculationMenu.setMnemonic( KeyEvent.VK_C );
		this.calculationMenu.getAccessibleContext( ).setAccessibleDescription(
			"Perform Data Calculations" );
		this.calculationMenuButtonGroup.add( this.pearsonCalculationMenuItem );
		this.calculationMenuButtonGroup.add( this.spearmanCalculationMenuItem );
		this.calculationMenuButtonGroup.add( this.kendallCalculationMenuItem );
		this.pearsonCalculationMenuItem.setMnemonic( KeyEvent.VK_P );
		this.spearmanCalculationMenuItem.setMnemonic( KeyEvent.VK_S );
		this.kendallCalculationMenuItem.setMnemonic( KeyEvent.VK_K );
		this.calculationMenu.add( this.pearsonCalculationMenuItem );
		this.calculationMenu.add( this.spearmanCalculationMenuItem );
		this.calculationMenu.add( this.kendallCalculationMenuItem );

		//VIEW MENU
		this.viewMenu.setMnemonic( KeyEvent.VK_V );
		this.viewMenu.getAccessibleContext( ).setAccessibleDescription(
			"Change the data view settings" );
		this.viewMenu.add( this.zoomInViewMenuItem );
		this.viewMenu.add( this.zoomOutViewMenuItem );
		this.viewMenu.add( this.fitToWindowViewMenuItem );

		this.menuBar.add( this.calculationMenu );
		this.menuBar.add( this.viewMenu );

		// Add the panels to the main panel
		this.add( menuBar, BorderLayout.NORTH );
//		this.add( this.correlationDisplayPanel, BorderLayout.CENTER );
		this.add( leftPanel, BorderLayout.WEST );
		this.add( bottomPanel, BorderLayout.SOUTH );
			
		// control configuration
		this.sortComboBox.addItem( "Index" );
		this.sortComboBox.addItem( "Group" );
		this.sortComboBox.addItem( "Name" );
		this.colorComboBox.addItem( "Normal Color" );
		this.colorComboBox.addItem( "High Contrast Color" );
		this.mapComboBox.addItem( "Multiple Circles" );
		this.mapComboBox.addItem( "Single Circle" );
		this.mapComboBox.addItem( "Heat Map" );
	}

	public void createGraph( ) {
			if ( this.data != null )
				this.createGraph( this.data );
	}
	public void createGraph( DataHandler data ) {
			this.data = data;
			this.setVisible( true );
			
			ArrayList <Experiment> experiments = data.getExperiments( );
			if ( experiments.size( ) < 1 ) {
				System.err.println( "This file doesn't appear to contain any data!" );
				return;
			}
			if ( experiments.size( ) == 1 ) {
				this.experiment = experiments.get( 0 );
			}
			else {
				// bring up a dialog to choose the experiment
				this.experiment = this.experimentSelectionDialog( experiments );
			}

			this.addVertices( );
			this.addEdges( );
			Layout <Molecule,Correlation> layout = new ClusterLayout<Molecule,Correlation>( this.graph );
			this.filterEdges( ((Double) minCorrelationSpinner.getValue( )).doubleValue( ),
			               ((Double) maxCorrelationSpinner.getValue( )).doubleValue( ));

			VisualizationViewer <Molecule,Correlation> viewer = 
				new VisualizationViewer <Molecule,Correlation>( layout );
			// add labels to the graph
			viewer.getRenderContext( ).setVertexLabelTransformer( new ToStringLabeller<Molecule>( ));
//			viewer.getRenderContext( ).setEdgeLabelTransformer( new ToStringLabeller<Correlation>( ));
			viewer.getRenderer( ).getVertexLabelRenderer( ).setPosition( Position.CNTR );

			DefaultModalGraphMouse mouse = new DefaultModalGraphMouse();
			mouse.setMode( ModalGraphMouse.Mode.PICKING );
			viewer.setGraphMouse( mouse );
			this.add( viewer, BorderLayout.CENTER );
	}

	protected int addVertices( ) {
		int returnValue = 0;
		for( String groupName : this.experiment.getMoleculeGroupNames( )) {
			for( Molecule molecule : this.experiment.getMoleculeGroup( groupName ).getMolecules( )) {
				this.graph.addVertex( molecule );
				returnValue++;
			}
		}
		return returnValue;
	}

	protected int addEdges( ) {
		int returnValue = 0;
		for( Correlation correlation : this.experiment.getCorrelations( )) {
			this.graph.addEdge( correlation, 
				             new Pair <Molecule> ( correlation.getMolecules( )),
				             EdgeType.UNDIRECTED );
			returnValue++;
		}
		return returnValue;
	}

	protected int filterEdges( double minValue, double maxValue ) {
		int returnValue = 0;
		for( Correlation correlation : this.experiment.getCorrelations( )) {
			if ( correlation.getValue( Correlation.PEARSON ) > minValue &&  
				   correlation.getValue( Correlation.PEARSON ) < maxValue ) {	
				returnValue++;
				// this Correlation belongs on the graph, make sure it is there.
				if ( !this.graph.containsEdge( correlation )) {
					this.graph.addEdge( correlation, 
						             new Pair <Molecule> ( correlation.getMolecules( )),
						             EdgeType.UNDIRECTED );
				}
			}
			else {
				// this Correlation does not belong on the graph, make sure it is not there.
				if ( this.graph.containsEdge( correlation )) {
					this.graph.removeEdge( correlation );
				}
			}
		}
			return returnValue;

	}
	
	protected Experiment experimentSelectionDialog( ArrayList <Experiment> experiments ) {
		String [] options = new String[ experiments.size( )];
		for ( int i=0; i < experiments.size( ); i++ ) {
			options[ i ] = String.format( "%s - %s",
			                 experiments.get( i ).getAttribute( "exp_id" ) ,
			                 experiments.get( i ).getAttribute( "description" )
											);
		}
		Object selectedValue = JOptionPane.showInputDialog(
		                          null,
		                          "Choose An Experiment", "Experiment Selection",
															JOptionPane.INFORMATION_MESSAGE, 
															null,
															options, options[ 0 ]);
		for( int counter = 0, maxCounter = options.length;
			 counter < maxCounter; counter++ ) {
			 if(options[ counter ].equals( selectedValue ))
				return experiments.get( counter );
		}
		return experiments.get( 0 );

	}
}


