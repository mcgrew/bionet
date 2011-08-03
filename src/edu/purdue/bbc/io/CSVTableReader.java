/*

Copyright: 2010 Bindley Bioscience Center, Purdue University

License: X11 license.

	Permission is hereby granted, free of charge, to any person
	obtaining a copy of this software and associated documentation
	files (the "Software"), to deal in the Software without
	restriction, including without limitation the rights to use,
	copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the
	Software is furnished to do so, subject to the following
	conditions:

	The above copyright notice and this permission notice shall be
	included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
	EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
	OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
	NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
	HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
	WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
	FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
	OTHER DEALINGS IN THE SOFTWARE.

*/

package edu.purdue.bbc.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 * A class for reading character separated tabular data.
 */
public class CSVTableReader implements Iterator<Map<String,String>> {
	protected char delimiter;
	protected String[] keys;
	protected Scanner scanner;
	protected boolean useQuotes;

	/**
	 * Creates a new CSV Reader
	 * 
	 * @param file The file to read data from.
	 */
	public CSVTableReader ( File file ) throws FileNotFoundException {
		this( new Scanner( file ), ',' );
	}

	/**
	 * Creates a new CSV Reader
	 * 
	 * @param file The file to read data from.
	 * @param delimiter The delimiter between fields, usually a comma, semicolon,
	 *	or tab character.
	 */
	public CSVTableReader ( File file, char delimiter ) 
	                        throws FileNotFoundException {
		this( new Scanner( file ), delimiter, true );
	}

	/**
	 * Creates a new CSV Reader
	 * 
	 * @param file The file to read data from.
	 * @param delimiter The delimiter between fields, usually a comma, semicolon,
	 *	or tab character.
	 * @param useQuotes Whether or not to honor quotes when parsing a file.
	 */
	public CSVTableReader ( File file, char delimiter, boolean useQuotes )
	                        throws FileNotFoundException {
		this( new Scanner( file ), delimiter, useQuotes );
	}

	/**
	 * Creates a new CSV Reader
	 * 
	 * @param input The InputStream to read data from.
	 *	or tab character.
	 */
	public CSVTableReader( InputStream input ) {
		this( new Scanner( input ), ',', true );
	}

	/**
	 * Creates a new CSV Reader
	 * 
	 * @param input The InputStream to read data from.
	 * @param delimiter The delimiter between fields, usually a comma, semicolon,
	 *	or tab character.
	 */
	public CSVTableReader ( InputStream input, char delimiter ) {
		this( new Scanner( input ), delimiter, true );
	}

	/**
	 * Creates a new CSV Reader
	 * 
	 * @param input The InputStream to read data from.
	 * @param delimiter The delimiter between fields, usually a comma, semicolon,
	 *	or tab character.
	 * @param useQuotes Whether or not to honor quotes when parsing a file.
	 */
	public CSVTableReader ( InputStream input, char delimiter, 
	                        boolean useQuotes ) {
		this( new Scanner( input ), delimiter, useQuotes );
	}

	/**
	 * Creates a new CSV Reader
	 * 
	 * @param input The Scanner to read data from.
	 *	or tab character.
	 */
	public CSVTableReader( Scanner input ) {
		this( input, ',', true );
	}

	/**
	 * Creates a new CSV Reader
	 * 
	 * @param input The Scanner to read data from.
	 * @param delimiter The delimiter between fields, usually a comma, semicolon,
	 *	or tab character.
	 */
	public CSVTableReader ( Scanner input, char delimiter ) {
		this( input, delimiter, true );
	}

	/**
	 * Creates a new CSV Reader
	 * 
	 * @param input The Scanner to read data from.
	 * @param delimiter The delimiter between fields, usually a comma, semicolon,
	 *	or tab character.
	 * @param useQuotes Whether or not to honor quotes when parsing a file.
	 */
	public CSVTableReader ( Scanner input, char delimiter, 
	                        boolean useQuotes ) {
		this.delimiter = delimiter;
		this.scanner = input;
		this.useQuotes = useQuotes;
		this.keys = this.splitLine( this.scanner.nextLine( ), delimiter, useQuotes );
	}

	protected String [] splitLine( String input, char delimiter, 
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

	/**
	 * Turns on or off the stripping of quotes.
	 * 
	 * @param use Whether or not to honor quotes when parsing a file
	 */
	public void setUseQuotes( boolean use ) {
		this.useQuotes = use;
	}

	/**
	 * Determines whether there is another line of values in the file.
	 * 
	 * @return True if there is another line of values.
	 */
	public boolean hasNext( ) {
		return this.scanner.hasNextLine( );
	}

	/**
	 * Returns the next line in the CSV as a Map.
	 * 
	 * @return A Map containing the key/value pairs for the next line in the file.
	 */
	public Map<String,String> next( ) {
		HashMap<String,String> returnValue = new HashMap<String,String>( );
		String[] values = this.splitLine( this.scanner.nextLine( ), this.delimiter,
		                                                  this.useQuotes );
		for ( int i=0; i < this.keys.length; i++ ) {
			if ( i >= values.length ) {
				returnValue.put( keys[ i ], "" );
			} else {
				if ( this.useQuotes ) {
					returnValue.put( keys[ i ], values[ i ] );
				} else {
					returnValue.put( keys[ i ], values[ i ]);
				}
			}
		}
		return returnValue;
	}

	/**
	 * This optional  method is not implemented.
	 */
	public void remove( ) { }

	/**
	 * Closes the underlying Scanner for this CSVTableReader
	 */
	public void close( ) {
		this.scanner.close( );
	}

}

