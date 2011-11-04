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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;
import edu.purdue.cc.sysnet.io.CSVDataReader;
import edu.purdue.cc.sysnet.io.MetsignDataReader;
import edu.purdue.cc.sysnet.io.DataReader;
import edu.purdue.cc.sysnet.util.Experiment;

import net.sourceforge.helpgui.gui.MainFrame;

import org.apache.log4j.Logger;

public class SysNetWindow extends JFrame implements ActionListener,TabbedWindow {

	private JTabbedPane tabPane = new IntroPane( );
	
	// Menu elements
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem newWindowFileMenuItem;
	private JMenuItem openFileMenuItem;
	private JMenuItem saveFileMenuItem;
	private JMenuItem printFileMenuItem;
	private JMenuItem exitFileMenuItem;
	private JMenu helpMenu;
	private JMenuItem contentsHelpMenuItem;
	private JMenuItem aboutHelpMenuItem;

	public SysNetWindow( ) {
		this( "SysNet" );
	}

	public SysNetWindow ( String title ) {

		super( title );
		this.setBackground( Color.WHITE );
		this.setLayout( new BorderLayout( ));
		Settings settings = Settings.getSettings( );
		int width = settings.getInt( "window.main.width" );
		int height = settings.getInt( "window.main.height" );
		int x = Math.max( 0, Math.min( 
		  settings.getInt( "window.main.position.x" ), 
			settings.getInt( "desktop.width" ) - width ));
		int y = Math.max( 0, Math.min( 
		  settings.getInt( "window.main.position.y" ), 
			settings.getInt( "desktop.height" ) - height ));
		
		this.setBounds( x, y, width, height );

		this.setupMenu( );

		this.getContentPane( ).add( tabPane, BorderLayout.CENTER );
		this.tabPane.setVisible( true );

		this.setVisible( true );
		this.repaint( );

		this.addWindowListener( new WindowAdapter( ) {
		  public void windowClosing( WindowEvent e ) {
				JFrame f = (JFrame)e.getSource( );
				Settings settings = Settings.getSettings( );
				settings.setInt( "window.main.position.x", f.getX( ));
				settings.setInt( "window.main.position.y", f.getY( ));
				settings.setInt( "window.main.width", f.getWidth( ));
				settings.setInt( "window.main.height", f.getHeight( ));
				if ( this.getWindowCount( ) == 1 )
			    System.exit( 0 );
			}
			private int getWindowCount( ) {
				int returnValue = 0;
				for( Window w : Window.getWindows( )) {
					if ( w.isShowing( ) && w instanceof SysNetWindow )
						returnValue++;
				}
				return returnValue;
			}
		});

	}

	public void addTab( String title, Component c ) {
		this.tabPane.addTab( title, c );
		this.tabPane.setSelectedComponent( c );
	}
	
	public TabbedWindow newWindow( ) {
		return new SysNetWindow( this.getTitle( ));
	}

	private void setupMenu( ) {
		Language language = Settings.getLanguage( );

		this.menuBar = new JMenuBar( );
		this.fileMenu = new JMenu( language.get( "File" ));
		this.newWindowFileMenuItem = new JMenuItem( 
			language.get( "New Window" ), KeyEvent.VK_N );
		this.openFileMenuItem = new JMenuItem( 
			language.get( "Open" ) + "...", KeyEvent.VK_O );
		this.saveFileMenuItem = new JMenuItem( 
			language.get( "Save" ) + "...", KeyEvent.VK_S );
		this.printFileMenuItem = new JMenuItem( 
			language.get( "Print" ) + "...", KeyEvent.VK_P );
		this.exitFileMenuItem = new JMenuItem( 
			language.get( "Close" ), KeyEvent.VK_C );
		this.helpMenu = new JMenu( language.get( "Help" ));
		this.contentsHelpMenuItem = new JMenuItem( 
			language.get( "Contents" ), KeyEvent.VK_C );
		this.aboutHelpMenuItem = new JMenuItem( 
			language.get( "About" ), KeyEvent.VK_A );

		// FILE MENU
		this.fileMenu.setMnemonic( KeyEvent.VK_F );
		this.fileMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "Perform file operations" ));
		this.fileMenu.add( this.newWindowFileMenuItem );
		this.fileMenu.add( this.openFileMenuItem );
		this.fileMenu.add( this.saveFileMenuItem );
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

		this.saveFileMenuItem.setEnabled( false );

		//HELP MENU
		this.helpMenu.setMnemonic( KeyEvent.VK_H );
		this.helpMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "SysNet Help" ));
		this.helpMenu.add( this.contentsHelpMenuItem );
		this.helpMenu.add( this.aboutHelpMenuItem );

		this.menuBar.add( this.fileMenu );
		this.menuBar.add( this.helpMenu );

		this.setJMenuBar( this.menuBar );

		this.addMenuListeners( );
	}

	private void addMenuListeners( ) {
		this.newWindowFileMenuItem.addActionListener( this );
		this.openFileMenuItem.addActionListener( this );
		this.saveFileMenuItem.addActionListener( this );
		this.printFileMenuItem.addActionListener( this );
		this.exitFileMenuItem.addActionListener( this );
		this.contentsHelpMenuItem.addActionListener( this );
		this.aboutHelpMenuItem.addActionListener( this );
	}

	public DataReader openCSV( ) {
		String lastOpenCSV = Settings.getSettings( ).getProperty( 
			"history.open.last" );
		JFileChooser fc;
		if ( lastOpenCSV != null ) {
			fc = new JFileChooser( 
				new File( lastOpenCSV ).getParentFile( ));
		} else {
			fc = new JFileChooser( );
		}
		fc.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
		fc.addChoosableFileFilter( new CSVFileFilter( ));
		fc.addChoosableFileFilter( new MetsignFileFilter( ));
		fc.setFileView( new MetsignFileView( ));
		int options = fc.showOpenDialog( this );
		if ( options == JFileChooser.APPROVE_OPTION ) {
			DataReader data;
			FileFilter fileFilter = fc.getFileFilter( );
			File selected = fc.getSelectedFile( );
			if ( fileFilter instanceof CSVFileFilter ) {
				if ( !selected.isDirectory( ))
					selected = selected.getParentFile( );
				data = new CSVDataReader( selected.getAbsolutePath( ));
			} else {
				data = new MetsignDataReader( selected.getAbsolutePath( ) );
			}
			Settings.getSettings( ).setProperty( "history.open.last", 
				selected.getAbsolutePath( ));
			data.load( );
			return data;
		}
		return null;
	
	}

	private class IntroPane extends ClosableTabbedPane {
		private BufferedImage logo;

		public IntroPane( ) {
			super( );
			try { 
				this.logo = ImageIO.read( getClass( ).getResourceAsStream( 
					"/resources/images/logo.png" ));
			} catch ( IOException e ) { }
		}

		@Override
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			if ( this.getTabCount( ) < 1 ) {
				String text;
				FontMetrics f = g.getFontMetrics( );
				Language language = Settings.getLanguage( );
				int verticalCenter = this.getHeight( ) / 2;
				int horizontalCenter = this.getWidth( ) / 2;

				g.setFont( new Font( "Arial", Font.BOLD, 14 ));
				text = language.get( "Copyright 2011" ); 
				g.drawString( text, 
					horizontalCenter - (f.stringWidth( text ) / 2), 
					verticalCenter + 10 );

				text = language.get( "SysNet is distributed under the GNU GPL license" );
				g.drawString( text, 
					horizontalCenter - (f.stringWidth( text ) / 2), 
					verticalCenter + 30 );

				text = language.get( "This project is funded by NIH Grant 5R01GM087735" );
				g.drawString( text,
					horizontalCenter - (f.stringWidth( text ) / 2), 
					verticalCenter + 50 );

				g.setFont( new Font( "Arial", Font.BOLD, 18 ));
				g.setColor( Color.RED );

				text = language.get( "Go to File -> Open to open a project" );
				g.drawString( text,
					horizontalCenter - (f.stringWidth( text ) / 2), 
					verticalCenter - 160 );

				g.setFont( new Font( "Arial Black", Font.BOLD, 48 ));
				f = g.getFontMetrics( );
				g.setColor( Color.BLACK );

				text = "SysNet";
				g.drawString( text, 
					horizontalCenter + 70 - (f.stringWidth( text ) / 2), 
					verticalCenter - 40 );

				g.drawImage( logo, 
					horizontalCenter - 25 - (f.stringWidth( text ) / 2), 
					verticalCenter - 90, null );

			}
		}

	}

	/**
	 * A class for filtering out improper file types from the FileChooser.
	 */
	private class CSVFileFilter extends FileFilter {
		private CSVFileView fileView = new CSVFileView( );

		public boolean accept( File f ) {
			return ( 
				f.isDirectory( ) && 
				( fileView.hasSubdirectories( f ) || fileView.isValidProject( f ))
			);
		}

		public String getDescription( ) {
			return "SysNet project folder";
		}

	}

	/**
	 * A class for indicating to the FileChooser if the directory is a project folder or not.
	 */
	private class CSVFileView extends FileView {
		Icon projectIcon;
		
		public CSVFileView( ) {
			super( );
			InputStream logo = 
				getClass( ).getResourceAsStream( "/resources/images/icon.png" );
			if ( logo == null ) {
				projectIcon = new javax.swing.plaf.metal.MetalIconFactory.FileIcon16( ); 
			} else {
				try {
					projectIcon = new ImageIcon( ImageIO.read( logo )
						.getScaledInstance( 16, 16, Image.SCALE_SMOOTH ));
				} catch ( IOException e ) {
					Logger.getLogger( getClass( )).debug(
						"Unable to read SysNet icon file" );
					projectIcon = new javax.swing.plaf.metal.MetalIconFactory.FileIcon16( ); 
				}
			}
		}
		
		public String getDescription( File f ) {
			if ( isValidProject( f ))
				return "SysNet project folder";
			return super.getDescription( f );
		}

		public Icon getIcon( File f ) {
			if ( projectIcon != null && isValidProject( f )) {
					return projectIcon;
			}
			return super.getIcon( f );
		}

		public String getName( File f ) {
			return super.getName( f );
		}

		public String getTypeDescription( File f ) {
			if ( isValidProject( f ))
				return "SysNet project folder";
			return super.getTypeDescription( f );
		}

		public Boolean isTraversable( File f ) {
			return new Boolean( hasSubdirectories( f ));
		}

		public boolean isValidProject( File f ) {
			if ( !f.isDirectory( ))
				return false;
			try {
				List<String> list = Arrays.asList( f.list( ));
				return ( list.contains( "Data.txt" ) &&
				         list.contains( "Experiment.txt" ) &&
						     list.contains( "Sample.txt" ));
			} catch ( NullPointerException e ) {
				return false;
			}
		}

		public boolean hasSubdirectories( File f ) {
			if ( !f.isDirectory( ))
				return false;
			for ( File file : f.listFiles( )) {
				if ( file.isDirectory( ))
					return true;
			}
			return false;
		}
	}

	/**
	 * A class for filtering out improper file types from the FileChooser.
	 */
	private class MetsignFileFilter extends FileFilter {
		private MetsignFileView fileView = new MetsignFileView( );

		public boolean accept( File f ) {
			return ( 
				f.isDirectory( ) && 
				( fileView.hasSubdirectories( f ) || fileView.isValidProject( f ))
			);
		}

		public String getDescription( ) {
			return "Metsign normalization file";
		}

	}

	/**
	 * A class for indicating to the FileChooser if the directory is a project folder or not.
	 */
	private class MetsignFileView extends CSVFileView {
		Icon projectIcon;
		
		public MetsignFileView( ) {
			super( );
		}
		
		public String getDescription( File f ) {
			if ( isValidProject( f ))
				return "Metsign normalization folder";
			return super.getDescription( f );
		}

		public String getTypeDescription( File f ) {
			if ( isValidProject( f ))
				return "Metsign normalization folder";
			return super.getTypeDescription( f );
		}

		public boolean isValidProject( File f ) {
			if ( !f.isDirectory( ))
				return false;
			try {
				List<String> list = Arrays.asList( f.list( ));
				return ( list.contains( "Normalization.csv" ));
			} catch ( NullPointerException e ) {
				return false;
			}
		}
	}

	public void actionPerformed( ActionEvent e ) {
		Logger logger = Logger.getLogger( getClass( ));
		logger.debug( String.format( "ActionEvent fired:" ));
		logger.debug( "\tactionCommand: "+e.getActionCommand( ));
		logger.debug( "\t  paramString: "+e.paramString( ));

		Object item = e.getSource( );
		if ( item == this.newWindowFileMenuItem ) {
			this.newWindow( );
		} else if ( item == this.openFileMenuItem ) {
			DataReader data = this.openCSV( );
			if ( data == null ) {
				return;
			}
			Map.Entry<Integer,List> choice = experimentSelection( data.getExperiments( ));
			if ( choice == null )
				return;

			if ( choice.getKey( ).intValue( ) == ExperimentSelectionDialog.CORRELATION_VIEW ) {
				CorrelationDisplayPanel cdp = new CorrelationDisplayPanel( );
				if( cdp.createView( choice.getValue( ))) {
					this.tabPane.addTab( cdp.getTitle( ), cdp );
					this.tabPane.setSelectedComponent( cdp );
				}
			}
			else if ( choice.getKey( ).intValue( ) == 
				ExperimentSelectionDialog.COMPARATIVE_ANALYSIS_VIEW ) {
				ComparativeAnalysisDisplayPanel cadp = new ComparativeAnalysisDisplayPanel( );
				if ( cadp.createView( choice.getValue( ))) {
					this.tabPane.addTab( cadp.getTitle( ), cadp );
					this.tabPane.setSelectedComponent( cadp );
				}
			}
			else if ( choice.getKey( ).intValue( ) == 
				ExperimentSelectionDialog.TIME_COURSE_STUDY_VIEW ) {
				TimeCourseStudyDisplayPanel tcdp = new TimeCourseStudyDisplayPanel( );
				if ( tcdp.createView( choice.getValue( ))) {
					this.tabPane.addTab( tcdp.getTitle( ), tcdp );
					this.tabPane.setSelectedComponent( tcdp );
				}
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
	 * Brings up a dialog to allow you to select the appropriate experiment. 
	 * 
	 * @param experiments An ArrayList containing the possible Experiments
	 * @return The experiment you selected, or null if you pressed cancel, or
	 *	if no experiments are available
	 */
	public Map.Entry<Integer,List> experimentSelection( Collection <Experiment> experiments ) {
		if ( experiments.size( ) < 1 ) {
			Logger.getLogger( getClass( )).error( 
				Settings.getLanguage( ).get( "These files do not appear to contain any data!" ));
			return null;
		}
		return ExperimentSelectionDialog.showInputDialog( 
			this, Settings.getLanguage( ).get( "Experiment Selection" ), experiments );
	}
	
}

