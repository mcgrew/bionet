import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Canvas;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Dialog;
import java.awt.Checkbox;
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

	// layout menu itmes
	private JMenu layoutMenu = new JMenu( "Layout" );
	private ButtonGroup layoutMenuButtonGroup = new ButtonGroup( );
	private JRadioButtonMenuItem multipleCirclesLayoutMenuItem = new JRadioButtonMenuItem( "Multiple Circles" );
	private JRadioButtonMenuItem singleCircleLayoutMenuItem = new JRadioButtonMenuItem( "Single Circle", true );
	private JRadioButtonMenuItem clusteredLayoutMenuItem = new JRadioButtonMenuItem( "Clustered" );
	private JRadioButtonMenuItem springLayoutMenuItem = new JRadioButtonMenuItem( "Spring Embedded" );
	private JRadioButtonMenuItem heatMapLayoutMenuItem = new JRadioButtonMenuItem( "Heat Map" );

	// view menu items
	private JMenu viewMenu = new JMenu( "View" );
	private JMenuItem zoomInViewMenuItem = new JMenuItem( "Zoom In", KeyEvent.VK_I );
	private JMenuItem zoomOutViewMenuItem = new JMenuItem( "Zoom Out", KeyEvent.VK_O );
	private JMenuItem fitToWindowViewMenuItem = new JMenuItem( "Fit to Window", KeyEvent.VK_F );
	
	// color menu items
	private JMenu colorMenu = new JMenu( "Color" );
	private ButtonGroup colorMenuButtonGroup = new ButtonGroup( );
	private JRadioButtonMenuItem normalColorMenuItem = new JRadioButtonMenuItem( "Normal Color", true );
	private JRadioButtonMenuItem highContrastColorMenuItem = new JRadioButtonMenuItem( "High Contrast Color" );

	private JLabel sortLabel = new JLabel( "Sort by ", SwingConstants.RIGHT );
	private JComboBox sortComboBox = new JComboBox( );
//	private JPanel correlationViewPanel = new JPanel( );
	private JButton noneButton = new JButton( "None" );
	private JButton allButton = new JButton( "All" );
	private JPanel moleculeList = new JPanel( );
	private JScrollPane moleculeScrollPane = new JScrollPane( this.moleculeList );
	private ArrayList <MoleculeCheckbox> moleculeCheckboxArrayList = new ArrayList<MoleculeCheckbox>( );
	
	private JLabel minCorrelationLabel = new JLabel( 
		"Higher Than: ", SwingConstants.RIGHT );
	private JLabel maxCorrelationLabel = new JLabel( 
		"Lower Than: ", SwingConstants.RIGHT );
	private JSpinner minCorrelationSpinner = 
		new JSpinner( new SpinnerNumberModel( 0.5, 0.0, 1.0, 0.01 ));
	private JSpinner maxCorrelationSpinner = 
		new JSpinner( new SpinnerNumberModel( 1.0, 0.0, 1.0, 0.01 ));

	protected VisualizationViewer <Molecule,Correlation> viewer;
	protected UndirectedSparseGraph <Molecule,Correlation> graph = new UndirectedSparseGraph <Molecule,Correlation>( );
	protected Layout <Molecule,Correlation> layout; //Graph Layout

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

		// ALL & RESET BUTTONS
		JPanel moleculeButtonPanel = new JPanel( new BorderLayout( ) );
		this.allButton.setPreferredSize( new Dimension( 75, 20 ));
		this.noneButton.setPreferredSize( new Dimension( 75, 20 ));
		this.allButton.addActionListener( new SelectButtonListener( this ));
		this.noneButton.addActionListener( new SelectButtonListener( this ));
		moleculeButtonPanel.add( this.allButton, BorderLayout.WEST );
		moleculeButtonPanel.add( this.noneButton, BorderLayout.EAST );	

		// MOLECULE LIST
		JPanel moleculeFilterPanel = new JPanel( new BorderLayout( ));
		moleculeFilterPanel.add( sortSelectionPanel, BorderLayout.NORTH );
		moleculeFilterPanel.add( this.moleculeScrollPane, BorderLayout.CENTER );
		moleculeFilterPanel.add( moleculeButtonPanel, BorderLayout.SOUTH );
		moleculeFilterPanel.setBorder( 
			BorderFactory.createTitledBorder( 
				BorderFactory.createLineBorder( Color.BLACK, 1 ),
					"Molecule Filter",
					TitledBorder.CENTER,
					TitledBorder.TOP
			)
		);


		// CORRELATION FILTER ELEMENTS
		this.minCorrelationSpinner.setPreferredSize( new Dimension( 80, 25 ));
		this.maxCorrelationSpinner.setPreferredSize( new Dimension( 80, 25 ));

		JPanel minCorrelationFilterPanel = new JPanel( new BorderLayout( ));
		minCorrelationFilterPanel.add( this.minCorrelationSpinner, BorderLayout.EAST );
		minCorrelationFilterPanel.add( this.minCorrelationLabel, BorderLayout.CENTER );

		JPanel maxCorrelationFilterPanel = new JPanel( new BorderLayout( ));
		maxCorrelationFilterPanel.add( this.maxCorrelationSpinner, BorderLayout.EAST );
		maxCorrelationFilterPanel.add( this.maxCorrelationLabel, BorderLayout.CENTER );

		JPanel correlationFilterPanel = new JPanel( new BorderLayout( ));
		correlationFilterPanel.add( minCorrelationFilterPanel, BorderLayout.NORTH );
		correlationFilterPanel.add( maxCorrelationFilterPanel, BorderLayout.SOUTH );
		correlationFilterPanel.setBorder( 
			BorderFactory.createTitledBorder( 
				BorderFactory.createLineBorder( Color.BLACK, 1 ),
				"Correlation Filter",
				TitledBorder.CENTER,
				TitledBorder.TOP
			)
		);

		JPanel leftPanel = new JPanel( new BorderLayout( ));
		leftPanel.add( moleculeFilterPanel, BorderLayout.CENTER );
		leftPanel.add( correlationFilterPanel, BorderLayout.SOUTH );

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
		this.pearsonCalculationMenuItem.addItemListener( new CalculationChangeListener( this ));
		this.spearmanCalculationMenuItem.addItemListener( new CalculationChangeListener( this ));
		this.kendallCalculationMenuItem.addItemListener( new CalculationChangeListener( this ));

		//LAYOUT MENU
		LayoutChangeListener lcl = new LayoutChangeListener( this );
		this.layoutMenu.setMnemonic( KeyEvent.VK_L );
		this.layoutMenu.getAccessibleContext( ).setAccessibleDescription(
			"Change the layout of the graph" );
		this.layoutMenuButtonGroup.add( this.multipleCirclesLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.singleCircleLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.clusteredLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.springLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.heatMapLayoutMenuItem );
		this.layoutMenu.add( this.multipleCirclesLayoutMenuItem );
		this.layoutMenu.add( this.singleCircleLayoutMenuItem );
		this.layoutMenu.add( this.clusteredLayoutMenuItem );
		this.layoutMenu.add( this.springLayoutMenuItem );
		this.layoutMenu.add( this.heatMapLayoutMenuItem );
		this.multipleCirclesLayoutMenuItem.addItemListener( lcl );
		this.singleCircleLayoutMenuItem.addItemListener( lcl );
		this.clusteredLayoutMenuItem.addItemListener( lcl );
		this.springLayoutMenuItem.addItemListener( lcl );
		this.heatMapLayoutMenuItem.addItemListener( lcl );

		//VIEW MENU
		this.viewMenu.setMnemonic( KeyEvent.VK_V );
		this.viewMenu.getAccessibleContext( ).setAccessibleDescription(
			"Change the data view settings" );
		this.viewMenu.add( this.zoomInViewMenuItem );
		this.viewMenu.add( this.zoomOutViewMenuItem );
		this.viewMenu.add( this.fitToWindowViewMenuItem );

		//COLOR MENU
		this.colorMenu.setMnemonic( KeyEvent.VK_R );
		this.colorMenu.getAccessibleContext( ).setAccessibleDescription(
			"Change the color of the graph" );
		this.colorMenuButtonGroup.add( this.normalColorMenuItem );
		this.colorMenuButtonGroup.add( this.highContrastColorMenuItem );
		this.colorMenu.add( this.normalColorMenuItem );
		this.colorMenu.add( this.highContrastColorMenuItem );

		this.menuBar.add( this.calculationMenu );
		this.menuBar.add( this.layoutMenu );
		this.menuBar.add( this.viewMenu );
		this.menuBar.add( this.colorMenu );

		// control configuration
		this.sortComboBox.addItem( "Index" );
		this.sortComboBox.addItem( "Group" );
		this.sortComboBox.addItem( "Name" );

		// Add the panels to the main panel
		this.add( menuBar, BorderLayout.NORTH );
//		this.add( this.correlationViewPanel, BorderLayout.CENTER );
		this.add( leftPanel, BorderLayout.WEST );
			
	}

	public void createGraph( ) {
			if ( this.data != null )
				this.createGraph( this.data );
	}
	public void createGraph( DataHandler data ) {
			this.data = data;
			this.setVisible( true );
			
			this.experiment = experimentSelection( data.getExperiments( ) );
			if ( this.experiment == null ) {
				return;
			}

			this.addVertices( );
			this.addEdges( );
			this.filterEdges( );

			this.setGraphLayout( new CircleLayout<Molecule,Correlation>( this.graph ));

			// add event listeners to the spinners to watch for changes.
			EdgeFilterChangeListener efcl = new EdgeFilterChangeListener( this );
			minCorrelationSpinner.addChangeListener( efcl ); 
			maxCorrelationSpinner.addChangeListener( efcl );
	}

	protected int addVertices( ) {
		int returnValue = 0;
		MoleculeCheckbox cb;
		VertexFilterChangeListener vfcl = new VertexFilterChangeListener( this );
		this.moleculeList.setLayout( new GridLayout( this.experiment.getMolecules( ).size( ), 1 ));
		for( String groupName : this.experiment.getMoleculeGroupNames( )) {
			for( Molecule molecule : this.experiment.getMoleculeGroup( groupName ).getMolecules( )) {
				this.graph.addVertex( molecule );
				cb = new MoleculeCheckbox( molecule, true );
				cb.addItemListener( vfcl );
				this.moleculeList.add( cb );
				this.moleculeCheckboxArrayList.add( cb );
				returnValue++;
			}
		}
		return returnValue;
	}

	protected int filterVertices( ) {
		int returnValue = 0;
		for( MoleculeCheckbox mcb : this.moleculeCheckboxArrayList ) {
			if ( mcb.getState( )) {
				returnValue++;
				if ( !this.graph.containsVertex( mcb.getMolecule( ))) {
					this.graph.removeVertex( mcb.getMolecule( ));
				}
			}
			else {
				if ( this.graph.containsVertex( mcb.getMolecule( ))) {
					this.graph.addVertex( mcb.getMolecule( ));
				}
			}
		}
		this.viewer.repaint( );
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

	protected boolean isValidEdge( Correlation correlation ) {
		Molecule [] molecules = correlation.getMolecules( );
		return ( this.graph.containsVertex( molecules[ 0 ] ) &&
		         this.graph.containsVertex( molecules[ 1 ] ) &&
						 correlation.getValue( ) > ((Double) minCorrelationSpinner.getValue( )).doubleValue( ) &&
             correlation.getValue( ) < ((Double) maxCorrelationSpinner.getValue( )).doubleValue( ));
	}

	protected int filterEdges( ) {
		int returnValue = 0;
		for( Correlation correlation : this.experiment.getCorrelations( )) {
			if ( this.isValidEdge( correlation )) {
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
	public void resetGraphLayout( ) {
		this.layout.reset( );
	}

	public void setGraphLayout( Layout<Molecule,Correlation> layout ) {
		this.layout = layout;
		if ( this.viewer != null )
			this.remove( this.viewer );
		this.viewer = new VisualizationViewer <Molecule,Correlation>( layout );
		// add labels to the graph
		this.viewer.getRenderContext( ).setVertexLabelTransformer( new ToStringLabeller<Molecule>( ));
//		this.viewer.getRenderContext( ).setEdgeLabelTransformer( new ToStringLabeller<Correlation>( ));
		this.viewer.getRenderer( ).getVertexLabelRenderer( ).setPosition( Position.CNTR );
		this.add( this.viewer, BorderLayout.CENTER );
		DefaultModalGraphMouse mouse = new DefaultModalGraphMouse();
		mouse.setMode( ModalGraphMouse.Mode.PICKING );
		this.viewer.setGraphMouse( mouse );
		this.viewer.repaint( );
	}
	
	public Experiment experimentSelection( ArrayList <Experiment> experiments ) {
		if ( experiments.size( ) < 1 ) {
			System.err.println( "These files do not appear to contain any data!" );
			return null;
		}
		if ( experiments.size( ) == 1 ) {
			return experiments.get( 0 );
		}
		// bring up a dialog to choose the experiment
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
		return null;

	}

	// ******************* PROTECTED CLASSES **************************
	protected class EdgeFilterChangeListener implements ChangeListener {
		private CorrelationDisplayPanel cdp;

		public EdgeFilterChangeListener( CorrelationDisplayPanel c ) {
			this.cdp = c;
		}

		public void stateChanged( ChangeEvent e ) {
			this.cdp.filterEdges( );
			this.cdp.viewer.repaint( );
		}
	}

	protected class VertexFilterChangeListener implements ItemListener {
		private CorrelationDisplayPanel displayPanel;

		public VertexFilterChangeListener( CorrelationDisplayPanel c ) {
			this.displayPanel = c;
		}

		public void itemStateChanged( ItemEvent event ) {
				Molecule molecule = (( MoleculeCheckbox )event.getSource( )).getMolecule( );
			if ( event.getStateChange( ) == ItemEvent.SELECTED ) {
				displayPanel.graph.addVertex( molecule );
				for( Correlation correlation : molecule.getCorrelations( )) {
					if ( displayPanel.isValidEdge( correlation ))
						displayPanel.graph.addEdge( correlation,
						                            new Pair( correlation.getMolecules( )),
																				EdgeType.UNDIRECTED );
				}
			}
			else {
				displayPanel.graph.removeVertex( molecule );
			}
			displayPanel.viewer.repaint( );
		}
	}

	protected class MoleculeCheckbox extends Checkbox {
		private Molecule molecule;

		public MoleculeCheckbox( Molecule molecule, boolean state ) {
			super( molecule.getAttribute( "id" ), state );
			this.molecule = molecule;
		}

		public void setMolecule( Molecule molecule ) {
			this.setLabel( molecule.getAttribute( "id" ));
			this.molecule = molecule;
		}

		public Molecule getMolecule( ) {
			return this.molecule;
		}
	}

	protected class SelectButtonListener implements ActionListener {
		private CorrelationDisplayPanel displayPanel;

		public SelectButtonListener( CorrelationDisplayPanel c ) {
			this.displayPanel = c;
		}

		public void actionPerformed( ActionEvent e ) {
			JButton source = ( JButton )e.getSource( );
			if ( source == this.displayPanel.allButton )
				this.setAll( true );
			else if ( source == this.displayPanel.noneButton )
				this.setAll( false );
		}

		private void setAll( boolean state ) {
			for ( MoleculeCheckbox m : this.displayPanel.moleculeCheckboxArrayList ) {
				m.setState( state );
				for ( ItemListener i : m.getItemListeners( )) {
					i.itemStateChanged( new ItemEvent(
						m,
						-1,
						m.getMolecule( ),
						state ? ItemEvent.SELECTED : ItemEvent.DESELECTED ));
				}
			}
		}

	}

	protected class CalculationChangeListener implements ItemListener {
		private CorrelationDisplayPanel displayPanel;

		public CalculationChangeListener( CorrelationDisplayPanel c ) {
			this.displayPanel = c;
		}

		public void itemStateChanged( ItemEvent event ) {
			JRadioButtonMenuItem item = (( JRadioButtonMenuItem )event.getSource( ));
			if ( event.getStateChange( ) == ItemEvent.SELECTED ) {
				if ( item == this.displayPanel.pearsonCalculationMenuItem )
					Correlation.setDefaultMethod( Correlation.PEARSON );
				else if ( item == this.displayPanel.spearmanCalculationMenuItem )
					Correlation.setDefaultMethod( Correlation.SPEARMAN );
				else if ( item == this.displayPanel.kendallCalculationMenuItem )
					Correlation.setDefaultMethod( Correlation.KENDALL );
				this.displayPanel.resetGraphLayout( );
				this.displayPanel.viewer.repaint( );
			}
		}
	}

	protected class LayoutChangeListener implements ItemListener {
			private CorrelationDisplayPanel cdp;

			public LayoutChangeListener( CorrelationDisplayPanel c ) {
				this.cdp = c;
			}

			public void itemStateChanged( ItemEvent event ) {
				JRadioButtonMenuItem item = ( JRadioButtonMenuItem )event.getSource( );
				if ( event.getStateChange( ) == ItemEvent.SELECTED ) {
					if ( item == this.cdp.singleCircleLayoutMenuItem )
						this.cdp.setGraphLayout( new CircleLayout<Molecule,Correlation>( this.cdp.graph ));
					else if ( item == this.cdp.clusteredLayoutMenuItem )
						this.cdp.setGraphLayout( new ClusteredLayout<Molecule,Correlation>( this.cdp.graph ));
				}
			}
	}
}


