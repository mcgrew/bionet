import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class Experiment {

	private HashMap <String,String> attributes;
	private HashMap <String,MoleculeGroup> moleculeGroups;
	private ArrayList <Molecule> molecules;
	private ArrayList <Correlation> correlations;

	public Experiment( HashMap <String,String> attributes ) {
		this.attributes = attributes;
		this.moleculeGroups = new HashMap <String,MoleculeGroup>( );
		this.molecules = new ArrayList <Molecule>( );
		this.correlations = new ArrayList <Correlation>( );
	}
	
	public ArrayList <Molecule> getMolecules( ) {
		return this.molecules;
	}

	public void addMolecule( String group, Molecule molecule ){
		if ( !moleculeGroups.containsKey( group )) {
			this.addMoleculeGroup( group );
		}
		this.moleculeGroups.get( group ).addMolecule( molecule );
		ListIterator <Molecule> iter = this.molecules.listIterator( );
		while ( iter.hasNext( )) {
			this.correlations.add( new Correlation( iter.next( ), molecule ));
		}
		this.molecules.add( molecule );
	}

	public void addMolecule( Molecule molecule ) {
		this.addMolecule( molecule.getGroup( ), molecule );
	}

	public HashMap <String,MoleculeGroup> getMoleculeGroups( ) {
		return this.moleculeGroups;
	}

	public MoleculeGroup getMoleculeGroup( String group ) {
		return this.moleculeGroups.get( group );
	}

	public String [ ] getMoleculeGroupNames( ) {
		String [ ] returnValue = this.moleculeGroups.keySet( ).toArray( new String[ 0 ]);
		Arrays.sort( returnValue );
		return returnValue;
	}

	public void addMoleculeGroup( String group ) {
		this.moleculeGroups.put( group, new MoleculeGroup( group ));
	}

	public HashMap <String,String> getAttributes( ) {
		return this.attributes;
	}

	public String getAttribute( String attr ){
		return this.attributes.get( attr );
	}

	public String [ ] getAttributeNames( ) {
		String [ ] returnValue = this.attributes.keySet( ).toArray( new String[ 0 ]);
		Arrays.sort( returnValue );
		return returnValue;
	}

	public ArrayList <Correlation> getCorrelations( ) {
		return this.correlations;
	}

}


