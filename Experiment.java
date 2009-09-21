import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class Experiment {
	
	private HashMap <String,String> attributes;
	private HashMap <String,MoleculeGroup> moleculeGroups;

	public Experiment( HashMap <String,String> attributes ) {
		this.attributes = attributes;
		this.moleculeGroups = new HashMap <String,MoleculeGroup>( );
	}
	
	public ArrayList <Molecule> getMolecules( ) {
		ArrayList <Molecule> returnvalue = new ArrayList <Molecule>( );
		
		return returnvalue;
	}

	public void addMolecule( String group, Molecule molecule ){
		if ( !moleculeGroups.containsKey( group )) {
			moleculeGroups.put( group, new MoleculeGroup( group ));
		}
		moleculeGroups.get( group ).addMolecule( molecule );
	}

	public void addMolecule( Molecule molecule ) {
		this.addMolecule( molecule.getGroup( ), molecule );
	}

	public HashMap <String,MoleculeGroup> getMoleculeGroups( ) {
		return moleculeGroups;
	}

	public MoleculeGroup getMoleculeGroup( String group ) {
		return moleculeGroups.get( group );
	}

	public String [ ] getMoleculeGroupNames( ) {
		String [ ] returnValue = this.moleculeGroups.keySet( ).toArray( new String[ 0 ]);
		Arrays.sort( returnValue );
		return returnValue;
	}

	public void addMoleculeGroup( String group ) {
		moleculeGroups.put( group, new MoleculeGroup( group ));
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
}


