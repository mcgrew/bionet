package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.PolarPoint2D;
import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Settings;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.LayoutDecorator;

import java.util.HashMap;
import java.util.Collection;



public class LayoutAnimator<V,E> implements Runnable {
	
	protected AbstractLayout<V,E> layout;
	protected Layout<V,E> observableLayout;
	protected boolean stopped = false;
	protected final double attractionStep = 0.2;
	protected final double repulsionStep = 0.05;

	public LayoutAnimator( Layout<V,E> layout ) {
		this.observableLayout = layout;
		while ( !AbstractLayout.class.isAssignableFrom( layout.getClass( ))) 
			layout = ((LayoutDecorator<V,E>)layout).getDelegate( );
		this.layout = ( AbstractLayout )layout;
	}

	public void run( ) {

		if ( !AbstractLayout.class.isAssignableFrom( this.layout.getClass( ))) {
			System.err.println( String.format( "Class '%s' is not usable by LayoutAnimator", layout.getClass( ).toString( )));
			return;
		}
		if ( Settings.DEBUG ) {
			System.err.println( "Animating Layout..." );
		}
		while( true ) {
			if ( stopped ) 
				return;
			this.step( );
			try {
				Thread.sleep( 20 );
			} catch ( Exception e ) {
				e.printStackTrace( System.err );
			}
		}
	}

	public synchronized void step ( ) {
		double height = this.observableLayout.getSize( ).height;
		double width = this.observableLayout.getSize( ).width;
		double layoutSize = Math.min( height, width );
		Collection<V> vertices = this.layout.getGraph( ).getVertices( );
		HashMap<V,PolarPoint2D> map = new HashMap<V,PolarPoint2D>( );
		for( V v : vertices ){
			map.put( v, new PolarPoint2D( layout.getX( v ), layout.getY( v )));
		}
		for ( int i=0; i < 3; i++ ) {
		for( V vertex1 : vertices ) {
			PolarPoint2D v1Location = map.get( vertex1 );
			for ( V vertex2 : vertices ) {
				if ( vertex1 != vertex2 ) {
					PolarPoint2D v2Location = map.get( vertex2 );
					double optimum = ( this.calcRepulsion( vertex1, vertex2 ) - 
														this.calcAttraction( vertex1, vertex2 )) *
														layoutSize;
					v1Location.setOrigin( v2Location );
					double newR = v1Location.getR( );
					if ( newR > optimum )
						newR -= this.attractionStep;
					else if ( newR < optimum )
						newR += this.repulsionStep;
					v1Location.setLocation( newR, v1Location.getTheta( ), PolarPoint2D.POLAR );
					v1Location.setLocation( 
						Math.max( 10, Math.min( width - 10, v1Location.getX( ))),
						Math.max( 10, Math.min( height - 10, v1Location.getY( ))));
				}
			}
			this.observableLayout.setLocation( vertex1, v1Location );
		}
		}
	}
	protected double calcAttraction( V v1, V v2 ) {
		return (( Molecule )v1).getCorrelation( (Molecule)v2 ).getValue( ) * 0.75;
	}

	protected double calcRepulsion( V v1, V v2 ) {
		return 0.8;
	}

	public void stop( ) {
		this.stopped = true;
	}
}


