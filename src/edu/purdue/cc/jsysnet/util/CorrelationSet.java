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

package edu.purdue.cc.jsysnet.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class CorrelationSet extends TreeSet<Correlation> {
	Collection<Molecule> molecules;
	Collection<Sample> samples;

	public CorrelationSet( Collection<Sample> samples ) {
		super( );
		this.samples = samples;
		this.molecules = new TreeSet<Molecule>( );
	}

	public CorrelationSet( CorrelationSet c,
	                       Collection<Sample> samples ) {
		super( c );
		this.samples = samples;
		this.molecules = new TreeSet<Molecule>( );
	}

	public CorrelationSet( Comparator<? super Correlation> c,
	                       Collection<Sample> samples ) {
		super( c );
		this.samples = samples;
		this.molecules = new TreeSet<Molecule>( );
	}

	public CorrelationSet( SortedSet<Correlation> s,
	                       Collection<Sample> samples ) {
		super( s );
		this.samples = samples;
		this.molecules = new TreeSet<Molecule>( );
	}

	public CorrelationSet( Collection<Molecule> molecules,
	                       Collection<Sample> samples ) {
		super( );
		this.molecules = new TreeSet<Molecule>( );
		this.samples = samples;
		for ( Molecule molecule : molecules ) {
			this.add( molecule );
		}
	}

	/**
	 * Adds a Molecule to this CorrelationSet, creating the appropriate 
	 * Correlations
	 * 
	 * @param molecule The molecule to be added.
	 */
	public boolean add( Molecule molecule ){
		if ( !this.molecules.contains( molecule )) {
			for ( Molecule m : this.molecules ) {
				super.add( new Correlation( m, molecule, this.samples ));
			}
			this.molecules.add( molecule );
		} else {
			Logger.getLogger( getClass( )).debug( String.format( 
				"This CorrelationSet already contains Molecule %s", 
				molecule.getId( )));
			return false;
		}
		return true;
	}

	@Override
	public boolean add( Correlation correlation ) {
		boolean returnValue = this.add( correlation.getFirst( ));
		returnValue = returnValue && this.add( correlation.getSecond( ));
		return returnValue;
	}

	public boolean addAll( Collection<? extends Correlation> correlations ) {
		boolean returnValue = false;
		for ( Correlation correlation : correlations ) {
			returnValue = returnValue || this.add( correlation );
		}
		return returnValue;
	}

	/**
	 * Returns the correlation object associated with the 2 passed in molecules.
	 * 
	 * @param molecule1 One molecule in the Correlation.
	 * @param molecule2 The other molecule in the Correlation.
	 * @return The requested correlation.
	 */
	public Correlation getCorrelation( Molecule molecule1, Molecule molecule2 ) {
		for ( Correlation correlation : this ) {
			if ( correlation.contains( molecule1 ) && 
			     correlation.contains( molecule2 ))
				return correlation;
		}
		return null;
	}

	/**
	 * Returns all Correlations associated with the passed in Molecule.
	 * 
	 * @param molecule The Molecule to get Correlations for.
	 * @return A Collection containing the requested Correlations.
	 */
	public Collection<Correlation> getCorrelations( Molecule molecule ) {
		Collection<Correlation> returnValue = new TreeSet<Correlation>( );
		for ( Correlation correlation : this ) {
			if ( correlation.contains( molecule )) {
				returnValue.add( correlation );
			}
		}
		return returnValue;
	}

	public Collection<Sample> getSamples( ) {
		return this.samples;
	}

	public Collection<Molecule> getMolecules( ) {
		return this.molecules;
	}

	@Override
	public boolean remove( Object o ) {
		if ( o instanceof Correlation ) {
			return this.removeCorrelation( (Correlation)o );
		} else if ( o instanceof Molecule ) {
			return this.removeMolecule( (Molecule)o );
		} else {
			throw new IllegalArgumentException( String.format( 
				"The value %s is not supported in this class", o.toString( )));
		}
	}

	private boolean removeCorrelation( Correlation c ) {
		boolean returnValue = this.removeMolecule( c.getFirst( ));
		returnValue = returnValue && this.removeMolecule( c.getSecond( ));
		return returnValue;
	}

	private boolean removeMolecule( Molecule m ) {
		boolean returnValue = false;
		if ( !this.molecules.contains( m )) {
			for ( Correlation correlation : this ) {
				if ( correlation.contains( m )) {
					returnValue = returnValue || super.remove( correlation );
				}
			}
		}
		return returnValue;
	}

}


