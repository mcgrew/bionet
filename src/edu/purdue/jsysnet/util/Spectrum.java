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

import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;

import org.jfree.chart.renderer.PaintScale;

public class Spectrum implements PaintScale,Cloneable,Serializable {
	private Range range;

	public Spectrum( ) {
		this( 0.0, 1.0 );
	}

	public Spectrum( double lowerBound, double upperBound ) {
		this.range = new Range( lowerBound, upperBound );
	}

	public Spectrum( Range range ) {
		this.range = range;
	}

	public double getLowerBound( ) {
		return this.range.getMin( );
	}

	public double getUpperBound( ) {
		return this.range.getMax( );
	}

	public void setLowerBound( double lowerBound ) {
		this.range.setMin( lowerBound );
	}

	public void setUpperBound( double upperBound ) {
		this.range.setMax( upperBound );
	}

	public Paint getPaint( double value ) throws IllegalArgumentException {
		if ( !this.range.contains( value ) || value == Double.NaN ) {
			return Color.WHITE;
		}
		if ( value < this.range.getMin( ) || value > this.range.getMax( )) {
			throw new IllegalArgumentException( String.format( 
				"%f is not a valid value for this %s. The value must be between %f and %f (inclusive).",
				value, this.getClass( ).getName( ), this.range.getMin( ), this.range.getMax( )));
		}
		value = ( value - this.range.getMin( )) / ( this.range.getSize( ));
		float red   = (float)( Math.max( 0, value * 2 - 1 ));
		float green = (float)( Math.max( 0, 1 - 2 * Math.abs( value - 0.5 ) ));
		float blue  = (float)( Math.max( 0, ( 1 - value ) * 2 - 1 ));
//		System.out.println( String.format( "Color: %f, %f, %f", red, green, blue ));
		return new Color( red, green, blue );
	}

	public Spectrum clone( ) {
		return new Spectrum( this.range.clone( ));
	}

}

