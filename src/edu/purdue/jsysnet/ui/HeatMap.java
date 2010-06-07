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

import edu.purdue.jsysnet.util.Spectrum;
import edu.purdue.jsysnet.util.Correlation;
import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.MonitorableRange;

import java.util.Collection;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.jfree.data.general.DefaultHeatMapDataset;
import org.jfree.data.general.HeatMapDataset;
import org.jfree.data.general.HeatMapUtilities;
import org.jfree.chart.axis.Axis;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;

import edu.uci.ics.jung.visualization.control.GraphMouseListener;

public class HeatMap extends JPanel implements MouseListener, GraphMouseListener<Correlation>,ChangeListener {
	private DefaultHeatMapDataset dataset;
	private List <Molecule> moleculeList;
	private int tickSize = 0;
	private ArrayList<GraphMouseListener> graphMouseListeners = 
		new ArrayList<GraphMouseListener>( );
	private Rectangle mapPosition;
	private String title;
	private MonitorableRange range;

	public HeatMap ( Collection <Molecule> molecules ) {
		this( "", molecules, new MonitorableRange( 0.0, 1.0 ));
	}

	public HeatMap ( String title, Collection <Molecule> molecules, MonitorableRange range ) {
		super( );
		this.title = title;
		this.range = range;
		range.addChangeListener( this );
		this.addMouseListener( this );
		this.addGraphMouseListener( this );
		this.moleculeList = new Vector( molecules );
		int size = moleculeList.size( );
		this.dataset = new DefaultHeatMapDataset( 
			size, size,
			0.0, (double)size,
			0.0, (double)size );
		for( int i=0; i < size; i++ ){
			for( int j=0; j < size; j++ ) {

				dataset.setZValue( i, j, (i==j)? Double.NaN : 
					Math.abs( Correlation.getValue( 
						moleculeList.get( i ), moleculeList.get( j ))));
			}
		}
	}

	/**
	 * This method retrieves a heatmap image from jfreechart and places it on the panel
	 * along with black divider lines and labels to create a heat map graph.
	 * 
	 * @param g The Graphics for the jpanel.
	 */
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		float tickStep;
		BufferedImage drawing = HeatMapUtilities.createHeatMapImage( 
			this.dataset, new Spectrum( range ));
		int leftEdge = this.getWidth( ) / 8;
		int topEdge = this.getHeight( ) / 32;
		int bottomEdge = this.getHeight( ) * 7 / 8;
		int rightEdge = this.getWidth( ) * 31 / 32;
		mapPosition = new Rectangle( leftEdge, topEdge, rightEdge - leftEdge, bottomEdge - topEdge );
		g.drawImage( drawing, leftEdge, topEdge, rightEdge - leftEdge, bottomEdge - topEdge, Color.WHITE, this );
		// y-axis
		int yAxisPos = leftEdge - 1;
//		g.drawLine( yAxisPos, topEdge, yAxisPos, bottomEdge );
		tickStep = ( bottomEdge - topEdge ) / (float)moleculeList.size( );
		for ( int i=0; i <= moleculeList.size( ); i++ ) {
			int tickY = Math.round( topEdge + i * tickStep );
			g.drawLine( rightEdge, tickY, yAxisPos - tickSize, tickY );
			if ( i < moleculeList.size( )) {
				String name = this.moleculeList.get( this.moleculeList.size( ) - 1 - i ).toString( );
				g.drawString( name,
					yAxisPos - 4 - g.getFontMetrics( ).stringWidth( name ),
					(int)( tickY + tickStep ));
			}
		}
		    
		// x-axis
		int xAxisPos = bottomEdge;
		tickStep = ( rightEdge - leftEdge ) / (float)moleculeList.size( );
//		g.drawLine( leftEdge, xAxisPos, rightEdge, xAxisPos );
		for ( int i=0; i <= moleculeList.size( ); i++ ) {
			int tickX = (int)( leftEdge + i * tickStep );
			g.drawLine( tickX, topEdge, tickX, xAxisPos + tickSize );
		}
		// clockwise 90 degrees
		AffineTransform at = new AffineTransform();
		at.quadrantRotate( 3 );
		((Graphics2D)g).transform(at);
		for ( int i=0; i < moleculeList.size( ); i++ ) {
			int tickX = Math.round( leftEdge + i * tickStep );
			String name = this.moleculeList.get( i ).toString( );
			g.drawString( name, 
				-(int)(  xAxisPos + 4 + g.getFontMetrics( ).stringWidth( name )),
				(int)( tickX + tickStep )
			);
		}

	}

	public void addGraphMouseListener( GraphMouseListener g ) {
		this.graphMouseListeners.add( g );
	}

	private void fireGraphMouseEvent( Correlation c, MouseEvent e ) {
		for ( GraphMouseListener g : graphMouseListeners ) {
			g.graphClicked( c, e );
		}
	}

	private Correlation getCorrelationFromPoint( Point p ) {
			int xComponent = 
				(int)((p.getX( ) - mapPosition.getX( )) * moleculeList.size( ) / mapPosition.getWidth( ));
			int yComponent = moleculeList.size( ) - 1 - 
				(int)((p.getY( ) - mapPosition.getY( )) * moleculeList.size( ) / mapPosition.getHeight( ));
			return moleculeList.get( xComponent ).getCorrelation( moleculeList.get( yComponent ));
		
	}

	// MouseListener interface methods
	public void mouseClicked( MouseEvent event ) {
		if ( mapPosition.contains( event.getPoint( ))) {
			Correlation clicked = this.getCorrelationFromPoint( new Point( event.getX( ), event.getY( )));
			if ( clicked != null && this.range.contains( Math.abs( clicked.getValue( )))) {
				this.fireGraphMouseEvent( clicked, event );
			}
		}
		
	}
	public void mouseEntered( MouseEvent event ) { }
	public void mouseExited( MouseEvent event ) { }
	public void mousePressed( MouseEvent event ) { }
	public void mouseReleased( MouseEvent event ) { }

	// GraphMouseListener interface methods
	public void graphClicked( Correlation c, MouseEvent e ) {
		new DetailWindow( this.title, c, range );
	}
	public void graphPressed( Correlation c, MouseEvent e ) { }
	public void graphReleased( Correlation c, MouseEvent e ) { }

	// ChanteListener interface method
	public void stateChanged( ChangeEvent event ) {
//		MonitorableRange range = (MonitorableRange)event.getSource( );	
		this.repaint( );
	}
}

