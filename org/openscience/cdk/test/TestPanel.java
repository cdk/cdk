/* TestPanel.java
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
package org.openscience.cdk.test;


import java.awt.*;
import javax.swing.*;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.renderer.Renderer2D;


public class TestPanel extends JPanel
{
	Image img;
	Molecule molecule;
	Renderer2D renderer;
	Color foreColor = Color.black, backColor = Color.gray;
	
	public TestPanel(Molecule molecule)
	{
		this.molecule = molecule;
		renderer = new Renderer2D();
	
	}
	
	public void paint(Graphics g)
	{
		g.setColor(backColor);
		g.fillRect(0,0,getSize().width,getSize().height); 
		g.setColor(foreColor);
		renderer.paintMolecule(molecule, g);
//		if (img == null)
//		{
//			img = createImage(900,800);
//		}
//		paintBuffer(img.getGraphics());
//		g.drawImage(img,0,0,this);
	}
	
	public void paintBuffer(Graphics g)
	{
		System.out.println("repaint???????");
//		renderer = new Renderer2D(g);
		
	}

}