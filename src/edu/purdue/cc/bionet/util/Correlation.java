/*

This file is part of BioNet.

BioNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BioNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with BioNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.bionet.util;

import edu.purdue.bbc.util.NumberList;
import edu.purdue.bbc.util.Statistics;
import edu.purdue.bbc.util.SimplePair;

import java.lang.Double;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A class for connecting two Molecules and determining their correlation
 * coefficient.
 *
 * @author Thomas McGrew
 */
public class Correlation extends SimplePair<Molecule> 
                         implements Comparable<Correlation> {

	public static final int PEARSON = 0;
	public static final int SPEARMAN = 1;
	public static final int KENDALL = 2;
	public static final String [] NAME =
		{ "Pearson R", "Spearman Rho", "Kendall Tau" };
	protected double pearsonCorrelation = Double.NaN;
	protected double spearmanCorrelation = Double.NaN;
	protected double kendallCorrelation = Double.NaN;
	private Collection<Sample> samples;
	@Deprecated
	protected Experiment experiment;
	private int sampleSize = 0;

	/**
	 * Constructor.
	 * 
	 * @param molecule1 The first Molecule of this Correlation.
	 * @param molecule2 The second Molecule of this Correlation.
	 * @param experiment The experiment this Correlation is associated with.
	 */
	@Deprecated
	public Correlation( Molecule molecule1, Molecule molecule2, 
	                    Experiment experiment ) {
		super( molecule1, molecule2 );
		this.samples = experiment.getSamples( );
		this.experiment = experiment;
	}

	/**
	 * Constructor.
	 * 
	 * @param molecule1 The first Molecule of this Correlation.
	 * @param molecule2 The second Molecule of this Correlation.
	 * @param samples The samples this Correlation is associated with.
	 */
	public Correlation( Molecule molecule1, Molecule molecule2, 
	                    Collection<Sample> samples ) {
		super( molecule1, molecule2 );
		this.samples = samples;
	}

	/**
	 * Constructor.
	 * 
	 * @param molecule1 The first Molecule of this Correlation.
	 * @param molecule2 The second Molecule of this Correlation.
	 */
	@Deprecated
	public Correlation( Molecule molecule1, Molecule molecule2 ) {
		super( molecule1, molecule2 );
		this.samples = new ArrayList<Sample>( );
	}

	/**
	 * Constructor which accepts an array of 2 Molecules.
	 * 
	 * @param molecules A Molecule array of length 2. Any extra values are
	 *	ignored.
	 * @param experiment The experiment this Correlation is associated with.
	 */
	@Deprecated
	public Correlation( Molecule [] molecules, Experiment experiment ) {
		this( molecules[ 0 ], molecules[ 1 ], experiment );
	}

	/**
	 * Constructor which accepts an array of 2 Molecules.
	 * 
	 * @param molecules A Molecule array of length 2. Any extra values are
	 *	ignored.
	 */
	public Correlation( Molecule [] molecules ) {
		this( molecules[ 0 ], molecules[ 1 ]);
	}

	@Deprecated
	public Experiment getExperiment( ) {
		return this.experiment;
	}

	/**
	 * Compares this Correlation to another.
	 * 
	 * @see Comparable#compareTo( T )
	 * @param c The Correlation to compare this Correlation to.
	 * @return An integer indicating the status of the comparison.
	 */
	public int compareTo( Correlation c ) {

		if ( c.containsAll( this ) && 
		     this.getSamples( ).containsAll( c.getSamples( )) &&
		     c.getSamples( ).containsAll( this.getSamples( ))) {
			return 0;
		}
		int returnValue = this.getFirst( ).compareTo( c.getFirst( ));
		if ( returnValue == 0 ) {
			returnValue = this.getSecond( ).compareTo( c.getSecond( ));
		}
		if ( returnValue == 0 ) {
			returnValue = (int)Math.signum( 
				this.getSamples( ).size( ) - c.getSamples( ).size( ));
		}
		return returnValue;
	}

	/**
	 * Retrieves the samples being used by this Correlation for calculation.
	 * 
	 * @return The samples being used.
	 */
	public Collection<Sample> getSamples( ) {
		return Collections.unmodifiableCollection( this.samples );
	}

	/**
	 * Gets the correlation coeffiecient of this Correlation
	 * 
	 * @param method The coefficient calculation method to use. Should be one of
	 *	Correlation.PEARSON, Correlation.SPEARMAN, or Correlation.KENDALL.
	 * @return A double containing the requested correlation value.
	 */
	public double getValue( int method ) {
		return this.getValue( method, false );
	}

	/**
	 * Gets the correlation coefficient of this Correlation.
	 * 
	 * @param method The coefficient calculation method to use. Should be one of
	 *	Correlation.PEARSON, Correlation.SPEARMAN, or Correlation.KENDALL.
	 * @param recalculate Whether or not to recalculate this value if it has 
	 *	already been calculated.
	 * @return A double containing the requested correlation value.
	 */
	public double getValue( int method, boolean recalculate ) {
		
		switch ( method ) {
			case PEARSON:
				return this.getPearsonCorrelation( recalculate );

			case SPEARMAN:
				return this.getSpearmanCorrelation( recalculate );

			case KENDALL:
				return this.getKendallCorrelation( recalculate );

			default:
				return -1;
		}
	}

	/**
	 * Gets the correlation coefficient of this Correlation.
	 * 
	 * @param method The coefficient calculation method to use. Should be one of
	 *	Correlation.PEARSON, Correlation.SPEARMAN, or Correlation.KENDALL in the
	 *	form of a Number.
	 * @return A double containing the requested correlation value.
	 */
	public double getValue( Number method ) {
		return this.getValue( method.intValue( ), false );
	}

	/**
	 * Gets the correlation coefficient of this Correlation.
	 * 
	 * @param method The coefficient calculation method to use. Should be one of
	 *	Correlation.PEARSON, Correlation.SPEARMAN, or Correlation.KENDALL in the
	 *	form of a Number.
	 * @param recalculate Whether or not to recalculate this value if it has 
	 *	already been calculated.
	 * @return A double containing the requested correlation value.
	 */
	public double getValue( Number method, boolean recalculate ) {
		return this.getValue( method.intValue( ), recalculate );
	}

	/**
	 * Returns the Pearson correlation coefficient of the 2 molecueles.
	 * 
	 * @return    The Pearson correlation value. 
	 */
	public double getPearsonCorrelation( ) {
		return this.getPearsonCorrelation( false );
	}

	/**
	 * Returns the Pearson correlation coefficient of the 2 molecueles.
	 * 
	 * @param recalculate True if you want the value recalculated, otherwise a 
	 *	cached value will be returned if present.
	 * @return       The Pearson correlation value.
	 */
	public double getPearsonCorrelation( boolean recalculate ) {
		// look for a change in the number of samples and clear the cache if so.
		if ( this.samples.size( ) != this.sampleSize ) {
			this.clearCache( );
			this.sampleSize = this.samples.size( );
		}
		//See if this value has already been calculated
		if ( recalculate || Double.isNaN( this.pearsonCorrelation )) {
			NumberList molecule0Values = new NumberList( 
				this.first.getValues( this.samples ));
			NumberList molecule1Values = new NumberList( 
				this.second.getValues( this.samples ));
			filterZeros( molecule0Values, molecule1Values );
			this.pearsonCorrelation = Statistics.getPearsonCorrelation( 
				molecule0Values.toDoubleArray( ), 
				molecule1Values.toDoubleArray( ));
			if ( Double.isNaN( this.pearsonCorrelation )) {
				this.pearsonCorrelation = 0.0;
			}
		}
		return this.pearsonCorrelation;
	}

	/**
	 * Returns the Spearman rank correlation coefficient of the 2 molecules.
	 *
	 * @return       The Spearman correlation value.
	 */
	public double getSpearmanCorrelation( ) {
		return this.getSpearmanCorrelation( false );
	}

	/**
	 * Returns the Spearman rank correlation coefficient of the 2 molecules.
	 *
	 * @param recalculate True if you want the value recalculated, otherwise a 
	 * cached value will be returned if present.
	 * @return The Spearman correlation value.
	 */
	public double getSpearmanCorrelation( boolean recalculate ) {
		// look for a change in the number of samples and clear the cache if so.
		if ( this.samples.size( ) != this.sampleSize ) {
			this.clearCache( );
			this.sampleSize = this.samples.size( );
		}
		//See if this value has already been calculated
		if ( recalculate || Double.isNaN( this.spearmanCorrelation ) ) {
			NumberList molecule0Values = new NumberList( 
				this.first.getValues( this.samples ));
			NumberList molecule1Values = new NumberList( 
				this.second.getValues( this.samples ));
			filterZeros( molecule0Values, molecule1Values );
			this.spearmanCorrelation = Statistics.getSpearmanCorrelation( 
				molecule0Values.toDoubleArray( ), 
				molecule1Values.toDoubleArray( ));
			if ( Double.isNaN( this.spearmanCorrelation )) {
				this.spearmanCorrelation = 0.0;
			}
		}
		return this.spearmanCorrelation;
	}

	/**
	 * Returns the Kendall tau-b rank correlation coefficient of the 2 molecules.
	 * 
	 * @return The Kendall tau correlation value.
	 */
	public double getKendallCorrelation( ) {
		return this.getKendallCorrelation( false );
	}

	/**
	 * Returns the Kendall tau-b rank correlation coefficient of the 2 molecules.
	 * 
	 * @param recalculate Whether or not to recalculate this value if it has 
	 *	already been calculated. 
	 * @return The Kendall tau correlation value.
	 */
	public double getKendallCorrelation( boolean recalculate ) {
		// look for a change in the number of samples and clear the cache if so.
		if ( this.samples.size( ) != this.sampleSize ) {
			this.clearCache( );
			this.sampleSize = this.samples.size( );
		}
		if ( recalculate || Double.isNaN( this.kendallCorrelation )) {
			NumberList molecule0Values = new NumberList( 
				this.first.getValues( this.samples ));
			NumberList molecule1Values = new NumberList( 
				this.second.getValues( this.samples ));
			filterZeros( molecule0Values, molecule1Values );
			this.kendallCorrelation = Statistics.getKendallCorrelation( 
				molecule0Values.toDoubleArray( ), 
				molecule1Values.toDoubleArray( ));
			if ( Double.isNaN( this.kendallCorrelation )) {
				this.kendallCorrelation = 0.0;
			}
		}
		return this.kendallCorrelation;
	}

	/**
	 * Clears the all cached values for this correlation, forcing recalculation
	 * the next time values are requested. This should be called any time the 
	 * samples change. Correlation will notice when the sample size changes 
	 * and call this method when a value is requested, but will not notice if 
	 * one sample is swapped for another between calls for correlation value.
	 */
	public void clearCache( ) {
		this.pearsonCorrelation = Double.NaN;
		this.spearmanCorrelation = Double.NaN;
		this.kendallCorrelation = Double.NaN;
	}

	/**
	 * Removes an element from both lists when the element in either is equal to 
	 * 0. Double.compare( ) is used to determine this. This operation is 
	 * destructive and therefore you should pass a copy of the List if you do not 
	 * want the original to be modified.
	 * 
	 * @param list0 The first list to be filtered.
	 * @param list1 The second list to be filtered.
	 */
	public static void filterZeros ( List<Number> list0, List<Number> list1 ) {
		int size = Math.min( list0.size( ), list1.size( ));
		for ( int i=size - 1; i >= 0;  i-- ) {
			if ( Double.compare( list0.get( i ).doubleValue( ), 0.0 ) == 0 ||
			     Double.compare( list1.get( i ).doubleValue( ), 0.0 ) == 0 ) {
				list0.remove( i );
				list1.remove( i );
			}
		}
	}
	
	/**
	 * Returns a string representation of this Correlation.
	 * 
	 * @return a String representing this Correlation.
	 */
	public String toString( ) {
		return String.format( "%s - %s", this.first.toString( ), 
			this.second.toString( ));
	}

	/**
	 * Returns the critical values for 0.1, 0.05, 0.02, and 0.01 for the given 
	 * method and sample count. A value of -1 means that there is no critical 
	 * value for this entry.
	 * 
	 * @param method The method to get the critical values for. Should be one of 
	 *	Correlation.PEARSON, Correlation.SPEARMAN, or Correlation.KENDALL.
	 * @param sampleCount The number of samples for which to get the critical 
	 *	values for.
	 * @return The critical values for 0.1, 0.05, 0.02, and 0.01.
	 */
	public static double [] getCriticalValues( int method, int sampleCount ) {
		if ( criticalValues[ method ].length < sampleCount )
			return new double [] { -1, -1, -1, -1 };
		return criticalValues[ method ][ sampleCount ];
	}

	private static final double [][][] criticalValues = 
		{{{ -1,   -1,   -1,   -1 }, {   -1,   -1,   -1,   -1 }, 
		{   -1,   -1,   -1,   -1 }, { .988, .997, .9995,.9999},
		{ .900, .950, .980, .990 }, { .805, .878, .934, .959 }, 
		{ .729, .811, .882, .917 }, { .669, .754, .833, .874 },
		{ .622, .707, .789, .834 }, { .582, .666, .750, .798 }, 
		{ .549, .632, .716, .765 }, { .521, .602, .685, .735 },
		{ .497, .576, .658, .708 }, { .476, .553, .634, .684 }, 
		{ .458, .532, .612, .661 }, { .441, .514, .592, .641 },
		{ .426, .497, .574, .623 }, { .412, .482, .558, .606 }, 
		{ .400, .468, .542, .590 }, { .389, .456, .528, .575 },
		{ .378, .444, .516, .561 }, { .369, .433, .503, .549 }, 
		{ .360, .423, .492, .537 }, { .352, .413, .482, .526 },
		{ .344, .404, .472, .515 }, { .337, .396, .462, .505 }, 
		{ .330, .388, .453, .496 }, { .323, .381, .445, .487 },
		{ .317, .374, .437, .479 }, { .311, .367, .430, .471 }, 
		{ .306, .361, .423, .463 }, { .301, .355, .416, .456 },
		{ .296, .349, .409, .449 }, { .296, .349, .409, .449 }, 
		{ .296, .349, .409, .449 }, { .296, .349, .409, .449 },
		{ .296, .349, .409, .449 }, { .275, .325, .381, .418 }, 
		{ .275, .325, .381, .418 }, { .275, .325, .381, .418 },
		{ .275, .325, .381, .418 }, { .275, .325, .381, .418 }, 
		{ .257, .304, .358, .393 }, { .257, .304, .358, .393 },
		{ .257, .304, .358, .393 }, { .257, .304, .358, .393 }, 
		{ .257, .304, .358, .393 }, { .243, .288, .338, .372 },
		{ .243, .288, .338, .372 }, { .243, .288, .338, .372 }, 
		{ .243, .288, .338, .372 }, { .243, .288, .338, .372 },
		{ .231, .273, .322, .354 }, { .231, .273, .322, .354 }, 
		{ .231, .273, .322, .354 }, { .231, .273, .322, .354 },
		{ .231, .273, .322, .354 }, { .231, .273, .322, .354 }, 
		{ .231, .273, .322, .354 }, { .231, .273, .322, .354 },
		{ .231, .273, .322, .354 }, { .231, .273, .322, .354 }, 
		{ .211, .250, .295, .325 }, { .211, .250, .295, .325 },
		{ .211, .250, .295, .325 }, { .211, .250, .295, .325 }, 
		{ .211, .250, .295, .325 }, { .211, .250, .295, .325 },
		{ .211, .250, .295, .325 }, { .211, .250, .295, .325 }, 
		{ .211, .250, .295, .325 }, { .211, .250, .295, .325 },
		{ .195, .232, .274, .303 }, { .195, .232, .274, .303 }, 
		{ .195, .232, .274, .303 }, { .195, .232, .274, .303 },
		{ .195, .232, .274, .303 }, { .195, .232, .274, .303 }, 
		{ .195, .232, .274, .303 }, { .195, .232, .274, .303 },
		{ .195, .232, .274, .303 }, { .195, .232, .274, .303 }, 
		{ .183, .217, .256, .283 }, { .183, .217, .256, .283 },
		{ .183, .217, .256, .283 }, { .183, .217, .256, .283 }, 
		{ .183, .217, .256, .283 }, { .183, .217, .256, .283 },
		{ .183, .217, .256, .283 }, { .183, .217, .256, .283 }, 
		{ .183, .217, .256, .283 }, { .183, .217, .256, .283 },
		{ .173, .205, .242, .267 }, { .173, .205, .242, .267 }, 
		{ .173, .205, .242, .267 }, { .173, .205, .242, .267 },
		{ .173, .205, .242, .267 }, { .173, .205, .242, .267 }, 
		{ .173, .205, .242, .267 }, { .173, .205, .242, .267 },
		{ .173, .205, .242, .267 }, { .173, .205, .242, .267 }, 
		{ .164, .195, .230, .254 }},

		{{  -1,   -1,   -1,   -1 }, {   -1,   -1,   -1,   -1 }, 
		{   -1,   -1,   -1,   -1 }, {   -1,   -1,   -1,   -1 },
		{ 1.00,   -1,   -1,   -1 }, { .900, 1.000, 1.000, -1 }, 
		{ .829, .886, .943, 1.000}, { .714, .786, .893, .929 },
		{ .643, .738, .833, .881 }, { .600, .700, .783, .833 }, 
		{ .564, .648, .745, .794 }, { .536, .618, .709, .755 },
		{ .503, .587, .671, .727 }, { .484, .560, .648, .703 }, 
		{ .464, .538, .622, .675 }, { .443, .521, .604, .654 },
		{ .429, .503, .582, .635 }, { .414, .485, .566, .615 }, 
		{ .401, .472, .550, .600 }, { .391, .460, .535, .584 },
		{ .380, .447, .520, .570 }, { .370, .435, .508, .5556}, 
		{ .361, .425, .496, .544 }, { .353, .415, .486, .532 },
		{ .344, .406, .476, .521 }, { .337, .398, .466, .511 }, 
		{ .331, .390, .457, .501 }, { .324, .382, .448, .491 },
		{ .317, .375, .440, .483 }, { .312, .368, .433, .475 }, 
		{ .306, .362, .425, .467 }, { .283, .335, .394, .433 }},

		{{  -1,   -1,   -1,   -1 }, {   -1,   -1,   -1,   -1 }, 
		{   -1,   -1,   -1,   -1 }, {   -1,   -1,   -1,   -1 },
		{ 1.00, 1.00, 1.00, 1.00 }, { .800, 1.00, 1.00, 1.00 }, 
		{ .733, .867, .867, 1.000}, { .619, .714, .810, .905 },
		{ .571, .643, .714, .786 }, { .500, .556, .667, .722 }, 
		{ .467, .511, .600, .644 }, { .418, .491, .564, .600 },
		{ .394, .455, .545, .576 }, { .359, .436, .513, .564 }, 
		{ .363, .407, .473, .516 }, { .333, .390, .467, .505 },
		{ .317, .383, .433, .483 }, { .309, .368, .426, .471 }, 
		{ .294, .346, .412, .451 }, { .287, .333, .392, .439 },
		{ .274, .326, .379, .421 }, { .267, .314, .371, .410 }, 
		{ .264, .307, .359, .394 }, { .257, .296, .352, .391 },
		{ .246, .290, .341, .377 }, { .240, .287, .333, .367 }, 
		{ .237, .280, .329, .360 }, { .231, .271, .322, .356 },
		{ .228, .265, .312, .344 }, { .222, .261, .310, .340 }, 
		{ .218, .255, .301, .333 }, { .213, .252, .295, .325 },
		{ .210, .246, .290, .323 }, { .205, .242, .288, .314 }, 
		{ .201, .237, .280, .312 }, { .197, .234, .277, .304 },
		{ .194, .232, .273, .302 }, { .192, .228, .267, .297 }, 
		{ .189, .223, .263, .292 }, { .188, .220, .260, .287 },
		{ .185, .218, .256, .285 }}}; 
}

