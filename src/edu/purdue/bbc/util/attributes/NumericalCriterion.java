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

/**
 * An interface for filtering a set of Attributes based on a single Criterion.
 */
public class NumericalCriterion<T extends Attributes> extends Criterion<T> {

	public NumericalCriterion( String key, Number value, int condition ) {
		super( key, (Comparable)value, condition );
	}

	/**
	 * Sets the value for this Criterion.
	 * 
	 * @param c The value for this Criterion. If c is not a Number, This value's 
	 *	toString() method is called and the result is passed to 
	 *	Double(). This should therefore be either a Number or a string
	 *	containing a valid number.
	 */
	@Override
	public void setValue( Comparable c ) {
		if ( c instanceof Number )
			this.value = c;
		else
			this.value = new Double( c.toString( ));
	}

	/**
	 * Creates a copy of this Criterion.
	 * 
	 * @see Object#clone( )
	 * @return A copy of this Criterion.
	 */
	public Criterion clone( ) {
		return new NumericalCriterion( this.key, (Number)this.value, this.condition );
	}

	/**
	 * Determines whether or not this value meets the Criterion.
	 * 
	 * @param value The value to be compared.
	 * @return true if the value meets the criterion.
	 */
	@Override
	protected boolean passes( Comparable value ) {
		int result;
		if ( value instanceof Number ) {
			result = value.compareTo( this.value );
		}
		try {
			result = Double.compare( Double.parseDouble( value.toString( )),
			                        ((Number)this.value).doubleValue( ));
		} catch ( NumberFormatException e ) {
			return false;
		}
		return ( result == 0 && ( this.condition & EQUAL   ) != 0 ) ||
		       ( result >  0 && ( this.condition & GREATER ) != 0 ) ||
		       ( result <  0 && ( this.condition & LESS    ) != 0 );
	}
}

