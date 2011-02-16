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

package edu.purdue.cc.jsysnet.ui;

import edu.purdue.cc.jsysnet.util.Sample;

import java.util.List;

import org.jfree.chart.axis.NumberTickUnit;

public class SampleTickUnit extends NumberTickUnit {
	private List<Sample> samples;
	
	public SampleTickUnit( double size, List<Sample> samples ) {
		super( size );
		this.samples = samples;
	}

	@Override
	public String valueToString( double value ) {
		if ( Math.round( value ) > samples.size( )-1 ){
			return "";
		}
		return this.samples.get( 
			Math.max( 0, (int)Math.round( value ))).toString( );
	}

	@Override
	public String toString( ) {
		return this.valueToString( this.getSize( ));
	}
}

