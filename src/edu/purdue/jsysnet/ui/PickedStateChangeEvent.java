package edu.purdue.jsysnet.ui;

import java.awt.Component;

/**
 * An event class for Changes in the picked state of a graph.
 */
public class PickedStateChangeEvent <T> {
	private Component source;
	private T item;
	private boolean stateChange;

	/**
	 * Creates a new PickedStateChangeEvent
	 * 
	 * @param source The Component who triggered this event.
	 * @param item The item whose state has changed.
	 * @param stateChange true if the item is now selected.
	 */
	public PickedStateChangeEvent( Component source, T item, boolean stateChange ) {
		this.source = source;
		this.item = item;
		this.stateChange = stateChange;
	}

	/**
	 * Returns the source Component of this event.
	 * 
	 * @return The source Component of this event.
	 */
	public Component getSource( ) {
		return this.source;
	}

	/**
	 * Returns the item whose state has changed.
	 * 
	 * @return The item whose state has changed.
	 */
	public T getItem( ) {
		return this.item;
	}

	/**
	 * Returns the new state of the item (selected or deselected).
	 * 
	 * @return True if the new state is selected, false otherwise.
	 */
	public boolean getStateChange( ) {
		return this.stateChange;
	}
		

}
