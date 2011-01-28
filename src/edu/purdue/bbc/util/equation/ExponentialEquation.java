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
 * Represents an equation in the form a * e<sup>bx</sup>
 */
public class ExponentialEquation implements Equation {
	private double a;
	private double b;

	/**
	 * Creates a new exponential equation. (a * e<sup>bx</sup>)
	 * 
	 * @param a The value for a in the equation.
	 * @param b The vlaue for b in the equation.
	 */
	public ExponentialEquation( double a, double b ) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Gets the value of a, the overall multipler.
	 * 
	 * @return The value of a.
	 */
	public double getMultiplier( ) {
		return this.a;
	}

	/**
	 * Gets the value of b, the exponent multiplier.
	 * 
	 * @return The value of b.
	 */
	public double getXMultiplier( ) {
		return this.b;
	}

	/**
	 * Solves the equation for the given value of x.
	 * 
	 * @param x The value to solve the equation for.
	 * @return The solution to the equation.
	 */
	public double solve( double x ) {
		return this.a * Math.exp( this.b * x ); 
	}
	/**
	 * Finds the derivative of this equation on x.
	 * 
	 * @return A new Equation which is the derivative of this equation.
	 */
	public Equation derivative( ) {
		return new ExponentialEquation( this.a * this.b, this.b );
	}

	/**
	 * Finds the integral of this equation on x.
	 * 
	 * @param constant The constant to be added to the resulting equation.
	 * @return A new Equation which is the integral of this equation.
	 */
	public Equation integral( double constant ) {
		MultiPartEquation returnValue = new MultiPartEquation( );
		returnValue.add( new ExponentialEquation( this.a / this.b, this.b ));
		returnValue.add( new EquationTerm( constant, 0 ));
		return returnValue;
	}

	/**
	 * Returns a string representation of this equation.
	 * 
	 * @return A string representation of this equation.
	 */
	public String toString( ) {
		return this.toString( "^(%s)" ); 	
	}

	/**
	 * Returns a string representation of this equation, formatting the
	 * exponents with the passed in format (passed to String.format( )).
	 * 
	 * @param exponentFormat The format for any exponents.
	 * @return A String representation of the Equation.
	 */
	public String toString( String exponentFormat ) {
		return String.format( "%.3ge%s", this.a, 
			String.format( exponentFormat, 
				String.format( "%.3gx", this.b )));
	}
}

