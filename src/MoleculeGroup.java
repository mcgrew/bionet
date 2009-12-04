import java.util.ArrayList;


public class MoleculeGroup {

	private String name;
	private ArrayList <Molecule> molecules;

	public MoleculeGroup( String name ) {
		this.name = name;
		this.molecules = new ArrayList <Molecule>( );
	}

	public void addMolecule( Molecule molecule ) {
		this.molecules.add( molecule );
	}

	public ArrayList <Molecule> getMolecules( ) {
		return this.molecules;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getName( ) {
		return this.name;
	}

}
