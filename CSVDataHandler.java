import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;

public class CSVDataHandler extends DataHandler {

	private Scanner file;

	public CSVDataHandler( String resource ){
		
		HashMap <String,String> moleculeData = new HashMap <String,String> ( );
		HashMap <String,String> sampleData = new HashMap <String,String> ( );
		String [ ] headings;
		String [ ] columns;
		this.experiments = new ArrayList <Experiment>( );
		String line = new String( );

		// *********************** load Experiment.txt *************************
		try{ 
			this.file = new Scanner( new File( resource+File.separator+"Experiment.txt" ));
		} catch( FileNotFoundException e ) {
			JSysNet.message( "Unable to load "+resource+File.separator+"Experiment.txt. No Data has been imported" );
			this.experiments = new ArrayList <Experiment>( );
		}
		if ( ! file.hasNext( ) )
		{
			JSysNet.message(resource+File.pathSeparator+"Experiment.txt does not appear to be a valid file. "
				+ "No Data has been imported.");
			return;
		}
		line = file.nextLine( );
		headings = line.split( "," );
		HashMap <String,String> columnsHashMap;
		while ( file.hasNextLine( )) {
			line = file.nextLine( );
			columns = line.split( "," );
			int columnLength = columns.length;
			columnsHashMap = new HashMap <String,String>( );
			for ( int i=headings.length-1; i >= 0;  i-- ) {
				columnsHashMap.put( headings[ i ].trim( ), 
					( columnLength > i ) ? columns[ i ].trim( ) : "" );
			}
			this.experiments.add( new Experiment( columnsHashMap ));
		}

		// *********************** load Molecule.txt ***************************
		try {
			this.file = new Scanner( new File( resource+File.separator+"Molecule.txt" ));
		} catch( FileNotFoundException e ) {
			JSysNet.message( "Unable to load "+resource+File.separator+"Molecule.txt. No Data has been imported" );
			this.experiments = new ArrayList <Experiment>( );
		}
			

		

	}

	public ArrayList <Experiment> getExperiments( ) {
		return this.experiments;
	}

	public boolean write( ){
		return false;
	}

	public boolean write( String resource ){
		return false;
	}
}
