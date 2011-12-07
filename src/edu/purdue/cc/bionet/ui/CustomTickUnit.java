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

package edu.purdue.cc.bionet.ui;


import java.util.List;

import org.jfree.chart.axis.NumberTickUnit;

public class CustomTickUnit extends NumberTickUnit {
	private List units;
	
	public CustomTickUnit( double size, List units ) {
		super( size );
		this.units = units;
	}

	@Override
	public String valueToString( double value ) {
		if ( Math.round( value ) > units.size( )-1 ){
			return "";
		}
		return this.units.get( 
			Math.max( 0, (int)Math.round( value ))).toString( );
	}

	@Override
	public String toString( ) {
		return this.valueToString( (double)this.getSize( ));
	}
}

