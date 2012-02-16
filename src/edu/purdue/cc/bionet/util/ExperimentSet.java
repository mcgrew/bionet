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

package edu.purdue.cc.bionet.util;

import edu.purdue.bbc.io.CSVTableReader;
import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.ArrayList;
import java.io.File;
import java.util.Map;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;


public class ExperimentSet extends SampleGroup {
	protected Set<Molecule> molecules;
	protected File resource;
	protected boolean loaded = false;
	protected String timeUnit;

	@Deprecated
	public ExperimentSet( ) {
		this( "", (File)null );
	}

	/**
	 * Creates a new ExperimentSet
	 * 
	 * @param name The name of this ExperimentSet
	 * @param resource The project resource directory.
	 */
	public ExperimentSet( String name, File resource ) {
		super( name );
		this.setResource( resource );
		this.molecules = new TreeSet<Molecule>( );
	}

	public ExperimentSet( String name, Set<Sample> samples ) {
		super( name );
		this.molecules = new TreeSet( );
		this.addAll( samples );
	}

	/**
	 * Creates a new ExperimentSet based on the passed in ExperimentSet. If the
	 * passed in ExperimentSet has data which was read from disk, the new 
	 * ExperimentSet will read from that original data, losing any modifications.
	 */
	public ExperimentSet( ExperimentSet experiment ) {
		super( experiment.getName( ));
		this.timeUnit = experiment.getTimeUnit( );
		File resource = experiment.getResource( );
		if ( resource != null ) {
			this.resource = experiment.getResource( );
			if ( experiment.isLoaded( )) {
				this.load( );
			}
		} else {
			this.addAll( experiment );
		}
	}

	public void setResource( File resource ) {
		if ( resource != null ) {
			this.resource = new File( resource.getAbsolutePath( ) + 
				File.separator + "Normalization" + File.separator + name + 
				File.separator + "Normalization.csv" );
			this.loaded = false;
		} else {
			this.loaded = true;
		}
	}

	public File getResource( ) {
		return this.resource;
	}

	@Deprecated
	public boolean addSample( Sample sample ) {
		return this.add( sample );
	}

	@Deprecated
	public void addSamples( Collection<Sample> samples ) {
		this.addAll( samples );
	}

	public void setSamples( Collection<Sample> samples ) {
		this.clear( );
		this.addAll( samples );
	}

	@Deprecated
	public Collection<Sample> getSamples( ) {
		return this;
	}

	public Collection<Molecule> getMolecules( ) {
		return this.molecules;
	}

	@Deprecated
	public boolean add( Sample sample ) {
		super.add( sample );
		this.molecules.addAll( sample.getMolecules( ));
		return true;
	}

	public boolean addAll( Collection<? extends Sample> samples ) {
		super.addAll( samples );
		for ( Sample sample : samples ) {
			this.molecules.addAll( sample.getMolecules( ));
		}
		return true;
	}

	public void addMolecule( Molecule molecule ) {
		this.molecules.add( molecule );
	}

	public boolean removeMolecule( Molecule molecule ) {
		for ( Sample sample : this ) {
			sample.removeMolecule( molecule );
		}
		return this.molecules.remove( molecule );
	}

	public String getTimeUnit( ) {
		return this.timeUnit;
	}

	public void setTimeUnit( String timeUnit ) {
		this.timeUnit = timeUnit;
	}

	public boolean isLoaded( ) {
		return this.loaded;
	}

	/**
	 * Loads experiment data from the appropriate file based on the set name
	 * and project resource folder.
	 * 
	 * @return true if there were no errors.
	 */
	public boolean load( ) {
		return this.load( this.resource );
	}

	public Sample getSample( String id ) {
		for ( Sample sample : this ) {
			if ( id.equals( sample.toString( ))) {
				return sample;
			}
		}
		return null;
	}

	/**
	 * Loads experiment data from the specified file.
	 * 
	 * @param resource The file to be read.
	 * @return true if there were no errors.
	 */
	public boolean load( File resource ) {
		Logger logger = Logger.getLogger( getClass( ));
		Language language = Settings.getLanguage( );
		CSVTableReader file;
		if ( resource == null ) {
			logger.debug( "Unable to load data, no resource has been specified" );
			return false;
		}
		Map<String,String> line;
		try {
			file = new CSVTableReader( resource, ",\t" );
		} catch( FileNotFoundException e ) {
			logger.fatal( String.format( language.get( 
				              "Unable to load '%s'. The file was not found." ), 
				              this.resource ) + language.get( "No Data has been imported" ));
			return false;
		}
		try {
			while( file.hasNext( )) {
				line = file.next( );
				String id = line.remove( "id" );
				if ( id == null )
					id = line.remove( "ID" );
				Molecule molecule = new Molecule( id );
				this.molecules.add( molecule );
				// add the remaining attributes to the molecule.
				for( Map.Entry<String,String> entry : line.entrySet( )) {
					// see if this column is a sample value
					Sample sample = this.getSample( entry.getKey( ));
					if ( sample != null ) {
						Number value = new Double( 0.0 );
						try {
							value = new Double( entry.getValue( ));
						} catch ( NumberFormatException exc ) {
							Logger.getLogger( getClass( )).trace( String.format( 
								"Invalid number format for sample value: %s", 
								entry.getValue( )), exc );
						}
						Logger.getLogger( getClass( )).trace( String.format( 
							"Adding sample value %f to %s for sample %s", 
							value, molecule, sample ));
						sample.setValue( molecule, value );
					} else {
						Logger.getLogger( getClass( )).trace( String.format( 
							"Setting attribute %s for Molecule %s to %s", 
							entry.getKey( ), molecule, entry.getValue( )));
						molecule.setAttribute( entry.getKey( ), entry.getValue( ));
					}
				}
			}
		} catch ( Exception e ) {
			logger.debug( e, e );
			logger.error( "An error occurred while reading the data file.\n" +
			              "This file may not be in the correct format." );
			return false;
		}
		ArrayList<Sample> samples = new ArrayList( this );
		for( Sample sample : new ArrayList<Sample>( this )) {
			if ( sample.getMolecules( ).size( ) == 0 ) {
				logger.debug( 
					String.format( language.get( "Dropping empty sample" ) + " %s", 
					               sample.toString( )));
				this.remove( sample );
			}
		}
		file.close( );
		this.loaded = true;
		return true;
	}

	public boolean save( ) {
		return false;
	}

	public SortedSet<SampleGroup> getTimePoints( ) {
		SortedSet<SampleGroup> returnValue = new TreeSet( );
		// place the samples in separate groups based on time.
		for ( Sample sample : this ) {
			String id = sample.getAttribute( "time" ) + " " + this.getTimeUnit( );
			SampleGroup group = null;
			// find the right group for this sample.
			for ( SampleGroup g : returnValue ) {
				if ( g.getName( ).equals( id )) {
					group = g;
				}
			}
			// if the group doesn't exist, create it.
			if ( group == null ) {
				group = new SampleGroup( id );
				returnValue.add( group );
			}
			group.add( sample );
		}
		return returnValue;
	}
}

