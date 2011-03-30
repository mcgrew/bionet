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
import edu.purdue.cc.jsysnet.util.Sample;

import java.util.ArrayList;
import java.util.Collection;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;

/**
 * A Panel containing a CheckBoxTree containing the available Samples in this
 * so they can be selected/deselected.
 */
public class SampleSelectorTreePanel extends CheckboxTreePanel {
	private DefaultMutableTreeNode rootNode;

	/**
	 * Creates a new SampleSelectorTreePanel.
	 * 
	 * @param samples A Collection of the Samples to be displayed in this panel.
	 */
	public SampleSelectorTreePanel ( Collection<Sample> samples ) {
		super( new BorderLayout( ));
		Language language = Settings.getLanguage( );
		this.rootNode = new DefaultMutableTreeNode( language.get( "Samples" ));
		for ( Sample sample : samples ) {
			DefaultMutableTreeNode sampleNode = new DefaultMutableTreeNode( sample );
			this.rootNode.add( sampleNode );
		}
		this.tree = new CheckboxTree( this.rootNode );
		//this.tree.setRootVisible( false );
		this.check( this.rootNode );
		this.tree.setSelectsByChecking( false );
		this.tree.getCheckingModel( ).setCheckingMode( 
			TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );
		this.add( new JScrollPane( tree ), BorderLayout.CENTER );
	}

	/**
	 * Checks the status of the Checkboxes in the tree and returns the Samples
	 * which have been selected for display.
	 * 
	 * @return A Collection containing the selected Samples.
	 */
	public Collection<Sample> getSamples( ) {
		DefaultMutableTreeNode sampleNode = 
			(DefaultMutableTreeNode)this.rootNode.getFirstChild( );
		Collection<Sample> returnValue = new ArrayList<Sample>( );
		while( sampleNode != null ) {
			if ( this.isChecked( sampleNode )) {
				returnValue.add( (Sample)sampleNode.getUserObject( ));
			}
			sampleNode = (DefaultMutableTreeNode)sampleNode.getNextSibling( );
		}
		return returnValue;
	}
}

