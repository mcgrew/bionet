import java.util.*;
import java.io.File;

public class Test {
	public static void main( String [ ] args ) {

		// Test CSVDataHandler
		String resource = ( args.length > 0 ) ? args[ 0 ] : 
			"../data/time_missing/text".replace( "/", File.separator );
		CSVDataHandler c = new CSVDataHandler( resource );
		ArrayList <Experiment> al = c.getExperiments( );
		String [] keys;
		HashMap <String,String> attributes;
		for( int i=0,l=al.size( ); i < l; i++ ) {
			attributes = al.get( i ).getAttributes( );
			keys = attributes.keySet( ).toArray( new String[0] );
			System.out.println( String.format( "Experiement %d:", i ));
			for( int j=0,m=keys.length; j < m; j++ ) {
				System.out.println( String.format( "\t%s: %s", keys[ j ], attributes.get( keys[ j ] )));
			}
		}

	}
}

