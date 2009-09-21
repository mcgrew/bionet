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
		this.attributes.put( attribute.toLowerCase( ), value );
	}

	public String getAttribute( String attribute ) {
		return this.attributes.get( attribute.toLowerCase( ));
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
//		boolean success = correlation.addMolecule( this );
		boolean success = true;
		if ( success ){
			this.correlations.add( correlation );
		}
		return success;
	}
	public boolean removeCorrelation( int index ){
		return false;
	}
	public boolean removeCorrelation( Correlation correlation ){
		return false;
	}
		

}
