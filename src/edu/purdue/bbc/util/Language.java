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

package edu.purdue.bbc.util;

import java.util.Properties;
import java.util.Locale;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class Language {
	private Locale locale = Locale.getDefault( );
	private Properties strings = new Properties( );

	public Language( ) {
		Settings settings = Settings.getSettings( );
		if ( settings == null || settings.getProperty( "locale" ) == null )
			setLocale( Locale.getDefault( ));
		else
			setLocale( settings.getProperty( "locale" ));
	}

	public Language( String locale ) {
		setLocale( locale );
	}

	public Language( Locale locale ) {
		setLocale( locale );
	}

	public void setLocale( String locale ) {
			String [] args = locale.split( "_", 2 );
			if ( args.length == 1 )
				setLocale( new Locale( args[0] ));
			else
				setLocale( new Locale( args[0], args[1] ));
	}

	public void setLocale( Locale locale ) {
		this.locale = locale;
		// try language + country first.
		InputStream resource = getClass( ).getResourceAsStream( 
			"/resources/i18n/" + locale.getLanguage( ) + "_" +locale.getCountry( ) + ".xml" );
		if ( resource == null ) {
			// try just language.
			resource = getClass( ).getResourceAsStream( 
				"/resources/i18n/" + locale.getLanguage( ) + ".xml" );
		}
		if ( resource /*still*/ == null ) {
			Logger.getLogger( getClass( )).debug( 
				get( "Unable to read language file for " ) + 
				locale.toString( ) + ". " );
		} else {
			try {
				strings.loadFromXML( resource ); 
			} catch ( IOException e ) {
				Logger.getLogger( getClass( )).debug( 
					get( "Unable to read language file for " ) + 
					locale.toString( ) + ". " );
			}
		}
	}

	public Locale getLocale( ) {
		return locale;
	}

	public String get( String s ) {
		if ( strings == null )
			return s;

		String returnValue = strings.getProperty( s );
		if ( returnValue != null ) {
			return returnValue;
		}

		if ( true )
			Logger.getLogger( getClass( )).debug( 
				String.format( "'%s' not in language file", s ));
		return s;
	}
}

