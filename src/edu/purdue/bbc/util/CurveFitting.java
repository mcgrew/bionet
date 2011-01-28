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
package edu.purdue.bbc.util;

import edu.purdue.bbc.util.equation.*;

import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * A class for curve fitting calculations.
 *
 * @author Thomas McGrew
 */
public class CurveFitting {

	/**
	 * Reduces a set of values to a linear regression, using the array indices
	 * as the x values. Finds an equation of the form y = ax + b which best matches
	 * the passed in data.
	 *
	 * References:
	 * <CITE> Fry, John M. (2010). Regression: Linear Models in Statistics
	 * (pp 3-6) Springer.</CITE>
	 * 
	 * @param values The y values of the points on a graph.
	 * @return A Polynomial object which can be used to find the regression line.
	 */
	public static Polynomial linearFit( double [] values ) {
		double meanX = 0.0;
		double meanY = 0.0;
		double meanXY = 0;
		double meanXsq = 0.0;
		int NaNCount = 0;
		for( int x=0; x < values.length; x++ ) {
			double y = values[ x ];
			if ( !Double.isNaN( y ) && 
				   Double.compare( y, 0.0 ) != 0 ) {
				meanXY += x * y;
				meanY  += y;
				meanXsq += x * x;
				meanX += x;
			} else {
				NaNCount++;
			}
		}
		meanXY /= values.length - NaNCount;
		meanY /= values.length - NaNCount;
		meanXsq /= values.length - NaNCount;
		meanX /= values.length - NaNCount;
		double b = ( meanXY - ( meanX * meanY )) / 
			( meanXsq - ( meanX * meanX ));
		double a = meanY - b * meanX;
		return new Polynomial( a, b );
	}

	/**
	 * Finds the power law curve fit for a set of data.
	 *
	 * <CITE>Weisstein, Eric W. "Least Squares Fitting--Power Law." From <I>MathWorld</I>--A Wolfram Web Resource.
	 *      Retrieved Dec 13, 2010, http://mathworld.wolfram.com/LeastSquaresFittingPowerLaw.html</CITE>
	 * 
	 * @param values The series of values to determine the curve for.
	 * @return The determined equation for the curve through these points.
	 */
	public static Equation powerLawFit( double [] values ) {
		double sumLogX = 0.0, sumLogX2 = 0.0, sumLogY = 0.0, sumLogXLogY = 0.0;
		int n=0;
		for ( int x=0; x < values.length; x++ ) {
			double y = values[ x ];
			if ( !Double.isNaN( y )) {
				n++;
				sumLogX += Math.log( x );
				sumLogY += Math.log( y );
				sumLogX2 += Math.pow( Math.log( x ), 2 );
				sumLogXLogY += Math.log( x ) * Math.log( y );
			}
		}
		double b = ( n * sumLogXLogY - sumLogX * sumLogY ) / 
		           ( n * sumLogX2 - sumLogX * sumLogX );
		double a = Math.exp(( sumLogY - b * sumLogX ) / n );
		return new EquationTerm( a, b );
	}

	public static Polynomial quadraticFit( double [] values ) {
		return null;
	}

	/**
	 * Determine an exponential curve fit for the data.
	 *
	 * <CITE>Curve Fitting (2010, Dec 10).
	 *      <I>Exponential Curve Fit</I>
	 *      Retrieved Dec 10, 2010, from http://www.engr.uidaho.edu/thompson/courses/ME330/lecture/least_squares.html</CITE>
	 * 
	 * @param values The series of values to determine the curve for.
	 * @return The determined equation for the curve through these points.
	 */
	public static Equation exponentialFit ( double [] values ) {
		double sumXLogY = 0.0, sumLogY = 0.0, sumX = 0.0;
		double sumX2 = 0.0;
		int n = 0;
		for ( int x=0; x < values.length; x++ ) {
			double y = values[ x ];
			if ( !Double.isNaN( y ) && 
				   Double.compare( y, 0.0 ) != 0 ) {
				n++;
				sumXLogY += x * Math.log( y );
				sumX += x;
				sumLogY += Math.log( y );
				sumX2 += x * x;
			}
		}
		double b = ( sumXLogY - ( sumX * sumLogY / n )) /
		           ( sumX2 - sumX * sumX / n );
		double a = Math.exp( sumLogY / n ) - b * ( sumX / n );
		return new ExponentialEquation( a, b );
	}

	/**
	 * Finds the logarithmic curve fit for a set of data.
	 *
	 * <CITE>Weisstein, Eric W. "Least Squares Fitting--Logarithmic." From <I>MathWorld</I>--A Wolfram Web Resource.
	 *      Retrieved Dec 13, 2010, http://mathworld.wolfram.com/LeastSquaresFittingLogarithmic.html</CITE>
	 * 
	 * @param values The series of values to determine the curve for.
	 * @return The determined equation for the curve through these points.
	 */
	public static Equation logFit( double [] values ) {
		Logger logger = Logger.getLogger( Statistics.class );
		double sumYLogX = 0.0, sumY = 0.0, sumX = 0.0;
		double sumLogX2 = 0.0, sumLogX = 0.0, sumLogY = 0.0;
		int n=0;
		for ( int x=0; x < values.length; x++ ) {
			double y = values[ x ];
			if ( !Double.isNaN( y ) && 
				   x != 0 &&
				   Double.compare( y, 0.0 ) != 0 ) {
				n++;
				sumY += y;
				sumLogX += Math.log( x );
				sumLogY += Math.log( y );
				sumLogX2 += Math.pow( Math.log( x ), 2 );
				sumYLogX += Math.log( x ) * y;
			}
		}
		double b = Math.abs(( n * sumYLogX - sumY * sumLogX ) /
		           ( n * sumLogX2 - sumLogX * sumLogX ));
		double a = ( sumY - b * sumLogX ) / n;
		MultiPartEquation returnValue = new MultiPartEquation( );
		returnValue.add( new LogEquation( b ));
		returnValue.add( new EquationTerm( a, 0 ));
		return returnValue;
	}

	/**
	 * Finds the best curve fitting equation for a set of data.
	 *
	 * <CITE>Chi-square Goodness of Fit Test, (1997, Nov 17).
	 *      <I>Exponential Curve Fit</I>
	 *      Retrieved Dec 14, 2010, from http://www.stat.yale.edu/Courses/1997-98/101/chigf.htm</CITE>
	 * 
	 * 
	 * @param values The values to choose a fitting equation for.
	 * @return The best fitting equation found.
	 */
	public static Equation chiSquareFit( double [] values ) {
		Equation returnValue = linearFit( values );
		double chi2 = getChiSquare( values, returnValue );

		Equation current = powerLawFit( values );
		double currentChi2 = getChiSquare( values, current );
		if ( Double.compare( currentChi2, chi2 ) < 0 ) {
			returnValue = current;
			chi2 = currentChi2;
		}

		current = logFit( values );
		currentChi2 = getChiSquare( values, current );
		if ( Double.compare( currentChi2, chi2 ) < 0 ) {
			returnValue = current;
			chi2 = currentChi2;
		}

		current = exponentialFit( values );
		currentChi2 = getChiSquare( values, current );
		if ( Double.compare( currentChi2, chi2 ) < 0 ) {
			returnValue = current;
			chi2 = currentChi2;
		}
		return returnValue;
	}

	public static double getChiSquare( double [] values, Equation equation ) {
		double returnValue = 0.0;
		double expected, actual;
		for( int x=0; x < values.length; x++ ) {
			try {
				expected = equation.solve( x );
			} catch ( IllegalArgumentException e ) {
				Logger.getLogger( Statistics.class ).debug( e );
				continue;
			}
			actual = values[ x ];
			if ( !Double.isNaN( actual ) && !Double.isNaN( expected ))
			returnValue += Math.pow( actual - expected, 2 )  / Math.abs( expected );
		}
		return returnValue;
	}
}

