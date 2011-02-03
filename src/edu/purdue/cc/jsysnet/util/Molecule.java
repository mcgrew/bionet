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
	protected List <Correlation> correlations;
	protected Experiment experiment;
	protected double molecularWeight;

	/**
	 * Constructor.
	 */
	public Molecule( ){
		this.attributes = new HashMap <String,String>( );
		this.samples = new TreeMap <Sample,Number>( );
		this.correlations = new ArrayList <Correlation>( );
		this.molecularWeight = Double.NaN;
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
	 * Get the name of all attributes set for this Molecule.
	 * 
	 * @return An array of Strings containing the names of all attributes.
	 */
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
	 * @param sample The Sample this value it associated with.
	 * @param value The value of this sample;
	 */
	public void addSample( Sample sample, double value ) {
		this.addSample( sample, new Double( value ));
	}

	/**
	 * Adds a new sample value to this Molecule.
	 * 
	 * @param sample The Sample this value it associated with.
	 * @param value The value of this sample;
	 */
	public void addSample( Sample sample, Number value ) {
		if ( this.experiment != null )
			this.experiment.addSample( sample );
		this.samples.put( sample, value );
	}

	/**
	 * Returns the sample values for this Molecule as an ArrayList of Doubles
	 * 
	 * @return An ArrayList containing a Double for each sample value
	 */
	public NumberList getSamples( ){
		return new NumberList( samples.values( ));
	}

	public NumberList getSamples( Collection<Sample> samples ) {
		NumberList returnValue = new NumberList( samples.size( ));
		for( Sample sample : samples ) {
			returnValue.add( this.samples.get( sample ));
		}
		return returnValue;
	}

	public Map<Sample,Number> getSampleMap( ) {
		return this.samples;
	}

	public Number getSample( Sample sample ) {
		Number returnValue = this.samples.get( sample );
		if ( returnValue == null )
			return new Double( Double.NaN );
		return returnValue;
	}

	public Number getSample( String sample ) {
		for ( Map.Entry<Sample,Number> sampleEntry : this.samples.entrySet( )) {
			if ( sampleEntry.getKey( ).toString( ).equals( sample )) {
				return sampleEntry.getValue( );
			}
		}
		return new Double( Double.NaN );
	}

	/**
	 * Sets the Experiement this Molecule belongs to.
	 * 
	 * @param experiment The experiment this Molecule belongs to.
	 */
	public void setExperiment( Experiment experiment ){
		//ToDo: make sure this Molecule actually has the group_name attribute first.
		experiment.addMolecule( this.getAttribute( "group_name" ), this );
		this.experiment = experiment;
	}
	/**
	 * Gets the Experiment this Molecule belongs to.
	 * 
	 * @return An Experiment object containing the experiment this Molecule 
	 *	belongs to.
	 */
	public Experiment getExperiment( ){
		return this.experiment;
	}

	/**
	 * Sets the group name this Molecule belongs to.
	 * 
	 * @param group A string containing the group name for this Molecule.
	 */
	public void setGroup( String group ) {
		this.setAttribute( "group_name", group );
	}

	/**
	 * Gets the group name this Molecule belongs to.
	 * 
	 * @return A String containing the group name for this Molecule.
	 */
	public String getGroup( ) {
		return this.getAttribute( "group_name" );
	}

	/**
	 * Returns the MoleculeGroup this Molecule belongs to.
	 * 
	 * @return The MoleculeGroup which contains this Molecule, or null if no
	 *	such group exists.
	 */
	public MoleculeGroup getMoleculeGroup( ) {
		for ( MoleculeGroup m : this.experiment.getMoleculeGroups( )) {
			if ( m.contains( this ))
				return m;
		}
		return null;
	}

	/**
	 * Adds a Correlation to this Molecule
	 * 
	 * @param correlation The new correlation to add to this Molecule.
	 * @return True if the operation is successful, false otherwise.
	 */
	public boolean addCorrelation( Correlation correlation ) {
		this.correlations.add( correlation );
		return true;
	}
	/**
	 * Remove a correlation from this Molecule. 
	 * 
	 * @param index The index of the correlation to be removed.
	 * @return True if the operation is successful, false otherwise.
	 */
	public boolean removeCorrelation( int index ) {
		return false;
	}
	/**
	 * Remove a correlation from this Molecule.
	 * 
	 * @param correlation The Correlation to be removed from this Molecule.
	 * @return True if the operation is successful, false otherwise.
	 */
	public boolean removeCorrelation( Correlation correlation ) {
		return false;
	}

	public List <Correlation> getCorrelations( ) {
		return this.correlations;
	}

	/**
	 * Gets a Correlation which is has the passed in Molecule as it's other
	 * Molecule.
	 * 
	 * @param molecule The Molecule for which the appropriate Correlation is to
	 *	be retrieved.
	 * @return The Correlation if it is found, or null if not.
	 */
	public Correlation getCorrelation( Molecule molecule ) {
		for( Correlation correlation : this.correlations ) {
			if ( correlation.getOpposite( this ) == molecule )
				return correlation;
		}
		return null;
	}

	/**
	 * Gets all correlations attached to this Molecule.
	 * 
	 * @return An ArrayList containing all of the Correlations for this Molecule.
	 */
	public ArrayList <Molecule> getCorrelated( ) {
		ArrayList <Molecule> returnValue = new ArrayList<Molecule>( );
		for ( Correlation correlation : this.correlations ) {
			Molecule[] molecules = correlation.getMolecules( );
			returnValue.add(( molecules[ 1 ] == this ) ? 
			                  molecules[ 0 ] : molecules[ 1 ]);
		}
		return returnValue;
	}

	public String toString( ) {
		return this.getAttribute( "id" );
	}

	public int compareTo( Molecule m ) {
		int returnValue = 0;
		if ( returnValue == 0 ) {
			returnValue = this.getGroup( ).compareTo( 
				m.getGroup( ));
		}
		if ( returnValue == 0 ) {
			returnValue = this.getAttribute( "id" ).compareTo( 
				m.getAttribute( "id" ));
		}
		if ( returnValue == 0 ) {
			returnValue = this.getExperiment( ).compareTo( 
					m.getExperiment( ));
		}
		return returnValue;
	}

	public boolean equals( Object m ) {
		if ( m instanceof Molecule )
			return ( this.compareTo((Molecule)m ) == 0 );
		return this.toString( ).equals( m.toString( ));
	}
}

