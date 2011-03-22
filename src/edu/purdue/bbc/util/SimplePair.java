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

package edu.purdue.bbc.util;

/**
 * A Class containing a pair of Objects of the same type.
 */
public class SimplePair<T> implements Pair<T> {
	private T firstItem;
	private T secondItem;

	/**
	 * Creates a new SimplePair.
	 * 
	 * @param first The first item in the pair.
	 * @param second The second item in the pair.
	 */
	public SimplePair( T first, T second ) {
		this.firstItem = first;
		this.secondItem = second;
	}

	/**
	 * Gets the first item in this pair.
	 * 
	 * @return The first of the two items in the pair.
	 */
	public T getFirstItem( ) {
		return this.firstItem; 
	}

	/**
	 * Gets the second item in this pair.
	 * 
	 * @return The second of the two items in the pair.
	 */
	public T getSecondItem( ) {
		return this.secondItem;
	}

	/**
	 * Gets the opposite item in this Pair.
	 * 
	 * @param item One of the two items in this Pair.
	 * @return The other item in this Pair.
	 */
	public T getOpposite( T item ) {
		if ( this.firstItem.equals( item ))
			return this.secondItem;
		if ( this.secondItem.equals( item ))
			return this.firstItem;
		return null;
	}

	/**
	 * Whether an item is a part of this Pair.
	 * 
	 * @param item The item to check for.
	 * @return boolean indicating whether the specified item is a part of this
	 *	pair.
	 */
	public boolean hasItem( T item ) {
		return ( this.firstItem.equals( item ) || this.secondItem.equals( item ));
	}
}


