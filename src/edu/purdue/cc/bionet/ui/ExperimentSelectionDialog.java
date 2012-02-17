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

import edu.purdue.bbc.util.attributes.AttributesFilterList;
import edu.purdue.bbc.util.attributes.Criterion;
import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;
import edu.purdue.cc.bionet.BioNet;
import edu.purdue.cc.bionet.util.ExperimentSet;
import edu.purdue.cc.bionet.util.Sample;
import edu.purdue.cc.bionet.util.SampleGroup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

/**
 * Creates a dialog for choosing from the available Samples.
 */
public class ExperimentSelectionDialog extends JDialog
		implements ActionListener {

	public static final int CORRELATION_VIEW = 0;
	public static final int COMPARATIVE_ANALYSIS_VIEW = 1;
	public static final int TIME_COURSE_STUDY_VIEW = 2;
	protected JButton okButton;
	protected JButton cancelButton;
	protected JRadioButton correlationButton;
	protected JRadioButton comparativeAnalysisButton;
	protected JRadioButton timeCourseStudyButton;
	protected ButtonGroup visualizationTypeSelection;
	protected AttributePanel attributePanel;
	protected FrequencyFilterPanel frequencyFilterPanel;
	protected Map.Entry <Integer,ExperimentSet> returnValue;
	protected final String chooseText;
	protected final String correlationButtonText;
	protected final String timeCourseStudyButtonText;
	protected final String comparativeAnalysisButtonText;
	protected final String okButtonText;
	protected final String cancelButtonText;

	private SampleGroup samples;

	/**
	 * Creates a new ExperimentSelectionDialog.
	 * 
	 * @param owner The parent frame of this Dialog. The parent frame will not
	 *	accept user input until the dialog is closed.
	 * @param title The title to be displayed in the title bar of this dialog.
	 * @param experiment The available experiment to be selected from.
	 */
	public ExperimentSelectionDialog( Frame owner, 
	                                  String title, 
																		SampleGroup samples ) {
		super( owner, title );
		this.samples = samples;
		this.getContentPane( ).setLayout( new BorderLayout( ));
		this.setBounds( Settings.getSettings( ).getInt( "window.main.position.x" ),
		                Settings.getSettings( ).getInt( "window.main.position.y" ),
										0, 0 );

		Language language = Settings.getLanguage( );
		this.chooseText = language.get( "Choose Sample Attributes" );
		this.correlationButtonText = language.get( "Correlation" );
		this.timeCourseStudyButtonText = language.get( "Clustering" );
		this.comparativeAnalysisButtonText = language.get( "Distribution Analysis" );
		this.okButtonText = language.get( "Select" );
		this.cancelButtonText = language.get( "Cancel" );

		this.okButton = new JButton( okButtonText );
		this.cancelButton = new JButton( cancelButtonText );
		this.correlationButton = new JRadioButton( correlationButtonText );
		this.comparativeAnalysisButton = 
			new JRadioButton( comparativeAnalysisButtonText );
		this.timeCourseStudyButton = new JRadioButton( timeCourseStudyButtonText );
		this.visualizationTypeSelection = new ButtonGroup( );
		if ( this.samples.size( ) == 0 ) {
			Logger.getLogger( getClass( )).error(
				"This experiment does not appear to contain any valid samples." );
			return;
		}
		this.attributePanel = new AttributePanel( samples );
		this.frequencyFilterPanel = 
			new FrequencyFilterPanel( samples.getAttributeNames( ));

		JPanel selectionPanel = new JPanel( new GridLayout( 1, 2 ));
		JPanel listPanel = new JPanel( new BorderLayout( ));
		JPanel buttonPanel = new JPanel( new GridLayout( 1, 2 ));
		JPanel viewTypePanel = new JPanel( new GridLayout( 3, 1 ));
		JPanel mainPanel = new JPanel( new BorderLayout( ));

		this.okButton.addActionListener( this );
		this.cancelButton.addActionListener( this );

		buttonPanel.add( this.okButton );
		buttonPanel.add( this.cancelButton );
		listPanel.add( this.attributePanel, BorderLayout.CENTER );
		viewTypePanel.add( this.correlationButton );
		viewTypePanel.add( this.comparativeAnalysisButton );
		viewTypePanel.add( this.timeCourseStudyButton );
		selectionPanel.add( listPanel, BorderLayout.CENTER );
		selectionPanel.add( viewTypePanel );
		mainPanel.add( selectionPanel );
		mainPanel.add( frequencyFilterPanel, BorderLayout.SOUTH );
		this.add( mainPanel, BorderLayout.CENTER );
		this.add( buttonPanel, BorderLayout.SOUTH );

		this.visualizationTypeSelection.add( this.correlationButton );
		this.visualizationTypeSelection.add( this.comparativeAnalysisButton );
		this.visualizationTypeSelection.add( this.timeCourseStudyButton );
		this.correlationButton.setSelected( true );

		listPanel.setBorder( 
			BorderFactory.createCompoundBorder( 
				BorderFactory.createEmptyBorder( 5, 5, 5, 5 ),
				BorderFactory.createTitledBorder( 
					BorderFactory.createLineBorder( Color.BLACK, 1 ),
					chooseText,
					TitledBorder.LEFT,
					TitledBorder.TOP )
		));
		viewTypePanel.setBorder( 
			BorderFactory.createCompoundBorder( 
				BorderFactory.createEmptyBorder( 5, 5, 5, 5 ),
				BorderFactory.createTitledBorder( 
					BorderFactory.createLineBorder( Color.BLACK, 1 ),
					language.get( "Analysis Type" ),
					TitledBorder.LEFT,
					TitledBorder.TOP )
		));

		this.setVisible( true );
		FontMetrics f = this.getGraphics( ).getFontMetrics( );
		int width;

		width = f.stringWidth( chooseText ) + 10;

		this.setVisible( false );
		this.setModalityType( Dialog.ModalityType.APPLICATION_MODAL );
		this.pack( );
		this.setVisible( true );
	}

	/**
	 * Shows the input dialog and returns the selection.
	 * 
	 * @param owner The parent frame of this Dialog. The parent frame will not
	 *	accept user input until the dialog is closed.
	 * @param title The title to be displayed in the title bar of this dialog.
	 * @param experiments The available experiments to be selected from.
	 * @return A Map.Entry containing the selected view type and List of Samples.
	 */
	public static Map.Entry<Integer,ExperimentSet> showInputDialog( 
		Frame owner, String title, SampleGroup samples ) {

		ExperimentSelectionDialog dialog = 
			new ExperimentSelectionDialog( owner, title, samples );
		return dialog.getReturnValue( );
	}

	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
		Object selection = this.visualizationTypeSelection.getSelection( );
		if ( source == this.cancelButton ) {
			this.returnValue = null;
			this.setVisible( false );
		} else if ( source == this.okButton ) {
			Integer returnCode = null;
			if ( selection == this.correlationButton.getModel( ))
				returnCode = new Integer( CORRELATION_VIEW );
			else if ( selection == this.comparativeAnalysisButton.getModel( ))
				returnCode = new Integer( COMPARATIVE_ANALYSIS_VIEW );
			else if ( selection == this.timeCourseStudyButton.getModel( ))
				returnCode = new Integer( TIME_COURSE_STUDY_VIEW );

			Set<Sample> selectedItems = this.attributePanel.getSamples( );
			System.out.println( selectedItems );
			Set<Sample> filteredItems = 
				this.frequencyFilterPanel.getFilter( ).filter( 
					new TreeSet<Sample>( selectedItems ));
			System.out.println( filteredItems );

			this.returnValue = new ReturnValue( returnCode,
				new ExperimentSet( this.samples.getName( ), filteredItems ));
			this.setVisible( false );
		}

	}

	/**
	 * Gets the return value from this dialog based on the user selection.
	 * 
	 * @return The selected experiments.
	 */
	public Map.Entry <Integer,ExperimentSet> getReturnValue( ) {
		return returnValue;
	}

	/**
	 * A class for holding a key/value pair (View type,Data)
	 */
	private class ReturnValue implements Map.Entry <Integer,ExperimentSet> {
		Integer visualizationType;
		ExperimentSet experiment;

		/**
		 * Creates a new ReturnValue
		 * 
		 * @param 
		 * @return 
		 */
		public ReturnValue( int visualizationType, ExperimentSet experiment ) {
			this.visualizationType = visualizationType;
			this.experiment = experiment;
		}

		/**
		 * Gets the key for this ReturnValue a.k.a. The view type selected.
		 * 
		 * @return The selected visualization type.
		 */
		public Integer getKey( ) {
			return visualizationType;
		}

		/**
		 * Sets the value for this ReturnValue, a.k.a The list of experiments.
		 * 
		 * @param value the new Value for this ReturnValue.
		 * @return The old value for this ReturnValue.
		 */
		public ExperimentSet setValue( ExperimentSet value ) {
			ExperimentSet tmp = this.experiment;
			this.experiment = value;
			return tmp;
		}

		/**
		 * Gets the value for this ReturnValue, a.k.a The list of samples
		 * selected.
		 * 
		 * @return The list of Samples.
		 */
		public ExperimentSet getValue( ) {
			return this.experiment;
		}

		/**
		 * Compares this ReturnValue to the object for equality.
		 * 
		 * @param o The object to compare this ReturnValue to.
		 * @return A boolean indicating if the 2 objects are equal.
		 */
		public boolean equals( Object o ) {
			return this.experiment.equals( o );
		}

		/**
		 * Returns a hash code for identifying this object.
		 * 
		 * @return An integer containing a hash code for this object.
		 */
		public int hashCode( ) {
			return  (( this.getKey( )   == null ? 0 : this.getKey( ).hashCode( )) ^
			         ( this.getValue( ) == null ? 0 : this.getValue( ).hashCode( )));
		}
	}

	private class AttributePanel extends JPanel {
		private Collection<Sample> samples;
		private Map<String,Collection<JCheckBox>> checkboxes;

		public AttributePanel( Collection<Sample> samples ) {
			super( new GridLayout( ));
			GridLayout layout = (GridLayout)this.getLayout( );
			this.checkboxes = new HashMap( );
			this.samples = samples;
			Map<String,Set<String>> attributes = 
				new SampleGroup( null, samples ).getAttributes( );
			int maxValues = 0;
			for ( String attribute : new ArrayList<String>( attributes.keySet( ))) {
				Collection values = attributes.get( attribute );
				// discard this attribute if it is too diverse
				if ((double)values.size( ) / samples.size( ) > 0.8 ) {
					attributes.remove( attribute );
				} else {
					maxValues = Math.max( maxValues, values.size( ));
				}
			}
			layout.setRows( attributes.size( ));
			layout.setColumns( maxValues + 1 );
			for ( Map.Entry<String,Set<String>> entry : attributes.entrySet( )) {
				this.add( new JLabel( entry.getKey( ) + ":" ));
				this.checkboxes.put( entry.getKey( ), new ArrayList( ));
				int index = 0;
				// create a checkbox for each value
				for ( String value : entry.getValue( )) {
					JCheckBox cb = new JCheckBox( value, true );
					this.checkboxes.get( entry.getKey( )).add( cb );
					this.add( cb );
					index++;
				}
				// add some empty panels
				while( index < maxValues ) {
					this.add( new JPanel( ));
					index++;
				}
			}	
		}

		public Set<Sample> getSamples( ) {
			Logger logger = Logger.getLogger( getClass( ));
			logger.debug( "Initial Samples -> " + new TreeSet( this.samples ));
			AttributesFilterList<Sample> filter = new AttributesFilterList( );
			for( Map.Entry<String,Collection<JCheckBox>> entry : 
				   this.checkboxes.entrySet( )) {
				AttributesFilterList<Sample> orFilter = 
					new AttributesFilterList( AttributesFilterList.OR );
				for( JCheckBox cb : entry.getValue( )) {
					if ( cb.isSelected( )) {
						Criterion criterion = new Criterion<Sample>( entry.getKey( ),
						              cb.getText( ), Criterion.EQUAL );
						logger.debug( criterion.toString( ) + " -> " + criterion.filter( samples ));
						orFilter.add( criterion );
					}
				}
				filter.add( orFilter );
				logger.debug( orFilter.toString( ) + " -> " + orFilter.filter( samples ));
			}
			Set returnValue = new TreeSet( filter.filter( this.samples ));
			logger.debug( filter.toString( ) + " -> " + returnValue );
			return returnValue;
		}
	}
}


