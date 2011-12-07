/*

This file is part of BioNet.

BioNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BioNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with BioNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.bionet.ui;

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Range;
import edu.purdue.bbc.util.Settings;
import edu.purdue.cc.bionet.util.Correlation;
import edu.purdue.cc.bionet.util.CorrelationSet;
import edu.purdue.cc.bionet.util.Experiment;
import edu.purdue.cc.bionet.util.Molecule;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


class DataTable extends JTable {

	public DataTable( Object[][] values, Object[] columnNames ) {
		super( values, columnNames );
		this.setBackground( Color.WHITE );
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
	public static DataTable getMoleculeTable( Collection<Correlation> correlations,
	                                          Molecule molecule ) {
		Map<String,String> attributes = molecule.getAttributes( );
		String [][] values = new String[ attributes.size( )][ 2 ];
		int i=0;
		for ( Map.Entry<String,String> attribute : attributes.entrySet( )) {
			values[ i ][ 0 ] = attribute.getKey( );
			values[ i ][ 1 ] = attribute.getValue( );
			i++;
		}
		Language language = Settings.getLanguage( );
		return new DataTable( values, new String[]{ 
			language.get( "Attribute" ),
			language.get( "Value" ),
		});
	}

	/**
	 * Creates an immutable JTable of Correlated Molecules.
	 * 
	 * @param molecule The Molecule to find the correlated Molecules for.
	 * @param correlationRange The range of the valid correlations to display.
	 * @return 
	 */
	public static DataTable getCorrelatedTable( CorrelationSet correlations,
	                                            Molecule molecule, 
	                                            Range correlationRange,
	                                            int correlationMethod ) {
		DefaultTableModel returnValue = new DefaultTableModel( );
		List <String[ ]> data = new ArrayList<String[ ]>( );
		double value;
		boolean mz = true;
		for ( Correlation c : correlations.getCorrelations( molecule )) {
			value = c.getValue( correlationMethod );
			if ( correlationRange.contains( Math.abs( value ))) {
				String [] row = new String [ 3 ];
				row[ 0 ] = c.getOpposite( molecule ).getId( );
				row[ 1 ] = c.getOpposite( molecule ).getAttribute( "m/z" );
				if ( "".equals( row[ 1 ] )) {
					row[ 1 ] = c.getOpposite( molecule ).getAttribute( "mw" );
				}
				if ( !"".equals( row[ 1 ] )) {
					mz = false;
				}
				row[ 2 ] = String.format( "%.3f", value );
				data.add( row );
			}
		}
		Language language = Settings.getLanguage( );
		return new DataTable( data.toArray( new String[ data.size( ) ][ 3 ]), 
			new String[]{ 
				language.get( "Molecule" ),
				( mz ) ? language.get( "MW" ) : language.get( "M/Z" ), 
				language.get( "Correlation" )});
	}
}

