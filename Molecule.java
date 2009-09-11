import java.util.ArrayList;

public class Molecule {
	
	private int index;
	private String name;
	private String accession;
	private int molecularWeight;
	private String formula;
	private String remarks;
	private ArrayList<Correlation> correlations;
	private Experiment experiment;

	public Molecule( ){
	}

	public void setIndex( int index ) {
		this.index = index;
	}
	public int getIndex( ){
		return this.index;
	}

	public void setName( String name ){
		this.name = name;
	}
	public String getName( ){
		return this.name;
	}

	public void setMolecularWeight( int mw ){
		this.molecularWeight = mw;
	}
	public int getMolecularWeight( ){
		return this.molecularWeight;
	}

	public void setFormula( String formula ){
		this.formula = formula;
	}
	public String getFormula( ){
		return this.formula;
	}

	public void setExperiment( Experiment experiment ){
//		experiment.addMolecule( this );
		this.experiment = experiment;
	}
	public Experiment getExperiment( ){
		return this.experiment;
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
