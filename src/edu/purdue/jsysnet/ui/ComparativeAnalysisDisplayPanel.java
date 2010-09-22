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

package edu.purdue.jsysnet.ui;

import edu.purdue.jsysnet.util.Experiment;
import edu.purdue.jsysnet.util.MoleculeGroup;
import edu.purdue.jsysnet.util.Molecule;
import edu.purdue.jsysnet.util.Settings;
import edu.purdue.jsysnet.util.PolarPoint2D;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.geom.Point2D;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;

import edu.uci.ics.jung.algorithms.layout.*; //testing
import edu.purdue.jsysnet.ui.layout.*; //testing

public class ComparativeAnalysisDisplayPanel extends JPanel {
	private List <Experiment> experiments;
	private JSplitPane splitPane;
	private SelectorTreePanel selectorTree;
	private GraphVisualizer<Molecule,Object> graph;


	public ComparativeAnalysisDisplayPanel ( ) {
		super( new BorderLayout( ));
	}

	public boolean createGraph( List <Experiment> experiments ) {
		this.experiments = experiments;
		graph = new ComparativeAnalysisGraphVisualizer<Molecule,Object>( ComparativeAnalysisLayout.class ); 
		for ( Experiment e : experiments ) {
			for ( Molecule m : e.getMolecules( )) {
				graph.addVertex( m );
			}
		}
		graph.resetLayout( );
		selectorTree = new SelectorTreePanel( experiments );
		splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		this.add( splitPane, BorderLayout.CENTER );
		this.splitPane.setLeftComponent( selectorTree );
		this.splitPane.setRightComponent( graph.getScrollPane( )); 
		this.splitPane.setDividerLocation( 200 );
		return true;
	}

	public boolean addExperiment( Experiment experiment ) {
		return experiments.add( experiment );
	}

	public boolean removeExperiment( Experiment experiment ) {
		return experiments.remove( experiment );
	}

	public String getTitle( ) {
		return Settings.getLanguage( ).get( "Comparative Analysis" );
	}

	private class SelectorTreePanel extends JPanel implements TreeCheckingListener {
		private CheckboxTree tree;

		public SelectorTreePanel( List <Experiment> experiments ) {
			super( new BorderLayout( ));
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(	
				Settings.getLanguage( ).get( "All Experiments" ));
			for ( Experiment e : experiments ) {
				DefaultMutableTreeNode experimentNode = new DefaultMutableTreeNode( e );
				for ( MoleculeGroup mg : e.getMoleculeGroups( )) {
					DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode( mg );
					for ( Molecule m : mg.getMolecules( )) {
						DefaultMutableTreeNode moleculeNode = new DefaultMutableTreeNode( m );
						groupNode.add( moleculeNode );
					}
					experimentNode.add( groupNode );
				}
				rootNode.add( experimentNode );
			}
			this.tree = new CheckboxTree( rootNode );
			this.tree.setCheckingPath( new TreePath( rootNode ));
			this.tree.addTreeCheckingListener( this );
			this.add( new JScrollPane( tree ), BorderLayout.CENTER);
		}

		public void addExperiment( Experiment experiment ) {
		}

		public void removeExperiment( Experiment experiment ) {
		}

		public void valueChanged ( TreeCheckingEvent e ) {
			Object obj = ((DefaultMutableTreeNode)e.getPath( ).getLastPathComponent( ))
					.getUserObject( );
			boolean checked = e.isCheckedPath( );
			List <Molecule> molecules = new ArrayList <Molecule>( );

			if ( Settings.getLanguage( ).get( "All Experiments" ).equals( obj )) {
				for( Experiment exp : experiments )
					molecules.addAll( exp.getMolecules( ));
			} else if ( Experiment.class.isInstance( obj )) {
				molecules.addAll( ((Experiment)obj).getMolecules( ));
			} else if ( MoleculeGroup.class.isInstance( obj )) {
				molecules.addAll( ((MoleculeGroup)obj).getMolecules( ));
			} else if ( Molecule.class.isInstance( obj )) {
				molecules.add( (Molecule)obj );
			} else {
				System.out.println( Settings.getLanguage( ).get( "Unrecognized object in tree" )
				 + ": " + obj.getClass( ).toString( ));
			}

			for ( Molecule m : molecules )
				if ( checked )
					graph.addVertex( m );
				else
					graph.removeVertex( m );
		}
	}

	private class ComparativeAnalysisGraphVisualizer<V,E> extends GraphVisualizer<V,E> {
		
		public ComparativeAnalysisGraphVisualizer ( Class <? extends AbstractLayout> layout ) {
			super( layout );
			this.setEdgePaint( Color.BLACK );
		}
	}
}


