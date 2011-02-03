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

import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.cc.jsysnet.util.Experiment;
import edu.purdue.cc.jsysnet.util.Sample;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

public class JavaMLTranslator extends InputStream {
	private Collection <Experiment> experiments;
	private Collection <String> molecules;
	private Iterator<String> moleculeIterator;
	private StringBuilder buffer;
	private Collection<Sample> samples;
	private boolean numericOnly;

	public JavaMLTranslator ( Collection <Experiment> experiments ) {
		super( );
		this.experiments = experiments;
		this.molecules = new TreeSet<String>( );
		this.samples = new TreeSet<Sample>( );
		for ( Experiment e : experiments ) {
			samples.addAll( e.getSamples( ));
			for( Molecule m : e.getMolecules( )) {
				molecules.add( m.getAttribute( "id" ));
			}
		}
		this.reset( );
	}

	public void close( ) { 
		moleculeIterator = null;
		molecules = null;
	}


	public int read( ) {
		if ( buffer.length( ) == 0 && !this.bufferNext( )) {
			return -1;
		}
		char returnValue = buffer.charAt( 0 );
		buffer.deleteCharAt( 0 );
		return (int)returnValue;
	}

	public int read( char[] cbuf ) {
		return read( cbuf, 0, cbuf.length );
	}

	public int read( char[] cbuf, int off, int len ) {
		// clear the character buffer
		Arrays.fill( cbuf, '\0' );
		while( len > this.buffer.length( ) && this.bufferNext( ));
		len = Math.min( len, this.buffer.length( ));
		len = Math.min( len, cbuf.length - off );
		if ( len > 0 ) {
			buffer.getChars( 0, len, cbuf, off );
			buffer.delete( 0, len );
		}
		if ( len == 0 ) {
			return -1;
		}
		return len;
	}

//	public boolean ready( ) {
//		return true;
//	}

	public void reset( ) {
		moleculeIterator = this.molecules.iterator( );
		buffer = new StringBuilder( );
//		buffer.append( "id," );
//		for ( Experiment exp : this.experiments ) {
//			for ( String att : this.attributes ) {
//				buffer.append( att + "_" + exp.getAttribute( "exp_id" ) + "," );
//			}
//		}
//		buffer.setCharAt( buffer.length( ) - 1, '\n' );
	}

	public long skip( long n ) {
		if ( n <= 0 )
			return 0;
		long remaining = n;
		while ( remaining > this.buffer.length( )) {
			remaining -= this.buffer.length( );
			buffer.setLength( 0 );
			if ( !this.bufferNext( ))
				return n - remaining;
		}
		buffer.delete( 0, (int)remaining );
		return n;
	}

	private boolean bufferNext( ) {
		if ( !moleculeIterator.hasNext( ))
			return false;
		String m = moleculeIterator.next( );
		this.appendToBuffer( m );
		return true;
	}

	private void appendToBuffer( Object m ) {
		buffer.append( m.toString( ) + "," );
		for( Experiment exp : this.experiments ) {
			Molecule molecule = exp.getMolecule( m.toString( ));
			for( Sample sample : this.samples ) {
				if ( molecule != null ) {
					buffer.append( molecule.getSample( sample ).toString( ) + "," );
				} else {
					buffer.append( "0," );
				}
			}
		}
		buffer.setCharAt( buffer.length( )-1, '\n' );
	}
}



