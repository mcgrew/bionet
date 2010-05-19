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

import edu.purdue.jsysnet.ui.layout.RandomLayout;
import edu.purdue.jsysnet.ui.layout.MultipleCirclesLayout;
import edu.purdue.jsysnet.util.Experiment;
import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Range;
import edu.purdue.jsysnet.io.DataHandler;
import edu.purdue.jsysnet.JSysNet;

import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.Enumeration;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;


/**
 * A class for displaying and interacting with Correlation data for a set of molecules.
 */
public class CorrelationDisplayPanel extends JPanel implements ActionListener {

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
	private JRadioButtonMenuItem kkLayoutMenuItem = new JRadioButtonMenuItem( "KKLayout" );
	private JRadioButtonMenuItem frLayoutMenuItem = new JRadioButtonMenuItem( "FRLayout" );
	private JRadioButtonMenuItem springLayoutMenuItem = new JRadioButtonMenuItem( "Spring Layout" );
	private JCheckBoxMenuItem clusteredLayoutMenuItem = new JCheckBoxMenuItem( "Clustered Spring Embedding" );

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
	protected InfoPanel infoPanel = new InfoPanel( );
	protected JSplitPane graphSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );

	protected DataHandler data = null;
	protected Experiment experiment = null;

	/**
	 * Creates a new CorrelationDisplayPanel. When this constructor is used,
	 *	you must specify a DataHandler object when you call createGraph.
	 */
	public CorrelationDisplayPanel ( ) {
		super( new BorderLayout( ) );
		this.buildPanel( );
	}

	/**
	 * Creates a CorrelationDisplayPanel object using the supplied data.
	 * 
	 * @param data The data to be used in this Panel
	 */
	public CorrelationDisplayPanel ( DataHandler data ) {
		super( new BorderLayout( ));
		this.buildPanel( );
		this.createGraph( data );
	}

	/**
	 * Adds all of the necessary Components to this Component.
	 */
	private void buildPanel ( ) {

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
		CalculationChangeListener ccl = new CalculationChangeListener( );
		this.pearsonCalculationMenuItem.addItemListener( ccl );
		this.spearmanCalculationMenuItem.addItemListener( ccl ); 
		this.kendallCalculationMenuItem.addItemListener( ccl );

		//LAYOUT MENU
		LayoutChangeListener lcl = new LayoutChangeListener( );
		this.layoutMenu.setMnemonic( KeyEvent.VK_L );
		this.layoutMenu.getAccessibleContext( ).setAccessibleDescription(
			"Change the layout of the graph" );
		this.layoutMenuButtonGroup.add( this.multipleCirclesLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.singleCircleLayoutMenuItem );
//		this.layoutMenuButtonGroup.add( this.clusteredLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.randomLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.kkLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.frLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.springLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.heatMapLayoutMenuItem );
		this.layoutMenu.add( this.multipleCirclesLayoutMenuItem );
		this.layoutMenu.add( this.singleCircleLayoutMenuItem );
//		this.layoutMenu.add( this.clusteredLayoutMenuItem );
		this.layoutMenu.add( this.randomLayoutMenuItem );
		this.layoutMenu.add( this.kkLayoutMenuItem );
		this.layoutMenu.add( this.frLayoutMenuItem );
		this.layoutMenu.add( this.springLayoutMenuItem );
		this.layoutMenu.add( this.heatMapLayoutMenuItem );
		this.layoutMenu.addSeparator( );
		this.layoutMenu.add( this.clusteredLayoutMenuItem );
		this.multipleCirclesLayoutMenuItem.addActionListener( lcl );
		this.singleCircleLayoutMenuItem.addActionListener( lcl );
//		this.clusteredLayoutMenuItem.addActionListener( lcl );
		this.randomLayoutMenuItem.addActionListener( lcl );
		this.kkLayoutMenuItem.addActionListener( lcl );
		this.frLayoutMenuItem.addActionListener( lcl );
		this.springLayoutMenuItem.addActionListener( lcl );
		this.heatMapLayoutMenuItem.addActionListener( lcl );
		this.clusteredLayoutMenuItem.addActionListener( lcl );

		//VIEW MENU
		this.viewMenu.setMnemonic( KeyEvent.VK_V );
		this.viewMenu.getAccessibleContext( ).setAccessibleDescription(
			"Change the data view settings" );
		this.viewMenu.add( this.zoomOutViewMenuItem );
		this.viewMenu.add( this.zoomInViewMenuItem );
		this.viewMenu.add( this.fitToWindowViewMenuItem );
		this.viewMenu.add( this.selectAllViewMenuItem );
		this.viewMenu.add( this.clearSelectionViewMenuItem );
		this.zoomOutViewMenuItem.addActionListener( this );
		this.zoomInViewMenuItem.addActionListener( this );
		this.fitToWindowViewMenuItem.addActionListener( this );
		this.selectAllViewMenuItem.addActionListener( this );
		this.clearSelectionViewMenuItem.addActionListener( this );
		this.selectAllViewMenuItem.setAccelerator(
			KeyStroke.getKeyStroke( KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK ));
		this.clearSelectionViewMenuItem.setAccelerator(
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ));

		this.zoomOutViewMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK ));
		this.zoomInViewMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK ));
		this.fitToWindowViewMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK ));

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

	/**
	 * Creates a Graph using the data supplied in the constructor.
	 */
	public void createGraph( ) {
			if ( this.data != null )
				this.createGraph( this.data );
	}

	/**
	 * Creates a Correlation Graph.
	 * 
	 * @param data A Datahandler Object containing the data to be used.
	 */
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

		this.add( this.graphSplitPane, BorderLayout.CENTER );
		this.graphSplitPane.setBottomComponent( this.infoPanel );
		this.graphSplitPane.setDividerLocation( 
			JSysNet.settings.getInt( "windowHeight" ) - 250 );
		this.setGraphVisualizer( this.graph );
		this.setGraphLayout( CircleLayout.class );
	}

	/**
	 * Adds the Vertices (Molecules) to the Graph
	 * 
	 * @return The number of molecules added to the graph
	 */
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

	/**
	 * Adds the Edges (Correlations) to the Graph.
	 * 
	 * @return The number of Correlations added to the graph.
	 */
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

	/**
	 * Resets the Graph Layout back to its original state.
	 */
	public void resetGraphLayout( ) {
		this.graph.resetLayout( );
	}

	/**
	 * Sets the graph layout for this CorrelationDisplayPanel.
	 * 
	 * @param layout The Class of the layout to use for the graph. A new
	 *	instance will be created.
	 */
	public void setGraphLayout( Class <? extends AbstractLayout> layout ) {
		this.graph.setGraphLayout( layout );
	}

	/**
	 * Sets the CorrelationGraphVisualizer component for this panel.
	 * 
	 * @param v The CorrelationGraphVisualizer to use.
	 */
	private void setGraphVisualizer( CorrelationGraphVisualizer v ) {
		if ( this.graph != null )
			this.remove( this.graph );
		this.graph = v;
		this.correlationFilterPanel.setVisualization( v );
		// add labels to the graph
		this.graphSplitPane.setTopComponent( this.graph.getScrollPane( ));
		v.addPickedVertexStateChangeListener( new PickedStateChangeListener <Molecule> ( ){

			public void stateChanged( PickedStateChangeEvent <Molecule> event ) {
				if ( event.getStateChange( ))
					infoPanel.add( event.getItem( ));
				else
					infoPanel.remove( event.getItem( ));
			}
		});
		v.addPickedEdgeStateChangeListener( new PickedStateChangeListener <Correlation> ( ){
			public void stateChanged( PickedStateChangeEvent <Correlation> event ) {
				if ( event.getStateChange( ))
					infoPanel.add( event.getItem( ));
				else
					infoPanel.remove( event.getItem( ));
			}
		});

	}

	/**
	 * Brings up a dialog to allow you to select the appropriate experiment. If
	 *	only one experiment is available, no dialog is shown an this method simply
	 *	returns that experiment.
	 * 
	 * @param experiments An ArrayList containing the possible Experiments
	 * @return The experiment you selected, or null if you pressed cancel, or
	 *	if no experiments are available
	 */
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

	/**
	 * Returns the range setting of the Correlation Filter
	 * 
	 * @return The value of the correlation setting
	 */
	public Range getCorrelationRange( ) {
		return this.correlationFilterPanel.getRange( );
	}

	/**
	 * Scales the graph to the given level relative to the current zoom level.
	 * 
	 * @param amount The amount to scale the graph view by.
	 */
	public void zoom( float amount ) {
		this.graph.scale( amount );
	}

	/**
	 * Sets the graph to the given zoom level, 1.0 being 100%.
	 * 
	 * @param level the new Zoom level.
	 */
	public void setZoom( float level ) {
		this.graph.zoomTo( level );
	}

	/**
	 * The actionPerformed method of the ActionListener interface.
	 * 
	 * @param event The event which triggered this action.
	 */
	public void actionPerformed( ActionEvent event ) {
		Component item = ( Component )event.getSource( );
		if ( item == this.selectAllViewMenuItem ) {
			this.graph.selectAll( );
		} else if ( item == this.clearSelectionViewMenuItem ) {
			this.graph.clearSelection( );
		}	else if ( item == this.zoomInViewMenuItem ) {
			this.graph.scale( 1.25f );
		} else if ( item == this.zoomOutViewMenuItem ) {
			this.graph.scale( 0.8f );
		} else if ( item == this.fitToWindowViewMenuItem ) {
			this.graph.resetView( );
		}
	}

	// ******************* PRIVATE/PROTECTED CLASSES **************************

	protected class MoleculeFilterPanel extends JPanel implements ItemListener,ActionListener {
		private JButton noneButton = new JButton( "None" );
		private JButton allButton = new JButton( "All" );
		private JPanel moleculeList = new JPanel( new GridLayout( 0, 1 ));
		private JScrollPane moleculeScrollPane = new JScrollPane( this.moleculeList );
		private ArrayList<MoleculeCheckBox> checkBoxArrayList = new ArrayList<MoleculeCheckBox>( );
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

		/**
		 * Adds a molecule to the Graph and creates a Checkbox for removing it.
		 * 
		 * @param m The Molecule to add.
		 */
		public void add( Molecule m ) {
			MoleculeCheckBox cb = new MoleculeCheckBox( m, true );
			this.moleculeList.add( cb );
			this.checkBoxArrayList.add( cb );
			cb.addItemListener( this );
		}

		/**
		 * Returns An arrayList of MoleculeCheckBoxes.
		 * 
		 * @return an ArrayList of MoleculeCheckBoxes from the display panel
		 */
		public ArrayList <MoleculeCheckBox> getCheckBoxes( ) {
			return this.checkBoxArrayList;
		}

		//for the checkboxes
		/**
		 * The itemStateChanged method of the ItemListener interface.
		 * 
		 * @param event the event which triggered this action.
		 */
		public void itemStateChanged( ItemEvent event ) {
			synchronized( graph.graph ) {
				Molecule molecule = (( MoleculeCheckBox )event.getSource( )).getMolecule( );
				if ( event.getStateChange( ) == ItemEvent.SELECTED ) {
					graph.addVertex( molecule );
					for( Correlation correlation : molecule.getCorrelations( )) {
						if ( graph.isValidEdge( correlation ))
							graph.addEdge( correlation,
			                            new Pair<Molecule>( correlation.getMolecules( )),
																	EdgeType.UNDIRECTED );
					}
				}
				else {
					graph.removeVertex( molecule );
				}
			}
			graph.repaint( );
		}

		// for the select buttons
		/**
		 * The actionPerformed method of the ActionListener interface.
		 * 
		 * @param e The event which triggered this action.
		 */
		public void actionPerformed( ActionEvent e ) {
			JButton source = ( JButton )e.getSource( );
			if ( source == this.allButton )
				this.setAll( true );
			else if ( source == this.noneButton )
				this.setAll( false );
		}

		/**
		 * Sets all checkboxes to the desired state.
		 * 
		 * @param state True for checkecd, false for unchecked.
		 */
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

		public void filter( String filter ) {
			Pattern p = Pattern.compile( filter );
			for( MoleculeCheckBox m : this.getCheckBoxes( )) {
				m.setVisible( p.matcher( m.getMolecule( ).toString( ) ).matches( ));
			}
		}
		
	}

	/**
	 * A class for checkboxes to turn on and off graph nodes.
	 */
	protected class MoleculeCheckBox extends JCheckBox {
		private Molecule molecule;

		/**
		 * Constructs a MoleculeCheckBox object.
		 * 
		 * @param molecule The molecule to connect with this check box.
		 * @param state The initial state of the checkbox.
		 */
		public MoleculeCheckBox( Molecule molecule, boolean state ) {
			super( molecule.getAttribute( "id" ), state );
			this.molecule = molecule;
		}

		/**
		 * Sets the molecule to be associated with this check box.
		 * 
		 * @param molecule The molecule to connect with this check box.
		 */
		public void setMolecule( Molecule molecule ) {
			this.setLabel( molecule.getAttribute( "id" ));
			this.molecule = molecule;
		}

		/**
		 * Returns the molecule associated with this check box.
		 * 
		 * @return The Molecule assocated with this check box.
		 */
		public Molecule getMolecule( ) {
			return this.molecule;
		}
	}

	protected class InfoPanel extends JTabbedPane {
		private final static String MOLECULE_INDEX = "id";
		private JTable moleculeTable = new JTable(0,0);
		private JTable correlationTable = new JTable(0,0);

		public InfoPanel( ) {
			super( );
			this.moleculeTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
			this.correlationTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
			this.add( new JScrollPane( moleculeTable ), "Molecules" );
			this.add( new JScrollPane( correlationTable ), "Correlations" );
		}

		public void add( Molecule molecule ) {
			DefaultTableModel tm = (DefaultTableModel)this.moleculeTable.getModel( );
			String [] attributes = molecule.getAttributeNames( );
			if ( tm.getColumnCount( ) == 0 ) {
				for ( String key : attributes ) {
					tm.addColumn( key );
				}
				Enumeration <TableColumn> columns = this.moleculeTable.getColumnModel( ).getColumns( );
				while( columns.hasMoreElements( ))
					columns.nextElement( ).setPreferredWidth( 75 );
			}
			int c = attributes.length;
			Object[] newRow = new Object[ c ];
			for( int i=0; i < c; i++ ) {
				newRow[ i ] = molecule.getAttribute( tm.getColumnName( i ));
			}
			tm.addRow( newRow );
		}

		public void add( Correlation correlation ) {
			DefaultTableModel tm = (DefaultTableModel)this.correlationTable.getModel( );
			if ( tm.getColumnCount( ) == 0 ) {
				String [ ] keys = { "Molecule 1", "Molecule 2", "Pearson Value", "Spearman Rank Value",
					"Kendall Tau b Rank Value" };
				for ( String key : keys ) {
					tm.addColumn( key );
				}
				Enumeration <TableColumn> columns = this.correlationTable.getColumnModel( ).getColumns( );
				while( columns.hasMoreElements( ))
					columns.nextElement( ).setPreferredWidth( 150 );
			}
			Object[] newRow = new Object[ 5 ];
			newRow[ 0 ] = correlation.getMolecules( )[ 0 ].getAttribute( MOLECULE_INDEX );
			newRow[ 1 ] = correlation.getMolecules( )[ 1 ].getAttribute( MOLECULE_INDEX );
			newRow[ 2 ] = String.format( "%.5f", correlation.getValue( Correlation.PEARSON ));
			newRow[ 3 ] = String.format( "%.5f", correlation.getValue( Correlation.SPEARMAN ));
			newRow[ 4 ] = String.format( "%.5f", correlation.getValue( Correlation.KENDALL ));
			tm.addRow( newRow );
		}

		public void remove( Molecule molecule ) {
			int row = this.getRowOf( molecule );
			if ( row >= 0 )
				((DefaultTableModel)this.moleculeTable.getModel( )).removeRow( row );
		}

		public void remove( Correlation correlation ) {
			int row = getRowOf( correlation );
			if ( row >= 0 )
				((DefaultTableModel)this.correlationTable.getModel( )).removeRow( row );
		}

		public void clearMolecules( ) {
			DefaultTableModel tm = (DefaultTableModel)this.moleculeTable.getModel( );
			while( tm.getRowCount( ) > 0 ) {
				tm.removeRow( 0 );
			}
		}

		public void clearCorrelations( ) {
			DefaultTableModel tm = (DefaultTableModel)this.correlationTable.getModel( );
			while( tm.getRowCount( ) > 0 ) {
				tm.removeRow( 0 );
			}
		}

		private int getRowOf( Molecule molecule  ) {
			DefaultTableModel tm = (DefaultTableModel)this.moleculeTable.getModel( );
			int returnValue = 0;
			boolean match;
			while( returnValue < tm.getRowCount( )) {
				match = true;
				for( int i=0; match && i < tm.getColumnCount( ); i++ ) {
					match = ( molecule.getAttribute( tm.getColumnName( i )).equals( 
						tm.getValueAt( returnValue, i )));
				}
				if ( match )
					return returnValue;
				returnValue++;
			}
			return -1;
		}

		private int getRowOf( Correlation correlation ) {
			DefaultTableModel tm = (DefaultTableModel)this.correlationTable.getModel( );
			int returnValue = 0;
			while( returnValue < tm.getRowCount( )) {
				if ( correlation.getMolecules( )[ 0 ].getAttribute( MOLECULE_INDEX ).equals( 
					tm.getValueAt( returnValue, 0 )) && 
					correlation.getMolecules( )[ 1 ].getAttribute( MOLECULE_INDEX ).equals(
					tm.getValueAt( returnValue, 1 )))
					return returnValue;
			}
			return -1;

		}

	}

	/**
	 * A class which listens for a change in the state of the calculation menu.
	 * @todo Integrate this listener with the CorrelationDisplayPanel class.
	 */
	protected class CalculationChangeListener implements ItemListener {

		/**
		 * The itemStateChanged method of the ItemListener interface.
		 * 
		 * @param event The event which triggered this action.
		 */
		public void itemStateChanged( ItemEvent event ) {
			JRadioButtonMenuItem item = (( JRadioButtonMenuItem )event.getSource( ));
			if ( event.getStateChange( ) == ItemEvent.SELECTED ) {
				if ( item == pearsonCalculationMenuItem )
					Correlation.setDefaultMethod( Correlation.PEARSON );
				else if ( item == spearmanCalculationMenuItem )
					Correlation.setDefaultMethod( Correlation.SPEARMAN );
				else if ( item == kendallCalculationMenuItem )
					Correlation.setDefaultMethod( Correlation.KENDALL );
				graph.filterEdges( );
				graph.repaint( );
			}
		}
	}

	/**
	 * A class for listening for clicks on menu items.
	 * @todo Integrate this listener with the CorrelationDisplayPanel class.
	 */
	protected class MenuItemListener implements ActionListener {

		/**
		 * The actionPerformed method of the ActionListener interface.
		 * 
		 * @param event The event which triggered this action.
		 */
		public void actionPerformed( ActionEvent event ) {
			Component item = ( Component )event.getSource( );

			if ( item == selectAllViewMenuItem ) {
				graph.selectAll( );
			}
			else if ( item == clearSelectionViewMenuItem ) {
				graph.clearSelection( );
			}
		}
	}

	/**
	 * A class for listening for changes to the layout menu.
	 * @todo Integrate this class with the CorrelationDisplayPanel class.
	 */
	protected class LayoutChangeListener implements ActionListener {

			/**
			 * The actionPerformed method of the ActionListener interface.
			 * 
			 * @param event The event which triggered this action.
			 */
			public void actionPerformed( ActionEvent event ) {
				Component item = ( Component )event.getSource( );

				if ( item == clusteredLayoutMenuItem ) {
					graph.animate( clusteredLayoutMenuItem.getState( ));
				} else {
					clusteredLayoutMenuItem.setState( false );
					graph.resetView( );
					if ( item == multipleCirclesLayoutMenuItem )
						setGraphLayout( MultipleCirclesLayout.class );
					else if ( item == singleCircleLayoutMenuItem )
						setGraphLayout( CircleLayout.class );
//					else if ( item == clusteredLayoutMenuItem )
//						setGraphLayout( ClusteredLayout.class );
					else if ( item == randomLayoutMenuItem )
						setGraphLayout( RandomLayout.class );
					else if ( item == kkLayoutMenuItem )
						setGraphLayout( KKLayout.class );
					else if ( item == frLayoutMenuItem )
						setGraphLayout( FRLayout.class );
					else if ( item == springLayoutMenuItem )
						setGraphLayout( SpringLayout2.class );

				}
			}
	}
}



