import java.awt.Toolkit;
import java.awt.Dimension;

public class Settings {

	public static Dimension DESKTOP_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	public static int DESKTOP_WIDTH  = DESKTOP_SIZE.width;
	public static int DESKTOP_HEIGHT = DESKTOP_SIZE.height;
	public static boolean DEBUG = false;
	public static boolean VERBOSE = false;
	public static int DEFAULT_WIDTH = 800;
	public static int DEFAULT_HEIGHT = 600;
	public static String HOME_DIR = System.getenv( "HOME" );
	public static String SETTINGS_FILE = HOME_DIR + "/.jsysnet";

	public static boolean load( ) {
		return load( SETTINGS_FILE );
	}

	public static boolean load( String settingsFile ) {
		return false;
	}
}
