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

package edu.purdue.bbc.util;

import java.util.TreeMap;

/**
 * A class for holding data in a sparse 2D array
 */
public class SparseMatrix<T> {
	private TreeMap<Key,T> values;
	private T defaultValue;

	/**
	 * Creates a new SparseMatrix with a null default value.
	 */
	public SparseMatrix( ) {
		this( null );
	}

	/**
	 * Creates a new SparseMatrix with the passed in default value.
	 * 
	 * @param defaultValue The value to return if the index is not set.
	 */
	public SparseMatrix( T defaultValue ) {
		this.values = new TreeMap<Key,T>( );
		this.defaultValue = defaultValue;
	}

	/**
	 * Sets a value in the matrix.
	 * 
	 * @param x The x coordinate of the value to set.
	 * @param y The y coordinate of the value to set.
	 * @param value The value to set the index to.
	 * @return The old value of this location, or the default value if there 
	 *	is not one.
	 */
	public T set( int x, int y, T value ) {
		T returnValue = this.values.put( new Key( x, y ), value );
		if ( returnValue == null )
			returnValue = this.defaultValue;
		return returnValue;
	}

	/**
	 * Gets a value from the matrix.
	 * 
	 * @param x The x coordinate of the value to get.
	 * @param y The y coordinate of the value to get.
	 * @return The value of the specified coordinate if it is set, or the
	 *	default value if it is not.
	 */
	public T get( int x, int y ) {
		T returnValue = this.values.get( new Key( x, y ));
		if ( returnValue == null )
			returnValue = this.defaultValue;
		return returnValue;
	}

	/**
	 * Determines whether this value has been set or not.
	 * 
	 * @param x The x coordinate of the value to check.
	 * @param y The y coordinate of the value to check.
	 * @return A boolean indicating whether the given location has been set.
	 */
	public boolean isSet( int x, int y ) {
		return this.values.containsKey( new Key( x, y ));
	}

	/**
	 * Gets the default value for this matrix. This is the value that is returned
	 * when an unset location is specified.
	 * 
	 * @return The default value for this matrix.
	 */
	public T getDefault( ) {
		return this.defaultValue;
	}

	/**
	 * Sets the default value for this matrix. This is the value that is returned
	 * when an unset location is specified.
	 * 
	 * @param defaultValue The value to set the default to.
	 */
	public void setDefault( T defaultValue ) {
		this.defaultValue = defaultValue;
	}
	
	/**
	 * A class for use as an index to keys in the internal Map.
	 */
	private class Key implements Comparable<Key> {
		public int x;
		public int y;

		/**
		 * Creates a new Key.
		 * 
		 * @param x The x value for the key.
		 * @param y The y value for the key.
		 */
		public Key( int x, int y ) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Compares two keys to see which should be ordered first. They are ordered
		 * first by x value, then by y if x is the same.
		 * 
		 * @see Comparable#compareTo(T)
		 * @param k The Key to compare this Key to.
		 * @return An int indicating the order for these keys.
		 */
		public int compareTo( Key k ) {
			int returnValue = this.x - k.x;
			if ( returnValue == 0 ) {
				returnValue = this.y - k.y;
			}
			return returnValue;
		}

		/**
		 * Test two keys for equality.
		 * 
		 * @see Object#equals(Object)
		 * @param k The Key to compare this key to.
		 * @return A boolean indicating whether the two keys are equal.
		 */
		public boolean equals( Object k ) {
			if ( Key.class.isAssignableFrom( k.getClass( )))
				return this.compareTo( (Key)k ) == 0;
			return this == k;
		}
	}
}

