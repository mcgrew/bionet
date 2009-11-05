import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JSysNetWindow extends JFrame implements ActionListener {

	private JTabbedPane tabPane = new JTabbedPane( );
	
	// Menu elements
	private JMenuBar menuBar = new JMenuBar( );
	private JMenu fileMenu = new JMenu( "File" );
	private JMenuItem openFileMenuItem = new JMenuItem( "Open...", KeyEvent.VK_O );
	private JMenuItem saveFileMenuItem = new JMenuItem( "Save...", KeyEvent.VK_S );
	private JMenuItem printFileMenuItem = new JMenuItem( "Print...", KeyEvent.VK_P );
	private JMenuItem exitFileMenuItem = new JMenuItem( "Exit", KeyEvent.VK_X );
	private JMenu databaseMenu = new JMenu( "Database" );
	private JMenu setupDatabaseMenu = new JMenu( "Setup" );
	private JMenuItem addSetupDatabaseMenuItem = new JMenuItem( "Add...", KeyEvent.VK_A );
	private JMenuItem removeSetupDatabaseMenuItem = new JMenuItem( "Remove...", KeyEvent.VK_R );
	private JMenuItem connectDatabaseMenuItem = new JMenuItem( "Connect...", KeyEvent.VK_C );
//	private JMenu clusteringMenu = new JMenu( "Clustering" );
//	private JMenuItem ldaClusteringMenuItem = new JMenuItem( "LDA", KeyEvent.VK_L );
	private JMenu helpMenu = new JMenu( "Help" );
	private JMenuItem contentsHelpMenuItem = new JMenuItem( "Contents", KeyEvent.VK_C );
	private JMenuItem aboutHelpMenuItem = new JMenuItem( "About", KeyEvent.VK_A );


	public JSysNetWindow ( String title ) {

		super( title );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setLayout( new BorderLayout( ));
		int x = ( Settings.DESKTOP_WIDTH - Settings.DEFAULT_WIDTH ) / 2;
		int y = ( Settings.DESKTOP_HEIGHT - Settings.DEFAULT_HEIGHT ) / 2;
		this.setBounds( x, y, Settings.DEFAULT_WIDTH, Settings.DEFAULT_HEIGHT );

		this.setupMenu( );

		this.getContentPane( ).add( tabPane, BorderLayout.CENTER );
		this.tabPane.setVisible( true );

		if ( Settings.DEBUG ) {
			this.tabPane.addTab( "Correlation View", new CorrelationDisplayPanel( ) );
			this.tabPane.addTab( "Tab1", new JLabel( "This is Tab One" ));
			this.tabPane.addTab( "Tab2", new JLabel( "This is Tab Two" ));
			this.tabPane.addTab( "Tab3", new JLabel( "This is Tab Three" ));
		}

		this.setVisible( true );
		this.repaint( );
	}

	private void setupMenu( ) {
	
		// FILE MENU
		this.fileMenu.setMnemonic( KeyEvent.VK_F );
		this.fileMenu.getAccessibleContext( ).setAccessibleDescription(
			"Perform file operations" );
		this.fileMenu.add( this.openFileMenuItem );
		this.fileMenu.add( this.saveFileMenuItem );
		this.fileMenu.add( this.printFileMenuItem );
		this.fileMenu.add( this.exitFileMenuItem );

		// DATABASE MENU
		this.databaseMenu.setMnemonic( KeyEvent.VK_D );
		this.databaseMenu.getAccessibleContext( ).setAccessibleDescription(
			"Manage Database Connections" );
		this.databaseMenu.add( this.setupDatabaseMenu );
		this.setupDatabaseMenu.add( this.addSetupDatabaseMenuItem );
		this.setupDatabaseMenu.add( this.removeSetupDatabaseMenuItem );
		this.databaseMenu.add( this.connectDatabaseMenuItem );


		//CLUSTERING MENU
//		this.clusteringMenu.setMnemonic( KeyEvent.VK_L );
//		this.clusteringMenu.add( this.ldaClusteringMenuItem );

		//HELP MENU
		this.helpMenu.setMnemonic( KeyEvent.VK_H );
		this.helpMenu.getAccessibleContext( ).setAccessibleDescription(
			"JSysNet Help" );
		this.helpMenu.add( this.contentsHelpMenuItem );
		this.helpMenu.add( this.aboutHelpMenuItem );

		this.menuBar.add( this.fileMenu );
		this.menuBar.add( this.databaseMenu );
//		this.menuBar.add( this.clusteringMenu );
		this.menuBar.add( this.helpMenu );

		this.setJMenuBar( this.menuBar );

		this.addMenuListeners( );

	}

	private void addMenuListeners( ) {
		this.openFileMenuItem.addActionListener( this );
		this.saveFileMenuItem.addActionListener( this );
		this.printFileMenuItem.addActionListener( this );
		this.exitFileMenuItem.addActionListener( this );
		this.addSetupDatabaseMenuItem.addActionListener( this );
		this.removeSetupDatabaseMenuItem.addActionListener( this );
		this.connectDatabaseMenuItem.addActionListener( this );
		this.contentsHelpMenuItem.addActionListener( this );
		this.aboutHelpMenuItem.addActionListener( this );
	}

	private void open( ) {
		
	}

	public void actionPerformed( ActionEvent e ) {
		if ( Settings.DEBUG ) {
			System.err.println( String.format( "ActionEvent fired:" ));
			System.err.println( "\tactionCommand: "+e.getActionCommand( ));
			System.err.println( "\t  paramString: "+e.paramString( ));
		}

		if ( e.getSource( ) == this.openFileMenuItem ) {
			System.out.println( "Open" );
			JFileChooser fc = new JFileChooser( );
			fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );	
			int options = fc.showOpenDialog( this );
			if ( options == JFileChooser.APPROVE_OPTION ) {
				DataHandler data = new CSVDataHandler( fc.getSelectedFile( ).getAbsolutePath( ));
				Test.showData( data );
			}
		}
	}
}
