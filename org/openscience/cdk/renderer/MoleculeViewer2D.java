/* MoleculeViewer2D.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
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
import java.awt.event.*;


public class MoleculeViewer2D extends JPanel implements CDKChangeListener
{
	static Molecule molecule;
	
	public Renderer2DModel r2dm;
	public Renderer2D renderer;

	public MoleculeViewer2D(Molecule molecule,Renderer2DModel r2dm)
	{
		this.molecule = molecule;
		this.r2dm = r2dm;
		r2dm.addCDKChangeListener(this);
		renderer = new Renderer2D(r2dm);
	}
	
	public MoleculeViewer2D(Molecule molecule)
	{
		this.molecule = molecule;
		r2dm = new Renderer2DModel();
		r2dm.addCDKChangeListener(this);
		renderer = new Renderer2D(r2dm);
	}
	
	public void display()
	{
		setPreferredSize(new Dimension(600, 400));
		setBackground(r2dm.getBackColor());
		GeometryTools.translateAllPositive(molecule);
		GeometryTools.scaleMolecule(molecule, getPreferredSize(), 0.8);			
		GeometryTools.center(molecule, getPreferredSize());
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		if (molecule != null)
		{
			renderer.paintMolecule(molecule, g);
		}
	}

	/**
	 * The main method.
	 *
	 * @param   args    The Arguments from the commandline
	 */	
	 public static void main(String[] args)
	 {
		try
		{
			FileInputStream fis = new FileInputStream(args[0]);
			MDLReader mr = new MDLReader(fis);
			molecule = mr.readChemFile().getChemSequence(0).getChemModel(0).getSetOfMolecules(0).getMolecule(0);
			fis.close();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();		
		}

		new MoleculeViewer2D(molecule, new Renderer2DModel());
	}

	public void stateChanged(EventObject e)
	{
		repaint();
	}

}


