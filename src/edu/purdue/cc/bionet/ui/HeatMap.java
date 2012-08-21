/*

This file is part of BioNet.

BioNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BioNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with BioNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.bionet.ui;

import edu.purdue.bbc.util.Range;
import edu.purdue.cc.bionet.io.SaveImageAction;
import edu.purdue.cc.bionet.util.Correlation;
import edu.purdue.cc.bionet.util.CorrelationSet;
import edu.purdue.cc.bionet.util.Molecule;
import edu.purdue.cc.bionet.util.MonitorableRange;
import edu.purdue.cc.bionet.util.Spectrum;
import edu.purdue.cc.bionet.util.SplitSpectrum;
import edu.purdue.bbc.util.DaemonListener;
import edu.purdue.bbc.util.UpdateDaemon;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.data.general.DefaultHeatMapDataset;
import org.jfree.ui.RectangleEdge;

import edu.uci.ics.jung.visualization.control.GraphMouseListener;

/**
 * A class for displaying a heat map of correlation values.
 */
public class HeatMap extends JPanel implements MouseListener, 
		GraphMouseListener<Correlation>, ChangeListener, 
		GraphItemChangeListener<Molecule>, Scalable, MouseWheelListener, 
		ComponentListener,DaemonListener {

	private List <Molecule> moleculeList;
	private int tickSize = 0;
	private ArrayList<GraphMouseListener> graphMouseListeners = 
		new ArrayList<GraphMouseListener>( );
	private Rectangle mapPosition;
	private String title;
	private MonitorableRange range;
	private JScrollPane scrollPane;
	private float currentZoom = 1.0f;
	private Spectrum spectrum;
	private SpectrumLegend spectrumLegend;
	private CorrelationSet correlations;
	private Number correlationMethod;
  private BufferedImage mapColors;
  private BufferedImage gridLines;
  private UpdateDaemon updateDaemon = UpdateDaemon.create( 100 );

	public HeatMap ( CorrelationSet correlations, Collection<Molecule> molecules,
	                 Number correlationMethod ) {
		this( "", correlations, molecules, new MonitorableRange( 0.0, 1.0 ), 
          correlationMethod );
	}

	/**
	 * Creates a new HeatMap with the given parameters.
	 * 
	 * @param title The title for this HeatMap.
	 * @param correlations A CorrelationSet containing the correlations to be
	 *	displayed in this HeatMap.
	 * @param range A MonitorableRange containing the values to be displayed in
	 *	the HeatMap.
	 * @param correlationMethod The correlation method to be used for determining
	 *	the correlation value.
	 */
	public HeatMap ( String title, CorrelationSet correlations,
                   Collection<Molecule> molecules, MonitorableRange range, 
                   Number correlationMethod ) {
		super( );
		// add a context menu for saving the graph to an image
		new ContextMenu( this ).add( new SaveImageAction( this ));
		this.correlations = correlations;
		this.scrollPane = new JScrollPane( this );
		this.title = title;
		this.range = range;
		this.correlationMethod = correlationMethod;
		range.addChangeListener( this );
		this.addMouseListener( this );
		this.addMouseWheelListener( this );
		this.addGraphMouseListener( this );
		this.addComponentListener( this );
		this.moleculeList = new ArrayList( molecules );
		this.spectrum = new SplitSpectrum( range, this.getBackground( ));
		this.spectrum.setOutOfRangePaint( this.getBackground( ));
		this.spectrumLegend = new SpectrumLegend( this.spectrum, new Range( -1.0, 1.0 ));
		this.setLayout( null );
		this.add( this.spectrumLegend );
    this.updateDaemon.update( this );
    this.updateDaemon.start( );
	}

	/**
	 * Returns a new DefaultHeatMapDataset for use in drawing the HeatMap.
	 * 
	 * @return The DefaultHeatMapDataset.
	 */
	private DefaultHeatMapDataset getDataset( ) {
		int size = moleculeList.size( );
		DefaultHeatMapDataset returnValue = new DefaultHeatMapDataset( 
			size, size,
			0.0, (double)size,
			0.0, (double)size );
		for( int i=0; i < size; i++ ){
			for( int j=0; j < size; j++ ) {
				returnValue.setZValue( i, j, ( i == j ) ? Double.NaN :
					this.correlations.getCorrelation( moleculeList.get( i ), 
						moleculeList.get( j )).getValue( correlationMethod ));
			}
		}
		return returnValue;
	}

	/**
	 * Sets a new background color for this HeatMap.
	 * 
	 * @param color The new Color to use for the background.
	 */
	@Override
	public void setBackground( Color color ) {
		super.setBackground( color );
		if ( this.spectrum != null )
			this.spectrum.setOutOfRangePaint( color );
		if ( this.spectrumLegend != null )
			this.spectrumLegend.setBackground( color );
	}

	/**
	 * Sets a new foreground Color for this HeatMap.
	 * 
	 * @param color The new color to be used for drawing the foreground.
	 */
	@Override
	public void setForeground( Color color ){
		super.setForeground( color );
		if ( this.spectrumLegend != null )
			this.spectrumLegend.setForeground( color );
	}

	/**
	 * Returns a JScrollPane containing this element.
	 * 
	 * @return a JScrollPane containing this element.
	 */
	public JScrollPane getScrollPane( ) {
		return this.scrollPane;
	}

	/**
	 * Scales the graph view by the given amount.
	 * 
	 * @param amount The multiplier to apply to the scaling.
	 * @return The new zoom level.
	 */
	public float scale( float amount ) {
		return this.scaleTo( this.currentZoom * amount, this.getCenterPoint( ));
	}

	/**
	 * Scales the graph view by the givem amount, centered on the 
	 * given point.
	 * 
	 * @param amount The multiplier to apply to the scaling.
	 * @param center The center point for the scaling operation.
	 * @return The new zoom level.
	 */
	public float scale( float amount, Point2D center ) {
		return this.scaleTo( this.currentZoom * amount, center );
	}

	/**
	 * Scales to the given graph level, 1.0 being 100%.
	 * 
	 * @param level The level to zoom to.
	 * @return The new zoom level.
	 */
	public float scaleTo( float level ) {
		return this.scaleTo( level, this.getCenterPoint( ));
	}

	/**
	 * Scales to the given zoom level, 1.0 being 100%, centered on the 
	 * given point.
	 * 
	 * @param level The level to zoom to.
	 * @param center The center point for the scaling operation.
	 * @return The new zoom level.
	 */
	public float scaleTo( float level, Point2D center ) {
		float oldZoom = this.currentZoom;
		this.currentZoom = Math.max( level, 0.99f );
		Dimension viewSize;
		if ( level < 1.0f ) {
			viewSize = this.scrollPane.getSize( );
		} else {
			viewSize = this.scrollPane.getViewport( ).getExtentSize( );
		}
		Dimension newSize = new Dimension( 
			(int)( viewSize.width  * currentZoom ),
			(int)( viewSize.height * currentZoom ));
		this.setPreferredSize( newSize );
		this.setSize( newSize );

		// translate the new view position so the mouse is in the same place
		// on the scaled view.
		JViewport vp = this.scrollPane.getViewport( );
		double centerX = center.getX( );
		double centerY = center.getY( );
		double viewPortMouseX = centerX - vp.getViewPosition( ).getX( );
		double viewPortMouseY = centerY - vp.getViewPosition( ).getY( );
		centerX *= currentZoom / oldZoom;
		centerY *= currentZoom / oldZoom;
		viewPortMouseX = centerX - viewPortMouseX;
		viewPortMouseY = centerY - viewPortMouseY;
		vp.setViewPosition( new Point( (int)viewPortMouseX, (int)viewPortMouseY ));

    if ( oldZoom != this.currentZoom ) {
      this.gridLines = null;
    }
    this.updateDaemon.update( this );
		return this.currentZoom;
	}

	/**
	 * Get the center point for the graph.
	 * 
	 * @return The center point of this graph as a Point2D.
	 */
	public Point2D getCenterPoint( ) {
		Dimension size = this.getSize( );
		return new Point2D.Double( size.width / 2.0, size.height / 2.0 );
	}

	/**
	 * This method retrieves a heatmap image from jfreechart and places it on the panel
	 * along with black divider lines and labels to create a heat map graph.
	 * 
	 * @param g The Graphics for the jpanel.
	 */
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
    int leftEdge = this.getWidth( ) / 8;
    int topEdge = this.getHeight( ) / 32;
    int bottomEdge = this.getHeight( ) * 7 / 8;
    int rightEdge = this.getWidth( ) * 31 / 32;
	  this.mapPosition = new Rectangle( leftEdge, topEdge, rightEdge - leftEdge, 
                                      bottomEdge - topEdge );
		
    if ( this.mapColors != null ) {
      g.drawImage( this.mapColors, leftEdge, topEdge, rightEdge - leftEdge, 
                   bottomEdge - topEdge, this.getBackground( ), this );
      if ( this.gridLines != null ) {
        g.drawImage( this.gridLines, 0, 0, this.getWidth( ), this.getHeight( ), 
                     null, this );
      } else {
        this.updateDaemon.update( this );
      }
    }
	}

  public void daemonUpdate( ) {
    this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
    try {
      this.drawGrid( );
      if ( this.mapColors == null ) {
        int size = moleculeList.size( );
        if ( size <= 0 )
          return;
        this.mapColors = new BufferedImage( size, 
                                            size,  
                                            BufferedImage.TYPE_4BYTE_ABGR );
        Graphics g2d = (Graphics2D)this.mapColors.getGraphics( );
        for( int i=0; i < size; i++ ){
          for( int j=0; j < size; j++ ) {
            if ( i != j ) {
              g2d.setColor( (Color)this.spectrum.getPaint( 
                this.correlations.getCorrelation( moleculeList.get( i ), 
                  moleculeList.get( j )).getValue( correlationMethod )));
            } else { 
              g2d.setColor( Color.BLACK );
            }
            g2d.fillRect( i, size-j-1, 1, 1  );
          }
          // if a new update is scheduled, stop this one.
          if ( this.mapColors == null ) {
            return;
          }
          this.repaint( );
        }
      }
    } catch( RuntimeException e ){
      // Something went wrong. Wait a moment and try updating again.
      try {
        Thread.sleep( 1000 );
      } catch( InterruptedException i ) { }
      updateDaemon.update( this );
    }
    this.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ));
  }

  public void drawGrid( ) {
    if ( this.gridLines == null && this.getWidth( ) > 0 && 
         this.getHeight( ) > 0 && this.moleculeList.size( ) > 0 ) {
			float tickStep;
			int leftEdge = this.getWidth( ) / 8;
			int topEdge = this.getHeight( ) / 32;
			int bottomEdge = this.getHeight( ) * 7 / 8;
			int rightEdge = this.getWidth( ) * 31 / 32;
      this.gridLines = new BufferedImage( this.getWidth( ), this.getHeight( ), 
                                          BufferedImage.TYPE_4BYTE_ABGR );
      Graphics g = this.gridLines.getGraphics( );
      g.setColor( this.getForeground( ));
			// y-axis
			int yAxisPos = leftEdge - 1;
	//		g.drawLine( yAxisPos, topEdge, yAxisPos, bottomEdge );
			tickStep = ( bottomEdge - topEdge ) / (float)moleculeList.size( );
			for ( int i=0; i <= moleculeList.size( ); i++ ) {
				int tickY = Math.round( topEdge + i * tickStep );
				g.drawLine( rightEdge, tickY, yAxisPos - tickSize + 1, tickY );
				if ( i < moleculeList.size( )) {
					String name = this.moleculeList.get( this.moleculeList.size( ) - 1 - i ).toString( );
					g.drawString( name,
						yAxisPos - 4 - g.getFontMetrics( ).stringWidth( name ),
						(int)( tickY + tickStep ));
          if ( this.gridLines == null ) {
            return;
          }
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
			// transform clockwise 90 degrees for the vertical text
			AffineTransform at = new AffineTransform();
			at.quadrantRotate( 3 );
			Graphics2D g2d = (Graphics2D)g.create( );
			g2d.transform(at);
			for ( int i=0; i < moleculeList.size( ); i++ ) {
				int tickX = Math.round( leftEdge + i * tickStep );
				String name = this.moleculeList.get( i ).toString( );
				g2d.drawString( name, 
					-(int)(  xAxisPos + 4 + g.getFontMetrics( ).stringWidth( name )),
					(int)( tickX + tickStep )
				);
			}
    }
    this.repaint( );
  }

	/**
	 * Adds a GraphMouseListener to this HeatMap.
	 * 
	 * @param g The GraphMouseListener to add.
	 */
	public void addGraphMouseListener( GraphMouseListener g ) {
		this.graphMouseListeners.add( g );
	}

	/**
	 * Notifies all GraphMouseListeners of an event.
	 * 
	 * @param c The correlation which was clicked on.
	 * @param e The underlying MouseEvent which triggered this event.
	 */
	private void fireGraphMouseEvent( Correlation c, MouseEvent e ) {
		for ( GraphMouseListener g : graphMouseListeners ) {
			g.graphClicked( c, e );
		}
	}

	/**
	 * Translates a set of coordinates into it's respective Correlation.
	 * 
	 * @param p The point on the graph to translate.
	 * @return The Correlation corresponding to that point.
	 */
	private Correlation getCorrelationFromPoint( Point p ) {
			int xComponent = (int)((p.getX( ) - mapPosition.getX( )) * 
					moleculeList.size( ) / mapPosition.getWidth( ));
			int yComponent = moleculeList.size( ) - 1 - (int)((p.getY( ) - mapPosition.getY( )) * 
					moleculeList.size( ) / mapPosition.getHeight( ));
			return this.correlations.getCorrelation( moleculeList.get( xComponent ),
			                                         moleculeList.get( yComponent ));
		
	}

	// MouseListener interface methods
	/**
	 * The mouseClicked method of the MouseListener interface.
	 * 
	 * @param event The MouseEvent which triggered this action.
	 */
	public void mouseClicked( MouseEvent event ) {
		if ( mapPosition.contains( event.getPoint( ))) {
			Correlation clicked = this.getCorrelationFromPoint( new Point( event.getX( ), event.getY( )));
			if ( clicked != null && this.range.contains( 
				   Math.abs( clicked.getValue( this.correlationMethod )))) {
				this.fireGraphMouseEvent( clicked, event );
			}
		}
		
	}
	/**
	 * The mouseEntered method of the MouseListener interface. Not implemented.
	 * 
	 * @param event The MouseEvent which triggered this action.
	 */
	public void mouseEntered( MouseEvent event ) { }
	/**
	 * The mouseExited method of the MouseListener interface. Not implemented.
	 * 
	 * @param event The MouseEvent which triggered this action.
	 */
	public void mouseExited( MouseEvent event ) { }
	/**
	 * The mousePressed method of the MouseListener interface. Not implemented.
	 * 
	 * @param event The MouseEvent which triggered this action.
	 */
	public void mousePressed( MouseEvent event ) { }
	/**
	 * The mouseReleased method of the MouseListener interface. Not implemented.
	 * 
	 * @param event The MouseEvent which triggered this action.
	 */
	public void mouseReleased( MouseEvent event ) { }

	/**
	 * The graphClicked method of the GraphMouseListener interface.
	 * 
	 * @param c The correlation which was clicked on.
	 * @param e The MouseEvent which triggered this action.
	 */
	public void graphClicked( Correlation c, MouseEvent e ) {
		new DetailWindow( this.correlations, c, range, 
		                  this.correlationMethod.intValue( ));
	}
	/**
	 * The graphPressed method of the GraphMouseListener interface. Not implemented.
	 * 
	 * @param c The correlation which was clicked on.
	 * @param e The MouseEvent which triggered this action.
	 */
	public void graphPressed( Correlation c, MouseEvent e ) { }
	/**
	 * The graphReleased method of the GraphMouseListener interface. Not implemented.
	 * 
	 * @param c The correlation which was clicked on.
	 * @param e The MouseEvent which triggered this action.
	 */
	public void graphReleased( Correlation c, MouseEvent e ) { }

	/**
	 * The stateChanged method of the ItemListener interface.
	 * 
	 * @param event the ChangeEvent which triggered this action.
	 */
	// ChangeListener interface method
	public void stateChanged( ChangeEvent event ) {
    this.mapColors = null;
    this.updateDaemon.update( this );
	}

	/**
	 * The stateChanged method of the GraphItemChangeListener interface.
	 * 
	 * @param event The GraphItemChangeEvent which triggered this action.
	 */
	public void stateChanged( GraphItemChangeEvent<Molecule> event ) {
		int change = event.getAction( );
		Molecule molecule = event.getItem( );
		if ( change == GraphItemChangeEvent.REMOVED )
			moleculeList.remove( molecule );
		else if ( change == GraphItemChangeEvent.ADDED && !moleculeList.contains( molecule ))
			moleculeList.add( molecule );
    this.mapColors = null;
    this.gridLines = null;
    this.updateDaemon.update( this );
	}

	/**
	 * The mouseWheelMoved method of the MouseWheelListener interface.
	 * 
	 * @param e The event which triggered this action.
	 */
	public void mouseWheelMoved( MouseWheelEvent e ) {
		this.scale((float)Math.pow( 1.25, -e.getWheelRotation( )), e.getPoint( ));
	}

	/**
	 * The componentHidden method of the ComponentListener interface. 
	 * Not Implemented.
	 * 
	 * @see ComponentListener#componentResized( ComponentEvent )
	 * @param e The event which triggered this action.
	 */
	public void componentHidden( ComponentEvent e ) { }

	/**
	 * The componentMoved method of the ComponentListener interface. 
	 * Moves the legend to the appropriate position when the HeatMap is scrolled
	 * or resized.
	 * 
	 * @see ComponentListener#componentResized( ComponentEvent )
	 * @param e The event which triggered this action.
	 */
	public void componentMoved( ComponentEvent e ) {
		// place the legend
		int h, w;
		if ( this.scrollPane  != null ) {
			Rectangle view = this.scrollPane.getViewport( ).getViewRect( );
			w = view.x;
			h = view.y + view.height;
		} else {
			w = 0;
			h = this.getHeight( );
		}
		
		Rectangle legendRect = new Rectangle( 
			w + 20,
			h - 35,
			150,
			20
		);
		this.spectrumLegend.setBounds( legendRect );
		this.spectrumLegend.repaint( );
	}

	/**
	 * The componentResized method of the ComponentListener interface. 
	 * Not Implemented.
	 * 
	 * @see ComponentListener#componentResized( ComponentEvent )
	 * @param e The event which triggered this action.
	 */
	public void componentResized( ComponentEvent e ) { 
		componentMoved( e );
	}

	/**
	 * The componentShown method of the ComponentListener interface. 
	 * Not Implemented.
	 * 
	 * @see ComponentListener#componentShown( ComponentEvent )
	 * @param e The event which triggered this action.
	 */
	public void componentShown( ComponentEvent e ) { }

}

