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

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.NumberList;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.ProcessUtils;
import edu.purdue.bbc.util.attributes.Attributes;
import edu.purdue.bbc.util.attributes.BasicAttributes;
import edu.purdue.cc.bionet.io.ProjectInfoWriter;
import edu.purdue.cc.bionet.ui.ContextMenu;
import edu.purdue.cc.bionet.util.ExperimentSet;
import edu.purdue.cc.bionet.util.Molecule;
import edu.purdue.cc.bionet.util.Project;
import edu.purdue.cc.bionet.util.Sample;
import edu.purdue.cc.bionet.util.SampleGroup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

public class ProjectDisplayPanel extends AbstractDisplayPanel 
	implements ActionListener,TableModelListener,TableColumnModelListener {

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

		this.metadataTable = new EditorTable( ) {
			public boolean isCellEditable( int row, int column ) {
				return column != 0;
			}
		};
		this.metadataScrollPane = new JScrollPane( );
		Attributes attributes = new BasicAttributes( false );
		attributes.setAttribute( "sample file", "" );
		attributes.setAttribute( "time", "" );
		this.newTable( attributes );
		this.projectTextField = new JTextField( );
		JPanel projectPanel = new JPanel( new BorderLayout( ));
		this.descriptionTextArea = new JTextArea( );
		JPanel descriptionPanel = new JPanel( new BorderLayout( ));
		this.analyticalPlatformTextField = new JTextField( );
		this.msModeTextField = new JTextField( );
		this.methodTextArea = new JTextArea( );
		JPanel methodPanel = new JPanel( new BorderLayout( ));
		this.projectNameLabel = new JLabel( 
			language.get( "Project Name" ) + ":" );
		this.projectNameLabel.setBorder( 
			BorderFactory.createEmptyBorder( 0, 5, 0, 5 ));
		this.descriptionLabel = new JLabel( language.get( "Description" ) + ":");
		JPanel descriptionLabelPanel = new JPanel( new BorderLayout( ));
		this.descriptionLabel.setBorder( 
			BorderFactory.createEmptyBorder( 0, 5, 0, 5 ));
		this.analyticalPlatformLabel = new JLabel( 
			language.get( "Analytical Platform" ) + ":" );
		this.analyticalPlatformLabel.setBorder( 
			BorderFactory.createEmptyBorder( 0, 0, 0, 5 ));
		this.msModeLabel = new JLabel( language.get( "MS Mode" ) + ":" );
		this.msModeLabel.setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 5 ));
		this.methodLabel = new JLabel( language.get( "Chromatography Method" ) + ":");
		JPanel methodLabelPanel = new JPanel( new BorderLayout( ));
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
		JScrollPane methodScrollPane = new JScrollPane( this.methodTextArea );
		methodScrollPane.setPreferredSize( new Dimension( 100, 100 ));
		methodPanel.add( methodScrollPane, BorderLayout.CENTER );
		methodPanel.setBorder( BorderFactory.createCompoundBorder( 
			BorderFactory.createEmptyBorder( 5, 5, 5, 5 ),
			BorderFactory.createLineBorder( Color.GRAY )));
		this.msExperimentPanel.add( methodPanel, BorderLayout.CENTER );
		methodLabelPanel.add( this.methodLabel, BorderLayout.NORTH );
		this.msExperimentPanel.add( methodLabelPanel, BorderLayout.WEST );
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
		upperMsExperimentPanel.setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 5 ));
		this.msExperimentPanel.add( upperMsExperimentPanel, BorderLayout.NORTH );

		JPanel projectNamePanel = new JPanel( new BorderLayout( ));
		projectNamePanel.add( this.projectNameLabel, BorderLayout.WEST );
		projectPanel.add( this.projectTextField, BorderLayout.CENTER );
		projectPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 0, 5 ));
		projectNamePanel.add( projectPanel, BorderLayout.CENTER );

		JPanel upperPanel = new JPanel( new BorderLayout( ));
		upperPanel.add( this.msExperimentPanel, BorderLayout.SOUTH );
		JScrollPane descriptionScrollPane = 
			new JScrollPane( this.descriptionTextArea );
		descriptionScrollPane.setPreferredSize( new Dimension( 100, 100 ));
		descriptionPanel.add( descriptionScrollPane, BorderLayout.CENTER );
		descriptionPanel.setBorder( BorderFactory.createCompoundBorder( 
			BorderFactory.createEmptyBorder( 5, 5, 5, 5 ),
			BorderFactory.createLineBorder( Color.GRAY )));
		upperPanel.add( descriptionPanel, BorderLayout.CENTER );
		descriptionLabelPanel.add( this.descriptionLabel, BorderLayout.NORTH );
		upperPanel.add( descriptionLabelPanel, BorderLayout.WEST );
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
	 * This method is invalid for this type. Always returns false.
	 * 
	 * @param experiment The experiment to be associated with this instance.
	 * @return Always returns false.
	 */
	public boolean createView( ExperimentSet experiments ) {
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
		if ( project.hasAttribute( "Analytical Platform" )) {
			this.analyticalPlatformTextField.setText( 
				project.getAttribute( "Analytical Platform" ));
		}
		if ( project.hasAttribute( "Description" )) {
		this.descriptionTextArea.setText( 
			project.getAttribute( "Description" ).replace( "<CR>", "\n" ));
		}
		if ( project.hasAttribute( "MS Method" )) {
		this.msModeTextField.setText( 
			project.getAttribute( "MS Method" ));
		}
		if ( project.hasAttribute( "Chromatography Method" )) {
			this.methodTextArea.setText( 
				project.getAttribute( "Chromatography Method" ).replace( "<CR>", "\n" ));
		}
		this.samples.addAll( project.getSamples( ));
		Collection<SampleGroup> sampleGroups = new TreeSet<SampleGroup>( );
		sampleGroups.add( new SampleGroup( "All Samples", samples ));
		this.setSampleGroups( sampleGroups );
		Iterator<Sample> sampleIterator = samples.iterator( );
		Sample sample = null;
		if ( sampleIterator.hasNext( )) {
			sample = sampleIterator.next( );
		}
		if ( sample != null ) {
			DefaultTableModel tableModel = 
				(DefaultTableModel)this.newTable( sample ).getModel( );
			sampleIterator = samples.iterator( );
			while ( sampleIterator.hasNext( )) {
				sample = sampleIterator.next( );
				int columnCount = this.metadataTable.getColumnCount( );
				Object[] row = new Object[ columnCount ];
				for ( int i=0; i < columnCount; i++ ) {
					row[ i ] = 
						sample.getAttribute( this.metadataTable.getColumnName( i ));
				}
				tableModel.addRow( row );
			}
		}
		return true;
	}

	private JTable newTable( Attributes attributes ) {
		if ( this.metadataTable != null )
			this.metadataTable.getColumnModel( ).removeColumnModelListener( this );
		this.metadataTable = new EditorTable( ) {
			public boolean isCellEditable( int row, int column ) {
				return column != 0;
			}
		};
		Collection<String> attributeKeys = 
			new TreeSet<String>( attributes.getAttributes( ).keySet( ));
		attributeKeys.remove( "sample file" );
		DefaultTableModel tableModel =
			(DefaultTableModel)this.metadataTable.getModel( );
		tableModel.addColumn( "sample file" );
		for( String attribute : attributeKeys ) {
			tableModel.addColumn( attribute );
		}
		this.metadataScrollPane.setViewportView( this.metadataTable );
		this.metadataTable.getColumnModel( ).addColumnModelListener( this );
		return this.metadataTable;
	}

	public void addExperiment( ExperimentSet experiment ) {
		this.project.add( experiment );	
	}

	public Sample addSample( String sampleName ) {
		Sample sample = this.project.getSample( sampleName );
		if ( sample != null ) {
			return sample;
		}
		if ( this.project.getSample( sampleName ) == null ) {
			sample = new Sample( sampleName );
			sample.setAttribute( "Sample File", sampleName );
			sample.setAttribute( "Time", "0" );
		}
		return this.addSample( sample );
	}

	public Sample addSample( Sample sample ) {
		Sample returnValue = this.project.getSample( sample.toString( ));
		if ( returnValue != null ) {
			return returnValue;
		}
		int columnCount = this.metadataTable.getColumnCount( );
		Object[] newRow = new Object[ columnCount ];
		this.samples.add( sample );
		this.project.addSample( sample );
		for( int i=0; i < columnCount; i++ ) {
			String value = 
				sample.getAttribute( this.metadataTable.getColumnName( i ));
			newRow[ i ] = ( value == null ) ? "" : value;
		}
		((DefaultTableModel)this.metadataTable.getModel( )).addRow( newRow );
		this.setProjectModified( true );
		return sample;
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
		String newValue = analyticalPlatformTextField.getText( );
		String oldValue = project.setAttribute( "Analytical Platform", newValue );
		if ( newValue != null && !newValue.equals( oldValue )) {
			this.setProjectModified( true );
		}

		newValue = descriptionTextArea.getText( ).
			replace( "\n", "<CR>" ).replace( "\r", "" );
		oldValue = project.setAttribute( "Description", newValue );

		if ( newValue != null && !newValue.equals( oldValue )) {
			this.setProjectModified( true );
		}

		newValue = msModeTextField.getText( );
		oldValue = project.setAttribute( "MS Method", newValue );
		if ( newValue != null && !newValue.equals( oldValue )) {
			this.setProjectModified( true );
		}

		newValue = methodTextArea.getText( ).
			replace( "\n", "<CR>" ).replace( "\r", "" );
		oldValue = project.setAttribute( "Chromatography Method", newValue );
		if ( newValue != null && !newValue.equals( oldValue )) {
			this.setProjectModified( true );
		}

		return this.isProjectModified( );
	}

	public boolean updateSamples( ) {
		Logger logger = Logger.getLogger( getClass( ));
		TableModel model = metadataTable.getModel( );
		int nameColumn = 0;
		for ( int i=0; i < model.getColumnCount( ); i++ ) {
			if ( model.getColumnName( i ).toLowerCase( ).equals( "sample file" )) {
				nameColumn = i;
			}
		}
		for ( int i=0; i < model.getRowCount( ); i++ ) {
			String sampleName = model.getValueAt( i, nameColumn ).toString( );
			Sample sample = project.getSample( sampleName );
			for ( int j=0; j < model.getColumnCount( ); j++ ) {
				Object attribute = model.getColumnName( j );
				if ( attribute != null ) {
					if ( !attribute.toString( ).toLowerCase( ).equals( "sample file" )) {
						if ( sample != null ) {
							Object value = model.getValueAt( i, j );
							if ( value == null )
								value = new String( );
							if ( !value.equals( sample.getAttribute( attribute.toString( )))) {
								logger.debug( String.format( "Setting %s to %s for sample %s",
									attribute, value, sample ));
								sample.setAttribute( attribute.toString( ), value.toString( ));
								this.setProjectModified( true );
							}
						} else {
							Logger.getLogger( getClass( )).debug( "Sample '" + sampleName +
								"' not found. Unable to update." );
						}
					}
				}
			}
		}
		return this.isProjectModified( );
	}

	public void tableChanged( TableModelEvent e ) {
		this.updateSamples( );
	}
	public void columnAdded( TableColumnModelEvent e ) {
		this.updateSamples( );
	}
	public void columnMarginChanged( ChangeEvent e ) { }
	public void columnMoved( TableColumnModelEvent e ) { }
	public void columnRemoved( TableColumnModelEvent e ) { 
		Logger.getLogger( getClass( )).debug( 
			String.format( "Column %d removed", e.getFromIndex( )));
		// clear the sample attributes
		for ( Sample sample : this.samples ) {
			sample.getAttributes( ).clear( );
			sample.setAttribute( "sample file", sample.toString( ));
		}
		this.updateSamples( );
	}
	public void columnSelectionChanged( ListSelectionEvent e ) { }

	public boolean isProjectModified( ) {
		return this.projectModified;
	}

	private void setProjectModified( boolean modified ) {
		if ( modified ) {
			Logger.getLogger( getClass( )).debug( 
				"Project marked as modified by " + ProcessUtils.getCallerDescription( ));
		}
		this.projectModified = modified;
	}

	public boolean saveProject( ) {
		if ( this.project != null ) {
			return this.saveProject( this.project );
		}
		Logger.getLogger( getClass( )).error( 
				"Unable to save the project, there is no project currently open." );
		return false;
	}

	public boolean saveProject( Project project ) {
		Logger logger = Logger.getLogger( getClass( ));
		this.updateProject( project );
		this.updateSamples( );
		try {
			new ProjectInfoWriter( project ) .write( );
			this.setProjectModified( false );
		} catch ( java.io.IOException exception ) {
			logger.debug( exception, exception );
			logger.error( 
				"There was an error when trying to save your project file.\n" +
				"Please check to make sure the path still exists." );
			return false;
		}
		return true;
	}

}


