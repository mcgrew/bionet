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

import edu.purdue.bbc.util.attributes.StringAttributes;
import edu.purdue.bbc.util.NumberList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * A class for keeping information about a particular sample in an experiment.
 */
public class Sample  extends StringAttributes implements Comparable<Sample>,Cloneable {
	protected String name;
	protected Map<Molecule,Number> valueMap;
  private static List<String> names = new ArrayList( );

	/**
	 * Constructor. Creates a new sample with the given name.
	 * 
	 * @param name The name of this Sample.
	 */
	public Sample( String name ) {
		super( false );
    if ( !names.contains( name )) {
      names.add( name );
      this.name = name;
    } else {
      this.name = names.get( names.indexOf( name ));
    }
		Logger.getLogger( getClass( )).debug( "Creating Sample: " + name );
		this.attributes = new HashMap<String,String>( );
		this.valueMap = new HashMap<Molecule,Number>( );
	}

	/**
	 * Creates a new Sample which is a copy of the passed in sample.
	 * 
	 * @param sample The sample to make a copy of.
	 */
	public Sample( Sample sample ) {
		this( sample.toString( ));
		this.setAttributes( sample.attributes );
		for ( Molecule molecule : sample.getMolecules( )) {
			this.setValue( molecule, sample.getValue( molecule ));
		}
	}

	/**
	 * Returns a String representation of this Sample; in this case the name.
	 * 
	 * @return The name of this sample.
	 */
	public String toString( ) {
		return name;
	}

	/**
	 * Compares this sample to another.
	 * 
	 * @param o The sample to compare to
	 * @return An integer indicating this Samples order relation to the passed-in
	 *	sample.
	 */
	public int compareTo( Sample o ) {
		int returnValue = 0;
    if ( this.name != o.toString( )) {
			return this.name.compareTo( o.toString( ));
    }
		if ( this.hasAttribute( "time" ) && o.hasAttribute( "time" )) {
			try {	
				double s1Time = Double.parseDouble( this.getAttribute( "time" ));
				double s2Time = Double.parseDouble( o.getAttribute( "time" ));
				return Double.compare( s1Time, s2Time );
			} catch ( NumberFormatException e ) { }
		}
//		if ( returnValue == 0 ) {
//			Iterator<Molecule> myMolecules = this.getMolecules( ).iterator( );
//			Iterator<Molecule> hisMolecules = o.getMolecules( ).iterator( );
//			while ( returnValue == 0 ) {
//				if ( myMolecules.hasNext( ) && !hisMolecules.hasNext( ))
//					return 1;
//				if ( !myMolecules.hasNext( ) && hisMolecules.hasNext( ))
//					return -1;
//				if ( !myMolecules.hasNext( ) && !hisMolecules.hasNext( ))
//					return 0;
//				returnValue = myMolecules.next( ).compareTo( hisMolecules.next( ));
//			}
//		}
		return 1;
		
	}

	/**
	 * Determines whether or not two samples are equal.
	 * 
	 * @param o The sample to compare this one to.
	 * @return True if they are equal.
	 */
	public boolean equals( Sample o ) {
    return ( this.name == o.toString( ) );
	}

	/**
	 * Sets a concentration value for the given Molecule in this sample.
	 * 
	 * @param molecule The molecule to set the concentration value for.
	 * @param value The value of the concentration.
	 */
	public void setValue( Molecule molecule, Number value ) {
		this.valueMap.put( molecule, value );
	}

	/**
	 * Retrieves a concentration value for the given Molecule.
	 * 
	 * @param molecule The Molecule to retreive the concentration value for.
	 * @return The concentration value as a Number.
	 */
	public Number getValue( Molecule molecule ) {
		Number returnValue = this.valueMap.get( molecule );
		if ( returnValue == null )
			return new Double( 0 );
		return returnValue;
	}

	/**
	 * Retrieves the concentration values for a set of Molecules
	 * 
	 * @param molecules A Collection containing the molecules to retrieve values
	 *	for.
	 * @return a NumberList containing the values.
	 */
	public NumberList getValues( Collection<Molecule> molecules ) {
		NumberList returnValue = new NumberList( );
		for ( Molecule m : molecules ) {
			returnValue.add( this.getValue( m ));
		}
		return returnValue;
	}

	/**
	 * Gets a molecule reference related to this sample by it's Id.
	 * 
	 * @param id An Object whose toString( ) method returns the same as the
	 *	Molecule's getId( ) method.
	 * @return The requested molecule.
	 */
	public Molecule getMolecule( Object id ) {
		for ( Molecule molecule : this.valueMap.keySet( )) {
			if ( id.toString( ).equals( molecule.getId( )))
				return molecule;
		}
		return null;
	}

	/**
	 * Returns a Collection of all Molecules associated with this Sample.
	 * 
	 * @return A Collection of Molecules.
	 */
	public Collection <Molecule> getMolecules( ) {
		return this.valueMap.keySet( );
	}

	public Number removeMolecule( Molecule m ) {
		return this.valueMap.remove( m );
	}

	/**
	 * Creates a copy of this Sample.
	 * 
	 * @return A copy of this Sample.
	 */
	@Override @Deprecated
	public Sample clone( ) {
		return new Sample( this );
	}

}

