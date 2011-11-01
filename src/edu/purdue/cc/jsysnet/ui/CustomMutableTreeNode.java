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

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A customized version of DefaultMutableTreeNode for a JTree.
 */
public class CustomMutableTreeNode extends DefaultMutableTreeNode {
	private String stringValue;	

	/**
	 * Creates a new empty CustomMutableTreeNode.
	 */
	public CustomMutableTreeNode( ) {
		super( );
	}

	/**
	 * Creates a new CustomMutableTreeNode containing the specified userObject.
	 * The stringValue for this TreeNode is determined by calling the userObject's
	 * toString( ) method.
	 * 
	 * @param userObject The object to be contained in this TreeNode.
	 */
	public CustomMutableTreeNode( Object userObject ) {
		super( userObject );
		this.stringValue = userObject.toString( );
	}

	/**
	 * Creates a new CustomMutableTreeNode containing the specified userObject
	 * and having the specified stringValue.
	 * 
	 * @param userObject The object to be contained in this TreeNode.
	 * @param stringValue value to be returned by the toString( ) method.
	 */
	public CustomMutableTreeNode( Object userObject, String stringValue  ) {
		super( userObject );
		this.stringValue = stringValue;
	}

	/**
	 * Returns the string value of this TreeNode.
	 * 
	 * @return A String representation of this TreeNode.
	 */
	public String toString( ) {
		return stringValue;
	}
}
