/*

Copyright: 2010 Bindley Bioscience Center, Purdue University

License: MIT license.

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 * A class for reading character separated tabular data.
 */
public class CsvTableReader implements Iterator<Map<String,String>> {
	protected String delimiter;
	protected String[] keys;
	protected Scanner scanner;

	public CsvTableReader ( File file ) throws FileNotFoundException {
		this( new FileInputStream( file ), "," );
	}

	public CsvTableReader ( File file, String delimiter ) throws FileNotFoundException{
		this( new FileInputStream( file ), delimiter );
	}

	public CsvTableReader( InputStream input ) {
		this( input, "," );
	}

	/**
	 * Creates a new CSV Reader
	 * 
	 * @param 
	 * @return 
	 */
	public CsvTableReader ( InputStream input, String delimiter ) {
		this.delimiter = delimiter;
		this.scanner = new Scanner( input );
		String[] keys = scanner.nextLine( ).split( delimiter );
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
		String[] values = scanner.nextLine( ).split( this.delimiter );
		for ( int i=0; i < keys.length; i++ ) {
			if ( i > values.length ) {
				returnValue.put( keys[ i ], null );
			} else {
				returnValue.put( keys[ i ], values[ i ]);
			}
		}
		return returnValue;
	}

	/**
	 * This optional  method is not implemented.
	 */
	public void remove( ) { }

}

