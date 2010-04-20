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
package edu.purdue.jsysnet.util;

import java.util.List;
import java.util.Arrays;

/**
 * A class for connecting two Molecules and determining their correlation
 * coefficient.
 *
 * @author Thomas McGrew
 * @version 1.0
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
	public static double getPearsonCorrelation( List<Double> x, List<Double> y ) {
		if ( x.size( ) != y.size( ))
			return Double.NaN;

		double Sx=0, Sy=0, meanX=0, meanY=0, thisX, thisY, numerator=0;
		double sumX=0, sumY=0, sumXSq=0, sumYSq=0;

		for( Double currentX : x ){
			meanX += currentX.doubleValue( );
		}
		for ( Double currentY : y ) {
			meanY += currentY.doubleValue( );
		}
		int n = x.size( );
		meanX /= n;
		meanY /= n;
		for( int i=0; i < n; i++ ) {
			thisX = x.get( i ).doubleValue( );
			thisY = y.get( i ).doubleValue( );
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
	public static double getSpearmanCorrelation( List<Double> x, List<Double> y ){
		if ( x.size( ) != y.size( ) )
			return Double.NaN;

		int n = x.size( );
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
	public static double getKendallCorrelation( List<Double> x, List<Double> y ) {
		int n = x.size( );
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
	 * @param array The arrayList to get the rank of.
	 * @return	    An array containing the rank order of each element.
	 */
	public static double [] getRank( List<Double> list ) {
		int size = list.size( );
		double [] returnValue = new double[ size ];
		RankTriplet [] rankedArray = new RankTriplet[size];
		int i=0;
		for ( Double d : list ) {
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
	 * A class for holding a value along with it's original order and rank
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


}

