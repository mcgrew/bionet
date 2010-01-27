package edu.purdue.jsysnet.ui.layout;

import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;

public class MySpringLayout<V,E> extends edu.uci.ics.jung.algorithms.layout.SpringLayout<V,E> {

	public MySpringLayout( Graph<V,E> graph ) {
		super( graph );
	}

	public void initialize( ) {
		this.initialized = true;
	}

}

