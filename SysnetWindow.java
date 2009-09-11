//import javax.swing.JFrame;
//import javax.swing.JMenuBar;
//import javax.swing.JTabbedPane;
import javax.swing.*;
import java.awt.*;

public class SysnetWindow extends JFrame {

	private JMenuBar menu = new JMenuBar( );
	private JTabbedPane tabPane = new JTabbedPane( );
	private GridBagConstraints constraints = new GridBagConstraints( );
	private static int DEFAULTWIDTH = 800;
	private static int DEFAULTHEIGHT = 600;
	
	public SysnetWindow ( String title ) {

		super( title );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setLayout( new BorderLayout( ));
		int x = ( Sysnet.desktopSize.width - DEFAULTWIDTH ) / 2;
		int y = ( Sysnet.desktopSize.height - DEFAULTHEIGHT ) / 2;
		this.setBounds( x, y, DEFAULTWIDTH, DEFAULTHEIGHT );
		this.getContentPane( ).add( tabPane, BorderLayout.CENTER );
		tabPane.setVisible( true );
		tabPane.addTab( "Tab1", new JLabel( "This is Tab One" ));
		tabPane.addTab( "Tab2", new JLabel( "This is Tab Two" ));
		tabPane.addTab( "Tab3", new JLabel( "This is Tab Three" ));

		this.setVisible( true );
		this.repaint( );

	}
}
