/* $RCSfile$   
 * $Author$   
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk.renderer;

import org.openscience.cdk.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class AtomicTable extends JPanel implements CDKChangeListener {
	public AtomContainer atomContainer;
  public JTable table;
	public String title = "Molecule Viewer";

	public AtomicTable(AtomContainer atomContainer) {
		this.atomContainer = atomContainer;
    
    table = new JTable();
    //this table should show atom types, coords etc...
    this.add(table);
	}
	
	/**
	 * Contructs a JFrame into which this JPanel is
	 * put and displays the frame with the molecule
	 */
	public void display()
	{
		setPreferredSize(new Dimension(600, 400));
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
	
}


