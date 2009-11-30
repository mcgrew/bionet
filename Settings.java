import java.awt.Toolkit;
import java.awt.Dimension;
import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

public class Settings extends Properties {

	private static Dimension DESKTOP_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	public static int DESKTOP_WIDTH  = DESKTOP_SIZE.width;
	public static int DESKTOP_HEIGHT = DESKTOP_SIZE.height;
	public static boolean DEBUG = false;
	public static boolean VERBOSE = false;
	public static int DEFAULT_WIDTH = 1024;
	public static int DEFAULT_HEIGHT = 768;
	private static String HOME_DIR = System.getenv( "HOME" );
	private static String SETTINGS_FILE = HOME_DIR + "/.jsysnet";

	public Settings ( ) {
		super( );
	}
	public Settings( String [ ] args ) {
		
	}

	public void save( ) {
		try {
			this.storeToXML( new BufferedOutputStream( 
				new FileOutputStream( new File( SETTINGS_FILE ))), null );
		} catch ( IOException e ) {
			System.out.print( String.format( 
				"Unable to save program settings. File %s is not writeable", SETTINGS_FILE ));
		}
	}
	
	public void load( ) {
		try {
			this.loadFromXML( new BufferedInputStream( 
				new FileInputStream( new File( SETTINGS_FILE ))));
		} catch ( IOException e ) { }
	}

}
