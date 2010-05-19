package edu.purdue.jsysnet.ui;

import java.awt.Component;

public class PickedStateChangeEvent <T> {
	private Component source;
	private T item;
	private boolean stateChange;

	public PickedStateChangeEvent( Component source, T item, boolean stateChange ) {
		this.source = source;
		this.item = item;
		this.stateChange = stateChange;
	}

	public Component getSource( ) {
		return this.source;
	}

	public T getItem( ) {
		return this.item;
	}

	public boolean getStateChange( ) {
		return this.stateChange;
	}
		

}
