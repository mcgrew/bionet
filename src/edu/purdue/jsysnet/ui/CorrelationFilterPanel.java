package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.Range;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.SwingConstants;
import javax.swing.SpinnerNumberModel;

public class CorrelationFilterPanel extends JPanel {

	private JLabel minCorrelationLabel = new JLabel( "Higher Than: ", SwingConstants.RIGHT );
	private JLabel maxCorrelationLabel = new JLabel( "Lower Than: ", SwingConstants.RIGHT );
	private JPanel minCorrelationFilterPanel = new JPanel( );
	private JPanel maxCorrelationFilterPanel = new JPanel( );
	private JSpinner minCorrelationSpinner;
	private JSpinner maxCorrelationSpinner;
	private EdgeFilterChangeListener efcl;   

	public CorrelationFilterPanel( ) {
		this( 0.6, 1.0 );
	}

	public CorrelationFilterPanel( double low, double high ) {
		this( 0.0, 1.0, 0.05, low, high );
	}

	public CorrelationFilterPanel( double min, double max, double step, double low, double high ) {
		this.setLayout( new BorderLayout( ));
		this.minCorrelationSpinner = new JSpinner( new SpinnerNumberModel( low, min, max, step ));
		this.maxCorrelationSpinner = new JSpinner( new SpinnerNumberModel( high, min, max, step ));

		this.minCorrelationSpinner.setPreferredSize( new Dimension( 80, 25 ));
		this.maxCorrelationSpinner.setPreferredSize( new Dimension( 80, 25 ));

		this.minCorrelationFilterPanel = new JPanel( new BorderLayout( ));
		this.minCorrelationFilterPanel.add( this.minCorrelationSpinner, BorderLayout.EAST );
		this.minCorrelationFilterPanel.add( this.minCorrelationLabel, BorderLayout.CENTER );

		this.maxCorrelationFilterPanel = new JPanel( new BorderLayout( ));
		this.maxCorrelationFilterPanel.add( this.maxCorrelationSpinner, BorderLayout.EAST );
		this.maxCorrelationFilterPanel.add( this.maxCorrelationLabel, BorderLayout.CENTER );

		this.add( this.minCorrelationFilterPanel, BorderLayout.NORTH );
		this.add( this.maxCorrelationFilterPanel, BorderLayout.SOUTH );

		this.setBorder( 
			BorderFactory.createTitledBorder( 
				BorderFactory.createLineBorder( Color.BLACK, 1 ),
				"Correlation Filter",
				TitledBorder.CENTER,
				TitledBorder.TOP
		));
	}


	public void setVisualization( CorrelationGraphVisualizer v ) {
		// add event listeners to the spinners to watch for changes.
		// remove the current listeners (if any)
		if ( this.efcl != null ) {
			this.minCorrelationSpinner.removeChangeListener( this.efcl );
			this.minCorrelationSpinner.removeChangeListener( this.efcl );
		}
		// add some new listeners
		this.efcl = new EdgeFilterChangeListener( v );
		this.minCorrelationSpinner.addChangeListener( this.efcl ); 
		this.maxCorrelationSpinner.addChangeListener( this.efcl );
	}

	public Range getRange( ) {
		return new Range((( Double )this.minCorrelationSpinner.getValue( )).doubleValue( ),
										 (( Double )this.maxCorrelationSpinner.getValue( )).doubleValue( ));
	}

	protected class EdgeFilterChangeListener implements ChangeListener {
		private CorrelationGraphVisualizer graph;

		public EdgeFilterChangeListener( CorrelationGraphVisualizer v ) {
			this.graph = v;
		}

		public void stateChanged( ChangeEvent e ) {
			this.graph.filterEdges( );
			this.graph.repaint( );
		}
	}

}
