package edu.purdue.cc.sysnet.ui.layout;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;

public class CenterLayout<V,E> extends AbstractLayout<V,E> {

	public CenterLayout( Graph<V,E> graph ) {
		super( graph );
	}

	public void initialize( ) {
		int widthCenter = this.size.width / 2;
		int heightCenter = this.size.height / 2;
		for ( V vertex : this.graph.getVertices( )) {
			this.locations.get( vertex ).setLocation( 
				widthCenter, heightCenter );
		}
	}

	public void reset( ) {
		this.initialize( );
	}
}

