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
import java.util.Locale;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;

import org.apache.log4j.Logger;

public class Settings extends Properties {
	private static Settings settings = new Settings( );
	private static Language language;

	public Settings( ) {
		super( );
		this.defaults = new DefaultSettings( );
		this.load( );
	}

	public Object setProperty( String property, String value ) {
		Object returnValue = super.setProperty( property, value );
		if ( "locale".equals( property ))
			if ( language == null )
				language = new Language( value );
			else
				language.setLocale( value );
		this.save( );
		return returnValue;
	}

	public static Settings getSettings( ) {
		return settings;
	}

	public static Language getLanguage( ) {
		if ( language == null )
			language = new Language( );
		return language;
	}

	public Properties getDefaults( ){
		return this.defaults;
	}

	public boolean getBoolean( String property ) {
		return Boolean.parseBoolean( get( property ));
	}

	public int getInt( String property ) {
		return Integer.parseInt( get( property ));
	}

	public double getDouble( String property ) {
		return Double.parseDouble( get( property ));
	}

	public String get( String property ) {
		return getProperty( property );
	}

	public void setBoolean( String property, boolean value ) {
		setProperty( property, Boolean.toString( value ));
	}

	public void setInt( String property, int value ) throws NumberFormatException {
		setProperty( property, Integer.toString( value ));
	}

	public void setDouble( String property, double value ) throws NumberFormatException {
		setProperty( property, Double.toString( value ));
	}

	public void save( ) {
		Logger logger = Logger.getLogger( getClass( ));
		try {
			logger.debug( "Saving settings..." );
			File settingsFile = new File( this.getProperty( "settingsFile" ));
			if( !settingsFile.getParentFile( ).exists( ) && !settingsFile.getParentFile( ).mkdirs( )) {
				logger.error( String.format(
				  "Unable to create directory '%s' for saving program settings.",
					settingsFile.getParent( )));
			}
			this.storeToXML( new BufferedOutputStream( 
				new FileOutputStream( settingsFile )), null );
		} catch ( IOException e ) {
			logger.error( String.format( 
				"Unable to save program settings. File '%s' is not writeable", 
				this.getProperty( "settingsFile" )), e );
		}
	}
	
	public void load( ) {
		Logger logger = Logger.getLogger( getClass( ));
		try {
			logger.debug( "Loading settings..." );
			this.loadFromXML( new BufferedInputStream( 
				new FileInputStream( new File( this.getProperty( "settingsFile" )))));
		} catch ( IOException e ) {
			logger.debug( String.format(
			  "Unable to read program settings. File %s is not readable",
				this.getProperty( "settingsFile" )));
		}
		if ( this.getProperty( "locale" ) != null )
			if ( language != null )
				language.setLocale( get( "locale" ));
			else
				language = new Language( get( "locale" ));
	}
	
	private class DefaultSettings extends Properties {

		public DefaultSettings( ) {
			super( );
			this.setProperty( "windowWidth", "1024" );
			this.setProperty( "windowHeight", "768" );
			this.setProperty( "homeDir", System.getProperty( "user.home" ));
			this.setProperty( "settingsFile", this.getProperty( "homeDir" ) + "/.jsysnet/settings.xml" );
			Dimension desktopSize = Toolkit.getDefaultToolkit( ).getScreenSize( );
			this.setProperty( "desktopWidth", Integer.toString( desktopSize.width ));
			this.setProperty( "desktopHeight", Integer.toString( desktopSize.height ));
			this.setProperty( "windowXPosition", Integer.toString(( desktopSize.width - 1024 ) / 2 ));
			this.setProperty( "windowYPosition", Integer.toString(( desktopSize.height - 768 ) / 2 ));
			this.setProperty( "detailWindowWidth", "850" );
			this.setProperty( "detailWindowHeight", "350" );
			this.setProperty( "detailWindowXPosition", "100" );
			this.setProperty( "detailWindowYPosition", "40" );
			this.setProperty( "debug", "false" );
			this.setProperty( "verbose", "false" );
		}

	}


}
