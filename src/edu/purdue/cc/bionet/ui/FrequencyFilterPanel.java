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

import edu.purdue.cc.bionet.util.FrequencyFilter;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.StringUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A UI class which shows a set of Spinners for adjusting the visible 
 * correlation range.
 */
public class FrequencyFilterPanel extends JPanel {

	private JLabel groupFrequencyLabel;
	private JLabel overallFrequencyLabel;
	private JLabel selectFeaturesLabel;
	private JSpinner groupFrequencySpinner;
	private JSpinner overallFrequencySpinner;
	private Collection<JCheckBox> features;

	/**
	 * Creates a new FrequencyFilterPanel with the passed in attributes.
	 * 
	 * @param features The selectable features for grouping.
	 */
	public FrequencyFilterPanel( Collection<String> features ) {
		this( 
			Settings.getSettings( ).getDouble( 
				"history.experimentSelection.frequencyFilter.overallFrequency", 30.0 ),
			Settings.getSettings( ).getDouble( 
				"history.experimentSelection.frequencyFilter.groupFrequency", 75.0 ),
			features );
	}

	/**
	 * Creates a new FrequencyFilterPanel with the passed in values.
	 *
	 * @param overallFrequency The initial overall frequency setting.
	 * @param groupFrequency The initial group frequency setting.
	 * @param features The selectable features for grouping.
	 */
	public FrequencyFilterPanel( double overallFrequency, double groupFrequency, 
	                             Collection<String> features ) {
		super( new BorderLayout( ));
		Language language = Settings.getLanguage( );
		Settings settings = Settings.getSettings( );
		this.groupFrequencyLabel = 
			new JLabel( language.get( "Frequency" ) + "(%): ", SwingConstants.RIGHT );
		this.overallFrequencyLabel = 
			new JLabel( language.get( "Frequency" ) + "(%): ", SwingConstants.RIGHT );
		this.selectFeaturesLabel = 
			new JLabel( language.get( "Select features" ) + ": ", SwingConstants.RIGHT );

		this.groupFrequencySpinner =
			new JSpinner( new SpinnerNumberModel( groupFrequency, 0, 100, 5 ));
		this.overallFrequencySpinner = 
			new JSpinner( new SpinnerNumberModel( overallFrequency, 0, 100, 5 ));

		this.groupFrequencySpinner.setPreferredSize( new Dimension( 80, 25 ));
		this.overallFrequencySpinner.setPreferredSize( new Dimension( 80, 25 ));

		JPanel featuresPanel = new JPanel( new BorderLayout( ));
	/**
	 * Creates a new FrequencyFilterPanel with the last selected values.
	 */
		JPanel featureBoxPanel = new JPanel( new GridLayout( 
			features.size( ) - 1, 1 ));
		JPanel groupPercentPanel = new JPanel( new BorderLayout( ));

		groupPercentPanel.add( this.groupFrequencyLabel, BorderLayout.WEST );
		groupPercentPanel.add( this.groupFrequencySpinner, BorderLayout.CENTER );
		featuresPanel.add( this.selectFeaturesLabel, BorderLayout.WEST );
		featuresPanel.add( featureBoxPanel, BorderLayout.CENTER );
		this.features = new ArrayList<JCheckBox>( );
		for ( String feature : features ) {
			if ( !feature.equals( "sample file" )) {
				JCheckBox box = new JCheckBox( feature, settings.getBoolean( 
					"history.experimentSelection.frequencyFilter.attributes." +
						StringUtils.camelCase( feature, ' ' ), true ));
				this.features.add( box );
				featureBoxPanel.add( box );
			}
		}
		JPanel groupFrequencyPanel = new JPanel( new GridLayout( 1, 2 ));
		JPanel groupFrequencySpinnerPanel = new JPanel( new BorderLayout( ));
		groupFrequencySpinnerPanel.add( groupPercentPanel, BorderLayout.NORTH );
		groupFrequencySpinnerPanel.add( new JPanel( ), BorderLayout.CENTER );
		groupFrequencyPanel.add( featuresPanel );
		groupFrequencyPanel.add( groupFrequencySpinnerPanel );

		JPanel overallFrequencyPanel = new JPanel( new BorderLayout( ));
		JPanel overallFrequencySpinnerPanel = new JPanel( new BorderLayout( ));
		overallFrequencySpinnerPanel.add( this.overallFrequencySpinner, 
																				BorderLayout.EAST );
		overallFrequencySpinnerPanel.add( this.overallFrequencyLabel, 
																				BorderLayout.CENTER );
		overallFrequencyPanel.add( overallFrequencySpinnerPanel, BorderLayout.NORTH );
		overallFrequencyPanel.add( new JPanel( ), BorderLayout.CENTER );

		this.add( overallFrequencyPanel, BorderLayout.WEST );
		this.add( groupFrequencyPanel, BorderLayout.CENTER );
		this.groupFrequencySpinner.setBackground( Color.WHITE );
		this.overallFrequencySpinner.setBackground( Color.WHITE );

		this.setBorder( 
			BorderFactory.createTitledBorder( 
				BorderFactory.createLineBorder( Color.BLACK, 1 ),
				Settings.getLanguage( ).get( "Frequency Filter" ),
				TitledBorder.LEFT,
				TitledBorder.TOP
		));
		groupFrequencyPanel.setBorder( 
			BorderFactory.createCompoundBorder( 
				BorderFactory.createEmptyBorder( 5, 5, 5, 5 ),
				BorderFactory.createTitledBorder( 
					BorderFactory.createLineBorder( Color.BLACK, 1 ),
					Settings.getLanguage( ).get( "Group-based filter" ),
					TitledBorder.LEFT,
					TitledBorder.TOP )
		));
		overallFrequencyPanel.setBorder( 
			BorderFactory.createCompoundBorder( 
				BorderFactory.createEmptyBorder( 5, 5, 5, 5 ),
				BorderFactory.createTitledBorder( 
					BorderFactory.createLineBorder( Color.BLACK, 1 ),
					Settings.getLanguage( ).get( "Cross-board filter" ),
					TitledBorder.LEFT,
					TitledBorder.TOP )
		));
	}

	public FrequencyFilter getFilter( ) {
		Collection<String> selectedFeatures = new ArrayList<String>( );
		Settings settings = Settings.getSettings( );
		for( JCheckBox box : this.features ) {
			settings.setBoolean( 
				"history.experimentSelection.frequencyFilter.attributes." +
				StringUtils.camelCase( box.getText( ), ' ' ), box.isSelected( ));
			if ( box.isSelected( )) {
				selectedFeatures.add( box.getText( ));
			}
		}
		double overall = 
			Double.parseDouble( this.overallFrequencySpinner.getValue( ).toString( ));
		double group = 
			Double.parseDouble( this.groupFrequencySpinner.getValue( ).toString( ));
		settings.setDouble( 
			"history.experimentSelection.frequencyFilter.overallFrequency", overall );
		settings.setDouble( 
			"history.experimentSelection.frequencyFilter.groupFrequency", group );
		return new FrequencyFilter( overall, group, selectedFeatures );
	}
}

