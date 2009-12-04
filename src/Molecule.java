import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

/**
 * Representation class for Molecule data
 * 
 * @author Thomas McGrew
 * @version 1.0
 */
public class Molecule {
	
	protected HashMap <String,String> attributes;
	protected ArrayList<Correlation> correlations;
	protected Experiment experiment;

	/**
	 * Constructor.
	 */
	public Molecule( ){
		this.attributes = new HashMap <String,String>( );
		this.correlations = new ArrayList <Correlation>( );
	}

	/**
	 * Gives this Molecule a new attribute.
	 * 
	 * @param attribute The name of the attribute to be set.
	 * @param value The value for the Attribute. 
	 */
	public void setAttribute( String attribute, String value ) {
		// to do: fix this so it checks for an attribute before adding it.
		this.attributes.put( attribute.toLowerCase( ).trim( ), value );
	}

	/**
	 * Retrieve an Attribute for this Molecule.
	 * 
	 * @param attribute The attribute to be retrieved.
	 * @return The value of the attribute as a String.
	 */
	public String getAttribute( String attribute ) {
		return this.attributes.get( attribute.toLowerCase( ).trim( ));
	}

	/**
	 * Get the name of all attributes set for this Molecule.
	 * 
	 * @return An array of Strings containing the names of all attributes.
	 */
	public String [] getAttributeNames( ){
		String [ ] returnValue = this.attributes.keySet( ).toArray( new String[ 0 ] );
		Arrays.sort( returnValue );
		return returnValue;
	}
	 
	/**
	 * Sets the &quot;index&quot; attribute for this Molecule.
	 * 
	 * @param index The index of this Molecule.
	 * @return The index attribute of this Molecule.
	 */
	public void setIndex( int index ) {
		this.setAttribute( "index", Integer.toString( index ));
	}
	/**
	 * Gets the &quot;index&quot; attribute for this Molecule.
	 * 
	 * @return An integer containing the index attribute.
	 */
	public int getIndex( ){
		return Integer.parseInt( this.getAttribute( "index" ));
	}

	/**
	 * Sets the &quot;name&quot; attribute for the Molecule.
	 * 
	 * @param name The value for the &quot;name&quot; attribute.
	 */
	public void setName( String name ){
		this.setAttribute( "name", name );
	}
	/**
	 * Gets the &quot;name&quot; attribute for the Molecule.
	 * 
	 * @return A string containing the &quot;name&quot; attribute for the Molecule.
	 */
	public String getName( ){
		return this.getAttribute( "name" );
	}

	/**
	 * Sets the &quot;molecularWeight&quot; attribute for the Molecule.
	 * 
	 * @param mw the &quot;molecularWeight&quot; for the Molecule.
	 */
	public void setMolecularWeight( int mw ){
		this.setAttribute( "molecularWeight", Integer.toString( mw ));
	}
	/**
	 * Gets the molecularWeight attribute for the Molecule.
	 * 
	 * @return An int containing the &quot;molecularWeight&quot; attribute.
	 */
	public int getMolecularWeight( ){
		return Integer.parseInt( this.getAttribute( "molecularWeight" ));
	}

	/**
	 * Sets the &quot;Formula&quot; attribute for the Molecule.
	 * 
	 * @param formula A string containing the new &quot;formula&quot; attribute.
	 * @return 
	 */
	public void setFormula( String formula ){
		this.setAttribute( "formula", formula );
	}
	/**
	 * Gets the &quot;formula&quot; attribute for the module.
	 * 
	 * @return A string containing the &quot;formula&quot; attribute for the Molecule.
	 */
	public String getFormula( ){
		return this.getAttribute( "formula" );
	}

	/**
	 * Sets the Experiement this Molecule belongs to.
	 * 
	 * @param experiment The experiment this Molecule belongs to.
	 */
	public void setExperiment( Experiment experiment ){
		// ToDo: check that this Molecule actually has the group_name attribute first.
		experiment.addMolecule( this.getAttribute( "group_name" ), this );
		this.experiment = experiment;
	}
	/**
	 * Gets the Experiment this Molecule belongs to.
	 * 
	 * @return An Experiment object containing the experiment this Molecule belongs to.
	 */
	public Experiment getExperiment( ){
		return this.experiment;
	}

	/**
	 * Sets the group name this Molecule belongs to.
	 * 
	 * @param group A string containing the group name for this Molecule.
	 */
	public void setGroup( String group ) {
		this.setAttribute( "group", group );
	}

	/**
	 * Gets the group name this Molecule belongs to.
	 * 
	 * @return A String containing the group name for this Molecule.
	 */
	public String getGroup( ) {
		return this.getAttribute( "group" );
	}

	/**
	 * Adds a Correlation to this Molecule
	 * 
	 * @param correlation The new correlation to add to this Molecule.
	 * @return True if the operation is successful, false otherwise.
	 */
	public boolean addCorrelation( Correlation correlation ) {
		this.correlations.add( correlation );
		return true;
	}
	/**
	 * Remove a correlation from this Molecule. 
	 * 
	 * @param index The index of the correlation to be removed.
	 * @return True if the operation is successful, false otherwise.
	 */
	public boolean removeCorrelation( int index ) {
		return false;
	}
	/**
	 * Remove a correlation from this Molecule.
	 * 
	 * @param correlation The Correlation to be removed from this Molecule.
	 * @return True if the operation is successful, false otherwise.
	 */
	public boolean removeCorrelation( Correlation correlation ) {
		return false;
	}

	/**
	 * Gets a Correlation which is has the passed in Molecule as it's other
	 * Molecule.
	 * 
	 * @param molecule The Molecule for which the appropriate Correlation is to
	 *	be retrieved.
	 * @return The Correlation if it is found, or null if not.
	 */
	public Correlation getCorrelation( Molecule molecule ) {
		for( Correlation correlation : this.correlations ) {
			if ( correlation.hasMolecule( molecule ))
				return correlation;
		}
		return null;
	}

	/**
	 * Gets all correlations attached to this Molecule.
	 * 
	 * @return An ArrayList containing all of the Correlations for this Molecule.
	 */
	public ArrayList <Molecule> getCorrelated( ) {
		ArrayList <Molecule> returnValue = new ArrayList<Molecule>( );
		for ( Correlation correlation : this.correlations ) {
			Molecule[] molecules = correlation.getMolecules( );
			returnValue.add(( molecules[ 1 ] == this ) ? 
			                  molecules[ 0 ] : molecules[ 1 ]);
		}
		return returnValue;
	}
		

}
