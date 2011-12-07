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
import edu.purdue.bbc.util.NumberList;
import edu.purdue.bbc.util.Settings;
import edu.purdue.cc.sysnet.util.Experiment;
import edu.purdue.cc.sysnet.util.ExperimentSet;
import edu.purdue.cc.sysnet.util.Molecule;
import edu.purdue.cc.sysnet.util.Project;
import edu.purdue.cc.sysnet.util.Sample;
import edu.purdue.cc.sysnet.util.SampleGroup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

public class ProjectDisplayPanel extends AbstractDisplayPanel 
                                 implements ActionListener {
//	private JMenuBar menuBar;
//	private JMenu projectMenu;
//	private JMenuItem saveProjectMenuItem;
	private JMenuItem importNormalizationProjectMenuItem;
	private JScrollPane metadataScrollPane;
	private JTable metadataTable;
	private JTextField projectTextField;
	private JTextArea descriptionTextArea;
	private JTextField analyticalPlatformTextField;
	private JTextField msModeTextField;
	private JTextArea methodTextArea;
	private JLabel projectNameLabel;
	private JLabel descriptionLabel;
	private JLabel analyticalPlatformLabel;
	private JLabel msModeLabel;
	private JLabel methodLabel;
	private JLabel sampleInformationLabel;
	private JPanel msExperimentPanel;

//	private Collection<Molecule> molecules;
	private Collection<Sample> samples;
	private Project project;
	private boolean projectModified = false;

	/**
	 * A class for displaying information about a Clustering
	 */
	public ProjectDisplayPanel( ) {
		super( new BorderLayout( ));
		Language language = Settings.getLanguage( );
//		this.menuBar = new JMenuBar( );
//		this.projectMenu = new JMenu( language.get( "Project" ));
//		this.importNormalizationProjectMenuItem = new JMenuItem( 
//			language.get( "Import Normalization" ));

		this.metadataTable = new JTable( ){ 
			public boolean isCellEditable( int row, int column ) {
				return column != 0;
			}
		};
		this.metadataScrollPane = new JScrollPane( this.metadataTable );
		this.projectTextField = new JTextField( );
		this.descriptionTextArea = new JTextArea( );
		this.descriptionTextArea.setPreferredSize( new Dimension( 100, 100 ));
		this.analyticalPlatformTextField = new JTextField( );
		this.msModeTextField = new JTextField( );
		this.methodTextArea = new JTextArea( );
		this.methodTextArea.setPreferredSize( new Dimension( 100, 100 ));
		this.projectNameLabel = new JLabel( 
			language.get( "Project Name" ) + ":" );
		this.projectNameLabel.setBorder( 
			BorderFactory.createEmptyBorder( 0, 5, 0, 5 ));
		this.descriptionLabel = new JLabel( language.get( "Description" ) + ":");
		this.descriptionLabel.setBorder( 
			BorderFactory.createEmptyBorder( 0, 5, 0, 5 ));
		this.analyticalPlatformLabel = new JLabel( 
			language.get( "Analytical Platform" ) + ":" );
		this.analyticalPlatformLabel.setBorder( 
			BorderFactory.createEmptyBorder( 0, 5, 0, 5 ));
		this.msModeLabel = new JLabel( language.get( "MS Mode" ) + ":" );
		this.msModeLabel.setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 5 ));
		this.methodLabel = new JLabel( language.get( "Chromotography Method" ));
		this.methodLabel.setBorder( BorderFactory.createEmptyBorder( 10, 5, 0, 5 ));
		this.sampleInformationLabel = new JLabel( 
			language.get( "Sample Information" ));

		this.msExperimentPanel = new JPanel( new BorderLayout( ));
		this.msExperimentPanel.setBorder( BorderFactory.createTitledBorder( 
				BorderFactory.createLineBorder( Color.BLACK, 1 ),
				Settings.getLanguage( ).get( "MS Experiment" ),
				TitledBorder.LEFT,
				TitledBorder.TOP
		));
		this.msExperimentPanel.add( this.methodTextArea, BorderLayout.CENTER );
		this.msExperimentPanel.add( this.methodLabel, BorderLayout.WEST );
		JPanel upperLeftMsExperimentPanel = new JPanel( new BorderLayout( ));
		JPanel upperRightMsExperimentPanel = new JPanel( new BorderLayout( ));
		JPanel upperMsExperimentPanel = new JPanel( new GridLayout( 1, 2 ));
		upperLeftMsExperimentPanel.add( this.analyticalPlatformLabel, 
		                                BorderLayout.WEST );
		upperLeftMsExperimentPanel.add( this.analyticalPlatformTextField, 
		                                BorderLayout.CENTER );
		upperRightMsExperimentPanel.add( this.msModeLabel, BorderLayout.WEST );
		upperRightMsExperimentPanel.add( this.msModeTextField, BorderLayout.CENTER );
		upperMsExperimentPanel.add( upperLeftMsExperimentPanel );
		upperMsExperimentPanel.add( upperRightMsExperimentPanel );
		this.msExperimentPanel.add( upperMsExperimentPanel, BorderLayout.NORTH );

		JPanel projectNamePanel = new JPanel( new BorderLayout( ));
		projectNamePanel.add( this.projectNameLabel, BorderLayout.WEST );
		projectNamePanel.add( this.projectTextField, BorderLayout.CENTER );

		JPanel upperPanel = new JPanel( new BorderLayout( ));
		upperPanel.add( this.msExperimentPanel, BorderLayout.SOUTH );
		upperPanel.add( this.descriptionTextArea, BorderLayout.CENTER );
		upperPanel.add( this.descriptionLabel, BorderLayout.WEST );
		upperPanel.add( projectNamePanel, BorderLayout.NORTH );
		

		JPanel mainPanel = new JPanel( new BorderLayout( ));
		mainPanel.add( upperPanel, BorderLayout.NORTH );
		mainPanel.add( this.metadataScrollPane, BorderLayout.CENTER );
		this.add( mainPanel, BorderLayout.CENTER );
//		this.add( this.menuBar, BorderLayout.NORTH );
//		this.projectMenu.add( this.importNormalizationProjectMenuItem );
//		this.importNormalizationProjectMenuItem.addActionListener( this );
//		this.projectMenu.add( this.saveProjectMenuItem );
//		this.saveProjectMenuItem.addActionListener( this );
//		this.menuBar.add( this.projectMenu );

	}

	/**
	 * Creates the visualization instance for a ProjectDisplayPanel. Creates
	 * a new Project with the given ExperimentSet.
	 * 
	 * @param experiments The experiments to be associated with this instance.
	 * @return true if creating the visualization succeeded.
	 */
	public boolean createView( Collection<Experiment> experiments ) {
		return false;
	}
		
	/**
	 * Creates the visualization instance for a ClusteringDisplayPanel
	 * 
	 * @param project The project to be associated with this instance.
	 * @return true if creating the visualization succeeded.
	 */
	public boolean createView( Project project ) {
		Logger logger = Logger.getLogger( getClass( ));
		this.project = project;
		this.samples = new SampleGroup( "All Samples" );
//		this.molecules = new TreeSet<Molecule>( );
		this.projectTextField.setText( 
			project.getAttribute( "Project Name" ));
		this.analyticalPlatformTextField.setText( 
			project.getAttribute( "Analytical Platform" ));
		this.descriptionTextArea.setText( 
			project.getAttribute( "Description" ));
		this.msModeTextField.setText( 
			project.getAttribute( "MS Method" ));
		this.samples.addAll( project.getSamples( ));
//		for ( Experiment experiment : experiments ) {
//			this.molecules.addAll( experiment.getMolecules( ));
//		}
		Collection<SampleGroup> sampleGroups = new TreeSet<SampleGroup>( );
		sampleGroups.add( new SampleGroup( "All Samples", samples ));
		this.setSampleGroups( sampleGroups );
		DefaultTableModel tableModel = 
			(DefaultTableModel)this.metadataTable.getModel( );
		Iterator<Sample> sampleIterator = samples.iterator( );
		Sample sample = null;
		if ( sampleIterator.hasNext( )) {
			sample = sampleIterator.next( );
		}
		if ( sample != null ) {
			Collection<String> attributes = 
				new TreeSet<String>( sample.getAttributes( ).keySet( ));
			attributes.remove( "sample file" );
			tableModel.addColumn( "sample file" );
			for( String attribute : attributes ) {
				tableModel.addColumn( attribute );
			}
			do {
				Object[] row = new Object[ attributes.size( ) + 1 ];
				int i = 0;
				row[ i++ ] = sample.getAttribute( "sample file" );
				for ( String attribute : attributes ) {
					row[ i++ ] = sample.getAttribute( attribute );
				}
				tableModel.addRow( row );
				if ( sampleIterator.hasNext( ))
					sample = sampleIterator.next( );
			} while ( sampleIterator.hasNext( ));
		}
		return true;
	}

	/**
	 * Gets the title of this panel.
	 * 
	 * @return The title of this panel as a String.
	 */
	public String getTitle( ) {
		return "Project Information";
	}

	/**
	 * The actionPerformed method of the ActionListener interface.
	 * 
	 * @param e The event which triggered this action.
	 */
	public void actionPerformed( ActionEvent e ) {
		Language language = Settings.getLanguage( );
		Object source = e.getSource( );
	}

	@Deprecated
	public boolean updateProject( ) {
		return this.updateProject( this.project );
	}

	public boolean updateProject( Project project ) {
		String newValue;
		newValue = analyticalPlatformTextField.getText( );
		if ( !newValue.equals( 
			project.setAttribute( "Analytical Platform", newValue )))
			this.projectModified = true;

		newValue = descriptionTextArea.getText( ).
			replace( "\n", "<CR>" ).replace( "\r", "" );
		if ( !newValue.equals( 
			project.setAttribute( "Description", newValue )))
			this.projectModified = true;

		newValue = msModeTextField.getText( );
		if ( !newValue.equals( 
			project.setAttribute( "MS Method", newValue )))
			this.projectModified = true;

		TableModel model = metadataTable.getModel( );
		int nameColumn = 0;
		for ( int i=0; i < model.getColumnCount( ); i++ ) {
			if ( model.getColumnName( i ).toLowerCase( ).equals( "sample file" )) {
				nameColumn = i;
			}
		}
		for ( int i=0; i < model.getRowCount( ); i++ ) {
			Sample sample = project.getSample( 
				model.getValueAt( i, nameColumn ).toString( ));
			for ( int j=0; j < model.getColumnCount( ); j++ ) {
				if ( !model.getColumnName( j ).toLowerCase( ).equals( "sample file" )) {
					sample.setAttribute( 
						model.getColumnName( j ).toString( ),
						model.getValueAt( i, j ).toString( ));
				}
			}
		}
		return this.projectModified;
	}

	public boolean isProjectModified( ) {
		return this.projectModified;
	}

	// ugly hack
	void setProjectModified( boolean modified ) {
		this.projectModified = modified;
	}

	// ============================= PRIVATE CLASSES =============================
}


