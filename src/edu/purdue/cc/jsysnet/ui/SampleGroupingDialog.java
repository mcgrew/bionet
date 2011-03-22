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

package edu.purdue.cc.jsysnet.ui;

import edu.purdue.bbc.util.Attributes;
import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Pair;
import edu.purdue.bbc.util.Range;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.SimplePair;
import edu.purdue.bbc.util.attributes.AttributesFilter;
import edu.purdue.bbc.util.attributes.Criterion;
import edu.purdue.bbc.util.attributes.NumericalCriterion;
import edu.purdue.cc.jsysnet.util.Sample;
import edu.purdue.cc.jsysnet.util.SampleGroup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class SampleGroupingDialog extends JDialog 
                                  implements ActionListener {

	Collection<Sample> samples;
	Pair<SampleGroup> groups;
	Map<String,Class> attributeTypes;
	protected JButton okButton;
	protected JButton cancelButton;
	protected Pair<GroupPanel> groupPanels;
	protected Pair<SampleGroup> returnValue;

	public SampleGroupingDialog( Frame owner, String title,
	                             Collection<Sample> samples ) {
		super( owner, title );
		this.getContentPane( ).setLayout( new BorderLayout( ));
		this.setBounds( Settings.getSettings( ).getInt( "windowXPosition" ),
		                  Settings.getSettings( ).getInt( "windowYPosition" ),
											600, 300
		                );
		this.pack( );
		Language language = Settings.getLanguage( );
		this.samples = samples;
		this.okButton = new JButton( language.get( "OK" ));
		this.okButton.addActionListener( this );
		this.cancelButton = new JButton( language.get( "Cancel" ));
		this.cancelButton.addActionListener( this );
		JPanel confirmPanel =  new JPanel( new GridLayout( 1, 2 ));
		confirmPanel.add( this.okButton );
		confirmPanel.add( this.cancelButton );
		this.add( confirmPanel, BorderLayout.SOUTH );

		this.groupPanels = new SimplePair<GroupPanel>( 
			new GroupPanel( "Group 1",  samples ), 
			new GroupPanel( "Group 2", samples ));
		JPanel mainGroupsPanel = new JPanel( new GridLayout( 2, 1 ));
		mainGroupsPanel.add( this.groupPanels.getFirstItem( ));
		mainGroupsPanel.add( this.groupPanels.getSecondItem( ));
		this.add( mainGroupsPanel, BorderLayout.CENTER );

		this.addWindowListener( new WindowAdapter( ) {
			public void windowOpened( WindowEvent e ) {
				e.getWindow( ).pack( );
			}
		});
		this.setVisible( false );
		this.setModalityType( Dialog.ModalityType.APPLICATION_MODAL );
		this.setVisible( true );
	}

	public static Pair<SampleGroup> showInputDialog(
			Frame owner, String title, Collection<Sample> samples ) {

		SampleGroupingDialog dialog = 
			new SampleGroupingDialog( owner, title, samples );
		return dialog.getReturnValue( );
	}

	public Pair<SampleGroup> getGroups( ) {
		return this.groups;	
	}

	public void actionPerformed( ActionEvent e ) {
		Object source = e.getSource( );
		if ( source == this.okButton ) {
			Logger.getLogger( getClass( )).debug( "Setting groups..." );
			this.returnValue = new SimplePair<SampleGroup>(
					this.groupPanels.getFirstItem( ).filter( this.samples ),
					this.groupPanels.getSecondItem( ).filter( this.samples ));
		} else {
			Logger.getLogger( getClass( )).debug( "Group setting canceled..." );
		}
		this.setVisible( false );
	}

	public Pair<SampleGroup> getReturnValue( ) {
		return this.returnValue;
	}
	
	/**
	 * A class for choosing a set of group criteria.
	 */
	private class GroupPanel extends JPanel implements ActionListener {
		private Collection<CriteriaPanel> criteria;
		private Collection<Sample> samples;
		private JPanel addCriterionPanel;
		private JButton addCriterionButton;
		private String title;

		public GroupPanel( String title, Collection<Sample> samples ) {
			super( new GridLayout( 9, 1 ));	
			this.setBorder( BorderFactory.createCompoundBorder( 
				BorderFactory.createEmptyBorder( 3, 3, 3, 3 ),
				BorderFactory.createLineBorder( Color.BLACK, 1 )));
			this.samples = samples;
			this.title = title;

			this.addCriterionPanel = new JPanel( new BorderLayout( ));
			this.addCriterionButton = new JButton( "Add Criterion" );
			this.addCriterionPanel.add( this.addCriterionButton, BorderLayout.EAST );
			this.addCriterionButton.addActionListener( this );
			JLabel label = new JLabel( title );
			this.addCriterionPanel.add( label, BorderLayout.CENTER );
			this.add( this.addCriterionPanel );

			this.criteria = new ArrayList<CriteriaPanel>( );
			CriteriaPanel panel = new CriteriaPanel( samples );
			this.criteria.add( panel );
			this.add( panel );
		}

		/**
		 * The actionPerformed method of the ActionListener interface.
		 * 
		 * @param e The event which triggered this action.
		 */
		public void actionPerformed( ActionEvent e ) {
			Object source = e.getSource( );
			if ( source == this.addCriterionButton && this.criteria.size( ) < 8 ) {
				CriteriaPanel panel = new CriteriaPanel( this.samples );	
				this.criteria.add( panel );
				this.add( panel, this.criteria.size( ));
				Window window = SwingUtilities.windowForComponent( this );
				if ( window != null )
					window.pack( );
			}
		}

		/**
		 * Filters the passed in group of samples based on the set criteria and
		 * returns the result.
		 * 
		 * @param all A set of samples to be filtered.
		 * @return A filtered set of samples.
		 */
		public SampleGroup filter( Collection<Sample> all ) {
			Collection<Sample> filtered = new ArrayList<Sample>( all );
			for ( CriteriaPanel cp : this.criteria ) {
				filtered = cp.getCriterion( ).filter( filtered );
			}
			return new SampleGroup( this.title, filtered );
		}
		

		private class CriteriaPanel extends JPanel 
		                            implements ItemListener, ActionListener {
			JLabel attributeLabel = new JLabel( 
				Settings.getLanguage( ).get( "Attribute" ) + ":" );
			JComboBox attributeSelector;
			JComboBox valueSelector;
			JComponent numberValueSelector;
			Pattern numberPattern = Pattern.compile("(\\d+\\.?\\d*|\\.\\d+)(e\\d+)?");
			Collection<Sample> attributes;
			JPanel valuePanel;
			private boolean numeric;
			
			public CriteriaPanel( Collection<Sample> attributes ) {
				super( new GridLayout( 1, 3 ));
				Language language = Settings.getLanguage( );
				this.attributes = attributes;
				Vector<String> attributeKeyVector = 
					new Vector( attributes.iterator( ).next( ).getAttributes( ).keySet( ));
				Collections.sort( attributeKeyVector );
				this.attributeSelector = new JComboBox( attributeKeyVector );
				this.attributeSelector.addActionListener( this );
				this.add( this.attributeSelector );
				this.attributeSelected( attributeKeyVector.get( 0 ));
			}

			public Criterion<Sample> getCriterion( ) {
				if ( this.numeric )	 {
					int valueSelectorIndex = this.valueSelector.getSelectedIndex( );
					int condition;
					switch( valueSelectorIndex ) {
						case 0:
							condition = Criterion.GREATER; break;
						case 1:
							condition = Criterion.LESS; break;
						case 2:
							condition = Criterion.EQUAL; break;
						case 3:
							condition = Criterion.NOT_EQUAL; break;
						default:
							condition = Criterion.EQUAL | Criterion.NOT_EQUAL; break;
					}
					return new NumericalCriterion<Sample>( 
						this.attributeSelector.getSelectedItem( ).toString( ),
						new Double(((JSpinner)this.numberValueSelector).getValue( ).toString( )),
						condition );
				} else {
					return new Criterion<Sample>(
						this.attributeSelector.getSelectedItem( ).toString( ),
						this.valueSelector.getSelectedItem( ).toString( ),
						Criterion.EQUAL );
				}
			}
			
			private boolean isNumeric( String attribute ) {
				boolean returnValue = true;
				Iterator<Sample> attributesIterator = 
					this.attributes.iterator( );
				while ( returnValue && attributesIterator.hasNext( )) {
					returnValue = returnValue && numberPattern.matcher( 
						attributesIterator.next( ).getAttribute( attribute )).matches( );
				}
				return returnValue;
			}

			/**
			 * Finds the number range for a numerical attribute.
			 * 
			 * @param attribute The attribute to find the range for.
			 * @return The range of the values for the given attribute
			 */
			public Range getRange( String attribute ) {
				double min = Double.POSITIVE_INFINITY;
				double max = Double.NEGATIVE_INFINITY;
				for ( Attributes<String> att : this.attributes ) {
					double value = Double.parseDouble( att.getAttribute( attribute ));
					min = Math.min( min, value );
					max = Math.max( max, value );
				}
				return new Range( min, max );
			}

			/**
			 * The itemStateChanged method of the ItemListener interface.
			 * 
			 * @param e The event which triggered this action.
			 */
			public void itemStateChanged( ItemEvent e ) {
			}

			/**
			 * Called when an attribute selection is made.
			 * 
			 * @param attributeKey The attribute which was selected.
			 */
			private void attributeSelected( String attributeKey ) {
				Logger logger = Logger.getLogger( getClass( ));
				Language language = Settings.getLanguage( );

				// remove the number & value selectors
				if ( this.valueSelector != null )
					this.remove( this.valueSelector );
				if ( this.numberValueSelector != null )
					this.remove( this.numberValueSelector );

				this.numeric = this.isNumeric( attributeKey );
				if ( this.numeric ) {
					logger.debug( "Numeric Attribute Selected: " + attributeKey );
					this.valueSelector = new JComboBox( new String[] { 
						language.get( "Greater Than" ), 
						language.get( "Less Than" ), 
						language.get( "Equal To" ), 
						language.get( "Not Equal To" )
					});

					double[] values = this.getRange( attributeKey ).getSequence( 3 );
					logger.debug( String.format( "Number range: %f - %f", values[ 0 ], values[ 2 ] ));
					this.numberValueSelector = new JSpinner( new SpinnerNumberModel( 
						values[ 1 ], values[ 0 ], values[ 2 ], 1.0 ));
				} else {
					logger.debug( "Attribute Selected: " + attributeKey );
					Collection<String> values = new TreeSet<String>( );
					for ( Attributes<String> att : this.attributes ) {
						values.add( att.getAttribute( attributeKey ));
					}
					this.valueSelector = new JComboBox( new Vector<String>( values ));
					this.numberValueSelector = new JPanel( );
				}
				this.add( this.valueSelector );
				this.add( this.numberValueSelector );
				Window window = SwingUtilities.windowForComponent( this );
				if ( window != null )
					window.pack( );
			}

			/**
			 * The actionPerformed method of the ActionListener interface.
			 * 
			 * @param e The event which triggered this action.
			 */
			public void actionPerformed( ActionEvent e ) {
				JComboBox box = (JComboBox)e.getSource( );
				this.attributeSelected( box.getSelectedItem( ).toString( ));
			}
		}
	}
}


