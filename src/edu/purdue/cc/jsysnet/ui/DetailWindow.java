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

package edu.purdue.cc.jsysnet.ui;

import edu.purdue.cc.jsysnet.util.Molecule;
import edu.purdue.cc.jsysnet.util.Correlation;
import edu.purdue.bbc.util.Range;
import edu.purdue.bbc.util.Settings;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import edu.purdue.cc.jsysnet.util.*;

/**
 * A class for displaying detail about a particular Molecule or Correlation.
 */
public class DetailWindow extends JFrame implements TabbedWindow {

	private JTabbedPane tabPane = new ClosableTabbedPane( );
	private Range correlationRange;
	private Experiment experiment;

	/**
	 * Creates a new DetailWindow with the specified title and Correlation Range.
	 * 
	 * @param experiment The experiment this window is associated with.
	 * @param range The Correlation range for this window.
	 */
	public DetailWindow( Experiment experiment, Range range ) {
		super( experiment.getAttribute( "description" ));
		this.correlationRange = range.clone( );
		Settings settings = Settings.getSettings( );
		int width  = settings.getInt( "detailWindowWidth"  );
		int height = settings.getInt( "detailWindowHeight" );
		int x = Math.max( 0, Math.min( 
		  settings.getInt( "detailWindowXPosition" ), 
			settings.getInt( "desktopWidth" ) - width ));
		int y = Math.max( 0, Math.min( 
		  settings.getInt( "detailWindowYPosition" ), 
			settings.getInt( "desktopHeight" ) - height ));
		
		this.setBounds( x, y, width, height );
		this.setLayout( new BorderLayout( ));
		this.getContentPane( ).add( tabPane, BorderLayout.CENTER );
		this.setVisible( true );

		this.addWindowListener(new WindowAdapter( ) {
		  public void windowClosing( WindowEvent e ) {
				JFrame f = (JFrame)e.getSource( );
				Settings settings = Settings.getSettings( );
				settings.setInt( "detailWindowXPosition", f.getX( ));
				settings.setInt( "detailWindowYPosition", f.getY( ));
				settings.setInt( "detailWindowWidth", f.getWidth( ));
				settings.setInt( "detailWindowHeight", f.getHeight( ));
			}
		});
	}

	/**
	 * Creates a new DetailWindow and shows detail about the specified Molecule.
	 * 
	 * @param experiment The experiment this window is associated with.
	 * @param molecule The Molecule to show detail about.
	 * @param range The correlation range for this DetailWindow.
	 */
	public DetailWindow( Experiment experiment, 
	                     Molecule molecule, Range range ) {
		this( experiment, range );
		this.show( molecule );
	}

	/**
	 * Creates a new DetailWindow and shows detail about the specified Correlation.
	 * 
	 * @param experiment The experiment this window is associated with.
	 * @param correlation The Correlation to show detail about.
	 * @param range The correlation range for this DetailWindow.
	 */
	public DetailWindow( Experiment experiment, 
	                     Correlation correlation, Range range ) {
		this( experiment, range );
		this.show( correlation );
	}

	public Experiment getExperiment( ) {
		return this.experiment;
	}

	/**
	 * Adds a new tab to display information about the specified Molecule.
	 * 
	 * @param molecule The Molecule to show detail about.
	 */
	public void show( Molecule molecule ) {
		this.tabPane.setSelectedComponent( 
			this.tabPane.add( molecule.toString( ), 
			  new MoleculeDetailPanel( molecule, this.correlationRange, this )));
	}

	/**
	 * Adds a new tab to display information about the specified Correlation.
	 * 
	 * @param correlation The Correlation to show detail about.
	 */
	public void show( Correlation correlation ) {
		this.tabPane.setSelectedComponent( 
			this.tabPane.add( correlation.toString( ),
				new CorrelationDetailPanel( correlation, this.correlationRange, this )));
	}

	/**
	 * The addTab method of the TabbedWindow interface. Creates a new tab and adds the
	 * specified component to it.
	 * @see edu.purdue.cc.jsysnet.ui.TabbedWindow#addTab(java.lang.String, java.awt.Component)
	 * 
	 * @param title The title for the new tab.
	 * @param c The component to add to it.
	 */
	public void addTab( String title, Component c ) {
		this.tabPane.addTab( title, c );
		this.tabPane.setSelectedComponent( c );
	}

	/**
	 * The newWindow method of the TabbedWindow interface. Creates a new DetailWindow.
	 * @see edu.purdue.cc.jsysnet.ui.TabbedWindow#newWindow()
	 * 
	 * @return A new instance of this class.
	 */
	public TabbedWindow newWindow( ) {
		return new DetailWindow( this.experiment, this.correlationRange );
	}

}
