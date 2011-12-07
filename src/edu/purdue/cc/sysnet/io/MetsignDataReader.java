/*

This file is part of SysNet.

SysNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SysNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SysNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.sysnet.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.io.CSVTableReader;
import edu.purdue.cc.sysnet.SysNet;
import edu.purdue.cc.sysnet.util.*;

import org.apache.log4j.Logger;

/**
 * A class for reading CSV Data for SysNet.
 * 
 * @author Thomas McGrew
 */
public class MetsignDataReader extends DataReader {

	/**
	 * Creates a new MetsignDataReader.
	 */
	public MetsignDataReader( ) {
		super( );
	}

	/**
	 * Creates a new MetsignDataReader.
	 * 
	 * @param resource The name of the directory containing the files to be used.
	 */
	public MetsignDataReader( String resource ){
		super( resource );
	}
		
	/**
	 * Loads the data
	 */
	public void load( ) {
		if ( this.resource == null )
			throw new NullPointerException( Settings.getLanguage( ).get( 
				"The resource to load from was not specified" ));
		Logger logger = Logger.getLogger( getClass( ));
		HashMap <String,String> moleculeData = new HashMap <String,String> ( );
		HashMap <String,String> sampleData = new HashMap <String,String> ( );
		String [ ] headings;
		String [ ] columns;
		this.project = new Project( new File( resource ));
		Map<String,String> line;
		Language language = Settings.getLanguage( );
		CSVTableReader file;

		// *********************** load Project Info *************************
		Scanner scanner;
		try{ 
			scanner = new Scanner( new File( 
				this.resource + File.separator + "project_info.csv" ));
		} catch( FileNotFoundException e ) {
			logger.fatal( String.format( language.get( 
											 "Unable to load '%s'. The file was not found." ), 
				               "project_info.txt" ) + 
				               language.get( "No Data has been imported" ));
			this.project = null;
			return;
		}
		if ( !scanner.hasNextLine( ) ) {
			logger.fatal(
				String.format( language.get( 
				               "'%s' does not appear to be a valid file." ),
				               resource+File.separator+ " Experiment.txt" ) +
				               language.get( "No Data has been imported." ));
			this.project = null;
			return;
		}
		String [] headerLine;
		while ( scanner.hasNextLine( )) {

			headerLine = this.splitLine( scanner.nextLine( ), ',', true );
			boolean blank = true;
			for ( int i=0; i < headerLine.length; i++ ) {
				blank = blank && ( headerLine[ i ].equals( "" ));
			}
			if ( blank )
				break;
			this.project.setAttribute( "Project Name", 
				new File( this.resource ).getName( ));
			this.project.setAttribute( headerLine[ 0 ].trim( ), 
			( headerLine.length > 1 ) ? headerLine[ 1 ].trim( ) : "" );
			logger.debug( String.format( "Read header %s: %s",
				headerLine[ 0 ].trim( ), 
				( headerLine.length > 1 ) ? headerLine[ 1 ].trim( ) : "" ));
		}

		// *********************** load Sample Info ***************************
		file = new CSVTableReader( scanner );
		String sampleFileHeader = "Sample File";
		for ( String key : file.getKeys( )) {
			if ( sampleFileHeader.toLowerCase( ).equals( key.toLowerCase( ))) {
				sampleFileHeader = key;
				break;
			}
		}
		try {
			while( file.hasNext( )) {
				line = file.next( );
				logger.debug( line.toString( ));
				Sample sample = new Sample( line.get( sampleFileHeader ));
				sample.setAttributes( line );
				project.addSample( sample );
			}
		} catch ( Exception e ) {
			logger.debug( e, e );
			logger.error( "An error occurred while reading the project info file.\n" +
			              "This file may not be in the correct format." );
			this.project = null;
			return;
		}
		// find any ExperimentSets
		File normDir = new File( this.resource + File.separator + "Normalization" );
		for ( File dir : normDir.listFiles( )) {
			if ( dir.isDirectory( )  &&
				Arrays.asList( dir.list( )).contains( "Normalization.csv" )) {
				ExperimentSet set = new ExperimentSet( 
					dir.getName( ), new File( this.resource ));
				if ( project.hasAttribute( "time unit" )) {
					set.setTimeUnit( project.getAttribute( "time unit" ));
				}
				project.add( set );
			}

		}
		file.close( );

	}

	private String [] splitLine( String input, char delimiter, 
	                             boolean useQuotes ) {
		ArrayList<String> returnValue = new ArrayList<String>( );
		StringBuilder nextValue = new StringBuilder( );
		boolean inQuotes = false;
		for ( int i=0; i < input.length( ); i++ ) {
			if ( input.charAt( i ) == '"' && useQuotes ) {
				inQuotes = !inQuotes;
			} else if ( input.charAt( i ) == delimiter && !inQuotes ) {
				returnValue.add( nextValue.toString( ));
				nextValue = new StringBuilder( );
			} else {
				nextValue.append( input.charAt( i ));
			}
		}
		if ( nextValue.length( ) > 0 ) {
			returnValue.add( nextValue.toString( ));
		}
		return returnValue.toArray( new String[ returnValue.size( ) ]);
	}

}
