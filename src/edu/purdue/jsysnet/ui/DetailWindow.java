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

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Dimension;
import edu.purdue.jsysnet.util.*;

public class DetailWindow extends JFrame {

	private JTabbedPane tabPane = new ClosableTabbedPane( );
	private Range correlationRange;

	public DetailWindow( String title, Range range ) {
		super( title );
		this.correlationRange = range;
		this.setSize( new Dimension( 800, 500 ));
		this.setLayout( new BorderLayout( ));
		this.getContentPane( ).add( tabPane, BorderLayout.CENTER );
		this.setVisible( true );

	}

	public DetailWindow( String title, Molecule molecule, Range range ) {
		this( title, range );
		this.show( molecule );
	}

	public DetailWindow( String title, Correlation correlation, Range range ) {
		this( title, range );
		this.show( correlation );
	}

	public void show( Molecule molecule ) {
		this.tabPane.setSelectedComponent( 
			this.tabPane.add( molecule.toString( ), 
			  new MoleculeDetailPanel( molecule, this.correlationRange, this )));
	}

	public void show( Correlation correlation ) {
		this.tabPane.setSelectedComponent( 
			this.tabPane.add( correlation.toString( ),
				new CorrelationDetailPanel( correlation, this.correlationRange, this )));
	}

}
