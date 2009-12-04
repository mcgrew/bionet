import java.lang.Math;
import java.lang.Double;
import java.util.ArrayList;
import java.util.Arrays;

public class Correlation {

	public static final int PEARSON = 0;
	public static final int SPEARMAN = 1;
	public static final int KENDALL = 2;
	private static int lastMethod = PEARSON;
	private double pearsonCorrelation = Double.NaN;
	private double spearmanCorrelation = Double.NaN;
	private double kendallCorrelation = Double.NaN;
	private Molecule [ ] molecules = new Molecule[ 2 ];
	
	public Correlation( Molecule molecule1, Molecule molecule2 ) {
		this.molecules[ 0 ] = molecule1;
		this.molecules[ 1 ] = molecule2;
		this.molecules[ 0 ].addCorrelation( this );
		this.molecules[ 1 ].addCorrelation( this );
	}

	public Correlation( Molecule [] molecules ){
		this.molecules[ 0 ] = molecules[ 0 ];
		this.molecules[ 1 ] = molecules[ 1 ];
		this.molecules[ 0 ].addCorrelation( this );
		this.molecules[ 1 ].addCorrelation( this );
	}

	public Molecule [ ] getMolecules( ) {
		return this.molecules;
	}

	public boolean hasMolecule( Molecule molecule ) {
		return ( molecule == this.molecules[ 0 ] ||
		         molecule == this.molecules[ 1 ] );
	}

	public double getValue( ) {
		return this.getValue( lastMethod );
	}
	public double getValue( int method ) {
		
		switch ( method ) {
			case PEARSON:
				return this.getPearsonCorrelation( );

			case SPEARMAN:
				return this.getSpearmanCorrelation( );

			case KENDALL:
				return this.getKendallCorrelation( );

			default:
				return -1;
		}
	}

	/**
	 * Returns the Pearson correlation coefficient of the 2 molecueles.
	 * 
	 *    x = sample values in molecule 0
	 *    y = sample values in molecule 1
	 *    n = number of samples in each molecule
	 *    Sx = standard deviation of x
	 *    Sy = standard deviation of y
	 *
	 *                     sum( i=0 to n-1, ( x[i] - x ) * ( y[i] - y ))
	 * correlationValue = -----------------------------------------------
	 *                                ( n - 1 ) Sx * Sy
	 *
	 * Adapted from	http://en.wikipedia.org/wiki/Correlation#Pearson.27s_product-moment_coefficient
	 *
	 * @return    The Pearson correlation value. 
	 */
	private double getPearsonCorrelation( ) {
	 return this.getPearsonCorrelation( false );
	}

	/**
	 * Returns the Pearson correlation coefficient of the 2 molecueles.
	 * 
	 *    x = sample values in molecule 0
	 *    y = sample values in molecule 1
	 *    n = number of samples in each molecule
	 *    Sx = standard deviation of x
	 *    Sy = standard deviation of y
	 *
	 *                     sum( i=0 to n-1, ( x[i] - x ) * ( y[i] - y ))
	 * correlationValue = -----------------------------------------------
	 *                                ( n - 1 ) Sx * Sy
	 * 
	 * Adapted from	http://en.wikipedia.org/wiki/Correlation#Pearson.27s_product-moment_coefficient
	 *
	 * @param recalc True if you want the value recalculated, otherwise a cached value will be returned if present.
	 * @return       The Pearson correlation value.
	 */
	private double getPearsonCorrelation( boolean recalc ) {
		this.lastMethod = PEARSON;
		//See if this value has already been calculated
		if ( recalc || Double.isNaN( pearsonCorrelation )) { 
			int S = 1, n = 0;
			String currentXString, currentYString;
			Double currentX, currentY;
			double Sx=0, Sy=0, meanX=0, meanY=0, numerator=0, denominator=0;
			ArrayList <Double> x = new ArrayList <Double>( );
			ArrayList <Double> y = new ArrayList <Double>( );

			// Get the sample data from the molecules.
			while ((( currentXString = this.molecules[0].getAttribute( "S" + S )) != null )
							&& (( currentYString = this.molecules[1].getAttribute( "S" + S++ )) != null )) {
				currentX = new Double( currentXString );
				currentY = new Double( currentYString );
				x.add( currentX );
				y.add( currentY );
				meanX += currentX.doubleValue( );
				meanY += currentY.doubleValue( );
			}
			n = x.size( );
			meanX /= n;
			meanY /= n;
			double thisX, thisY;
			for( int i=0; i < n; i++ ) {
					thisX = x.get( i ).doubleValue( ) - meanX;
					thisY = y.get( i ).doubleValue( ) - meanY;
					numerator += ( thisX ) * ( thisY );
					Sx += thisX * thisX;
					Sy += thisY * thisY;
			}
			Sx = Math.sqrt( Sx / n );
			Sy = Math.sqrt( Sy / n );

			this.pearsonCorrelation = ( numerator / (( n-1 ) * Sx * Sy ));
			if ( this.pearsonCorrelation > 1 || this.pearsonCorrelation < -1 )
				meanY = 0;
		}

		return this.pearsonCorrelation;

	}

	/**
	 * Returns the Spearman rank correlation coefficient of the 2 molecules.
	 *
	 * x = sample values in molecule 0
	 * y = sample values in molecule 1
	 * n = number of samples in each molecule
	 * Rx = rank array of x, ie. the new locations of each element of x if x were sorted (starting at 1).
	 * Ry = rank array of y, ie. the new locations of each element of y if y were sorted ( starting at 1)
	 * 
	 *                         6 * sum( i=0 to n-1, ( Rx[i] - Ry[i] )^2 )
	 * correlationValue = 1 - --------------------------------------------
	 *                                       n * ( n^2 - 1 )
	 * 
	 * Adapted from http://en.wikipedia.org/wiki/Spearman_correlation
	 * 
	 * @return       The Spearman correlation value.
	 */
	private double getSpearmanCorrelation( ) {
		return this.getSpearmanCorrelation( false );
	}

	/**
	 * Returns the Spearman rank correlation coefficient of the 2 molecules.
	 *
	 * x = sample values in molecule 0
	 * y = sample values in molecule 1
	 * n = number of samples in each molecule
	 * Rx = rank array of x, ie. the new locations of each element of x if x were sorted (starting at 1).
	 * Ry = rank array of y, ie. the new locations of each element of y if y were sorted ( starting at 1)
	 * 
	 *                         6 * sum( i=0 to n-1, ( Rx[i] - Ry[i] )^2 )
	 * correlationValue = 1 - --------------------------------------------
	 *                                       n * ( n^2 - 1 )
	 * 
	 * Adapted from http://en.wikipedia.org/wiki/Spearman_correlation
	 * 
	 * @param recalc True if you want the value recalculated, otherwise a cached value will be returned if present.
	 * @return       The Spearman correlation value.
	 */
	private double getSpearmanCorrelation( boolean recalc ) {
		this.lastMethod = SPEARMAN;
		//See if this value has already been calculated
		if ( recalc || Double.isNaN( this.spearmanCorrelation ) ) { 
			
			int S = 1, n = 0;
			String currentXString, currentYString;
			ArrayList <Double> ValueListX = new ArrayList <Double>( ),
			                   ValueListY = new ArrayList <Double>( );
			double [ ] x,y;
			int[ ] Rx, Ry;
			double mean, numerator=0;

			// Get the sample data from the molecules.
			while ((( currentXString = this.molecules[0].getAttribute( "S" + S )) != null )
							&& (( currentYString = this.molecules[1].getAttribute( "S" + S++ )) != null )) {
					ValueListX.add( new Double( currentXString ));
					ValueListY.add( new Double( currentYString ));
			}
			n = ValueListY.size( );
			x = new double[ n ];
			y = new double[ n ];

			// Convert and make 2 copies of the sample data.
			for ( int i=0; i < n; i++ ) {
				x[ i ] = ValueListX.get( i ).doubleValue( );
				y[ i ] = ValueListY.get( i ).doubleValue( );
			}
			Rx = getRank( x );
			Ry = getRank( y );

			for ( int i=0; i < n; i++ ) {
				numerator += Math.pow( Rx[ i ] - Ry[ i ], 2 );
			}
			numerator *= 6;

			this.spearmanCorrelation = 1 - ( numerator ) / ( n * ( n*n - 1 ));
		}
		return this.spearmanCorrelation;
	}

	private double getKendallCorrelation( ) {
		return this.getKendallCorrelation( false );
	}

	/**
	 * Returns the Kendall tau rank correlation coefficient of the 2 molecules.
	 * 
	 * x = sample values of molecule 0
	 * y = sample values of molecule 1
	 * n = number of samples in each molecule
	 * Rx = the rank of the samples in molecule 0
	 * Ry = the rank of the samples in molecule 1
	 * concordant = The number of concordant pairs
	 * discordant = The number of discordant pairs
	 *
	 *                       concordant - discordant
	 * correlationValue = -----------------------------
	 *                     (1/2) * pairs * ( pairs-1 )
	 * @param 
	 * @return The Kendall tau correlation value.
	 */
	private double getKendallCorrelation( boolean recalc ) {
		this.lastMethod = KENDALL;
		if ( recalc || Double.isNaN( this.kendallCorrelation )) { 			
			int S = 1, n = 0;
			String currentXString, currentYString;
			ArrayList <Double> ValueListX = new ArrayList <Double>( ),
			                   ValueListY = new ArrayList <Double>( );
			double [ ] x,y;
			int[ ] Rx, Ry;
			double mean, numerator=0;

			// Get the sample data from the molecules.
			while ((( currentXString = this.molecules[0].getAttribute( "S" + S )) != null )
							&& (( currentYString = this.molecules[1].getAttribute( "S" + S++ )) != null )) {
					ValueListX.add( new Double( currentXString ));
					ValueListY.add( new Double( currentYString ));
			}
			n = ValueListX.size( );
			x = new double[ n ];
			y = new double[ n ];

			// Convert and make 2 copies of the sample data.
			for ( int i=0; i < n; i++ ) {
				x[ i ] = ValueListX.get( i ).doubleValue( );
				y[ i ] = ValueListY.get( i ).doubleValue( );
			}
			Rx = getRank( x );
			Ry = getRank( y );

			// Count the number of concordant and discordant pairs.
			int concordant = 0, discordant = 0;
			for ( int i=0; i < n-1; i++ ) {
				for ( int j=i+1; j < n; j++ ) {
					if ( Math.signum( x[ i ] - x[ j ] ) == 
							 Math.signum( y[ i ] - y[ j ] )) {
						concordant++;
					}
				
					else if ( Math.signum( x[ i ] - x[ j ] ) == 
									 -Math.signum( y[ i ] - y[ j ] )) {
						discordant++;
					}
				}
			}

			this.kendallCorrelation = 1 - 2 * ( concordant - discordant ) / ( n * ( n-1 ));
			
		}

		return this.kendallCorrelation;
	}

	/** 
	 * Returns the index of the specified value of the array. Returns -1 if
	 * the value is not found in the array.
	 * 
	 * @param array The array to be searched
	 * @param value The value to be located in the array.
	 * @return      The index of the value in the array.
	 */
	private static int indexOf( int[ ] array, int value ) {
		for( int i=0,l=array.length; i < l; i++ ) {
				if ( array[ i ] == value ) {
					return i;
				}
		}
		return -1;
	}

	/** 
	 * Returns the index of the specified value of the array. Returns -1 if
	 * the value is not found in the array.
	 * 
	 * @param array The array to be searched
	 * @param value The value to be located in the array.
	 * @return      The index of the value in the array.
	 */
	private static int indexOf( double[ ] array, double value ) {
		for( int i=0,l=array.length; i < l; i++ ) {
				if ( Double.compare( array[ i ], value ) == 0  ) {
					return i;
				}
		}
		return -1;
	}

	/** 
	 * Returns the rank of each element, ie the new location of each element
	 * in the array if the array were sorted. Based on selection sort.
	 *	
	 * @param array The array to get the rank of.
	 * @return	    An array containing the rank order of each element.
	 */
	private static int [] getRank( double[ ] array ) {
		int arrayLen=array.length;
		int [] returnValue = new int[ arrayLen ];
		double [] copy = Arrays.copyOf( array, arrayLen );
		Arrays.sort( copy );

		for ( int i=0; i < arrayLen; i++ ) {
			returnValue[ i ] = indexOf( copy, array[ i] );
		}
		
		return returnValue;
	}
}

