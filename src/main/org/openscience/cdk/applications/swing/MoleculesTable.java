/* $RCSfile$
 * $Author$   
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.applications.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.EventObject;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.event.ICDKChangeListener;

/**
 * @cdk.module  extra
 * @cdk.svnrev  $Revision$
 * @cdk.require swing
 */
public class MoleculesTable extends JPanel implements ICDKChangeListener {

    private static final long serialVersionUID = 1833180730497812300L;
    
    private JTable table;
	private String title = "Molecule Viewer";
  
	public MoleculesTable(org.openscience.cdk.interfaces.IMoleculeSet set) {
    MoleculeContainerModel mcm = new MoleculeContainerModel(set);
    table = new JTable(mcm);
    table.setPreferredScrollableViewportSize(new Dimension(500,300));
    JScrollPane scrollPane = new JScrollPane(table);
    this.add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Contructs a JFrame into which this JPanel is
	 * put and displays the frame with the molecule
	 */
	public void display()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.setTitle(title);
		frame.pack();
		frame.setVisible(true);		
	}

  public void stateChanged(EventObject e) {
    // not implemented yet
  }
	
  class MoleculeContainerModel extends AbstractTableModel {

    private static final long serialVersionUID = -7822373381094927714L;

    private org.openscience.cdk.interfaces.IMoleculeSet set;
    
    final String[] columnNames = {"title", "casRN", "beilsteinRN", 
                                  "autonomName"};
    
    public MoleculeContainerModel(org.openscience.cdk.interfaces.IMoleculeSet set) {
      this.set = set;
    }
    
    public int getColumnCount() {
      return columnNames.length;
    }
    
    public int getRowCount() {
      return set.getMoleculeCount();
    }
    
    public String getColumnName(int col) {
      return columnNames[col];
    }
    
    public Class getColumnClass(int col) {
      Object o = getValueAt(0,col);
      if (o == null) {
        return (new String()).getClass();
      } else {
        return o.getClass();
      }
    }
    
    public Object getValueAt(int row, int col) {
      if (getColumnName(col).equals("title")) {
        return (set.getMolecule(row)).getProperty("title");
      } else if (getColumnName(col).equals("casRN")) {
        return (set.getMolecule(row)).getProperty(CDKConstants.CASRN);
      } else if (getColumnName(col).equals("beilsteinRN")) {
        return (set.getMolecule(row)).getProperty(CDKConstants.BEILSTEINRN);
      } else if (getColumnName(col).equals("autonomName")) {
        return (set.getMolecule(row)).getProperty(CDKConstants.AUTONOMNAME);
      } else {
        return null;
      }
    }
    
    public boolean isCellEditable(int row, int col) {
      return false;
    }
  }
}


