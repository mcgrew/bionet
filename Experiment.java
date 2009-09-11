import java.util.HashMap;
import java.util.ArrayList;

public class Experiment {
	
	private HashMap <String,String> attributes;

	public Experiment( HashMap <String,String> attributes ) {
		this.attributes = attributes;
	}
	
	public ArrayList <Molecule> getMolecules( ) {
		return null;
	}

	public HashMap <String,String> getAttributes( ) {
		return this.attributes;
	}

}


