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

package edu.purdue.jsysnet.io;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.io.File;
import java.io.FileNotFoundException;
import edu.purdue.jsysnet.util.*;
import edu.purdue.jsysnet.JSysNet;

/**
 * A class for reading CSV Data for JSysNet.
 * 
 * @author Thomas McGrew
 */
public class CSVDataHandler extends DataHandler {

	private Scanner file;
	ArrayList <HashMap<String,String>> moleculeArrayList, dataArrayList, sampleArrayList;


	/**
	 * Creates a new CSVDataHandler.
	 */
	public CSVDataHandler( ) {
		super( );
	}

	/**
	 * Creates a new CSVDataHandler.
	 * 
	 * @param resource The name of the directory containing the files to be used.
	 */
	public CSVDataHandler( String resource ){
		super( resource );
	}
		
	/**
	 * Loads the data
	 */
	public void load( ) {
		if ( this.resource == null )
			throw new NullPointerException( "The resource to load from was not specified" );
		HashMap <String,String> moleculeData = new HashMap <String,String> ( );
		HashMap <String,String> sampleData = new HashMap <String,String> ( );
		String [ ] headings;
		String [ ] columns;
		this.experiments = new ArrayList <Experiment>( );
		String line = new String( );

		// *********************** load Experiment.txt *************************
		try{ 
			this.file = new Scanner( new File( this.resource+File.separator+"Experiment.txt" ));
		} catch( FileNotFoundException e ) {
			JSysNet.message( "Unable to load "+this.resource+File.separator+"Experiment.txt. The file was not found. No Data has been imported" );
			this.experiments = new ArrayList <Experiment>( );
		}
		if ( ! file.hasNext( ) )
		{
			JSysNet.message(resource+File.pathSeparator+"Experiment.txt does not appear to be a valid file. "
				+ "No Data has been imported.");
			return;
		}
		line = file.nextLine( );
		headings = line.split( "," );
		HashMap <String,String> columnsHashMap;
		while ( file.hasNextLine( )) {
			line = file.nextLine( );
			columns = line.split( "," );
			int columnLength = columns.length;
			columnsHashMap = new HashMap <String,String>( );
			for ( int i=headings.length-1; i >= 0;  i-- ) {
				columnsHashMap.put( headings[ i ].trim( ).toLowerCase( ), 
					( columnLength > i ) ? columns[ i ].trim( ) : "" );
			}
			this.addExperiment( new Experiment( columnsHashMap ));
		}
		this.file.close( );

		// *********************** load Molecule.txt ***************************
		try {
			this.file = new Scanner( new File( resource+File.separator+"Molecule.txt" ));
		} catch( FileNotFoundException e ) {
			JSysNet.message( "Unable to load "+resource+File.separator+"Molecule.txt. The file was not found. No Data has been imported" );
			this.experiments = new ArrayList <Experiment>( );
			return;
		}
		line = file.nextLine( );
		headings = line.split( "," );
		moleculeArrayList = new ArrayList <HashMap<String,String>>( );
		while( file.hasNextLine( )) {
			line = file.nextLine( );
			columns = line.split( "," );
			int columnLength = columns.length;
			columnsHashMap = new HashMap <String,String>( );
			for ( int i=headings.length-1; i >= 0; i-- ) {
				columnsHashMap.put( headings[ i ].trim( ).toLowerCase( ), 
					( columnLength > i ) ? columns[ i ].trim( ) : "" );
			}
			moleculeArrayList.add( columnsHashMap );

		}
		this.file.close( );

		// *********************** load Sample.txt ***************************
		try {
			this.file = new Scanner( new File( resource+File.separator+"Sample.txt" ));
		} catch( FileNotFoundException e ) {
			JSysNet.message( "Unable to load "+resource+File.separator+"Sample.txt. The file was not found. No Data has been imported" );
			this.experiments = new ArrayList <Experiment>( );
			return;
		}
		line = file.nextLine( );
		headings = line.split( "," );
		sampleArrayList = new ArrayList <HashMap<String,String>>( );
		while( file.hasNextLine( )) {
			line = file.nextLine( );
			columns = line.split( "," );
			int columnLength = columns.length;
			columnsHashMap = new HashMap <String,String>( );
			for ( int i=headings.length-1; i >= 0; i-- ) {
				columnsHashMap.put( headings[ i ].trim( ).toLowerCase( ), 
					( columnLength > i ) ? columns[ i ].trim( ) : "" );
			}
			sampleArrayList.add( columnsHashMap );

		}
		this.file.close( );

		// *********************** load Data.txt ***************************
		try {
			this.file = new Scanner( new File( resource+File.separator+"Data.txt" ));
		} catch( FileNotFoundException e ) {
			JSysNet.message( "Unable to load "+resource+File.separator+"Data.txt. The file was not found. No Data has been imported" );
			this.experiments = new ArrayList <Experiment>( );
			return;
		}
		line = file.nextLine( );
		headings = line.split( "," );
		dataArrayList = new ArrayList <HashMap<String,String>>( );
		while( file.hasNextLine( )) {
			line = file.nextLine( );
			columns = line.split( "," );
			int columnLength = columns.length;
			columnsHashMap = new HashMap <String,String>( );
			for ( int i=headings.length-1; i >= 0; i-- ) {
				columnsHashMap.put( headings[ i ].trim( ).toLowerCase( ), 
					( columnLength > i ) ? columns[ i ].trim( ) : "" );
			}
			dataArrayList.add( columnsHashMap );

		}

		for( int i=0,l=dataArrayList.size( ); i < l; i++ ) {
			// Create the Molecule Objects
			HashMap <String,String> moleculeDataHashMap = dataArrayList.get( i );
			Molecule mol = new Molecule( );
			String [ ] molAttr = moleculeDataHashMap.keySet( ).toArray( new String[ 0 ] );

			// Add the first set of attributes to the Molecule Objects
			for ( int j=0,m=molAttr.length; j < m; j++ ) {
				mol.setAttribute( molAttr[ j ], moleculeDataHashMap.get( molAttr[ j ]));
			}
			HashMap <String,String>extraMoleculeData = null; 
			ListIterator <HashMap<String,String>>moleculeArrayListIterator = moleculeArrayList.listIterator( );

			// See if threre is more information for this molecule in molecule.txt
			while ( moleculeArrayListIterator.hasNext( ) ) {
				extraMoleculeData = moleculeArrayListIterator.next( );
				if ( mol.getAttribute( "id" ).equals( extraMoleculeData.get( "id" )) &&
					mol.getAttribute( "group_name" ).equals( extraMoleculeData.get( "group_name" )) &&
					mol.getAttribute( "exp_id" ).equals( extraMoleculeData.get( "exp_id" )))
					break;
				extraMoleculeData = null;
			}
			
			// Add the second set of attributes to the Molecule Objects
			if ( extraMoleculeData != null ) {
				String [] extraMolAttr = extraMoleculeData.keySet( ).toArray( new String[ 0 ] );
				for( int j=0,m=extraMolAttr.length; j < m; j++ ) {
					mol.setAttribute( extraMolAttr[ j ], extraMoleculeData.get( extraMolAttr[ j ] ));
				}
			}
			// Add the Molecules to their appropriate experiment
			String exp_id = mol.getAttribute( "exp_id" );
			for ( int j=0,m=this.experiments.size( ); j < m; j++ ) {
				if ( exp_id.equals( this.experiments.get( j ).getAttributes( ).get( "exp_id" ))) {
					mol.setExperiment( this.experiments.get( j ));
					break;
				}
			}
				
		}
		this.file.close( );

	}

	/**
	 * For writing new data back to disk; Currently not implemented.
	 */
	public boolean write( ){
		return false;
	}

	/**
	 * For writing a copy of the data to disk; Currently not implemented.
	 * 
	 * @param resource The directory to write the files to.
	 */
	public boolean write( String resource ){
		return false;
	}
}
