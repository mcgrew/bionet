import java.util.ArrayList;


/**
 * A class for holding data about a group of Molecules.
 *
 * @author Thomas McGrew
 * @version 1.0
 */
public class MoleculeGroup {

	private String name;
	private ArrayList <Molecule> molecules;

	/**
	 * Constructor.
	 * 
	 * @param name The name of this MoleculeGroup.
	 */
	public MoleculeGroup( String name ) {
		this.name = name;
		this.molecules = new ArrayList <Molecule>( );
	}

	/**
	 * Adds a Molecule to this MoleculeGroup.
	 * 
	 * @param molecule The molecule to be added.
	 */
	public void addMolecule( Molecule molecule ) {
		this.molecules.add( molecule );
	}

	/**
	 * Gets all of the Molecules in this group.
	 * 
	 * @return An ArrayList containing all of the molecules.
	 */
	public ArrayList <Molecule> getMolecules( ) {
		return this.molecules;
	}

	/**
	 * Sets the Name of this group
	 * 
	 * @param name The new name for this group.
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * Gets the name for this group.
	 * 
	 * @return A String containing the name of this group.
	 */
	public String getName( ) {
		return this.name;
	}

}
