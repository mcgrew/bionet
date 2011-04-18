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

package edu.purdue.cc.jsysnet.util;

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
	protected String id;

	/**
	 * Constructs a new Molecule with the given id.
	 * 
	 * @param id The id of the new Molecule
	 */
	public  Molecule( String id ) {
		Logger.getLogger( getClass( )).debug( "Creating Molecule " + id );
		this.attributes = new HashMap <String,String>( );
		this.molecularWeight = Double.NaN;
		this.id = id;
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
	 * @param sample The Sample this value it associated with.
	 * @param value The value of this sample;
	 */
	public void setValue( Sample sample, double value ) {
		this.addSample( sample, new Double( value ));
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
	 * Adds a new sample value to this Molecule.
	 * 
	 * @param sample The Sample this value it associated with.
	 * @param value The value of this sample;
	 */
	public void setValue( Sample sample, Number value ) {
		sample.setValue( this, value );
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

	public Map<Sample,Number> getSampleMap( Collection<Sample> samples ) {
		Map<Sample,Number> returnValue = new TreeMap<Sample,Number>( );
		for ( Sample sample : samples ) {
			returnValue.put( sample, sample.getValue( this ));
		}
		return returnValue;
	}

	public Number getValue( Sample sample ) {
		return sample.getValue( this );
	}

	public String toString( ) {
		return this.getId( );
	}

	public int compareTo( Molecule m ) {
		int returnValue = this.getId( ).compareTo( m.getId( ));
		return returnValue;
	}

	public boolean equals( Object m ) {
		if ( m instanceof Molecule )
			return ( this.compareTo((Molecule)m ) == 0 );
		return this.toString( ).equals( m.toString( ));
	}
}

