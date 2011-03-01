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

import edu.purdue.cc.jsysnet.util.Experiment;

import java.util.Collection;

/**
 * Interface for data views in JSysNet.
 */
public interface DisplayPanel {

	/**
	 * Creates a graph based on the passed in experimental data.
	 * 
	 * @param experiments The Experiments to be used in creating the graph.
	 * @return Whether or not the display creation was successful.
	 */
	public boolean createView( Collection<Experiment> experiments );

	/**
	 * Returns the title of this view.
	 * 
	 * @return The title of this view as a string.
	 */
	public String getTitle( );

}
