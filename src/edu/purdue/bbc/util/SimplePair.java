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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

/**
 * A Class containing a pair of Objects of the same type.
 */
public class SimplePair<T> implements Pair<T>, Collection<T> {
	protected T first;
	protected T second;
	protected static final UnsupportedOperationException UNSUPPORTED =
		new UnsupportedOperationException( "This operation is not supported" );

	/**
	 * Creates a new SimplePair.
	 * 
	 * @param first The first item in the pair.
	 * @param second The second item in the pair.
	 */
	public SimplePair( T first, T second ) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Gets the first item in this pair.
	 * 
	 * @return The first of the two items in the pair.
	 */
	public T getFirst( ) {
		return this.first; 
	}

	/**
	 * Gets the second item in this pair.
	 * 
	 * @return The second of the two items in the pair.
	 */
	public T getSecond( ) {
		return this.second;
	}

	/**
	 * Gets the opposite item in this Pair.
	 * 
	 * @param item One of the two items in this Pair.
	 * @return The other item in this Pair.
	 */
	public T getOpposite( T item ) {
		if ( this.first.equals( item ))
			return this.second;
		if ( this.second.equals( item ))
			return this.first;
		return null;
	}

	// Collection Interface Methods

	/**
	 * Unsupported operation.
	 */
	public boolean add( T t ) {
		throw UNSUPPORTED;
	}

	/**
	 * Unsupported operation.
	 */
	public boolean addAll( 	Collection <? extends T> c ) {
		throw UNSUPPORTED;
	}

	/**
	 * Unsupported operation.
	 */
	public void clear( ) {
		throw UNSUPPORTED;
	}

	/**
	 * Whether an item is a part of this Pair.
	 * 
	 * @see Collection#contains( Object )
	 * @param item The item to check for.
	 * @return boolean indicating whether the specified item is a part of this
	 *	pair.
	 */
	public boolean contains( Object item ) {
		return ( this.first.equals( item ) || this.second.equals( item ));
	}

	public boolean containsAll ( Collection <?> c ) {
		boolean returnValue = true;
		for ( Object item : c ) {
			returnValue = returnValue && this.contains( item );
			if ( !returnValue )
				break;
		}
		return returnValue;
	}

	/**
	 * Compares this Pair with another Collection for equality.
	 * 
	 * @see Collection#equals( Object )
	 * @param o the Object to compare with.
	 * @return true if the passed in object is a collection of the same size
	 *	and contains the same 2 items.
	 */
	public boolean equals ( Object o ) {
		if ( o == this ) {
			return true;
		}
		if ( o instanceof Pair ) {
			return this.contains( ((Pair)o).getFirst( )) && 
			       this.contains( ((Pair)o).getSecond( ));
		}
		if ( o instanceof Collection ) {
			if ( ((Collection)o).size( ) != 2 ) {
				return false;
			}
			Iterator iter = ((Collection)o).iterator( );
			return this.contains( iter.next( )) && this.contains( iter.next( ));
		}
		return false;
	}

	/**
	 * Returns a hashCode for this Object.
	 *
	 * @see Collection#hashCode( )
	 * @see Object#hashCode( )
	 * @return a hashCode for this Object.
	 */
	public int hashCode( ) {
		int hashCode = 1;
		Iterator<T> i = this.iterator( );
		while ( i.hasNext( )) {
				T obj = i.next( );
				hashCode = 31 * hashCode + ( obj == null ? 0 : obj.hashCode( ));
		}
		return hashCode;
	}

	/**
	 * Determines if this Pair is empty. In the base class this should never
	 * happen unless you construct the class with 2 null values.
	 *
	 * @see Collection#isEmpty( )
	 * @return a boolean indicating whether or not this Pair contains any items.
	 */
	public boolean isEmpty( ) {
		return ( this.first == null) && ( this.second == null );
	}

	/**
	 * Returns an iterator over the elements in this collection.
	 * 
	 * @see Collection#iterator( )
	 * @return an Iterator which iterates the 2 items in this Pair.
	 */
	public Iterator<T> iterator( ) {
		return new PairIterator( this.first, this.second );
	}

	/**
	 * Unsupported operation.
	 */
	public boolean remove( Object o ) {
		throw UNSUPPORTED;
	}

	/**
	 * Unsupported operation.
	 */
	public boolean removeAll( Collection <?> c  ) {
		throw UNSUPPORTED;
	}

	/**
	 * Unsupported operation.
	 */
	public boolean retainAll( Collection <?> c ) {
		throw UNSUPPORTED;
	}

	/**
	 * Returns the number of elements in this Collection, which is always 2.
	 * 
	 * @see Collection#size( )
	 * @return The size of this collection, 2.
	 */
	public int size( ) {
		return 2;
	}

	/**
	 * Returns an array containing all of the elements in this collection.
	 * 
	 * @see Collection#toArray( )
	 * @return This Pair as an Array.
	 */
	public Object [] toArray( ) {
		return new Object[] { this.first, this.second };
	}

	/**
	 * Returns an array containing all of the elements in this collection; 
	 * the runtime type of the returned array is that of the specified array.
	 * 
	 * @see Collection#toArray( T )
	 * @param array the array into which the elements of this collection are 
	 *	to be stored, if it is big enough; otherwise, a new array of the same 
	 *	runtime type is allocated for this purpose.
	 * @return An array containing all of the elemnts in this Collection.
	 */
	public <S> S[] toArray( S [] array ) {
		S[] returnValue = array;
		if ( returnValue.length <  2 ) {
			returnValue = (S[])Array.newInstance( 
				returnValue.getClass( ).getComponentType( ), 2 );
		} 
		if ( returnValue.length > 2  ) {
			returnValue[ 2 ] = null;
		}
		returnValue[ 0 ] = (S)this.first;
		returnValue[ 1 ] = (S)this.second;
		return returnValue;
	}

	/**
	 * A class for use with the iterator( ) function.
	 */
	private class PairIterator implements Iterator<T> {
		private int iterPos;
		private Object[] items;

		/**
		 * Constructs a new instance.
		 * 
		 * @param first The first Item in the Pair.
		 * @param second The second item in the Pair.
		 */
		public PairIterator( T first, T second ) {
			items = new Object[2];
			items[0] = first;
			items[1] = second;
		}

		/**
		 * Returns true if the iteration has more elements.
		 * 
		 * @see Iterator#hasNext( )
		 * @return true if the iterator has more elements.
		 */
		public boolean hasNext( ) {
			return ( iterPos < 1 );
		}

		/**
		 * Returns the next element in the Iteration.
		 * 
		 * see Iterator#next( )
		 * @return The next element in the iteration.
		 */
		public T next( ) {
			if ( this.hasNext( ))
				return (T)items[ iterPos++ ];
			return null;
		}

		/**
		 * Unsupported operation.
		 */
		public void remove( ) {
			throw UNSUPPORTED;
		}
	}
}


