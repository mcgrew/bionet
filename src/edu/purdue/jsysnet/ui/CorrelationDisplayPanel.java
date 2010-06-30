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

import edu.purdue.jsysnet.ui.layout.LayoutAnimator;
import edu.purdue.jsysnet.ui.layout.RandomLayout;
import edu.purdue.jsysnet.ui.layout.MultipleCirclesLayout;
import edu.purdue.jsysnet.util.Experiment;
import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Range;
import edu.purdue.jsysnet.util.MonitorableRange;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Collection;
import java.util.Vector;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.visualization.picking.PickedState;


/**
 * A class for displaying and interacting with Correlation data for a set of molecules.
 */
public class CorrelationDisplayPanel extends JPanel implements ActionListener,ChangeListener,ItemListener {

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
	private JRadioButtonMenuItem randomLayoutMenuItem = new JRadioButtonMenuItem( "Random" );
	private JRadioButtonMenuItem heatMapLayoutMenuItem = new JRadioButtonMenuItem( "Heat Map" );
	private JRadioButtonMenuItem kkLayoutMenuItem = new JRadioButtonMenuItem( "KKLayout" );
	private JRadioButtonMenuItem frLayoutMenuItem = new JRadioButtonMenuItem( "FRLayout" );
	private JRadioButtonMenuItem springLayoutMenuItem = new JRadioButtonMenuItem( "Spring Layout" );
	private JCheckBoxMenuItem animatedLayoutMenuItem = new JCheckBoxMenuItem( "Spring Embedding" );

	// view menu items
	private JMenu viewMenu = new JMenu( "View" );
	private JMenuItem zoomInViewMenuItem = new JMenuItem( "Zoom In", KeyEvent.VK_I );
	private JMenuItem zoomOutViewMenuItem = new JMenuItem( "Zoom Out", KeyEvent.VK_O );
	private JMenuItem fitToWindowViewMenuItem = new JMenuItem( "Fit to Window", KeyEvent.VK_F );
	private JMenuItem selectAllViewMenuItem = new JMenuItem( "Select All", KeyEvent.VK_A );
	private JMenuItem clearSelectionViewMenuItem = new JMenuItem( "Clear Selection", KeyEvent.VK_C );
	private JMenuItem invertSelectionViewMenuItem = new JMenuItem( "Invert Selection", KeyEvent.VK_I );
	private JMenuItem selectCorrelatedViewMenuItem = new JMenuItem( "Select Correlated to Selection", KeyEvent.VK_R );
	private JMenuItem hideSelectedViewMenuItem = new JMenuItem( "Hide Selected", KeyEvent.VK_H );
	private JMenuItem hideUnselectedViewMenuItem = new JMenuItem( "Hide Unselected", KeyEvent.VK_U );
	private JMenuItem hideUncorrelatedViewMenuItem = new JMenuItem( "Hide Uncorrelated to Selection", KeyEvent.VK_L );
	private JMenuItem hideOrphansViewMenuItem = new JMenuItem( "Hide Orphans", KeyEvent.VK_P );
	private JMenuItem showCorrelatedViewMenuItem = new JMenuItem( "Show All Correlated to Visible", KeyEvent.VK_S );

	
	// color menu items
	private JMenu colorMenu = new JMenu( "Color" );
	private ButtonGroup colorMenuButtonGroup = new ButtonGroup( );
	private JRadioButtonMenuItem normalColorMenuItem = new JRadioButtonMenuItem( "Normal Color", true );
	private JRadioButtonMenuItem highContrastColorMenuItem = new JRadioButtonMenuItem( "High Contrast Color" );

	private MoleculeFilterPanel moleculeFilterPanel;
	private CorrelationFilterPanel correlationFilterPanel;

	private CorrelationGraphVisualizer graph;
	private Layout <Molecule,Correlation> layout; //Graph Layout
	private InfoPanel infoPanel = new InfoPanel( );
	private JSplitPane graphSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
	private HeatMap  heatMapPanel;

	private DataHandler data = null;
	private Experiment experiment = null;
	private String title;
	private Scalable visibleGraph;

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
	 * Gets the title of this CorrelationDisplayPanel, which should be the
	 * description field of the experiment.
	 * 
	 * @return the title of this CorrelationDisplayPanel.
	 */
	public String getTitle( ) {
		return this.title;
	}

	/**
	 * Sets the title of this CorrelationDisplayPanel.
	 * 
	 * @param title The new title for this Panel
	 */
	public void setTitle( String title ) {
		this.title = title;
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
		this.layoutMenuButtonGroup.add( this.randomLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.kkLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.frLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.springLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.heatMapLayoutMenuItem );
		this.layoutMenu.add( this.multipleCirclesLayoutMenuItem );
		this.layoutMenu.add( this.singleCircleLayoutMenuItem );
		this.layoutMenu.add( this.randomLayoutMenuItem );
		this.layoutMenu.add( this.kkLayoutMenuItem );
		this.layoutMenu.add( this.frLayoutMenuItem );
		this.layoutMenu.add( this.springLayoutMenuItem );
		this.layoutMenu.add( this.heatMapLayoutMenuItem );
		this.layoutMenu.addSeparator( );
		this.layoutMenu.add( this.animatedLayoutMenuItem );
		this.multipleCirclesLayoutMenuItem.addActionListener( lcl );
		this.singleCircleLayoutMenuItem.addActionListener( lcl );
		this.randomLayoutMenuItem.addActionListener( lcl );
		this.kkLayoutMenuItem.addActionListener( lcl );
		this.frLayoutMenuItem.addActionListener( lcl );
		this.springLayoutMenuItem.addActionListener( lcl );
		this.heatMapLayoutMenuItem.addActionListener( lcl );
		this.animatedLayoutMenuItem.addActionListener( lcl );

		//VIEW MENU
		this.viewMenu.setMnemonic( KeyEvent.VK_V );
		this.viewMenu.getAccessibleContext( ).setAccessibleDescription(
			"Change the data view settings" );
		this.viewMenu.add( this.zoomOutViewMenuItem );
		this.viewMenu.add( this.zoomInViewMenuItem );
		this.viewMenu.add( this.fitToWindowViewMenuItem );
		this.viewMenu.addSeparator( );
		this.viewMenu.add( this.selectAllViewMenuItem );
		this.viewMenu.add( this.clearSelectionViewMenuItem );
		this.viewMenu.add( this.invertSelectionViewMenuItem );
		this.viewMenu.add( this.selectCorrelatedViewMenuItem );
		this.viewMenu.addSeparator( );
		this.viewMenu.add( this.hideSelectedViewMenuItem );
		this.viewMenu.add( this.hideUnselectedViewMenuItem );
		this.viewMenu.add( this.hideUncorrelatedViewMenuItem );
		this.viewMenu.add( this.hideOrphansViewMenuItem );
		this.viewMenu.add( this.showCorrelatedViewMenuItem );
		this.zoomOutViewMenuItem.addActionListener( this );
		this.zoomInViewMenuItem.addActionListener( this );
		this.fitToWindowViewMenuItem.addActionListener( this );
		this.selectAllViewMenuItem.addActionListener( this );
		this.clearSelectionViewMenuItem.addActionListener( this );
		this.invertSelectionViewMenuItem.addActionListener( this );
		this.selectCorrelatedViewMenuItem.addActionListener( this );
		this.hideSelectedViewMenuItem.addActionListener( this );
		this.hideUnselectedViewMenuItem.addActionListener( this );
		this.hideUncorrelatedViewMenuItem.addActionListener( this );
		this.hideOrphansViewMenuItem.addActionListener( this );
		this.showCorrelatedViewMenuItem.addActionListener( this );
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
//		this.highContrastColorMenuItem.setEnabled( false );
		this.colorMenu.add( this.normalColorMenuItem );
		this.colorMenu.add( this.highContrastColorMenuItem );
		this.normalColorMenuItem.addItemListener( this );
		this.highContrastColorMenuItem.addItemListener( this );

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
	public boolean createGraph( ) {
			if ( this.data != null ) {
				return this.createGraph( this.data );
			}
			return false;
	}

	/**
	 * Creates a Correlation Graph.
	 * 
	 * @param data A Datahandler Object containing the data to be used.
	 */
	public boolean createGraph( DataHandler data ) {
		this.data = data;
		this.setVisible( true );
		
		this.experiment = experimentSelection( data.getExperiments( ));
		if ( this.experiment == null ) {
			return false;
		}
		this.title = this.experiment.getAttribute( "description" );

		this.graph = new CorrelationGraphVisualizer( 
			this.experiment, this.correlationFilterPanel.getMonitorableRange( ));
		this.graph.addGraphMouseListener( new CorrelationGraphMouseListener( ));
		for( String groupName : this.experiment.getMoleculeGroupNames( ))
			for( Molecule molecule : this.experiment.getMoleculeGroup( groupName ).getMolecules( ))
				this.moleculeFilterPanel.add( molecule );

		this.add( this.graphSplitPane, BorderLayout.CENTER );
		this.graphSplitPane.setBottomComponent( this.infoPanel );
		this.graphSplitPane.setDividerLocation( 
			JSysNet.settings.getInt( "windowHeight" ) - 250 );
		this.setGraphVisualizer( this.graph );
		this.setGraphLayout( CircleLayout.class );

		this.heatMapPanel = new HeatMap( this.getTitle( ), this.graph.getVertices( ), this.getCorrelationRange( ));
		this.graph.addVertexChangeListener( this.heatMapPanel );
		return true;
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
		if ( this.heatMapPanel != null && this.visibleGraph == this.heatMapPanel ) {
			this.remove( this.heatMapPanel.getScrollPane( ));
			this.add( this.graphSplitPane, BorderLayout.CENTER );
			this.visibleGraph = this.graph;
			this.selectAllViewMenuItem.setEnabled( true );
			this.clearSelectionViewMenuItem.setEnabled( true );
			this.invertSelectionViewMenuItem.setEnabled( true );
			this.selectCorrelatedViewMenuItem.setEnabled( true );
			this.hideSelectedViewMenuItem.setEnabled( true );
			this.hideUnselectedViewMenuItem.setEnabled( true );
			this.hideUncorrelatedViewMenuItem.setEnabled( true );
			this.animatedLayoutMenuItem.setEnabled( true );
			this.graph.getScrollPane( ).repaint( );
		}
		this.graph.setGraphLayout( layout );
	}

	private void heatMap( ) {
		this.selectAllViewMenuItem.setEnabled( false );
		this.clearSelectionViewMenuItem.setEnabled( false );
		this.invertSelectionViewMenuItem.setEnabled( false );
		this.selectCorrelatedViewMenuItem.setEnabled( false );
		this.hideSelectedViewMenuItem.setEnabled( false );
		this.hideUnselectedViewMenuItem.setEnabled( false );
		this.hideUncorrelatedViewMenuItem.setEnabled( false );
		this.animatedLayoutMenuItem.setEnabled( false );
		this.remove( this.graphSplitPane );
		this.add( this.heatMapPanel.getScrollPane( ), BorderLayout.CENTER );
		this.visibleGraph = this.heatMapPanel;
		this.repaint( );
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
		this.correlationFilterPanel.getMonitorableRange( ).addChangeListener( v );
		// add labels to the graph
		this.graphSplitPane.setTopComponent( this.graph.getScrollPane( ));
		this.visibleGraph = this.graph;
		this.graph.addAnimationListener( this );
		this.graph.addVertexChangeListener( this.moleculeFilterPanel );
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
	
	public Experiment getExperiment( ) {
		return experiment;
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
	public MonitorableRange getCorrelationRange( ) {
		return this.correlationFilterPanel.getMonitorableRange( );
	}

	/**
	 * Scales the graph to the given level relative to the current zoom level.
	 * 
	 * @param amount The amount to scale the graph view by.
	 */
	public float scale( float amount ) {
		return this.visibleGraph.scale( amount );
	}

	/**
	 * Sets the graph to the given zoom level, 1.0 being 100%.
	 * 
	 * @param level the new Zoom level.
	 */
	public float scaleTo( float level ) {
		return this.visibleGraph.scaleTo( level );
	}

	/**
	 * The stateChanged method of the ChangeListener interface
	 * 
	 * @param event The event which triggered this action.
	 */
	public void stateChanged( ChangeEvent event ) {
		LayoutAnimator animator = (LayoutAnimator)event.getSource( );
		if ( animator.isStopped( ))
			this.animatedLayoutMenuItem.setState( false );
	}

	public void itemStateChanged( ItemEvent e ) {
		if ( e.getStateChange( ) == ItemEvent.SELECTED ) {
			Object item = e.getSource( );
			if ( item == this.highContrastColorMenuItem ) {
				graph.setBackground( Color.BLACK );
				graph.setForeground( Color.WHITE );
				graph.setPickedLabelColor( Color.WHITE );
				graph.setVertexPaint( Color.ORANGE.darker( ));
				graph.setPickedVertexPaint( Color.RED );
				graph.setPickedEdgePaint( Color.WHITE );
				heatMapPanel.setBackground( Color.BLACK );
				heatMapPanel.setForeground( Color.WHITE );
			}
			else if ( item == this.normalColorMenuItem ) {
				graph.setBackground( null );
				graph.setForeground( Color.BLACK );
				graph.setPickedLabelColor( Color.BLUE );
				graph.setVertexPaint( Color.ORANGE );
				graph.setPickedVertexPaint( Color.YELLOW );
				graph.setPickedEdgePaint( Color.BLACK );
				heatMapPanel.setForeground( Color.BLACK );
				heatMapPanel.setBackground( null );
			}

		}
	}

	/**
	 * The actionPerformed method of the ActionListener interface.
	 * 
	 * @param event The event which triggered this action.
	 */
	public void actionPerformed( ActionEvent event ) {
		Component item = ( Component )event.getSource( );
			PickedState <Molecule> pickedVertexState = this.graph.getPickedVertexState( );
			PickedState <Correlation> pickedEdgeState = this.graph.getPickedEdgeState( );
			Collection <Molecule> vertices = new Vector( this.graph.getVertices( ));
			Collection <Correlation> edges = new Vector( this.graph.getEdges( ));
		if ( item == this.selectAllViewMenuItem ) {
			this.graph.selectAll( );
		} else if ( item == this.clearSelectionViewMenuItem ) {
			this.graph.clearSelection( );
		}	else if ( item == this.zoomInViewMenuItem ) {
			this.visibleGraph.scale( 1.25f );
		} else if ( item == this.zoomOutViewMenuItem ) {
			this.visibleGraph.scale( 0.8f );
		} else if ( item == this.fitToWindowViewMenuItem ) {
			this.visibleGraph.scale( 0.99f );
		} else if ( item == this.invertSelectionViewMenuItem ) {
			for ( Molecule m : vertices )
				pickedVertexState.pick( m, !pickedVertexState.isPicked( m ));
			for ( Correlation c : edges )
				pickedEdgeState.pick( c, !pickedEdgeState.isPicked( c ));
		} else if ( item == this.selectCorrelatedViewMenuItem ) {
			Collection <Molecule> picked = new Vector( pickedVertexState.getPicked( ));
			for ( Molecule m : picked ) {
				for ( Correlation c : graph.getIncidentEdges( m )) {
					pickedEdgeState.pick( c, true );
					pickedVertexState.pick( c.getOpposite( m ), true );
				}
			}
		} else if ( item == this.hideSelectedViewMenuItem ) {
			for ( Molecule m : new Vector<Molecule>( pickedVertexState.getPicked( )))
				this.graph.removeVertex( m );
		} else if ( item == this.hideUnselectedViewMenuItem ) {
			for ( Molecule m : vertices ) {
				if ( !pickedVertexState.isPicked( m ))
					this.graph.removeVertex( m );
			}
		} else if ( item == this.hideUncorrelatedViewMenuItem ) {
			boolean keep;
			for( Molecule m : vertices ) {
				keep = false;
				for ( Correlation c : graph.getIncidentEdges( m )) {
					keep = keep || 
						pickedVertexState.isPicked( m ) || 
						pickedVertexState.isPicked( c.getOpposite( m ));
				}
				if ( !keep ) {
					this.graph.removeVertex( m );
				}
			}

		} else if ( item == this.hideOrphansViewMenuItem ) {
			for ( Molecule m : vertices ) {
				if ( this.graph.getNeighborCount( m ) == 0 )
					this.graph.removeVertex( m );
			}
		} else if ( item == this.showCorrelatedViewMenuItem ) {
			for ( Molecule m : this.experiment.getMolecules( )) {
				for ( Correlation c : m.getCorrelations( )) {
					if ( this.getCorrelationRange( ).contains( c.getValue( ))
						&& !vertices.contains( m )
						&& vertices.contains( c.getOpposite( m )))
						this.graph.addVertex( m );
				}
			}
		}
	}

	// ******************* PRIVATE/PROTECTED CLASSES **************************

	private class MoleculeFilterPanel extends JPanel implements ItemListener,ActionListener,GraphItemChangeListener<Molecule> {
		private JButton noneButton = new JButton( "None" );
		private JButton allButton = new JButton( "All" );
		private JPanel moleculeList = new JPanel( new GridLayout( 0, 1 ));
		private JScrollPane moleculeScrollPane = new JScrollPane( this.moleculeList );
		private HashMap<Molecule,JCheckBox> checkBoxMap = 
			new HashMap<Molecule,JCheckBox>( );
		private HashMap<JCheckBox,Molecule> moleculeMap =
			new HashMap<JCheckBox,Molecule>( );
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
			JCheckBox cb = new JCheckBox( m.toString( ), true );
			this.moleculeList.add( cb );
			this.checkBoxMap.put( m, cb );
			this.moleculeMap.put( cb, m );
			cb.addItemListener( this );
		}

		/**
		 * Returns An arrayList of MoleculeCheckBoxes.
		 * 
		 * @return an ArrayList of MoleculeCheckBoxes from the display panel
		 */
		public Collection <JCheckBox> getCheckBoxes( ) {
			return this.checkBoxMap.values( );
		}

		//for the checkboxes
		/**
		 * The itemStateChanged method of the ItemListener interface.
		 * 
		 * @param event the event which triggered this action.
		 */
		public void itemStateChanged( ItemEvent event ) {
			synchronized( graph.getGraph( )) {
				Molecule molecule = moleculeMap.get( event.getSource( ));
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
					graph.getPickedVertexState( ).pick( molecule, false );
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
		 * @param state True for checked, false for unchecked.
		 */
		private void setAll( boolean state ) {
			for ( Molecule m : this.checkBoxMap.keySet( )) {
				this.set( m, state );
			}
		}

		private void set( Molecule m, boolean state ) {
			JCheckBox cb = this.checkBoxMap.get( m );
			cb.setSelected( state );
			// fire the listeners
			for ( ItemListener i : cb.getItemListeners( )) {
				i.itemStateChanged( new ItemEvent(
					cb, -1, m,
					state ? ItemEvent.SELECTED : ItemEvent.DESELECTED
				));
			}
		}

		public void filter( String filter ) {
			Pattern p = Pattern.compile( filter );
			for( JCheckBox cb : this.getCheckBoxes( )) {
				cb.setVisible( p.matcher( this.moleculeMap.get( cb ).toString( ) ).matches( ));
			}
		}

		/**
		 * The stateChanged method of the GraphItemChangeListener interface.
		 * 
		 * @param event The GraphItemChangeEvent which triggered this action.
		 */
		public void stateChanged( GraphItemChangeEvent<Molecule> event ) {
			JCheckBox cb = this.checkBoxMap.get(event.getItem( ));
			int change = event.getAction( );
			if ( change == GraphItemChangeEvent.REMOVED )
				cb.setSelected( false );
			else if ( change == GraphItemChangeEvent.ADDED )
				cb.setSelected( true );
		}
		
	}

	/**
	 * A class for displaying the table below the graph.
	 */
	private class InfoPanel extends JTabbedPane {
		private final static String MOLECULE_INDEX = "id";
		private JTable moleculeTable = new JTable(0,0) {
			public boolean isCellEditable( int row, int col ) {
				return false;
			}
		};
		private JTable correlationTable = new JTable(0,0) {
			public boolean isCellEditable( int row, int col ) {
				return false;
			}
		};

		/**
		 * Creates a new InfoPanel.
		 */
		public InfoPanel( ) {
			super( );
			this.moleculeTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
			this.correlationTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
			this.add( new JScrollPane( moleculeTable ), "Molecules" );
			this.add( new JScrollPane( correlationTable ), "Correlations" );
		}

		/**
		 * Adds a new Molecule to the table.
		 * 
		 * @param molecule The new molecule to be added.
		 */
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

		/**
		 * Adds a new Correlation to the table.
		 * 
		 * @param correlation The new Correlation to be added.
		 */
		public void add( Correlation correlation ) {
			DefaultTableModel tm = (DefaultTableModel)this.correlationTable.getModel( );
			if ( tm.getColumnCount( ) == 0 ) {
				String [ ] keys = { "Molecule 1", "Molecule 2", "Pearson Value", "Spearman Rank Value",
					"Kendall Tau-b Rank Value" };
				for ( String key : keys ) {
					tm.addColumn( key );
				}
				Enumeration <TableColumn> columns = this.correlationTable.getColumnModel( ).getColumns( );
				while( columns.hasMoreElements( ))
					columns.nextElement( ).setPreferredWidth( 175 );
			}
			Object[] newRow = new Object[ 5 ];
			newRow[ 0 ] = correlation.getMolecules( )[ 0 ].getAttribute( MOLECULE_INDEX );
			newRow[ 1 ] = correlation.getMolecules( )[ 1 ].getAttribute( MOLECULE_INDEX );
			newRow[ 2 ] = String.format( "%.5f", correlation.getValue( Correlation.PEARSON ));
			newRow[ 3 ] = String.format( "%.5f", correlation.getValue( Correlation.SPEARMAN ));
			newRow[ 4 ] = String.format( "%.5f", correlation.getValue( Correlation.KENDALL ));
			tm.addRow( newRow );
		}

		/**
		 * Removes a Molecule from the table. Does nothing if the Molecule is not present.
		 * 
		 * @param molecule the Molecule to be removed.
		 */
		public void remove( Molecule molecule ) {
			int row = this.getRowOf( molecule );
			if ( row >= 0 )
				((DefaultTableModel)this.moleculeTable.getModel( )).removeRow( row );
		}

		/**
		 * Removes a Correlation from the table. Does nothing if the Correlation is not present.
		 * 
		 * @param correlation the Correlation to be added.
		 */
		public void remove( Correlation correlation ) {
			int row = getRowOf( correlation );
			if ( row >= 0 )
				((DefaultTableModel)this.correlationTable.getModel( )).removeRow( row );
		}

		/**
		 * Clears the Molecule table.
		 */
		public void clearMolecules( ) {
			DefaultTableModel tm = (DefaultTableModel)this.moleculeTable.getModel( );
			while( tm.getRowCount( ) > 0 ) {
				tm.removeRow( 0 );
			}
		}

		/**
		 * Clears tht Correlation Table.
		 */
		public void clearCorrelations( ) {
			DefaultTableModel tm = (DefaultTableModel)this.correlationTable.getModel( );
			while( tm.getRowCount( ) > 0 ) {
				tm.removeRow( 0 );
			}
		}

		/**
		 * Finds the appropriate row in the table for the given Molecule.
		 * 
		 * @param Molecule The Molecule to search for.
		 * @return The row number of the Molecule.
		 */
		private int getRowOf( Molecule molecule ) {
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

		/**
		 * Finds the apropriate row in the table for the given Correlation.
		 * 
		 * @param correlation The Correlation to search for.
		 * @return The row number of the Correlation.
		 */
		private int getRowOf( Correlation correlation ) {
			DefaultTableModel tm = (DefaultTableModel)this.correlationTable.getModel( );
			int returnValue = 0;
			while( returnValue < tm.getRowCount( )) {
				if ( correlation.getMolecules( )[ 0 ].getAttribute( MOLECULE_INDEX ).equals( 
					tm.getValueAt( returnValue, 0 )) && 
					correlation.getMolecules( )[ 1 ].getAttribute( MOLECULE_INDEX ).equals(
					tm.getValueAt( returnValue, 1 )))
					return returnValue;
				returnValue++;
			}
			return -1;

		}

	}

	/**
	 * A class which listens for a change in the state of the calculation menu.
	 * @todo Integrate this listener with the CorrelationDisplayPanel class.
	 */
	private class CalculationChangeListener implements ItemListener {

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
	private class MenuItemListener implements ActionListener {

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
	private class LayoutChangeListener implements ActionListener {

			/**
			 * The actionPerformed method of the ActionListener interface.
			 * 
			 * @param event The event which triggered this action.
			 */
			public void actionPerformed( ActionEvent event ) {
				Component item = ( Component )event.getSource( );

				if ( item == animatedLayoutMenuItem ) {
					graph.animate( animatedLayoutMenuItem.getState( ));
				} else {
					graph.animate( false );
					graph.resetView( );
					if ( item == multipleCirclesLayoutMenuItem )
						setGraphLayout( MultipleCirclesLayout.class );
					else if ( item == singleCircleLayoutMenuItem )
						setGraphLayout( CircleLayout.class );
					else if ( item == randomLayoutMenuItem )
						setGraphLayout( RandomLayout.class );
					else if ( item == kkLayoutMenuItem )
						setGraphLayout( KKLayout.class );
					else if ( item == frLayoutMenuItem )
						setGraphLayout( FRLayout.class );
					else if ( item == springLayoutMenuItem )
						setGraphLayout( SpringLayout2.class );
					else if ( item == heatMapLayoutMenuItem )
						heatMap( );

				}
			}
	}
}



