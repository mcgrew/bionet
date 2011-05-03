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

package edu.purdue.cc.jsysnet.ui;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;

/**
 * A base class for A JPanel containing a CheckBoxTree with some convenience
 * methods.
 */
public abstract class CheckboxTreePanel extends JPanel {

	protected CheckboxTree tree;
	private Collection<TreeSelectionListener> treeSelectionListeners;
	private Collection<TreeCheckingListener> treeCheckingListeners;

	/**
	 * Creates a new SelectorTreePanel with a BorderLayout
	 */
	protected CheckboxTreePanel( ) {
		this( new DefaultMutableTreeNode( ));
		this.tree.setRootVisible( false );
	}

	/**
	 * Creates a new SelectorTreePanel with the specified LayoutManager.
	 */
	protected CheckboxTreePanel( TreeNode rootNode ) {
		super( new BorderLayout( ));
		this.treeSelectionListeners = new ArrayList<TreeSelectionListener>( );
		this.treeCheckingListeners = new ArrayList<TreeCheckingListener>( );
		this.tree = new CheckboxTree( rootNode );
		this.tree.setSelectsByChecking( false );
		this.tree.getCheckingModel( ).setCheckingMode( 
			TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );

		CustomTreeListener customListener = new CustomTreeListener( this );
		this.tree.addTreeCheckingListener( customListener );
		this.tree.addTreeSelectionListener( customListener );

		this.add( new JScrollPane( this.tree ), BorderLayout.CENTER );
	}

	/**
	 * A private class for handling internal Tree events and notifying any
	 * outside listeners.
	 */
	private class CustomTreeListener 
	        implements TreeSelectionListener,TreeCheckingListener {
			private CheckboxTreePanel source;

			public CustomTreeListener( CheckboxTreePanel source ) {
				this.source = source;
			}

			public void valueChanged( TreeSelectionEvent e ) {
				TreeSelectionEvent myEvent = 
					(TreeSelectionEvent)e.cloneWithSource( this.source );
				for ( TreeSelectionListener l : treeSelectionListeners ) {
					l.valueChanged( myEvent );
				}
			}

			public void valueChanged( TreeCheckingEvent e ) {
				TreeCheckingEvent myEvent = 
					new TreeCheckingEvent( this.source, e.getPath( ), e.isCheckedPath( ));
				for ( TreeCheckingListener l : treeCheckingListeners ) {
					l.valueChanged( myEvent );
				}
			}
		}

	/**
	 * Gets the tree associated with this Panel.
	 * 
	 * @return The CheckboxTree contained within this panel.
	 */
	public CheckboxTree getTree( ) {
		return this.tree;
	}

	/**
	 * Gets the root node of the contained tree.
	 * 
	 * @return The root TreeNode of the tree.
	 * @throws ClassCastException if the root node cannot be cast as a
	 *	DefaultMutableTreeNode.
	 */
	public DefaultMutableTreeNode getRoot( ) {
		return (DefaultMutableTreeNode)this.tree.getModel( ).getRoot( );
	}

	/**
	 * Causes the tree to recheck it's nodes to look for changes.
	 */
	public void reload( ) {
		((DefaultTreeModel)this.tree.getModel( )).reload( );
	}

	/**
	 * Causes the tree to rechedk the children of the give node for changes.
	 * 
	 * @param node The node to check the children of.
	 */
	public void reload( TreeNode node ) {
		((DefaultTreeModel)this.tree.getModel( )).reload( node );
	}

	/**
	 * Finds the comlete path to a given TreeNode.
	 * 
	 * @param node The node to find the path to.
	 * @return The path to the given node.
	 */
	public TreePath getNodePath( TreeNode node ) {
		LinkedList pathList = new LinkedList( );
		pathList.push( node );
		while(( node = node.getParent( )) != null ) {
			pathList.push( node );
		}
		return new TreePath( pathList.toArray( ));
	}

	/**
	 * Sets the checked state of the passed in node.
	 * 
	 * @param node The node to set the state of.
	 * @param checked whether or not the node is to be checked.
	 */
	public boolean setChecked( TreeNode node, boolean checked  ) {
		if ( checked )
			return this.check( node );
		return this.uncheck( node );
	}

	/**
	 * Sets the specified node's state to checked.
	 * 
	 * @param node The node to set the state of.
	 * @return The initial state of the node.
	 */
	public boolean check( TreeNode node ) {
		TreePath path = this.getNodePath( node );
		boolean returnValue = this.isChecked( path );
		this.tree.addCheckingPath( path );
		return returnValue;
	}

	/**
	 * Sets the specified node's state to unchecked.
	 * 
	 * @param node The node to set the state of.
	 * @return The initial state of the node.
	 */
	public boolean uncheck( TreeNode node ) {
		TreePath path = this.getNodePath( node );
		boolean returnValue = this.isChecked( path );
		this.tree.removeCheckingPath( path );
		return returnValue;
	}
	
	/**
	 * Determines whether the tree node is checked.
	 * 
	 * @param node  The node to get the status of.
	 * @return true if the node in the tree is checked.
	 */
	public boolean isChecked( TreeNode node ) {
		return this.isChecked( this.getNodePath( node ));
	}

	/**
	 * Determines whether the given tree path is checked.
	 * 
	 * @param path The path to check.
	 * @return true if the node is checked.
	 */
	public boolean isChecked( TreePath path ) {
		return this.tree.isPathChecked( path );
	}

	/**
	 * Returns an iterator over the checked children of the given node.
	 * 
	 * @param node The node to retrieve the checked children of.
	 * @return an iterator over the checked child nodes.
	 */
	public Iterator<TreeNode> checkedChildIterator( TreeNode node ) {
		Collection<TreeNode> iterList = new ArrayList<TreeNode>( );
		for ( int i=0; i < node.getChildCount( ); i++ ) {
			TreeNode childNode = node.getChildAt( i );
			if ( this.isChecked( childNode )) {
				iterList.add( childNode );
			}
		}
		return iterList.iterator( );
	}

	/**
	 * Gets all checked descendant nodes of this TreeNode
	 * 
	 * @param node The node to get the descendants of (including itself)
	 * @param level The level of the nodes to retrieve or -1 for all.
	 * @return An iterator over the requested nodes.
	 */
	public Iterator<TreeNode> checkedDescendantIterator( TreeNode node, 
	                                                    int level ) {
		Collection<TreeNode> iterList = new ArrayList<TreeNode>( );
		int nodeLevel = this.getNodePath( node ).getPathCount( ) - 1;
		// if this node is on the right level, add it.
		if ( isChecked( node ) && ( level < 0 || level == nodeLevel )) {
			iterList.add( node );
		}
		// if node is a parent of the level we are seeking, iterate through the
		// children of node.
		if ( level < 0 || nodeLevel < level ) {
			for ( int i=0; i < node.getChildCount( ); i++ ) {
				TreeNode childNode = node.getChildAt( i );
				Iterator<TreeNode> descIter = 
					this.checkedDescendantIterator( childNode, level );
				while( descIter.hasNext( )){ 
					iterList.add( descIter.next( ));
				}
			}
		}
		return iterList.iterator( );
	}

	/**
	 * Gets all checked descendant nodes of this TreeNode
	 * 
	 * @param node The node to get the descendants of (including itself)
	 * @param level The level of the nodes to retrieve or -1 for all.
	 * @return An iterator over the requested nodes.
	 */
	public Iterator<TreeNode> descendantIterator( TreeNode node, int level ) {
		Collection<TreeNode> iterList = new ArrayList<TreeNode>( );
		int nodeLevel = this.getNodePath( node ).getPathCount( ) - 1;

		// if this node is on the right level, add it.
		if ( level < 0 || level == nodeLevel ) {
			iterList.add( node );
		}
		// if node is a parent of the level we are seeking, iterate through the
		// children of node.
		if ( level < 0 || nodeLevel < level ) {
			for ( int i=0; i < node.getChildCount( ); i++ ) {
				TreeNode childNode = node.getChildAt( i );
				Iterator<TreeNode> descIter = 
					this.descendantIterator( childNode, level );
				while( descIter.hasNext( )){ 
					iterList.add( descIter.next( ));
				}
			}
		}
		return iterList.iterator( );
	}

	/**
	 * Adds a TreeSelectionListener to this panel. All listeners will be notified
	 * of a change to the Selection of the internal tree.
	 * 
	 * @param l The TreeSelectionListener to be added.
	 */
	public void addTreeSelectionListener( TreeSelectionListener l ) {
		this.treeSelectionListeners.add( l );
	}

	/**
	 * Removes the specified TreeSelectionListener. 
	 * 
	 * @param l The listener to be removed.
	 * @return A boolean indicating whether the operation was successful.
	 */
	public boolean removeTreeSelectionListener( TreeSelectionListener l ) {
		return this.treeSelectionListeners.remove( l );
	}

	/**
	 * Adds a TreeCheckingListener to this panel.
	 * 
	 * @param l The listener to be added to this panel.
	 */
	public void addTreeCheckingListener( TreeCheckingListener l ) {
		this.treeCheckingListeners.add( l );
	}

	/**
	 * Removes a TreeCheckingListener from this panel. 
	 * 
	 * @param l The listener to be removed.
	 * @return a boolean indicating whether the operation was successful.
	 */
	public boolean removeTreeCheckingListener( TreeCheckingListener l ) {
		return this.treeCheckingListeners.remove( l );
	}
}

