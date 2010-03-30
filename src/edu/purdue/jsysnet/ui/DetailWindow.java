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
		this.setSize( new Dimension( 600, 400 ));
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
		//this.show( correlation )
	}

	public void show( Molecule molecule ) {
		this.tabPane.add( molecule.toString( ), 
		  new MoleculeDetailPanel( molecule, this.correlationRange ));
	}

	public void show( Correlation correlation ) {
	}

}
