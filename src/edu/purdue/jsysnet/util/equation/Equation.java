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
 * An interface for a solvable single-variable equation.
 */
public interface Equation {

	/**
	 * Solves the equation for the given value of x.
	 * 
	 * @param x The value to solve the equation for.
	 * @return The solution to the equation with the given value.
	 */
	public double solve( double x );

	/**
	 * Finds the derivative of the equation and returns it. This
	 * method may throw an UnsupportedOperationException if the
	 * operation is not supported by the implementing class.
	 *
	 * @return A new Equation which is the derivative of this equation.
	 */
	public Equation derivative( );

	/**
	 * Finds the integral of the equation and returns it. This
	 * method may throw an UnsupportedOperationException if the
	 * operation is not supported by the implementing class.
	 *
	 * @param constant A constant to be added to the integral of this
	 * equation.
	 * @return A new Equation which is the integral of this equation.
	 */
	public Equation integral( double constant );
	
	/**
	 * Returns a String representation of this Equation with the given 
	 * exponentFormat.
	 * 
	 * @param exponentFormat The format string to use for the exponent.
	 * @return A String representation of this Equation.
	 */
	public String toString( String exponentFormat );
}

