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

import edu.purdue.bbc.util.Range;

import java.awt.Paint;

/**
 * A Class for mapping a color value to each value in a Range, based on the visible spectrum.
 * This implementation splits the spectrum on 0, and returns colors in the lower end of the
 * spectrum for values &lt; 0, and the upper end for values &gt; 0.
 */
public class SplitSpectrum extends Spectrum {

	public SplitSpectrum( double lowerBound, double upperBound ) {
		super( lowerBound, upperBound );
	}

	public SplitSpectrum( double lowerBound, double upperBound, Paint outOfRangePaint ) {
		super( lowerBound, upperBound, outOfRangePaint );
	}

	public SplitSpectrum( Range range ) {
		super( range );
	}

	public SplitSpectrum( Range range, Paint outOfRangePaint ) {
		super( range, outOfRangePaint );
	}

	/**
	 * Normalizes the value based on a split spectrum model mirrored in positive and negative.
	 * Positive values for value return a number between 0.5 and 1, Negative values return
	 * a number between 0 and 0.5.
	 * 
	 * @param value The value to be normalized.
	 * @return The normalized value.
	 */
	protected float normalize( float value ) {
		float returnValue = (float)( 0.5f + 
			( Math.abs( value ) - this.range.getMin( )) / 
				( this.range.getSize( ) * 2 ));
		return ( value < 0f ) ? 1f - returnValue : returnValue;
	}

	/**
	 * Returns true if the value is contained in this Spectrum's Range.
	 * 
	 * @param value The value to be checked.
	 * @return Whether the value is in range or not.
	 */
	protected boolean inRange( float value ) {
		return ( value != Float.NaN && this.range.contains( Math.abs( value )));
	}

	/**
	 * Creates a copy of this Object.
	 * 
	 * @return A new SplitSpectrum with the same Range.
	 */
	public SplitSpectrum clone( ) {
		return new SplitSpectrum( this.range );
	}

}

