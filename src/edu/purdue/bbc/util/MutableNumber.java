/*

Copyright: 2010 Purdue University

This file is distributed under the following terms (MIT/X11 License):

	Permission is hereby granted, free of charge, to any person
	obtaining a copy of this file and associated documentation
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
 * Creates a Number whose value can be changed. This can be useful for
 * instance if you wish to pass a number to a class and have that class
 * be aware of changes the value of the Number.
 */
public class MutableNumber extends Number {
	private Number value;

	/**
	 * Creates a new instance of MubableNumber with the specified value.
	 * 
	 * @param value The value for this Number.
	 */
	public MutableNumber( Number value ) {
		this.value = value;
	}

	/**
	 * Creates a new instance of MubableNumber with the specified value.
	 * 
	 * @param value The value for this Number.
	 */
	public MutableNumber( byte value ) {
		this.value = new Byte( value );
	}

	/**
	 * Creates a new instance of MubableNumber with the specified value.
	 * 
	 * @param value The value for this Number.
	 */
	public MutableNumber( double value ) {
		this.value = new Double( value );
	}

	/**
	 * Creates a new instance of MubableNumber with the specified value.
	 * 
	 * @param value The value for this Number.
	 */
	public MutableNumber( float value ) {
		this.value = new Float( value );
	}

	/**
	 * Creates a new instance of MubableNumber with the specified value.
	 * 
	 * @param value The value for this Number.
	 */
	public MutableNumber( int value ) {
		this.value = new Integer( value );
	}

	/**
	 * Creates a new instance of MubableNumber with the specified value.
	 * 
	 * @param value The value for this Number.
	 */
	public MutableNumber( long value ) {
		this.value = new Long( value );
	}

	/**
	 * Creates a new instance of MubableNumber with the specified value.
	 * 
	 * @param value The value for this Number.
	 */
	public MutableNumber( short value ) {
		this.value = new Short( value );
	}

	/**
	 * Changes the value of this Number to the specified value.
	 * 
	 * @param value The new value for this number.
	 */
	public void setValue( Number value ) {
		this.value = value;
	}

	/**
	 * Changes the value of this Number to the specified value.
	 * 
	 * @param value The new value for this number.
	 */
	public void setValue( byte value ) {
		this.value = new Byte( value );
	}

	/**
	 * Changes the value of this Number to the specified value.
	 * 
	 * @param value The new value for this number.
	 */
	public void setValue( double value ) {
		this.value = new Double( value );
	}

	/**
	 * Changes the value of this Number to the specified value.
	 * 
	 * @param value The new value for this number.
	 */
	public void setValue( float value ) {
		this.value = new Float( value );
	}

	/**
	 * Changes the value of this Number to the specified value.
	 * 
	 * @param value The new value for this number.
	 */
	public void setValue( int value ) {
		this.value = new Integer( value );
	}

	/**
	 * Changes the value of this Number to the specified value.
	 * 
	 * @param value The new value for this number.
	 */
	public void setValue( long value ) {
		this.value = new Long( value );
	}

	/**
	 * Changes the value of this Number to the specified value.
	 * 
	 * @param value The new value for this number.
	 */
	public void setValue( short value ) {
		this.value = new Short( value );
	}

	/**
	 * Gets the value of this Number as a byte.
	 * 
	 * @see Number#byteValue( )
	 * @return The value of this number as a byte.
	 */
	public byte byteValue( ) {
		return this.value.byteValue( );
	}

	/**
	 * Gets the value of this Number as a double.
	 * 
	 * @see Number#doubleValue( )
	 * @return The value of this number as a double.
	 */
	public double doubleValue( ) {
		return this.value.doubleValue( );
	}

	/**
	 * Gets the value of this Number as a float.
	 * 
	 * @see Number#floatValue( )
	 * @return The value of this number as a float.
	 */
	public float floatValue( ) {
		return this.value.floatValue( );
	}

	/**
	 * Gets the value of this Number as a int.
	 * 
	 * @see Number#intValue( )
	 * @return The value of this number as a int.
	 */
	public int intValue( ) {
		return this.value.intValue( );
	}

	/**
	 * Gets the value of this Number as a long.
	 * 
	 * @see Number#longValue( )
	 * @return The value of this number as a long.
	 */
	public long longValue( ) {
		return this.value.longValue( );
	}

	/**
	 * Gets the value of this Number as a short.
	 * 
	 * @see Number#shortValue( )
	 * @return The value of this number as a short.
	 */
	public short shortValue( ) {
		return this.value.shortValue( );
	}
}
