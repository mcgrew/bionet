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
package edu.purdue.jsysnet.util;

import java.util.List;

public class Polynomial {
	private NumberList coefficients;

	public Polynomial( ) {
		this.coefficients = new NumberList( );
	}

	public Polynomial( double x0 ) {
		this.coefficients = new NumberList( );
		this.coefficients.add( x0 );
	}

	public Polynomial( double x0, double x1 ) {
		this.coefficients = new NumberList( );
		this.coefficients.add( x0 );
		this.coefficients.add( x1 );
	}

	public Polynomial( double x0, double x1, double x2 ) {
		this.coefficients = new NumberList( );
		this.coefficients.add( x0 );
		this.coefficients.add( x1 );
		this.coefficients.add( x2 );
	}

	public Polynomial( double x0, double x1, double x2, double x3 ) {
		this.coefficients = new NumberList( );
		this.coefficients.add( x0 );
		this.coefficients.add( x1 );
		this.coefficients.add( x2 );
		this.coefficients.add( x3 );
	}

	public Polynomial( double [] coefficients ) {
		this.coefficients = new NumberList( coefficients );
	}

	public Polynomial( List<Number> coefficients ) {
		this.coefficients = new NumberList( coefficients );
	}

	public double solve( double x ) {
		double returnValue = 0.0;
		for ( int i=0; i < this.coefficients.size( ); i++ ) {
			returnValue += Math.pow( x, i ) * this.coefficients.get( i ).doubleValue( );
		}
		return returnValue;
	}

	public int getDegree( ) {
		return this.coefficients.size( );
	}

	public double getCoefficient( int power ) {
		return this.coefficients.get( power ).doubleValue( );
	}

	public Polynomial getDerivative( ) {
		NumberList newCoefficients = new NumberList( this.coefficients.size( ) - 1 );
		for( int i=1; i < this.coefficients.size( ); i++ ) {
			newCoefficients.add( this.coefficients.get( i ).doubleValue( ) * i );
		}
		return new Polynomial( newCoefficients );
	}

	public Polynomial getIntegral( ) {
		NumberList newCoefficients = new NumberList( this.coefficients.size( ) - 1 );
		newCoefficients.add( 0 );
		for( int i=0; i < this.coefficients.size( ); i++ ) {
			newCoefficients.add( this.coefficients.get( i ).doubleValue( ) / ( i+1 ));
		}
		return new Polynomial( newCoefficients );
	}

	public String toString( ) {
		return this.toString( "^%s" );
	}

	public String toString( String exponentFormat ) {
		StringBuilder returnValue = new StringBuilder( );
		for ( int i=this.coefficients.size( ) - 1; i >= 0; i-- ) {
			double coefficient =  this.coefficients.get( i ).doubleValue( );
			if ( returnValue.length( ) > 0 ) {
				if ( coefficient < 0 )
					returnValue.append( " - " );
				else
					returnValue.append( " + " );
				returnValue.append( Double.toString( Math.abs( coefficient )));
			} else {
				returnValue.append( Double.toString( coefficient ));
			}
			if ( i > 0 )
				returnValue.append( "x" );
			if ( i > 1 )
				returnValue.append( String.format( exponentFormat, i ));
		}
		return returnValue.toString( ).trim( );
	}
}

