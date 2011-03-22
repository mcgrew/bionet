/*

Copyright: 2010 Bindley Bioscience Center, Purdue University

License: MIT license.

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

public class AttributesFilterList<T extends Attributes>
                                  extends ArrayList<AttributesFilter<T>> 
                                  implements AttributesFilter<T> {

	/**
	 * Creates a new CriteriaFilter
	 */
	public AttributesFilterList( ) { 
		super( );
	}

	/**
	 * Creates a new AttributesFilterList with the initial number of Criterion slots.
	 * 
	 * @see ArrayList#constructor( int )
	 * @param initialSize The initial size of the backing List
	 */
	public AttributesFilterList( int initialSize ) {
		super( initialSize );
	}

	/**
	 * Creates a new AttributesFilterList with the passed in AttributesFilters
	 * 
	 * @see ArrayList#constructor( Collection )
	 * @param criteria The initial set of Criteria to use.
	 */
	public AttributesFilterList( Collection<AttributesFilter<T>> criteria ) {
		super( criteria );
	}

	/**
	 * Filters the passed in Attribute objects based on this filter's criteria.
	 * 
	 * @see AttributesFilter#filter( Collection )
	 * @param attributes The set of Attributes to be filtered.
	 * @return The new set containing only objects which passed through the 
	 *	filtering process.
	 */
	public Collection<T> filter( Collection<T> attributes ) {

		Collection<T> returnValue = new ArrayList<T>( attributes );
		for ( AttributesFilter a : this ) {
			returnValue = a.filter( returnValue );
		}
		return returnValue;
	}

}



