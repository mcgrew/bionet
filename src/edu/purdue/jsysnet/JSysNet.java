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
import edu.purdue.jsysnet.util.Language;

import gnu.getopt.Getopt;

import javax.swing.UIManager;
import javax.swing.JOptionPane;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.ConsoleAppender;


public class JSysNet {

	public static void main ( String [ ] args ) {
		// read the command line options
		Getopt g = new Getopt( "JSysNet", args, "dg:hv" );
		String arg;
		int c;
		Settings settings = Settings.getSettings( );
		Language language = Settings.getLanguage( );
		Logger rootLogger = Logger.getRootLogger( );
		rootLogger.setLevel( Level.DEBUG );
		rootLogger.addAppender( new ConsoleAppender( 
			new PatternLayout( PatternLayout.TTCC_CONVERSION_PATTERN ),
			ConsoleAppender.SYSTEM_ERR ));

		while (( c = g.getopt( )) != -1 ) {
			switch( c ) {
				case 'd':
					rootLogger.setLevel( Level.DEBUG );
					settings.getDefaults( ).setProperty( "debug", "true" );
					break;
				case 'g':
					try {
						String[] size = g.getOptarg( ).split( "x" );
						settings.setInt( "windowWidth", Integer.parseInt( size[ 0 ] ));
						settings.setInt( "windowHeight", Integer.parseInt( size[ 1 ] ));
					} catch( NumberFormatException e ) {
						System.out.println( language.get( "You specified an invalid value for" ) +
							" -g.\n"+ 
							language.get( "Values should be in the format WxH in pixels, such as" ) + 
							" 1024x768." );
						System.exit( -1 );
					}
					break;
				case 'h':
					System.out.println( );
					System.out.println( language.get( "Usage" ) + ":" );
					System.out.println( "java JSysNet [-d|h] [-g geometry]" );
					System.out.println( "-d          "+ language.get( "Runs JSysnet in debug mode" ));
					System.out.printf ( "-g          "+ language.get( "Overrides the default window size of" ) 
						+ " %dx%d\n",
						settings.getInt( "windowWidth" ), 
						settings.getInt( "windowHeight" ));
					System.out.println( "-h          "+ language.get( "Prints this message and exits" ));
					System.exit( 0 );
				case 'v':
					rootLogger.setLevel( Level.INFO );
					settings.getDefaults( ).setProperty( "verbose", "true" );
					break;
				case '?':
					break;
				default:
					System.out.printf ( language.get( "You specified an unrecognized option" ) + " '%s'", c );
					System.exit( -1 );
			}
		}
		// try to make this blend in
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
		} catch ( Exception e ) {
			if ( settings.getBoolean( "VERBOSE" )) {
				System.err.println( 
					language.get( "Attemping to load the system look and feel resulted in the following error" )
					+ ":" );
				System.err.println( "\t" + e.getMessage( ));
			} else {
				System.err.println( language.get( "Unable to load system look and feel, switching to default" ));
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

