import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

public class Molecule {
	
	private HashMap <String,String> attributes;
	private ArrayList<Correlation> correlations;
	private Experiment experiment;

	public Molecule( ){
		this.attributes = new HashMap <String,String>( );
		this.correlations = new ArrayList <Correlation>( );
	}

	public void setAttribute( String attribute, String value ) {
		this.attributes.put( attribute.toLowerCase( ).trim( ), value );
	}

	public String getAttribute( String attribute ) {
		return this.attributes.get( attribute.toLowerCase( ).trim( ));
	}

	public String [] getAttributeNames( ){
		String [ ] returnValue = this.attributes.keySet( ).toArray( new String[ 0 ] );
		Arrays.sort( returnValue );
		return returnValue;
	}
	 
	public void setIndex( int index ) {
		this.setAttribute( "index", Integer.toString( index ));
	}
	public int getIndex( ){
		return Integer.parseInt( this.getAttribute( "index" ));
	}

	public void setName( String name ){
		this.setAttribute( "name", name );
	}
	public String getName( ){
		return this.getAttribute( "name" );
	}

	public void setMolecularWeight( int mw ){
		this.setAttribute( "molecularWeight", Integer.toString( mw ));
	}
	public int getMolecularWeight( ){
		return Integer.parseInt( this.getAttribute( "molecularWeight" ));
	}

	public void setFormula( String formula ){
		this.setAttribute( "formula", formula );
	}
	public String getFormula( ){
		return this.getAttribute( "formula" );
	}

	public void setExperiment( Experiment experiment ){
		experiment.addMolecule( this.getAttribute( "group_name" ), this );
		this.experiment = experiment;
	}
	public Experiment getExperiment( ){
		return this.experiment;
	}

	public void setGroup( String group ) {
		this.setAttribute( "group", group );
	}

	public String getGroup( ) {
		return this.getAttribute( "group" );
	}

	public boolean addCorrelation( Correlation correlation ){
		this.correlations.add( correlation );
		return true;
	}
	public boolean removeCorrelation( int index ){
		return false;
	}
	public boolean removeCorrelation( Correlation correlation ){
		return false;
	}

	public Correlation getCorrelation( Molecule molecule ) {
		for( Correlation correlation : this.correlations ) {
			if ( correlation.hasMolecule( molecule ))
				return correlation;
		}
		return null;
	}

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
