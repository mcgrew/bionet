import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Canvas;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class CorrelationDisplayPanel extends JPanel {

	// here are declarations for the controls you created
	private JLabel sortLabel = new JLabel( "Sort list by" );
	private JComboBox sortComboBox = new JComboBox( );
	private JScrollPane correlationDisplayPane = new JScrollPane( );
	private JButton resetButton = new JButton( "Reset" );
	private JButton allButton = new JButton( "All" );
	private JScrollPane moleculeList = new JScrollPane( );

	private JComboBox colorComboBox = new JComboBox( );
	private JComboBox mapComboBox = new JComboBox( );
	
	private JLabel minCorrelationLabel = new JLabel( "Correlation Coefficient Higher Than:" );
	private JLabel maxCorrelationLabel = new JLabel( "Correlation Coefficient Lower Than:" );
	private JSpinner minCorrelationSpinner = 
		new JSpinner( new SpinnerNumberModel( 0.5, 0.0, 1.0, 0.01 ));
	private JSpinner maxCorrelationSpinner = 
		new JSpinner( new SpinnerNumberModel( 1.0, 0.0, 1.0, 0.01 ));
	
	public CorrelationDisplayPanel ( ) {
		super( new BorderLayout( ) );

		JPanel sortSelectionPanel = new JPanel( new BorderLayout( ));
		sortSelectionPanel.add( this.sortLabel, BorderLayout.CENTER );
		sortSelectionPanel.add( this.sortComboBox, BorderLayout.EAST );

		JPanel leftPanel = new JPanel( new BorderLayout( ));
		leftPanel.add( sortSelectionPanel, BorderLayout.NORTH );
		leftPanel.add( this.moleculeList, BorderLayout.CENTER );

		JPanel moleculeButtonPanel = new JPanel( new BorderLayout( ) );
		moleculeButtonPanel.add( this.allButton, BorderLayout.WEST );
		moleculeButtonPanel.add( this.resetButton, BorderLayout.EAST );	

		JPanel correlationValuePanel = new JPanel( new BorderLayout( ));
		correlationValuePanel.add( this.minCorrelationSpinner, BorderLayout.NORTH );
		correlationValuePanel.add( this.maxCorrelationSpinner, BorderLayout.SOUTH );
		this.minCorrelationSpinner.setPreferredSize( new Dimension( 70, 25 ));
		this.maxCorrelationSpinner.setPreferredSize( new Dimension( 70, 25 ));

		JPanel correlationLabelPanel = new JPanel( new BorderLayout( ));
		

		JPanel bottomLeftPanel = new JPanel( new BorderLayout( ));
		bottomLeftPanel.add( moleculeButtonPanel, BorderLayout.WEST );
		bottomLeftPanel.add( correlationValuePanel, BorderLayout.EAST );

		JPanel bottomRightPanel = new JPanel( new BorderLayout( ));
		bottomRightPanel.add( this.colorComboBox, BorderLayout.NORTH );
		bottomRightPanel.add( this.mapComboBox, BorderLayout.SOUTH );

		JPanel bottomPanel = new JPanel( new BorderLayout( ));
		bottomPanel.add( bottomLeftPanel, BorderLayout.CENTER );
		bottomPanel.add( bottomRightPanel, BorderLayout.EAST ); 

		this.add( this.correlationDisplayPane, BorderLayout.CENTER );
		this.add( leftPanel, BorderLayout.WEST );
		this.add( bottomPanel, BorderLayout.SOUTH );
			
		// control configuration
		this.sortComboBox.addItem( "Index" );
		this.sortComboBox.addItem( "Group" );
		this.sortComboBox.addItem( "Name" );
		this.colorComboBox.addItem( "Normal Color" );
		this.colorComboBox.addItem( "High Contrast Color" );
		this.mapComboBox.addItem( "Multiple Circles" );
		this.mapComboBox.addItem( "Single Circle" );
		this.mapComboBox.addItem( "Heat Map" );
	}
}


