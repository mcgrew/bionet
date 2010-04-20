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
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;


/**
 * A class for displaying and interacting with Correlation data for a set of molecules.
 */
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
		super( new BorderLayout( ) );
		this.buildPanel( );
		this.createGraph( data );
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

		this.setGraphVisualizer( this.graph );
		this.setGraphLayout( CircleLayout.class );
		this.moleculeFilterPanel.setGraph( this.graph );
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
		this.add( new GraphZoomScrollPane( this.graph ), BorderLayout.CENTER );
//		this.graph.repaint( );
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

	// ******************* PROTECTED CLASSES **************************


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
				this.displayPanel.graph.filterEdges( );
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

				if ( item == this.cdp.clusteredLayoutMenuItem ) {
					this.cdp.graph.animate( this.cdp.clusteredLayoutMenuItem.getState( ));
				} else {
					this.cdp.clusteredLayoutMenuItem.setState( false );
					if ( item == this.cdp.multipleCirclesLayoutMenuItem )
						this.cdp.setGraphLayout( MultipleCirclesLayout.class );
					else if ( item == this.cdp.singleCircleLayoutMenuItem )
						this.cdp.setGraphLayout( CircleLayout.class );
//					else if ( item == this.cdp.clusteredLayoutMenuItem )
//						this.cdp.setGraphLayout( ClusteredLayout.class );
					else if ( item == this.cdp.randomLayoutMenuItem )
						this.cdp.setGraphLayout( RandomLayout.class );
					else if ( item == this.cdp.kkLayoutMenuItem )
						this.cdp.setGraphLayout( KKLayout.class );
					else if ( item == this.cdp.frLayoutMenuItem )
						this.cdp.setGraphLayout( FRLayout.class );
					else if ( item == this.cdp.springLayoutMenuItem )
						this.cdp.setGraphLayout( SpringLayout2.class );

				}
			}
	}
}



