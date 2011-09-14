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

package edu.purdue.cc.jsysnet.io;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class ChartWriter extends BufferedImage {
	private Component component;

	public ChartWriter( Component component ) {
		super( component.getWidth( ), component.getHeight( ), TYPE_INT_RGB );
		this.component = component;
	}

	public void write( File file ) throws IOException {
		component.paint( this.createGraphics( ));
		ImageIO.write( this, "png", file );
	}

	public void write( String file ) throws IOException {
		this.write( new File( file ));
	}

	public void write( ) throws IOException {
		JFileChooser fc = new JFileChooser( );
		fc.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
		int options = fc.showSaveDialog( this.component );
		if ( options == JFileChooser.APPROVE_OPTION ) {
			this.write( fc.getSelectedFile( ).getAbsolutePath( ));
		}
	}
}


