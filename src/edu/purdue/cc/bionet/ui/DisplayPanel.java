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

import edu.purdue.cc.bionet.util.ExperimentSet;

/**
 * Interface for data views in BioNet.
 */
public interface DisplayPanel {

	/**
	 * Creates a graph based on the passed in experimental data.
	 * 
	 * @param experiments The Experiments to be used in creating the graph.
	 * @return Whether or not the display creation was successful.
	 */
	public boolean createView( ExperimentSet experiment );

	/**
	 * Returns the title of this view.
	 * 
	 * @return The title of this view as a string.
	 */
	public String getTitle( );

}
