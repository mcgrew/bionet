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

package edu.purdue.jsysnet.ui;

/**
 * Interface class for listening for PickedStateChangeEvents on a graph.
 */
public interface PickedStateChangeListener <T> {

	/**
	 * Called when the picked state has changed.
	 * 
	 * @param event The event which triggered this action.
	 */
	void stateChanged( PickedStateChangeEvent <T> event );
	
}

