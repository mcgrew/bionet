import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Canvas;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class CorrelationDisplayPanel extends JPanel {

	// here are declarations for the controls you created
	private JLabel sortLabel = new JLabel( "Sort list by" );
	private JScrollPane correlationDisplayPane = new JScrollPane( );
	private JButton resetButton = new JButton( "Reset" );
	private JButton allButton = new JButton( "All" );
	private JScrollPane moleculeList = new JScrollPane( );
	private JComboBox sortDropDown = new JComboBox( );
	private GridBagConstraints constraints = new GridBagConstraints( );
	
	public CorrelationDisplayPanel ( ) {
		super( new GridBagLayout( ) );

		// here we add the conotrols to the container.

		constraints.fill = GridBagConstraints.NONE;

		constraints.gridx = 0;
		constraints.gridy = 0;
		this.add( sortLabel, constraints );

		constraints.gridx = 1;
		this.add( sortDropDown, constraints );

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridheight = 2;
		this.add( resetButton, constraints );

		constraints.fill = GridBagConstraints.BOTH;

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridheight = 1;
		constraints.gridwidth = 2;
		this.add( moleculeList, constraints );
		moleculeList.setSize( 200, 400 );

		constraints.gridx = 2;
		constraints.gridwidth = 3;
		this.add( correlationDisplayPane, constraints );
		correlationDisplayPane.setSize( 400, 400 );
			
		
			
			
		// control configuration
		sortDropDown.addItem( "Index" );
		sortDropDown.addItem( "Group" );
		sortDropDown.addItem( "Name" );

		this.setSize( 800, 600 );
	}
}


