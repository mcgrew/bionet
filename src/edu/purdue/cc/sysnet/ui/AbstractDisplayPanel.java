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

package edu.purdue.cc.sysnet.ui;

import edu.purdue.cc.sysnet.util.SampleGroup;
import edu.purdue.cc.sysnet.util.SampleGroupChangeEvent;
import edu.purdue.cc.sysnet.util.SampleGroupChangeListener;

import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JPanel;

public abstract class AbstractDisplayPanel extends JPanel
                                           implements DisplayPanel {

	@Deprecated
	protected Collection<SampleGroup> sampleGroups;
	private ArrayList<SampleGroupChangeListener> sampleGroupChangeListeners = 
		new ArrayList<SampleGroupChangeListener>( );

	public AbstractDisplayPanel( ) {
		super( );
	}

	public AbstractDisplayPanel( LayoutManager layout ) {
		super( layout );
	}

	public void setSampleGroups( Collection<SampleGroup> sampleGroups ) {
		if ( sampleGroups != null ) {
			this.sampleGroups = sampleGroups;
			SampleGroupChangeEvent event = 
				new SampleGroupChangeEvent( this, sampleGroups );
			for ( SampleGroupChangeListener l : this.sampleGroupChangeListeners ) {
				l.groupStateChanged( event );
			}
		}
	}

	public Collection<SampleGroup> getSampleGroups( ) {
		return this.sampleGroups;
	}

	public void addSampleGroupChangeListener( SampleGroupChangeListener l ) {
		this.sampleGroupChangeListeners.add( l );
	}

	public boolean removeSampleGroupChangeListener( SampleGroupChangeListener l ) {
		return this.sampleGroupChangeListeners.remove( l );
	}

	public Collection<SampleGroupChangeListener> getSampleGroupChangeListeners( ) {
		return this.sampleGroupChangeListeners;
	}
}
