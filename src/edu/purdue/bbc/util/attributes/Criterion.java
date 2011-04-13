/*

Copyright: 2010 Bindley Bioscience Center, Purdue University

License: X11 license.

	Permission is hereby granted, free of charge, to any person
	obtaining a copy of this software and associated documentation
	files (the "Software"), to deal in the Software without
	restriction, including without limitation the rights to use,
	copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the
	Software is furnished to do so, subject to the following
	conditions:

	The above copyright notice and this permission notice shall be
	included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
	EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
	OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
	NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
	HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
	WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
	FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
	OTHER DEALINGS IN THE SOFTWARE.

*/

package edu.purdue.bbc.util.attributes;

import edu.purdue.bbc.util.Attributes;

import java.util.ArrayList;
import java.util.Collection;
/**
 * An interface for filtering a set of Attributes based on a single Criterion.
 */
public class Criterion<T extends Attributes> 
                         implements AttributesFilter<T>,Cloneable {
	public static final int EQUAL = 1;
	public static final int LESS = 2;
	public static final int GREATER = 4;
	public static final int NOT_EQUAL = LESS | GREATER;

	protected String key;
	protected Comparable value;
	protected int condition;

	public Criterion( String key, Comparable value, int condition ) {
		this.key = key;
		this.value = value;
		this.condition = condition;
	}

	/**
	 * Sets the attribute to be used in evaluating the Object's validity.
	 * 
	 * @param key The Attribute name to use for filtering.
	 */
	public void setKey( String key ) {
		this.key = key;
	}

	/**
	 * Gets the attribute to be used in evaluating an Object's validity.
	 * 
	 * @return The Attribute name to be used for filtering.
	 */
	public String getKey( ) {
		return this.key;
	}

	/**
	 * Sets the value to be used for filtering the Attributes objects based on
	 * attributes values.
	 * 
	 * @param c The value to be used to confirm the validity of an Attributes
	 *	object.
	 */
	public void setValue( Comparable c ) {
		this.value = c;
	}

	/**
	 * Gets the value to be used for filtering the Attributes objects based on
	 * attributes values.
	 * 
	 * @return The value to be used to confirm the validity of an Attributes
	 *	object.
	 */
	public Comparable getValue( ) {
		return this.value;
	}

	/**
	 * Set the condition for passing the Attributes object through the filter.
	 * 
	 * @param condition
	 */
	public void setCondition( int condition ) {
		this.condition = condition;
	}

	/**
	 * Get the condition for passing the  object through the filter.
	 * 
	 * @return An int referring to the condition to use.
	 */
	public int getCondition( ) {
		return this.condition;
	}

	/**
	 * Creates a copy of this Criterion.
	 * 
	 * @see Object#clone( )
	 * @return A copy of this Criterion.
	 */
	public Criterion clone( ) {
		return new Criterion( this.key, this.value, this.condition );
	}

	/**
	 * Filters the attribute Collection based on this Criterion.
	 * 
	 * @param attributesCollection The collection of attributes to be filtered.
	 * @return The filtered Collection of attributes.
	 */
	public Collection<T> filter( 
			Collection<T> attributesCollection ) {

		Collection<T> returnValue = new ArrayList<T>( );
		for ( T attributes : attributesCollection ) {
			Comparable value = (Comparable)attributes.getAttribute( this.key );
			if ( this.passes( value )) {
				returnValue.add( attributes );
			}
		}
		return returnValue;
	}

	/**
	 * Determines whether or not this value meets the Criterion.
	 * 
	 * @param value The value to be compared.
	 * @return true if the value meets the criterion.
	 */
	protected boolean passes( Comparable value ) {
		int result = value.compareTo( this.value );
		return ( result == 0 && ( this.condition & EQUAL   ) != 0 ) ||
		       ( result >  0 && ( this.condition & GREATER ) != 0 ) ||
		       ( result <  0 && ( this.condition & LESS    ) != 0 );
	}
}

