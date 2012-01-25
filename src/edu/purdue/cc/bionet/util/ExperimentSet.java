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
import java.util.TreeSet;
import java.util.ArrayList;
import java.io.File;
import java.util.Map;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;


public class ExperimentSet extends TreeSet<Experiment>
                           implements Comparable<ExperimentSet> {
	protected Set<Sample> samples;
	protected Set<Molecule> molecules;
	protected String name;
	protected File resource;
	protected boolean loaded = false;
	protected String timeUnit;

	@Deprecated
	public ExperimentSet( ) {
		this( "", null );
	}

	/**
	 * Creates a new ExperimentSet
	 * 
	 * @param name The name of this ExperimentSet
	 * @param resource The project resource directory.
	 */
	public ExperimentSet( String name, File resource ) {
		this.name = name;
		this.setResource( resource );
		this.samples = new TreeSet<Sample>( );
		this.molecules = new TreeSet<Molecule>( );
	}

	public void setResource( File resource ) {
		if ( resource != null ) {
			this.resource = new File( resource.getAbsolutePath( ) + 
				File.separator + "Normalization" + File.separator + name + 
				File.separator + "Normalization.csv" );
		}
	}

	/**
	 * Creates another instance of this ExperimentSet, reading from the original
	 * file. Any modifications made to the object after loading will not be
	 * retained.
	 * 
	 * @return A new ExperimentSet which will read from the original resource file.
	 */
	public ExperimentSet clone( ) {
		ExperimentSet returnValue = new ExperimentSet( this.name, this.resource );
		if ( this.isLoaded( ))
			returnValue.load( );
		return returnValue;
	}

	public File getResource( ) {
		return this.resource;
	}

	public boolean addSample( Sample sample ) {
		return this.samples.add( sample );
	}

	public void addSamples( Collection<Sample> samples ) {
		this.samples.addAll( samples );
	}

	public void setSamples( Collection<Sample> samples ) {
		this.samples = new TreeSet<Sample>( samples );
	}

	public Collection<Sample> getSamples( ) {
		return this.samples;
	}

	public Collection<Molecule> getMolecules( ) {
		return this.molecules;
	}

	@Override
	public boolean add( Experiment experiment ) {
		this.samples.addAll( experiment.getSamples( ));
		this.molecules.addAll( experiment.getMolecules( ));
		return super.add( experiment );
	}

	@Override
	public boolean addAll( Collection<? extends Experiment> experiments ) {
		for ( Experiment experiment : experiments ) {
			this.samples.addAll( experiment.getSamples( ));
			this.molecules.addAll( experiment.getMolecules( ));
		}
		return super.addAll( experiments );
	}

	public Experiment get( String id ) {
		for ( Experiment e : this ) {
			if ( id.equals( e.getId( )))
				return e;
		}
		return null;
	}

	public String getName( ) {
		return this.name;
	}

	public void setName( String name ) {
		this.name = name;
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
		for ( Sample sample : this.samples ) {
			String expId = Settings.getLanguage( ).get( "Time" ) + " " +
				sample.getAttribute( "Time" );
			if ( this.timeUnit != null ) {
				expId = sample.getAttribute( "Time" ) + " " + this.timeUnit;
			}
			if ( this.get( expId ) == null )
				this.add( new Experiment( expId ));
			this.get( expId ).addSample( sample );
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
				Molecule molecule;
				molecule = new Molecule( id );
				for ( Experiment experiment : this ) {
					experiment.addMolecule( molecule );
				}
				this.molecules.add( molecule );
				// add the remaining attributes to the molecule.
				for( Map.Entry<String,String> entry : line.entrySet( )) {
					// see if this column is a sample value
					Sample sample = null;
					for ( Experiment e : this ) {
						sample = e.getSample( entry.getKey( ));
						if ( sample != null )
							break;
					}
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
		for ( Experiment experiment : this ) {
			ArrayList<Sample> samples = 
				new ArrayList<Sample>( experiment.getSamples( ));
			for( Sample sample : samples ) {
				if ( sample.getMolecules( ).size( ) == 0 ) {
					logger.debug( 
						String.format( "Dropping sample %s", sample.toString( )));
					experiment.removeSample( sample );
				}
			}
		}
		file.close( );
		this.loaded = true;
		return true;
	}

	public boolean save( ) {
		return false;
	}

	public int compareTo( ExperimentSet e ) {
		return this.getName( ).compareTo( e.getName( ));
	}

}

