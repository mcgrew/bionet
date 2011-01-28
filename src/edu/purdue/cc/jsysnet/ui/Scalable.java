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

import java.awt.geom.Point2D;
import javax.swing.JScrollPane;

public interface Scalable {

	/**
	 * Scales the graph view by the given amount.
	 * 
	 * @param amount The multiplier to apply to the scaling.
	 * @return The new zoom level.
	 */
	public float scale( float amount );

	/**
	 * Scales the graph view by the givem amount, centered on the 
	 * given point.
	 * 
	 * @param amount The multiplier to apply to the scaling.
	 * @param center The center point for the scaling operation.
	 * @return The new zoom level.
	 */
	public float scale( float amount, Point2D center );

	/**
	 * Scales to the given zoom level, 1.0 being 100%.
	 * 
	 * @param level The level to zoom to.
	 * @return The new zoom level.
	 */
	public float scaleTo( float level );

	/**
	 * Scales to the given zoom level, 1.0 being 100%, centered on the 
	 * given point.
	 * 
	 * @param level The level to zoom to.
	 * @param center The center point for the scaling operation.
	 * @return The new zoom level.
	 */
	public float scaleTo( float level, Point2D center );

	/**
	 * Returns a JScrollPane containing this element.
	 * 
	 * @return a JScrollPane containing this element.
	 */
	public JScrollPane getScrollPane( );
	
}


