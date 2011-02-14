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
import edu.purdue.bbc.io.CSVTableReader;
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
		Map<String,String> line;
		Language language = Settings.getLanguage( );
		CSVTableReader file;

		// *********************** load Experiment.txt *************************
		try{ 
			file = new CSVTableReader( new File( 
				this.resource+File.separator+"Experiment.txt" ));
		} catch( FileNotFoundException e ) {
			logger.fatal( String.format( language.get( 
											 "Unable to load '%s'. The file was not found." ), 
				               this.resource + File.separator + "Experiment.txt" ) + 
				               language.get( "No Data has been imported" ));
			this.experiments = new ArrayList <Experiment>( );
			return;
		}
		if ( !file.hasNext( ) ) {
			logger.fatal(
				String.format( language.get( 
				               "'%s' does not appear to be a valid file." ),
				               resource+File.separator+ " Experiment.txt" ) +
				               language.get( "No Data has been imported." ));
			return;
		}
		while ( file.hasNext( )) {
			line = file.next( );
			String id = line.remove( "exp_id" );
			this.addExperiment( new Experiment( id, line ));
		}
		file.close( );

		// *********************** load Sample.txt ***************************
		try {
			file = new CSVTableReader( new File( resource+File.separator+"Sample.txt" ));
		} catch( FileNotFoundException e ) {
			logger.fatal( String.format( 
				language.get( "Unable to load '%s'. The file was not found." ), 
					this.resource + File.separator + "Sample.txt" ) + 
				language.get( "No Data has been imported" ));
			this.experiments = new ArrayList <Experiment>( );
			return;
		}
		file.setQuoteStripping( true );
		while( file.hasNext( )) {
			line = file.next( );
			Sample sample = new Sample( line.get( "Sample" ));
			sample.setAttributes( line );
			for ( Experiment experiment : experiments ) {
				experiment.addSample( sample.clone( ));
			}
		}
		file.close( );

		// *********************** load Data.txt ***************************
		try {
			file = new CSVTableReader( new File( resource+File.separator+"Data.txt" ));
		} catch( FileNotFoundException e ) {
			logger.fatal( String.format( language.get( 
				              "Unable to load '%s'. The file was not found." ), 
				              this.resource + File.separator + "Data.txt" ) + 
				              language.get( "No Data has been imported" ));
			this.experiments = new ArrayList <Experiment>( );
			return;
		}
		Map<String,Molecule> moleculeMap = new HashMap<String,Molecule>( );
		while( file.hasNext( )) {
			line = file.next( );
			Molecule molecule = new Molecule( line.remove( "id" ));
			moleculeMap.put( molecule.getId( ), molecule );
			String exp_id = line.remove( "exp_id" );
			Experiment experiment = this.getExperiment( exp_id );
			// add the remaining attributes to the molecule.
			for( Map.Entry<String,String> entry : line.entrySet( )) {
				// see if this column is a sample value
				Sample sample = experiment.getSample( entry.getKey( ));
				if ( sample != null ) {
					Number value = new Double( Double.NaN );
					try {
						value = new Double( entry.getValue( ));
					} catch ( NumberFormatException exc ) {
						Logger.getLogger( getClass( )).debug( String.format( 
							"Invalid number format for sample value: %s", 
							entry.getValue( )), exc );
					}
					sample.setValue( molecule, value );
				} else {
					molecule.setAttribute( entry.getKey( ), entry.getValue( ));
				}
			}
			experiment.addMolecule( molecule );
		}
		file.close( );

		// *********************** load Molecule.txt ***************************
		File moleculeFile = new File( resource+File.separator+"Molecule.txt" );
		if ( moleculeFile.isFile( )) {
			try {
				file = new CSVTableReader( moleculeFile );
			} catch( FileNotFoundException e ) { }
			// iterate through the data in Molecule.txt and add the attributes
			while( file.hasNext( )) {
				line = file.next( );
				line.remove( "exp_id" ); // unneeded info.
				String id = line.remove( "id" );
				Molecule molecule = moleculeMap.get( line.remove( "id" ));
				if ( molecule != null ) {
					// Add the first set of attributes to the Molecule Objects
					for ( Map.Entry<String,String> molAttr : line.entrySet( )) {
						molecule.setAttribute( molAttr.getKey( ), molAttr.getValue( ));
					}
				} else {
					logger.debug( String.format( 
						"Molecule '%s' from Molecule.txt not found in Data.txt", id ));
				}
			}
		}
	}
}
