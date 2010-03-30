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

package edu.purdue.jsysnet.util;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

public class Settings extends Properties {

	private static Dimension DESKTOP_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	public static int DESKTOP_WIDTH  = DESKTOP_SIZE.width;
	public static int DESKTOP_HEIGHT = DESKTOP_SIZE.height;
	public static boolean DEBUG = false;
	public static boolean VERBOSE = false;
	public static int DEFAULT_WIDTH = 1024;
	public static int DEFAULT_HEIGHT = 768;
	private static String HOME_DIR = System.getenv( "HOME" );
	private static String SETTINGS_FILE = HOME_DIR + "/.jsysnet";

	public Settings ( ) {
		super( );
	}
	public Settings( String [ ] args ) {
		
	}

	public void save( ) {
		try {
			this.storeToXML( new BufferedOutputStream( 
				new FileOutputStream( new File( SETTINGS_FILE ))), null );
		} catch ( IOException e ) {
			System.out.print( String.format( 
				"Unable to save program settings. File %s is not writeable", SETTINGS_FILE ));
		}
	}
	
	public void load( ) {
		try {
			this.loadFromXML( new BufferedInputStream( 
				new FileInputStream( new File( SETTINGS_FILE ))));
		} catch ( IOException e ) { }
	}

}
