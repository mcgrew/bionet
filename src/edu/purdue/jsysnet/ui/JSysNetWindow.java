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

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.Window;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import edu.purdue.jsysnet.util.Experiment;
import edu.purdue.jsysnet.util.Settings;
import edu.purdue.jsysnet.util.Language;
import edu.purdue.jsysnet.io.DataHandler;
import edu.purdue.jsysnet.io.CSVDataHandler;

import net.sourceforge.helpgui.gui.MainFrame;

public class JSysNetWindow extends JFrame implements ActionListener,TabbedWindow {

	private JTabbedPane tabPane = new ClosableTabbedPane( );
	
	// Menu elements
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem newWindowFileMenuItem;
	private JMenuItem openFileMenuItem;
	private JMenuItem saveFileMenuItem;
	private JMenuItem printFileMenuItem;
	private JMenuItem exitFileMenuItem;
//	private JMenu databaseMenu;
//	private JMenu setupDatabaseMenu;
//	private JMenuItem addSetupDatabaseMenuItem;
//	private JMenuItem removeSetupDatabaseMenuItem;
//	private JMenuItem connectDatabaseMenuItem;
//	private JMenu clusteringMenu;
//	private JMenuItem ldaClusteringMenuItem;
	private JMenu helpMenu;
	private JMenuItem contentsHelpMenuItem;
	private JMenuItem aboutHelpMenuItem;

	public JSysNetWindow( ) {
		this( "JSysNet" );
	}

	public JSysNetWindow ( String title ) {

		super( title );
		this.setLayout( new BorderLayout( ));
		Settings settings = Settings.getSettings( );
		int width = settings.getInt( "windowWidth" );
		int height = settings.getInt( "windowHeight" );
		int x = Math.max( 0, Math.min( 
		  settings.getInt( "windowXPosition" ), 
			settings.getInt( "desktopWidth" ) - width ));
		int y = Math.max( 0, Math.min( 
		  settings.getInt( "windowYPosition" ), 
			settings.getInt( "desktopHeight" ) - height ));
		
		this.setBounds( x, y, width, height );

		this.setupMenu( );

		this.getContentPane( ).add( tabPane, BorderLayout.CENTER );
		this.tabPane.setVisible( true );

		this.setVisible( true );
		this.repaint( );

		this.addWindowListener(new WindowAdapter() {
		  public void windowClosing(WindowEvent e) {
				JFrame f = (JFrame)e.getSource( );
				Settings settings = Settings.getSettings( );
				settings.setInt( "windowXPosition", f.getX( ));
				settings.setInt( "windowYPosition", f.getY( ));
				settings.setInt( "windowWidth", f.getWidth( ));
				settings.setInt( "windowHeight", f.getHeight( ));
				if ( this.getWindowCount( ) == 1 )
			    System.exit(0);
			}
			private int getWindowCount( ) {
				int returnValue = 0;
				for( Window w : Window.getWindows( )) {
					if ( w.isShowing( ))
						returnValue++;
				}
				return returnValue;
			}
		});

	}

	public void addTab( String title, Component c ) {
		this.tabPane.addTab( title, c );
	}
	
	public TabbedWindow newWindow( ) {
		return new JSysNetWindow( this.getTitle( ));
	}

	private void setupMenu( ) {
		Language language = Settings.getLanguage( );

		this.menuBar = new JMenuBar( );
		this.fileMenu = new JMenu( language.get( "File" ));
		this.newWindowFileMenuItem = new JMenuItem( language.get( "New Window" ), KeyEvent.VK_N );
		this.openFileMenuItem = new JMenuItem( language.get( "Open" ) + "...", KeyEvent.VK_O );
		this.saveFileMenuItem = new JMenuItem( language.get( "Save" ) + "...", KeyEvent.VK_S );
		this.printFileMenuItem = new JMenuItem( language.get( "Print" ) + "...", KeyEvent.VK_P );
		this.exitFileMenuItem = new JMenuItem( language.get( "Close" ), KeyEvent.VK_C );
//	this.databaseMenu = new JMenu( "Database" );
//	this.setupDatabaseMenu = new JMenu( "Setup" );
//	this.addSetupDatabaseMenuItem = new JMenuItem( "Add...", KeyEvent.VK_A );
//	this.removeSetupDatabaseMenuItem = new JMenuItem( "Remove...", KeyEvent.VK_R );
//	this.connectDatabaseMenuItem = new JMenuItem( "Connect...", KeyEvent.VK_C );
//	this.clusteringMenu = new JMenu( "Clustering" );
//	this.ldaClusteringMenuItem = new JMenuItem( "LDA", KeyEvent.VK_L );
		this.helpMenu = new JMenu( language.get( "Help" ));
		this.contentsHelpMenuItem = new JMenuItem( language.get( "Contents" ), KeyEvent.VK_C );
		this.aboutHelpMenuItem = new JMenuItem( language.get( "About" ), KeyEvent.VK_A );

		// FILE MENU
		this.fileMenu.setMnemonic( KeyEvent.VK_F );
		this.fileMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "Perform file operations" ));
		this.fileMenu.add( this.newWindowFileMenuItem );
		this.fileMenu.add( this.openFileMenuItem );
		this.fileMenu.add( this.saveFileMenuItem );
//		this.fileMenu.add( this.printFileMenuItem );
		this.fileMenu.add( this.exitFileMenuItem );
		this.newWindowFileMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK ));
		this.openFileMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK ));
		this.saveFileMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK ));
		this.printFileMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK ));
		this.exitFileMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK ));

		// DATABASE MENU
//		this.databaseMenu.setMnemonic( KeyEvent.VK_D );
//		this.databaseMenu.getAccessibleContext( ).setAccessibleDescription(
//			"Manage Database Connections" );
//		this.databaseMenu.add( this.setupDatabaseMenu );
//		this.setupDatabaseMenu.add( this.addSetupDatabaseMenuItem );
//		this.setupDatabaseMenu.add( this.removeSetupDatabaseMenuItem );
//		this.databaseMenu.add( this.connectDatabaseMenuItem );


		//CLUSTERING MENU
//		this.clusteringMenu.setMnemonic( KeyEvent.VK_L );
//		this.clusteringMenu.add( this.ldaClusteringMenuItem );

		//HELP MENU
		this.helpMenu.setMnemonic( KeyEvent.VK_H );
		this.helpMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "JSysNet Help" ));
		this.helpMenu.add( this.contentsHelpMenuItem );
		this.helpMenu.add( this.aboutHelpMenuItem );

		this.menuBar.add( this.fileMenu );
//		this.menuBar.add( this.databaseMenu );
//		this.menuBar.add( this.clusteringMenu );
		this.menuBar.add( this.helpMenu );

		this.setJMenuBar( this.menuBar );

		this.addMenuListeners( );

//		this.setVisible( true );
	}

	private void addMenuListeners( ) {
		this.newWindowFileMenuItem.addActionListener( this );
		this.openFileMenuItem.addActionListener( this );
		this.saveFileMenuItem.addActionListener( this );
		this.printFileMenuItem.addActionListener( this );
		this.exitFileMenuItem.addActionListener( this );
//		this.addSetupDatabaseMenuItem.addActionListener( this );
//		this.removeSetupDatabaseMenuItem.addActionListener( this );
//		this.connectDatabaseMenuItem.addActionListener( this );
		this.contentsHelpMenuItem.addActionListener( this );
		this.aboutHelpMenuItem.addActionListener( this );
	}

	public DataHandler openCSV( ) {
		JFileChooser fc = new JFileChooser( Settings.getSettings( ).getProperty( "lastOpenCSV" ));
		fc.setFileFilter( new CSVFileFilter( ));
		fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );	
		int options = fc.showOpenDialog( this );
		if ( options == JFileChooser.APPROVE_OPTION ) {
			File selected = fc.getSelectedFile( );
			if ( !selected.isDirectory( ))
				selected = selected.getParentFile( );
			Settings.getSettings( ).setProperty( "lastOpenCSV", selected.getAbsolutePath( ));
			DataHandler data = new CSVDataHandler( selected.getAbsolutePath( ));
			data.load( );
			return data;
		}
		return null;
	
	}

	private class CSVFileFilter extends FileFilter {

		public boolean accept( File f ) {
			return ( f.isDirectory( ));
		}

		public String getDescription( ) {
			return "JSysNet data folder";
		}

	}

	public void actionPerformed( ActionEvent e ) {
		if ( Settings.getSettings( ).getBoolean( "debug" )) {
			System.err.println( String.format( "ActionEvent fired:" ));
			System.err.println( "\tactionCommand: "+e.getActionCommand( ));
			System.err.println( "\t  paramString: "+e.paramString( ));
		}

		Object item = e.getSource( );
		if ( item == this.newWindowFileMenuItem ) {
			this.newWindow( );
		} else if ( item == this.openFileMenuItem ) {
			DataHandler data = this.openCSV( );
			if ( data == null ) {
				return;
			}
			Map.Entry<Integer,List> choice = experimentSelection( data.getExperiments( ));
			if ( choice == null )
				return;

			if ( choice.getKey( ).intValue( ) == ExperimentSelectionDialog.CORRELATION_VIEW ) {
				CorrelationDisplayPanel cdp = new CorrelationDisplayPanel( );
				if( cdp.createGraph( (Experiment)choice.getValue( ).get( 0 )))
					this.tabPane.addTab( cdp.getTitle( ), cdp );
			}

		} else if ( item == this.exitFileMenuItem ) {
			this.dispose( );

		} else if ( item == this.contentsHelpMenuItem ) {
			new MainFrame( "/docs/help/", "plastic" ).setVisible( true );

		} else if ( item == this.aboutHelpMenuItem ) {
			new About( ).setVisible( true );
		}
		

	}

	/**
	 * Brings up a dialog to allow you to select the appropriate experiment. If
	 *	only one experiment is available, no dialog is shown an this method simply
	 *	returns that experiment.
	 * 
	 * @param experiments An ArrayList containing the possible Experiments
	 * @return The experiment you selected, or null if you pressed cancel, or
	 *	if no experiments are available
	 */
	public Map.Entry<Integer,List> experimentSelection( List <Experiment> experiments ) {
		if ( experiments.size( ) < 1 ) {
			System.err.println( 
				Settings.getLanguage( ).get( "These files do not appear to contain any data!" ));
			return null;
		}
//		if ( experiments.size( ) == 1 ) {
//			return experiments.get( 0 );
//		}
		return ExperimentSelectionDialog.showInputDialog( 
			this, Settings.getLanguage( ).get( "Experiment Selection" ), (List)experiments );
	}
	
}

