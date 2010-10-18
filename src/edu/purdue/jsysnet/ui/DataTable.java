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

package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Range;
import edu.purdue.jsysnet.util.Settings;
import edu.purdue.jsysnet.util.Language;

import java.util.ArrayList;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


class DataTable extends JTable {

	public DataTable( Object[][] values, Object[] columnNames ) {
		super( values, columnNames );
	}

	public boolean isCellEditable( int row, int col ) {
		return false;
	}

	/**
	 * Creates an immutable JTable of Molecule Attributes.
	 * 
	 * @param molecule The molecule to get The attributes of.
	 * @return A JTable instance containing the appropriate data.
	 */
	public static DataTable getMoleculeTable( Molecule molecule ) {
		String [] attributes = molecule.getAttributeNames( );
		String [][] values = new String[ attributes.length ][ 2 ];
		for ( int i=0; i < attributes.length; i++ ) {
			values[ i ][ 0 ] = attributes[ i ];
			values[ i ][ 1 ] = molecule.getAttribute( attributes[ i ]);
		}
		Language language = Settings.getLanguage( );
		return new DataTable( values, new String[]{ 
			language.get( "Attribute" ),
			language.get( "Value" )
		});
	}

	/**
	 * Creates an immutable JTable of Correlated Molecules.
	 * 
	 * @param molecule The Molecule to find the correlated Molecules for.
	 * @param correlationRange The range of the valid correlations to display.
	 * @return 
	 */
	public static DataTable getCorrelatedTable( Molecule molecule, Range correlationRange ) {
		DefaultTableModel returnValue = new DefaultTableModel( );
	  ArrayList <Correlation> correlations = molecule.getCorrelations( );
		ArrayList <String[ ]> data = new ArrayList<String[ ]>( );
		double value;
		for ( Correlation c : correlations ) {
			value = c.getValue( );
			if ( correlationRange.contains( Math.abs( value ))) {
				String [] row = new String [ 3 ];
				row[ 0 ] = String.format( "%.3f", value );
				row[ 1 ] = c.getOpposite( molecule ).getAttribute( "group_name" );
				row[ 2 ] = c.getOpposite( molecule ).getAttribute( "name" );
				data.add( row );
			}
		}
		Language language = Settings.getLanguage( );
		return new DataTable( data.toArray( new String[ 0 ][ 0 ]), new String[]{ 
			language.get( "Correlation" ), 
			language.get( "Group" ), 
			language.get( "Molecule" ) 
		});
	}
}

