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

public class ClosableTabbedPane extends JTabbedPane implements ActionListener,MouseListener {
	protected boolean tearOffEnabled = true;
	protected TabPopup tabPopup = new TabPopup( );
	
	public void addTab( String title, Component component ) {
		super.addTab( title, component );
		this.makeClosable( this.getTabCount( )-1 );
	}

	public void addTab( String title, Icon icon, Component component ) {
		super.addTab( title, icon, component );
		this.makeClosable( this.getTabCount( )-1 );
	}
		
	public void addTab( String title, Icon icon, Component component, String tip ) {
		super.addTab( title, icon, component, tip );
		this.makeClosable( this.getTabCount( )-1 );
	}

	public void makeClosable( int index ) {
		this.makeClosable( index, true );
	}

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

	private void enableTearOff( int index ) {
		this.getTabComponentAt( index ).addMouseListener( this );
	}

	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
		if ( source.getClass( ) == TabCloseButton.class ) {
			int i = this.indexOfTabComponent( ((JComponent)source).getParent( ));
			if (  i >= 0 ) {
				this.remove( i );
			}
		}
	}

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

	public void mouseEntered( MouseEvent event ) { }
	public void mouseExited( MouseEvent event ) { }
	public void mousePressed( MouseEvent event ) { }
	public void mouseReleased( MouseEvent event ) { }

	public void setTearOff( boolean enable ) {
		this.tearOffEnabled = enable;
	}

	protected class TabPopup extends JPopupMenu implements ActionListener {
		private JMenuItem tearOffMenuItem = new JMenuItem( "Open in  new window" );
		private String tabTitle;
		private Component tabComponent;

		public TabPopup( ) {
			super( );
			this.tearOffMenuItem.addActionListener( this );
			this.add( this.tearOffMenuItem );
		}

		public void show( Component invoker, int x, int y, String tabTitle, Component tabComponent ) {
			if ( TabbedWindow.class.isAssignableFrom(
				((JComponent)invoker).getTopLevelAncestor( ).getClass( ))) {
					this.tabTitle = tabTitle;
					this.tabComponent = tabComponent;
					this.show( invoker, x, y );
			}
		}

		public void actionPerformed( ActionEvent event ) {
			Object source = event.getSource( );
			if ( source == this.tearOffMenuItem ) {
				TabbedWindow top = (TabbedWindow)((JComponent)this.getInvoker( )).getTopLevelAncestor( );
				TabbedWindow newWindow = top.newWindow( );
				newWindow.addTab( this.tabTitle, this.tabComponent );
			}
		}

	}

	protected class TabCloseButton extends JButton {
		private final int size = 17;

		public TabCloseButton ( ) {
			this.setPreferredSize( new Dimension( this.size, this.size ));
			this.setToolTipText( "Close" );
			this.setUI( new BasicButtonUI( ));
//				this.setContentAreaFilled( false );
			this.setFocusable( false );
			this.setBorder( BorderFactory.createEtchedBorder( ));
			this.setBorderPainted( false );
		}
		
		public void updateUI( ) { }

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
	
