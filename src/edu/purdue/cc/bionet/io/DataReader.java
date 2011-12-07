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

package edu.purdue.cc.bionet.io;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import edu.purdue.cc.bionet.util.*;

/**
 * An abstract clas for handling data input to BioNet
 */
public abstract class DataReader {

	protected Project project;
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
	 * @deprecated This has been replaced by the getProject( ) method.
	 * @return A Collection of the experiments.
	 */
	@Deprecated
	public Collection<Experiment> getExperiments( ) {
		return this.project.iterator( ).next( );
	}

	/**
	 * Returns the Project read from the data.
	 * 
	 * @return The Project.
	 */
	public Project getProject( ) {
		return this.project;
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
	 * @param experiments The Experiment to add.
	 */
	public void addExperimentSet( ExperimentSet experiments ) {
		this.project.add( experiments );
	}

	/**
	 * Removes an Experiment from the data set.
	 * 
	 * @param experiments The experiment to remove, if present.
	 * @return true if the Experiment was found and removed.
	 */
	public boolean removeExperimentSet( ExperimentSet experiments ){
		return this.project.remove( experiments );
	}

	/**
	 * Retrieves an Experiment by Id.
	 * 
	 * @param name The name of the Experiment to retrieve.
	 * @return The requested experiment.
	 */
	public ExperimentSet getExperimentSet( String name ) {
		for ( ExperimentSet e : project ) {
			if ( name.equals( e.getName( ))) {
				return e;
			}
		}
		return null;
	}

}
