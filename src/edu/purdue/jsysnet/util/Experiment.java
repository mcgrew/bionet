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

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import java.util.ListIterator;

/**
 * A class for holding data about a particular experiment.
 * 
 * @author Thomas McGrew
 */
public class Experiment {

	private HashMap <String,String> attributes;
	private HashMap <String,MoleculeGroup> moleculeGroups;
	private List <Molecule> molecules;
	private List <Correlation> correlations;

	/**
	 * Constructor.
	 * 
	 * @param attributes A HashMap containing this Experiment's attributes.
	 */
	public Experiment( HashMap <String,String> attributes ) {
		this.attributes = attributes;
		this.moleculeGroups = new HashMap <String,MoleculeGroup>( );
		this.molecules = new ArrayList <Molecule>( );
		this.correlations = new ArrayList <Correlation>( );
	}
	
	/**
	 * Gets the molecules associated with this Experiment.
	 * 
	 * @return A List of Molecules.
	 */
	public List <Molecule> getMolecules( ) {
		return this.molecules;
	}

	/**
	 * Adds a Molecule to this Experiment.
	 * 
	 * @param group A string containing the group this molecule belongs to.
	 * @param molecule The molecule to be added.
	 */
	public void addMolecule( String group, Molecule molecule ){
		if ( !moleculeGroups.containsKey( group )) {
			this.addMoleculeGroup( group );
		}
		this.moleculeGroups.get( group ).addMolecule( molecule );
		ListIterator <Molecule> iter = this.molecules.listIterator( );
		while ( iter.hasNext( )) {
			this.correlations.add( new Correlation( iter.next( ), molecule ));
		}
		this.molecules.add( molecule );
	}

	/**
	 * Adds a Molecule to this Experiment.
	 * 
	 * @param molecule The molecule to be added. The group is determined from the
	 *	Molecule's &quot;group&quot; attribute.
	 */
	public void addMolecule( Molecule molecule ) {
		this.addMolecule( molecule.getGroup( ), molecule );
	}

	/**
	 * The MoleculeGroups for this experiement.
	 * 
	 * @return A Map containing the MoleculeGroups in this Experiment,
	 *	indexed by group name.
	 */
	public Map <String,MoleculeGroup> getMoleculeGroupMap( ) {
		return this.moleculeGroups;
	}

	/**
	 * The MoleculeGroups for this experiement.
	 * 
	 * @return A Collection containing the MoleculeGroups in this Experiment.
	 */
	public Collection <MoleculeGroup> getMoleculeGroups( ) {
		return this.moleculeGroups.values( );
	}

	/**
	 * Gets a particular MoleculeGroup.
	 * 
	 * @param group The name of the MoleculeGroup to be retrieved.
	 * @return The MoleculeGroup requested.
	 */
	public MoleculeGroup getMoleculeGroup( String group ) {
		return this.moleculeGroups.get( group );
	}

	/**
	 * Gets an array containing the molecule group names.
	 * 
	 * @return a String array containing the names of the MoleculeGroups for this Experiment.
	 */
	public String [ ] getMoleculeGroupNames( ) {
		String [ ] returnValue = this.moleculeGroups.keySet( ).toArray( new String[ 0 ]);
		Arrays.sort( returnValue );
		return returnValue;
	}

	/**
	 * Adds a MoleculeGroup to this Experiment.
	 * 
	 * @param group The name of the group to be added.
	 */
	public void addMoleculeGroup( String group ) {
		this.moleculeGroups.put( group, new MoleculeGroup( group ));
	}

	/**
	 * Gets all attributes for this Experiment.
	 * 
	 * @return A HashMap containing all of the attributes for this experiment indexed by
	 */
	public HashMap <String,String> getAttributes( ) {
		return this.attributes;
	}

	/**
	 * Gets a particular attribute from this Experiment.
	 * 
	 * @param attr The name of the attribute to retrieve.
	 * @return A String containing the requested Attribute.
	 */
	public String getAttribute( String attr ){
		return this.attributes.get( attr );
	}

	/**
	 * Gets the names of all of the attributes for this Experiment.
	 * 
	 * @return A String array containing the attribute names.
	 */
	public String [ ] getAttributeNames( ) {
		String [ ] returnValue = this.attributes.keySet( ).toArray( new String[ 0 ]);
		Arrays.sort( returnValue );
		return returnValue;
	}

	/**
	 * Gets all Molecule Correlations present in this experiment. 
	 * 
	 * @return A List containing all of the Correlations.
	 */
	public List <Correlation> getCorrelations( ) {
		return this.correlations;
	}

	/**
	 * Returns the description of this Experiment, if any.
	 * 
	 * @return The description attribute.
	 */
	public String toString( ) {
		return String.format( "%s - %s", 
			this.getAttribute( "exp_id" ),
			this.getAttribute( "description" ));
	}

}


