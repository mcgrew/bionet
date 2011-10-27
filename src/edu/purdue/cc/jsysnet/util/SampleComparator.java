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

import java.util.Comparator;

public class SampleComparator implements Comparator<Sample> {

	public int compare( Sample s1, Sample s2 ) {
		double s1Time = Double.parseDouble( s1.getAttribute( "time" ));
		double s2Time = Double.parseDouble( s2.getAttribute( "time" ));
		if ( s1Time < s2Time )
			return -1;
		else if ( s2Time < s1Time )
			return 1;
		else
			return s1.compareTo( s2 );
	}

}

