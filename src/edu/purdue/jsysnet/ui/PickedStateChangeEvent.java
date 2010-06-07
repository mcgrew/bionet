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

import java.awt.Component;

/**
 * An event class for Changes in the picked state of a graph.
 */
public class PickedStateChangeEvent <T> {
	private Component source;
	private T item;
	private boolean stateChange;

	/**
	 * Creates a new PickedStateChangeEvent
	 * 
	 * @param source The Component who triggered this event.
	 * @param item The item whose state has changed.
	 * @param stateChange true if the item is now selected.
	 */
	public PickedStateChangeEvent( Component source, T item, boolean stateChange ) {
		this.source = source;
		this.item = item;
		this.stateChange = stateChange;
	}

	/**
	 * Returns the source Component of this event.
	 * 
	 * @return The source Component of this event.
	 */
	public Component getSource( ) {
		return this.source;
	}

	/**
	 * Returns the item whose state has changed.
	 * 
	 * @return The item whose state has changed.
	 */
	public T getItem( ) {
		return this.item;
	}

	/**
	 * Returns the new state of the item (selected or deselected).
	 * 
	 * @return True if the new state is selected, false otherwise.
	 */
	public boolean getStateChange( ) {
		return this.stateChange;
	}
		

}
