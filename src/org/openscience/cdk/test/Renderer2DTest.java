/* Renderer2DTest.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

package org.openscience.cdk.test;


import org.openscience.cdk.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.geometry.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;
import java.awt.*;


public class Renderer2DTest extends JPanel
{
	MDLReader mr;
        CMLReader cr;
	ChemFile chemFile;
	ChemSequence chemSequence;
	ChemModel chemModel;
	SetOfMolecules setOfMolecules;
	Molecule molecule;

	Renderer2DModel r2dm;
	Renderer2D renderer;

	public Renderer2DTest(String inFile)
	{
		Hashtable ht = null;
		r2dm = new Renderer2DModel();
		renderer = new Renderer2D(r2dm);
		setPreferredSize(new Dimension(600, 400));
		setBackground(r2dm.getBackColor());
		
	
		try
		{
			FileInputStream fis = new FileInputStream(inFile);
			if (inFile.endsWith(".cml")) {
			    cr = new CMLReader(new FileReader(inFile));
			    chemFile = (ChemFile)cr.read((ChemObject)new ChemFile());
			} else {
			    mr = new MDLReader(fis);
			    chemFile = (ChemFile)mr.read((ChemObject)new ChemFile());
			}
			fis.close();
			chemSequence = chemFile.getChemSequence(0);
			chemModel = chemSequence.getChemModel(0);
			setOfMolecules = chemModel.getSetOfMolecules();
			molecule = setOfMolecules.getMolecule(0);
			ht = r2dm.getColorHash();
			ht.put(molecule.getAtomAt(2), Color.red);
			ht.put(molecule.getAtomAt(4), Color.red);
			GeometryTools.translateAllPositive(molecule);
			GeometryTools.scaleMolecule(molecule, getPreferredSize(), 0.8);			
			GeometryTools.center(molecule, getPreferredSize());
		}
		catch(Exception exc)
		{
			exc.printStackTrace();		
		}
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		renderer.paintMolecule(molecule, g);
	}

	/**
	 * The main method.
	 *
	 * @param   args    The Arguments from the commandline
	 */	public static void main(String[] args)
	{
		new Renderer2DTest("data/reserpine.mol");
	}
}

