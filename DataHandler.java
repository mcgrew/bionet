import java.util.ArrayList;

public abstract class DataHandler {

	protected ArrayList <Experiment> experiments;

	public abstract void load( String resource );

	public abstract boolean write( );

	public abstract boolean write( String resource );

	public ArrayList <Experiment> getExperiments( ) {
		return this.experiments;
	}

	public ArrayList <Molecule>getMolecules( ){
		return null;
	}

	public boolean addExperiment( Experiment experiment ) {
		return false;
	}

	public Experiment removeExperiment( int index ) {
		return null;
	}

	public Experiment removeExperiment( Experiment experiment ){
		return null;
	}

}
