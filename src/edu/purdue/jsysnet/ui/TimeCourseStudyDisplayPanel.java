/*

This file is part of JSysNet.

JSysNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JSysNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with JSysNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.Experiment;
import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.io.ArffMoleculeReader;

import java.io.BufferedReader;
import java.util.Collection;
import javax.swing.JPanel;

import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.neural.lvq.Som;
import weka.classifiers.trees.J48;
import weka.classifiers.Classifier;

public class TimeCourseStudyDisplayPanel extends JPanel {

	public TimeCourseStudyDisplayPanel( ) {
		super( );
	}
		
	public boolean createGraph( Collection <Experiment> experiments ) {
		String [] options = new String [0];
		try {
			System.out.println( "Reading data..." );
			BufferedReader reader = new BufferedReader( new ArffMoleculeReader( experiments, true ));
			System.out.println( "Creating instance..." );
			Instances data = new Instances( reader );
			Classifier classifier = new Som( );
			System.out.println( "Building Classifier..." );
			classifier.buildClassifier( data );
			System.out.println( "Creating Evaluation..." );
			Evaluation eval = new Evaluation( data );
			System.out.println( "Evaluating..." );
			eval.evaluateModel( classifier, options );
			System.out.println( eval.toSummaryString( "\nResultes\n========\n", false ));
		} catch ( Exception e ) {
			e.printStackTrace( );
		}
		
		return true;
	}

	public String getTitle( ) {
		return "Time Course Study";
	}

}



