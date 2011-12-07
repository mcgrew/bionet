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
import java.awt.Frame;
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
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;
//import edu.purdue.cc.sysnet.io.CSVDataReader;
import edu.purdue.cc.sysnet.io.DataReader;
import edu.purdue.cc.sysnet.io.MetsignDataReader;
import edu.purdue.cc.sysnet.io.ProjectInfoWriter;
import edu.purdue.cc.sysnet.util.Experiment;
import edu.purdue.cc.sysnet.util.ExperimentSet;
import edu.purdue.cc.sysnet.util.Project;

import net.sourceforge.helpgui.gui.MainFrame;

import org.apache.log4j.Logger;

public class SysNetWindow extends JFrame implements ActionListener,TabbedWindow {

	private ClosableTabbedPane tabPane = new IntroPane( );
	
	// Menu elements
	private JMenuBar menuBar;
	private JMenu projectMenu;
	private JMenuItem newWindowProjectMenuItem;
	private JMenuItem openProjectMenuItem;
	private JMenuItem saveProjectMenuItem;
	private JMenu openExperimentProjectMenu;
	private JMenuItem printProjectMenuItem;
	private JMenuItem closeProjectMenuItem;
	private JMenuItem exitProjectMenuItem;
	private JMenu helpMenu;
	private JMenuItem contentsHelpMenuItem;
	private JMenuItem aboutHelpMenuItem;

	// one project per window
	private Project project;

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
				checkModifications( );
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
	}

	public void addTab( String title, Component c, boolean closable ) {
		this.tabPane.addTab( title, c, closable );
	}
	
	public TabbedWindow newWindow( ) {
		return new SysNetWindow( this.getTitle( ));
	}

	public Project getProject( ) {
		return this.project;
	}

	public void setProject( Project project ) {
		if ( this.project != project ) {
			this.checkModifications( );
		}
		this.tabPane.removeAll( );
		this.openExperimentProjectMenu.removeAll( );
		if ( project != null ) {
			for ( ExperimentSet experimentSet : project ) {
				this.openExperimentProjectMenu.add( new JMenuItem( 
						new ExperimentSetAction( experimentSet )));
			}
			this.openExperimentProjectMenu.setEnabled( true );
			this.saveProjectMenuItem.setEnabled( true );
			this.setTitle( project.getAttribute( "Project Name" ) + " - SysNet" );
		} else {
			this.openExperimentProjectMenu.setEnabled( false );
			this.saveProjectMenuItem.setEnabled( false );
			this.setTitle( "SysNet" );
		}
		this.project = project;
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
		this.newWindowProjectMenuItem = new JMenuItem( 
			language.get( "New Window" ), KeyEvent.VK_N );
		this.openProjectMenuItem = new JMenuItem( 
			language.get( "Open Project" ) + "...", KeyEvent.VK_O );
		this.saveProjectMenuItem = new JMenuItem( 
			language.get( "Save" ) + "...", KeyEvent.VK_S );
		this.openExperimentProjectMenu = new JMenu( 
			language.get( "Open Experiment" ));
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
		this.projectMenu.setMnemonic( KeyEvent.VK_F );
		this.projectMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "Perform file operations" ));
		this.projectMenu.add( this.newWindowProjectMenuItem );
		this.projectMenu.add( this.openProjectMenuItem );
		this.projectMenu.add( this.saveProjectMenuItem );
		this.projectMenu.add( this.openExperimentProjectMenu );
		this.projectMenu.add( this.closeProjectMenuItem );
		this.projectMenu.add( this.exitProjectMenuItem );
		this.newWindowProjectMenuItem.setAccelerator( 
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

		//HELP MENU
		this.helpMenu.setMnemonic( KeyEvent.VK_H );
		this.helpMenu.getAccessibleContext( ).setAccessibleDescription(
			language.get( "SysNet Help" ));
		this.helpMenu.add( this.contentsHelpMenuItem );
		this.helpMenu.add( this.aboutHelpMenuItem );

		this.menuBar.add( this.projectMenu );
		this.menuBar.add( this.helpMenu );

		this.setJMenuBar( this.menuBar );

		this.addMenuListeners( );
	}

	private void addMenuListeners( ) {
		this.newWindowProjectMenuItem.addActionListener( this );
		this.openProjectMenuItem.addActionListener( this );
		this.saveProjectMenuItem.addActionListener( this );
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
//		fc.addChoosableFileFilter( new CSVFileFilter( ));
		fc.addChoosableFileFilter( new MetsignFileFilter( ));
		fc.setFileView( new MetsignFileView( ));
		int options = fc.showOpenDialog( this );
		if ( options == JFileChooser.APPROVE_OPTION ) {
			DataReader data = null;
			FileFilter fileFilter = fc.getFileFilter( );
			File selected = fc.getSelectedFile( );
//			if ( fileFilter instanceof CSVFileFilter ) {
//				if ( !selected.isDirectory( ))
//					selected = selected.getParentFile( );
//				data = new CSVDataReader( selected.getAbsolutePath( ));
//			} else {
			if ( fileFilter instanceof MetsignFileFilter ) {
				data = new MetsignDataReader( selected.getAbsolutePath( ) );
			}
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
					this.saveProject( this.project );
				}
				if ( option == JOptionPane.CANCEL_OPTION ) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean saveProject( Project project ) {
		Logger logger = Logger.getLogger( getClass( ));
		try {
			new ProjectInfoWriter( project )
				.write( );
			this.getProjectDisplayPanel( ).setProjectModified( false );
		} catch ( java.io.IOException exception ) {
			logger.debug( exception, exception );
			logger.error( 
				"There was an error when trying to save your project file.\n" +
				"Please check to make sure the path still exists." );
			return false;
		}
		return true;
	}


	// ============================= PRIVATE CLASSES =============================
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
			return "Metsign project";
		}

	}

	/**
	 * A class for indicating to the FileChooser if the directory is a project 
	 * folder or not.
	 */
	private class MetsignFileView extends CSVFileView {
		Icon projectIcon;
		
		public MetsignFileView( ) {
			super( );
		}
		
		public String getDescription( File f ) {
			if ( isValidProject( f ))
				return "Metsign project";
			return super.getDescription( f );
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
			if ( isValidProject( f ))
				return Boolean.FALSE;
			return super.isTraversable( f );
		}
	}

	public void actionPerformed( ActionEvent e ) {
		Logger logger = Logger.getLogger( getClass( ));
		logger.debug( String.format( "ActionEvent fired:" ));
		logger.debug( "\tactionCommand: "+e.getActionCommand( ));
		logger.debug( "\t  paramString: "+e.paramString( ));

		Object item = e.getSource( );
		if ( item == this.newWindowProjectMenuItem ) {
			this.newWindow( );
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
					SysNetWindow window = (SysNetWindow)this.newWindow( );
					window.setProject( project );
					window.addTab( pdp.getTitle( ), pdp, false );
				} else {
					this.setProject( project );
					this.addTab( pdp.getTitle( ), pdp, false );
				}
			}

		} else if ( item == this.saveProjectMenuItem ) {
			this.getProjectDisplayPanel( ).updateProject( this.project );
			this.saveProject( this.project );

		} else if ( item == this.closeProjectMenuItem ) {
			this.setProject( null );

		} else if ( item == this.exitProjectMenuItem ) {
			this.dispose( );

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
				experiments.load( );
			Component component = getProjectDisplayPanel( );
			while (!( component instanceof TabbedWindow )) {
				component = component.getParent( );	
			}
			TabbedWindow tabPane = (TabbedWindow)component;
			while (!( component instanceof Frame )) {
				component = component.getParent( );	
			}
			Map.Entry<Integer,List> choice = ExperimentSelectionDialog.showInputDialog( 
				(Frame)component, "Experiment Selection", experiments );
			if ( choice == null )
				return;

			if ( choice.getKey( ).intValue( ) == ExperimentSelectionDialog.CORRELATION_VIEW ) {
				CorrelationDisplayPanel cdp = new CorrelationDisplayPanel( );
				if( cdp.createView( choice.getValue( ))) {
					tabPane.addTab( cdp.getTitle( ), cdp );
//					tabPane.setSelectedComponent( cdp );
				}
			}
			else if ( choice.getKey( ).intValue( ) == 
				ExperimentSelectionDialog.COMPARATIVE_ANALYSIS_VIEW ) {
				DistributionAnalysisDisplayPanel cadp = new DistributionAnalysisDisplayPanel( );
				if ( cadp.createView( choice.getValue( ))) {
					tabPane.addTab( cadp.getTitle( ), cadp );
//					tabPane.setSelectedComponent( cadp );
				}
			}
			else if ( choice.getKey( ).intValue( ) == 
				ExperimentSelectionDialog.TIME_COURSE_STUDY_VIEW ) {
				ClusteringDisplayPanel tcdp = new ClusteringDisplayPanel( );
				if ( tcdp.createView( choice.getValue( ))) {
					tabPane.addTab( tcdp.getTitle( ), tcdp );
//					tabPane.setSelectedComponent( tcdp );
				}
			}
		}
	}
}

