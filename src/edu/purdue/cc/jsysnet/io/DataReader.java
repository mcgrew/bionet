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

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import edu.purdue.cc.jsysnet.util.*;

/**
 * An abstract clas for handling data input to JSysNet
 */
public abstract class DataReader {

	protected Collection<Experiment> experiments;
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
	 * @return A Collection of the experiments.
	 */
	public Collection<Experiment> getExperiments( ) {
		return this.experiments;
	}

	/**
	 * Returns the Molecules read from the data.
	 */
	public Collection<Molecule>getMolecules( ){
		Set<Molecule> returnValue = new TreeSet<Molecule>( );
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
	 * @param experiment The experiment to remove, if present.
	 * @return true if the Experiment was found and removed.
	 */
	public boolean removeExperiment( Experiment experiment ){
		return this.experiments.remove( experiment );
	}

	/**
	 * Retrieves an Experiment by Id.
	 * 
	 * @param id The id of the Experiment to retrieve.
	 * @return The requested experiment.
	 */
	public Experiment getExperiment( String id ) {
		for ( Experiment e : experiments ) {
			if ( id.equals( e.getId( ))) {
				return e;
			}
		}
		return null;
	}

}
