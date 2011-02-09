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
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Set;

import edu.purdue.bbc.util.NumberList;

/**
 * Representation class for Molecule data
 * 
 * @author Thomas McGrew
 */
public class Molecule implements Comparable<Molecule> {
	
	protected Map <String,String> attributes;
	protected SortedMap <Sample,Number> samples;
	protected Experiment experiment;
	protected double molecularWeight;
	protected String id;

	/**
	 * Constructor.
	 * @deprecated This constructor will be removed in a future version.
	 */
	@Deprecated
	public Molecule( ) {
		this.attributes = new HashMap <String,String>( );
		this.molecularWeight = Double.NaN;
	}

	/**
	 * Constructs a new Molecule with the given id.
	 * 
	 * @param id The id of the new Molecule
	 */
	public  Molecule( String id ) {
		this.attributes = new HashMap <String,String>( );
		this.molecularWeight = Double.NaN;
		this.id = id;
	}

	/**
	 * Gets the id of this Molecule.
	 * 
	 * @return A String containing the Molecule's id.
	 */
	public String getId( ) {
		if ( this.id == null ) {
			return this.getAttribute( "id" );
		}
		return this.id;
	}

	/**
	 * Gives this Molecule a new attribute.
	 * 
	 * @param attribute The name of the attribute to be set.
	 * @param value The value for the Attribute. 
	 */
	public void setAttribute( String attribute, String value ) {
		this.attributes.put( attribute.toLowerCase( ).trim( ), value );
	}

	/**
	 * Retrieve an Attribute for this Molecule.
	 * 
	 * @param attribute The attribute to be retrieved.
	 * @return The value of the attribute as a String.
	 */
	public String getAttribute( String attribute ) {
		return this.attributes.get( attribute.toLowerCase( ).trim( ));
	}

	/**
	 * Returns all attributes for this Molecule.
	 * 
	 * @return A Map containing all meta attributes.
	 */
	public Map <String,String> getAttributes( ) {
		return this.attributes;
	}

	/**
	 * Get the name of all attributes set for this Molecule.
	 * 
	 * @deprecated This method will be removed in a future version. Use
	 * getAttributes( ) instead.
	 * @return An array of Strings containing the names of all attributes.
	 */
	@Deprecated
	public String [] getAttributeNames( ){
		String [] returnValue = this.attributes.keySet( ).toArray( new String[ 0 ]);
		Arrays.sort( returnValue );
		return returnValue;
	}
	 
	/**
	 * Sets the &quot;name&quot; attribute for the Molecule.
	 * This method is deprecated; use setAttribute( &quot;name&quot; ) instead.
	 * 
	 * @param name The value for the &quot;name&quot; attribute.
	 */
	@Deprecated
	public void setName( String name ){
		this.setAttribute( "name", name );
	}

	/**
	 * Gets the &quot;name&quot; attribute for the Molecule.
	 * This method is deprecated; use getAttribute( &quot;name&quot; ) instead.
	 * 
	 * @return A string containing the &quot;name&quot; attribute for the 
	 *	Molecule.
	 */
	@Deprecated
	public String getName( ){
		return this.getAttribute( "name" );
	}

	/**
	 * Sets the molecular weight of the molecule;
	 * 
	 * @param mw The molecular weight of the molecule;
	 */
	public void setMolecularWeight( double mw ) {
		this.molecularWeight = mw;
	}

	/**
	 * Gets the molecularWeight attribute for the Molecule.
	 * 
	 * @return An int containing the &quot;molecularWeight&quot; attribute.
	 */
	public double getMolecularWeight( ){
		return this.molecularWeight;
	}

	/**
	 * Sets the &quot;Formula&quot; attribute for the Molecule.
	 * 
	 * @deprecated This method is deprecated; use 
	 *	setAttribute( &quot;formula&quot;, value ) instead.
	 * @param formula A string containing the new &quot;formula&quot; attribute.
	 */
	@Deprecated
	public void setFormula( String formula ){
		this.setAttribute( "formula", formula );
	}
	/**
	 * Gets the &quot;formula&quot; attribute for the module.
	 * 
	 * @deprecated This method is deprecated; use 
	 *	getAttribute( &quot;formula&quot; ) instead.
	 * @return A string containing the &quot;formula&quot; attribute for the 
	 *	Molecule.
	 */
	@Deprecated
	public String getFormula( ){
		return this.getAttribute( "formula" );
	}

	/**
	 * Adds a new sample value to this Molecule.
	 * 
	 * @deprecated This method is being replaced by setValue( Sample, double )
	 * @param sample The Sample this value it associated with.
	 * @param value The value of this sample;
	 */
	@Deprecated
	public void addSample( Sample sample, double value ) {
		this.addSample( sample, new Double( value ));
	}

	/**
	 * Adds a new sample value to this Molecule.
	 * 
	 * @param sample The Sample this value it associated with.
	 * @param value The value of this sample;
	 */
	public void setValue( Sample sample, double value ) {
		this.addSample( sample, new Double( value ));
	}

	/**
	 * Adds a new sample value to this Molecule.
	 * 
	 * @deprecated This method is being replaced by setValue( Sample, Number )
	 * @param sample The Sample this value it associated with.
	 * @param value The value of this sample;
	 */
	public void addSample( Sample sample, Number value ) {
		this.setValue( sample, value );
	}

	/**
	 * Adds a new sample value to this Molecule.
	 * 
	 * @param sample The Sample this value it associated with.
	 * @param value The value of this sample;
	 */
	public void setValue( Sample sample, Number value ) {
		sample.setValue( this, value );
	}

	/**
	 * Returns the sample value for the appropriate Samples
	 * 
	 * deprecated This method is being replaced by getValues( Sample ).
	 * @param samples The samples to get the values for.
	 * @return The sample values.
	 */
	@Deprecated
	public NumberList getSamples( Collection<Sample> samples ) {
		return this.getValues( samples );
	}

	/**
	 * Returns the sample value for the appropriate Samples
	 * 
	 * @param samples The samples to get the values for.
	 * @return The sample values.
	 */
	public NumberList getValues ( Collection<Sample> samples ) {
		NumberList returnValue = new NumberList( samples.size( ));
		for( Sample sample : samples ) {
			returnValue.add( sample.getValue( this ));
		}
		return returnValue;
	}

	public Map<Sample,Number> getSampleMap( Collection<Sample> samples ) {
		Map<Sample,Number> returnValue = new TreeMap<Sample,Number>( );
		for ( Sample sample : samples ) {
			returnValue.put( sample, sample.getValue( this ));
		}
		return returnValue;
	}

	@Deprecated
	public Number getSample( Sample sample ) {
		return this.getValue( sample );
	}

	public Number getValue( Sample sample ) {
		return sample.getValue( this );
	}

	/**
	 * Sets the group name this Molecule belongs to.
	 * 
	 * @deprecated Molecule groups will be removed from a future version.
	 * @param group A string containing the group name for this Molecule.
	 */
	@Deprecated
	public void setGroup( String group ) {
		this.setAttribute( "group_name", group );
	}

	/**
	 * Gets the group name this Molecule belongs to.
	 * 
	 * @deprecated Molecule groups will be removed from a future version.
	 * @return A String containing the group name for this Molecule.
	 */
	@Deprecated
	public String getGroup( ) {
		return this.getAttribute( "group_name" );
	}

	/**
	 * Returns the MoleculeGroup this Molecule belongs to.
	 * 
	 * @deprecated The MoleculeGroup class will be removed from a future version.
	 * @return The MoleculeGroup which contains this Molecule, or null if no
	 *	such group exists.
	 */
	@Deprecated
	public MoleculeGroup getMoleculeGroup( ) {
		for ( MoleculeGroup m : this.experiment.getMoleculeGroups( )) {
			if ( m.contains( this ))
				return m;
		}
		return null;
	}

	public String toString( ) {
		return this.getId( );
	}

	public int compareTo( Molecule m ) {
		int returnValue = this.getId( ).compareTo( m.getId( ));
		return returnValue;
	}

	public boolean equals( Object m ) {
		if ( m instanceof Molecule )
			return ( this.compareTo((Molecule)m ) == 0 );
		return this.toString( ).equals( m.toString( ));
	}
}

