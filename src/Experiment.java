import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

/**
 * A class for holding data about a particular experiment.
 * 
 * @author Thomas McGrew
 * @version 1.0
 */
public class Experiment {

	private HashMap <String,String> attributes;
	private HashMap <String,MoleculeGroup> moleculeGroups;
	private ArrayList <Molecule> molecules;
	private ArrayList <Correlation> correlations;

	/**
	 * Constructor.
	 * 
	 * @param attributes A HashMap containing this Experiment's attributes.
	 */
	public Experiment( HashMap <String,String> attributes ) {
		this.attributes = attributes;
		this.moleculeGroups = new HashMap <String,MoleculeGroup>( );
		this.molecules = new ArrayList <Molecule>( );
		this.correlations = new ArrayList <Correlation>( );
	}
	
	/**
	 * Gets the molecules associated with this Experiment.
	 * 
	 * @return An ArrayList of Molecules.
	 */
	public ArrayList <Molecule> getMolecules( ) {
		return this.molecules;
	}

	/**
	 * Adds a Molecule to this Experiment.
	 * 
	 * @param group A string containing the group this molecule belongs to.
	 * @param molecule The molecule to be added.
	 */
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

	/**
	 * Adds a Molecule to this Experiment.
	 * 
	 * @param molecule The molecule to be added. The group is determined from the
	 *	Molecule's &quot;group&quot; attribute.
	 */
	public void addMolecule( Molecule molecule ) {
		this.addMolecule( molecule.getGroup( ), molecule );
	}

	/**
	 * The MoleculeGroups for this experiement.
	 * 
	 * @return A HashMap containing the MoleculeGroups in this Experiment,
	 *	indexed by group name.
	 */
	public HashMap <String,MoleculeGroup> getMoleculeGroups( ) {
		return this.moleculeGroups;
	}

	/**
	 * Gets a particular MoleculeGroup.
	 * 
	 * @param group The name of the MoleculeGroup to be retrieved.
	 * @return The MoleculeGroup requested.
	 */
	public MoleculeGroup getMoleculeGroup( String group ) {
		return this.moleculeGroups.get( group );
	}

	/**
	 * Gets an array containing the molecule group names.
	 * 
	 * @return a String array containing the names of the MoleculeGroups for this Experiment.
	 */
	public String [ ] getMoleculeGroupNames( ) {
		String [ ] returnValue = this.moleculeGroups.keySet( ).toArray( new String[ 0 ]);
		Arrays.sort( returnValue );
		return returnValue;
	}

	/**
	 * Adds a MoleculeGroup to this Experiment.
	 * 
	 * @param group The name of the group to be added.
	 */
	public void addMoleculeGroup( String group ) {
		this.moleculeGroups.put( group, new MoleculeGroup( group ));
	}

	/**
	 * Gets all attributes for this Experiment.
	 * 
	 * @return A HashMap containing all of the attributes for this experiment indexed by
	 */
	public HashMap <String,String> getAttributes( ) {
		return this.attributes;
	}

	/**
	 * Gets a particular attribute from this Experiment.
	 * 
	 * @param attr The name of the attribute to retrieve.
	 * @return A String containing the requested Attribute.
	 */
	public String getAttribute( String attr ){
		return this.attributes.get( attr );
	}

	/**
	 * Gets the names of all of the attributes for this Experiment.
	 * 
	 * @return A String array containing the attribute names.
	 */
	public String [ ] getAttributeNames( ) {
		String [ ] returnValue = this.attributes.keySet( ).toArray( new String[ 0 ]);
		Arrays.sort( returnValue );
		return returnValue;
	}

	/**
	 * Gets all Molecule Correlations present in this experiment. 
	 * 
	 * @return An ArrayList containing all of the Correlations.
	 */
	public ArrayList <Correlation> getCorrelations( ) {
		return this.correlations;
	}

}


