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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A class for holding data about a group of Samples.
 *
 * @author Thomas McGrew
 */
public class SampleGroup extends TreeSet<Sample> 
                         implements Comparable<SampleGroup> {

	protected String name;

	/**
	 * Constructor.
	 * 
	 * @param name The name of this SampleGroup.
	 */
	public SampleGroup( String name ) {
		super( );
		this.name = name;
	}

	/**
	 * Creates a new SampleGroup
	 * 
	 * @param name The name of this SamplGroup.
	 * @param samples A collection of samples to add to this group.
	 */
	public SampleGroup( String name, Collection<Sample> samples ) {
		super( samples );
		this.name = name;
	}

	/**
	 * Creates a shallow copy of the passed in SampleGroup
	 * 
	 * @param group The group to create a copy of.
	 */
	public SampleGroup( SampleGroup group ) {
		super( group );
		this.name = group.getName( );
	}

	/**
	 * Gets a molecule by its id attribute.
	 * 
	 * @param id The id of the Sample to be retrieved.
	 * @return The requested Sample.
	 */
	public Sample getSample( String id ) {
		for ( Sample s : this ) {
			if ( id.equals( s.getAttribute( "id" )))
				return s;
		}
		return null;
	}

	/**
	 * Sets the Name of this group
	 * 
	 * @param name The new name for this group.
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * Gets the name for this group.
	 * 
	 * @return A String containing the name of this group.
	 */
	public String getName( ) {
		return this.name;
	}

	/**
	 * Gets the name for this group.
	 * 
	 * @return A String containing the name of this group.
	 */
	public String toString( ) {
		return this.name;
	}

	/**
	 * The compareTo method of the Comparable interface.
	 * @see java.lang.Comparable#compareTo(T)
	 * 
	 * @param sg Compares this object with the specified object for order. 
	 * @return a negative integer, zero, or a positive integer as this SampleGroup
	 *	is less than, equal to, or greater than the specified SampleGroup.
	 */
	public int compareTo( SampleGroup sg ) {
		int returnValue = this.getName( ).compareTo( sg.getName( ));
		if ( returnValue == 0 ) {
			returnValue = (int)Math.signum( this.size( ) -  sg.size( ));
		}
		if ( returnValue == 0 ) {
			Iterator<Sample> mySamples = this.iterator( );
			Iterator<Sample> hisSamples = sg.iterator( );
			while( returnValue == 0 ) {
				if ( mySamples.hasNext( ) && !hisSamples.hasNext( ))
					return 1;
				if ( !mySamples.hasNext( ) && hisSamples.hasNext( ))
					return -1;
				if ( !mySamples.hasNext( ) && !hisSamples.hasNext( ))
					return 0;
				returnValue = mySamples.next( ).compareTo( hisSamples.next( ));
			}
			
		}
		return returnValue;
	}

	/**
	 * Returns the name of all possible attributes for all samples.
	 * 
	 * @return A collection containing the name of all valid attributes.
	 */
	public Set<String> getAttributeNames( ) {
		Set<String> returnValue = new TreeSet( );
		for( Sample sample : this ) {
			returnValue.addAll( sample.getAttributes( ).keySet( ));
		}
		return returnValue;
	}

	/**
	 * Returns all values for the given attribute in the samples in this group.
	 * 
	 * @param attribute The attribute to retrieve values for.
	 * @return all values for the given attribute.
	 */
	public Set<String> getValues( String attribute ) {
		Set<String> returnValue = new TreeSet( );
		for ( Sample sample: this ) {
			returnValue.add( sample.getAttribute( attribute ));
		}
		return returnValue;
	}

	/**
	 * Returns a Map containing the attribute names each paired with it's
	 * potential values.
	 * 
	 * @return A Map containing the attributes of the contained samples.
	 */
	public Map<String,Set<String>> getAttributes( ) {
		Set<String> attNames = this.getAttributeNames( );
		Map<String,Set<String>> returnValue = new TreeMap( );
		for ( String attribute : attNames ) {
			returnValue.put( attribute, this.getValues( attribute ));
		}
		return returnValue;

	}

}

