/*

This file is part of SysNet.

SysNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SysNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SysNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.sysnet.util;

import edu.purdue.bbc.util.attributes.Attributes;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Project extends TreeSet<Experiment> implements Attributes<String> {
	private Map<String,String> attributes;
	private Set<Sample> samples;

	public Project( ) {
		super( );
		this.attributes = new TreeMap<String,String>( );
		this.samples = new TreeSet<Sample>( );
	}

	public Project( Collection<Experiment> experiments ) {
		this( );
		this.addAll( experiments );
		if ( experiments instanceof Project )
			this.setAttributes( ((Project)experiments).getAttributes( ));
	}

	public Collection<String> getSampleAttributeNames( ) {
		Collection<String> returnValue = new TreeSet<String>( );
		for ( Sample s : this.samples ) {
			returnValue.addAll( s.getAttributes( ).keySet( ));
		}
		return returnValue;
	}

	public Set<Sample> getSamples( ) {
		return this.samples;
	}

	@Override
	public boolean add( Experiment experiment ) {
		this.samples.addAll( experiment.getSamples( ));
		return super.add( experiment );
	}

	@Override
	public boolean addAll( Collection<? extends Experiment> experiments ) {
		for ( Experiment experiment : experiments ) {
			this.samples.addAll( experiment.getSamples( ));
		}
		return super.addAll( experiments );
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

