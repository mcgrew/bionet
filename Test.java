import java.util.*;
import java.io.File;

public class Test {
	public static void main( String [ ] args ) {

		// Test CSVDataHandler
		String resource = ( args.length > 0 ) ? args[ 0 ] : 
			"../data/time_missing/text".replace( "/", File.separator );
		readCSV( resource );
	}

	public static void readCSV( String resource ) {
		CSVDataHandler c = new CSVDataHandler( resource );
		showData( c );
	}

	public static void showData( DataHandler c ) {
		ArrayList <Experiment> al = c.getExperiments( );
		String [] keys;
		HashMap <String,String> attributes;
		// Print out the read in data in a readable format
		for( int i=0,l=al.size( ); i < l; i++ ) {
			// Each Experiment
			Experiment exp = al.get( i );
			keys = exp.getAttributeNames( );
			System.out.println( String.format( "Experiment %d:", i ));
			for( int j=0,m=keys.length; j < m; j++ ) {
				// The Experiment attributes
				System.out.println( String.format( "\t%s: %s", keys[ j ], exp.getAttribute( keys[ j ])));
			}
/*			String [ ] groupNames = exp.getMoleculeGroupNames( );
			for ( int j=0,m=groupNames.length; j < m; j++ ) {
				// The MoleculeGroups contained in the Experiment
				MoleculeGroup group = exp.getMoleculeGroup( groupNames[ j ] );
				System.out.println( String.format( "\t\tGroup %s:", group.getName( )));
				ListIterator <Molecule> moleculeIterator = group.getMolecules( ).listIterator( );
				Molecule molecule;
				while( moleculeIterator.hasNext( )) {
					// The Molecule Data for each MoleculeGroup
					molecule = moleculeIterator.next( );
					String [ ] moleculeAttrs = molecule.getAttributeNames( );
					for ( int k=0,n=moleculeAttrs.length; k < n; k++ ) {
						System.out.println( String.format( "\t\t\t%s: %s", 
							moleculeAttrs[ k ], molecule.getAttribute( moleculeAttrs[ k ] )));
					}
					System.out.println( );
				}
			}
		*/	
				
			System.out.println( "\t\tCorrelation Info:" );
			ListIterator <Correlation> iter = exp.getCorrelations( ).listIterator( );
			Molecule [ ] molecules;
			while( iter.hasNext( )) {
				Correlation cor = iter.next( );
				molecules = cor.getMolecules( );
				System.out.println( String.format( "\t\t\t%s, %s: %f",
					molecules[ 0 ].getAttribute( "id" ), molecules[ 1 ].getAttribute( "id" ), cor.getValue( Correlation.PEARSON )));
			}
			// Print molecules data
		}


	}
}

