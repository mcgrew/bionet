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

import java.util.List;
import java.util.ArrayList;

/**
 * A class for holding data about a group of Molecules.
 *
 * @author Thomas McGrew
 * @version 1.0
 */
public class MoleculeGroup extends ArrayList<Molecule> implements Comparable<MoleculeGroup> {

	private String name;

	/**
	 * Constructor.
	 * 
	 * @param name The name of this MoleculeGroup.
	 */
	public MoleculeGroup( String name ) {
		this.name = name;
	}

	/**
	 * Adds a Molecule to this MoleculeGroup.
	 * 
	 * @param molecule The molecule to be added.
	 *
	 * @deprecated This type now implements List&lt;Molecule&gt;. Use add( Molecule ) instead.
	 */
	@Deprecated
	public void addMolecule( Molecule molecule ) {
		this.add( molecule );
	}

	/**
	 * Gets all of the Molecules in this group.
	 * 
	 * @return A List containing all of the molecules.
	 *
	 * @deprecated This type now implements List, so this method simply returns itelf.
	 */
	@Deprecated
	public List <Molecule> getMolecules( ) {
		return this;
	}

	/**
	 * Gets a molecule by its id attribute.
	 * 
	 * @param id The id of the Molecule to be retrieved.
	 * @return The requested Molecule.
	 */
	public Molecule getMolecule( String id ) {
		for ( Molecule m : this ) {
			if ( id.equals( m.getAttribute( "id" )))
				return m;
		}
		return null;
	}

	/**
	 * Sets the Name of this group
	 * 
	 * @param name The new name for this group.
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * Gets the name for this group.
	 * 
	 * @return A String containing the name of this group.
	 */
	public String getName( ) {
		return this.name;
	}

	/**
	 * Gets the name for this group.
	 * 
	 * @return A String containing the name of this group.
	 */
	public String toString( ) {
		return this.name;
	}

	/**
	 * The compareTo method of the Comparable interface.
	 * @see java.lang.Comparable#compareTo(T)
	 * 
	 * @param mg Compares this object with the specified object for order. 
	 * @return a negative integer, zero, or a positive integer as this MoleculeGroup is
	 *	less than, equal to, or greater than the specified MoleculeGroup.
	 */
	public int compareTo( MoleculeGroup mg ) {
		int returnValue = this.getName( ).compareTo( mg.getName( ));
		if ( returnValue == 0 ) {
			returnValue = (int)Math.signum( this.size( ) -  mg.size( ));
		}
		return returnValue;
	}

}
