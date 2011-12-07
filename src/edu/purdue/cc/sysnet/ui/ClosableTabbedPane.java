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
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Icon;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.Language;

/**
 * A class for adding the ability to close tabs and move tabs to a new window.
 */
public class ClosableTabbedPane extends JTabbedPane implements ActionListener,MouseListener {
	protected boolean tearOffEnabled = true;
	protected TabPopup tabPopup = new TabPopup( );
	
	public void addTab( String title, Component component ) {
		this.addTab( title, component, true );
	}
		
	/**
	 * Adds a component represented by a title and no icon.
	 * 
	 * @param title the title to be displayed in this tab.
	 * @param component the component to be displayed when this tab is clicked.
	 * @param focus Whether or not the new tab should be given focus.
	 */
	public void addTab( String title, Component component, 
	                    boolean closable, boolean focus ) {
		super.addTab( title, component );
		if ( closable )
			this.makeClosable( this.getTabCount( )-1 );
		if ( focus )
			this.setSelectedComponent( component );
	}

	public void addTab( String title, Component component, boolean closable ) {
			this.addTab( title, component, closable, true );
	}

	public void addTab( String title, Icon icon, Component component ) {
			this.addTab( title, component, true, true );
	}
		
	/**
	 * Adds a component represented by a title and/or icon, either of which can be null. 
	 * 
	 * @param title the title to be displayed in this tab.
	 * @param icon the icon to be displayed in this tab.
	 * @param component the component to be displayed when this tab is clicked.
	 */
	public void addTab( String title, Icon icon, Component component, boolean closable ) {
		super.addTab( title, icon, component );
		if ( closable )
			this.makeClosable( this.getTabCount( )-1 );
	}

	public void addTab( String title, Icon icon, Component component, String tip ) {
		this.addTab( title, icon, component, tip, true, true );
	}
		
	/**
	 * Adds a component and tip represented by a title and/or icon, either of which can be null.
	 * 
	 * @param title the title to be displayed in this tab
	 * @param icon the icon to be displayed in this tab
	 * @param component the component to be displayed when this tab is clicked.
	 */
	public void addTab( String title, Icon icon, Component component, String tip, 
	                    boolean closable ) {
		this.addTab( title, icon, component, tip, closable, true );
	}

	/**
	 * Adds a component and tip represented by a title and/or icon, either of which can be null.
	 * 
	 * @param title the title to be displayed in this tab
	 * @param icon the icon to be displayed in this tab
	 * @param component the component to be displayed when this tab is clicked.
	 * @param focus Whether or not the new tab should be given focus.
	 */
	public void addTab( String title, Icon icon, Component component, String tip, 
	                    boolean closable, boolean focus ) {
		super.addTab( title, icon, component, tip );
		if ( closable )
			this.makeClosable( this.getTabCount( )-1 );
		if ( focus )
			this.setSelectedComponent( component );
	}

	/**
	 * Makes a tab closable.
	 * 
	 * @param index The index of the tab to make closable.
	 */
	private void makeClosable( int index ) {
		this.makeClosable( index, true );
	}

	/**
	 * Makes a tab closable.
	 * 
	 * @param index the index of the tab to make closable.
	 * @param closable true to make the tab closable, false otherwise. Currently 
	 *	ignored.
	 */
	private void makeClosable( int index, boolean closable ) {
		JPanel tabComponent = new JPanel( new BorderLayout( ));
		JLabel tabLabel = new JLabel( this.getTitleAt( index ));
		tabComponent.add( tabLabel, BorderLayout.CENTER );
		TabCloseButton button = new TabCloseButton( );
		tabComponent.add( button, BorderLayout.EAST );
		this.setTabComponentAt( index, tabComponent );
		button.addActionListener( this );
		if ( tearOffEnabled )
			this.enableTearOff( index );
	}

	/**
	 * Enables the ability to move a tab to a new window.
	 * 
	 * @param index The index of the tab to enable tear off on.
	 */
	private void enableTearOff( int index ) {
		this.getTabComponentAt( index ).addMouseListener( this );
	}

	/**
	 * actionPerformed method of the ActionListener interface.
	 * 
	 * @param event The event which triggered this action.
	 */
	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
		if ( source.getClass( ) == TabCloseButton.class ) {
			int i = this.indexOfTabComponent( ((JComponent)source).getParent( ));
			if (  i >= 0 ) {
				this.remove( i );
			}
		}
	}

	/**
	 * The mouseClicked method of the MouseListener interface.
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 * 
	 * @param event The event which triggered this action.
	 */
	public void mouseClicked( MouseEvent event ) {
		JComponent source = (JComponent)event.getSource( );
		int index = this.indexOfTabComponent( source );
		if ( index >= 0 ) {
			if ( event.getButton( ) == MouseEvent.BUTTON1 ) {
				this.setSelectedIndex( index );
			} else if ( event.getButton( ) == MouseEvent.BUTTON3 ) {
				Component c = this.getComponentAt( index );
				this.tabPopup.show( 
					(JComponent)event.getSource( ), 
					event.getX( ), event.getY( ), 
					this.getTitleAt( index ),
					this.getComponentAt( index ));
			}
		}
	}

	/**
	 * The mouseEntered method of the MouseListener interface. Not implemented.
	 * 
	 * @param event The event which triggered this action.
	 */
	public void mouseEntered( MouseEvent event ) { }

	/**
	 * The mouseExited method of the MouseListener interface. Not implemented.
	 * 
	 * @param event The event which triggered this action.
	 */
	public void mouseExited( MouseEvent event ) { }

	/**
	 * The mousePressed method of the MouseListener interface. Not implemented.
	 * 
	 * @param event The event which triggered this action.
	 */
	public void mousePressed( MouseEvent event ) { }
	
	/**
	 * The mouseReleased method of the MouseListener interface. Not implemented. 
	 * 
	 * @param event The event which triggered this action.
	 */
	public void mouseReleased( MouseEvent event ) { }

	/**
	 * Enables or disables the "tearing off" of tabs into new windows.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setTearOff( boolean enable ) {
		this.tearOffEnabled = enable;
	}

	/**
	 * The popup menu for the tabs.
	 */
	protected class TabPopup extends JPopupMenu implements ActionListener {
		private JMenuItem tearOffMenuItem = new JMenuItem( 
			Settings.getLanguage( ).get( "Open in a new window" ));
		private String tabTitle;
		private Component tabComponent;

		/**
		 * Constructs a new TabPopup
		 */
		public TabPopup( ) {
			super( );
			this.tearOffMenuItem.addActionListener( this );
			this.add( this.tearOffMenuItem );
		}

		/**
		 * Displays the popup menu.
		 * 
		 * @param invoker The Component which invoked this menu.
		 * @param x The x coordinate of the location for this menu.
		 * @param y The y coordinate of the location for this menu.
		 * @param tabTitle The title of the tab which was clicked on.
		 * @param tabComponent The component contained in the tab which was clicked on.
		 */
		public void show( Component invoker, int x, int y, String tabTitle, Component tabComponent ) {
			if ( TabbedWindow.class.isAssignableFrom(
				((JComponent)invoker).getTopLevelAncestor( ).getClass( ))) {
					this.tabTitle = tabTitle;
					this.tabComponent = tabComponent;
					this.show( invoker, x, y );
			}
		}

		/**
		 * The actionPerformed method of the ActionListener interface.
		 * 
		 * @param event The event which triggered this action.
		 */
		public void actionPerformed( ActionEvent event ) {
			Object source = event.getSource( );
			if ( source == this.tearOffMenuItem ) {
				TabbedWindow top = (TabbedWindow)((JComponent)this.getInvoker( )).getTopLevelAncestor( );
				TabbedWindow newWindow = top.newWindow( );
				newWindow.addTab( this.tabTitle, this.tabComponent );
			}
		}

	}

	/**
	 * A button with an 'x' on it for closing tabs.
	 */
	protected class TabCloseButton extends JButton {
		private final int size = 17;

		/**
		 * Constructs a new TabCloseButton
		 */
		public TabCloseButton ( ) {
			this.setPreferredSize( new Dimension( this.size, this.size ));
			this.setToolTipText( Settings.getLanguage( ).get( "Close" ));
			this.setUI( new BasicButtonUI( ));
//				this.setContentAreaFilled( false );
			this.setFocusable( false );
			this.setBorder( BorderFactory.createEtchedBorder( ));
			this.setBorderPainted( false );
		}
		
		/**
		 * Overrides the updateUI method of JButton with a method
		 * which does nothing.
		 */
		public void updateUI( ) { }

		/**
		 * Draws the 'x' on the button.
		 * 
		 * @param g The graphics component of this button.
		 */
		protected void paintComponent( Graphics g ) {
			super.paintComponent( g );
			Graphics2D g2 = (Graphics2D)g.create( );
			if ( this.getModel( ).isPressed( ))
				g2.translate( 1, 1 );
			g2.setStroke( new BasicStroke( 2 ));
			g2.setColor( Color.BLACK );
			g2.drawLine( 7,5,11,9 );
			g2.drawLine( 11,5,7,9 );
			g2.dispose( );
		}
	}
}
	
