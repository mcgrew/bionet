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

import edu.purdue.jsysnet.util.Experiment;
import edu.purdue.jsysnet.util.Settings;
import edu.purdue.jsysnet.util.Language;
import edu.purdue.jsysnet.JSysNet;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.awt.Frame;
import java.awt.FontMetrics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dialog;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class ExperimentSelectionDialog extends JDialog implements ActionListener,ChangeListener {
	public static final int CORRELATION_VIEW = 0;
	public static final int COMPARATIVE_ANALYSIS_VIEW = 1;
	public static final int TIME_COURSE_STUDY_VIEW = 2;
	protected JButton okButton;
	protected JButton cancelButton;
	protected JRadioButton correlationButton;
	protected JRadioButton comparativeAnalysisButton;
	protected JRadioButton timeCourseStudyButton;
	protected ButtonGroup visualizationTypeSelection;
	protected JLabel instructionLabel;
	protected JList experimentList;
	protected Map.Entry <Integer,List> returnValue;
	protected final String chooseText;
	protected final String correlationButtonText;
	protected final String timeCourseStudyButtonText;
	protected final String comparativeAnalysisButtonText;
	protected final String okButtonText;
	protected final String cancelButtonText;

	public ExperimentSelectionDialog( Frame owner, String title, List experiments ) {
		super( owner, title );
		this.getContentPane( ).setLayout( null );
		this.setBounds( 
			Settings.getSettings( ).getInt( "windowXPosition" ),
			Settings.getSettings( ).getInt( "windowYPosition" ),
			500, 200 );
		
		Language language = Settings.getLanguage( );
		this.chooseText = language.get( "Choose an Experiment" );
		this.correlationButtonText = language.get( "Correlation" );
		this.timeCourseStudyButtonText = language.get( "Time Course Study" );
		this.comparativeAnalysisButtonText = language.get( "Comparative Analysis" );
		this.okButtonText = language.get( "Select" );
		this.cancelButtonText = language.get( "Cancel" );

		this.okButton = new JButton( okButtonText );
		this.cancelButton = new JButton( cancelButtonText );
		this.correlationButton = new JRadioButton( correlationButtonText );
		this.comparativeAnalysisButton = new JRadioButton( comparativeAnalysisButtonText );
		this.timeCourseStudyButton = new JRadioButton( timeCourseStudyButtonText );
		this.visualizationTypeSelection = new ButtonGroup( );
		this.instructionLabel = new JLabel( chooseText );
		this.experimentList = new JList( experiments.toArray( new Object[ experiments.size( )]));

		this.okButton.addActionListener( this );
		this.cancelButton.addActionListener( this );

		this.add( this.okButton );
		this.add( this.cancelButton );
		this.add( this.instructionLabel );
		this.add( this.experimentList );
		this.add( this.correlationButton );
		this.add( this.comparativeAnalysisButton );
		this.add( this.timeCourseStudyButton );

		this.correlationButton.addChangeListener( this );
		this.comparativeAnalysisButton.addChangeListener( this );
		this.timeCourseStudyButton.addChangeListener( this );
		this.visualizationTypeSelection.add( this.correlationButton );
		this.visualizationTypeSelection.add( this.comparativeAnalysisButton );
		this.visualizationTypeSelection.add( this.timeCourseStudyButton );
		this.correlationButton.setSelected( true );
		this.timeCourseStudyButton.setEnabled( false );

		this.setVisible( true );
		FontMetrics f = this.getGraphics( ).getFontMetrics( );
		int width;

		this.okButton.setBounds( 30, 130, 100, 20 );
		this.cancelButton.setBounds( 170, 130, 100, 20 );
		this.experimentList.setBounds( 30, 40, 240, 80 );
		this.experimentList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		this.correlationButton.setBounds( 290, 40, 200, 20 );
		this.comparativeAnalysisButton.setBounds( 290, 70, 200, 20 );
		this.timeCourseStudyButton.setBounds( 290, 100, 200, 20 );

		width = f.stringWidth( chooseText ) + 10;
		instructionLabel.setBounds( 150 - width/2, 10, width, 40 );

		this.setVisible( false );
		this.setModalityType( Dialog.ModalityType.APPLICATION_MODAL );
		this.setVisible( true );
	}

	public static Map.Entry<Integer,List> showInputDialog( Frame owner, String title, List experiments ) {
		ExperimentSelectionDialog dialog = new ExperimentSelectionDialog( owner, title, experiments );
		return dialog.getReturnValue( );
	}
	
	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
		Object selection = this.visualizationTypeSelection.getSelection( );
		if ( source == this.cancelButton ) {
			returnValue = null;
			this.setVisible( false );
		} else if ( source == this.okButton ) {
			Integer returnCode = null;
			if ( selection == this.correlationButton.getModel( ))
				returnCode = new Integer( CORRELATION_VIEW );
			else if ( selection == this.comparativeAnalysisButton.getModel( ))
				returnCode = new Integer( COMPARATIVE_ANALYSIS_VIEW );
			else if ( selection == this.timeCourseStudyButton.getModel( ))
				returnCode = new Integer( TIME_COURSE_STUDY_VIEW );

			Object [] selectedItems = experimentList.getSelectedValues( );
			if ( selectedItems.length < 1 ) {
				JSysNet.message( Settings.getLanguage( ).get( "You must select at least one item" ));
				return;
			}

			returnValue = new ReturnValue( returnCode,
				Arrays.asList( experimentList.getSelectedValues( )));
			this.setVisible( false );
		}

	}

	public void stateChanged( ChangeEvent e ) {
		Object source = e.getSource( );
		if ( source == this.correlationButton )
			this.experimentList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		else
			this.experimentList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
	}

	public Map.Entry getReturnValue( ) {
		return returnValue;
	}

	/**
	 * A class for holding a key/value pair (View type,Data)
	 */
	private class ReturnValue implements Map.Entry <Integer,List> {
		Integer visualizationType;
		List experiments;

		public ReturnValue( int visualizationType, List experiments ) {
			this.visualizationType = visualizationType;
			this.experiments = experiments;
		}

		public Integer getKey( ) {
			return visualizationType;
		}

		public List setValue( List value ) {
			List tmp = this.experiments;
			this.experiments = value;
			return tmp;
		}

		public List getValue( ) {
			return this.experiments;
		}

		public boolean equals( Object o ) {
			return this.experiments.equals( o );
		}

		public int hashCode( ) {
			return  (( this.getKey( ) == null   ? 0 : this.getKey( ).hashCode( )) ^
			         ( this.getValue( ) == null ? 0 : this.getValue( ).hashCode( )));
		}
	}
}


