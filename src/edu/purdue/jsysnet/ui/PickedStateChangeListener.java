package edu.purdue.jsysnet.ui;

/**
 * Interface class for listening for PickedStateChangeEvents on a graph.
 */
public interface PickedStateChangeListener <T> {

	/**
	 * Called when the picked state has changed.
	 * 
	 * @param event The event which triggered this action.
	 */
	void stateChanged( PickedStateChangeEvent <T> event );
	
}

