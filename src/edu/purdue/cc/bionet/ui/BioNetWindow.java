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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.SwingConstants;

import edu.purdue.bbc.io.CSVTableReader;
import edu.purdue.bbc.io.CSVTableWriter;
import edu.purdue.bbc.io.FileUtils;
import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;
import edu.purdue.bbc.util.StringUtils;
import edu.purdue.cc.bionet.io.DataReader;
import edu.purdue.cc.bionet.io.MetsignDataReader;
import edu.purdue.cc.bionet.io.ProjectInfoWriter;
import edu.purdue.cc.bionet.util.ExperimentSet;
import edu.purdue.cc.bionet.util.Project;
import edu.purdue.cc.bionet.util.Sample;

import net.sourceforge.helpgui.gui.MainFrame;

import org.apache.log4j.Logger;

public class BioNetWindow extends JFrame implements ActionListener,TabbedWindow {

	private ClosableTabbedPane tabPane = new IntroPane( );
	
	// Menu elements
	private JMenuBar menuBar;
	private JMenu projectMenu;
	private JMenuItem newProjectMenuItem;
	private JMenuItem openProjectMenuItem;
	private JMenuItem saveProjectMenuItem;
	private JMenuItem importProjectMenuItem;
	private JMenu openExperimentProjectMenu;
	private JMenuItem printProjectMenuItem;
	private JMenuItem closeProjectMenuItem;
	private JMenuItem exitProjectMenuItem;
	private JMenu helpMenu;
	private JMenuItem contentsHelpMenuItem;
	private JMenuItem aboutHelpMenuItem;

	// one project per window
	private Project project;

	public BioNetWindow( ) {
		this( "BioNet" );
	}

	public BioNetWindow ( String title ) {

		super( title );
		this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
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
		this.setExtendedState( 
			settings.getInt( "window.main.frameState", Frame.NORMAL ));
		this.repaint( );
		this.addWindowListener( new WindowAdapter( ) {
		  public void windowClosing( WindowEvent e ) {
				BioNetWindow w = (BioNetWindow)e.getSource( );
				if ( checkModifications( )) {
					Settings settings = Settings.getSettings( );
					// make sure the window is not maximized before saving the
					// size and position.
					int state = w.getExtendedState( );
					if ( state == Frame.NORMAL ) {
						settings.setInt( "window.main.position.x", w.getX( ));
						settings.setInt( "window.main.position.y", w.getY( ));
						settings.setInt( "window.main.width", w.getWidth( ));
						settings.setInt( "window.main.height", w.getHeight( ));
					}
					// discard the 'iconified' state
					state &= ~Frame.ICONIFIED;
					settings.setInt( "window.main.frameState", state ); 
					w.dispose( );
					BioNetWindow.checkWindowCount( );
				}
			}
		});
	}

	private static int getWindowCount( ) {
		int returnValue = 0;
		for( Window w : Window.getWindows( )) {
			if ( w.isShowing( ) && w instanceof BioNetWindow )
				returnValue++;
		}
		return returnValue;
	}

	private static void checkWindowCount( ) {
		int count = BioNetWindow.getWindowCount( );
		Logger logger = Logger.getLogger( BioNetWindow.class );
		logger.debug( "Detected " + count + " open windows" );
		if ( count < 1 ) {
			logger.debug( "Shutting Down" );
			System.exit( 0 );
		}
	}

	public void addTab( String title, Component c ) {
		this.tabPane.addTab( title, c );
	}

	public void addTab( String title, Component c, boolean closable ) {
		this.tabPane.addTab( title, c, closable );
	}
	
	public TabbedWindow newWindow( ) {
		return new BioNetWindow( this.getTitle( ));
	}

	public Project getProject( ) {
		return this.project;
	}

	public void setProject( Project project ) {
		boolean cancel = false;
		if ( this.project != project ) {
			cancel = !this.checkModifications( );
		}
		if ( !cancel ) {
			this.tabPane.removeAll( );
			this.openExperimentProjectMenu.removeAll( );
			if ( project != null ) {
				for ( ExperimentSet experimentSet : project ) {
					this.openExperimentProjectMenu.setEnabled( true );
					this.openExperimentProjectMenu.add( new JMenuItem( 
							new ExperimentSetAction( experimentSet )));
				}
				this.saveProjectMenuItem.setEnabled( true );
				this.importProjectMenuItem.setEnabled( true );
				this.setTitle( project.getAttribute( "Project Name" ) + " - BioNet" );
			} else {
				this.openExperimentProjectMenu.setEnabled( false );
				this.saveProjectMenuItem.setEnabled( false );
				this.importProjectMenuItem.setEnabled( false );
				this.setTitle( "BioNet" );
			}
			this.project = project;
		}
	}

	private ProjectDisplayPanel getProjectDisplayPanel( ) {
		Component pdp = null;
		for ( int i=0; i < this.tabPane.getTabCount( ) && 
			             !( pdp instanceof ProjectDisplayPanel ); i++ ) {
			pdp = this.tabPane.getComponentAt( i );
		}
		if ( pdp instanceof ProjectDisplayPanel )
			return (ProjectDisplayPanel)pdp;
		else
			return null;
	}

	private void setupMenu( ) {
		Language language = Settings.getLanguage( );

		this.menuBar = new JMenuBar( );
		this.projectMenu = new JMenu( language.get( "Project" ));
		this.newProjectMenuItem = new JMenuItem( 
			language.get( "New Project" )+"...", KeyEvent.VK_N );
		this.openProjectMenuItem = new JMenuItem( 
			language.get( "Open Project" ) + "...", KeyEvent.VK_O );
		this.saveProjectMenuItem = new JMenuItem( 
			language.get( "Save" ), KeyEvent.VK_S );
		this.importProjectMenuItem = new JMenuItem( 
			language.get( "Import Data" ) + "...", KeyEvent.VK_I );
		this.openExperimentProjectMenu = new JMenu( 
			language.get( "Experiment" ));
		this.openExperimentProjectMenu.setMnemonic( KeyEvent.VK_X );
		this.printProjectMenuItem = new JMenuItem( 
			language.get( "Print" ) + "...", KeyEvent.VK_P );
		this.closeProjectMenuItem = new JMenuItem( 
			language.get( "Close" ), KeyEvent.VK_C );
		this.exitProjectMenuItem = new JMenuItem( 
			language.get( "Exit" ), KeyEvent.VK_X );
		this.helpMenu = new JMenu( language.get( "Help" ));
		this.contentsHelpMenuItem = new JMenuItem( 
			language.get( "Contents" ), KeyEvent.VK_C );
		this.aboutHelpMenuItem = new JMenuItem( 
			language.get( "About" ), KeyEvent.VK_A );

		// FILE MENU
		this.projectMenu.setMnemonic( KeyEvent.VK_P );
		this.projectMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "Perform file operations" ));
		this.projectMenu.add( this.newProjectMenuItem );
		this.projectMenu.add( this.openProjectMenuItem );
		this.projectMenu.add( this.saveProjectMenuItem );
		this.projectMenu.add( this.importProjectMenuItem );
		this.projectMenu.add( this.closeProjectMenuItem );
		this.projectMenu.add( this.exitProjectMenuItem );
		this.newProjectMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK ));
		this.openProjectMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK ));
		this.saveProjectMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK ));
		this.printProjectMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK ));
		this.closeProjectMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK ));
		this.exitProjectMenuItem.setAccelerator( 
			KeyStroke.getKeyStroke( KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK ));

		this.openExperimentProjectMenu.setEnabled( false );
		this.saveProjectMenuItem.setEnabled( false );
		this.importProjectMenuItem.setEnabled( false );

		//HELP MENU
		this.helpMenu.setMnemonic( KeyEvent.VK_H );
		this.helpMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "BioNet Help" ));
//		this.helpMenu.add( this.contentsHelpMenuItem );
		this.helpMenu.add( this.aboutHelpMenuItem );

		this.menuBar.add( this.projectMenu );
		this.menuBar.add( this.openExperimentProjectMenu );
		this.menuBar.add( this.helpMenu );

		this.setJMenuBar( this.menuBar );

		this.addMenuListeners( );
	}

	private void addMenuListeners( ) {
		this.newProjectMenuItem.addActionListener( this );
		this.openProjectMenuItem.addActionListener( this );
		this.saveProjectMenuItem.addActionListener( this );
		this.importProjectMenuItem.addActionListener( this );
		this.printProjectMenuItem.addActionListener( this );
		this.closeProjectMenuItem.addActionListener( this );
		this.exitProjectMenuItem.addActionListener( this );
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
		fc.addChoosableFileFilter( new MetsignFileFilter( ));
		fc.setFileView( new MetsignFileView( ));
		int options = fc.showOpenDialog( this );
		if ( options == JFileChooser.APPROVE_OPTION ) {
			DataReader data = null;
			FileFilter fileFilter = fc.getFileFilter( );
			File selected = fc.getSelectedFile( );
//			if ( fileFilter instanceof MetsignFileFilter ) {
				data = new MetsignDataReader( selected.getAbsolutePath( ) );
//			}
			Settings.getSettings( ).setProperty( "history.open.last", 
				selected.getAbsolutePath( ));
			data.load( );
			return data;
		}
		return null;
	
	}

	
	public boolean isProjectModified( ) {
		ProjectDisplayPanel p = this.getProjectDisplayPanel( );
		if ( p != null )
			return p.isProjectModified( );
		else
			return false;
	}

	/**
	 * Displays a dialog asking the user to save the project if modifications
	 * have been made. 
	 * 
	 * @return false if the user clicks 'Cancel' in the dialog. true otherwise.
	 */
	private boolean checkModifications( ) {
		ProjectDisplayPanel p = this.getProjectDisplayPanel( );
		if ( p != null ) {
			p.updateProject( this.project );
			if ( this.isProjectModified( )) {
				Language language = Settings.getLanguage( );
				int option = JOptionPane.showConfirmDialog( this,
					language.get( "You have made changes to your project." ) + "\n" + 
					language.get( "Would you like to save the changes before closing?" ),
					language.get( "Save Changes" ),
					JOptionPane.YES_NO_CANCEL_OPTION, 
					JOptionPane.WARNING_MESSAGE ); 
				if ( option == JOptionPane.YES_OPTION ) {
					p.saveProject( this.project );
				}
				if ( option == JOptionPane.CANCEL_OPTION ) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean createProject( ) {
		Logger logger = Logger.getLogger( getClass( ));
		JFileChooser fc = new JFileChooser( );
		int options = fc.showSaveDialog( this );
		File file = fc.getSelectedFile( );
		if ( options == JFileChooser.APPROVE_OPTION ) {
			if ( file.exists( )) {
				logger.error( "The directory you selected already exists. " +
					"Please choose a new directory name." );
				return false;
			}
			if ( !file.getParentFile( ).canWrite( )) {
				logger.error( "You do not have permission to write to this directory. " +
					"Please choose a different path or contact your system administrator." );
				return false;
			}
			file.mkdirs( );
			// create the 'Normalization' directory.
			new File( file.getAbsolutePath( ) + File.separator + 
			          "Normalization" ).mkdir( );
			Project newProject = new Project( file );
			newProject.setAttribute( "Project Name", file.getName( ));
			try { 
				new ProjectInfoWriter( newProject ).write( );
			} catch ( IOException e ) {
				logger.error( "An error occurred while attempting to save your project" );
				logger.debug( e, e );
				return false;
			}
			ProjectDisplayPanel pdp = new ProjectDisplayPanel( );
			if ( pdp.createView( newProject )) {
				if ( this.tabPane.getTabCount( ) >= 1 ) {
					BioNetWindow window = (BioNetWindow)this.newWindow( );
					window.setProject( project );
					window.addTab( pdp.getTitle( ), pdp, false );
				} else {
					this.setProject( newProject );
					this.addTab( pdp.getTitle( ), pdp, false );
				}
			}
		}
		return true;
	}

	// ============================= PRIVATE CLASSES =============================
	private class IntroPane extends ClosableTabbedPane {
		private BufferedImage logo;
		private final String license = Settings.getLanguage( ).get( 
			"BioNet is distributed under the GNU GPL license" );
		private final String funding = Settings.getLanguage( ).get( 
			"This project is funded by NIH Grant 5R01GM087735" );
		private final String helpText = Settings.getLanguage( ).get( 
			"Go to Project -> Open Project to open a project" );
		private final String copyright = Settings.getLanguage( ).get( 
			"Copyright 2012" );

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
				int verticalCenter = this.getHeight( ) / 2;
				int horizontalCenter = this.getWidth( ) / 2;

				g.setFont( new Font( "Arial", Font.BOLD, 14 ));
				text = copyright;
				g.drawString( text, 
					horizontalCenter - (f.stringWidth( text ) / 2), 
					verticalCenter + 10 );

				text = license;
				g.drawString( text, 
					horizontalCenter - (f.stringWidth( text ) / 2), 
					verticalCenter + 30 );

				text = funding;
				g.drawString( text,
					horizontalCenter - (f.stringWidth( text ) / 2), 
					verticalCenter + 50 );

				g.setFont( new Font( "Arial", Font.BOLD, 18 ));
				g.setColor( Color.RED );

				text = helpText; 
				g.drawString( text,
					horizontalCenter - (f.stringWidth( text ) / 2), 
					verticalCenter - 160 );

				g.setFont( new Font( "Arial Black", Font.BOLD, 48 ));
				f = g.getFontMetrics( );
				g.setColor( Color.BLACK );

				text = "BioNet";
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

		public boolean accept( File f ) {
			return ( f.isDirectory( ) || f.getName( ).endsWith( ".csv" )) && f.canRead( );
		}

		public String getDescription( ) {
			return "CSV Data file";
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
			return "Metsign project";
		}

	}

	/**
	 * A class for indicating to the FileChooser if the directory is a project 
	 * folder or not.
	 */
	private class MetsignFileView extends FileView {
		Icon projectIcon;
		
		public MetsignFileView( ) {
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
						"Unable to read BioNet icon file" );
					projectIcon = new javax.swing.plaf.metal.MetalIconFactory.FileIcon16( ); 
				}
			}
		}
		
		public String getDescription( File f ) {
			if ( isValidProject( f ))
				return "Metsign project";
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
				return "Metsign project";
			return super.getTypeDescription( f );
		}

		public boolean isValidProject( File f ) {
			if ( !f.isDirectory( ))
				return false;
			try {
				return Arrays.asList( f.list( )).contains( "project_info.csv" );
			} catch ( NullPointerException e ) {
				return false;
			}
		}

		public Boolean isTraversable( File f ) {
			return new Boolean( hasSubdirectories( f ));
		}

		public boolean hasSubdirectories( File f ) {
			if ( isValidProject( f ))
				return false;
			if ( !f.isDirectory( ) || !f.canRead( ))
				return false;
			try {
				for ( File file : f.listFiles( )) {
					if ( file.isDirectory( ))
						return true;
				}
			} catch ( NullPointerException e ) {
				Logger.getLogger( getClass( )).debug( 
					"An error occurred reading the directory " + f.getAbsolutePath( ) +
					". You may not have the proper permissions" );
				return false;
			}
			return false;
		}
	}

	public void actionPerformed( ActionEvent e ) {
		Logger logger = Logger.getLogger( getClass( ));
		Object item = e.getSource( );
		if ( item == this.newProjectMenuItem ) {
			this.createProject( );
		} else if ( item == this.openProjectMenuItem ) {
			DataReader data = this.openCSV( );
			if ( data == null ) {
				return;
			}
			this.project = data.getProject( );
			if ( project == null )
				return;
			ProjectDisplayPanel pdp = new ProjectDisplayPanel( );
			if ( pdp.createView( project )) {
				if ( this.tabPane.getTabCount( ) >= 1 ) {
					BioNetWindow window = (BioNetWindow)this.newWindow( );
					window.setProject( project );
					window.addTab( pdp.getTitle( ), pdp, false );
				} else {
					this.setProject( project );
					this.addTab( pdp.getTitle( ), pdp, false );
				}
			}

		} else if ( item == this.saveProjectMenuItem ) {
			this.getProjectDisplayPanel( ).saveProject( this.project );

		} else if ( item == this.importProjectMenuItem ) {
			Settings settings = Settings.getSettings( );
			String lastImport = settings.get( "history.import.lastDirectory" );
			JFileChooser fc;
			if ( lastImport != null ) {
				fc = new JFileChooser( new File( lastImport ));
			} else {
				fc = new JFileChooser( );
			}
			fc.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
			fc.addChoosableFileFilter( new CSVFileFilter( ));
			int options = fc.showOpenDialog( this );
			CSVTableReader reader = null;
			if ( options == JFileChooser.APPROVE_OPTION ) {
				try {
					reader = new CSVTableReader( fc.getSelectedFile( ), ",\t" );
				} catch( IOException exc ) {
					logger.error( "Unable to find the specified file" );
					logger.debug( exc, exc );
					return;
				}
				settings.set( "history.import.lastDirectory", 
					            fc.getSelectedFile( ).getParentFile( ).getAbsolutePath( ));
				if ( reader == null )
					return;
				List<String> keys = new ArrayList( Arrays.asList( reader.getKeys( )));
				keys.remove( "id" );
				while ( reader.hasNext( )) {
					Map<String,String> values = reader.next( );
					for ( int i=0; i < keys.size( ); i++ ) {
						String value = values.get( keys.get( i ));
						if ( !"NA".equals( value ) && !StringUtils.isNumeric( value )) {
							logger.debug( keys.get( i ) + 
							  " does not appear to be completely numeric, dropping" );
							keys.remove( i-- );
						}
					}
				}
				reader.close( );
				ImportDialog dialog = new ImportDialog( 
					this, "Select Sample Value Columns", keys );
				if ( dialog.getOption( ) == JOptionPane.OK_OPTION ) {
					Collection<String> sampleNames = dialog.getSampleNames( );
					String experimentName = dialog.getExperimentName( );

					// copy the imported file into the project.
					File outputFile = 
						new File( this.project.getResource( ).getAbsolutePath( ) + 
						File.separator + "Normalization" + File.separator + 
						experimentName + File.separator + "Normalization.csv" );
					try { 
						reader = new CSVTableReader( fc.getSelectedFile( ), ",\t" );
						keys = new ArrayList( Arrays.asList( reader.getKeys( )));
						// make sure there is an 'id' field.
						if ( !keys.contains( "id" )) {
							keys.add( 0, "id" );
						}
						outputFile.getParentFile( ).mkdirs( );
						outputFile.createNewFile( );
						CSVTableWriter writer = 
							new CSVTableWriter( outputFile, keys );
						int id = 1;
						while( reader.hasNext( )) {
							Map<String,String> row = reader.next( );
							// make sure there is an 'id' field.
							if ( row.get( "id" ) == null ) {
								row.put( "id", Integer.toString( id++ ));
							}
							writer.write( row );
						}
						reader.close( );
						writer.close( );
					} catch ( IOException exc ) {
						logger.debug( exc, exc );
						logger.error( "Unable to copy the file to the project folder\n" +
							"Please ensure that the project directory is writable and has not " +
							"been removed" );
						return;
					}
					ExperimentSet experimentSet = 
						new ExperimentSet( experimentName, this.project.getResource( ));
					for ( String name : sampleNames ) {
						experimentSet.addSample( 
							this.getProjectDisplayPanel( ).addSample( name ));
					}
					ProjectDisplayPanel p = this.getProjectDisplayPanel( );
					p.addExperiment( experimentSet );
					p.saveProject( );
					this.openExperimentProjectMenu.setEnabled( true );
					this.openExperimentProjectMenu.add( new JMenuItem( 
							new ExperimentSetAction( experimentSet )));
				}
			}

		} else if ( item == this.closeProjectMenuItem ) {
			this.setProject( null );

		} else if ( item == this.exitProjectMenuItem ) {
			this.setProject( null );
			this.dispose( );
			BioNetWindow.checkWindowCount( );

		} else if ( item == this.contentsHelpMenuItem ) {
			new MainFrame( "/docs/help/", "plastic" ).setVisible( true );

		} else if ( item == this.aboutHelpMenuItem ) {
			new About( ).setVisible( true );
		}
	}

	private class ExperimentSetAction extends AbstractAction {
		private String name;
		private ExperimentSet experiments;

		public ExperimentSetAction( ExperimentSet experiments ) {
			super( experiments.getName( ));
			this.experiments = experiments;
		}

		public void actionPerformed( ActionEvent e ) {
			getProjectDisplayPanel( ).updateProject( project );
			if ( !this.experiments.isLoaded( ))
				this.experiments.load( );
			Component component = getProjectDisplayPanel( );
			while (!( component instanceof TabbedWindow )) {
				component = component.getParent( );	
			}
			TabbedWindow tabPane = (TabbedWindow)component;
			while (!( component instanceof Frame )) {
				component = component.getParent( );	
			}
			Map.Entry<Integer,ExperimentSet> choice = 
				ExperimentSelectionDialog.showInputDialog( 
					(Frame)component, "Experiment Selection", this.experiments );
			if ( choice == null )
				return;

			if ( choice.getKey( ).intValue( ) == 
				   ExperimentSelectionDialog.CORRELATION_VIEW ) {
				if ( this.experiments.getSamples( ).size( ) <= 3 ) {
					Logger.getLogger( getClass( )).error(
						"This experiment appears to contain less than 3 valid samples.\n" +
						"Correlation calculations require at least 3 samples." );
					return;
				}
				CorrelationDisplayPanel cdp = new CorrelationDisplayPanel( );
				if( cdp.createView( choice.getValue( ))) {
					tabPane.addTab( cdp.getTitle( ), cdp );
				}
			}
			else if ( choice.getKey( ).intValue( ) == 
				        ExperimentSelectionDialog.COMPARATIVE_ANALYSIS_VIEW ) {
				DistributionAnalysisDisplayPanel cadp = new DistributionAnalysisDisplayPanel( );
				if ( cadp.createView( choice.getValue( ))) {
					tabPane.addTab( cadp.getTitle( ), cadp );
				}
			}
			else if ( choice.getKey( ).intValue( ) == 
				        ExperimentSelectionDialog.TIME_COURSE_STUDY_VIEW ) {
				ClusteringDisplayPanel tcdp = new ClusteringDisplayPanel( );
				if ( tcdp.createView( choice.getValue( ))) {
					tabPane.addTab( tcdp.getTitle( ), tcdp );
				}
			}
		}
	}

	private class ImportDialog extends JDialog implements ActionListener {
		private Collection<String> sampleNames;
		private String experimentName;
		private JTextField experimentNameField;
		private JButton okButton;
		private JButton cancelButton;
		private Collection <JCheckBox> checkboxes;
		private int option = JOptionPane.CANCEL_OPTION;
		
		public ImportDialog ( Frame owner, String title, 
		                     Collection<String> headings ) {
			super( owner, title );
			int layoutWidth = (int)( Math.ceil( Math.sqrt( headings.size( ))) / 2 );
			JPanel namePanel = new JPanel( new BorderLayout( ));
			JPanel checkboxPanel = new JPanel( 
				new GridLayout( headings.size( ) / layoutWidth, layoutWidth ));
			JPanel buttonPanel = new JPanel( new GridLayout( 1, 2 ));
			JPanel contentPane = (JPanel)this.getContentPane( );
			contentPane.setLayout( new BorderLayout( ));
			contentPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 0, 5 ));
			Language language = Settings.getLanguage( );
			JLabel nameLabel = 
				new JLabel( language.get( "Experiment Name" ));
			nameLabel.setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 5 ));
			namePanel.add( nameLabel, BorderLayout.WEST );
			this.experimentNameField = new JTextField( );
			namePanel.add( this.experimentNameField );
			this.checkboxes = new ArrayList<JCheckBox>( );
			for ( String heading : headings ) {
				JCheckBox headingBox = new JCheckBox( heading, true );
				checkboxes.add( headingBox );
				checkboxPanel.add( headingBox );
			}
			this.okButton = new JButton( language.get( "OK" ));
			this.cancelButton = new JButton( language.get( "Cancel" ));
			buttonPanel.add( this.okButton );
			buttonPanel.add( this.cancelButton );
			this.okButton.addActionListener( this );
			this.cancelButton.addActionListener( this );
			this.add( namePanel, BorderLayout.NORTH );
			this.add( checkboxPanel, BorderLayout.CENTER );
			this.add( buttonPanel, BorderLayout.SOUTH );
			this.setVisible( false );
			this.setModalityType( Dialog.ModalityType.APPLICATION_MODAL );
			this.setResizable( false );
			this.pack( );
			this.setVisible( true );
		}

		public Collection<String> getSampleNames( ) {
			return this.sampleNames;
		}

		public String getExperimentName( ) {
			return this.experimentName;
		}

		public int getOption( ) {
			return this.option;
		}

		public void actionPerformed( ActionEvent e ) {
			Object source = e.getSource( );
			if ( source == this.okButton ) {
				this.experimentName = 
					this.experimentNameField.getText( ).replaceAll( 
						"[^a-zA-Z0-9.]", "_" );
				if ( "".equals( this.experimentName )) {
					JOptionPane.showMessageDialog( this, Settings.getLanguage( ).get(
						"Please enter a name for the new experiment" ));
				} else { 
					this.sampleNames = new ArrayList( );
					for ( JCheckBox box : this.checkboxes ) {
						if ( box.isSelected( )) {
							sampleNames.add( box.getText( ));
						}
					}
					this.option = JOptionPane.OK_OPTION;
					this.setVisible( false );
				}
			} else {
				this.setVisible( false );
			}
		}
	}
}

