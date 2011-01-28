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

package edu.purdue.cc.jsysnet.io;

import java.util.List;
import java.util.ArrayList;
import edu.purdue.cc.jsysnet.util.*;

/**
 * An abstract clas for handling data input to JSysNet
 */
public abstract class DataReader {

	protected ArrayList <Experiment> experiments;
	protected String resource;

	/**
	 * Creates a new DataReader.
	 */
	protected DataReader( ) { }

	/**
	 * Creates a new DataReader.
	 * 
	 * @param resource A string specifying the resource to load.
	 */
	protected DataReader( String resource ) {
		this.resource = resource;
	}

	/**
	 * Loads the data.
	 *
	 * @param resource A string indicating the resource to be loaded.
	 */
	public void load( String resource ){
		this.resource = resource;
		this.load( );
	}

	/**
	 * Loads the data.
	 */
	public abstract void load( );

	/**
	 * Returns the Experiments read from the data.
	 * 
	 * @return A List of the experiments.
	 */
	public List <Experiment> getExperiments( ) {
		return this.experiments;
	}

	/**
	 * Returns the Molecules read from the data.
	 */
	public List <Molecule>getMolecules( ){
		ArrayList <Molecule> returnValue = new ArrayList<Molecule>( );
		for ( Experiment e : this.getExperiments( )) {
			returnValue.addAll( e.getMolecules( ));
		}
		return returnValue;
	}

	/**
	 * Adds an Experiment to the data set.
	 * 
	 * @param experiment The Experiment to add.
	 */
	public void addExperiment( Experiment experiment ) {
		this.experiments.add( experiment );
	}

	/**
	 * Removes an Experiment from the data set.
	 * 
	 * @param index The index of the Experiment to remove.
	 * @return The Experiment which was removed.
	 */
	public Experiment removeExperiment( int index ) {
		return this.experiments.remove( index );
	}

	/**
	 * Removes an Experiment from the data set.
	 * 
	 * @param experiment The experiment to remove, if present.
	 * @return true if the Experiment was found and removed.
	 */
	public boolean removeExperiment( Experiment experiment ){
		return this.experiments.remove( experiment );
	}

}
