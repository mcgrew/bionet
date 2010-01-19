import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class DetailWindow extends JFrame {

	private JTabbedPane tabPane = new JTabbedPane( );

	public DetailWindow( String title ) {
			super( title );
			this.getContentPane( ).add( tabPane, BorderLayout.CENTER );

	}

	public void show( Molecule molecule ) {
		tabPane.add( new MoleculeDetailView( molecule ));
	}

	public void show( Correlation correlation ) {
	}

}
