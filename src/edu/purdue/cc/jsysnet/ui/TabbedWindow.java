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

import java.awt.Component;

/**
 * An interface for dealing with generic classes which extend JFrame and 
 * contain a JTabbedPane
 */
public interface TabbedWindow {

	/**
	 * A method for adding a new tab to the window.
	 * 
	 * @param title The title for the tab.
	 * @param c The component to add to the tab.
	 */
	public void addTab( String title, Component c );

	/**
	 * Creates a new instance of this JFrame with an enpty JTabbedPane.
	 * 
	 * @return A new instance of this class casted as a TabbedWindow.
	 */
	public TabbedWindow newWindow( );

}

