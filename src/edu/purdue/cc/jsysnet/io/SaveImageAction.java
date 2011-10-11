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

import edu.purdue.bbc.util.Settings;

import java.awt.Component;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

/**
 * A class for saving a copy of a Component to an image.
 */
public class SaveImageAction extends AbstractAction {
	private Component component;

	/**
	 * Creates a new SaveImageAction (normally for use in a menu). The Action
	 * name defaults to "Save Image".
	 * 
	 * @param component The component to be saved to an image.
	 */
	public SaveImageAction( Component component ) {
		this( Settings.getLanguage( ).get( "Save Image" ), component );
	}

	/**
	 * Creates a new SaveImageAction (normally for use in a menu).
	 * 
	 * @param name The Action name which will be displayed in the menu.
	 * @param component The component to be saved to an image.
	 */
	public SaveImageAction( String name, Component component ) {
		super( name );
		this.component = component;
	}

	/**
	 * Creates a new ChartWriter which prompts for a file name and saves the 
	 * image to disk.
	 * 
	 * @param e The event which triggered this action. Not used.
	 */
	public void actionPerformed( ActionEvent e ) {
		try {
			new ChartWriter( this.component ).write( );
		} catch ( IOException exception ) {

		}
	}

	/**
	 * Updates the Component which will be saved when this Action is performed.
	 * 
	 * @param component The new Component to be used when creating the image.
	 */
	public void setComponent( Component component ) {
		this.component = component;
	}

	/**
	 * Gets the currently set Component to be saved.
	 * 
	 * @return The Component to be saved.
	 */
	public Component getComponent( ) {
		return this.component;
	}

	/**
	 * A class for writing the contents of a Component to an image file.
	 */
	public static class ChartWriter extends BufferedImage {
		private Component component;

		/**
		 * Creates a new ChartWriter with the given component as it's source.
		 * 
		 * @param component The Component to be saved to an image.
		 */
		public ChartWriter( Component component ) {
			super( component.getWidth( ), component.getHeight( ), TYPE_INT_RGB );
			this.component = component;
		}

		/**
		 * Writes the image to disk in the specified File.
		 * 
		 * @param file The File to save the image to.
		 */
		public void write( File file ) throws IOException {
			component.paint( this.createGraphics( ));
			String filename = file.getName( );
			String filetype = filename.substring( filename.lastIndexOf( "." ) + 1 );
			if ( !ImageIO.write( this, filetype, file )) {
				Logger.getLogger( getClass( )).error( String.format( 
					"Unrecognized file type for file '%s'\nThe image was not saved", 
					file.getName( )));
			}
		}

		/**
		 * Writes the image to the specified file name.
		 * 
		 * @param file A String containing the file name to be written.
		 */
		public void write( String file ) throws IOException {
			this.write( new File( file ));
		}

		/**
		 * Prompts for a file name to save to and then writes an image to that file.
		 */
		public void write( ) throws IOException {
			JFileChooser fc = new JFileChooser( );
			fc.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
			fc.addChoosableFileFilter( new JpgFileFilter( ));
			fc.addChoosableFileFilter( new GifFileFilter( ));
			fc.addChoosableFileFilter( new PngFileFilter( ));
			int options = fc.showSaveDialog( this.component );
			File file = fc.getSelectedFile( );
			String filename = file.getAbsolutePath( );
			if ( !file.getName( ).contains( "." )) {
				FileFilter ff = fc.getFileFilter( );
				if ( ff instanceof PngFileFilter ) {
					filename += ".png";
				} else if ( ff instanceof JpgFileFilter ) {
					filename += ".jpg";
				} else if ( ff instanceof GifFileFilter ) {
					filename += ".gif";
				}
			}
			if ( options == JFileChooser.APPROVE_OPTION ) {
				this.write( filename );
			}
		}

		private class PngFileFilter extends FileFilter {

			public PngFileFilter( ) {
				super( );
			}

			public boolean accept( File f ) {
				String filename = f.getName( );
				String filetype = filename.substring( filename.lastIndexOf( "." ) + 1 );
				return f.isDirectory( ) || "png".equals( filetype );
			}

			public String getDescription( ) {
				return "PNG Image";
			}
		}

		private class JpgFileFilter extends FileFilter {

			public JpgFileFilter( ) {
				super( );
			}

			public boolean accept( File f ) {
				String filename = f.getName( );
				String filetype = filename.substring( filename.lastIndexOf( "." ) + 1 );
				return f.isDirectory( ) || 
					"jpg".equals( filetype ) || "jpeg".equals( filetype );
			}

			public String getDescription( ) {
				return "Jpeg Image";
			}
		}
		
		private class GifFileFilter extends FileFilter {

			public GifFileFilter( ) {
				super( );
			}

			public boolean accept( File f ) {
				String filename = f.getName( );
				String filetype = filename.substring( filename.lastIndexOf( "." ) + 1 );
				return f.isDirectory( ) || "gif".equals( filetype );
			}

			public String getDescription( ) {
				return "GIF Image";
			}
		}
	}
}


