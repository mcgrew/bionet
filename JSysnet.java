import java.awt.Toolkit;
import java.awt.Dimension;

public class JSysNet {

	public static boolean DEBUG = true;
	public static Dimension desktopSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static void main ( String [ ] args ) {
		newWindow( );
	}

	public static void newWindow( ){
		JSysNetWindow s = new JSysNetWindow( "JSysNet" );
	}

}
	
	
