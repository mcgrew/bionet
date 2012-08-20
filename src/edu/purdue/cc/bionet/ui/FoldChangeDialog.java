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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

/**
 * Creates a dialog for choosing from the available Samples.
 */
public class FoldChangeDialog extends JDialog
		implements ActionListener {

	protected JButton okButton;
	protected JButton cancelButton;
  protected JCheckBox logCheckBox;
  protected JTextField valueTextField;
  protected JLabel foldChangeLabel;
	protected double foldChange;
  protected boolean logData;
	protected String okButtonText;
	protected String cancelButtonText;
  protected String checkBoxText;
  protected String foldChangeText;

	/**
	 * Creates a new ExperimentSelectionDialog.
	 * 
	 * @param owner The parent frame of this Dialog. The parent frame will not
	 *	accept user input until the dialog is closed.
	 * @param title The title to be displayed in the title bar of this dialog.
	 * @param experiment The available experiment to be selected from.
	 */
	public FoldChangeDialog( Frame owner, String title, double foldChange, 
                           boolean logData ) {
		super( owner, title );
    this.foldChange = foldChange;
    this.logData = logData;
		this.getContentPane( ).setLayout( new BorderLayout( ));
		this.setBounds( 
      Settings.getSettings( ).getInt( "window.main.position.x", 0 ),
      Settings.getSettings( ).getInt( "window.main.position.y", 0 ),
      0, 0 );

		Language language = Settings.getLanguage( );
		this.okButtonText = language.get( "Select" );
		this.cancelButtonText = language.get( "Cancel" );
    this.foldChangeText = language.get( "Enter fold change value" );
    this.checkBoxText = language.get( "My response values are logarithmic" );

		this.okButton = new JButton( okButtonText );
		this.cancelButton = new JButton( cancelButtonText );
    this.logCheckBox = new JCheckBox(  this.checkBoxText );
		logCheckBox.setSelected( this.logData );
    this.foldChangeLabel = new JLabel( this.foldChangeText );
    this.valueTextField = new JTextField( );
		this.okButton.addActionListener( this );
		this.cancelButton.addActionListener( this );

    JPanel buttonPanel = new JPanel( new GridLayout( 1, 2, 10, 10 ));
    JPanel mainPanel = new JPanel( new BorderLayout( ));
    JPanel textFieldPanel = new JPanel( new BorderLayout( ));
    valueTextField.setText( Double.toString( this.foldChange ));
    textFieldPanel.setBorder( BorderFactory.createEmptyBorder( 20, 20, 20, 20 ));
    textFieldPanel.add( this.foldChangeLabel, BorderLayout.NORTH );
    textFieldPanel.add( this.valueTextField, BorderLayout.CENTER );

		buttonPanel.add( this.okButton );
		buttonPanel.add( this.cancelButton );
    mainPanel.add( textFieldPanel, BorderLayout.CENTER );
    mainPanel.add( this.logCheckBox, BorderLayout.SOUTH );
		this.add( mainPanel, BorderLayout.CENTER );
		this.add( buttonPanel, BorderLayout.SOUTH );

		this.setVisible( true );

		this.setVisible( false );
		this.setModalityType( Dialog.ModalityType.APPLICATION_MODAL );
		this.pack( );
		this.setVisible( true );
	}

  public FoldChangeDialog getReturnValue( ) {
    return this;
  }

  /**
   * Gets the value entered by the user.
   * 
   * @return A double indicating the value entered by the user.
   */
  public double getFoldChange( ) {
    return this.foldChange;
  }

  /**
   * Returns true if the user indicated they are using logarithmic data.
   * 
   * @return true if the user indicated they are using logarithmic data.
   */
  public boolean isLogData( ) {
    return this.logData;
  }
  

	/**
	 * Shows the input dialog and returns the selection.
	 * 
	 * @param owner The parent frame of this Dialog. The parent frame will not
	 *	accept user input until the dialog is closed.
	 * @param title The title to be displayed in the title bar of this dialog.
	 * @return A FoldChangeDialog Object.
	 */
	public static FoldChangeDialog showInputDialog( Frame owner, String title,
                                        double foldChange, boolean logData) {

		FoldChangeDialog dialog = 
			new FoldChangeDialog( owner, title, foldChange, logData );
		return dialog.getReturnValue( );
	}

	public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource( );
		if ( source == this.cancelButton ) {
			this.setVisible( false );
		} else if ( source == this.okButton ) {
      try {
        this.foldChange = Double.parseDouble( this.valueTextField.getText( ));
      } catch ( NumberFormatException e ) { 
        Logger.getLogger( getClass( )).error( 
          "You entered an invalid number. The fold change parameter should " +
          "be a number greater than or equal to 1.0" );
        return;
      }
      if ( foldChange < 1.0 ) {
        Logger.getLogger( getClass( )).error( 
          "You entered an invalid number. The fold change parameter should " +
          "be a number greater than or equal to 1.0" );
        return;
      } 
			this.logData = this.logCheckBox.isSelected( );
			this.setVisible( false );
		}

	}
}


