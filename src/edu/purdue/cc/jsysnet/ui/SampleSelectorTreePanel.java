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

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;
import edu.purdue.cc.jsysnet.util.Experiment;
import edu.purdue.cc.jsysnet.util.Sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;

/**
 * A Panel containing a CheckBoxTree containing the available Samples in this
 * so they can be selected/deselected.
 */
public class SampleSelectorTreePanel extends CheckboxTreePanel {
	private static final int EXPERIMENT = 1;
	private static final int SAMPLE = 2;

	/**
	 * Creates a new SampleSelectorTreePanel.
	 * 
	 * @param experiments A Collection of the Samples to be displayed in this panel.
	 */
	public SampleSelectorTreePanel ( Collection<Experiment> experiments ) {
		super( new DefaultMutableTreeNode( 
			Settings.getLanguage( ).get( "Samples" )));
		DefaultMutableTreeNode rootNode = this.getRoot( );
		for ( Experiment experiment : experiments ) {
			DefaultMutableTreeNode experimentNode =
				new DefaultMutableTreeNode( experiment );
			for ( Sample sample : experiment.getSamples( )) {
				DefaultMutableTreeNode sampleNode = 
					new DefaultMutableTreeNode( sample );
				experimentNode.add( sampleNode );
			}
			rootNode.add( experimentNode );
		}
		this.reload( );
		this.check( rootNode );
	}

	/**
	 * Checks the status of the Checkboxes in the tree and returns the Samples
	 * which have been selected for display.
	 * 
	 * @return A Collection containing the selected Samples.
	 */
	public Collection<Sample> getSamples( ) {
		Collection<Sample> returnValue = new ArrayList<Sample>( );
		Iterator<TreeNode> sampleNodeIter = 
			this.checkedDescendantIterator( this.getRoot( ), SAMPLE );
		while( sampleNodeIter.hasNext( )) {
			returnValue.add( (Sample)
				((DefaultMutableTreeNode)sampleNodeIter.next( )).getUserObject( ));
		}
		return returnValue;
	}
}

