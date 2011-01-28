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
package edu.purdue.bbc.util.equation;

/**
 * Implements a natural logarighmic equation: a * ln( b * x )
 */
public class LogEquation implements Equation {
	private double a;
	private double b;

	/**
	 * Creates a new instance of LogEquation with the given value for b and a=1.
	 */
	public LogEquation( double b ) {
		this( 1, b );
	}

	/**
	 * Creates a new instance of a LogEquation with the given values for a and b.
	 * 
	 * @param a The multiplier for the equation.
	 * @param b The value to multiply x by prior to taking the natural log.
	 */
	public LogEquation( double a, double b ) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Returns the value for a, the multiplier.
	 * 
	 * @return The value for a.
	 */
	public double getMultipler( ) {
		return this.a;
	}

	/**
	 * Returns the value for b.
	 * 
	 * @return The value for b.
	 */
	public double getXMultiplier( ) {
		return this.b;
	}

	/**
	 * Solves the equation for the given x.
	 * 
	 * @param x The value for x.
	 * @return The solution to the equation with the given x.
	 */
	public double solve( double x ) {
		if ( this.b * x < 0 ) {
			throw new IllegalArgumentException( "Unable to determine the log of a negative number (b*x = " + (this.b * x) + ")" );
		} else if ( this.b * x == 0 ) {
			return this.a * Math.log( Double.MIN_VALUE );
		}
		return this.a * Math.log( this.b * x );
	}

	/**
	 * Finds the derivative of this equation.
	 * 
	 * @return A new Equation which is the derivative of this one.
	 */
	public Equation derivative( ) {
		return new EquationTerm( this.a, -1 );
	}

	/**
	 * Find the integral of the equation. This operation is not currently
	 * supported on this type (throws UnsupportedOperationException)
	 * 
	 * @param constant The constant term to be added to the integral.
	 * @return A new Equation which is the integral of this one.
	 */
	public Equation integral( double constant ) {
		throw new UnsupportedOperationException( 
			"Finding the integral of a LogEquation is not supported at this time." );
	}

	/**
	 * Returns a String representation of this Equation.
	 * 
	 * @return A String representaion of this Equation.
	 */
	public String toString( ) {
		return this.toString( "^%s" );
	}

	/**
	 * Returns a String representation of this Equation.
	 * 
	 * @param exponentFormat Not used. This Equation format has no exponents.
	 * @return A String representation of this Equation.
	 */
	public String toString( String exponentFormat ) {
		return String.format( "%.3g * ln( %.3gx )", this.a, this.b );
	}
}


