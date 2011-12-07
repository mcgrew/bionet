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

import edu.purdue.cc.bionet.util.SampleGroup;
import edu.purdue.cc.bionet.util.SampleGroupChangeEvent;
import edu.purdue.cc.bionet.util.SampleGroupChangeListener;

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
