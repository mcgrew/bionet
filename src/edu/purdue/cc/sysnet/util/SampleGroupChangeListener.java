/*

This file is part of SysNet.

SysNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SysNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SysNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.sysnet.util;

import java.util.EventListener;

/**
 * An interface for listening for changes to the elements contained in a graph.
 */
public interface SampleGroupChangeListener<T> extends EventListener {

	/**
	 * Called when sample grouping is changed in the display panel.
	 * 
	 * @param e The SampleGroupChangeEvent which triggered this action.
	 */
	public void groupStateChanged( SampleGroupChangeEvent e );

}


