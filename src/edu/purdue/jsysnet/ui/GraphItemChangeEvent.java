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

import java.util.EventObject;

public class GraphItemChangeEvent<T> extends EventObject {
	public static final int REMOVED = 0;
	public static final int ADDED = 1;
	private T item;
	private int action;

	public GraphItemChangeEvent( Object source, T item, int action ) {
		super( source );
		this.item = item;
		this.action = action;
	}

	public T getItem( ) {
		return item;
	}

	public int getAction( ) {
		return action;
	}

}

