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
import edu.purdue.jsysnet.util.Spectrum;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import javax.swing.JPanel;

public class SpectrumLegend extends JPanel {
	private Spectrum spectrum;
	private Range range;
	private int scaleSpace = 13;
	private boolean drawBorder = true;
	private int tickSpacing = 25;
	private NumberFormat numberFormat = new DecimalFormat( "0.##" );

	/**
	 * Creates a new SpectrumLegend
	 * 
	 * @param spectrum The Spectrum to create a legend for.
	 * @param range The range of values to show on the legend.
	 */
	public SpectrumLegend ( Spectrum spectrum, Range range ) {
		super( );
		this.spectrum = spectrum;
		this.range = range;
	}

	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );
		stamp( g, new Rectangle( 0, 0, this.getWidth( ), this.getHeight( )));
	}

	/**
	 * "Stamps" the Graphics instance with the legend in the area specified.
	 * 
	 * @param g The Graphics object to "stamp"
	 * @param area The area of the Graphics object to stamp.
	 */
	public void stamp( Graphics g, Rectangle area ) {
		// set up a smaller font
		Font origFont = g.getFont( );
		g.setFont( new Font( Font.SANS_SERIF, Font.PLAIN, 8 ));

		// draw the legend
		int legendBottom = area.y + area.height - scaleSpace;
		double[] sequence = range.getSequence( area.width - 2 );
		int stop = area.width - 2;
		for( int i=0; i < stop; i++ ) {
			g.setColor( (Color)this.spectrum.getPaint( sequence[ i ]));
			g.drawLine( area.x+i+1, area.y, area.x+i+1, legendBottom );
		}

		g.setColor( Color.BLACK );
		if ( drawBorder )
			g.drawRect( area.x, area.y, area.width-1, legendBottom - area.y );
		if ( scaleSpace > 0 ) {
			for( double i : new Range( 0, 1 ).getSequence( area.width / tickSpacing ))
				placeLabel( g, area, i, scaleSpace );
		}

		// reset the font
		g.setFont( origFont );

	}

	protected void placeLabel( Graphics g, Rectangle area, double percent, int scaleSpace ) {
		int legendBottom = area.y + area.height - scaleSpace;
		FontMetrics f = g.getFontMetrics( );

		if ( drawBorder ) {
			int tickX = area.x + Math.min( (int)( area.width * percent ), area.width - 1 );
			g.drawLine( (int)tickX, area.y,
		              (int)tickX, legendBottom + Math.min( scaleSpace, 4 ));
		}
		if ( scaleSpace > 0 ) {
			String s = numberFormat.format( range.getMin( ) + (range.getMax( ) - range.getMin( ))*percent);
			int width = f.stringWidth( s );
			g.drawString( 
				s,
				area.x + Math.max( 0, 
					Math.min( (int)( area.width * percent - width / 2 ), 
						area.width - width )),
				area.y + area.height - 2 );
		}
	}

	public void setLabelFormat( NumberFormat f ) {
		this.numberFormat = f;
	}

	public NumberFormat getLabelFormat( ) {
		return this.numberFormat;
	}

	public void setTickSpacing( int i ) {
		this.tickSpacing = i;
	}

	public int getTickSpacing( ) {
		return this.tickSpacing;
	}

	public void setBounds( Rectangle rect ) {
		Graphics g = this.getGraphics( );
		if ( g != null ) {
			g.setColor( this.getParent( ).getBackground( ));
			g.fillRect( 0, 0, this.getWidth( ), this.getHeight( ));
		}
		super.setBounds( rect );
	}
}
