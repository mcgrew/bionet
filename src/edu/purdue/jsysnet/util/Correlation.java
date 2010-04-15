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

import java.lang.Math;
import java.lang.Double;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * A class for connecting two Molecules and determining their correlation
 * coefficient.
 *
 * @author Thomas McGrew
 * @version 1.0
 */
public class Correlation {

	public static final int PEARSON = 0;
	public static final int SPEARMAN = 1;
	public static final int KENDALL = 2;
	protected static int defaultMethod = PEARSON;
	protected double pearsonCorrelation = Double.NaN;
	protected double spearmanCorrelation = Double.NaN;
	protected double kendallCorrelation = Double.NaN;
	protected Molecule [ ] molecules = new Molecule[ 2 ];
	
	/**
	 * Constructor.
	 * 
	 * @param molecule1 The first Molecule of this Correlation.
	 * @param molecule2 The second Molecule of this Correlation.
	 */
	public Correlation( Molecule molecule1, Molecule molecule2 ) {
		this.molecules[ 0 ] = molecule1;
		this.molecules[ 1 ] = molecule2;
		this.molecules[ 0 ].addCorrelation( this );
		this.molecules[ 1 ].addCorrelation( this );
	}

	/**
	 * Constructor.
	 * 
	 * @param molecules A Molecule array of length 2. Any extra values are
	 *	ignored.
	 */
	public Correlation( Molecule [] molecules ){
		this.molecules[ 0 ] = molecules[ 0 ];
		this.molecules[ 1 ] = molecules[ 1 ];
		this.molecules[ 0 ].addCorrelation( this );
		this.molecules[ 1 ].addCorrelation( this );
	}

	/**
	 * Sets the default calculation method for all Correlations.
	 * 
	 * @param method The method to be used.
	 * @return true on success, false on failure.
	 */
	public static boolean setDefaultMethod( int method ) {
		if ( method < 0 || method > 2 )
			return false;
		defaultMethod = method;
		return true;
	}
	/**
	 * Gets the Molecules associated with this Correlation.
	 * 
	 * @return A Molecule array of length 2, containing the 2 Molecules
	 *	associated with this correlation.
	 */
	public Molecule [ ] getMolecules( ) {
		return this.molecules;
	}

	/**
	 * Returns the other molecule in this Correlation
	 * 
	 * @param molecule The Molecule you wish to get the opposite of.
	 * @return The opposite Molecule from the passed in Molecule which is assiciated 
	 *	with this correlation. Returns null if the passed in Molecule is not
	 *	associated with this Correlation.
	 */
	public Molecule getOpposite( Molecule molecule ) {
		if ( molecule == this.molecules[ 0 ])
			return this.molecules[ 1 ];
		if ( molecule == this.molecules[ 1 ])
			return this.molecules[ 0 ];
		return null;
	}

	/**
	 * Whether or not this Correlation is associated with the specified Molecule.
	 * 
	 * @param molecule The molecule to test association with.
	 * @return True if this Correlation is associated with the specified Molecule,
	 *	false otherwise.
	 */
	public boolean hasMolecule( Molecule molecule ) {
		return ( molecule == this.molecules[ 0 ] ||
		         molecule == this.molecules[ 1 ] );
	}

	/**
	 * Gets the correlation coefficient of this Correlation.
	 * 
	 * @return A double containing the last calculated correlation coeficcient.
	 *	Defaults to PEARSON.
	 */
	public double getValue( ) {
		return this.getValue( defaultMethod, false );
	}

	/**
	 * Gets the correlation coeffiecient of this Correlation
	 * 
	 * @param recalculate Whether or not to recalculate this value if it has already been calculated.
	 * @return A double containing the last calculated correlation coeficcient.
	 */
	public double getValue( boolean recalculate ) {
		return this.getValue( defaultMethod, recalculate );
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
	 * @param recalculate Whether or not to recalculate this value if it has already been calculated.
	 * @return A double containing the requested correlation value.
	 */
	public double getValue( int method, boolean recalculate ) {
		
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
	 * Gets the correlation coefficient of this Correlation.
	 * 
	 * @param molecule0 The first Molecule to use for the calculation.
	 * @param molecule1 The second Molecule to use for the calculation. 
	 * @return The correlation value;
	 */
	public static double getValue( Molecule molecule0, Molecule molecule1 ) {
		return Correlation.getValue( defaultMethod, molecule0, molecule1 );
	}

	/**
	 * Gets the correlation coefficient of this Correlation.
	 * 
	 * @param method The method to use for calculating the correlation. 
	 * @param molecule0 The first Molecule to use for the calculation.
	 * @param molecule1 The second Molecule to use for the calculation. 
	 * @return The correlation value;
	 */
	public static double getValue( int method, Molecule molecule0, Molecule molecule1 ) {

		switch ( method ) {
			case PEARSON:
				return Correlation.getPearsonCorrelation( molecule0, molecule1 );

			case SPEARMAN:
				return Correlation.getSpearmanCorrelation( molecule0, molecule1 );

			case KENDALL:
				return Correlation.getKendallCorrelation( molecule0, molecule1 );

			default:
				return -1;
		}
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
	 * @param recalculate True if you want the value recalculated, otherwise a cached value will be returned if present.
	 * @return       The Pearson correlation value.
	 */
	public double getPearsonCorrelation( boolean recalculate ) {
		//See if this value has already been calculated
		if ( recalculate || Double.isNaN( this.pearsonCorrelation )) {
			this.pearsonCorrelation = 
				Correlation.getPearsonCorrelation( this.molecules[ 0 ], this.molecules[ 1 ]);
		}
		return this.pearsonCorrelation;
	}

	/**
	 * Returns the Pearson correlation coefficient of the 2 molecueles.
	 * 
	 * @param molecule0 The first Molecule to use for the calculation.
	 * @param molecule1 The second Molecule to use for the calculation.
	 * @return    The Pearson correlation value. 
	 */
	public static double getPearsonCorrelation( Molecule molecule0, Molecule molecule1 ) {
		return Statistics.getPearsonCorrelation( molecule0.getSamples( ), molecule1.getSamples( ));
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
	 * @param recalculate True if you want the value recalculated, otherwise a cached value will be returned if present.
	 * @return       The Spearman correlation value.
	 */
	public double getSpearmanCorrelation( boolean recalculate ) {
		//See if this value has already been calculated
		if ( recalculate || Double.isNaN( this.spearmanCorrelation ) ) {
			Correlation.getSpearmanCorrelation( this.molecules[ 0 ], this.molecules[ 1 ]);
		}
		return this.spearmanCorrelation;
	}

	/**
	 * Returns the Spearman rank correlation coefficient of the 2 molecules.
	 *
	 * @param molecule0 The first Molecule to use for the calculation.
	 * @param molecule1 The second Molecule to use for the calculation.
	 * @return       The Spearman correlation value.
	 */
	public static double getSpearmanCorrelation( Molecule molecule0, Molecule molecule1 ) {
		return Statistics.getSpearmanCorrelation( molecule0.getSamples( ), molecule1.getSamples( ));
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
	 * @param recalculate Whether or not to recalculate this value if it has already been calculated. 
	 * @return The Kendall tau correlation value.
	 */
	public double getKendallCorrelation( boolean recalculate ) {
		if ( recalculate || Double.isNaN( this.kendallCorrelation )) {
			this.kendallCorrelation = 
				Correlation.getKendallCorrelation( this.molecules[ 0 ], this.molecules[ 1 ]);
		}
		return this.kendallCorrelation;
	}

	/**
	 * Returns the Kendall tau-b rank correlation coefficient of the 2 molecules.
	 * 
	 * @param molecule0 The first Molecule to use for the calculation.
	 * @param molecule1 The second Molecule to use for the calculation.
	 * @return The Kendall tau correlation value.
	 */
	public static double getKendallCorrelation( Molecule molecule0, Molecule molecule1 ) {
		return Statistics.getKendallCorrelation( molecule0.getSamples( ), molecule1.getSamples( ));
	}

}

