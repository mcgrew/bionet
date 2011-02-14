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

import edu.purdue.bbc.util.Attributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Sample implements Comparable<Sample>,Attributes<String>,Cloneable {
	protected Map<String,String> attributes;
	protected String name;
	protected Map<Molecule,Number> valueMap;

	public Sample( String name ) {
		this.name = name;
		this.attributes = new HashMap<String,String>( );
		this.valueMap = new HashMap<Molecule,Number>( );
	}

	public String toString( ) {
		return name;
	}

	public void setAttribute( String attribute, String value ) {
		this.attributes.put( attribute.toLowerCase( ), value );
	}

	public void setAttributes( Map<String,String> attributes ) {
		for ( Map.Entry<String,String> attribute : attributes.entrySet( )) {
			this.attributes.put( 
				attribute.getKey( ).toLowerCase( ), attribute.getValue( ) );
		}
	}

	public String getAttribute( String attribute ) {
		return this.attributes.get( attribute.toLowerCase( ));
	}

	public String removeAttribute( String attribute ) {
		return this.attributes.remove( attribute );
	}

	public int compareTo( Sample o ) {
		return this.name.compareTo( o.toString( ));
		
	}

	public boolean equals( Sample o ) {
		return ( this.compareTo( o ) == 0 );
	}

	public void setValue( Molecule molecule, Number value ) {
		this.valueMap.put( molecule, value );
	}

	public Number getValue( Molecule molecule ) {
		Number returnValue = this.valueMap.get( molecule );
		if ( returnValue == null )
			return new Double( Double.NaN );
		return returnValue;
	}

	public Molecule getMolecule( Object id ) {
		for ( Molecule molecule : this.valueMap.keySet( )) {
			if ( id.toString( ).equals( molecule.getId( )))
				return molecule;
		}
		return null;
	}

	public Collection <Molecule> getMolecules( ) {
		return this.valueMap.keySet( );
	}

	@Override
	public Sample clone( ) {
		Sample returnValue = new Sample( this.name );
		returnValue.setAttributes( this.attributes );
		for ( Map.Entry<Molecule,Number> entry : this.valueMap.entrySet( )) {
			returnValue.setValue( entry.getKey( ), entry.getValue( ));
		}
		return returnValue;
	}
}

