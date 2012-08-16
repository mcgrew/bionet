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

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Set;

import edu.purdue.bbc.util.NumberList;

import org.apache.log4j.Logger;

/**
 * Representation class for Molecule data
 * 
 * @author Thomas McGrew
 */
public class Molecule implements Comparable<Molecule> {
	
	protected Map <String,String> attributes;
	protected SortedMap <Sample,Number> samples;
	protected Experiment experiment;
	protected double molecularWeight;
	public String id;
  // optimization: only create one instance of each id to speed
  // up comparison of molecules for equality.
  private static List<String> ids = new ArrayList( );

	/**
	 * Constructs a new Molecule with the given id.
	 * 
	 * @param id The id of the new Molecule
	 */
	public  Molecule( String id ) {
		Logger.getLogger( getClass( )).debug( "Creating Molecule " + id );
		this.attributes = new HashMap <String,String>( );
		this.molecularWeight = Double.NaN;
    if ( ids.contains( id )) {
      this.id = ids.get( ids.indexOf( id ));
    } else {
      ids.add( id ); 
      this.id = id;
    }
	}

	/**
	 * Gets the id of this Molecule.
	 * 
	 * @return A String containing the Molecule's id.
	 */
	public String getId( ) {
		return this.id;
	}

	/**
	 * Gives this Molecule a new attribute.
	 * 
	 * @param attribute The name of the attribute to be set.
	 * @param value The value for the Attribute. 
	 */
	public void setAttribute( String attribute, String value ) {
		this.attributes.put( attribute.toLowerCase( ).trim( ), value );
	}

	/**
	 * Retrieve an Attribute for this Molecule.
	 * 
	 * @param attribute The attribute to be retrieved.
	 * @return The value of the attribute as a String.
	 */
	public String getAttribute( String attribute ) {
		return this.attributes.get( attribute.toLowerCase( ).trim( ));
	}

	/**
	 * Returns all attributes for this Molecule.
	 * 
	 * @return A Map containing all meta attributes.
	 */
	public Map <String,String> getAttributes( ) {
		return this.attributes;
	}

	/**
	 * Sets the molecular weight of the molecule;
	 * 
	 * @param mw The molecular weight of the molecule;
	 */
	public void setMolecularWeight( double mw ) {
		this.molecularWeight = mw;
	}

	/**
	 * Gets the molecularWeight attribute for the Molecule.
	 * 
	 * @return An int containing the &quot;molecularWeight&quot; attribute.
	 */
	public double getMolecularWeight( ){
		return this.molecularWeight;
	}

	/**
	 * Adds a new sample value to this Molecule.
	 * 
	 * @param sample The Sample this value is associated with.
	 * @param value The value of this sample;
	 */
	public void setValue( Sample sample, double value ) {
		this.addSample( sample, new Double( value ));
	}

	/**
	 * Adds a new sample value to this Molecule.
	 * 
	 * @param sample The Sample this value it associated with.
	 * @param value The value of this sample;
	 */
	public void setValue( Sample sample, Number value ) {
		sample.setValue( this, value );
	}

	/**
	 * Adds the given sample values to this molecule. If the values list is 
	 * longer, extra values are ignored. If the samples list is longer, the
	 * values list is padded with zeros.
	 * 
	 * @param samples The samples to set the values for.
	 * @param values The values of the samples.
	 */
	public void setValues( List<Sample> samples, List<Number> values ) {
		for ( int i=0; i < samples.size( ); i++ ) {
			this.setValue( samples.get( i ), ( i < values.size( )) ? 
			               values.get( i ) : new Double( 0.0 ));
		}
	}

	/**
	 * Adds the given sample values to this molecule.
	 * 
	 * @param samples The samples to set the values for.
	 * @param value The value of all passed in samples.
	 */
	public void setValues( Collection<Sample> samples, Number value ) {
		for ( Sample sample : samples ) {
			this.setValue( sample, value );
		}
	}

	/**
	 * Adds the given sample values to this molecule.
	 * 
	 * @param samples The samples to set the values for.
	 * @param value The value of all passed in samples.
	 */
	public void setValues( Collection<Sample> samples, double value ) {
		for ( Sample sample : samples ) {
			this.setValue( sample, value );
		}
	}

	/**
	 * Adds a new sample value to this Molecule.
	 * 
	 * @deprecated This method is being replaced by setValue( Sample, Number )
	 * @param sample The Sample this value it associated with.
	 * @param value The value of this sample;
	 */
	public void addSample( Sample sample, Number value ) {
		this.setValue( sample, value );
	}

	/**
	 * Returns the sample value for the appropriate Samples
	 * 
	 * @param samples The samples to get the values for.
	 * @return The sample values.
	 */
	public NumberList getValues ( Collection<Sample> samples ) {
		NumberList returnValue = new NumberList( samples.size( ));
		for( Sample sample : samples ) {
			returnValue.add( sample.getValue( this ));
		}
		return returnValue;
	}

	/**
	 * Returns the concentratios associated with this Molecule in each sample
	 * as a Map.
	 * 
	 * @param samples The samples to get the concentration values for.
	 * @return A Map containing the samples and concentration values.
	 */
	public Map<Sample,Number> getSampleMap( Collection<Sample> samples ) {
		Map<Sample,Number> returnValue = new TreeMap<Sample,Number>( );
		for ( Sample sample : samples ) {
			returnValue.put( sample, sample.getValue( this ));
		}
		return returnValue;
	}

	/**
	 * Returns the concentration of this Molecule in the specified Sample.
	 * 
	 * @param sample The sample to retrieve the concentration value for.
	 * @return The value of the concentration of this Molecule.
	 */
	public Number getValue( Sample sample ) {
		return sample.getValue( this );
	}

	/**
	 * Returns a string representation of this Molecule, which is the same
	 * as calling getId( ).
	 * 
	 * @return A string representation of this Molecule.
	 */
	public String toString( ) {
		return this.getId( );
	}

	/**
	 * Compares this Molecule to another.
	 * 
	 * @param m The Molecule to compare this one to.
	 * @return An integer indicating which order the two Molecules should be in
	 *	in a sorted list.
	 */
	public int compareTo( Molecule m ) {
		int returnValue = this.id.compareTo( m.id );
		return returnValue;
	}

	/**
	 * Compares this molecule to another object.
	 * 
	 * @param m The Object to compare this Molecule to.
	 * @return A boolean indicating if the 2 Objects are equal.
	 */
	public boolean equals( Object m ) {
    if ( m == this )
      return true;
		if ( m instanceof Molecule ) {
      if ( ((Molecule)m).id == this.id )
        return true;
      return false;
    }
		return this.toString( ).equals( m.toString( ));
	}
}

