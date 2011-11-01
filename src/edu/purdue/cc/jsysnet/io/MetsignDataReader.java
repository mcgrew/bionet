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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
import edu.purdue.cc.jsysnet.JSysNet;
import edu.purdue.cc.jsysnet.util.*;

import org.apache.log4j.Logger;

/**
 * A class for reading CSV Data for JSysNet.
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
		this.experiments = new TreeSet<Experiment>( );
		Map<String,String> line;
		Language language = Settings.getLanguage( );
		CSVTableReader file;

		// *********************** load Experiment Info *************************
		Scanner scanner;
		try{ 
			scanner = new Scanner( new File( 
				this.resource + File.separator + ".." + File.separator + 
				".." + File.separator + "project_info.csv" ));
		} catch( FileNotFoundException e ) {
			logger.fatal( String.format( language.get( 
											 "Unable to load '%s'. The file was not found." ), 
				               "project_info.txt" ) + 
				               language.get( "No Data has been imported" ));
			this.experiments = new ArrayList <Experiment>( );
			return;
		}
		if ( !scanner.hasNextLine( ) ) {
			logger.fatal(
				String.format( language.get( 
				               "'%s' does not appear to be a valid file." ),
				               resource+File.separator+ " Experiment.txt" ) +
				               language.get( "No Data has been imported." ));
			return;
		}
		String [] headerLine;
		while ( scanner.hasNextLine( )) {

			headerLine = scanner.nextLine( ).split( "," );
			boolean blank = true;
			for ( int i=0; i < headerLine.length; i++ ) {
				blank = blank && ( headerLine[ i ].equals( "" ));
			}
			if ( blank )
				break;
			logger.debug( String.format( "Read header %s: %s",
				( headerLine.length > 0 ) ? headerLine[ 0 ] : "", 
				( headerLine.length > 1 ) ? headerLine[ 1 ] : "" ));
		}

		// *********************** load Sample Info ***************************
		file = new CSVTableReader( scanner );
		file.setUseQuotes( true );
		Map<Sample,Boolean> samplePresent = new HashMap<Sample,Boolean>( );
		while( file.hasNext( )) {
			line = file.next( );
			logger.debug( line.toString( ));
			Sample sample = 
				new Sample( line.get( "Sample File" ));
			sample.setAttributes( line );
			String expId = Settings.getLanguage( ).get( "Time" ) + " " +
					sample.getAttribute( "Time" );
			Experiment experiment = 
				this.getExperiment( expId );
			if ( experiment == null ) {
				experiment = new Experiment( expId );
				experiments.add( experiment );
			}
			experiment.addSample( sample );
			samplePresent.put( sample, Boolean.FALSE );
		}
		file.close( );

		// *********************** load Data ***************************
		try {
			file = new CSVTableReader( new File( resource + File.separator + 
			"Normalization.csv" ));
		} catch( FileNotFoundException e ) {
			logger.fatal( String.format( language.get( 
				              "Unable to load '%s'. The file was not found." ), 
				              this.resource + File.separator + "Normalization.csv" ) + 
				              language.get( "No Data has been imported" ));
			this.experiments = new ArrayList <Experiment>( );
			return;
		}
		while( file.hasNext( )) {
			line = file.next( );
			String id = line.remove( "id" );
      if ( id == null )
        id = line.remove( "ID" );
			Molecule molecule;
			molecule = new Molecule( id );
			for ( Experiment experiment : this.experiments ) {
        experiment.addMolecule( molecule );
			}
			// add the remaining attributes to the molecule.
			for( Map.Entry<String,String> entry : line.entrySet( )) {
				// see if this column is a sample value
				Sample sample = null;
				for ( Experiment e : this.experiments ) {
					sample = e.getSample( entry.getKey( ));
					if ( sample != null )
						break;
				}
				if ( sample != null ) {
					Number value = new Double( Double.NaN );
					try {
						value = new Double( entry.getValue( ));
					} catch ( NumberFormatException exc ) {
						Logger.getLogger( getClass( )).trace( String.format( 
							"Invalid number format for sample value: %s", 
							entry.getValue( )), exc );
					}
					Logger.getLogger( getClass( )).trace( String.format( 
						"Adding sample value %f to %s for sample %s", 
						value, molecule, sample ));
					sample.setValue( molecule, value );
					samplePresent.put( sample, Boolean.TRUE );
				} else {
					Logger.getLogger( getClass( )).trace( String.format( 
						"Setting attribute %s for Molecule %s to %s", 
						entry.getKey( ), molecule, entry.getValue( )));
					molecule.setAttribute( entry.getKey( ), entry.getValue( ));
				}
			}
		}
		for ( Experiment experiment : this.experiments ) {
			ArrayList<Sample> samples = 
				new ArrayList<Sample>( experiment.getSamples( ));
			for( Sample sample : samples ) {
				if ( !samplePresent.get( sample ).booleanValue( )) {
					logger.debug( 
						String.format( "Dropping sample %s", sample.toString( )));
					experiment.removeSample( sample );
				}
			}
		}
		file.close( );
	}
}
