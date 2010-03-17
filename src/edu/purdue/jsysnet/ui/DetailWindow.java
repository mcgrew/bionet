package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Correlation;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Dimension;
import edu.purdue.jsysnet.util.*;

public class DetailWindow extends JFrame {

	private JTabbedPane tabPane = new JTabbedPane( );

	public DetailWindow( String title ) {
		super( title );
		this.setSize( new Dimension( 600, 400 ));
		this.setLayout( new BorderLayout( ) );
		this.getContentPane( ).add( tabPane, BorderLayout.CENTER );
		this.setVisible( true );

	}

	public DetailWindow( String title, Molecule molecule ) {
		this( title );
		this.show( molecule );
	}

	public DetailWindow( String title, Correlation correlation ) {
		this( title );
		//this.show( correlation )

	}

	public void show( Molecule molecule ) {
		this.tabPane.add( molecule.toString( ), new MoleculeDetailPanel( molecule ));
	}

	public void show( Correlation correlation ) {
	}

}
