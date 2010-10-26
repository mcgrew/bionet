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

package edu.purdue.jsysnet.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class for storing primitive wrappers in a List that can easily be
 * converted to an array of the primitive counterparts
 */
public class NumberList extends ArrayList<Number> {
	private byte[] byteArray;
	private double[] doubleArray;
	private float[] floatArray;
	private int[] intArray;
	private long[] longArray;
	private short[] shortArray;

	/**
	 * Creates a new empty NumberList
	 */
	public NumberList ( ) {
		super( );
	}

	/**
	 * Creates a new NumberList which contains the Numbers from the passed
	 * in collection.
	 * 
	 * @param c The collection to obtain new objects from.
	 */
	public NumberList( Collection<Number> c ) {
		super( c );
	}

	/**
	 * Creates a new numberList with a given initial capacity. It will be
	 * expanded as needed.
	 * 
	 * @param initialCapacity The initial capacity of the List.
	 */
	public NumberList( int initialCapacity ) {
		super( initialCapacity );
	}

	/**
	 * Adds a new Number to the List
	 * 
	 * @param e The new Number to be added.
	 * @return true if the append was successful.
	 */
	public boolean add( Number e ) {
		this.clearCache( );
		return super.add( e );
	}

	/**
	 * Adds a new Byte object to the List.
	 * 
	 * @param value The new value to be added.
	 * @return true if adding the value was accepted.
	 */
	public boolean add( byte value ) {
		return this.add( new Byte( value ));
	}

	/**
	 * Adds a new Double object to the List.
	 * 
	 * @param value The new value to be added.
	 * @return true if adding the value was accepted.
	 */
	public boolean add( double value ) {
		return this.add( new Double( value ));
	}

	/**
	 * Adds a new Float object to the List.
	 * 
	 * @param value The new value to be added.
	 * @return true if adding the value was accepted.
	 */
	public boolean add( float value ) {
		return this.add( new Float( value ));
	}

	/**
	 * Adds a new Integer object to the List.
	 * 
	 * @param value The new value to be added.
	 * @return true if adding the value was accepted.
	 */
	public boolean add( int value ) {
		return this.add( new Integer( value ));
	}

	/**
	 * Adds a new Long object to the List.
	 * 
	 * @param value The new value to be added.
	 * @return true if adding the value was accepted.
	 */
	public boolean add( long value ) {
		return this.add( new Long( value ));
	}

	/**
	 * Adds a new Short object to the list.
	 * 
	 * @param value The new value to be added.
	 * @return true if adding the value was accepted.
	 */
	public boolean add( short value ) {
		return this.add( new Short( value ));
	}

	/**
	 * Adds a new Number object at the specified location in the List.
	 * 
	 * @param element The new value to be added.
	 */
	public void add( int index, Number element ) {
		super.add( index, element );
		this.clearCache( );
	}

	/**
	 * Adds a new Byte object at the specified position in the List.
	 * 
	 * @param value The new value to be added.
	 */
	public void add( int index, byte value ) {
		this.add( index, new Byte( value ));
	}

	/**
	 * Adds a new Double object at the specified position in the List.
	 * 
	 * @param value The new value to be added.
	 */
	public void add( int index, double value ) {
		this.add( index, new Double( value ));
	}

	/**
	 * Adds a new Double object at the specified position in the List.
	 * 
	 * @param value The new value to be added.
	 */
	public void add( int index, float value ) {
		this.add( index, new Float( value ));
	}

	/**
	 * Adds a new Integer object at the specified position in the List.
	 * 
	 * @param value The new value to be added.
	 */
	public void add( int index, int value ) {
		this.add( index, new Integer( value ));
	}

	/**
	 * Adds a new Long object at the specified position in the List.
	 * 
	 * @param value The new value to be added.
	 */
	public void add( int index, long value ) {
		this.add( index, new Long( value ));
	}

	/**
	 * Adds a new Short object at the specified position in the List.
	 * 
	 * @param value The new value to be added.
	 */
	public void add( int index, short value ) {
		this.add( index, new Short( value ));
	}

	public boolean addAll( Collection<? extends Number> c ) {
		this.clearCache( );
		return super.addAll( c );
	}

	/**
	 * Clears all of the cached values in the private arrays. This should be
	 * called any time the list is modified. The cache will be rebuilt as needed.
	 */
	private void clearCache( ) {
		this.byteArray = null;
		this.doubleArray = null;
		this.floatArray = null;
		this.intArray = null;
		this.longArray = null;
		this.shortArray = null;
	}

	/**
	 * Converts this List to an array of bytes.
	 * 
	 * @return A byte array containing all values of this List.
	 */
	public byte[] asByteArray( ) {
		if ( byteArray == null || this.byteArray.length != this.size( )) {
			byteArray = new byte[ this.size( )];
			int i=0;
			for( Number t : this ) {
				byteArray[ i++ ]  = t.byteValue( );
			}
		}
		return this.byteArray;
	}

	/**
	 * Converts this List to an array of doubles.
	 * 
	 * @return A double array containing all values of this List. 
	 */
	public double[] asDoubleArray( ) {
		if ( this.doubleArray == null || this.doubleArray.length != this.size( )) {
			this.doubleArray = new double[ this.size( )];
			int i=0;
			for( Number t : this ) {
				this.doubleArray[ i++ ]  = t.doubleValue( );
			}
		}
		return this.doubleArray;
	}

	/**
	 * Converts this List to an array of floats.
	 * 
	 * @return A float array containing all values of this List.
	 */
	public float[] asFloatArray( ) {
		if ( this.floatArray == null || this.floatArray.length != this.size( )) {
			this.floatArray = new float[ this.size( )];
			int i=0;
			for( Number t : this ) {
				this.floatArray[ i++ ]  = t.floatValue( );
			}
		}
		return this.floatArray;
	}

	/**
	 * Converts this List an array of ints.
	 * 
	 * @return An int array containing all values of this List.
	 */
	public int[] asIntArray( ) {
		if ( this.intArray == null || this.intArray.length != this.size( )) {
			this.intArray = new int[ this.size( )];
			int i=0;
			for( Number t : this ) {
				this.intArray[ i++ ]  = t.intValue( );
			}
		}
		return this.intArray;
	}

	/**
	 * Converts this List to an array of longs.
	 * 
	 * @return A long array containing all values of this List.
	 */
	public long[] asLongArray( ) {
		if ( this.longArray == null || this.longArray.length != this.size( )) {
			this.longArray = new long[ this.size( )];
			int i=0;
			for( Number t : this ) {
				this.longArray[ i++ ]  = t.longValue( );
			}
		}
		return this.longArray;
	}

	/**
	 * Converts this List to an array of shorts.
	 * 
	 * @return A short array containing all values of this List.
	 */
	public short[] asShortArray( ) {
		if ( this.shortArray == null || this.shortArray.length != this.size( )) {
			this.shortArray = new short[ this.size( )];
			int i=0;
			for( Number t : this ) {
				this.shortArray[ i++ ]  = t.shortValue( );
			}
		}
		return this.shortArray;
	}

	/**
	 * Removes the object at the specified position in the List.
	 * @see java.util.ArrayList#remove(int)
	 * 
	 * @param index The index of the object to be removed.
	 * @return The Number which was removed from the List.
	 */
	public Number remove( int index ) {
		this.clearCache( );
		return super.remove( index );
	}

	/**
	 * Removes the first occurrence of the specified element from this List, if 
	 * it is present.
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 * 
	 * @param o The object to be removed.
	 * @return true if the object was found and removed.
	 */
	public boolean remove( Object o ) {
		this.clearCache( );
		return super.remove( o );
	}

	/**
	 * Removes all elements in the specified range.
	 * @see java.util.ArrayList#removeRange(int, int)
	 * 
	 * @param fromIndex The index of the first element to remove (inclusive)
	 * @param toIndex The index after the last element to remove (non-inclusive)
	 */
	protected void removeRange( int fromIndex, int toIndex ) {
		super.removeRange( fromIndex, toIndex );
		this.clearCache( );
	}

	/**
	 * Replaces the element at the specified position in this List with the
	 * specified value.
	 * @see java.util.ArrayList#set(int,E)
	 * 
	 * @param index The index of the value to be replaced.
	 * @param element The new value for the index.
	 * @return The old value of that index.
	 */
	public Number set( int index, Number element ) {
		Number returnValue = super.set( index, element );
		this.clearCache( );
		return returnValue;
	}

	/**
	 * Replaces the element at the specified position in this List with the
	 * specified value.
	 * 
	 * @param index The index of the value to be replaced.
	 * @param value The new value for the specified index.
	 * @return The old value of that index as a byte.
	 */
	public byte set( int index, byte value ) {
		return this.set( index, new Byte( value )).byteValue( );
	}

	/**
	 * Replaces the element at the specified position in this List with the
	 * specified value.
	 * 
	 * @param index The index of the value to be replaced.
	 * @param value The new value for the specified index.
	 * @return The old value of that index as a double.
	 */
	public double set( int index, double value ) {
		return this.set( index, new Double( value )).doubleValue( );
	}

	/**
	 * Replaces the element at the specified position in this List with the
	 * specified value.
	 * 
	 * @param index The index of the value to be replaced.
	 * @param value The new value for the specified index.
	 * @return The old value of that index as a float.
	 */
	public float set( int index, float value ) {
		return this.set( index, new Float( value )).floatValue( );
	}

	/**
	 * Replaces the element at the specified position in this List with the
	 * specified value.
	 * 
	 * @param index The index of the value to be replaced.
	 * @param value The new value for the specified index.
	 * @return The old value of that index as an int.
	 */
	public int set( int index, int value ) {
		return this.set( index, new Double( value )).intValue( );
	}

	/**
	 * Replaces the element at the specified position in this List with the
	 * specified value.
	 * 
	 * @param index The index of the value to be replaced.
	 * @param value The new value for the specified index.
	 * @return The old value of that index as a long
	 */
	public long set( int index, long value ) {
		return this.set( index, new Long( value )).longValue( );
	}

	/**
	 * Replaces the element at the specified position in this List with the
	 * specified value.
	 * 
	 * @param index The index of the value to be replaced.
	 * @param value The new value for the specified index.
	 * @return The old value of that index as a short.
	 */
	public short set( int index, short value ) {
		return this.set( index, new Short( value )).shortValue( );
	}

}

