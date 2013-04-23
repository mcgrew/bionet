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

import edu.purdue.bbc.util.attributes.Attributes;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Project extends TreeSet<ExperimentSet> 
                     implements Attributes<String> {
	private Map<String,String> attributes;
	private Set<Sample> samples;
	private File resource;

	@Deprecated
	public Project( ) {
		this( (File)null );
	}

	public Project( File resource ) {
		super( );
		this.resource = resource;
		this.attributes = new TreeMap<String,String>( );
		this.samples = new TreeSet<Sample>( );
	}

	public Project( Project project ) {
		this( );
		this.addAll( project );
		this.attributes = new TreeMap<String,String>( );
		this.samples = new TreeSet<Sample>( );
		this.setAttributes( project.getAttributes( ));
	}

	public Collection<String> getSampleAttributeNames( ) {
		Collection<String> returnValue = new TreeSet<String>( );
		for ( Sample s : this.samples ) {
			returnValue.addAll( s.getAttributes( ).keySet( ));
		}
		return returnValue;
	}

	public boolean addSample( Sample sample ) {
		return this.samples.add( sample );
	}

	public void addSamples( Collection<Sample> samples ) {
		this.samples.addAll( samples );
	}

	public void setSamples( Collection<Sample> samples ) {
		this.samples = new TreeSet( samples );
	}

	public Set<Sample> getSamples( ) {
		return this.samples;
	}

	public Sample getSample( String id ) {
		for ( Sample sample : this.samples ) {
			if ( sample.toString( ).equals( id ))
				return sample;
		}
		return null;
	}

	@Override
	public boolean add( ExperimentSet experiments ) {
		if ( !experiments.isLoaded( )) {
			experiments.addSamples( this.samples );
		}
		return super.add( experiments );
	}

	@Override
	public boolean addAll( Collection<? extends ExperimentSet> experimentSets ) {
		for ( ExperimentSet experiments : experimentSets ) {
			if ( !experiments.isLoaded( )) {
				
			}
		}
		return super.addAll( experimentSets );
	}

	public File getResource( ) {
		return this.resource;
	}

	public void setResource( File resource ) {
		this.resource = resource;
	}

	/**
	 * Gets an attribute for this object.
	 * 
	 * @param attribute The attribute to retrieve.
	 * @return The value of the requested attribute, or null if it does not exist.
	 */
	public String getAttribute( String attribute ) {
		attribute = attribute.toLowerCase( );
		return this.attributes.get( attribute );
	}

	/**
	 * Gets an attribute for this object.
	 * 
	 * @param attribute The attribute to retrieve.
   * @param default The value to return if the attribute is not set.
	 * @return The value of the requested attribute, or null if it does not exist.
	 */
	public String getAttribute( String attribute, String defaultValue ) {
		attribute = attribute.toLowerCase( );
    String returnValue = this.attributes.get(attribute);
    if (returnValue == null)
      return defaultValue;
		return returnValue;
	}

	/**
	 * Gets the attributes of this object as a Map.
	 * 
	 * @return A Map containing the attributes of this object.
	 */
	public Map<String,String> getAttributes( ) {
		return this.attributes;
	}

	/**
	 * Determines if this object has the requested attribute.
	 * 
	 * @param attribute The attribute to check for.
	 * @return A boolean indicating whether or not the object has the requested
	 *	attribute.
	 */
	public boolean hasAttribute( String attribute ) {
		attribute = attribute.toLowerCase( );
		return this.attributes.containsKey( attribute );
	}

	/**
	 * Sets an attribute for this object.
	 * 
	 * @param attribute The attribute to set.
	 * @param value The new value for the specified attribute.
	 */
	public String setAttribute( String attribute, String value ) {
		attribute = attribute.toLowerCase( );
		return this.attributes.put( attribute, value );
	}

	/**
	 * Sets multiple Attributes for this object
	 * 
	 * @param map A map containing all of the attributes to be set.
	 */
	public void setAttributes( Map<String,String> map ) {
		for ( Map.Entry<String,String> attribute : map.entrySet( )) {
			this.attributes.put( attribute.getKey( ).toLowerCase( ), 
													 attribute.getValue( ));
		}
	}

	/**
	 * Sets multiple Attributes for this object
	 * 
	 * @param attributes An attributes object containing all of the attributes 
	 *	to be set.
	 */
	public void setAttributes( Attributes<String> attributes ) {
		for ( Map.Entry<String,String> attribute : 
			    attributes.getAttributes( ).entrySet( )) {
			this.attributes.put( attribute.getKey( ).toLowerCase( ), 
													 attribute.getValue( ) );
		}
	}

	/**
	 * Removes the specified attribute and returns it's value.
	 * 
	 * @param attribute The attribute to remove
	 * @return The value of the requested attribute, or null if it does not exist.
	 */
	public String removeAttribute( String attribute ) {
		attribute = attribute.toLowerCase( );
		return this.attributes.remove( attribute );
	}
}

