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

package edu.purdue.cc.bionet.io;

import edu.purdue.bbc.io.CSVTableWriter;
import edu.purdue.bbc.util.attributes.Attributes;
import edu.purdue.cc.bionet.util.Sample;
import edu.purdue.cc.bionet.util.Experiment;
import edu.purdue.cc.bionet.util.Project;

import java.io.File;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.io.FileWriter;

import org.apache.log4j.Logger;

public class ProjectInfoWriter {
	private Project project;

	public ProjectInfoWriter( Project project ) {
		this.project = project;
	}

	public boolean write( ) throws IOException {
		File infoFile = new File( this.project.getResource( ).getAbsolutePath( ) + 
			File.separator + "project_info.csv" );
		infoFile.createNewFile( ); // if the file does not exist
		Logger.getLogger( getClass( )).debug( 
			"Writing project file to " + infoFile.getAbsolutePath( ));
		BufferedWriter output = new BufferedWriter( new FileWriter( infoFile ));
		for ( Map.Entry<String,String> attribute : 
		      this.project.getAttributes( ).entrySet( )) {
			output.write( "\"" + attribute.getKey( ) + "\",\"" +
			              attribute.getValue( ) + "\"" );
			output.newLine( );
		}
		output.newLine( );
		CSVTableWriter csvWriter = new CSVTableWriter( output, 
			project.getSampleAttributeNames( ));
		for ( Sample sample : project.getSamples( )) {
			csvWriter.write( sample );
		}
		csvWriter.close( );
		return true;
	}
}

