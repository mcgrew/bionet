/*

This file is part of BioNet.

BioNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BioNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with BioNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.bionet.ui;

import edu.purdue.bbc.util.DaemonListener;
import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.MutableNumber;
import edu.purdue.bbc.util.NumberList;
import edu.purdue.bbc.util.Pair;
import edu.purdue.bbc.util.Range;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.SimplePair;
import edu.purdue.bbc.util.UpdateDaemon;
import edu.purdue.cc.bionet.io.DataReader;
import edu.purdue.cc.bionet.io.SaveImageAction;
import edu.purdue.cc.bionet.ui.layout.LayoutAnimator;
import edu.purdue.cc.bionet.ui.layout.CenterLayout;
import edu.purdue.cc.bionet.ui.layout.MultipleCirclesLayout;
import edu.purdue.cc.bionet.ui.layout.RandomLayout;
import edu.purdue.cc.bionet.ui.renderer.FastEdgeRenderer;
import edu.purdue.cc.bionet.util.Correlation;
import edu.purdue.cc.bionet.util.CorrelationSet;
import edu.purdue.cc.bionet.util.ExperimentSet;
import edu.purdue.cc.bionet.util.Molecule;
import edu.purdue.cc.bionet.util.MonitorableRange;
import edu.purdue.cc.bionet.util.Sample;
import edu.purdue.cc.bionet.util.SampleGroup;
import edu.purdue.cc.bionet.util.SampleGroupChangeEvent;
import edu.purdue.cc.bionet.util.SampleGroupChangeListener;
import edu.purdue.cc.bionet.util.Spectrum;
import edu.purdue.cc.bionet.util.SplitSpectrum;

import java.io.IOException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.LayoutDecorator;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

/**
 * A class for displaying and interacting with Correlation data for a set of 
 * molecules.
 */
public class CorrelationDisplayPanel extends AbstractDisplayPanel 
		implements ActionListener,ChangeListener,ItemListener {

	private JMenuBar menuBar;

	// correlationMethod menu items
	private JMenu correlationMethodMenu;
	private ButtonGroup correlationMethodMenuButtonGroup;
	private JRadioButtonMenuItem pearsonCalculationMenuItem;
	private JRadioButtonMenuItem spearmanCalculationMenuItem;
	private JRadioButtonMenuItem kendallCalculationMenuItem;

	// layout menu itmes
	private JMenu layoutMenu;
	private ButtonGroup layoutMenuButtonGroup;
	private JRadioButtonMenuItem multipleCirclesLayoutMenuItem;
	private JRadioButtonMenuItem singleCircleLayoutMenuItem;
	private JRadioButtonMenuItem randomLayoutMenuItem;
	private JRadioButtonMenuItem heatMapLayoutMenuItem;
	private JRadioButtonMenuItem kkLayoutMenuItem;
//	private JRadioButtonMenuItem frLayoutMenuItem;
//	private JRadioButtonMenuItem springLayoutMenuItem;
	private JRadioButtonMenuItem frSpringLayoutMenuItem;
//	private JCheckBoxMenuItem animatedLayoutMenuItem;

	// view menu items
	private JMenu viewMenu;
	private JMenuItem zoomInViewMenuItem;
	private JMenuItem zoomOutViewMenuItem;
	private JMenuItem fitToWindowViewMenuItem;
	private JMenuItem selectAllViewMenuItem;
	private JMenuItem clearSelectionViewMenuItem;
	private JMenuItem invertSelectionViewMenuItem;
	private JMenuItem selectCorrelatedViewMenuItem;
	private JMenuItem hideSelectedViewMenuItem;
	private JMenuItem hideUnselectedViewMenuItem;
	private JMenuItem hideUncorrelatedViewMenuItem;
	private JMenuItem hideOrphansViewMenuItem;
	private JMenuItem showCorrelatedViewMenuItem;
	private SaveImageAction saveImageAction;

	// groups menu items
	private JMenu groupsMenu;
	private JMenuItem resetSampleGroupsMenuItem;
	private JMenuItem chooseSampleGroupsMenuItem;
	private JMenuItem setFoldChangeGroupsMenuItem;

	
	// color menu items
	private JMenu colorMenu;
	private ButtonGroup colorMenuButtonGroup;
	private JRadioButtonMenuItem normalColorMenuItem;
	private JRadioButtonMenuItem highContrastColorMenuItem;

	private MoleculeFilterPanel moleculeFilterPanel;
	private CorrelationFilterPanel correlationFilterPanel;

	private CorrelationGraphVisualizer graph;
	private Layout <Molecule,Correlation> layout; //Graph Layout
	private InfoPanel infoPanel;
	private JSplitPane graphSplitPane;
	private HeatMap  heatMapPanel;

	private ExperimentSet experiment;
	private Collection<Molecule> molecules;
	private Collection<Sample> samples;
	private CorrelationSet correlations;
	private String title;
	private Scalable visibleGraph;
	private MutableNumber correlationMethod;

	/**
	 * Creates a new CorrelationDisplayPanel. 
	 */
	public CorrelationDisplayPanel( ) {
		super( new BorderLayout( ) );
		this.menuBar = new JMenuBar( );
		this.graphSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		this.correlationMethod = new MutableNumber( Correlation.PEARSON );
		this.buildPanel( );
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

		Language language = Settings.getLanguage( );
		this.correlationMethodMenu = new JMenu( language.get( "Correlation Method" ));
		this.correlationMethodMenuButtonGroup = new ButtonGroup( );
		this.pearsonCalculationMenuItem = 
			new JRadioButtonMenuItem( language.get( "Pearson" ), true );
		this.spearmanCalculationMenuItem = 
			new JRadioButtonMenuItem( language.get( "Spearman" ));
		this.kendallCalculationMenuItem = 
			new JRadioButtonMenuItem( language.get( "Kendall" ));

		// layout menu itmes
		this.layoutMenu = new JMenu( language.get( "Layout" ));
		this.layoutMenuButtonGroup = new ButtonGroup( );
		this.multipleCirclesLayoutMenuItem = 
			new JRadioButtonMenuItem( language.get( "Multiple Circles" ));
		this.singleCircleLayoutMenuItem = 
			new JRadioButtonMenuItem( language.get( "Single Circle" ), true);
		this.randomLayoutMenuItem = 
			new JRadioButtonMenuItem( language.get( "Random" ));
		this.heatMapLayoutMenuItem = 
			new JRadioButtonMenuItem( language.get( "Heat Map" ));
		this.kkLayoutMenuItem = 
			new JRadioButtonMenuItem( language.get( "Kamada-Kawai" ));
//		this.frLayoutMenuItem = 
//				new JRadioButtonMenuItem( language.get( "Fruchterman-Reingold" ));
//		this.springLayoutMenuItem = 
//					new JRadioButtonMenuItem( language.get( "Spring Layout" ));
		this.frSpringLayoutMenuItem = 
					new JRadioButtonMenuItem( language.get( "Spring Layout" ));
//		this.animatedLayoutMenuItem = new JCheckBoxMenuItem( 
//			language.get( "Fruchterman-Reingold Spring Embedding" ));

		// view menu items
		this.viewMenu = new JMenu( language.get( "View" ));
		this.zoomInViewMenuItem = 
			new JMenuItem( language.get( "Zoom In" ), KeyEvent.VK_I );
		this.zoomOutViewMenuItem = 
			new JMenuItem( language.get( "Zoom Out" ), KeyEvent.VK_O );
		this.fitToWindowViewMenuItem = 
			new JMenuItem( language.get( "Fit to Window" ), KeyEvent.VK_F );
		this.selectAllViewMenuItem = 
			new JMenuItem( language.get( "Select All" ), KeyEvent.VK_A );
		this.clearSelectionViewMenuItem = 
			new JMenuItem( language.get( "Clear Selection" ), KeyEvent.VK_C );
		this.invertSelectionViewMenuItem = 
			new JMenuItem( language.get( "Invert Selection" ), KeyEvent.VK_I );
		this.selectCorrelatedViewMenuItem = new JMenuItem( 
			language.get( "Select Correlated to Selection" ), KeyEvent.VK_R );
		this.hideSelectedViewMenuItem = 
			new JMenuItem( language.get( "Hide Selected" ), KeyEvent.VK_H );
		this.hideUnselectedViewMenuItem = 
			new JMenuItem( language.get( "Hide Unselected" ), KeyEvent.VK_U );
		this.hideUncorrelatedViewMenuItem = new JMenuItem( 
			language.get( "Hide Uncorrelated to Selection" ), KeyEvent.VK_L );
		this.hideOrphansViewMenuItem = 
			new JMenuItem( language.get( "Hide Orphans" ), KeyEvent.VK_P );
		this.showCorrelatedViewMenuItem = new JMenuItem( 
			language.get( "Show All Correlated to Visible" ), KeyEvent.VK_S );
		this.saveImageAction = new SaveImageAction( 
			language.get( "Save Main Graph Image" ) + "..." , null );

		// groups menu items
		this.groupsMenu = new JMenu( language.get( "Groups" ));
		this.resetSampleGroupsMenuItem = 
			new JMenuItem( language.get( "Reset Sample Groups" ), KeyEvent.VK_R );
		this.chooseSampleGroupsMenuItem = 
			new JMenuItem( language.get( "Choose Sample Groups" ) + "...", 
			               KeyEvent.VK_C );
		this.setFoldChangeGroupsMenuItem = 
			new JMenuItem( language.get( "Set Fold Change" )+"...", KeyEvent.VK_F );
		
		// color menu items
		this.colorMenu = new JMenu( language.get( "Color" ));
		this.colorMenuButtonGroup = new ButtonGroup( );
		this.normalColorMenuItem = 
			new JRadioButtonMenuItem( language.get( "Normal Color" ), true );
		this.highContrastColorMenuItem = 
			new JRadioButtonMenuItem( language.get( "High Contrast Color" ));

		// CORRELATION FILTER ELEMENTS
		JPanel leftPanel = new JPanel( new BorderLayout( ));
		this.moleculeFilterPanel = new MoleculeFilterPanel( );
		leftPanel.add( moleculeFilterPanel, BorderLayout.CENTER );
		this.correlationFilterPanel = new CorrelationFilterPanel( );
		leftPanel.add( this.correlationFilterPanel, BorderLayout.SOUTH );

		//CALCULATION MENU
		this.correlationMethodMenu.setMnemonic( KeyEvent.VK_C );
		this.correlationMethodMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "Perform Data Calculations" ));
		this.correlationMethodMenuButtonGroup.add( this.pearsonCalculationMenuItem );
		this.correlationMethodMenuButtonGroup.add( this.spearmanCalculationMenuItem );
		this.correlationMethodMenuButtonGroup.add( this.kendallCalculationMenuItem );
		this.pearsonCalculationMenuItem.setMnemonic( KeyEvent.VK_P );
		this.spearmanCalculationMenuItem.setMnemonic( KeyEvent.VK_S );
		this.kendallCalculationMenuItem.setMnemonic( KeyEvent.VK_K );
		this.correlationMethodMenu.add( this.pearsonCalculationMenuItem );
		this.correlationMethodMenu.add( this.spearmanCalculationMenuItem );
		this.correlationMethodMenu.add( this.kendallCalculationMenuItem );
		this.pearsonCalculationMenuItem.addItemListener( this );
		this.spearmanCalculationMenuItem.addItemListener( this ); 
		this.kendallCalculationMenuItem.addItemListener( this );

		//LAYOUT MENU
		LayoutChangeListener lcl = new LayoutChangeListener( );
		this.layoutMenu.setMnemonic( KeyEvent.VK_L );
		this.layoutMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "Change the layout of the graph" ));
		this.layoutMenuButtonGroup.add( this.multipleCirclesLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.singleCircleLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.randomLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.kkLayoutMenuItem );
//		this.layoutMenuButtonGroup.add( this.frLayoutMenuItem );
//		this.layoutMenuButtonGroup.add( this.springLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.frSpringLayoutMenuItem );
		this.layoutMenuButtonGroup.add( this.heatMapLayoutMenuItem );
		
		Enumeration<AbstractButton> e = this.layoutMenuButtonGroup.getElements( );
		this.layoutMenu.add( this.multipleCirclesLayoutMenuItem );
		this.layoutMenu.add( this.singleCircleLayoutMenuItem );
		this.layoutMenu.add( this.randomLayoutMenuItem );
		this.layoutMenu.add( this.kkLayoutMenuItem );
//		this.layoutMenu.add( this.frLayoutMenuItem );
//		this.layoutMenu.add( this.springLayoutMenuItem );
		this.layoutMenu.add( this.frSpringLayoutMenuItem );
		this.layoutMenu.add( this.heatMapLayoutMenuItem );
//		this.layoutMenu.addSeparator( );
//		this.layoutMenu.add( this.animatedLayoutMenuItem );
		this.multipleCirclesLayoutMenuItem.addActionListener( lcl );
		this.multipleCirclesLayoutMenuItem.setEnabled( false );
		this.singleCircleLayoutMenuItem.addActionListener( lcl );
		this.randomLayoutMenuItem.addActionListener( lcl );
		this.kkLayoutMenuItem.addActionListener( lcl );
//		this.frLayoutMenuItem.addActionListener( lcl );
		this.frSpringLayoutMenuItem.addActionListener( lcl );
		this.heatMapLayoutMenuItem.addActionListener( lcl );
//		this.animatedLayoutMenuItem.addActionListener( lcl );

		//VIEW MENU
		this.viewMenu.add( this.colorMenu );
		this.viewMenu.addSeparator( );
		this.viewMenu.setMnemonic( KeyEvent.VK_V );
		this.viewMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "Change the data view settings" ));
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
		this.viewMenu.addSeparator( );
		this.viewMenu.add( this.saveImageAction );
		this.resetSampleGroupsMenuItem.addActionListener( this );
		this.chooseSampleGroupsMenuItem.addActionListener( this );
		this.setFoldChangeGroupsMenuItem.addActionListener( this );
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

		// GROUPS MENU
		this.groupsMenu.setMnemonic( KeyEvent.VK_G );
		this.groupsMenu.add( this.resetSampleGroupsMenuItem );
		this.groupsMenu.add( this.chooseSampleGroupsMenuItem );
		this.groupsMenu.add( this.setFoldChangeGroupsMenuItem );

		this.zoomOutViewMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK ));
		this.zoomInViewMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK ));
		this.fitToWindowViewMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK ));
		this.hideSelectedViewMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0 ));

		//COLOR MENU
		this.colorMenu.setMnemonic( KeyEvent.VK_R );
		this.colorMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "Change the color of the graph" ));
		this.colorMenuButtonGroup.add( this.normalColorMenuItem );
		this.colorMenuButtonGroup.add( this.highContrastColorMenuItem );
		this.colorMenu.add( this.normalColorMenuItem );
		this.colorMenu.add( this.highContrastColorMenuItem );
		this.normalColorMenuItem.addItemListener( this );
		this.highContrastColorMenuItem.addItemListener( this );

		this.menuBar.add( this.correlationMethodMenu );
		this.menuBar.add( this.layoutMenu );
		this.menuBar.add( this.viewMenu );
		this.menuBar.add( this.groupsMenu );

		// Add the panels to the main panel
		this.add( menuBar, BorderLayout.NORTH );
//		this.add( this.correlationViewPanel, BorderLayout.CENTER );
		this.add( leftPanel, BorderLayout.WEST );
	}

	/**
	 * Creates a Correlation Graph.
	 * 
	 * @param experiment An ExperimentSet Object containing the data to be used.
	 */
	public boolean createView( ExperimentSet experiment ) {
		if ( experiment.size( ) == 0 ) {
			return false;
		}
		this.setBackground( Color.WHITE );
		this.setVisible( true );
		this.title = Settings.getLanguage( ).get( "Correlation Network" );
		this.experiment = experiment;
		this.molecules = new TreeSet<Molecule>( );
		this.molecules.addAll( experiment.getMolecules( ));
		this.samples = new TreeSet<Sample>( );
		this.correlations = new CorrelationSet( this.molecules, this.samples );
		this.samples.addAll( experiment );
		for( Molecule molecule : experiment.getMolecules( )) {
			this.moleculeFilterPanel.add( molecule );
			this.molecules.add( molecule );
			this.correlations.add( molecule );
		}

		this.graph = new CorrelationGraphVisualizer( 
			this.correlations, this.correlationFilterPanel.getMonitorableRange( ));
		this.addSampleGroupChangeListener( this.graph );
		this.saveImageAction.setComponent( this.graph );
		this.graph.setBackground( this.getBackground( ));
		this.graph.setIndicateCommonNeighbors( true );
		this.infoPanel = new InfoPanel( );
		this.addSampleGroupChangeListener( this.infoPanel );
		this.graph.addGraphMouseListener( new CorrelationGraphMouseListener( ));

		this.graph.addGraphMouseEdgeListener( 
			new GraphMouseListener<Correlation>( ) {

				public void graphClicked( Correlation c, MouseEvent e ) {
					if ( e.getButton( ) == MouseEvent.BUTTON1 && 
					     e.getClickCount( ) >= 2 ) {
						new DetailWindow( correlations, 
															c, correlationFilterPanel.getRange( ),
															correlationMethod.intValue( ));
					}
				}
				public void graphPressed(  Correlation c, MouseEvent e ) { } 
				public void graphReleased( Correlation c, MouseEvent e ) { }
		});

		Collection<SampleGroup> sampleGroups = new ArrayList<SampleGroup>( );
		sampleGroups.add( new SampleGroup( "All Samples", this.samples ));
		this.setSampleGroups( sampleGroups );

		this.add( this.graphSplitPane, BorderLayout.CENTER );
		this.graphSplitPane.setBottomComponent( this.infoPanel );
		this.setGraphVisualizer( this.graph );

		this.heatMapPanel = new HeatMap( this.getTitle( ), 
		                                 this.correlations,
                                     this.molecules,
		                                 this.getCorrelationRange( ),
		                                 this.correlationMethod );
		this.graph.addVertexChangeListener( this.heatMapPanel );
		this.pearsonCalculationMenuItem.addChangeListener( this.heatMapPanel );
		this.spearmanCalculationMenuItem.addChangeListener( this.heatMapPanel );
		this.kendallCalculationMenuItem.addChangeListener( this.heatMapPanel );
		this.addComponentListener( new InitialSetup( ));
		return true;
	}

	// ============================ InitialSetup =================================
	/**
	 * A class for handling the sizng of panels and such in 
	 * ClusteringDisplayPanel when it is initially shown.
	 */
	private class InitialSetup extends ComponentAdapter {
		public InitialSetup( ) {
			super( );
		}

		/**
		 * Fired when the component is shown. Sets up a few things 
		 * based on the overall size of the panel when it is made visible, then
		 * unregisters itself as a listener so it is only fired once.
		 * 
		 * @param e The event which triggered this action.
		 */
		public void componentShown( ComponentEvent e ) {
			Component source = e.getComponent( );
			graphSplitPane.setDividerLocation( source.getHeight( ) - 300 );
			setGraphLayout( CircleLayout.class );
			// we only need to perform this action once.
			source.removeComponentListener( this );
		}
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
			this.setVisibleGraph( this.graph );
			this.selectAllViewMenuItem.setEnabled( true );
			this.clearSelectionViewMenuItem.setEnabled( true );
			this.invertSelectionViewMenuItem.setEnabled( true );
			this.selectCorrelatedViewMenuItem.setEnabled( true );
			this.hideSelectedViewMenuItem.setEnabled( true );
			this.hideUnselectedViewMenuItem.setEnabled( true );
			this.hideUncorrelatedViewMenuItem.setEnabled( true );
//			this.animatedLayoutMenuItem.setEnabled( true );
			this.validate( );
			this.graphSplitPane.repaint( );
		}
		this.graph.setGraphLayout( layout );
	}

	/**
	 * Switches the view layout to heat map.
	 */
	private void heatMap( ) {
		this.selectAllViewMenuItem.setEnabled( false );
		this.clearSelectionViewMenuItem.setEnabled( false );
		this.invertSelectionViewMenuItem.setEnabled( false );
		this.selectCorrelatedViewMenuItem.setEnabled( false );
		this.hideSelectedViewMenuItem.setEnabled( false );
		this.hideUnselectedViewMenuItem.setEnabled( false );
		this.hideUncorrelatedViewMenuItem.setEnabled( false );
//		this.animatedLayoutMenuItem.setEnabled( false );
		this.remove( this.graphSplitPane );
		this.add( this.heatMapPanel.getScrollPane( ), BorderLayout.CENTER );
		this.setVisibleGraph( this.heatMapPanel );
		this.validate( );
		this.heatMapPanel.getScrollPane( ).repaint( );
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
		this.setVisibleGraph( this.graph );
		this.graph.addAnimationListener( this );
		this.graph.addVertexChangeListener( this.moleculeFilterPanel );
		v.addPickedVertexStateChangeListener( 
			new PickedStateChangeListener <Molecule> ( ){
				public void stateChanged( PickedStateChangeEvent <Molecule> event ) {
					if ( event.getStateChange( ))
						infoPanel.add( event.getItem( ));
					else
						infoPanel.remove( event.getItem( ));
				}
		});
		v.addPickedEdgeStateChangeListener( 
			new PickedStateChangeListener <Correlation> ( ){
				public void stateChanged( PickedStateChangeEvent <Correlation> event ) {
					if ( event.getStateChange( ))
						infoPanel.add( event.getItem( ));
					else
						infoPanel.remove( event.getItem( ));
				}
		});

	}
  

	private void setVisibleGraph( Scalable graph ) {
		this.visibleGraph = graph;
		this.saveImageAction.setComponent( (Component)graph );
	}
	
	/**
	 * Returns the ExperimentSet associated with this CorrelationDisplayPanel.
	 * 
	 * @return The associated ExperimentSet
	 */
	public ExperimentSet getExperiment( ) {
		return this.experiment;
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
	 * The stateChanged method of the ChangeListener interface.
	 * @see ChangeListener#stateChanged(ChangeEvent)
	 * 
	 * @param event The event which triggered this action.
	 */
	public void stateChanged( ChangeEvent event ) {
//		LayoutAnimator animator = (LayoutAnimator)event.getSource( );
//		if ( animator.isStopped( ))
//			this.animatedLayoutMenuItem.setState( false );
	}

	/**
	 * The itemStateChanged method of the ItemListener interface.
	 * @see ItemListener#itemStateChanged(ItemEvent)
	 * 
	 * @param e The event which triggered this action.
	 */
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
			} else if ( item == this.normalColorMenuItem ) {
				graph.setBackground( Color.WHITE );
				graph.setForeground( Color.BLACK );
				graph.setPickedLabelColor( Color.BLUE );
				graph.setVertexPaint( Color.ORANGE );
				graph.setPickedVertexPaint( Color.YELLOW );
				graph.setPickedEdgePaint( Color.BLACK );
				heatMapPanel.setForeground( Color.BLACK );
				heatMapPanel.setBackground( Color.WHITE );
			} else if ( item == pearsonCalculationMenuItem ) {
					correlationMethod.setValue( Correlation.PEARSON );
			} else if ( item == spearmanCalculationMenuItem ) {
					correlationMethod.setValue( Correlation.SPEARMAN );
			} else if ( item == kendallCalculationMenuItem ) {
					correlationMethod.setValue( Correlation.KENDALL );
			}
			graph.filterEdges( );
		}
	}

	/**
	 * The actionPerformed method of the ActionListener interface.
	 * 
	 * @param event The event which triggered this action.
	 */
	public void actionPerformed( ActionEvent event ) {
		Component item = ( Component )event.getSource( );
		Language language = Settings.getLanguage( );
			PickedState <Molecule> pickedVertexState = this.graph.getPickedVertexState( );
			PickedState <Correlation> pickedEdgeState = this.graph.getPickedEdgeState( );
			Collection <Molecule> vertices = new Vector( this.graph.getVertices( ));
      Collection <Correlation> edges;
      synchronized( this.graph ) {
        edges = new Vector( this.graph.getEdges( ));
      }
		if ( item == this.selectAllViewMenuItem ) {
			this.graph.selectAll( );
		} else if ( item == this.clearSelectionViewMenuItem ) {
			this.graph.clearSelection( );
		}	else if ( item == this.zoomInViewMenuItem ) {
			this.visibleGraph.scale( 1.25f );
		} else if ( item == this.zoomOutViewMenuItem ) {
			this.visibleGraph.scale( 0.8f );
		} else if ( item == this.fitToWindowViewMenuItem ) {
			this.visibleGraph.scaleTo( 0.99f );
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
			this.graph.setIgnoreRepaint( true );
			for ( Correlation c : this.correlations ){
				if ( this.getCorrelationRange( ).contains( 
					   c.getValue( this.correlationMethod )) &&
				     ( vertices.contains( c.getFirst( ) ) ||
						   vertices.contains( c.getSecond( ) ))) {
					this.graph.addVertex( c.getFirst( ));
					this.graph.addVertex( c.getSecond( ));
				}
			}
			this.graph.setIgnoreRepaint( false );
		} else if ( item == this.chooseSampleGroupsMenuItem ) {
			Component frame = this;
			while( !(frame instanceof Frame) && frame != null ) {
				frame = frame.getParent( );
			}
			this.setSampleGroups( SampleGroupDialog.showInputDialog( 
					(Frame)frame, language.get( "Choose groups" ), 
					this.samples));
			if ( this.getSampleGroups( ).size( ) > 1 ) {
				this.infoPanel.repaint( );
				Logger logger = Logger.getLogger( getClass( ));
				Iterator<SampleGroup> iterator = this.getSampleGroups( ).iterator( );
				SampleGroup group = iterator.next( );
				logger.debug( group.toString( ));
				for ( Sample sample : group ) {
					logger.debug( "\t" + sample.toString( ));
				}
				group = iterator.next( );
				logger.debug( group.toString( ));
				for ( Sample sample : group ) {
					logger.debug( "\t" +  sample.toString( ));
				}
			}
		} else if ( item == this.resetSampleGroupsMenuItem ) {
			Collection<SampleGroup> sampleGroups = new ArrayList<SampleGroup>( );
			sampleGroups.add( new SampleGroup( 
				language.get( "All samples" ), this.samples ));
			this.setSampleGroups( sampleGroups );
			this.infoPanel.repaint( );
		} else if ( item == this.setFoldChangeGroupsMenuItem ) {
			Settings settings = Settings.getSettings( );
			double newFold = 0.0;
      Component owner = this;
      while(!( owner instanceof Frame )) {
        owner = owner.getParent( );
      }
			FoldChangeDialog newFoldEntry = 
				FoldChangeDialog.showInputDialog( (Frame)owner, 
					language.get( "Enter new fold change value" ),
					settings.getDouble( "preferences.correlation.foldChange", 2.0 ),
					settings.getBoolean( "preferences.correlation.logData", false ));
			if ( newFoldEntry != null ) {
					settings.setDouble( "preferences.correlation.foldChange", 
                              newFoldEntry.getFoldChange( ));
					settings.setBoolean( "preferences.correlation.logData", 
                               newFoldEntry.isLogData( ));
					this.repaint( );
			}
		}
	}

	public Graph getGraph( ) {
		return this.graph;
	}

  public static int getRegulation( Molecule m, Pair<SampleGroup> sampleGroups ){
    Settings settings = Settings.getSettings( );
    return CorrelationDisplayPanel.getRegulation( m, sampleGroups,
            settings.getDouble( "preferences.correlation.foldChange", 2.0 ),
            settings.getBoolean( "preferences.correlation.logData", false ));
  }

  public static int getRegulation( Molecule m, Pair<SampleGroup> sampleGroups,
                                   double foldChange, boolean logData ) {
    if ( sampleGroups == null ) {
      return 0;
    }
    NumberList group1 = m.getValues( sampleGroups.getFirst( ));
    NumberList group2 = m.getValues( sampleGroups.getSecond( ));
    filterZeros( group1 );
    filterZeros( group2 );
    double mean1;
    double mean2;
    if ( group1.size( ) == 0 )
      mean1 = 0.0;
    else
      mean1 = group1.getMean( );

    if ( group2.size( ) == 0 )
      mean2 = 0.0;
    else
      mean2 = group2.getMean( );

    if ( logData ) {
      if ( mean2 - mean1 > Math.log( foldChange )) {
        return 1;
      }
      if ( mean1 - mean2  > Math.log( foldChange )) {
        return -1;
      }
    }
    if ( mean2 / mean1 > foldChange ) {
      return 1;
    }
    if ( mean1 / mean2  > foldChange ) {
      return -1;
    }
    return 0;
  }

  /**
   * Removes an element from a list when the element in either is equal to 
   * 0. Double.compare( ) is used to determine this. This operation is 
   * destructive and therefore you should pass a copy of the List if you do not 
   * want the original to be modified.
   * 
   * @param list The list to be filtered.
   */
  private static void filterZeros ( List<Number> list ) {
    int size = list.size( );
    for ( int i=size - 1; i >= 0;  i-- ) {
      if ( Double.compare( list.get( i ).doubleValue( ), 0.0 ) == 0 ) {
        list.remove( i );
      }
    }
  }

	// =================== PRIVATE/PROTECTED CLASSES ==========================
	// ====================== MoleculeFilterPanel =============================

	/**
	 * A UI class for hiding/showing moledules (nodes)
	 */
	private class MoleculeFilterPanel extends JPanel implements 
			ItemListener, ActionListener, GraphItemChangeListener<Molecule>,
			KeyListener {

		private HashMap<Molecule,MoleculeCheckBox> checkBoxMap = 
			new HashMap<Molecule,MoleculeCheckBox>( );
		private JLabel filterLabel;
		private JButton clearButton;
		private JButton noneButton;
		private JButton allButton;
		private JTextField filterBox = new JTextField( );
		private MoleculePopup popup = new MoleculePopup( );
		private JScrollPane moleculeScrollPane;

		private JPanel moleculeList = new JPanel( new GridLayout( 0, 1 )) {
			// Inserts the checkboxes in alphabetical order
			public Component add( Component component ) {
				Logger logger = Logger.getLogger( getClass( ));
				int index = 0;
				String componentText = null;
				// find where the new checkBox should go.
				componentText = ((MoleculeCheckBox)component).getText( );
				String cText = null;
				Component[] components = this.getComponents( );
				for ( int i=0; i < components.length; i++ ) {
					if ( components[ i ] instanceof AbstractButton ) {
						cText = ((AbstractButton)components[ i ]).getText( );
						if ( cText.compareTo(( componentText )) > 0 ) {
							logger.debug( "Attemping to insert " + 
								componentText + " before " + cText );
							try {
								this.add( component, i );
							} catch ( IllegalArgumentException e ) {
								// add it to the end instead.
								logger.debug( 
									"Adding " + componentText + " to the end of the list" );
								return super.add( component );
							}
							return component;
						}
					}
				}
				// it didn't fit before anything, so add it to the end instead.
				logger.debug( 
					"Adding " + componentText + " to the end of the list" );
				return super.add( component );
			}
		};

		/**
		 * Creates a new instance of MoleculeFilterPanel.
		 */
		public MoleculeFilterPanel( ) {
			super( new BorderLayout( ));
			Language language = Settings.getLanguage( );

			this.noneButton = new JButton( language.get( "None" ));
			this.allButton = new JButton( language.get( "All" ));
			this.filterLabel = new JLabel( language.get( "Search" ) + ": ",
			                               SwingConstants.RIGHT );
			this.clearButton = new JButton( language.get( "Clear" ));

			JPanel searchPanel = new JPanel( new BorderLayout( ));
			searchPanel.add( this.clearButton, BorderLayout.SOUTH );
			searchPanel.add( this.filterLabel, BorderLayout.WEST );
			searchPanel.add( this.filterBox, BorderLayout.CENTER );
			this.filterBox.addKeyListener( this );
			this.clearButton.addActionListener( this );
			this.clearButton.setPreferredSize( new Dimension( 75, 20 ));
		
			// ALL & RESET BUTTONS
			JPanel moleculeButtonPanel = new JPanel( new BorderLayout( ));
			this.allButton.setPreferredSize( new Dimension( 75, 20 ));
			this.noneButton.setPreferredSize( new Dimension( 75, 20 ));
			this.allButton.addActionListener( this );
			this.noneButton.addActionListener( this );
			moleculeButtonPanel.add( this.allButton, BorderLayout.WEST );
			moleculeButtonPanel.add( this.noneButton, BorderLayout.EAST );	

			// MOLECULE LIST
			this.add( searchPanel, BorderLayout.NORTH );
			JPanel moleculeListPanel = new JPanel( new BorderLayout( ));
			moleculeListPanel.add( this.moleculeList, BorderLayout.NORTH );
			JPanel padding = new JPanel( );
			padding.setBackground( Color.WHITE );
			moleculeListPanel.add( padding, BorderLayout.CENTER );
			this.moleculeScrollPane = new JScrollPane( moleculeListPanel );
			this.add( this.moleculeScrollPane, BorderLayout.CENTER );
			this.add( moleculeButtonPanel, BorderLayout.SOUTH );
			this.setBorder( 
				BorderFactory.createTitledBorder( 
					BorderFactory.createLineBorder( Color.BLACK, 1 ),
						language.get( "Molecule List" ),
						TitledBorder.CENTER,
					TitledBorder.TOP
				)
			);
			this.moleculeList.setBackground( Color.WHITE );
			this.filterBox.setBackground( Color.WHITE );
			this.moleculeScrollPane.getVerticalScrollBar( ).setUnitIncrement( 50 );
		}

		/**
		 * Adds a molecule to the Graph and creates a Checkbox for removing it.
		 * 
		 * @param m The Molecule to add.
		 * @return true if the molecule doesn't already exist in the list.
		 */
		public boolean add( Molecule m ) {
			if ( this.checkBoxMap.keySet( ).contains( m )) {
				return false;
			}
			MoleculeCheckBox cb = new MoleculeCheckBox( m, this.popup, true );
			cb.setBackground( Color.WHITE );
			this.moleculeList.add( cb );
			this.checkBoxMap.put( m, cb );
			cb.addItemListener( this );
			return true;
		}

		/**
		 * Returns An arrayList of MoleculeCheckBoxes.
		 * 
		 * @return an ArrayList of MoleculeCheckBoxes from the display panel
		 */
		public Collection <JCheckBox> getCheckBoxes( ) {
			return new ArrayList<JCheckBox>( this.checkBoxMap.values( ));
		}

		//for the checkboxes
		/**
		 * The itemStateChanged method of the ItemListener interface.
		 * @see ItemListener#itemStateChanged(ItemEvent)
		 * 
		 * @param event the event which triggered this action.
		 */
		public void itemStateChanged( ItemEvent event ) {
      Molecule molecule = 
        ((MoleculeCheckBox)event.getSource( )).getMolecule( );
      if ( event.getStateChange( ) == ItemEvent.SELECTED ) {
        graph.addVertex( molecule );
        synchronized( graph ) {
          for( Correlation correlation : correlations ){
            if ( graph.isValidEdge( correlation )) {
              graph.addEdge( correlation,
                new edu.uci.ics.jung.graph.util.Pair<Molecule>( 
                  correlation.getFirst( ), correlation.getSecond( )),
                  EdgeType.UNDIRECTED );
            }
          }
        }
      } else {
        graph.getPickedVertexState( ).pick( molecule, false );
        graph.removeVertex( molecule );
      }
			graph.repaint( );
		}

		// for the select all/none buttons
		/**
		 * The actionPerformed method of the ActionListener interface.
		 * @see ActionListener#actionPerformed(ActionEvent)
		 * 
		 * @param e The event which triggered this action.
		 */
		public void actionPerformed( ActionEvent e ) {
			Object source = e.getSource( );
			if ( source == this.allButton ) {
				this.setAllVisible( true );
			} else if ( source == this.noneButton ) {
				this.setAllVisible( false );
			} else if ( source == this.clearButton ) {
				this.filterBox.setText( "" );
				// simulate a key press of backspace.
				KeyEvent keyEvent = new KeyEvent( this.filterBox, -1,
					System.currentTimeMillis( ), 0, KeyEvent.VK_BACK_SPACE, '\b' );
				for ( KeyListener k : this.filterBox.getKeyListeners( )) {
					k.keyPressed( keyEvent );
					k.keyTyped( keyEvent );
					k.keyReleased( keyEvent );
				}
			}
		}

		/**
		 * The keyReleased method of the KeyListener interface.
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 * 
		 * @param e The event which triggered this action.
		 */
		public void keyReleased( KeyEvent e ) {
			Object source = e.getSource( );
			if ( source == this.filterBox ) {
				String text = this.filterBox.getText( );
				this.filter( text );
			}
		}
		/**
		 * The keyPressed method of the KeyListener interface. Not implemented.
		 * 
		 * @param e The event which triggered this action.
		 */
		public void keyPressed( KeyEvent e ) { }

		/**
		 * The keyTyped method of the KeyListener interface. Not implemented.
		 * 
		 * @param e The event which triggered this action.
		 */
		public void keyTyped( KeyEvent e ) { }

		/**
		 * Sets all visible checkboxes to the desired state.
		 * 
		 * @param state True for checked, false for unchecked.
		 */
		private void setAllVisible( boolean state ) {
			graph.setIgnoreRepaint( true );
			if ( state ) {
				TreeSet<Molecule> currentMolecules = 
					new TreeSet<Molecule>( graph.getVertices( ));
				// the graph seems to slowly come to a crawl on larger datasets if we 
				// don't clear it first (workaround)
				for ( Molecule m : currentMolecules ) {
					graph.removeVertex( m );
				}
				for ( Component c : this.moleculeList.getComponents( )) {
					MoleculeCheckBox cb = (MoleculeCheckBox)c;
					Molecule m = cb.getMolecule( );
					if ( m != null ) {
						cb.setSelected( state );
						currentMolecules.add( m );
					}
				}
				// adding them back in reverse order seems to be slightly faster.
				for ( Molecule m : currentMolecules.descendingSet( )) {
					graph.addVertex( m );
				}
			} else {
				for ( Component c : this.moleculeList.getComponents( )) {
					MoleculeCheckBox cb = (MoleculeCheckBox)c;
					Molecule m = cb.getMolecule( );
					if ( m != null ) {
						cb.setSelected( state );
							if ( graph.containsVertex( m ))
								graph.removeVertex( m );
						}
				}
			}
			graph.filterEdges( );
		}

		/**
		 * Sets all checkboxes to the desired state.
		 * 
		 * @param state True for checked, false for unchecked.
		 */
		private void setAll( boolean state ) {
			graph.setIgnoreRepaint( true );
			for ( MoleculeCheckBox cb : this.checkBoxMap.values( )) {
				Molecule m = cb.getMolecule( );
				if ( m != null ) {
					cb.setSelected( state );
					if ( state ) {
						if ( !graph.containsVertex( m ))
							graph.addVertex( m );
					} else {
						if ( graph.containsVertex( m ))
							graph.removeVertex( m );
					}
				}
			}
			graph.filterEdges( );
		}

		/**
		 * Sets a checkbox to the desired state.
		 * 
		 * @param m The Molecule corresponding to the checkbox to be changed.
		 * @param state The new state for the checkbox.
		 */
		private void set( Molecule m, boolean state ) {
			MoleculeCheckBox cb = this.checkBoxMap.get( m );
			cb.setSelected( state );
			// fire the listeners
			for ( ItemListener i : cb.getItemListeners( )) {
				i.itemStateChanged( new ItemEvent(
					cb, -1, m,
					state ? ItemEvent.SELECTED : ItemEvent.DESELECTED
				));
			}
		}

		/**
		 * Filters the molecule list based on the passed in string.
		 * 
		 * @param filter The string to filter on.
		 */
		public void filter( String filter ) {
			// try the string as a regular Pattern, if that fails then try a literal 
			// string match. If all of that fails, just print the stack trace and show
			// all.
			Pattern p;
			// this will cause the string to emulate shell style pattern matching, 
			// since most users will not be expecting regular expression ability. 
			// This may be changed later. some RegEx characters will still work, 
			// however. If the filter is surrounded by '/', we assume it is a regex.
			if ( filter.length( ) >= 2 && 
				   filter.startsWith( "/" ) && filter.endsWith( "/" )) {
				filter = String.format( ".*%s.*", 
					filter.substring( 1, filter.length( ) - 1 ));
			} else {
				filter = String.format( ".*%s.*", 
					filter.replace( "*", ".*" ).replace( "?", "." ));
			}
			try { 
				p = Pattern.compile( filter, Pattern.CASE_INSENSITIVE );

				} catch ( PatternSyntaxException e ) { 
					try {
						p = Pattern.compile( filter, 
						                     Pattern.CASE_INSENSITIVE | Pattern.LITERAL );

					} catch ( PatternSyntaxException exc ) {
						Logger.getLogger( getClass( )).debug( e );
						p = Pattern.compile( ".*", Pattern.CASE_INSENSITIVE );
					}
				}

			for( JCheckBox cb : this.getCheckBoxes( )) {
				if ( cb.isVisible( ) && !p.matcher(
							((MoleculeCheckBox)cb).getMolecule( ).toString( ) ).matches( )) {
					this.moleculeList.remove( cb );
					cb.setVisible( false );
					
				} else if ( !cb.isVisible( ) && p.matcher( 
						((MoleculeCheckBox)cb).getMolecule( ).toString( ) ).matches( )) {
					this.moleculeList.add( cb );
					cb.setVisible( true );
				}
				this.moleculeScrollPane.validate( );
			}
		}

		/**
		 * The stateChanged method of the GraphItemChangeListener interface.
		 * @see GraphItemChangeListener#stateChanged(GraphItemChangeEvent)
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

		private class MoleculeCheckBox extends JCheckBox implements MouseListener {
			private Molecule molecule;
			private MoleculePopup popup;

			public MoleculeCheckBox ( Molecule m, MoleculePopup popup,
			                          boolean checked ) {
				super( m.toString( ), checked );
				this.molecule = m;
				this.popup = popup;
				this.addMouseListener( this );
			}

			public Molecule getMolecule( ) {
				return this.molecule;
			}

			public void mouseClicked( MouseEvent e ) { 
				if ( e.getButton( ) == MouseEvent.BUTTON3 ) {
					this.popup.show( this, e.getX( ), e.getY( ), this.molecule );
				}
			}
			public void mouseEntered( MouseEvent e ) { }
			public void mouseExited( MouseEvent e ) { }
			public void mousePressed( MouseEvent e ) { }
			public void mouseReleased( MouseEvent e ) { }
		}
	}

	// ============================ InfoPanel ===================================
	/**
	 * A class for displaying the table below the graph.
	 */
	private class InfoPanel extends JTabbedPane 
	                        implements ChangeListener,SampleGroupChangeListener {
		private ConditionPanel conditionPanel;
		private JPanel topologyPanel;
		private JPanel degreeDistributionPanel;
		private JPanel correlationDistributionPanel;
		private JPanel neighborhoodConnectivityPanel;
		private JTable moleculeTable;
		private JTable correlationTable;

		/**
		 * Creates a new InfoPanel.
		 */
		public InfoPanel( ) {
			super( );
			Language language = Settings.getLanguage( );

			this.conditionPanel = new ConditionPanel( );
			this.topologyPanel = new TopologyPanel( );
			this.degreeDistributionPanel = new DegreeDistributionPanel( );
			this.correlationDistributionPanel = new CorrelationDistributionPanel( );
			this.neighborhoodConnectivityPanel = 
					new NeighborhoodConnectivityDistributionPanel( );
			this.moleculeTable = new JTable(0,0) {
				public boolean isCellEditable( int row, int col ) {
					return false;
				}
			};
			this.correlationTable = new JTable(0,0) {
				public boolean isCellEditable( int row, int col ) {
					return false;
				}
			};
			this.setBackground( Color.WHITE );

			this.conditionPanel.setBackground( Color.WHITE );
			this.topologyPanel.setBackground( Color.WHITE );
			this.moleculeTable.setBackground( Color.WHITE );
			this.correlationTable.setBackground( Color.WHITE );

			this.moleculeTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
			this.correlationTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
			this.add( new JScrollPane( moleculeTable ), 
			          language.get( "Molecules" ));
			this.add( new JScrollPane( correlationTable ), 
			          language.get( "Correlations" ));
			this.add( conditionPanel, language.get( "Display Conditions" ));
			this.add( topologyPanel, language.get( "Topological Information" ));
			this.add( degreeDistributionPanel, 
			          language.get( "Node Degree Distribution" ));
			this.add( correlationDistributionPanel, 
			          language.get( "Correlation Distribution" ));
			this.add( neighborhoodConnectivityPanel, 
			          language.get( "Neighborhood Connectivity" ));
		}

		/**
		 * Adds a new Molecule to the table.
		 * 
		 * @param molecule The new molecule to be added.
		 */
		public void add( Molecule molecule ) {
			DefaultTableModel tm = (DefaultTableModel)this.moleculeTable.getModel( );
			Map<String,String> attributes = molecule.getAttributes( );
			if ( tm.getColumnCount( ) == 0 ) {
				tm.addColumn( "id" );
				for ( String key : attributes.keySet( )) {
					tm.addColumn( key );
				}
				for ( Sample sample : samples ) {
					tm.addColumn( sample.toString( ));
				}
				Enumeration <TableColumn> columns = 
					this.moleculeTable.getColumnModel( ).getColumns( );
				while( columns.hasMoreElements( ))
					columns.nextElement( ).setPreferredWidth( 75 );
			}
			int c = attributes.size( ) + samples.size( ) + 1;
			Object[] newRow = new Object[ c ];
			int i=0;
			newRow[ i++ ] = molecule.getId( );
			for( String value : attributes.values( )) {
				newRow[ i++ ] = value.toString( );
			}
			for( Sample sample : samples ) {
				newRow[ i++ ] = sample.getValue( molecule );
			}
			tm.addRow( newRow );
		}

		/**
		 * Adds a new Correlation to the table.
		 * 
		 * @param correlation The new Correlation to be added.
		 */
		public void add( Correlation correlation ) {
			Language language = Settings.getLanguage( );
			DefaultTableModel tm = 
				(DefaultTableModel)this.correlationTable.getModel( );
			if ( tm.getColumnCount( ) == 0 ) {
				String [ ] keys = { 
					language.get( "Molecule" ) + " 1", 
					language.get( "Molecule" ) + " 2", 
					language.get( "Pearson Value" ),
					language.get( "Spearman Rank Value" ),
					language.get( "Kendall Tau-b Rank Value" )
				};
				for ( String key : keys ) {
					tm.addColumn( key );
				}
				Enumeration <TableColumn> columns = 
					this.correlationTable.getColumnModel( ).getColumns( );
				int column = 0;
				while( columns.hasMoreElements( )) {
					TableColumn tc = columns.nextElement( );
					tc.setPreferredWidth( 175 );
					switch ( column ) {
						case 2:
							tc.setCellRenderer( new PickedColumnRenderer( 
								pearsonCalculationMenuItem ));
							break;
						case 3:
							tc.setCellRenderer( new PickedColumnRenderer( 
								spearmanCalculationMenuItem ));
							break;
						case 4:
							tc.setCellRenderer( new PickedColumnRenderer( 
								kendallCalculationMenuItem ));
							break;
					}
					column++;
				}
				pearsonCalculationMenuItem.addChangeListener( this );
				spearmanCalculationMenuItem.addChangeListener( this );
				kendallCalculationMenuItem.addChangeListener( this );
			}
			Object[] newRow = new Object[ 5 ];
			newRow[ 0 ] = correlation.getFirst( ).getId( );
			newRow[ 1 ] = correlation.getSecond( ).getId( );
			newRow[ 2 ] = String.format( "%.5f", 
				correlation.getValue( Correlation.PEARSON ));
			newRow[ 3 ] = String.format( "%.5f", 
				correlation.getValue( Correlation.SPEARMAN ));
			newRow[ 4 ] = String.format( "%.5f", 
				correlation.getValue( Correlation.KENDALL ));
			tm.addRow( newRow );
		}

		/**
		 * Removes a Molecule from the table. Does nothing if the Molecule is not 
		 * present.
		 * 
		 * @param molecule the Molecule to be removed.
		 */
		public void remove( Molecule molecule ) {
			int row = this.getRowOf( molecule );
			if ( row >= 0 )
				((DefaultTableModel)this.moleculeTable.getModel( )).removeRow( row );
		}

		/**
		 * Removes a Correlation from the table. Does nothing if the Correlation 
		 * is not present.
		 * 
		 * @param correlation the Correlation to be added.
		 */
		public void remove( Correlation correlation ) {
			int row = getRowOf( correlation );
			if ( row >= 0 )
				((DefaultTableModel)this.correlationTable.getModel( ))
					.removeRow( row );
		}

		/**
		 * Clears the Molecule table.
		 */
		public void clearMolecules( ) {
			DefaultTableModel tm = 
				(DefaultTableModel)this.moleculeTable.getModel( );
			while( tm.getRowCount( ) > 0 ) {
				tm.removeRow( 0 );
			}
		}

		/**
		 * Clears the Correlation Table.
		 */
		public void clearCorrelations( ) {
			DefaultTableModel tm = 
				(DefaultTableModel)this.correlationTable.getModel( );
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
				if ( tm.getValueAt( returnValue, 0 ).equals( molecule.getId( )))
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
			DefaultTableModel tm = 
				(DefaultTableModel)this.correlationTable.getModel( );
			int returnValue = 0;
			while( returnValue < tm.getRowCount( )) {
				if ( correlation.getFirst( ).getId( ).equals( 
					tm.getValueAt( returnValue, 0 )) && 
					correlation.getSecond( ).getId( ).equals(
					tm.getValueAt( returnValue, 1 )))
					return returnValue;
				returnValue++;
			}
			return -1;

		}

		public void stateChanged( ChangeEvent event ) {
			correlationTable.repaint( );	
		}

		public void groupStateChanged( SampleGroupChangeEvent event ) {
			this.conditionPanel.groupStateChanged( event );
		}

		// ====================== LinkListener ==================================
		// ====================== PickedColumnRenderer ==========================
		/**
		 * A class for highlighting the selected correlation in the InfoTable
		 */
		private class PickedColumnRenderer extends DefaultTableCellRenderer {
			private AbstractButton pickedIndicator;
			private Color pickedColor = Color.YELLOW;

			public PickedColumnRenderer( AbstractButton indicator ) {
				super( );
				this.pickedIndicator = indicator;
			}

			public Component getTableCellRendererComponent( 
				JTable table, Object value, boolean isSelected, 
				boolean hasFocus,int row, int column ) {

				Component cell = super.getTableCellRendererComponent( 
					table, value, isSelected, hasFocus, row, column );
				if ( !isSelected ) {
					if ( pickedIndicator.isSelected( )) {
						cell.setBackground( this.pickedColor );
					} else {
					 cell.setBackground( null );
					}
				}
				return cell;

			}
		}

		// ======================== ConditionPanel =============================
		/**
		 * A UI class for displaying the current graph conditions.
		 */
		private class ConditionPanel extends JPanel 
					  implements ActionListener,SampleGroupChangeListener {
			private JScrollPane sampleGroupingScrollPane;
			private JTable sampleGroupingTable;

			/**
			 * Creates a new ConditionPanel
			 */
			public ConditionPanel( ) {
				super( );
				this.sampleGroupingTable = new JTable( ) {
					public boolean isCellEditable( int row, int col ) {
						return false;
					}
				};
				this.sampleGroupingTable.setBackground( Color.WHITE );
				this.sampleGroupingScrollPane = 
					new JScrollPane( this.sampleGroupingTable );
				this.setLayout( new BorderLayout( ));
				this.add( this.sampleGroupingScrollPane, BorderLayout.EAST );
				// listen for changes to layout/calculation
				multipleCirclesLayoutMenuItem.addActionListener( this );
				multipleCirclesLayoutMenuItem.addActionListener( this );
				singleCircleLayoutMenuItem.addActionListener( this );
				randomLayoutMenuItem.addActionListener( this );
				kkLayoutMenuItem.addActionListener( this );
//				frLayoutMenuItem.addActionListener( this );
				frSpringLayoutMenuItem.addActionListener( this );
				pearsonCalculationMenuItem.addActionListener( this );
				spearmanCalculationMenuItem.addActionListener( this ); 
				kendallCalculationMenuItem.addActionListener( this );
			}

			/**
			 * Called when the Panel is repainted.
			 * @see Component#paintComponent(java.awt.Graphics)
			 * 
			 * @param g The graphics component associated with this panel
			 */
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );

				Language language = Settings.getLanguage( );
				String text;
				String layoutName;
				FontMetrics f = g.getFontMetrics( );
				try { 
					layoutName = ((LayoutDecorator)graph.getGraphLayout( ))
						.getDelegate( ).getClass( ).getSimpleName( );
				} catch ( ClassCastException e ) {
					layoutName = graph.getGraphLayout( ).getClass( ).getSimpleName( );
				}
				text = String.format( language.get( "Layout" ) + ": %s", layoutName );
				g.drawString( text, 20, 30 );

				text = String.format( language.get( "Correlation Method" ) + ": %s",
					Correlation.NAME[ correlationMethod.intValue( )]);
				g.drawString( text, 20, 50 );
			}

			/**
			 * The actionPerformed method of the ActionListener interface.
			 * @see ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 * 
			 * @param e The event which triggered this action.
			 */
			public void actionPerformed( ActionEvent e ) {
				if ( this.isVisible( ))
					this.repaint( );
			}

			public void groupStateChanged( SampleGroupChangeEvent e ) {
				Collection<SampleGroup> sampleGroups = e.getGroups( );
				DefaultTableModel tableModel = 
					(DefaultTableModel)this.sampleGroupingTable.getModel( );
				tableModel.setColumnCount( 0 );
				tableModel.setRowCount( 0 );
				int columnCount = sampleGroups.size( );
				Iterator[] groups = new Iterator[ columnCount ];
				int j = 0;
				for ( SampleGroup group : sampleGroups ) {
					tableModel.addColumn( group.toString( ));
					groups[ j++ ] = group.iterator( );
				}
				Object[] row = new Object[ columnCount ];
				boolean done = false;
				do {
					done = true;
					for( int i=0; i < columnCount; i++ ) {
						row[ i ] = groups[ i ].hasNext( ) ? 
							groups[ i ].next( ).toString( ) : "";
						done = done && !groups[ i ].hasNext( );
					}
					tableModel.addRow( row );
				} while ( !done );
				if ( this.isVisible( ))
					this.repaint( );
			}
		}

		// ============================= TopologyPanel ===========================
		/**
		 * A UI class for displaying the graph Topology.
		 */
		private class TopologyPanel extends JPanel 
		                         implements GraphItemChangeListener,DaemonListener {
      private double averageNeighbors;
      private int networkDiameter;
      private double characteristicPathLength;
      private UpdateDaemon updateDaemon = UpdateDaemon.create( 500 );

			/**
			 * Creates a new TopologyPanel.
			 */
			public TopologyPanel( ) {
				super( );
				graph.addVertexChangeListener( this );
				graph.addEdgeChangeListener( this );
        updateDaemon.update( this );
        updateDaemon.start( );
			}

			/**
			 * Called when the panel is repainted.
			 * @see Component#PaintComponent(java.awt.Graphics)
			 * 
			 * @param g The Graphics for this Component.
			 */
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				Language language = Settings.getLanguage( );
				Vector <Molecule> molecules = 
					new Vector<Molecule>( graph.getVertices( ));
        synchronized( graph ) {
          Vector <Correlation> correlations = 
            new Vector<Correlation>( graph.getEdges( ));
        }
//				g.setFont( new Font( "Sans Serif", Font.BOLD, 18 ));
				String text;
				
				text = String.format( language.get( "Number of Nodes" ) + ": %d", 
					molecules.size( ));
				g.drawString( text, 20, 16 );

				text = String.format( language.get( "Number of Edges" ) + ": %d", 
					correlations.size( ));
				g.drawString( text, 20, 34 );

				text = String.format( language.get( "Number of correlated molecules" ) +
					": %d", getCorrelatedCount( molecules ));
				g.drawString( text, 20, 52 );

				text = language.get( "Average number of neighbors" ) + 
          (( this.averageNeighbors >= 0 ) ?
            String.format( ": %.2f", this.averageNeighbors ) : 
            ": Calculating..." );
				g.drawString( text, 20, 70 );

				text = language.get( "Network diameter" ) + 
          (( this.networkDiameter >= 0 ) ? 
            String.format( ": %d", this.networkDiameter ) : ": Calculating..." );
				g.drawString( text, 20, 86 );

				text = language.get( "Characteristic path length" ) + 
          (( this.characteristicPathLength >= 0 ) ? 
            String.format( ": %.2f", this.characteristicPathLength ) : 
            ": Calculating..." );
				g.drawString( text, 20, 102 );


			}

      public void daemonUpdate( ) {
        this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
          this.averageNeighbors = -1;
          this.networkDiameter = -1;
          this.characteristicPathLength = -1;
          this.repaint( );
        try {
          this.averageNeighbors = getAverageNeighbors( molecules );
          this.repaint( );
          this.networkDiameter = getNetworkDiameter( molecules );
          this.repaint( );
          this.characteristicPathLength = getCharacteristicPathLength( molecules );
          this.repaint( );
        } catch( RuntimeException e ){
          // Something went wrong. Wait a moment and try updating again.
          try {
            Thread.sleep( 1000 );
          } catch( InterruptedException i ) { }
          updateDaemon.update( this );
        }
        this.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ));
      }

			/**
			 * Returns the number of nodes which are connected to at least one other 
			 * node.
			 */
			public int getCorrelatedCount( Collection <Molecule> molecules ) {
				int returnValue = 0;
				for ( Molecule m : molecules ) {
					if ( graph.getNeighborCount( m ) > 0 )
						returnValue++;
				}
				return returnValue;
			}

			/**
			 * Returns the average number of neighbors (connections) for all nodes in
			 * the network.
			 * 
			 * @param molecules A Collection of nodes to check.
			 * @return The average connection count.
			 */
			public double getAverageNeighbors( Collection <Molecule> molecules ) {
				int neighbors = 0;
				for ( Molecule m : molecules ) {
					neighbors += graph.getNeighborCount( m );
				}
				return (double)neighbors / molecules.size( );
			}

			/**
			 * Returns the network diameter, or the longest shortest path from one
			 * node to any other. Nodes which do not have a path to one another are
			 * ignored.
			 * 
			 * @param molecule The Collection of molecules to check.
			 * @return The longest shortest path.
			 */
			public int getNetworkDiameter( Collection <Molecule> molecules ) {
				int returnValue = 0;
				int currentValue;
				List<Correlation> path;
				for ( Molecule m : molecules ) {
					for ( Molecule n : molecules ) {
						path = graph.getShortestPath( m, n );
						if ( path != null ) {
							currentValue = path.size( );
							if ( currentValue > returnValue )
								returnValue = currentValue;
						}
					}
				}
				return returnValue;
			}

			/**
			 * Returns the characteristic path length for the network, or the average
			 * shortest path length from one node to another. Nodes which do not have
			 * a path to one another are ignored.
			 * 
			 * @param molecules The collection of molecules to check.
			 * @return The characteristic path length.
			 */
			public double getCharacteristicPathLength( 
					Collection <Molecule> molecules ) {

				double returnValue = 0.0;
				int count = 0;
				List<Correlation> path;
				for ( Molecule m : molecules ) {
					for ( Molecule n : molecules ) {
						path = graph.getShortestPath( m, n );
						if ( path != null ) {
							returnValue += path.size( );
							count++;
						}
					}
				}
				return returnValue / count;
			}

			/**
			 * The stateChanged method of the GraphItemChangeListener interface.
			 * @see GraphItemChangeListener#stateChanged(GraphItemChangeEvent)
			 * 
			 * @param event The event which triggered this action.
			 */
			public void stateChanged( GraphItemChangeEvent event ) {
        updateDaemon.update( this );
				if ( this.isVisible( ))
					this.repaint( );
			}
		}

		// ========================== DistributionPanel ===========================
		private abstract class DistributionPanel extends JPanel 
		                       implements GraphItemChangeListener {

			protected JFreeChart distributionChart;
			protected DefaultCategoryDataset distributionData;

			protected DistributionPanel( ) {

				super( );
				graph.addVertexChangeListener( this );
				graph.addEdgeChangeListener( this );
				distributionData = new DefaultCategoryDataset( );
				distributionChart = ChartFactory.createBarChart( 
					null, //title
					this.getCategoryAxisLabel( ), // category axis label
					this.getValueAxisLabel( ), // value axis label
					distributionData, // plot data
					PlotOrientation.VERTICAL, // Plot Orientation
					false, // show legend
					false, // use tooltips
					false // configure chart to generate URLs
				);
//				this.distributionChart.getTitle( ).setFont( new Font( "Arial", Font.BOLD, 18 ));
				CategoryPlot plot = distributionChart.getCategoryPlot( );
				BarRenderer renderer = (BarRenderer)plot.getRenderer( );
				renderer.setBarPainter( new StandardBarPainter( ));
				renderer.setShadowVisible( false );
				plot.setBackgroundPaint( Color.WHITE );
				plot.setRangeGridlinePaint( Color.GRAY );
				plot.setDomainGridlinePaint( Color.GRAY );
				// add a context menu for saving the graph to an image
				new ContextMenu( this ).add( new SaveImageAction( this ));
			}

			public abstract void getDistributionData ( 
					DefaultCategoryDataset distributionData );

			public void paintComponent( Graphics g ) {
				CategoryPlot plot = this.distributionChart.getCategoryPlot( );
				plot.getDomainAxis( ).setLabel( this.getCategoryAxisLabel( ));
				plot.getRangeAxis( ).setLabel( this.getValueAxisLabel( ));
				super.paintComponent( g );
				getDistributionData( distributionData );
				g.drawImage( 
					distributionChart.createBufferedImage( getWidth( ), getHeight( )), 
					0, 0, Color.WHITE, this );
			}

			public void stateChanged( GraphItemChangeEvent event ) {
				if ( this.isVisible( ))
					this.repaint( );
			}

			public abstract String getCategoryAxisLabel( );
			public abstract String getValueAxisLabel( );
		}

		// ========================= DegreeDistributionPanel ======================
		private class DegreeDistributionPanel extends DistributionPanel {

			public DegreeDistributionPanel( ) {
				super( );
				distributionChart.getCategoryPlot( ).getRangeAxis( )
					.setStandardTickUnits( NumberAxis.createIntegerTickUnits( ));
				// add a context menu for saving the graph to an image
				new ContextMenu( this ).add( new SaveImageAction( this ));
			}

			public void getDistributionData( 
					DefaultCategoryDataset distributionData ) {

				Vector<Molecule> molecules = 
					new Vector<Molecule>( graph.getVertices( ));
				int max = -1;
				int [] dist = new int[ molecules.size( ) ];
				for( Molecule m : molecules ) {
					int currentDeg = graph.getNeighborCount( m );
					max = Math.max( max, currentDeg );
					dist[ currentDeg ]++;
				}
				distributionData.clear( );
				for ( int i=0; i <= max; i++ ) {
					distributionData.addValue( dist[ i ], "", new Integer( i ));
				}
			}

			public String getCategoryAxisLabel( ) {
				return Settings.getLanguage( ).get( "Neighbor Count" ) + 
					String.format( "(%s, Range %.3f - %.3f)", 
						Correlation.NAME[ correlationMethod.intValue( )],
						correlationFilterPanel.getRange( ).getMin( ),
						correlationFilterPanel.getRange( ).getMax( ));
			}

			public String getValueAxisLabel( ) {
				return Settings.getLanguage( ).get( "Nodes" );
			}
		}

	// ===================== CorrelationDistributionPanel ======================
		private class CorrelationDistributionPanel extends JPanel 
		                      implements ItemListener,GraphItemChangeListener {

			protected SimpleHistogramDataset distributionData;
			protected JFreeChart distributionChart;


			public CorrelationDistributionPanel( ) {
				super( );
//				graph.addVertexChangeListener( this );
//				graph.addEdgeChangeListener( this );
				Language language = Settings.getLanguage( );
				pearsonCalculationMenuItem.addItemListener( this );
				spearmanCalculationMenuItem.addItemListener( this ); 
				kendallCalculationMenuItem.addItemListener( this );
				// add a context menu for saving the graph to an image
				new ContextMenu( this ).add( new SaveImageAction( this ));
				this.distributionData = 
					new SimpleHistogramDataset( "Correlation Distribution"  );
				distributionChart = ChartFactory.createHistogram(
					null, //title
					language.get( "Correlation Value (All correlations, " +
						Correlation.NAME[ correlationMethod.intValue( )] + ")"
						), // category axis label
					language.get( "Number of Correlations" ), // value axis label
					distributionData, // plot data
					PlotOrientation.VERTICAL, // Plot Orientation
					false, // show legend
					false, // use tooltips
					false // configure chart to generate URLs
				);
//				this.distributionChart.getTitle( ).setFont( new Font( "Arial", Font.BOLD, 18 ));
				boolean includeLower = false, includeUpper = true;
				for( int i=-100; i < 100; i++ ) {
					if ( i == -1 )
						includeUpper = false;
					else if ( i == 0 )
						includeLower = true; 
					SimpleHistogramBin s = new SimpleHistogramBin( i * 0.01, (i+1) * 0.01,
						includeLower, includeUpper );
					distributionData.addBin( s );
				}
				XYPlot plot = distributionChart.getXYPlot( );
				XYBarRenderer renderer = (XYBarRenderer)plot.getRenderer( );
				renderer.setBarPainter( new StandardXYBarPainter( ));
				renderer.setShadowVisible( false );
				plot.getDomainAxis( ).setRange( -1.0, 1.0 );
				plot.setBackgroundPaint( Color.WHITE );
				plot.setRangeGridlinePaint( Color.GRAY );
				plot.setDomainGridlinePaint( Color.GRAY );
			}

			public void paintComponent( Graphics g ) {
				XYPlot plot = this.distributionChart.getXYPlot( );
				plot.getDomainAxis( ).setLabel( 
					Settings.getLanguage( ).get( "Correlation Value (All correlations, " +
						Correlation.NAME[ correlationMethod.intValue( )] + ")"
						));
				super.paintComponent( g );
				getDistributionData( distributionData );
				g.drawImage( 
					distributionChart.createBufferedImage( getWidth( ), getHeight( )),
					0, 0, Color.WHITE, this );
			}

			public void getDistributionData( SimpleHistogramDataset distributionData ) {
				Collection <Correlation> edges = correlations;
				Range correlationRange = correlationFilterPanel.getRange( );
				distributionData.clearObservations( );
				for( Correlation c : edges ) {
					double value = c.getValue( correlationMethod );
//					if ( correlationRange.contains( Math.abs( value ))) {
						try {
							distributionData.addObservation( value );
						} catch ( RuntimeException e ) {
							Logger.getLogger( getClass( )).debug( 
								String.format( "No bin available for value %f", value ), e );
						}
//					}
				}
			}

			public void stateChanged( GraphItemChangeEvent event ) {
				if ( this.isVisible( ))
					this.repaint( );
			}
			public void itemStateChanged( ItemEvent e ) {
				if ( this.isVisible( ))
					this.repaint( );
			}

		}

		// ================== NeighborhoodConnectivityDistributionPanel ==========
		private class NeighborhoodConnectivityDistributionPanel 
				                                            extends DistributionPanel {

			public NeighborhoodConnectivityDistributionPanel( ) {
				super(  );
			}

			public void getDistributionData( 
						DefaultCategoryDataset distributionData ) {

				Vector <Molecule> nodes = new Vector<Molecule>( graph.getVertices( ));
				int [] neighborCount = new int[ nodes.size( )];
				int [] nodeCount = new int[ nodes.size( )];
				int maxNeighborCount = 0;
				// First, go through all of the nodes, finding how many neighbors they
				// have and add the neighbors' degrees to the appropriate "bucket".
				// also increment the "buckets" to keep track of how many nodes have 
				// which degree.
				for( int i=0; i < nodes.size( ); i++ ) {
					Molecule m = nodes.get( i );
					Collection<Molecule> neighbors = graph.getNeighbors( m );
					nodeCount[ neighbors.size( )]++;
					if ( neighbors.size( ) > maxNeighborCount )
						maxNeighborCount = neighbors.size( );
					for ( Molecule n : neighbors ) {
						neighborCount[ neighbors.size( ) ] += graph.getNeighborCount( n );
					}
				}
				// Now add the data we collected to the chart.
				distributionData.clear( );
				for( int i=1; i <= maxNeighborCount; i++ ) {
					distributionData.addValue( 
						(double)neighborCount[ i ] / ( nodeCount[ i ] * i ), "",
						String.format( "%d", i ));
				}
			}

			public String getCategoryAxisLabel( ) {
				return Settings.getLanguage( ).get( "Neighbor Count" ) + 
					String.format( "(%s, Range %.3f - %.3f)", 
						Correlation.NAME[ correlationMethod.intValue( )],
						correlationFilterPanel.getRange( ).getMin( ),
						correlationFilterPanel.getRange( ).getMax( ));
			}

			public String getValueAxisLabel( ) {
				return Settings.getLanguage( ).get( "Average Neighbor Degree" );
			}
		}
	}

	// ========================= MenuItemListener ============================
	/**
	 * A class for listening for clicks on menu items.
	 * @todo Integrate this listener with the CorrelationDisplayPanel class.
	 */
	private class MenuItemListener implements ActionListener {

		/**
		 * The actionPerformed method of the ActionListener interface.
		 * @see ActionListener#actionPerformed(ActionEvent)
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

	// ========================== LayoutChangeListener =========================
	/**
	 * A class for listening for changes to the layout menu.
	 * @todo Integrate this class with the CorrelationDisplayPanel class.
	 */
	private class LayoutChangeListener implements ActionListener {

			/**
			 * The actionPerformed method of the ActionListener interface.
			 # @see ActionListener#actionPerformed(ActionEvent)
			 * 
			 * @param event The event which triggered this action.
			 */
			public void actionPerformed( ActionEvent event ) {
				Component item = ( Component )event.getSource( );

//				if ( item == animatedLayoutMenuItem ) {
//					graph.animate( animatedLayoutMenuItem.getState( ));
//				} else {
					graph.animate( false );
					if ( item == multipleCirclesLayoutMenuItem ) {
						// MultipleCircleLayout needs a copy of the sampleGroups 
						// to initialize.
						setGraphLayout( MultipleCirclesLayout.class );
						Layout layout = graph.getGraphLayout( );
						while ( layout instanceof LayoutDecorator ) {
							layout = ((LayoutDecorator)layout).getDelegate( );
						}
						MultipleCirclesLayout mclayout = (MultipleCirclesLayout)layout;
						mclayout.setSampleGroups(
							new SimplePair<SampleGroup>( getSampleGroups( )));
						mclayout.initialize( );
					} else if ( item == singleCircleLayoutMenuItem ) {
						setGraphLayout( CircleLayout.class );
					} else if ( item == randomLayoutMenuItem ) {
						setGraphLayout( RandomLayout.class );
					} else if ( item == kkLayoutMenuItem ) {
						setGraphLayout( KKLayout.class );
//					} else if ( item == frLayoutMenuItem ) {
//						setGraphLayout( FRLayout.class );
//					} else if ( item == springLayoutMenuItem ) {
//						setGraphLayout( SpringLayout2.class );
					} else if ( item == frSpringLayoutMenuItem ) {
						setGraphLayout( CircleLayout.class );
						graph.animate( true );
					} else if ( item == heatMapLayoutMenuItem ) {
						heatMap( );
					}
//				}
			}
	}

	// ==================== CorrelationGraphMouseListener ======================
	/**
	 * A class for implementing a context menu on network nodes.
	 */
	private class CorrelationGraphMouseListener 
	              implements GraphMouseListener<Molecule> {
		MoleculePopup popup = new MoleculePopup( );

		/**
		 * The graphClicked method of the GraphMouseListener class
		 * 
		 * @param m The Molecule (node) which was clicked on.
		 * @param e The event which triggered this action.
		 */
		public void graphClicked( Molecule m, MouseEvent e ) {
			if ( e.getButton( ) == MouseEvent.BUTTON3 ) {
				popup.show( e.getComponent( ), e.getX( ), e.getY( ), m );
			} else if ( e.getButton( ) == MouseEvent.BUTTON1 && 
			            e.getClickCount( ) >= 2 ) {

				CorrelationGraphVisualizer graph = 
					(CorrelationGraphVisualizer)e.getComponent( );
				new DetailWindow( correlations, m, graph.getRange( ),
													correlationMethod.intValue( ));
			}
		}
		/**
		 * The graphPressed method of the GraphMouseListener class. Not implemented.
		 * 
		 * @param m The Molecule (node) which was clicked on.
		 * @param e The event which triggered this action.
		 */
		public void graphPressed(  Molecule m, MouseEvent e ) { } 
		/**
		 * The graphPressed method of the GraphMouseListener class. Not implemented.
		 * 
		 * @param m The Molecule (node) which was clicked on.
		 * @param e The event which triggered this action.
		 */
		public void graphReleased( Molecule m, MouseEvent e ) { }
	}
	// =========================== MoleculePopup ============================
	/**
	 * A class for implementing the context menu.
	 */
	private class MoleculePopup extends JPopupMenu implements ActionListener {
		protected JMenuItem hideMenuItem;
		protected JMenuItem showMenuItem;
		protected JMenuItem detailsMenuItem;
		protected JMenuItem selectMenuItem;
		protected JMenuItem selectCorrelatedMenuItem;
		protected JMenuItem selectSubnetworkMenuItem;
		protected JMenuItem exploreCorrelationsMenu;
		protected Molecule molecule;
		protected HashMap <JMenuItem,Correlation> correlationMap = 
			new HashMap <JMenuItem,Correlation>( );
		protected SaveImageAction saveImageAction;
		
		/**
		 * Creates a new instance of the PopupMenu
		 */
		public MoleculePopup ( ) {
			super( );
			Language language = Settings.getLanguage( );
			this.hideMenuItem = new JMenuItem( language.get( "Hide" ));
			this.showMenuItem = new JMenuItem( language.get( "Show" ));
			this.detailsMenuItem = new JMenuItem( language.get( "Details" ));
			this.selectMenuItem = new JMenuItem( language.get( "Toggle selection" ));
			this.selectCorrelatedMenuItem = 
				new JMenuItem( language.get( "Select Directly Correlated" ));
			this.selectSubnetworkMenuItem =
				new JMenuItem( language.get( "Select Subnetwork" ));
			this.exploreCorrelationsMenu =
				new JMenu( language.get( "Explore Correlations" ));
			this.add( this.hideMenuItem );
			this.add( this.showMenuItem );
			this.add( this.detailsMenuItem );
			this.add( this.selectMenuItem );
			this.add( this.selectCorrelatedMenuItem );
			this.add( this.selectSubnetworkMenuItem );
			this.add( this.exploreCorrelationsMenu );
			this.addSeparator( );
			this.saveImageAction = new SaveImageAction( graph );
			this.add( this.saveImageAction );
			this.hideMenuItem.addActionListener( this );
			this.detailsMenuItem.addActionListener( this );
			this.selectMenuItem.addActionListener( this );
			this.selectSubnetworkMenuItem.addActionListener( this );
			this.selectCorrelatedMenuItem.addActionListener( this );
		}

		/**
		 * Causes the JPopupMenu to be displayed at the given coordinates.
		 * @see JPopupMenu#show(Component,int,int)
		 * 
		 * @param invoker The component which invoked this menu.
		 * @param x The x position to display this menu.
		 * @param y The y position to display this menu.
		 * @param m The molecule which was clicked on to trigger this popup.
		 */
		public void show( Component invoker, int x, int y, Molecule m ) {
			this.exploreCorrelationsMenu.removeAll( );
			this.correlationMap.clear( );
			this.molecule = m;
			this.saveImageAction.setComponent( graph );
			boolean containsVertex = graph.containsVertex( m );
			this.hideMenuItem.setEnabled( containsVertex );
			this.showMenuItem.setEnabled( !containsVertex );
			this.selectMenuItem.setEnabled( containsVertex );
			this.selectSubnetworkMenuItem.setEnabled( containsVertex );
			this.selectCorrelatedMenuItem.setEnabled( containsVertex );
			Component displayPanel = invoker;
			while ( displayPanel != null && 
				      !( displayPanel instanceof CorrelationDisplayPanel )) {
				displayPanel = displayPanel.getParent( );
			}
			Range range =
				((CorrelationDisplayPanel)displayPanel).getCorrelationRange( );
			for( Correlation c : correlations ) {
				if ( c.contains( m ) && 
						 range.contains( Math.abs( c.getValue( correlationMethod )))) {
					JMenuItem menuItem = new JMenuItem( c.getOpposite( m ).toString( ));
					this.correlationMap.put( menuItem, c );
					this.exploreCorrelationsMenu.add( menuItem );
					menuItem.addActionListener( this );
				}
			}
			this.show( invoker, x, y );
		}


		/**
		 * The actionPerformed method of the ActionListener interface.
		 * @see ActionListner#actionPerformed(ActionEvent)
		 * 
		 * @param e the event which triggered this action.
		 */
		public void actionPerformed ( ActionEvent e ) {
			Component displayPanel = this.getInvoker( );
			while( displayPanel != null &&
						!(displayPanel instanceof CorrelationDisplayPanel )) {
				displayPanel = displayPanel.getParent( );
			}
			CorrelationGraphVisualizer graph = (CorrelationGraphVisualizer)
				((CorrelationDisplayPanel)displayPanel).getGraph( );
			Range range = graph.getRange( );
			Object source = e.getSource( );

			if ( this.correlationMap.containsKey( source )) {
				new DetailWindow( correlations,
													this.correlationMap.get( source ), 
													range, correlationMethod.intValue( ));
			} else if ( source == this.hideMenuItem ) {
				graph.removeVertex( this.molecule );

			} else if ( source == this.detailsMenuItem ) {
				new DetailWindow( correlations,
													this.molecule, range, correlationMethod.intValue( ));

			} else if ( source == this.selectMenuItem ) {
				PickedState<Molecule> state = graph.getPickedVertexState( );
				state.pick( this.molecule, !state.isPicked( this.molecule ));
			} else if ( source == this.selectCorrelatedMenuItem ) {
				PickedState<Molecule> state = graph.getPickedVertexState( );
				if ( !state.isPicked( molecule )) {
					state.pick( this.molecule, true );
				}
				for ( Molecule m : graph.getNeighbors( this.molecule )) {
					state.pick( m, true );
				}
				PickedState<Correlation> edgeState = graph.getPickedEdgeState( );
				for ( Correlation c : graph.getIncidentEdges( this.molecule )) {
					edgeState.pick( c, true );
				}

			} else if ( source == this.selectSubnetworkMenuItem ) {
				PickedState<Molecule> state = graph.getPickedVertexState( );
				PickedState<Correlation> edgeState = graph.getPickedEdgeState( );
				Collection <Molecule> subnetwork = 
					this.getSubnetwork( this.molecule, graph, null);
				for( Molecule m : subnetwork ) {
					state.pick( m, true );
					for ( Correlation c : graph.getIncidentEdges( m )) {
						edgeState.pick( c, true );
					}
				}
			}
		}

		/**
		 * Recursive function for selecting connected nodes.
		 * 
		 * @param molecule The central molcule to select all connected nodes for.
		 * @param graph The graph the molecule belongs to.
		 */
		private Collection<Molecule> getSubnetwork( 
																					Molecule molecule, 
																					CorrelationGraphVisualizer graph, 
																					Collection<Molecule> collection ) {
			if ( collection == null ) 
				collection = new ArrayList<Molecule>( );
			if ( collection.contains( molecule ))
				return collection;

			collection.add( molecule );
			for ( Molecule m : graph.getNeighbors( molecule )) {
				this.getSubnetwork( m, graph, collection );
			}
			return collection;
		}
		
	}

	// ====================== CorrelationFilterPanel =============================
	/**
	 * A UI class which shows a set of Spinners for adjusting the visible 
	 * correlation range.
	 */
	private class CorrelationFilterPanel extends JPanel implements ChangeListener {

		private JLabel minCorrelationLabel;
		private JLabel maxCorrelationLabel;
		private JPanel minCorrelationFilterPanel = new JPanel( );
		private JPanel maxCorrelationFilterPanel = new JPanel( );
		private JSpinner minCorrelationSpinner;
		private JSpinner maxCorrelationSpinner;
		private MonitorableRange range; 

		/**
		 * Creates a new CorrelationFilterPanel with the default correlation range 
		 * (0.6-1.0) a step of 0.5, min of 0.0 and max of 1.0
		 */
		public CorrelationFilterPanel( ) {
			this( 0.95, 1.0 );
		}

		/**
		 * Creates a new CorrelationFilterPanel with the the specified correlation
		 * range, a step of 0.05, and min of 0.0, max of 1.0.
		 * 
		 * @param low The initial low setting for the filter.
		 * @param high The initial high setting for the filter.
		 */
		public CorrelationFilterPanel( double low, double high ) {
			this( 0.0, 1.0, 0.05, low, high );
		}

		/**
		 * Creates a new Correlation filter with the passed in values.
		 * 
		 * @param min The minimum allowed value in the spinners.
		 * @param max The maximum allowed value in the spinners.
		 * @param step The step amount for each spinner click.
		 * @param low The initial low setting for the filter.
		 * @param high The initial high setting for the filter.
		 */
		public CorrelationFilterPanel( double min, double max, double step, 
		                               double low, double high ) {
			super( new BorderLayout( ));
			Language language = Settings.getLanguage( );
			this.minCorrelationLabel = 
				new JLabel( language.get( "Higher Than" ) + ": ", SwingConstants.RIGHT );
			this.maxCorrelationLabel = 
				new JLabel( language.get( "Lower Than" )+ ": ", SwingConstants.RIGHT );

			this.range = new MonitorableRange( low, high );
			this.minCorrelationSpinner =
				new JSpinner( new SpinnerNumberModel( low, min, max, step ));
			this.maxCorrelationSpinner = 
				new JSpinner( new SpinnerNumberModel( high, min, max, step ));

			this.minCorrelationSpinner.setPreferredSize( new Dimension( 80, 25 ));
			this.maxCorrelationSpinner.setPreferredSize( new Dimension( 80, 25 ));

			this.minCorrelationFilterPanel = new JPanel( new BorderLayout( ));
			this.minCorrelationFilterPanel.add( this.minCorrelationSpinner, 
			                                    BorderLayout.EAST );
			this.minCorrelationFilterPanel.add( this.minCorrelationLabel, 
			                                    BorderLayout.CENTER );

			this.maxCorrelationFilterPanel = new JPanel( new BorderLayout( ));
			this.maxCorrelationFilterPanel.add( this.maxCorrelationSpinner, 
			                                    BorderLayout.EAST );
			this.maxCorrelationFilterPanel.add( this.maxCorrelationLabel, 
			                                    BorderLayout.CENTER );

			this.add( this.minCorrelationFilterPanel, BorderLayout.NORTH );
			this.add( this.maxCorrelationFilterPanel, BorderLayout.SOUTH );
			this.minCorrelationSpinner.addChangeListener( this ); 
			this.maxCorrelationSpinner.addChangeListener( this );
			this.minCorrelationSpinner.setBackground( Color.WHITE );
			this.maxCorrelationSpinner.setBackground( Color.WHITE );

			this.setBorder( 
				BorderFactory.createTitledBorder( 
					BorderFactory.createLineBorder( Color.BLACK, 1 ),
					Settings.getLanguage( ).get( "Correlation Filter" ),
					TitledBorder.CENTER,
					TitledBorder.TOP
			));
		}


		/**
		 * Returns the current range setting of the filter.
		 * 
		 * @return The current range setting of the filter.
		 */
		public Range getRange( ) {
			return range;
		}

		/**
		 * Returns the current range setting of the filter as a MonitorableRange
		 * which will accept a listener.
		 * 
		 * @return The current range setting as a MonitorableRange object.
		 */
		public MonitorableRange getMonitorableRange( ) {
			return range;
		}

		/**
		 * The stateChanged method of the ChangeListener interface.
		 * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
		 * 
		 * @param e The event which triggered this action.
		 */
		public void stateChanged( ChangeEvent e ) {
			range.setRange((( Double )this.minCorrelationSpinner.getValue( )).doubleValue( ),
											 (( Double )this.maxCorrelationSpinner.getValue( )).doubleValue( ));
		}
	}

	// ======================= CorrelationGraphVisualizer ======================
	/**
	 * A class for displaying a JUNG graph tailored for BioNet
	 */
	private class CorrelationGraphVisualizer 
			extends GraphVisualizer<Molecule,Correlation> 
			implements ChangeListener,GraphMouseListener<Correlation>, 
				ComponentListener,SampleGroupChangeListener {

		public MonitorableRange range;
		protected CorrelationSet correlations;
		protected Collection<Molecule> molecules;
		protected Spectrum spectrum;
		protected SpectrumLegend spectrumLegend;
		protected RegulationLegend regulationLegend;
		protected Collection<SampleGroup> sampleGroups;
		protected Color upRegulationOutline = Color.GREEN.darker( );
		protected Color downRegulationOutline = Color.RED;
		protected Color vertexColor = Color.ORANGE;


		/**
		 * Creates a new CorrelationGraphVisualizer.
		 * 
		 * @param experiment The experiment to be associated with this 
		 *  CorrelationGraphVisualizer.
		 * @param range A MonitorableRange object used to determine which 
		 *  Correlations to show on the graph.
		 */
		public CorrelationGraphVisualizer( CorrelationSet correlations, 
																			 MonitorableRange range ) {
			super( );
			this.setRange( range );
			this.setCorrelations( correlations );
			ArrayList<SampleGroup> sampleGroups = new ArrayList<SampleGroup>( );
			sampleGroups.add( new SampleGroup( "", correlations.getSamples( )));
			this.setSampleGroups( sampleGroups );

			this.addGraphMouseEdgeListener( this );
			this.addComponentListener( this );

			this.spectrum = new SplitSpectrum( range );
			this.spectrum.setOutOfRangePaint( Color.WHITE );
			this.spectrumLegend = 
				new SpectrumLegend( this.spectrum, new Range( -1.0, 1.0 ));
			this.regulationLegend = 
				new RegulationLegend( this.upRegulationOutline,
				                      this.downRegulationOutline,
															this.vertexColor );
			this.setLayout( null );
			this.add( this.spectrumLegend );
			Transformer e = new Transformer<Correlation,Paint>( ) {
				public Paint transform( Correlation e ) {
					if ( getPickedEdgeState( ).isPicked( e )) {
						return pickedEdgePaint;
					} else {
						return spectrum.getPaint( 
							e.getValue( correlationMethod ));
					}
				}
			};
			this.getRenderer( ).setEdgeRenderer( 
				new FastEdgeRenderer<Molecule,Correlation>( ));
			this.getRenderContext( ).setEdgeDrawPaintTransformer( e );
			this.getRenderContext( ).setVertexStrokeTransformer( 
				new Transformer<Molecule,Stroke>( ) {
					Stroke stroke = new BasicStroke( 2 );

					public Stroke transform( Molecule m ) {
						return this.stroke;
					}
				});
		}

    // optimization
    @Override
    public edu.uci.ics.jung.graph.util.Pair<Molecule> getEndpoints( 
      Correlation c ) {
      return new edu.uci.ics.jung.graph.util.Pair<Molecule> (
        c.getFirst( ), c.getSecond( ));
    }

		@Override
		public void setBackground( Color color ) {
			super.setBackground( color );
			if ( this.spectrumLegend != null )
				this.spectrumLegend.setBackground( color );
			if ( this.regulationLegend != null )
				this.regulationLegend.setBackground( color );
		}

		@Override
		public void setForeground( Color color ) {
			super.setForeground( color );
			if ( this.spectrumLegend != null )
				this.spectrumLegend.setForeground( color );
			if ( this.regulationLegend != null )
				this.regulationLegend.setForeground( color );
		}

		/**
		* Used to change the experiment which is displayed in this graph.
		* 
		* @param experiment The new experiment.
		*/
		public void setCorrelations( CorrelationSet correlations ) {
			// remove all edges and vertices.
			for ( Molecule v : this.getVertices( ))
				 this.removeVertex( v );
      synchronized( this ) {
        for ( Correlation e : this.getEdges( ))
           this.removeEdge( e );
      }
      // add the new data.
      this.correlations = correlations;
      this.molecules = correlations.getMolecules( );
			this.addVertices( );
			this.addEdges( );
		}

		/**
		 * The sampleGroupsChanged method of the SampleGroupChangeListener
		 * interface.
		 * 
		 * @param event The event which triggered this action.
		 */
		 public void groupStateChanged( SampleGroupChangeEvent event ) {
			 this.setSampleGroups( event.getGroups( ));
		 }


		/**
		 * Sets sample groups for up/downregulation indicators on the display.
		 * 
		 * @param sg The selected groups.
		 */
		public void setSampleGroups( Collection<SampleGroup> sg ) {
			this.sampleGroups = sg;
			this.getRenderContext( ).setVertexDrawPaintTransformer(
				new RegulationTransformer( 
					sg, this.upRegulationOutline, 
					this.downRegulationOutline, null ));
			if ( sg.size( ) > 1 ) {
				resetSampleGroupsMenuItem.setEnabled( true );
				setFoldChangeGroupsMenuItem.setEnabled( true );
				this.add( this.regulationLegend );
				this.componentMoved( new ComponentEvent( this, -1 ));
				multipleCirclesLayoutMenuItem.setEnabled( true );
			} else {
				if ( this.regulationLegend != null ) {
					this.remove( this.regulationLegend );
				}
				resetSampleGroupsMenuItem.setEnabled( false );
				setFoldChangeGroupsMenuItem.setEnabled( false );
			}
			this.repaint( );
		}

		/**
		 * Sets a new range to be used for filtering.
		 * 
		 * @param range The new MonitorableRange object to use.
		 */
		public void setRange( MonitorableRange range ) {
			if ( this.range != null )
				this.range.removeChangeListener( this );
			this.range = range;
			range.addChangeListener( this );
		}

		/**
		 * Gets the current MonitorableRange object being used.
		 * 
		 * @return The current MonitorableRange.
		 */
		public MonitorableRange getRange( ) {
			return this.range;
		}

		public CorrelationSet getCorrelations( ) {
			return this.correlations;
		}

		/**
		 * Adds the Vertices (Molecules) to the Graph
		 */
		protected void addVertices( ) {
      for( Molecule molecule : this.correlations.getMolecules( )) {
        this.graph.addVertex( molecule );
      }
		}

		/**
		 * Adds the Edges (Correlations) to the Graph.
		 */
		protected void addEdges( ) {
        synchronized( graph ) { 
          for( Correlation correlation : this.correlations ) {
            if ( this.isValidEdge( correlation )) {
              this.graph.addEdge( 
                correlation, 
                new edu.uci.ics.jung.graph.util.Pair <Molecule> ( 
                  correlation.toArray( new Molecule[ 2 ])),
                EdgeType.UNDIRECTED );
            }
          }
        }
		}

		/**
		 * Filters the edges displayed in the graph based on the MonitorableRange
		 * associated with this graph.
		 * 
		 * @return The new number of edges contained in the graph.
		 */
		public void filterEdges( ) {
//      this.updateDaemon.update( new DaemonListener( ) {
//        public void daemonUpdate( ) {
          synchronized( graph ) { 
            for( Correlation correlation : correlations ) {
              if ( isValidEdge( correlation )) {
                // this Correlation belongs on the graph, make sure it is there.
                if ( !containsEdge( correlation )) {
                  addEdge( correlation, 
                           new edu.uci.ics.jung.graph.util.Pair<Molecule>( 
                           correlation.getFirst( ), correlation.getSecond( )),
                           EdgeType.UNDIRECTED );
                }
              } else {
                // this Correlation does not belong on the graph, make sure it is 
                // not there.
                if ( containsEdge( correlation )) {
                  getPickedEdgeState( ).pick( correlation, false );
                  try {
                    removeEdge( correlation );
                  } catch ( NullPointerException e ) {
                    Logger.getLogger( getClass( )).debug( "Removal of edge " + 
                      correlation + " failed!", e );
                  }
                }
              }
            }
          }
          repaint( );
//        }
//      });
    }

		/**
		 * Checks to see if an edge is valid and belongs in the graph.
		 * 
		 * @param correlation The edge to check.
		 * @return true If the correlation should be shown, false otherwise.
		 */
		public boolean isValidEdge( Correlation correlation ) {
			return ( this.containsVertex( correlation.getFirst( )) &&
							 this.containsVertex( correlation.getSecond( )) &&
							 this.range.contains( 
								 Math.abs( correlation.getValue( correlationMethod ))));
		}

		/**
		 * The stateChanged method of the ChangeListener interface.
		 * @see ChangeListener#stateChanged(ChangeEvent)
		 * 
		 * @param event The event which triggered this action.
		 */
		public void stateChanged( ChangeEvent event ) {
			this.filterEdges( );
		}

		/**
		 * The graphClicked method of the GraphMouseListener interface.
		 * @see GraphMouseListener#graphClicked(V,MouseEvent)
		 * 
		 * @param edge The edge which was clicked on to trigger the event.
		 * @param event The event which triggered this action.
		 */
		public void graphClicked( Correlation edge, MouseEvent event ) {
			if ( event.getButton( ) == MouseEvent.BUTTON1 ) {
				PickedState <Molecule> state = this.getPickedVertexState( );
				// see if shift or ctrl is being held down
				if(( event.getModifiers( ) & 
					 ( InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK )) == 0 ) 
						state.clear( );
				state.pick( edge.getFirst( ), true );
				state.pick( edge.getSecond( ), true );
			}
		}

		/**
		 * The graphPressed method of the GraphMouseListener interface. Not 
		 * implemented.
		 * 
		 * @param edge The edge which was clicked on to trigger the event.
		 * @param event The event which triggered this action.
		 */
		public void graphPressed( Correlation edge, MouseEvent event ) { }

		/**
		 * The graphReleased method of the GraphMouseListener interface. Not 
		 * implemented.
		 * 
		 * @param edge The edge which was clicked on to trigger the event.
		 * @param event The event which triggered this action.
		 */
		public void graphReleased( Correlation edge, MouseEvent event ) { }

		// ComponentListener Methods
		public void componentHidden( ComponentEvent e ) { }
		public void componentMoved( ComponentEvent e ) {
			int bottom, left, right;
			if ( this.scrollPane != null ) {
				Rectangle view = this.scrollPane.getViewport( ).getViewRect( );
				left = view.x;
				bottom = view.y + view.height;
				right = view.x + view.width;
			} else {
				left = 0;
				bottom = this.getHeight( );
				right = this.getWidth( );
			}
			Rectangle legendArea = new Rectangle( left + 20, bottom - 35, 150, 20);
			this.spectrumLegend.setBounds( legendArea );
			this.spectrumLegend.repaint( );
			if ( this.sampleGroups.size( ) > 1 ) {
				legendArea = new Rectangle( right - 130, bottom - 120, 130, 120);
				this.regulationLegend.setBounds( legendArea );
				this.regulationLegend.repaint( );
			}
		}
		public void componentResized( ComponentEvent e ) { 
			componentMoved( e );
		}
		public void componentShown( ComponentEvent e ) { }

		// ====================== RegulationTransformer ============================
		/**
		 * A class for deteriming the outline  color of a Molecule vertex.
		 */
		private class RegulationTransformer implements Transformer<Molecule,Paint> {
			private Pair<SampleGroup> sampleGroups;
			private Paint upPaint;
			private Paint downPaint;
			private Paint neutralPaint;

			/**
			 * Creates a new RegulationTransformer.
			 * 
			 * @param sg A pair of SampleGroups to be used for determining up or
			 *  downregulation.
			 * @param upPaint The Paint to be used for outlining upregulated 
			 *	Molecules.
			 * @param downPaint The Paint to be used for outlining downregulated
			 *  Molecules.
			 * @param neutralPaint The Paint to be used for outlining Molecules which
			 *  are neither up or downregulated.
			 */
			public RegulationTransformer ( Pair<SampleGroup> sg, 
																		 Paint upPaint,
																		 Paint downPaint, 
																		 Paint neutralPaint ) {
				this.sampleGroups = sg;
				this.upPaint = upPaint;
				this.downPaint = downPaint;
				this.neutralPaint = neutralPaint;
			}

			/**
			 * Creates a new RegulationTransformer.
			 * 
			 * @param sg A Collection of SampleGroups to be used for determining up or
			 *  downregulation. The Collection should contain at least 2 Samplegroups.
			 * @param upPaint The Paint to be used for outlining upregulated 
			 *	Molecules.
			 * @param downPaint The Paint to be used for outlining downregulated
			 *  Molecules.
			 * @param neutralPaint The Paint to be used for outlining Molecules which
			 *  are neither up or downregulated.
			 */
			public RegulationTransformer ( Collection<SampleGroup> sg, 
																		 Paint upPaint,
																		 Paint downPaint, 
																		 Paint neutralPaint ) {
				if ( sg instanceof Pair ) {
					this.sampleGroups = (Pair<SampleGroup>)sg;
				} else {
					if ( sg.size( ) >= 2 ) {
						Iterator<SampleGroup> iterator = sg.iterator( );
						this.sampleGroups = 
							new SimplePair( iterator.next( ), iterator.next( ));
					}
				}
				this.upPaint = upPaint;
				this.downPaint = downPaint;
				this.neutralPaint = neutralPaint;
			}

			/**
			 * Returns the appropriate Paint for the indicated molecule.
			 * 
			 * @param m The Molecule to determine the outline Paint for.
			 * @return The Paint to use.
			 */
			public Paint transform( Molecule m ) {
        Settings settings = Settings.getSettings( );
        int regulation = 
          CorrelationDisplayPanel.getRegulation( m, this.sampleGroups,
            settings.getDouble( "preferences.correlation.foldChange", 2.0 ),
            settings.getBoolean( "preferences.correlation.logData", false ));
        if ( regulation > 0 ) {
          return this.upPaint;
        }
        if ( regulation < 0 ) {
          return this.downPaint;
        }
				return this.neutralPaint;
			}
		}

    
	}

	// ====================== RegulationLegend =================================
	private class RegulationLegend extends JPanel {
		private Color upColor;
		private Color downColor;
		private Color nodeColor;
		private int scaleSpace = 13;
		private boolean drawBorder = true;
		private NumberFormat numberFormat = new DecimalFormat( "0.##" );

		/**
		 * Creates a new RegulationLegend
		 * 
		 * @param spectrum The Spectrum to create a legend for.
		 * @param range The range of values to show on the legend.
		 */
		public RegulationLegend ( Color upColor, Color downColor, 
		                          Color nodeColor ) {
			super( );
			this.upColor = upColor;
			this.downColor = downColor;
			this.nodeColor = nodeColor;
			this.setForeground( Color.BLACK );
		}

		protected void paintComponent( Graphics g ) {
			super.paintComponent( g );
			stamp( g, new Rectangle( 0, 0, this.getWidth( ), this.getHeight( )));
		}

		/**
		 * "Stamps" the Graphics instance with the legend in the area specified.
		 * 
		 * @param g The Graphics object to "stamp"
		 * @param area The area of the Graphics object to stamp.
		 */
		public void stamp( Graphics g, Rectangle area ) {
			// set up a smaller font
			Font origFont = g.getFont( );
			Language language = Settings.getLanguage( );
			int bottom = area.x + area.height;
			int right = area.y + area.width;
			int verticalCenter = area.x + area.height/2;
			int horizCenter = area.y + area.width/2;

			g.setFont( new Font( Font.SANS_SERIF, Font.PLAIN, 12 ));
			FontMetrics f = g.getFontMetrics( );
			String s = language.get( "Regulation" );
			g.drawString( s, area.x + area.width / 2 - f.stringWidth( s ) / 2, 20 );
			s = language.get( "Up" );
			g.drawString( s, horizCenter - 25 - f.stringWidth( s ) / 2, 60 );
			s = language.get( "Down" );
			g.drawString( s, horizCenter + 25 - f.stringWidth( s ) / 2, 60 );
			s = language.get( "Fold change: " ) + 
				Settings.getSettings( ).getDouble( 
				"preferences.correlation.foldChange", 2.0 );
			g.drawString( s, horizCenter - f.stringWidth( s ) / 2, 80 );
			s = language.get( "Group2 / Group1" );
			g.drawString( s, horizCenter - f.stringWidth( s ) / 2, 100 );

			// reset the font
			g.setFont( origFont );

			// draw the legend
			g.setColor( this.nodeColor );
			g.fillOval( horizCenter - 35, 27, 20, 20 );
			g.fillOval( horizCenter + 15, 27, 20, 20 );
			((Graphics2D)g).setStroke( new BasicStroke( 2 ));
			g.setColor( this.upColor );
			g.drawOval( horizCenter - 35, 27, 20, 20 );
			g.setColor( this.downColor );
			g.drawOval( horizCenter + 15, 27, 20, 20 );
			g.setColor( this.getForeground( ));

		}

		public void setBounds( Rectangle rect ) {
			Graphics g = this.getGraphics( );
			if ( g != null ) {
				g.setColor( this.getParent( ).getBackground( ));
				g.fillRect( 0, 0, this.getWidth( ), this.getHeight( ));
			}
			super.setBounds( rect );
		}
	}
}



