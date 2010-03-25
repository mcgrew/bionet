package edu.purdue.jsysnet.ui;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Icon;
import javax.swing.plaf.basic.BasicButtonUI;

public class ClosableTabbedPane extends JTabbedPane implements ActionListener {
	
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
	}

	public void actionPerformed( ActionEvent event ) {
		int i = this.indexOfTabComponent( ((TabCloseButton)event.getSource( )).getParent( ));
		if (  i >= 0 ) {
			this.remove( i );
		}
	}

	private class TabCloseButton extends JButton {
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
//			if( this.getModel( ).isRollover( ))
//				g2.setColor( new Color( 1f, 0.5f, 0.5f ));
//			else
//				g2.setColor( Color.RED );
//			g2.fillRect( 5,3,8,8 );
			g2.setColor( Color.BLACK );
			g2.drawLine( 7,5,11,9 );
			g2.drawLine( 11,5,7,9 );
			g2.dispose( );
		}
	}
}
	
