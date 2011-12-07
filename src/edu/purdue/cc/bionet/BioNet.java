/*

This file is part of BioNet.

BioNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BioNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with BioNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.bionet;

import edu.purdue.cc.bionet.ui.*;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.Language;

import gnu.getopt.Getopt;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;


public class BioNet {

	public static void main ( String [ ] args ) {
		// read the command line options
		Getopt g = new Getopt( "BioNet", args, "dg:hv" );
		String arg;
		int c;

		Logger rootLogger = Logger.getRootLogger( );
		rootLogger.setLevel( Level.ERROR );
		rootLogger.addAppender( new ConsoleAppender( 
			new PatternLayout( PatternLayout.TTCC_CONVERSION_PATTERN ),
			ConsoleAppender.SYSTEM_ERR ));
		rootLogger.addAppender( new DialogAppender( Level.ERROR ));

		Properties defaultSettings = new Properties( );
		defaultSettings.setProperty( "window.main.width", "1024" );
		defaultSettings.setProperty( "window.main.height", "768" );
		Dimension desktopSize = Toolkit.getDefaultToolkit( ).getScreenSize( );
		defaultSettings.setProperty( "desktop.width", Integer.toString( desktopSize.width ));
		defaultSettings.setProperty( "desktop.height", Integer.toString( desktopSize.height ));
		defaultSettings.setProperty( "window.main.position.x", Integer.toString(( desktopSize.width - 1024 ) / 2 ));
		defaultSettings.setProperty( "window.main.position.y", Integer.toString(( desktopSize.height - 768 ) / 2 ));
		defaultSettings.setProperty( "window.detail.width", "850" );
		defaultSettings.setProperty( "window.detail.height", "350" );
		defaultSettings.setProperty( "window.detail.position.x", "100" );
		defaultSettings.setProperty( "window.detail.position.y", "40" );
		defaultSettings.setProperty( "debug", "false" );
		defaultSettings.setProperty( "verbose", "false" );

		Settings settings = new Settings( defaultSettings,
			System.getProperty( "user.home" ) + "/.bionet/settings.xml" );
		Language language = Settings.getLanguage( );

		while (( c = g.getopt( )) != -1 ) {
			switch( c ) {
				case 'd':
					rootLogger.setLevel( Level.DEBUG );
					settings.getDefaults( ).setProperty( "debug", "true" );
					break;
				case 'g':
					try {
						String[] size = g.getOptarg( ).split( "x" );
						settings.setInt( "window.main.width", Integer.parseInt( size[ 0 ] ));
						settings.setInt( "window.main.height", Integer.parseInt( size[ 1 ] ));
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
					System.out.println( "java BioNet [-d|h] [-g geometry]" );
					System.out.println( "-d          "+ language.get( "Runs BioNet in debug mode" ));
					System.out.printf ( "-g          "+ language.get( "Overrides the default window size of" ) 
						+ " %dx%d\n",
						settings.getInt( "window.main.width" ), 
						settings.getInt( "window.main.height" ));
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
			Logger logger = Logger.getLogger( BioNet.class );
			logger.debug( 
				language.get( "Attemping to load the system look and feel resulted in the following error" )
				+ ":" , e );
			logger.info( language.get( "Unable to load system look and feel, switching to default" ));
		}
		
		BioNet.newWindow( );
	}

	public static void newWindow( ){
		BioNetWindow s = new BioNetWindow( "BioNet" );
	}

	private static class DialogAppender extends AppenderSkeleton {
		private Level minimumLevel;

		public DialogAppender( Level minimumLevel ) {
			super( );
			this.setMinimumLevel( minimumLevel );
		}

		public void setMinimumLevel( Level minimumLevel ) {
			this.minimumLevel = minimumLevel;
		}

		public void append( LoggingEvent event ) {
			if ( event.getLevel( ).isGreaterOrEqual( this.minimumLevel )) {
				JOptionPane.showMessageDialog( null, event.getMessage( ));
			}
		}

		public boolean requiresLayout( ) {
			return false;
		}

		public void close( ) { }
	}

}

