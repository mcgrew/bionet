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

import java.util.EventListener;

/**
 * An interface for listening for changes to the elements contained in a graph.
 */
public interface GraphItemChangeListener<T> extends EventListener {

	/**
	 * Called when the elements of type T contained in a GraphVisualizer change.
	 * 
	 * @param e The GraphItemChangeEvent which triggered this action.
	 */
	public void stateChanged( GraphItemChangeEvent<T> e );

}

