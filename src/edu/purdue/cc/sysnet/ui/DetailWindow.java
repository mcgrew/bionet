/*

This file is part of SysNet.

SysNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SysNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SysNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.sysnet.ui;

import edu.purdue.bbc.util.Range;
import edu.purdue.bbc.util.Settings;
import edu.purdue.cc.sysnet.util.Correlation;
import edu.purdue.cc.sysnet.util.CorrelationSet;
import edu.purdue.cc.sysnet.util.Molecule;
import edu.purdue.cc.sysnet.util.Experiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

/**
 * A class for displaying detail about a particular Molecule or Correlation.
 */
public class DetailWindow extends JFrame implements TabbedWindow {

	private JTabbedPane tabPane = new ClosableTabbedPane( );
	private Range correlationRange;
	private CorrelationSet correlations;
	private int correlationMethod;

	/**
	 * Creates a new DetailWindow with the specified title and Correlation Range.
	 * 
	 * @param correlations The correlations this window is associated with.
	 * @param range The Correlation range for this window.
	 */
	public DetailWindow( CorrelationSet correlations, Range range, 
	                     int correlationMethod ) {
		super( "" );
		Logger.getLogger( getClass( )).debug( String.format(
			"Creating detail window:\n\tRange:      %s\n", range));
		this.correlations = correlations;
		this.correlationRange = range.clone( );
		this.correlationMethod = correlationMethod;
		Settings settings = Settings.getSettings( );
		int width  = settings.getInt( "window.detail.width"  );
		int height = settings.getInt( "window.detail.height" );
		int x = Math.max( 0, Math.min( 
		  settings.getInt( "window.detail.position.x" ), 
			settings.getInt( "desktop.width" ) - width ));
		int y = Math.max( 0, Math.min( 
		  settings.getInt( "window.detail.position.y" ), 
			settings.getInt( "desktop.height" ) - height ));
		
		this.setBounds( x, y, width, height );
		this.setLayout( new BorderLayout( ));
		this.getContentPane( ).add( tabPane, BorderLayout.CENTER );
		this.setVisible( true );

		this.addWindowListener(new WindowAdapter( ) {
		  public void windowClosing( WindowEvent e ) {
				JFrame f = (JFrame)e.getSource( );
				Settings settings = Settings.getSettings( );
				settings.setInt( "window.detail.position.x", f.getX( ));
				settings.setInt( "window.detail.position.y", f.getY( ));
				settings.setInt( "window.detail.width", f.getWidth( ));
				settings.setInt( "window.detail.height", f.getHeight( ));
			}
		});
	}

	/**
	 * Creates a new DetailWindow and shows detail about the specified Molecule.
	 * 
	 * @param correlations The correlations this window is associated with.
	 * @param molecule The Molecule to show detail about.
	 * @param range The correlation range for this DetailWindow.
	 */
	public DetailWindow( CorrelationSet correlations, Molecule molecule, 
	                     Range range, int correlationMethod ) {
		this( correlations, range, correlationMethod );
		this.show( molecule );
	}

	/**
	 * Creates a new DetailWindow and shows detail about the specified Correlation.
	 * 
	 * @param correlations The correlations this window is associated with.
	 * @param correlation The Correlation to show detail about.
	 * @param range The correlation range for this DetailWindow.
	 */
	public DetailWindow( CorrelationSet correlations, 
	                     Correlation correlation,
	                     Range range, int correlationMethod ) {
		this( correlations, range, correlationMethod );
		this.show( correlation );
	}

	/**
	 * Adds a new tab to display information about the specified Molecule.
	 * 
	 * @param molecule The Molecule to show detail about.
	 */
	public void show( Molecule molecule ) {
		this.tabPane.setSelectedComponent( 
			this.tabPane.add( molecule.toString( ), 
			  new MoleculeDetailPanel( molecule, this.correlationRange, 
			                           this, this.correlationMethod )));
	}

	/**
	 * Adds a new tab to display information about the specified Correlation.
	 * 
	 * @param correlation The Correlation to show detail about.
	 */
	public void show( Correlation correlation ) {
		this.tabPane.setSelectedComponent( 
			this.tabPane.add( correlation.toString( ),
				new CorrelationDetailPanel( correlation, this.correlationRange, 
				                            this, this.correlationMethod )));
	}

	/**
	 * The addTab method of the TabbedWindow interface. Creates a new tab and adds the
	 * specified component to it.
	 * @see edu.purdue.cc.sysnet.ui.TabbedWindow#addTab(java.lang.String, java.awt.Component)
	 * 
	 * @param title The title for the new tab.
	 * @param c The component to add to it.
	 */
	public void addTab( String title, Component c ) {
		this.tabPane.addTab( title, c );
		this.tabPane.setSelectedComponent( c );
	}

	/**
	 * Returns the correlations associated with this DetailWindow
	 * 
	 * @return The correlations associated with this DetailWindow
	 */
	public CorrelationSet getCorrelations( ) {
		return this.correlations;
	}

	/**
	 * The newWindow method of the TabbedWindow interface. Creates a new DetailWindow.
	 * @see edu.purdue.cc.sysnet.ui.TabbedWindow#newWindow()
	 * 
	 * @return A new instance of this class.
	 */
	public TabbedWindow newWindow( ) {
		return new DetailWindow( this.correlations, this.correlationRange,
		                         this.correlationMethod );
	}

}
