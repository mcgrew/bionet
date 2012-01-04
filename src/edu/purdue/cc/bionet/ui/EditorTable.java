/*

This file is part of BioNet.

BioNet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BioNet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with BioNet.  If not, see <http://www.gnu.org/licenses/>.

*/

package edu.purdue.cc.bionet.ui;

import edu.purdue.bbc.util.Language;
import edu.purdue.bbc.util.Settings;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class EditorTable extends JTable 
                         implements ActionListener,TableModelListener {
	protected EditMenu menu;
	protected JMenuItem deleteColumnMenuItem;
	protected JMenuItem addColumnMenuItem;

	public EditorTable( ) {
		super( );
		Language language = Settings.getLanguage( );
		this.menu = new EditMenu( this.getTableHeader( ));
		this.deleteColumnMenuItem = new JMenuItem( language.get( "Delete Column" ));
		this.addColumnMenuItem = new JMenuItem( language.get( "Add Column" ));
		this.deleteColumnMenuItem.addActionListener( this );
		this.addColumnMenuItem.addActionListener( this );
		this.menu.add( this.deleteColumnMenuItem );
		this.menu.add( this.addColumnMenuItem );
	}

	public void addColumn( ) {
		((DefaultTableModel)this.getModel( )).addColumn( 
			JOptionPane.showInputDialog( this, 
				Settings.getLanguage( ).get( "Enter new column name" )));
	}

	public void actionPerformed( ActionEvent e ) {
		Object source = e.getSource( );
		if ( source == this.deleteColumnMenuItem ) {
			this.removeColumn( this.menu.getColumn( ));
		} else if ( source == this.addColumnMenuItem ) {
			this.addColumn( );
		}
	}

	@Override
	protected TableModel createDefaultDataModel( ) {
		return new CustomTableModel( );
	}

	@Override
	protected TableColumnModel createDefaultColumnModel( ) {
		return new CustomColumnModel( );
	}

	@Override
	public void removeColumn( TableColumn tableColumn ) {
		int index = 0;
		Enumeration<TableColumn> columnEnum = this.columnModel.getColumns( );
		// find the column
		while( columnEnum.hasMoreElements( )) {
			TableColumn tc = columnEnum.nextElement( );
			if ( tc == tableColumn )
				break;
			index++;
		}
		if ( index < this.getColumnCount( )) {
			this.columnModel.removeColumn( tableColumn );
			((CustomTableModel)this.dataModel).removeColumn( index );
		}
	}

	public void removeColumn( int column ) {
		((CustomTableModel)this.dataModel).removeColumn( column );
		TableColumn tableColumn = this.columnModel.getColumn( column );
		this.columnModel.removeColumn( tableColumn );
	}

//	public void tableChanged( TableModelEvent e ) {
//		super.tableChanged( e );
//		this.editCellAt( e.getColumn( ), e.getLastRow( ) + 1 );
//	}
	
	private int getColumnHeaderAt( int x, int y ) {
		for( int i = 0; i < this.getColumnCount( ); i++ ) {
			if ( this.getTableHeader( ).getHeaderRect( i ).contains( x, y ))
				return i;
		}
		return -1;
	}

	// ============================= PRIVATE CLASSES =============================

	private class EditMenu extends ContextMenu {
		int column;

		public EditMenu( Component target ) {
			super( target );
		}

		public void mouseClicked( MouseEvent e ) {
			this.column = getColumnHeaderAt( e.getX( ), e.getY( ));
			deleteColumnMenuItem.setEnabled( this.column >= 1 );
			if ( this.column >= 0 )
				super.mouseClicked( e );
		}

		public int getColumn( ) {
			return this.column;
		}
	}

	private class CustomTableModel extends DefaultTableModel {

		public CustomTableModel( ) {
			super( );
		}
		
		public void removeColumn( int column ) {
			this.columnIdentifiers.remove( column );
			for( Object row : this.dataVector ) {
				((Vector)row).remove( column );
			}
		}
	}

	private class CustomColumnModel extends DefaultTableColumnModel {

		public CustomColumnModel( ) {
			super( );
		}
		
		@Override
		public void removeColumn( TableColumn column ) {
			int index = this.tableColumns.indexOf( column );
			if ( index >= 0 ) {
				// decrement any columns which come after the one being removed.
				for ( TableColumn tc : this.tableColumns ) {
					if ( tc.getModelIndex( ) > column.getModelIndex( )) {
						tc.setModelIndex( tc.getModelIndex( ) - 1 );
					}
				}
			}
			if ( this.selectionModel != null ) {
					selectionModel.removeIndexInterval( index, index );
			}

			column.removePropertyChangeListener( this );
			tableColumns.removeElementAt( index );
			totalColumnWidth = -1; // invalidates the width cache

			// Post columnAdded event notification.  (JTable and JTableHeader
			// listens so they can adjust size and redraw)
			this.fireColumnRemoved( 
				new TableColumnModelEvent( this, index, 0));

		}
	}
}

