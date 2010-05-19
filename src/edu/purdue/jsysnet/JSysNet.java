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

package edu.purdue.jsysnet;

import edu.purdue.jsysnet.ui.*;
import edu.purdue.jsysnet.util.Settings;

import gnu.getopt.Getopt;

import javax.swing.UIManager;
import javax.swing.JOptionPane;
import java.util.Properties;


public class JSysNet {
	public static Settings settings;

	public static void main ( String [ ] args ) {
		// read the command line options
		Getopt g = new Getopt( "JSysNet", args, "dg:hv" );
		String arg;
		int c;

		JSysNet.settings = new Settings( );
		JSysNet.settings.load( );

		while (( c = g.getopt( )) != -1 ) {
			switch( c ) {
				case 'd':
					JSysNet.settings.getDefaults( ).setProperty( "debug", "true" );
					break;
				case 'g':
					try {
						String[] size = g.getOptarg( ).split( "x" );
						JSysNet.settings.setInt( "windowWidth", Integer.parseInt( size[ 0 ] ));
						JSysNet.settings.setInt( "windowHeight", Integer.parseInt( size[ 1 ] ));
					} catch( NumberFormatException e ) {
						System.out.println( "You specified an invalid value for -g.\n"+
							"Values should be in the format WxH in pixels, such as 1024x768." );
						System.exit( -1 );
					}
					break;
				case 'h':
					System.out.println( );
					System.out.println( "Usage:" );
					System.out.println( "java JSysNet [-d|h] [-g geometry]" );
					System.out.println( "-d          Runs JSysnet in debug mode" );
					System.out.printf ( "-g          Overrides the default window size of %dx%d\n",
						JSysNet.settings.getDefaults( ).getProperty( "windowWidth" ), 
						JSysNet.settings.getDefaults( ).getProperty( "windowHeight" ));
					System.out.println( "-h          Prints this message and exits" );
					System.exit( 0 );
				case 'v':
					JSysNet.settings.getDefaults( ).setProperty( "verbose", "true" );
					break;
				case '?':
					break;
				default:
					System.out.printf ( "You specified an unrecognized option '%s'", c );
					System.exit( -1 );
			}
		}
		// try to make this blend in
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
		} catch ( Exception e ) {
			if ( JSysNet.settings.getBoolean( "VERBOSE" )) {
				System.err.println( "Attemping to load the system look and feel resulted "+
					"in the following error:" );
				System.err.println( "\t" + e.getMessage( ));
			} else {
				System.err.println( "Unable to load system look and feel, switching to default" );
			}
		}
		
		JSysNet.newWindow( );
	}

	public static void newWindow( ){
		JSysNetWindow s = new JSysNetWindow( "JSysNet" );
	}

	public static void message( String text ){
		System.err.println( text );
		JOptionPane.showMessageDialog( null, text );
	}


}

