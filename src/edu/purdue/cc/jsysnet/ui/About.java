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

import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.Language;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;


/**
 * A class for displaying an "about" dialog
 */
public class About extends JFrame {
	private JPanel panel = new AboutPanel( );
	private BufferedImage logo;

	/**
	 * Creates an instance of an about dialog.
	 */
	public About( ) {
		super( );
		int x = Settings.getSettings( ).getInt( "windowXPosition" );
		int y = Settings.getSettings( ).getInt( "windowYPosition" );
		this.setBounds( x, y, 400, 250 );
		this.add( this.panel );
		try { 
			this.logo = ImageIO.read( getClass( ).getResourceAsStream( "/resources/images/logo.png" ));
		} catch ( IOException e ) {
		}
	}

	private class AboutPanel extends JPanel {

		public void paintComponent( Graphics g ) {
			String text;
			FontMetrics f = g.getFontMetrics( );
			Language language = Settings.getLanguage( );

			text = language.get( "Copyright 2010 Purdue University" ); 
			g.drawString( text, 200 - (f.stringWidth( text ) / 2), 130 );

			text = language.get( "JSysNet is distributed under the GNU GPL license" );
			g.drawString( text, 200 - (f.stringWidth( text ) / 2), 150 );

			text = language.get( "This project is funded by NIH Grant 5R01GM087735" );
			g.drawString( text, 200 - (f.stringWidth( text ) / 2), 170 );

			g.setFont( new Font( "Serif", Font.BOLD, 42 ));
			f = g.getFontMetrics( );

			text = "JSysNet";
			g.drawString( text, 230 - (f.stringWidth( text ) / 2), 80 );

			g.drawImage( logo, 50, 30, null );

		}
	}

}


