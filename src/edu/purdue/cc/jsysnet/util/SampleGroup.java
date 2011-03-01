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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class for holding data about a group of Samples.
 *
 * @author Thomas McGrew
 */
public class SampleGroup extends ArrayList<Sample> 
                         implements Comparable<SampleGroup> {

	private String name;

	/**
	 * Constructor.
	 * 
	 * @param name The name of this SampleGroup.
	 */
	public SampleGroup( String name ) {
		this.name = name;
	}

	/**
	 * Gets a molecule by its id attribute.
	 * 
	 * @param id The id of the Sample to be retrieved.
	 * @return The requested Sample.
	 */
	public Sample getSample( String id ) {
		for ( Sample m : this ) {
			if ( id.equals( m.getAttribute( "id" )))
				return m;
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

}

