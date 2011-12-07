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

import edu.purdue.bbc.util.Range;

import java.util.ArrayList;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class MonitorableRange extends Range {
	private ArrayList <ChangeListener> changeListeners = new ArrayList<ChangeListener>( );
	
	public MonitorableRange( ) {
		super( );
	}

	public MonitorableRange( int min, int max ) {
		super( min, max );
	}

	public MonitorableRange( float min, float max ) {
		super( min, max );
	}

	public MonitorableRange( double min, double max ) {
		super( min, max );
	}

	public MonitorableRange( Range range ) {
		super( range.getMin( ), range.getMax( ));
	}

	public void addChangeListener( ChangeListener c ) {
		changeListeners.add( c );
	}

	public ChangeListener [] getChangeListeners( ) {
		return changeListeners.toArray( 
			new ChangeListener[ changeListeners.size( )]);
	}
	
	public boolean removeChangeListener( ChangeListener c ) {
		return changeListeners.remove( c );
	}

	private void fireChangeListeners( ) {
		for( ChangeListener c : changeListeners ) {
			c.stateChanged( new ChangeEvent( this ));
		}
	}

	public void setMin( double min ) {
		super.setMin( min );
		this.fireChangeListeners( );

	}

	public void setMax( double max ) {
		super.setMax( max );
		this.fireChangeListeners( );
	}

	public void setRange( double min, double max ) {
		super.setRange( min, max );
		this.fireChangeListeners( );
	}
}
