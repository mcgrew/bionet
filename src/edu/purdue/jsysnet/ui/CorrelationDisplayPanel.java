package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.ui.layout.*;
import edu.purdue.jsysnet.util.*;
import edu.purdue.jsysnet.io.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Dialog;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.util.ListIterator;
import java.util.ArrayList;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.*;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.graph.Graph;



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
//	private JRadioButtonMenuItem clusteredLayoutMenuItem = new JRadioButtonMenuItem( "Clustered" );
	private JRadioButtonMenuItem randomLayoutMenuItem = new JRadioButtonMenuItem( "Random" );
	private JRadioButtonMenuItem heatMapLayoutMenuItem = new JRadioButtonMenuItem( "Heat Map" );
	private JCheckBoxMenuItem springLayoutMenuItem = new JCheckBoxMenuItem( "Spring Embedding" );

	// view menu items
	private JMenu viewMenu = new JMenu( "View" );
	private JMenuItem zoomInViewMenuItem = new JMenuItem( "Zoom In", KeyEvent.VK_I );
	private JMenuItem zoomOutViewMenuItem = new JMenuItem( "Zoom Out", KeyEvent.VK_O );
	private JMenuItem fitToWindowViewMenuItem = new JMenuItem( "Fit to Window", KeyEvent.VK_F );
	private JMenuItem selectAllViewMenuItem = new JMenuItem( "Select All", KeyEvent.VK_A );
	private JMenuItem clearSelectionViewMenuItem = new JMenuItem( "Clear Selection", KeyEvent.VK_C );
	
	// color menu items
	private JMenu colorMenu = new JMenu( "Color" );
	private ButtonGroup colorMenuButtonGroup = new ButtonGroup( );
	private JRadioButtonMenuItem normalColorMenuItem = new JRadioButtonMenuItem( "Normal Color", true );
	private JRadioButtonMenuItem highContrastColorMenuItem = new JRadioButtonMenuItem( "High Contrast Color" );

	private MoleculeFilterPanel moleculeFilterPanel;
	private CorrelationFilterPanel correlationFilterPanel;

	protected CorrelationGraphVisualizer graph;
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
		this.moleculeFilterPanel.setGraph( this.graph );
	}

	/**
	 * Adds all of the necessary Components to this Component.
	 */
	private void buildPanel ( ) {

		MenuItemListener menuItemListener = new MenuItemListener( this );

		// CORRELATION FILTER ELEMENTS
		JPanel leftPanel = new JPanel( new BorderLayout( ));
		this.moleculeFilterPanel = new MoleculeFilterPanel( );
		leftPanel.add( moleculeFilterPanel, BorderLayout.CENTER );
		this.correlationFilterPanel = new CorrelationFilterPanel( );
		leftPanel.add( this.correlationFilterPanel, BorderLayout.SOUTH );

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
		CalculationChangeListener ccl = new CalculationChangeListener( this );
		this.pearsonCalculationMenuItem.addItemListener( ccl );
		this.spearmanCalculationMenuItem.addItemListener( ccl ); 
		this.kendallCalculationMenuItem.addItemListener( ccl );

		//LAYOUT MENU
		LayoutChangeListener lcl = new LayoutChangeListener( this );
		this.layoutMenu.setMnemonic( KeyEvent.VK_L );
		this.layoutMenu.getAccessibleContext( ).setAccessibleDescription(
			"Change the layout of the graph" );
		this.layoutMenuButtonGroup.add( this.multipleCirclesLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.singleCircleLayoutMenuItem );
//		this.layoutMenuButtonGroup.add( this.clusteredLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.randomLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.heatMapLayoutMenuItem );
		this.layoutMenu.add( this.multipleCirclesLayoutMenuItem );
		this.layoutMenu.add( this.singleCircleLayoutMenuItem );
//		this.layoutMenu.add( this.clusteredLayoutMenuItem );
		this.layoutMenu.add( this.randomLayoutMenuItem );
		this.layoutMenu.add( this.heatMapLayoutMenuItem );
		this.layoutMenu.addSeparator( );
		this.layoutMenu.add( this.springLayoutMenuItem );
		this.multipleCirclesLayoutMenuItem.addActionListener( lcl );
		this.singleCircleLayoutMenuItem.addActionListener( lcl );
//		this.clusteredLayoutMenuItem.addActionListener( lcl );
		this.randomLayoutMenuItem.addActionListener( lcl );
		this.heatMapLayoutMenuItem.addActionListener( lcl );
		this.springLayoutMenuItem.addActionListener( lcl );

		//VIEW MENU
		this.viewMenu.setMnemonic( KeyEvent.VK_V );
		this.viewMenu.getAccessibleContext( ).setAccessibleDescription(
			"Change the data view settings" );
		this.viewMenu.add( this.zoomInViewMenuItem );
		this.viewMenu.add( this.zoomOutViewMenuItem );
		this.viewMenu.add( this.fitToWindowViewMenuItem );
		this.viewMenu.add( this.selectAllViewMenuItem );
		this.viewMenu.add( this.clearSelectionViewMenuItem );
		this.selectAllViewMenuItem.addActionListener( menuItemListener );
		this.clearSelectionViewMenuItem.addActionListener( menuItemListener );
		this.selectAllViewMenuItem.setAccelerator(
			KeyStroke.getKeyStroke( KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK ));
		this.clearSelectionViewMenuItem.setAccelerator(
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ));

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

			this.graph = new CorrelationGraphVisualizer( );
			this.graph.setCorrelationFilterPanel( this.correlationFilterPanel );
			this.graph.setCorrelationDisplayPanel( this );
			this.graph.addGraphMouseListener( new CorrelationGraphMouseListener( ));
			this.addVertices( );
			this.addEdges( );
			this.graph.filterEdges( );

			this.setGraphVisualizer( this.graph );
			this.setGraphLayout( CircleLayout.class );
	}

	protected synchronized int addVertices( ) {
		int returnValue = 0;
		MoleculeCheckBox cb;
		for( String groupName : this.experiment.getMoleculeGroupNames( )) {
			for( Molecule molecule : this.experiment.getMoleculeGroup( groupName ).getMolecules( )) {
				this.graph.addVertex( molecule );
				this.moleculeFilterPanel.add( molecule );
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

	public void resetGraphLayout( ) {
		this.graph.resetLayout( );
	}

	public void setGraphLayout( Class <? extends AbstractLayout> layout ) {
		this.graph.setGraphLayout( layout );
	}

	private void setGraphVisualizer( CorrelationGraphVisualizer v ) {
		if ( this.graph != null )
			this.remove( this.graph );
		this.graph = v;
		this.correlationFilterPanel.setVisualization( v );
		// add labels to the graph
		this.add( new GraphZoomScrollPane( this.graph ), BorderLayout.CENTER );
//		this.graph.repaint( );
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

	protected class CorrelationGraphVisualizer extends GraphVisualizer<Molecule,Correlation>{
		private CorrelationFilterPanel correlationFilterPanel;
		private CorrelationDisplayPanel correlationDisplayPanel;

		public void setCorrelationFilterPanel( CorrelationFilterPanel cfp ){
			this.correlationFilterPanel = cfp;
		}

		public void setCorrelationDisplayPanel( CorrelationDisplayPanel cdp ){
			this.correlationDisplayPanel = cdp;
		}

		public int filterEdges( ) {
			
			int returnValue = 0;
			for( Correlation correlation : this.correlationDisplayPanel.experiment.getCorrelations( )) {
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
		private boolean isValidEdge( Correlation correlation ) {
			Molecule [] molecules = correlation.getMolecules( );
			return ( this.graph.containsVertex( molecules[ 0 ] ) &&
							 this.graph.containsVertex( molecules[ 1 ] ) &&
							 Math.abs( correlation.getValue( )) > this.correlationFilterPanel.getLow( ) &&
							 Math.abs( correlation.getValue( )) < this.correlationFilterPanel.getHigh( ));
		}

	}

	protected class CorrelationFilterPanel extends JPanel {

		private JLabel minCorrelationLabel = new JLabel( "Higher Than: ", SwingConstants.RIGHT );
		private JLabel maxCorrelationLabel = new JLabel( "Lower Than: ", SwingConstants.RIGHT );
		private JPanel minCorrelationFilterPanel = new JPanel( );
		private JPanel maxCorrelationFilterPanel = new JPanel( );
		private JSpinner minCorrelationSpinner;
		private JSpinner maxCorrelationSpinner;
		private EdgeFilterChangeListener efcl;   

		public CorrelationFilterPanel( ) {
			this( 0.6, 1.0 );
		}

		public CorrelationFilterPanel( double low, double high ) {
			this( 0.0, 1.0, 0.05, low, high );
		}

		public CorrelationFilterPanel( double min, double max, double step, double low, double high ) {
			this.setLayout( new BorderLayout( ));
			this.minCorrelationSpinner = new JSpinner( new SpinnerNumberModel( low, min, max, step ));
			this.maxCorrelationSpinner = new JSpinner( new SpinnerNumberModel( high, min, max, step ));

			this.minCorrelationSpinner.setPreferredSize( new Dimension( 80, 25 ));
			this.maxCorrelationSpinner.setPreferredSize( new Dimension( 80, 25 ));

			this.minCorrelationFilterPanel = new JPanel( new BorderLayout( ));
			this.minCorrelationFilterPanel.add( this.minCorrelationSpinner, BorderLayout.EAST );
			this.minCorrelationFilterPanel.add( this.minCorrelationLabel, BorderLayout.CENTER );

			this.maxCorrelationFilterPanel = new JPanel( new BorderLayout( ));
			this.maxCorrelationFilterPanel.add( this.maxCorrelationSpinner, BorderLayout.EAST );
			this.maxCorrelationFilterPanel.add( this.maxCorrelationLabel, BorderLayout.CENTER );

			this.add( this.minCorrelationFilterPanel, BorderLayout.NORTH );
			this.add( this.maxCorrelationFilterPanel, BorderLayout.SOUTH );

			this.setBorder( 
				BorderFactory.createTitledBorder( 
					BorderFactory.createLineBorder( Color.BLACK, 1 ),
					"Correlation Filter",
					TitledBorder.CENTER,
					TitledBorder.TOP
			));
		}
	

		public void setVisualization( CorrelationGraphVisualizer v ) {
			// add event listeners to the spinners to watch for changes.
			// remove the current listeners (if any)
			if ( this.efcl != null ) {
				this.minCorrelationSpinner.removeChangeListener( this.efcl );
				this.minCorrelationSpinner.removeChangeListener( this.efcl );
			}
			// add some new listeners
			this.efcl = new EdgeFilterChangeListener( v );
			this.minCorrelationSpinner.addChangeListener( this.efcl ); 
			this.maxCorrelationSpinner.addChangeListener( this.efcl );
		}

		public double getLow( ) {
			return (( Double )this.minCorrelationSpinner.getValue( )).doubleValue( );
		}

		public double getHigh( ) {
			return (( Double )this.maxCorrelationSpinner.getValue( )).doubleValue( );
		}

	}

	protected class MoleculeFilterPanel extends JPanel implements ItemListener,ActionListener {
		private JButton noneButton = new JButton( "None" );
		private JButton allButton = new JButton( "All" );
		private JPanel moleculeList = new JPanel( new GridLayout( 0, 1 ));
		private JScrollPane moleculeScrollPane = new JScrollPane( this.moleculeList );
		private ArrayList<MoleculeCheckBox> checkBoxArrayList = new ArrayList<MoleculeCheckBox>( );
		private CorrelationGraphVisualizer graph;
		private JLabel sortLabel = new JLabel( "Sort by ", SwingConstants.RIGHT );
		private JComboBox sortComboBox = new JComboBox( );

		public MoleculeFilterPanel( ) {
			super( new BorderLayout( ));
			JPanel sortSelectionPanel = new JPanel( new BorderLayout( ));
			sortSelectionPanel.add( this.sortLabel, BorderLayout.CENTER );
			sortSelectionPanel.add( this.sortComboBox, BorderLayout.EAST );
			// control configuration
			this.sortComboBox.addItem( "Index" );
			this.sortComboBox.addItem( "Group" );
			this.sortComboBox.addItem( "Name" );
		
			// ALL & RESET BUTTONS
			JPanel moleculeButtonPanel = new JPanel( new BorderLayout( ));
			this.allButton.setPreferredSize( new Dimension( 75, 20 ));
			this.noneButton.setPreferredSize( new Dimension( 75, 20 ));
			this.allButton.addActionListener( this );
			this.noneButton.addActionListener( this );
			moleculeButtonPanel.add( this.allButton, BorderLayout.WEST );
			moleculeButtonPanel.add( this.noneButton, BorderLayout.EAST );	

			// MOLECULE LIST
			this.add( sortSelectionPanel, BorderLayout.NORTH );
			this.add( this.moleculeScrollPane, BorderLayout.CENTER );
			this.add( moleculeButtonPanel, BorderLayout.SOUTH );
			this.setBorder( 
				BorderFactory.createTitledBorder( 
					BorderFactory.createLineBorder( Color.BLACK, 1 ),
						"Molecule Filter",
						TitledBorder.CENTER,
					TitledBorder.TOP
				)
			);
			this.moleculeScrollPane.getVerticalScrollBar( ).setUnitIncrement( 50 );
		}

		public void setGraph( CorrelationGraphVisualizer g ) {
			this.graph = g;
		}

		public void add( Molecule m ) {
			MoleculeCheckBox cb = new MoleculeCheckBox( m, true );
			this.moleculeList.add( cb );
			this.checkBoxArrayList.add( cb );
			cb.addItemListener( this );
		}

		public ArrayList <MoleculeCheckBox> getCheckBoxes( ) {
			return this.checkBoxArrayList;
		}

		//for the checkboxes
		public void itemStateChanged( ItemEvent event ) {
			synchronized( this.graph.graph ) {
				Molecule molecule = (( MoleculeCheckBox )event.getSource( )).getMolecule( );
				if ( event.getStateChange( ) == ItemEvent.SELECTED ) {
					this.graph.addVertex( molecule );
					for( Correlation correlation : molecule.getCorrelations( )) {
						if ( this.graph.isValidEdge( correlation ))
							this.graph.addEdge( correlation,
			                            new Pair<Molecule>( correlation.getMolecules( )),
																	EdgeType.UNDIRECTED );
					}
				}
				else {
					this.graph.removeVertex( molecule );
				}
			}
			this.graph.repaint( );
		}

		// for the select buttons
		public void actionPerformed( ActionEvent e ) {
			JButton source = ( JButton )e.getSource( );
			if ( source == this.allButton )
				this.setAll( true );
			else if ( source == this.noneButton )
				this.setAll( false );
		}

		private void setAll( boolean state ) {
			for ( MoleculeCheckBox m : this.getCheckBoxes( ) ) {
				m.setSelected( state );
				// fire the listeners
				for ( ItemListener i : m.getItemListeners( )) {
					i.itemStateChanged( new ItemEvent(
						m,
						-1,
						m.getMolecule( ),
						state ? ItemEvent.SELECTED : ItemEvent.DESELECTED
					));
				}
			}
		}
		
	}

	protected class EdgeFilterChangeListener implements ChangeListener {
		private CorrelationGraphVisualizer graph;

		public EdgeFilterChangeListener( CorrelationGraphVisualizer v ) {
			this.graph = v;
		}

		public void stateChanged( ChangeEvent e ) {
			this.graph.filterEdges( );
			this.graph.repaint( );
		}
	}

	protected class MoleculeCheckBox extends JCheckBox {
		private Molecule molecule;

		public MoleculeCheckBox( Molecule molecule, boolean state ) {
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
				this.displayPanel.graph.repaint( );
			}
		}
	}

	protected class MenuItemListener implements ActionListener {
		private CorrelationDisplayPanel cdp;

		public MenuItemListener ( CorrelationDisplayPanel c ) {
			this.cdp = c;
		}

		public void actionPerformed( ActionEvent event ) {
			Component item = ( Component )event.getSource( );

			if ( item == this.cdp.selectAllViewMenuItem ) {
				this.cdp.graph.selectAll( );
			}
			else if ( item == this.cdp.clearSelectionViewMenuItem ) {
				this.cdp.graph.clearSelection( );
			}
		}
	}

	protected class LayoutChangeListener implements ActionListener {
			private CorrelationDisplayPanel cdp;

			public LayoutChangeListener( CorrelationDisplayPanel c ) {
				this.cdp = c;
			}

			public void actionPerformed( ActionEvent event ) {
				Component item = ( Component )event.getSource( );

				if ( item == this.cdp.springLayoutMenuItem ) {
					this.cdp.graph.animate( this.cdp.springLayoutMenuItem.getState( ));
				} else {
					this.cdp.springLayoutMenuItem.setState( false );
					if ( item == this.cdp.multipleCirclesLayoutMenuItem )
						this.cdp.setGraphLayout( MultipleCirclesLayout.class );
					if ( item == this.cdp.singleCircleLayoutMenuItem )
						this.cdp.setGraphLayout( CircleLayout.class );
//					else if ( item == this.cdp.clusteredLayoutMenuItem )
//						this.cdp.setGraphLayout( ClusteredLayout.class );
					else if ( item == this.cdp.randomLayoutMenuItem )
						this.cdp.setGraphLayout( RandomLayout.class );
				}
			}
	}
}



