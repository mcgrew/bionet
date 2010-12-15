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

/**
 * Represents a single term Equation of the form ax<sup>b</sup>.
 */
public class EquationTerm implements Equation {
	private double a;
	private double b;

	/**
	 * Creates a new EquationTerm with a=1 and the given value for b.
	 * 
	 * @param b The power to be used for x.
	 */
	public EquationTerm( double b ) {
		this( 1, b );
	}

	/**
	 * Creates a new EquationTerm with the given values for a and b.
	 * 
	 * @param a The multiplier for the EquationTerm.
	 * @param b The power for the EquationTerm.
	 */
	public EquationTerm( double a, double b ) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Soves the equation for the given value.
	 * 
	 * @param x The value to solve the equation for.
	 * @return The solution to the equation with the given value.
	 */
	public double solve( double x ) {
		return ( this.a * Math.pow( x, this.b ));
	}

	/**
	 * Gets the value for a, the multiplier.
	 * 
	 * @return The value for a.
	 */
	public double getMultiplier( ) {
		return this.a;
	}

	/**
	 * Gets the value for b, the power.
	 * 
	 * @return The value for b.
	 */
	public double getPower( ) {
		return this.b;
	}

	/**
	 * Returns the derivative of this equation on x.
	 * 
	 * @return A new Equation which is the derivative of this equation.
	 */
	public Equation derivative( ) {
		return new EquationTerm( this.a * this.b, this.b - 1 );
	}

	/**
	 * Returns the integral of this equation on x.
	 * 
	 * @param constant the constant value to be added to this equation.
	 * @return A new Equation which is the derivative of this equation.
	 */
	public Equation integral( double constant ) {
			Equation integral = 
				new EquationTerm( this.a / ( this.b + 1 ), this.b + 1 );
		if ( constant == 0 ) {
			return integral;
		} else {
			MultiPartEquation returnValue = new MultiPartEquation( );
			returnValue.add( integral );
			returnValue.add( new EquationTerm( constant, 0 ));
			return returnValue;
		}
	}

	public String toString( ) {
		return this.toString( "^%s" );
	}

	public String toString( String exponentFormat ) {
		if ( Double.compare( this.b, 0.0 ) == 0) {
			return String.format( "%.3g", this.a );
		} else if ( Double.compare( this.b, 1.0 ) == 0 ) {
			return String.format( "%.3gx", this.a );
		}
		return String.format( "%.3gx%s", this.a, 
			String.format( exponentFormat, 
				String.format( "%.3g", this.b )));
	}

}

