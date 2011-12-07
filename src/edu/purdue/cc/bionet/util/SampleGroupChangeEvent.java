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

package edu.purdue.cc.bionet.util;

import java.util.Collection;
import javax.swing.event.ChangeEvent;

/**
 * A class used to notify SampleGroupChangeListeners that there has been
 * a change in the elements contained in a graph.
 */
public class SampleGroupChangeEvent extends ChangeEvent {
	private Collection<SampleGroup> groups;

	/**
	 * Creates a new SampleGroupChangeEvent
	 * 
	 * @param source The Graph which triggered the event.
	 * @param groups The new Collection of groups.
	 */
	public SampleGroupChangeEvent( Object source, 
	                                  Collection<SampleGroup> groups ) {
		super( source );
		this.groups = groups;
	}

	/**
	 * Returns The item associated with this event.
	 * 
	 * @return The item associated with this event.
	 */
	public Collection<SampleGroup> getGroups( ) {
		return groups;
}}


