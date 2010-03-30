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

import gnu.getopt.Getopt;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import edu.purdue.jsysnet.ui.*;
import edu.purdue.jsysnet.util.Settings;

public class JSysNet {


	public static void main ( String [ ] args ) {
		// try to make this guy blend in :)
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
		} catch ( Exception e ) {
			if ( Settings.VERBOSE ) {
				System.err.println( "Attemping to load the system look and feel resulted "+
					"in the following error:" );
				System.err.println( "\t" + e.getMessage( ));
			} else {
				System.err.println( "Unable to load system look and feel, switching to default" );
			}
		}

		// read the command line options
		Getopt g = new Getopt( "JSysNet", args, "dg:hv" );
		String arg;
		int c;

		while (( c = g.getopt( )) != -1 ) {
			switch( c ) {
				case 'd':
					Settings.DEBUG = true;
					break;
				case 'g':
					try {
						String[] size = g.getOptarg( ).split( "x" );
						Settings.DEFAULT_WIDTH  = Integer.parseInt( size[ 0 ] );
						Settings.DEFAULT_HEIGHT = Integer.parseInt( size[ 1 ] );
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
						Settings.DEFAULT_WIDTH, Settings.DEFAULT_HEIGHT );
					System.out.println( "-h          Prints this message and exits" );
					System.exit( 0 );
				case 'v':
					Settings.VERBOSE = true;
					break;
				case '?':
					break;
				default:
					System.out.printf ( "You specified an unrecognized option '%s'", c );
					System.exit( -1 );
			}
		}
		newWindow( );
	}

	public static void newWindow( ){
		JSysNetWindow s = new JSysNetWindow( "JSysNet" );
	}

//	public static void newDetailWindow( ){
//		DetailWindow d = new DetailWindow( "Details" );
//	}

	public static void message( String text ){
		System.err.println( text );
		JOptionPane.showMessageDialog( null, text );
	}

}
	
	
