package edu.purdue.jsysnet.io;

import java.util.ArrayList;
import edu.purdue.jsysnet.util.*;

public abstract class DataHandler {

	protected ArrayList <Experiment> experiments;

	public abstract void load( String resource );

	public abstract boolean write( );

	public abstract boolean write( String resource );

	public ArrayList <Experiment> getExperiments( ) {
		return this.experiments;
	}

	public ArrayList <Molecule>getMolecules( ){
		ArrayList <Molecule> returnValue = new ArrayList<Molecule>( );
		for ( Experiment e : this.getExperiments( )) {
			returnValue.addAll( e.getMolecules( ));
		}
		return returnValue;
	}

	public void addExperiment( Experiment experiment ) {
		this.experiments.add( experiment );
	}

	public Experiment removeExperiment( int index ) {
		return this.experiments.remove( index );
	}

	public boolean removeExperiment( Experiment experiment ){
		return this.experiments.remove( experiment );
	}

}
