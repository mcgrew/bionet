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

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.renderer.PaintScale;

public class Spectrum implements PaintScale,Cloneable {
	protected Range range;
	protected Paint outOfRangePaint = null;

	public Spectrum( ) {
		this( 0.0, 1.0 );
	}

	public Spectrum( double lowerBound, double upperBound ) {
		this.range = new Range( lowerBound, upperBound );
	}

	public Spectrum( double lowerBound, double upperBound, Paint outOfRangePaint ) {
		this.range = new Range( lowerBound, upperBound );
		this.outOfRangePaint = outOfRangePaint;
	}

	public Spectrum( Range range ) {
		this.range = range;
	}

	public Spectrum( Range range, Paint outOfRangePaint ) {
		this.range = range;
		this.outOfRangePaint = outOfRangePaint;
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
		if ( !this.inRange( value )) {
			if ( this.outOfRangePaint != null ) {
				return this.outOfRangePaint;
			} else {
				throw new IllegalArgumentException( String.format( 
					"%f is not a valid value for this %s. The value must be between %f and %f (inclusive).",
					value, this.getClass( ).getName( ), this.range.getMin( ), this.range.getMax( )));
			}
		}
		value = this.normalize( value );
		return new Color( 
			this.getRed( value ), 
			this.getGreen( value ), 
			this.getBlue( value ));
	}

	public Spectrum clone( ) {
		return new Spectrum( this.range );
	}

	/**
	 * Returns true if the value is contained in this Spectrum's Range.
	 * 
	 * @param value The value to be checked.
	 * @return Whether the value is in range or not.
	 */
	protected boolean inRange( double value ) {
		return ( value != Double.NaN && this.range.contains( value ));
	}

	protected double normalize( double value ) {
		return ( value - this.range.getMin( )) / ( this.range.getSize( ));
	}

	public float getRed( double v ) {
		return this.getRed( (float)v );
	}

	public float getRed( float v ) {
		return (float)( Math.min( 1, Math.max( 0, 4 * v - 2 )));
	}

	public float getGreen( double v ) {
		return this.getGreen( (float)v );
	}

	public float getGreen( float v ) {
		return (float)( Math.min( 1, Math.max( 0, -4 * Math.abs( v - 0.5 ) + 1.5 )));
	}

	public float getBlue( double v ) {
		return this.getBlue( (float)v );
	}

	public float getBlue( float v ) {
		return (float)( Math.min( 1, Math.max( 0, -3 * v + 1.5 )));
	}

	public void setOutOfRangePaint( Paint p ) {
		this.outOfRangePaint = p;
	}

	public Paint getOutOfRangePaint( ) {
		return this.outOfRangePaint;
	}

}

