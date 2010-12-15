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
package edu.purdue.jsysnet.util.equation;

import edu.purdue.jsysnet.util.NumberList;

import java.util.List;

/**
 * Implements a Polynomial Equation.
 */
public class Polynomial implements Equation {
	private NumberList coefficients;

	/**
	 * Creates a new Polynomial of degree 0, a.
	 * 
	 * @param x0 The constant term.
	 */
	public Polynomial( double x0 ) {
		this.coefficients = new NumberList( );
		this.coefficients.add( x0 );
	}

	/**
	 * Creates a new Polynomial of degree 1, ax<sup>1</sup> + b.
	 * 
	 * @param x0 The constant coefficient.
	 * @param x1 The x coefficient.
	 */
	public Polynomial( double x0, double x1 ) {
		this.coefficients = new NumberList( );
		this.coefficients.add( x0 );
		this.coefficients.add( x1 );
	}

	/**
	 * Creates a new Polynomial of degree 2, ax<sup>2</sup> + bx + c.
	 * 
	 * @param x0 The constant coefficient.
	 * @param x1 The x coefficient.
	 * @param x2 The x<sup>2</sup> coefficient.
	 */
	public Polynomial( double x0, double x1, double x2 ) {
		this.coefficients = new NumberList( );
		this.coefficients.add( x0 );
		this.coefficients.add( x1 );
		this.coefficients.add( x2 );
	}

	/**
	 * Creates a new Polynomial of degree 3, ax<sup>3</sup> + bx<sup>2</sup> + cx + d.
	 * 
	 * @param x0 The constant coefficient.
	 * @param x1 The x coefficient.
	 * @param x2 The x<sup>2</sup> coefficient.
	 * @param x3 The x<sup>3</sup> coefficient.
	 */
	public Polynomial( double x0, double x1, double x2, double x3 ) {
		this.coefficients = new NumberList( );
		this.coefficients.add( x0 );
		this.coefficients.add( x1 );
		this.coefficients.add( x2 );
		this.coefficients.add( x3 );
	}

	/**
	 * Creates a new Polynomial with the given coefficients.
	 * 
	 * @param coefficients The coefficients for the powers of x, in order from 0 to n.
	 */
	public Polynomial( double [] coefficients ) {
		this.coefficients = new NumberList( coefficients );
	}

	/**
	 * Creates a new Polynomial with the given goefficients
	 * 
	 * @param coefficients The coefficients for the powers of x, in order from 0 to n.
	 */
	public Polynomial( List<Number> coefficients ) {
		this.coefficients = new NumberList( coefficients );
	}

	/**
	 * Solves the equation for the given x.
	 * 
	 * @param x The value of x to find the solution for.
	 * @return The solution for the equation.
	 */
	public double solve( double x ) {
		double returnValue = 0.0;
		for ( int i=0; i < this.coefficients.size( ); i++ ) {
			returnValue += Math.pow( x, i ) * this.coefficients.get( i ).doubleValue( );
		}
		return returnValue;
	}

	/**
	 * Returns the degree of the Polynomial.
	 * 
	 * @return The degree of the Polynomial.
	 */
	public int getDegree( ) {
		return this.coefficients.size( ) - 1;
	}

	/**
	 * returns the coefficient of the requested x power.
	 * 
	 * @param power The power to return the coefficient of.
	 * @return The coefficient.
	 */
	public double getCoefficient( int power ) {
		return this.coefficients.get( power ).doubleValue( );
	}

	/**
	 * Finds the derivative of this Polynomial on x.
	 * 
	 * @return A new Polynomial which is the dervative of this Polynomial.
	 */
	public Polynomial derivative( ) {
		NumberList newCoefficients = new NumberList( this.coefficients.size( ) - 1 );
		for( int i=1; i < this.coefficients.size( ); i++ ) {
			newCoefficients.add( this.coefficients.get( i ).doubleValue( ) * i );
		}
		return new Polynomial( newCoefficients );
	}

	/**
	 * Finds the integral of this Polynomial on x.
	 * 
	 * @param constant The constant to be added to the end of the new Polynomial.
	 * @return A new Polynomial which is the integral of this Polynomial.
	 */
	public Polynomial integral( double constant ) {
		NumberList newCoefficients = new NumberList( this.coefficients.size( ) - 1 );
		newCoefficients.add( constant );
		for( int i=0; i < this.coefficients.size( ); i++ ) {
			newCoefficients.add( this.coefficients.get( i ).doubleValue( ) / ( i+1 ));
		}
		return new Polynomial( newCoefficients );
	}

	/**
	 * Returns a String representation of this Polynomial.
	 *
	 * @return A string representation of this equation.
	 */
	public String toString( ) {
		return this.toString( "^%s" );
	}

	/**
	 * Returns a String representation of this Polynomial.
	 * 
	 * @param exponentFormat The format string to be passed to String.format( ) for formatting of exponents.
	 * @return A string representation of this equation.
	 */
	public String toString( String exponentFormat ) {
		StringBuilder returnValue = new StringBuilder( );
		for ( int i=this.coefficients.size( ) - 1; i >= 0; i-- ) {
			double coefficient =  this.coefficients.get( i ).doubleValue( );
			if ( returnValue.length( ) > 0 ) {
				if ( coefficient < 0 )
					returnValue.append( " - " );
				else
					returnValue.append( " + " );
				returnValue.append( String.format( "%.3g", Math.abs( coefficient )));
			} else {
				returnValue.append( String.format( "%.3g", coefficient ));
			}
			if ( i > 0 )
				returnValue.append( "x" );
			if ( i > 1 )
				returnValue.append( String.format( exponentFormat, 
					String.format( "%.3g", i )));
		}
		return returnValue.toString( ).trim( );
	}
}

