import java.awt.Toolkit;
import java.awt.Dimension;

public class Sysnet {

	public static DEBUG = true;
	public static Dimension desktopSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static void main ( String [ ] args ) {
		newWindow( );
	}

	public static void newWindow( ){
		SysnetWindow s = new SysnetWindow( "Sysnet" );
	}

}
	
	
