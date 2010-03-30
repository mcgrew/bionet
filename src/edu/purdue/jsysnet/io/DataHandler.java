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

package edu.purdue.jsysnet.io;

import java.util.ArrayList;
import edu.purdue.jsysnet.util.*;

public abstract class DataHandler {

	protected ArrayList <Experiment> experiments;

	public abstract void load( String resource );

	public abstract boolean write( );

	public abstract boolean write( String resource );

	public ArrayList <Experiment> getExperiments( ) {
		return this.experiments;
	}

	public ArrayList <Molecule>getMolecules( ){
		ArrayList <Molecule> returnValue = new ArrayList<Molecule>( );
		for ( Experiment e : this.getExperiments( )) {
			returnValue.addAll( e.getMolecules( ));
		}
		return returnValue;
	}

	public void addExperiment( Experiment experiment ) {
		this.experiments.add( experiment );
	}

	public Experiment removeExperiment( int index ) {
		return this.experiments.remove( index );
	}

	public boolean removeExperiment( Experiment experiment ){
		return this.experiments.remove( experiment );
	}

}
