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

package edu.purdue.cc.bionet.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * A class for filtering molecular data based on presence.
 */
public class FrequencyFilter {
	private double overallPercent;
	private double groupPercent;
	Collection<String> groupAttributes;

	/**
	 * Creates a new FrequencyFilter.
	 * 
	 * @param overallPercent The minimum percentage that the molecule must be
	 *	present in all samples to be retained.
	 * @param groupPercent The minimum percentage that the molecule must be 
	 *	present in each group to be retained.
	 * @param groupAttributes The attributes to be used to separate the samples
	 *	into groups.
	 */
	public FrequencyFilter( double overallPercent, double groupPercent, 
	                        Collection<String> groupAttributes ) {
		this.overallPercent = overallPercent / 100;
		this.groupPercent = groupPercent / 100;
		this.groupAttributes = groupAttributes;
	}

	/**
	 * Filters the experiment's molecular data.
	 * 
	 * @param experiment The experiment to be filtered.
	 * @return The passed in experiment after filtering.
	 */
	public Set<Sample> filter( Set<Sample> samples ) {
		Logger logger = Logger.getLogger( getClass( ));
		Set<Molecule> molecules = new TreeSet( );
		Set<Sample> returnValue = new TreeSet( );
		for ( Sample sample : samples ) {
			molecules.addAll( sample.getMolecules( ));
			returnValue.add( new Sample( sample ));
		}
		// filter all molecules using the cross-board filter
		if ( this.overallPercent > 0.0 ) {
			logger.debug( String.format( "Cross-board Filter : %.1f%%", 
			                             overallPercent * 100 ));
			for ( Molecule molecule : 
						new TreeSet<Molecule>( molecules )) {
				if ( this.getFrequency( molecule.getValues( samples )) < overallPercent ) {
					dropMolecule( molecule, samples );
				}
			}
		}
		// filter all molecules using the group filter
		if ( this.groupPercent > 0.0 ) {
			logger.debug( String.format( "Group-based Filter : %.1f%%", 
			                             groupPercent * 100 ));
			List<SampleGroup> groups = new ArrayList<SampleGroup>( );
			StringBuilder key = new StringBuilder( );
			for ( Sample sample : returnValue ) {
				key.setLength( 0 );
				for ( String attribute : groupAttributes ) {
					key.append( sample.getAttribute( attribute ) + ":" );
				}
				int i;
				for ( i=0; i < groups.size( ); i++ ) {
					if ( groups.get( i ).getName( ).equals( key.toString( )))
						break;
				}
				if ( i == groups.size( ))
					groups.add( new SampleGroup( key.toString( )));
				logger.debug( "Adding " + sample + " to group " + key );
				groups.get( i ).add( sample );
			}
			for ( Molecule molecule : molecules ) {
				for ( SampleGroup group : groups ) {
					if ( this.getFrequency( molecule.getValues( group )) < 
							 groupPercent ) {
						logger.debug( "Zeroing group " + group.getName( ) + 
						              " for molecule " + molecule );
						molecule.setValues( group, 0.0 );
					}
				}
				// check to see if each molecule has a frequency of 0 now. if so, drop it.
				if ( Double.compare( 
						this.getFrequency( molecule.getValues( samples )), 0.0 ) == 0 ) {
					logger.debug( "Molecule " + molecule + " is now empty" );
					dropMolecule( molecule, returnValue );
				}
			}
		}

		return returnValue;
	}

	/**
	 * Drops the specified molecule from all experiments.
	 * 
	 * @param molecule The molecule to be dropped.
	 * @param samples The set of Samples the molecule is to be removed from.
	 */
	private static void dropMolecule( Molecule molecule, 
	                           Set<Sample> samples ) {
		Logger.getLogger( FrequencyFilter.class ).debug( 
			"Dropping Molecule " + molecule );
		for ( Sample sample : samples ) {
			sample.removeMolecule( molecule );
		}
	}

	/**
	 * Sets the percentage for the overall filter.
	 * 
	 * @param overallPercent The new percentage.
	 */
	public void setOverallPercent( double overallPercent ) {
		this.overallPercent = overallPercent / 100;
	}

	/**
	 * Gets the current overall percentage setting.
	 * 
	 * @return The current percentage setting.
	 */
	public double getOverallPercent( ) {
		return groupPercent * 100;
	}

	/**
	 * Sets the group-based percentage for the filter.
	 * 
	 * @param groupPercent the new percentage setting.
	 */
	public void setGroupPercent( double groupPercent ) {
		this.groupPercent = groupPercent / 100;
	}

	/**
	 * Gets the current group-based percentage setting.
	 * 
	 * @return The current percentage setting.
	 */
	public double getGroupPercent( ) {
		return this.groupPercent * 100;
	}

	/**
	 * Determines the presence frequency for a set of values.
	 * 
	 * @param values The values to check the frequency for.
	 * @return The ratio of values which are not zero.
	 */
	private double getFrequency( Collection<Number> values ) {
		// count the number of zeros
		int zeros = 0;
		for ( Number value : values ) {
			if ( Double.compare( value.doubleValue( ), 0.0 ) == 0 ||
				   Double.isNaN( value.doubleValue( )))
				zeros++;
		}
		return 1 - (double)zeros / values.size( );
	}
}

