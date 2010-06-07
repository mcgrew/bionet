/*

This file is part of JSysNet.

JSysNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JSysNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with JSysNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.Range;
import edu.purdue.jsysnet.util.MonitorableRange;

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

public class CorrelationFilterPanel extends JPanel implements ChangeListener {

	private JLabel minCorrelationLabel = new JLabel( "Higher Than: ", SwingConstants.RIGHT );
	private JLabel maxCorrelationLabel = new JLabel( "Lower Than: ", SwingConstants.RIGHT );
	private JPanel minCorrelationFilterPanel = new JPanel( );
	private JPanel maxCorrelationFilterPanel = new JPanel( );
	private JSpinner minCorrelationSpinner;
	private JSpinner maxCorrelationSpinner;
	private MonitorableRange range; 

	public CorrelationFilterPanel( ) {
		this( 0.6, 1.0 );
	}

	public CorrelationFilterPanel( double low, double high ) {
		this( 0.0, 1.0, 0.05, low, high );
	}

	public CorrelationFilterPanel( double min, double max, double step, double low, double high ) {
		this.range = new MonitorableRange( low, high );
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
		this.minCorrelationSpinner.addChangeListener( this ); 
		this.maxCorrelationSpinner.addChangeListener( this );

		this.setBorder( 
			BorderFactory.createTitledBorder( 
				BorderFactory.createLineBorder( Color.BLACK, 1 ),
				"Correlation Filter",
				TitledBorder.CENTER,
				TitledBorder.TOP
		));
	}


	public Range getRange( ) {
		return range;
	}

	public MonitorableRange getMonitorableRange( ) {
		return range;
	}

	public void stateChanged( ChangeEvent e ) {
		range.setRange((( Double )this.minCorrelationSpinner.getValue( )).doubleValue( ),
										 (( Double )this.maxCorrelationSpinner.getValue( )).doubleValue( ));
	}

}
