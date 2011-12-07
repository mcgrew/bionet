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

import java.util.EventObject;

/**
 * A class used to notify GraphItemChangeListeners that there has been
 * a change in the elements contained in a graph.
 */
public class GraphItemChangeEvent<T> extends EventObject {
	public static final int REMOVED = 0;
	public static final int ADDED = 1;
	private T item;
	private int action;

	/**
	 * Creates a new GraphItemChangeEvent
	 * 
	 * @param source The Graph which triggered the event.
	 * @param item the item which was added or removed.
	 * @param action The action which occurred. One of 
	 * GraphItemChangeEvent.REMOVED or GraphItemChangeEvent.ADDED
	 */
	public GraphItemChangeEvent( Object source, T item, int action ) {
		super( source );
		this.item = item;
		this.action = action;
	}

	/**
	 * Returns The item associated with this event.
	 * 
	 * @return The item associated with this event.
	 */
	public T getItem( ) {
		return item;
	}

	/**
	 * Returns the action type which occurred. Should be one of 
	 *	GraphItemChangeEvent.REMOVED or GraphItemChangeEvent.ADDED
	 * 
	 * @return The action type which occurred.
	 */
	public int getAction( ) {
		return action;
	}

}

