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
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
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
import java.util.Stack;
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

/**
 * A class which displays a dialog for choosing 2 sample groups.
 */
public class SampleGroupingDialog extends JDialog 
                                  implements ActionListener,ChangeListener {

	protected Collection<Sample> samples;
	protected Map<String,Class> attributeTypes;
	protected JButton okButton;
	protected JButton cancelButton;
	protected Pair<GroupPanel> groupPanels;
	protected Pair<SampleGroup> returnValue;

	/**
	 * Creates a new SampleGroupingDialog. This should probably not be called
	 * directly, instead use showInputDialog( ).
	 * 
	 * @param owner The parent of this dialog.
	 * @param title The title for the dialog.
	 * @param samples A group of samples to be sorted into groups.
	 */
	public SampleGroupingDialog( Frame owner, String title,
	                             Collection<Sample> samples ) {
		super( owner, title );
		this.getContentPane( ).setLayout( new BorderLayout( ));
		this.setBounds( Settings.getSettings( ).getInt( "windowXPosition" ),
		                  Settings.getSettings( ).getInt( "windowYPosition" ),
											600, 300 );
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
		this.groupPanels.getFirstItem( ).addChangeListener( this );
		this.groupPanels.getSecondItem( ).addChangeListener( this );
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
		this.setResizable( false );
		this.setVisible( true );
		this.stateChanged( new ChangeEvent( this ));
	}

	/**
	 * Creates a new SampleGroupingDialog and returns the SampleGroups once the
	 * selection is made.
	 * 
	 * @param owner The parent of this dialog.
	 * @param title The title for the dialog.
	 * @param samples A group of samples to be sorted into groups.
	 */
	public static Pair<SampleGroup> showInputDialog(
			Frame owner, String title, Collection<Sample> samples ) {

		SampleGroupingDialog dialog = 
			new SampleGroupingDialog( owner, title, samples );
		return dialog.getReturnValue( );
	}

	/**
	 * The actionPerformed method of The ActionListener interface.
	 * @see ActionListener#actionPerformed( ActionEvent )
	 * 
	 * @param e The event which triggered this action.
	 */
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

	/**
	 * Returns the groups which were selected by the user. This value will be
	 * null until the user clicks "OK".
	 * 
	 * @return The selected groups.
	 */
	public Pair<SampleGroup> getReturnValue( ) {
		return this.returnValue;
	}

	/**
	 * The stateChanged method of the ChangeListener interface.
	 * @see ChangeListener#stateChanged( ChangeEvent )
	 * 
	 * @param e The event which triggered this action.
	 */
	public void stateChanged( ChangeEvent e ) {
		// set the ok button to be enabled only if both groups are not 0.
		this.okButton.setEnabled( 
			this.groupPanels.getFirstItem( ).getGroupSize( ) != 0  &&
			this.groupPanels.getSecondItem( ).getGroupSize( ) != 0 );
	}
	
	// ========================= PRIVATE CLASSES ================================
	// ============================ GroupPanel ==================================
	/**
	 * A class for choosing a set of group criteria.
	 */
	private class GroupPanel extends JPanel 
	                         implements ActionListener,ChangeListener {
		private Stack<CriterionPanel> criteria;
		private Collection<Sample> samples;
		private Collection<ChangeListener> changeListeners;
		private JPanel addCriterionPanel;
		private JPanel removeCriterionPanel;
		private JButton addCriterionButton;
		private JButton removeCriterionButton;
		private String title;
		private int groupSize;
		private JLabel groupSizeLabel;

		/**
		 * Creates a new GroupPanel.
		 * 
		 * @param title The title of this Group panel.
		 * @param samples The samples which are to be chosen from.
		 */
		public GroupPanel( String title, Collection<Sample> samples ) {
			super( new GridLayout( 10, 1 ));	
			this.setBorder( BorderFactory.createCompoundBorder( 
				BorderFactory.createEmptyBorder( 3, 3, 3, 3 ),
				BorderFactory.createLineBorder( Color.BLACK, 1 )));
			this.samples = samples;
			this.title = title;
			this.changeListeners = new ArrayList<ChangeListener>( );
			Language language = Settings.getLanguage( );

			this.addCriterionPanel = new JPanel( new BorderLayout( ));
			JLabel label = new JLabel( title );
			this.addCriterionButton = new JButton(
				language.get( "Add Criterion" ));
			this.addCriterionButton.addActionListener( this );
			this.addCriterionPanel.add( label, BorderLayout.CENTER );
			this.addCriterionPanel.add( this.addCriterionButton, BorderLayout.EAST );
			this.add( this.addCriterionPanel );

			this.removeCriterionPanel = new JPanel( new BorderLayout( ));
			this.groupSizeLabel = new JLabel( );
			this.removeCriterionButton = new JButton( 
				language.get( "Remove Criterion" ));
			this.removeCriterionButton.addActionListener( this );
			this.removeCriterionPanel.add( this.groupSizeLabel, BorderLayout.CENTER );
			this.removeCriterionPanel.add( this.removeCriterionButton, BorderLayout.EAST );
			this.add( this.removeCriterionPanel );

			this.criteria = new Stack<CriterionPanel>( );
			CriterionPanel panel = new CriterionPanel( samples );
			this.criteria.push( panel );
			this.add( panel );
			panel.addChangeListener( this );
			this.stateChanged( new ChangeEvent( panel ));
		}

		/**
		 * The actionPerformed method of the ActionListener interface.
		 * 
		 * @param e The event which triggered this action.
		 */
		public void actionPerformed( ActionEvent e ) {
			Object source = e.getSource( );
			if ( source == this.addCriterionButton && this.criteria.size( ) < 8 ) {
				CriterionPanel panel = new CriterionPanel( this.samples );	
				this.criteria.push( panel );
				this.add( panel, this.criteria.size( ) + 1 );
				panel.addChangeListener( this );
				Window window = SwingUtilities.windowForComponent( this );
				if ( window != null )
					window.pack( );
				this.stateChanged( new ChangeEvent( panel ));
			} else if ( source == this.removeCriterionButton && 
			            this.criteria.size( ) > 1 ) {
				this.remove( this.criteria.pop( ));
				this.repaint( );
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
			for ( CriterionPanel cp : this.criteria ) {
				filtered = cp.getCriterion( ).filter( filtered );
			}
			return new SampleGroup( this.title, filtered );
		}

		/**
		 * The stateChanged method of the ChangeListener interface.
		 * @see ChangeListener#stateChanged( ChangeEvent )
		 * 
		 * @param e The event which triggered this action
		 */
		public void stateChanged( ChangeEvent e ) {
			this.groupSize = this.filter( this.samples ).size( );
			Logger.getLogger( getClass( )).debug( "New group size: " + this.groupSize );
			this.groupSizeLabel.setText( 
					Settings.getLanguage( ).get( "Group size" ) + ": " + this.groupSize );
			this.fireChangeEvent( );

		}

		/**
		 * Returns the size for the group which would be returned given the
		 * currently selected criteria.
		 * 
		 * @return The size of the currently selected group.
		 */
		public int getGroupSize( ) {
			return this.groupSize;
		}

		/**
		 * Adds a ChangeListener to this Panel which will be notified when changes
		 * are made to the selected criteria.
		 * 
		 * @param c The ChangeListener to be added.
		 */
		public void addChangeListener( ChangeListener c ) {
			this.changeListeners.add( c );
		}

		/**
		 * Returns the ChangeListeners which have been added to this Panel.
		 * 
		 * @return The ChangeListeners which have been added to this Panel.
		 */
		public ChangeListener[] getChangeListeners( ) {
			return this.changeListeners.toArray( 
				new ChangeListener[ this.changeListeners.size( )]);
		}

		/**
		 * Removes a ChangeListener from this Panel.
		 * 
		 * @param c The changeListener to remove.
		 * @return A boolean indicating whether the listener was removed.
		 */
		public boolean removeChangeListener( ChangeListener c ) {
			return this.changeListeners.remove( c );
		}

		/**
		 * Triggers a ChangeEvent, notifying all of this Panel's listeners that
		 * something has changed.
		 */
		private void fireChangeEvent( ) {
			ChangeEvent e = new ChangeEvent( this );
			for ( ChangeListener c : this.changeListeners ) {
				c.stateChanged( e );
			}
		}
		

		// ============================ CriterionPanel =============================
		/**
		 * A class for selecting a single Criterion to filter Samples with.
		 */
		private class CriterionPanel extends JPanel 
		                            implements ChangeListener, ActionListener {
			Collection<ChangeListener> changeListeners;
			Collection<Sample> attributes;
			JComboBox attributeSelector;
			JComboBox valueSelector;
			JComponent numberValueSelector;
			JLabel attributeLabel = new JLabel( 
				Settings.getLanguage( ).get( "Attribute" ) + ":" );
			JPanel valuePanel;
			Pattern numberPattern = Pattern.compile("(\\d+\\.?\\d*|\\.\\d+)(e\\d+)?");
			private boolean numeric;
			
			/**
			 * Creates a new CriterionPanel.
			 * 
			 * @param samples The samples to be filtered with this Panel.
			 */
			public CriterionPanel( Collection<Sample> samples ) {
				super( new GridLayout( 1, 3 ));
				Language language = Settings.getLanguage( );
				this.attributes = samples;
				Vector<String> attributeKeyVector = 
					new Vector( samples.iterator( ).next( ).getAttributes( ).keySet( ));
				Collections.sort( attributeKeyVector );
				this.attributeSelector = new JComboBox( attributeKeyVector );
				this.attributeSelector.addActionListener( this );
				this.add( this.attributeSelector );
				this.attributeSelected( attributeKeyVector.get( 0 ));
				this.changeListeners = new ArrayList<ChangeListener>( );
			}

			/**
			 * Gets a Criterion object created with the selected attribute.
			 * 
			 * @return A Criterion created by the selected attribute.
			 */
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
			
			/**
			 * Determines if the given attribute has all numeric values.
			 * 
			 * @param attribute The attribute to check.
			 * @return A boolean indicating if the attribute is numeric.
			 */
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
			public void stateChanged( ChangeEvent e ) {
				this.fireChangeEvent( );
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
					logger.debug( String.format( "Number range: %f - %f", 
					                             values[ 0 ], values[ 2 ] ));
					this.numberValueSelector = new JSpinner( new SpinnerNumberModel( 
						values[ 1 ], values[ 0 ], values[ 2 ], 1.0 ));
					((JSpinner)this.numberValueSelector).addChangeListener( this );
				} else {
					logger.debug( "Attribute Selected: " + attributeKey );
					Collection<String> values = new TreeSet<String>( );
					for ( Attributes<String> att : this.attributes ) {
						values.add( att.getAttribute( attributeKey ));
					}
					this.valueSelector = new JComboBox( new Vector<String>( values ));
					this.numberValueSelector = new JPanel( );
				}
				this.valueSelector.addActionListener( this );
				this.add( this.valueSelector );
				this.add( this.numberValueSelector );
				Window window = SwingUtilities.windowForComponent( this );
				if ( window != null )
					window.pack( );
			}

			/**
			 * Adds a ChangeListener to this Panel. The listeners will be notified
			 * when the user changes a selection.
			 * 
			 * @param changeListener A listener to be notified when the user changes
			 *  a selection.
			 */
			public void addChangeListener( ChangeListener changeListener ) {
				this.changeListeners.add( changeListener );
			}

			/**
			 * Gets an array containing the ChangeListeners which have been added
			 * to this Panel.
			 * 
			 * @return An array of ChangeListeners.
			 */
			public ChangeListener[] getChangeListeners( ) {
				return this.changeListeners.toArray( 
					new ChangeListener[ this.changeListeners.size( ) ]);
			}

			/**
			 * Removes the given ChangeListener from this Panel.
			 * 
			 * @param changeListener The listener to be removed.
			 * @return A boolean indicating whether the listener was removed.
			 */
			public boolean removeChangeListener( ChangeListener changeListener ) {
				return this.changeListeners.remove( changeListener );
			}

			/**
			 * Fires a change event, which notifies all ChangeListeners registered
			 * with this Panel.
			 */
			private void fireChangeEvent( ) {
				Logger.getLogger( getClass( )).debug( "Firing ChangeEvent..." );
				ChangeEvent e = new ChangeEvent( this );
				for ( ChangeListener c : this.changeListeners ) {
					c.stateChanged( e );
				}
			}

			/**
			 * The actionPerformed method of the ActionListener interface.
			 * 
			 * @param e The event which triggered this action.
			 */
			public void actionPerformed( ActionEvent e ) {
				JComboBox box = (JComboBox)e.getSource( );
				if ( box == this.attributeSelector ) {
					this.attributeSelected( box.getSelectedItem( ).toString( ));
				} 
				this.fireChangeEvent( );
			}
		}
	}
}


