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

public class ContextMenu extends JPopupMenu implements MouseListener {
	private Component component;
	
	public ContextMenu( Component component ) {
		this.component = component;
		component.addMouseListener( this );
	}

	public void mouseClicked( MouseEvent e ) { 
		if ( event.getButton( ) == MouseEvent.BUTTON2 ) {
			this.show( this.component, e.getX( ), e.getY( ));
		}
	}

	public void mouseEntered( MouseEvent e ) { }
	public void mouseExited( MouseEvent e ) { }
	public void mousePressed( MouseEvent e ) { }
	public void mouseReleased( MouseEvent e ) { }

}
	
