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

import java.util.Arrays;

/**
 * A class for connecting two Molecules and determining their correlation
 * coefficient.
 *
 * @author Thomas McGrew
 */
public class Statistics {

	/**
	 * Returns the Pearson correlation coefficient of the 2 sets of values.
	 * 
	 * <PRE>
	 *    x = sample values in molecule 0
	 *    y = sample values in molecule 1
	 *    n = number of samples in each molecule
	 *    Sx = standard deviation of x
	 *    Sy = standard deviation of y
	 * 
	 *                                               _              _
	 *                     sum( i=0 to n-1, ( x[i] - x ) * ( y[i] - y ))
	 * correlationValue = -----------------------------------------------
	 *                                ( n - 1 ) Sx * Sy
	 * 
	 * References:
	 * <CITE>Wikipedia, the free encyclopedia (2010, April 13).
	 *     <I>Correlation and Dependence</I>.
	 *     Retrieved April 14, 2010, from http://en.wikipedia.org/wiki/Correlation#Pearson.27s_product-moment_coefficient</CITE>
	 * </PRE>
	 *
	 * @param x The first set of values to use for the calculation.
	 * @param y The second set of values to use for the calculation.
	 * @return    The Pearson correlation value. 
	 */
	public static double getPearsonCorrelation( double[] x, double[] y ) {
		if ( x.length != y.length )
			return Double.NaN;

		double Sx=0, Sy=0, meanX=0, meanY=0, thisX, thisY, numerator=0;
		double sumX=0, sumY=0, sumXSq=0, sumYSq=0;

		for( double currentX : x ){
			meanX += currentX;
		}
		for ( double currentY : y ) {
			meanY += currentY;
		}
		int n = x.length;
		meanX /= n;
		meanY /= n;
		for( int i=0; i < n; i++ ) {
			thisX = x[ i ];
			thisY = y[ i ];
			numerator += ( thisX - meanX ) * ( thisY - meanY );
			sumX   += thisX;
			sumY   += thisY;
			sumXSq += thisX * thisX;
			sumYSq += thisY * thisY;
		}
		Sx = Math.sqrt(( sumXSq - sumX * sumX / n ) / (n-1));
		Sy = Math.sqrt(( sumYSq - sumY * sumY / n ) / (n-1));

		return ( numerator / (( n-1 ) * Sx * Sy ));

	}

	/**
	 * Returns the Spearman rank correlation coefficient of the 2 sets of data.
	 *
	 * <PRE>
	 * x = sample values in molecule 0
	 * y = sample values in molecule 1
	 * n = number of samples in each molecule
	 * Rx = rank array of x, ie. the new locations of each element of x if x were sorted
	 * Ry = rank array of y, ie. the new locations of each element of y if y were sorted
	 * 
	 *                         6 * sum( i=0 to n-1, ( Rx[i] - Ry[i] )^2 )
	 * correlationValue = 1 - --------------------------------------------
	 *                                       n * ( n^2 - 1 )
	 * 
	 * References: 
	 * <CITE>Wikipedia, the free encyclopedia (2010, April 1). 
	 *     <I>Spearman's rank correlation coefficient</I>. 
	 *     Retrieved April 14, 2010, from http://en.wikipedia.org/wiki/Spearman's_rank_correlation_coefficient</CITE>
	 * <CITE>Spiegel, Murray R. (1961). Statistics, 2nd Edition (pp 376,391-393). New York.</CITE>
	 * </PRE>
	 * 
	 * @param x The first set of data to use for the calculation.
	 * @param y The second set of data to use for the calculation.
	 * @return       The Spearman correlation value.
	 */
	public static double getSpearmanCorrelation( double[] x, double[] y ){
		if ( x.length != y.length )
			return Double.NaN;

		int n = x.length;
		double [] Rx = getRank( x );
		double [] Ry = getRank( y );

		double numerator=0;
		for ( int i=0; i < n; i++ ) {
			numerator += Math.pow( Rx[ i ] - Ry[ i ], 2 );
		}
		
		return 1 - ( 6 * numerator ) / ( n * ( n*n - 1 ));
	}

	/**
	 * Returns the Kendall tau rank correlation coefficient of the 2 sets of data.
	 * 
	 * <PRE>
	 * x = sample values of molecule 0
	 * y = sample values of molecule 1
	 * n = number of samples in each molecule
	 * Rx = the rank of the samples in molecule 0
	 * Ry = the rank of the samples in molecule 1
	 * Tx = the number of ties in x.
	 * Ty = the number of ties in y.
	 * concordant = The number of concordant pairs
	 * discordant = The number of discordant pairs
	 *
	 *                                 concordant - discordant
	 * correlationValue = ---------------------------------------------
	 *                              n*(n-1)            n*(n-1)         
	 *                     sqrt( ( ------- - Tx ) * (  ------- - Ty ) )
	 *                                2                   2            
	 *
	 * <CITE>Wikipedia, the free encyclopedia (2010, March 25). 
	 *     <I>Kendall tau rank correlation coefficient</I>. 
	 *     Retrieved April 14, 2010, from http://en.wikipedia.org/wiki/Kendall_tau_rank_correlation_coefficient</CITE>
	 * </PRE>
	 *
	 * @param x The first set of data to use for the calculation.
	 * @param y The second set of data to use for the calculation.
	 * @return The Kendall tau correlation value.
	 */
	public static double getKendallCorrelation( double[] x, double[] y ) {
		if ( x.length != y.length )
			return Double.NaN;

		int n = x.length;
		double [] Rx = getRank( x );
		double [] Ry = getRank( y );

		// Count the number of concordant and discordant pairs, and ties.
		int concordant = 0, discordant = 0, xTies=0, yTies=0, xRel, yRel;
		for ( int i=0; i < n-1; i++ ) {
			for ( int j=i+1; j < n; j++ ) {
				xRel = (int)Math.signum( Rx[ j ] - Rx[ i ] );
				yRel = (int)Math.signum( Ry[ j ] - Ry[ i ] );
				if ( xRel == 0 || yRel == 0 ) {	// check for ties first
					if ( xRel == 0 )
						xTies++;
					if( yRel == 0 )
						yTies++;
				}
				else if ( xRel ==  yRel ) {
					concordant++;
				}
			
				else if ( xRel == -yRel ) {
					discordant++;
				}
			}
		}
		double denominator = ( n * ( n-1 ) / 2.0 );
		return ( concordant - discordant ) / Math.sqrt(( denominator - xTies ) * ( denominator - yTies ));
			
	}

	/** 
	 * Finds the rank of each element in a List.
	 *	
	 * @param list The arrayList to get the rank of.
	 * @return An array containing the rank order of each element.
	 */
	public static double [] getRank( double[] list ) {
		int size = list.length;
		double [] returnValue = new double[ size ];
		RankTriplet [] rankedArray = new RankTriplet[size];
		int i=0;
		for ( double d : list ) {
			rankedArray[ i ] = new RankTriplet( d, i++ );
		}
		Arrays.sort( rankedArray );

		i=0;
		for ( RankTriplet t : rankedArray ){
			t.rank = i++;
		}

		// look for tied ranks.
		int lastDiff = 0;
		for ( i=0; i < size; i++ ) {
			if ( rankedArray[ i ].compareTo( rankedArray[ lastDiff ] ) != 0 ) {
				if ( i - lastDiff > 1 ) {
					double newRank = lastDiff + (((i-1) - lastDiff ) / 2.0 );
					for( int j=lastDiff; j < i; j++ ) {
						rankedArray[ j ].rank = newRank;
					}
				}
				lastDiff = i;
			}
		}

		for ( RankTriplet t : rankedArray ) {
			returnValue[ t.order ] = t.rank;
	 }
	 return returnValue;
	}

	/**
	 * A class for holding a value along with its original order and rank
	 */
	private static class RankTriplet implements Comparable<RankTriplet> {
		public double value;
		public int order;
		public double rank;

		public RankTriplet ( double value ) {
			this( value, 0, 0.0 );
		}

		public RankTriplet ( double value, int order ) {
			this( value, order, 0.0 );
		}

		public RankTriplet ( double value, int order, double rank ) {
			this.value = value;
			this.order = order;
			this.rank = rank;
		}

		public boolean equals( RankTriplet p ) {
			return this.compareTo( p ) == 0;
		}

		public int compareTo( RankTriplet p ) {
			return (int)Math.signum( this.value - p.value );
		}

		public String toString ( ) {
			return "< "+ this.value +", "+ this.order +", "+ this.rank +" >";
		}
	}

	/**
	 * Returns the standard deviation of a set of values.
	 * 
	 * @param values The set to find the standard deviation of.
	 * @return The standard deviation as a double.
	 */
	public static double standardDeviation( double [] values ) {
		double sum = 0.0, sumSq = 0.0;
		for ( double d : values ) {
			sum += d;
			sumSq += d*d;
		}
		return Math.sqrt((( sumSq - sum * sum ) / values.length ) / ( values.length - 1 ));
	}

	/**
	 * Finds the median of a set of values.
	 * 
	 * @param values The values to find the median of.
	 * @return The median of those values.
	 */
	public static double median( double [] values ) {
		double [] sorted = Arrays.copyOf( values, values.length );
		Arrays.sort( sorted );
		if (( values.length & 1 ) == 0 ) {
			return ( values[ values.length / 2 ] + values[ values.length / 2 + 1 ]) / 2;
		} else {
			return values[ values.length / 2 ];
		}
	}

	/**
	 * Finds the mean of a set of values.
	 * 
	 * @param values The set of values.
	 * @return The mean of those values.
	 */
	public static double mean( double [] values ) {
		double sum = 0.0;
		for ( double d : values ) {
			sum += d;
		}
		return sum / values.length;
	}

	/**
	 * Finds the sum of a set of values.
	 * 
	 * @param values The set of values.
	 * @return The sum of those values.
	 */
	public static double sum( double [] values ) {
		double sum = 0.0;
		for ( double d : values ) {
			sum += d;
		}
		return sum;
	}

	/**
	 * Returns the minimum of a set of values.
	 * 
	 * @param values The set of values to find the minimum for.
	 * @return The minimum value in the array.
	 */
	public static double min( double [] values ) {
		double returnValue = Double.MAX_VALUE;
		for ( double d : values ) {
			if ( Double.compare( returnValue, d ) < 0 )
				returnValue = d;
		}
		return returnValue;
	}

	/**
	 * Returns the maximum of a set of values.
	 * 
	 * @param values the set of values to find the maximum for.
	 * @return The maximum value in the array.
	 */
	public static double max( double [] values ) {
		double returnValue = Double.MIN_VALUE;
		for ( double d : values ) {
			if ( Double.compare( returnValue, d ) > 0 )
				returnValue = d;
		}
		return returnValue;
	}

	/**
	 * Reduces a set of values to a linear regression, using the array indices
	 * as the x values.
	 *
	 * References:
	 * <CITE> Fry, John M. (2010). Regression: Linear Models in Statistics
	 * (pp 3-6) Springer.</CITE>
	 * 
	 * @param values The y values of the points on a graph.
	 * @return A Polynomial object which can be used to find the regression line.
	 */
	public static Polynomial linearRegression( double [] values ) {
		double meanX = 0.0;
		double meanY = 0.0;
		double meanXY = 0;
		double meanXsq = 0.0;
		int NaNCount = 0;
		for( int x=0; x < values.length; x++ ) {
			if ( !Double.isNaN( values[ x ])) {
				meanXY += x * values[ x ];
				meanY  += values[ x ];
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

	public static Polynomial chiSquareFit( double [] values ) {
		return null;
	}
}

