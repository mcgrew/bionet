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

package edu.purdue.cc.sysnet.ui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

/**
 * A class for creating a context menu on a component which will show
 * when the component is clicked on. By default the menu is triggered
 * by right-clicking.
 */
public class ContextMenu extends JPopupMenu implements MouseListener {
	protected Component target;
	protected int mouseButton;
	
	/**
	 * Creates a new ContextMenu which will be triggered by right-clicking on
	 * the specified component.
	 * 
	 * @param target The target Component.
	 */
	public ContextMenu( Component target ) {
		this( target, MouseEvent.BUTTON3 );
	}

	/**
	 * Creates a new ContextMenu which will be triggered by the specified click
	 * on the specified component.
	 * 
	 * @param target The target Component.
	 * @param mouseButton The mouse button which will trigger the popup. This should
	 *	be one of the MouseEvent constants.
	 */
	public ContextMenu( Component target, int mouseButton ) {
		super( );
		this.target = target;
		this.mouseButton = mouseButton;
		this.target.addMouseListener( this );
	}

	/**
	 * The mouseClicked method of the MouseListener interface. Triggers the popup
	 * if the proper button is pressed.
	 * 
	 * @param e The event which triggered this action.
	 */
	public void mouseClicked( MouseEvent e ) { 
		if ( e.getButton( ) == this.mouseButton ) {
			this.show( (Component)e.getSource( ), e.getX( ), e.getY( ));
		}
	}

	public void mouseEntered( MouseEvent e ) { }
	public void mouseExited( MouseEvent e ) { }
	public void mousePressed( MouseEvent e ) { }
	public void mouseReleased( MouseEvent e ) { }

	/**
	 * Sets a new target Component for this menu.
	 * 
	 * @param target The new target Component.
	 */
	public void setTarget( Component target ) {
		this.target.removeMouseListener( this );
		this.target = target;
		target.addMouseListener( this );
	}

	/**
	 * Gets the current target Component of this menu.
	 * 
	 * @return The current target Component.
	 */
	public Component getTarget( ) {
		return this.target;
	}

}
	
