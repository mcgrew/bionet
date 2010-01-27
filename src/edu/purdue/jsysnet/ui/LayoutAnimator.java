package edu.purdue.jsysnet.ui;

import edu.uci.ics.jung.algorithms.layout.Layout;



public class LayoutAnimator<V,E> implements Runnable {
	
	private Layout<V,E> layout;

	public LayoutAnimator( Layout<V,E> layout ) {
		this.layout = layout;
	}

	public void run( ) {

	}
}


