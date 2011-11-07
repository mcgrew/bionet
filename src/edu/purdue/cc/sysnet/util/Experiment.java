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

package edu.purdue.cc.sysnet.util;

import edu.purdue.bbc.util.attributes.StringAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * A class for holding data about a particular experiment.
 * 
 * @author Thomas McGrew
 */
public class Experiment extends StringAttributes implements Comparable<Experiment> {

	private Collection <Molecule> molecules;
	private Collection <Correlation> correlations;
	private SortedSet <Sample> sampleSet;
	private String id;

	/**
	 * Constructor
	 * 
	 * @param id The id for this experiment.
	 */
	public Experiment( String id ) {
		this( id, new HashMap<String,String>( ));
	}

	/**
	 * Constructor
	 * 
	 * @param id The id for this experiment
	 * @param attributes A Map containing this Experiment's attributes.
	 */
	public Experiment( String id, Map <String,String> attributes ) {
		super( false );
		this.attributes = attributes;
		this.molecules = new TreeSet<Molecule>( );
		this.correlations = new ArrayList <Correlation>( );
		this.sampleSet = new TreeSet<Sample>( );
		this.id = id;
	}

	/**
	 * Returns the id of this experiment.
	 * 
	 * @return The id as a String.
	 */
	public String getId( ) {
		if ( this.id == null )
			return this.getAttribute( "exp_id" );
		return this.id;
	}

	/**
	 * Sets the id of this expeiment.
	 * 
	 * @param id The id for the experiment.
	 */
	public void setId( String id ) {
		this.id = id;
	}

	/**
	 * Gets the Molecule with the given id.
	 * 
	 * @param id The id of the Molecule to get.
	 * @return The requested Molecule, or null if it does not exist.
	 */
	public Molecule getMolecule( Object id ) {
		if ( id != null ) {
			for ( Molecule m : this.getMolecules( )) {
				if ( id.toString( ).equals( m.getId( )))
					return m;
			}
		}
		return null;
	}
	
	/**
	 * Gets the molecules associated with this Experiment.
	 * 
	 * @return A Colelction of Molecules.
	 */
	public Collection <Molecule> getMolecules( ) {
		return this.molecules;
	}

	/**
	 * Adds a Molecule to this Experiment.
	 * 
	 * @param molecule The molecule to be added.
	 */
	public void addMolecule( Molecule molecule ){
		if ( !this.molecules.contains( molecule )) {
			for ( Molecule m : this.molecules ) {
				this.correlations.add( new Correlation( m, molecule, this ));
			}
			this.molecules.add( molecule );
		} else {
			Logger.getLogger( getClass( )).debug( String.format( 
				"Experiment %s already contains Molecule %s", 
				this.getId( ), molecule.getId( )));
		}
	}

	/**
	 * Gets all Molecule Correlations present in this experiment. 
	 * 
	 * @return A Collection containing all of the Correlations.
	 */
	@Deprecated
	public Collection <Correlation> getCorrelations( ) {
		return this.correlations;
	}

	/**
	 * Returns the correlation object associated with the 2 passed in molecules.
	 * 
	 * @param molecule1 One molecule in the Correlation.
	 * @param molecule2 The other molecule in the Correlation.
	 * @return The requested correlation.
	 */
	@Deprecated
	public Correlation getCorrelation( Molecule molecule1, Molecule molecule2 ) {
		for ( Correlation correlation : this.correlations ) {
			if ( correlation.contains( molecule1 ) && 
			     correlation.contains( molecule2 ))
				return correlation;
		}
		return null;
	}

	/**
	 * Returns all Correlations associated with the passed in Molecule in this
	 * experiment.
	 * 
	 * @param molecule The Molecule to get Correlations for.
	 * @return A Collection containing the requested Correlations.
	 */
	@Deprecated
	public Collection<Correlation> getCorrelations( Molecule molecule ) {
		Collection<Correlation> returnValue = new ArrayList<Correlation>( );
		for ( Correlation correlation : this.correlations ) {
			if ( correlation.contains( molecule )) {
				returnValue.add( correlation );
			}
		}
		return returnValue;
	}

	/**
	 * Adds a sample to this experiment.
	 * 
	 * @param sample The sample to be added.
	 * @return true if the Experiment did not already contain the specified
	 * sample.
	 */
	public boolean addSample( Sample sample ) {
		return this.sampleSet.add( sample );
	}

	/**
	 * Removes a sample from this experiment.
	 * 
	 * @param sample The sample to remove from this Experiment.
	 * @return true if the sample was removed.
	 */
	public boolean removeSample( Object sample ) {
		return this.sampleSet.remove( sample );
	}

	/**
	 * Gets the of samples for this experiment;
	 * 
	 * @return All samples associated with this experiment.
	 */
	public Set<Sample> getSamples( ) {
		return this.sampleSet;
	}

	/**
	 * Retrieves a sample by name.
	 * 
	 * @param name The name of the sample to be retrieved.
	 * @return The requested sample.
	 */
	public Sample getSample( String name ) {
		for ( Sample sample : this.sampleSet ) {
			if ( sample.toString( ).equals( name ))
				return sample;
		}
		return null;
	}

	/**
	 * Returns the description of this Experiment, if any.
	 * 
	 * @return The description attribute.
	 */
	public String toString( ) {
		return String.format( "%s - %s", 
			this.getId( ),
			this.getAttribute( "description" ));
	}

	/**
	 * The compareTo method of the Comparable interface.
	 * 
	 * @param e The Experiment to compare this Experiment to
	 * @return Negative if the
	 */
	public int compareTo( Experiment e ) {
		return this.getId( ).compareTo( 
			e.getId( ));
	}

}


