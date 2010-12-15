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

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class for holding multiple equations in a single construct. Performs all operations as if each Equation contained within were to be added together.
 */
public class MultiPartEquation extends ArrayList<Equation> implements Equation {

	/**
	 * Creates a new MultiPartEquation.
	 */
	public MultiPartEquation( ) {
		super( );
	}

	/**
	 * Creates a new MultiPartEquation.
	 * 
	 * @param c A collection of Equations to add to this MultiPartEquation.
	 */
	public MultiPartEquation( Collection <? extends Equation> c ) {
		super( c );
	}

	/**
	 * Solves the equation for the given x.
	 * 
	 * @param x The value of x to solve the equation for.
	 * @return The solution for the equation for the given x value.
	 */
	public double solve ( double x ) {
		double returnValue = 0.0;
		for ( Equation e : this ) {
			returnValue += e.solve( x );
		}
		return returnValue;
	}

	/**
	 * Finds the derivative of this equation on x. May throw an
	 * UnsupportedOperationException if any of the parts of this Equaiton do
	 * not support finding the derivative.
	 * 
	 * @return A new Equation which is the derivative of this Equation.
	 */
	public Equation derivative( ) {
		MultiPartEquation returnValue = new MultiPartEquation( );
		for( Equation e : this ) {
			Equation deriv = e.derivative( );
			if ( deriv != null )
				returnValue.add( deriv );
		}
		return returnValue;
	}

	/**
	 * Finds the integral of this equation on x. May throw an 
	 * UnsupportedOperationException if any of the parts of this Equaiton do
	 * not support finding the integral.
	 * 
	 * @param constant The constant value to be appended to this equation.
	 * @return A new Equation which is the integral of this equation.
	 */
	public Equation integral( double constant ) {
		MultiPartEquation returnValue = new MultiPartEquation( );
		for ( Equation e : this ) {
			returnValue.add( e.integral( 0 ));
		}
		if ( constant != 0.0 ) {
			returnValue.add( new EquationTerm( constant, 0 ));
		}
		return returnValue;
	}

	/**
	 * Returns a String representation of this Equation.
	 * 
	 * @return a String representation of this Equation.
	 */
	public String toString( ) {
		return this.toString( "^%s" );
	}

	/**
	 * Returns a String representation of this Equation.
	 * 
	 * @param exponentFormat The format string for exponent representation.
	 * @return a String representation of this Equation.
	 */
	public String toString( String exponentFormat ) {
		StringBuilder returnValue = new StringBuilder( );
		for ( Equation e : this ) {
			if ( returnValue.length( ) > 0 ) {
				returnValue.append( " + " );
			}
			returnValue.append( e.toString( exponentFormat ));
		}
		return returnValue.toString( );
	}

}

