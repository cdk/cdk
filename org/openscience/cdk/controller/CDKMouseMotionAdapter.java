/* CDKMouseMotionAdapter.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.controller;


import java.awt.event.*;
import java.awt.*;
import org.openscience.cdk.*;
import org.openscience.cdk.renderer.*;


public class CDKMouseMotionAdapter extends MouseMotionAdapter
{
	Controller2D controller;
	
	
	public CDKMouseMotionAdapter(Container panel, Molecule molecule, Renderer2D renderer)
	{
		super();
		controller = new Controller2D(panel, molecule, renderer);
	}
	
    public void mouseMoved(MouseEvent e)
    {
		controller.mouseMoved(e.getX(), e.getY());
    }
	
}
