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

package edu.purdue.cc.jsysnet.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;
import edu.purdue.cc.jsysnet.JSysNet;
import edu.purdue.cc.jsysnet.util.*;

import org.apache.log4j.Logger;

/**
 * A class for reading CSV Data for JSysNet.
 * 
 * @author Thomas McGrew
 */
public class CSVDataReader extends DataReader {



	/**
	 * Creates a new CSVDataReader.
	 */
	public CSVDataReader( ) {
		super( );
	}

	/**
	 * Creates a new CSVDataReader.
	 * 
	 * @param resource The name of the directory containing the files to be used.
	 */
	public CSVDataReader( String resource ){
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
		this.experiments = new ArrayList <Experiment>( );
		String line = new String( );
		Language language = Settings.getLanguage( );
		Scanner file;

		// *********************** load Experiment.txt *************************
		try{ 
			file = new Scanner( new File( 
				this.resource+File.separator+"Experiment.txt" ));
		} catch( FileNotFoundException e ) {
			logger.fatal( String.format( language.get( 
											 "Unable to load '%s'. The file was not found." ), 
				               this.resource + File.separator + "Experiment.txt" ) + 
				               language.get( "No Data has been imported" ));
			this.experiments = new ArrayList <Experiment>( );
			return;
		}
		if ( ! file.hasNext( ) ) {
			logger.fatal(
				String.format( language.get( 
				               "'%s' does not appear to be a valid file." ),
				               resource+File.separator+ " Experiment.txt" ) +
				               language.get( "No Data has been imported." ));
			return;
		}
		line = file.nextLine( );
		headings = line.split( "," );
		HashMap <String,String> columnsMap;
		while ( file.hasNextLine( )) {
			line = file.nextLine( );
			columns = line.split( "," );
			int columnLength = columns.length;
			columnsMap = new HashMap <String,String>( );
			for ( int i=headings.length-1; i >= 0;  i-- ) {
				columnsMap.put( headings[ i ].trim( ), 
					( columnLength > i ) ? columns[ i ].trim( ) : "" );
			}
			String id = columnsMap.remove( "exp_id" );
			this.addExperiment( new Experiment( id, columnsMap ));
		}
		file.close( );

		// *********************** load Sample.txt ***************************
		Map<String,Sample> sampleMap = new HashMap<String,Sample>( );
		try {
			file = new Scanner( new File( resource+File.separator+"Sample.txt" ));
		} catch( FileNotFoundException e ) {
			logger.fatal( String.format( 
				language.get( "Unable to load '%s'. The file was not found." ), 
					this.resource + File.separator + "Sample.txt" ) + 
				language.get( "No Data has been imported" ));
			this.experiments = new ArrayList <Experiment>( );
			return;
		}
		line = file.nextLine( );
		headings = line.split( "," );
		while( file.hasNextLine( )) {
			line = file.nextLine( );
			columns = line.split( "," );
			Map<String,String> sampleAttrs = new HashMap<String,String>( );
			for ( int i=headings.length-1; i >= 0; i-- ) {
				// read the value, removing any surrounding quotes.
				String value = (( columns.length > i ) ? columns[ i ].trim( ) : "")
					.replaceAll( "\"(.*)\"", "$1" );
				sampleAttrs.put( headings[ i ].trim( ), value );
			}
			Sample sample = new Sample( sampleAttrs.get( "Sample" ));
			sample.setAttributes( sampleAttrs );
			sampleMap.put( sample.toString( ), sample );
			for ( Experiment experiment : experiments ) {
				experiment.addSample( sample );
			}

		}
		file.close( );

		// *********************** load Data.txt ***************************
		try {
			file = new Scanner( new File( resource+File.separator+"Data.txt" ));
		} catch( FileNotFoundException e ) {
			logger.fatal( String.format( language.get( 
				              "Unable to load '%s'. The file was not found." ), 
				              this.resource + File.separator + "Data.txt" ) + 
				              language.get( "No Data has been imported" ));
			this.experiments = new ArrayList <Experiment>( );
			return;
		}
		line = file.nextLine( );
		headings = line.split( "," );
		while( file.hasNextLine( )) {
			line = file.nextLine( );
			columns = line.split( "," );
			int columnLength = columns.length;

			Map<String,String> data = new HashMap<String,String>( );
			for ( int i=headings.length-1; i >= 0; i-- ) {
				String key = headings[ i ].trim( );  
				String valueString = ( columnLength > i ) ? columns[ i ].trim( ) : "";
				data.put( key, valueString );
			}
			Molecule molecule = new Molecule( data.remove( "id" ));
			String exp_id = data.remove( "exp_id" );
			// add this molecule to the appropriate experiment
			// add the remaining attributes to the molecule.
			for( Map.Entry<String,String> entry : data.entrySet( )) {
				// see if this column is a sample value
				if ( sampleMap.containsKey( entry.getKey( ))) {
					double value = Double.NaN;
					try {
						value = Double.parseDouble( entry.getValue( ));
					} catch ( NumberFormatException e ) {
						Logger.getLogger( getClass( )).debug( String.format( 
							"Invalid number format for sample value: %s", 
							entry.getValue( )), e );
					}
					molecule.addSample( sampleMap.get( entry.getKey( )), value );
				} else {
					molecule.setAttribute( entry.getKey( ), entry.getValue( ));
				}
					
			}
			for ( Experiment experiment : experiments ) {
				if ( experiment.getId( ).equals( exp_id ))
					experiment.addMolecule( molecule );
			}
		}
		file.close( );

		// *********************** load Molecule.txt ***************************
		File moleculeFile = new File( resource+File.separator+"Molecule.txt" );
		List <HashMap<String,String>>  moleculeList = 
			new ArrayList <HashMap<String,String>>( );
		if ( moleculeFile.isFile( )) {
			try {
				file = new Scanner( moleculeFile );
			} catch( FileNotFoundException e ) { }
			line = file.nextLine( );
			headings = line.split( "," );
			while( file.hasNextLine( )) {
				line = file.nextLine( );
				columns = line.split( "," );
				int columnLength = columns.length;
				columnsMap = new HashMap <String,String>( );
				for ( int i=headings.length-1; i >= 0; i-- ) {
					columnsMap.put( headings[ i ].trim( ), 
						( columnLength > i ) ? columns[ i ].trim( ) : "" );
				}
				moleculeList.add( columnsMap );
	
			}
			file.close( );
		}

		// iterate through the data in Molecule.txt and add the attributes
		for( HashMap <String,String> moleculeDataHashMap : moleculeList ) {
			Experiment experiment = null;
			for ( Experiment e : experiments ) {
				if ( e.getId( ).equals( moleculeDataHashMap.get( "exp_id" ))) {
					experiment = e;
					break;
				}
			}
			if ( experiment != null ) {
				Molecule molecule = experiment.getMolecule( 
					moleculeDataHashMap.get( "id" ));
	
				if ( molecule != null ) {
					// Add the first set of attributes to the Molecule Objects
					for ( Map.Entry<String,String> molAttr : 
					      moleculeDataHashMap.entrySet( )) {
						molecule.setAttribute( molAttr.getKey( ), molAttr.getValue( ));
					}
				}
			} else {
				logger.debug( String.format( 
					"Experiment '%s' from Molecule.txt not found",
					moleculeDataHashMap.get( "exp_id" )));
			}
		}
	}
}
